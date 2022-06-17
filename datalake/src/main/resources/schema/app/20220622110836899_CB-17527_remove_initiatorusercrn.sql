-- // CB-17527 remove usage of initiatoruser in sdx service in order to avoid ums notfound issues
-- Migration SQL that makes the change goes here.

SELECT id, initiatorUserCrn INTO tmp_sdxcluster_initiator FROM sdxcluster;
ALTER TABLE sdxcluster DROP COLUMN IF EXISTS initiatorUserCrn;

-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE sdxcluster ADD COLUMN IF NOT EXISTS initiatorUserCrn character varying(255);
UPDATE sdxcluster s SET initiatorUserCrn = (SELECT t.initiatorUserCrn FROM tmp_sdxcluster_initiator t WHERE t.id = s.id );
ALTER TABLE sdxcluster ALTER COLUMN initiatorUserCrn SET NOT NULL;
DROP TABLE IF EXISTS tmp_sdxcluster_initiator;
