-- Field sheets.
--
-- A field sheet names the poi task it is submitted for by the code poi gives
-- it, and its status records poi's answer to the delivery. It is submitted as
-- a scanned PDF held in the object store under scan_key. What the PDF says
-- (beneficiary, dates, supplies) is stored on approval, so an approved row has
-- a beneficiary and an evaluation date. Its supplies are part of it, so they
-- go with it.
CREATE TABLE field_sheets (
    id UUID PRIMARY KEY,
    task_code VARCHAR(20) NOT NULL,
    beneficiary_document_type VARCHAR(20),
    beneficiary_document_number VARCHAR(12),
    evaluation_date TIMESTAMP,
    securement_date TIMESTAMP,
    scan_key TEXT NOT NULL,
    scan_size BIGINT NOT NULL CHECK (scan_size > 0),
    submitted_by UUID NOT NULL,
    submission_date TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('SUBMITTED', 'DELIVERED', 'APPROVED', 'REJECTED', 'VOIDED')),
    reviewed_by UUID,
    review_date TIMESTAMP,
    CHECK ((reviewed_by IS NULL) = (review_date IS NULL)),
    CHECK ((beneficiary_document_type IS NULL) = (beneficiary_document_number IS NULL)),
    CHECK (status <> 'APPROVED' OR (beneficiary_document_type IS NOT NULL AND evaluation_date IS NOT NULL))
);

CREATE TABLE field_sheet_supplies (
    field_sheet_id UUID NOT NULL REFERENCES field_sheets (id) ON DELETE CASCADE,
    position INTEGER NOT NULL,
    name TEXT NOT NULL,
    amount NUMERIC NOT NULL CHECK (amount >= 0),
    unit VARCHAR(20) NOT NULL,
    PRIMARY KEY (field_sheet_id, position)
);
