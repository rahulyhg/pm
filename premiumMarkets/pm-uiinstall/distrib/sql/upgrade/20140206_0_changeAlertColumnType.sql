ALTER TABLE ALERTS ADD COLUMN VALUEb DECIMAL(20,4) DEFAULT 0;
UPDATE ALERTS SET VALUEb="VALUE";
ALTER TABLE ALERTS DROP COLUMN "VALUE";
RENAME COLUMN ALERTS.VALUEb TO "VALUE";

