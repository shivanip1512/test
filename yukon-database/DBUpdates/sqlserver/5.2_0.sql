/******************************************/ 
/**** SQL Server DBupdates             ****/ 
/******************************************/ 

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

CREATE UNIQUE INDEX Indx_CRFAdd_SerNum_Man_Mod_UNQ ON CRFAddress (
SerialNumber ASC,
Manufacturer ASC,
Model ASC
);
GO
/* End YUK-8718 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
