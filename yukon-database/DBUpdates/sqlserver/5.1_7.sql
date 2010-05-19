/******************************************/ 
/**** SQLServer 2000 DBupdates         ****/ 
/******************************************/ 

/* Start YUK-8710 */
/* @error ignore-begin */
INSERT INTO DeviceTypeCommand VALUES (-810, -52, 'Repeater 850', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-811, -3, 'Repeater 850', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-812, -53, 'Repeater 850', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-813, -54, 'Repeater 850', 4, 'Y', -1);
/* @error ignore-end */
/* End YUK-8710 */

/* Start YUK-8718 */
CREATE TABLE CRFAddress (
   DeviceId             NUMERIC              NOT NULL,
   SerialNumber         VARCHAR(30)          NOT NULL,
   Manufacturer         VARCHAR(80)          NOT NULL,
   Model                VARCHAR(80)          NOT NULL,
   CONSTRAINT PK_CRFAdd PRIMARY KEY (DeviceId)
);
GO

ALTER TABLE CRFAddress
    ADD CONSTRAINT FK_CRFAdd_Device FOREIGN KEY (DeviceId)
        REFERENCES Device (DeviceId)
            ON DELETE CASCADE;
GO
/* End YUK-8718 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
