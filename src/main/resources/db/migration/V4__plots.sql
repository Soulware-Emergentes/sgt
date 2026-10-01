-- Plots, each stored as the polygon its survey produced.
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
    boundary geometry(Polygon) NOT NULL CHECK (ST_IsValid(boundary)),
    footprint geography(Polygon, 4326) GENERATED ALWAYS AS (ST_Transform(boundary, 4326)::geography) STORED
);

CREATE INDEX plots_footprint_idx ON plots USING GIST (footprint);
