-- Plots, each registered to one beneficiary and stored as the polygon its
-- survey produced.
--
-- The owner is named by legal document, the way field sheets name their
-- beneficiary. A beneficiary holds a single plot, so the pair is unique.
--
-- ubigeo is the district the plot lies in, wherever its owner is registered:
-- department, province and district, two digits each. Plots are read by any
-- of those levels as a prefix, which text_pattern_ops lets the index serve.
--
-- boundary keeps the survey exactly as measured, in the SRID of the UTM zone
-- it was projected in, so the column takes any SRID. Comparing plots from
-- different zones needs one common space, which is what footprint is: the
-- same polygon on the WGS84 spheroid, where intersection areas come out in
-- square metres wherever the plots are. Postgres derives it, so it can never
-- disagree with boundary.
--
-- The CHECK keeps self-intersecting rings out. PostGIS refuses to intersect
-- an invalid polygon, so one stored plot of that kind would break the
-- overlap check for every plot registered near it afterwards.
CREATE TABLE plots (
    id UUID PRIMARY KEY,
    owner_document_type VARCHAR(20) NOT NULL,
    owner_document_number VARCHAR(12) NOT NULL,
    boundary geometry(Polygon) NOT NULL CHECK (ST_IsValid(boundary)),
    footprint geography(Polygon, 4326) GENERATED ALWAYS AS (ST_Transform(boundary, 4326)::geography) STORED,
    ubigeo VARCHAR(6) NOT NULL CHECK (ubigeo ~ '^[0-9]{6}$'),
    registration_date TIMESTAMP NOT NULL,
    CONSTRAINT plots_owner_key UNIQUE (owner_document_type, owner_document_number)
);

CREATE INDEX plots_footprint_idx ON plots USING GIST (footprint);
CREATE INDEX plots_ubigeo_idx ON plots (ubigeo text_pattern_ops);
