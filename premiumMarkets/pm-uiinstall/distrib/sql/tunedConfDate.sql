update TUNEDCONF set LASTTUNING = '1970-01-01' where LASTTUNING is NULL or LASTTUNING = '0019-06-01';
update TUNEDCONF set LASTCALCULATION = '1970-01-01' where LASTCALCULATION is NULL or LASTCALCULATION = '0019-06-01' or LASTCALCULATION = '0000-00-00';

ALTER TABLE TUNEDCONF  alter column LASTTUNING NOT NULL;
ALTER TABLE TUNEDCONF  alter column LASTCALCULATION NOT NULL;