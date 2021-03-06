--NO
ALTER TABLE "APP"."PORTFOLIO" DROP CONSTRAINT "FK_STOCK";
ALTER TABLE "APP"."PORTFOLIO" DROP CONSTRAINT "FK_PF2_STOCK";

ALTER TABLE "APP"."SHARES" ADD CONSTRAINT "SHARES_PKEY" PRIMARY KEY ("SYMBOL", "ISIN", "CATEGORY");

--ALTER TABLE "APP"."SHARES" ADD CONSTRAINT "SHARES_SYMBOL_ISIN" UNIQUE ("SYMBOL", "ISIN");
--ALTER TABLE "APP"."PORTFOLIO" ADD CONSTRAINT "FK_SHARE" FOREIGN KEY ("SYMBOL", "ISIN") REFERENCES "APP"."SHARES" ("SYMBOL", "ISIN") ON DELETE NO ACTION ON UPDATE NO ACTION;

