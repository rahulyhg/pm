drop table ALERTS_ALERTS;
drop table ALERTS;
CREATE TABLE "APP"."ALERTS" ("SYMBOL" VARCHAR(20) NOT NULL, "ISIN" VARCHAR(20) NOT NULL, "NAME" VARCHAR(255) NOT NULL, "ALERTTYPE" VARCHAR(255) NOT NULL,"THRESHOLDTYPE" VARCHAR(255) NOT NULL, "VALUE" NUMERIC(19,2) NOT NULL,"OPTIONALMESSAGE" LONG VARCHAR);
ALTER TABLE "APP"."ALERTS" ADD CONSTRAINT "FK_ALERTS_TO_PORTFOLIO" FOREIGN KEY ("SYMBOL", "ISIN", "NAME") REFERENCES "APP"."PORTFOLIO" ("SYMBOL", "ISIN", "NAME") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "APP"."ALERTS" ADD CONSTRAINT "ALERTS_ID_PKEY" PRIMARY KEY ("SYMBOL", "ISIN", "NAME","THRESHOLDTYPE","ALERTTYPE");
