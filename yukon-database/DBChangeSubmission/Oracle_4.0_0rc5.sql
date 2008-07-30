/* 4.0_0rc4 to 4.0_0rc5 changes.  These are changes to 4.0 that have been made since 4.0_0rc4*/
/* This script must be run manually using the SQL tool and not the DBToolsFrame tool. */
/* START SPECIAL BLOCK */
/* Start YUK-6093 */
ALTER TABLE DynamicCCCapBank ALTER COLUMN beforeVar VARCHAR(48) NOT NULL; 
ALTER TABLE DynamicCCCapBank ALTER COLUMN afterVar VARCHAR(48) NOT NULL; 
ALTER TABLE DynamicCCCapBank ALTER COLUMN changeVar VARCHAR(48) NOT NULL; 
/* End YUK-6093 */