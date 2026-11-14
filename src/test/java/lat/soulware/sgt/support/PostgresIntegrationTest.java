package lat.soulware.sgt.support;

import java.io.IOException;
import java.nio.file.Path;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Container;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

/**
 * Base class for tests that need a real Postgres with this project's two roles.
 *
 * <p>One container is started for the whole test suite and torn down by Ryuk when the JVM exits.
 * It runs {@code scripts/db/setup-roles.sql} verbatim, so the script that provisions a real
 * deployment is the same one every test runs against - if it breaks, the build breaks.
 *
 * <p>The datasource properties in {@code application.properties} have no defaults on purpose:
 * a missing {@code DB_URL} in production must fail loudly. Tests therefore supply real values
 * for a real database rather than weakening that config.
 */
public abstract class PostgresIntegrationTest {

    // Thse have to match the setup-roles.sql script
    protected static final String DATABASE_NAME = "sgt";
    protected static final String MIGRATOR_USERNAME = "sgt_migrator";
    protected static final String APP_USERNAME = "sgt_app";

    // WARNING: DO NOT let these match your actual .env passwords
    private static final String MIGRATOR_PASSWORD = "MIGRATOR_RANDOM_PASSWORD";
    private static final String APP_PASSWORD = "APP_RANDOM_PASSWORD";

    private static final Path SETUP_ROLES_SCRIPT = Path.of("scripts", "db", "setup-roles.sql");
    private static final String SCRIPT_PATH_IN_CONTAINER = "/tmp/setup-roles.sql";

    private static final PostgreSQLContainer POSTGRES = startProvisionedContainer();

    /**
     * Starts the container and provisions its roles before any subclass runs. Assigned to a static
     * field so one container is shared across every integration test rather than started per class.
     */
    private static PostgreSQLContainer startProvisionedContainer() {
        MountableFile setupScript = MountableFile.forHostPath(SETUP_ROLES_SCRIPT.toAbsolutePath());

        DockerImageName postgis = DockerImageName.parse("postgis/postgis:17-3.5-alpine")
            .asCompatibleSubstituteFor("postgres");
        PostgreSQLContainer container = new PostgreSQLContainer(postgis);
        container.withCopyFileToContainer(setupScript, SCRIPT_PATH_IN_CONTAINER);

        container.start();
        createRoles(container);

        return container;
    }

    /**
     * Runs setup-roles.sql through psql inside the container. It has to be psql rather than JDBC
     * because the script uses meta-commands ({@code \set}, {@code \connect}) that only psql
     * understands, and {@code CREATE DATABASE} cannot run inside a transaction block.
     */
    private static void createRoles(PostgreSQLContainer container) {
        String psqlCommand = """
            SGT_MIGRATOR_PASSWORD='%s' SGT_APP_PASSWORD='%s' \
            psql -v ON_ERROR_STOP=1 -U %s -d %s -f %s"""
            .formatted(
                MIGRATOR_PASSWORD,
                APP_PASSWORD,
                container.getUsername(),
                container.getDatabaseName(),
                SCRIPT_PATH_IN_CONTAINER
            );

        try {
            Container.ExecResult result = container.execInContainer("sh", "-c", psqlCommand);
            boolean scriptFailed = result.getExitCode() != 0;

            if (scriptFailed) {
                throw new IllegalStateException(
                    "setup-roles.sql failed with exit code %d:%n%s"
                        .formatted(result.getExitCode(), result.getStderr())
                );
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not run setup-roles.sql in the container", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while running setup-roles.sql", e);
        }
    }

    /** JDBC URL of the provisioned {@code sgt} database, whichever role connects to it. */
    protected static String jdbcUrl() {
        Integer mappedPort = POSTGRES.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT);

        return "jdbc:postgresql://%s:%d/%s".formatted(POSTGRES.getHost(), mappedPort, DATABASE_NAME);
    }

    @DynamicPropertySource
    /**
     * Points Spring at the container after it has been assigned its port. The two roles are handed
     * out separately: Flyway migrates as the migrator, the application connects as the app role,
     * which is what makes the privilege separation testable at all.
     */
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.flyway.url", PostgresIntegrationTest::jdbcUrl);
        registry.add("spring.flyway.user", () -> MIGRATOR_USERNAME);
        registry.add("spring.flyway.password", () -> MIGRATOR_PASSWORD);

        registry.add("spring.datasource.url", PostgresIntegrationTest::jdbcUrl);
        registry.add("spring.datasource.username", () -> APP_USERNAME);
        registry.add("spring.datasource.password", () -> APP_PASSWORD);
        registry.add("API_DOCS_ENABLED", () -> "true");
    }

    @DynamicPropertySource
    /**
     * Gives each external service the settings its client needs to be built. No test reaches these
     * services, so the addresses point nowhere: they exist because the properties have no defaults,
     * for the same reason the datasource's have none.
     */
    static void externalServiceProperties(DynamicPropertyRegistry registry) {
        registry.add("people.beneficiaries.base-url", () -> "http://beneficiaries.invalid");
        registry.add("people.directory.base-url", () -> "http://directory.invalid");
        registry.add("monitoring.poi.base-url", () -> "http://poi.invalid");
        registry.add("STAFF_ISSUER", TestTokens::issuer);
        registry.add("SGT_SERVICE_CLIENT_ID", () -> "sgt-service-dev");
        registry.add("SGT_SERVICE_CLIENT_SECRET", () -> "unused-in-tests");
        registry.add("POI_TOKEN_AUDIENCE", () -> "poi-api-dev");
        registry.add("DIRECTORY_TOKEN_AUDIENCE", () -> "staff-directory-api-dev");
        registry.add("BENEFICIARIES_TOKEN_AUDIENCE", () -> "beneficiarios-api-dev");
        registry.add("monitoring.scans.endpoint", () -> "http://scans.invalid");
        registry.add("monitoring.scans.region", () -> "us-east-1");
        registry.add("monitoring.scans.bucket", () -> "field-sheets");
        registry.add("monitoring.scans.access-key", () -> "unused-in-tests");
        registry.add("monitoring.scans.secret-key", () -> "unused-in-tests");
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", TestTokens::issuer);
        registry.add("spring.security.oauth2.resourceserver.jwt.audiences", () -> TestTokens.AUDIENCE);
        registry.add("sgt.web.origin", () -> "http://localhost:5173");
    }
}
