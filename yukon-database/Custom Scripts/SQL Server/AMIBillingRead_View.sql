/* These queries were built to integrat between Yukon and Cayenta (CIS vendor) for Energy United.
 * The requirement is for a database view to be created in Yukon for Cayenta to query against 
 * to retrieve billing reads.
 */

--Lookup table that contains DeviceType, PointType, PointOffset mappings for AMIBillingReadView
CREATE TABLE AMIBillingReadLookup(
   PAOType              VARCHAR(30)          NOT NULL,
   PointType            VARCHAR(20)          NOT NULL,
   PointOffset          NUMERIC              NOT NULL,
   RegisterTP           VARCHAR(10)          NOT NULL,
   RegisterNO           NUMERIC              NOT NULL,
   RegisterLoc	 		VARCHAR(15)			 NOT NULL
   CONSTRAINT PK_AMIBillReadLKUP PRIMARY KEY (PAOType, PointType, PointOffset)
);
GO

INSERT INTO AMIBillingReadLookup VALUES('MCT-410IL', 'PulseAccumulator', 1, 'KWH', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-410IL', 'PulseAccumulator', 101, 'KWH', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-410IL', 'PulseAccumulator', 121, 'KWH', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-410IL', 'PulseAccumulator', 141, 'KWH', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-410IL', 'PulseAccumulator', 161, 'KWH', 4, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-410IL', 'DemandAccumulator', 11, 'KW', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-410IL', 'DemandAccumulator', 111, 'KW', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-410IL', 'DemandAccumulator', 131, 'KW', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-410IL', 'DemandAccumulator', 151, 'KW', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-410IL', 'DemandAccumulator', 171, 'KW', 4, 'Current');

INSERT INTO AMIBillingReadLookup VALUES('MCT-410FL', 'PulseAccumulator', 1, 'KWH', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-410FL', 'PulseAccumulator', 101, 'KWH', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-410FL', 'PulseAccumulator', 121, 'KWH', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-410FL', 'PulseAccumulator', 141, 'KWH', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-410FL', 'PulseAccumulator', 161, 'KWH', 4, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-410FL', 'DemandAccumulator', 11, 'KW', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-410FL', 'DemandAccumulator', 111, 'KW', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-410FL', 'DemandAccumulator', 131, 'KW', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-410FL', 'DemandAccumulator', 151, 'KW', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-410FL', 'DemandAccumulator', 171, 'KW', 4, 'Current');

INSERT INTO AMIBillingReadLookup VALUES('MCT-410CL', 'PulseAccumulator', 1, 'KWH', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-410CL', 'PulseAccumulator', 101, 'KWH', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-410CL', 'PulseAccumulator', 121, 'KWH', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-410CL', 'PulseAccumulator', 141, 'KWH', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-410CL', 'PulseAccumulator', 161, 'KWH', 4, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-410CL', 'DemandAccumulator', 11, 'KW', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-410CL', 'DemandAccumulator', 111, 'KW', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-410CL', 'DemandAccumulator', 131, 'KW', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-410CL', 'DemandAccumulator', 151, 'KW', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-410CL', 'DemandAccumulator', 171, 'KW', 4, 'Current');

INSERT INTO AMIBillingReadLookup VALUES('MCT-410GL', 'PulseAccumulator', 1, 'KWH', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-410GL', 'PulseAccumulator', 101, 'KWH', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-410GL', 'PulseAccumulator', 121, 'KWH', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-410GL', 'PulseAccumulator', 141, 'KWH', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-410GL', 'PulseAccumulator', 161, 'KWH', 4, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-410GL', 'DemandAccumulator', 11, 'KW', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-410GL', 'DemandAccumulator', 111, 'KW', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-410GL', 'DemandAccumulator', 131, 'KW', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-410GL', 'DemandAccumulator', 151, 'KW', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-410GL', 'DemandAccumulator', 171, 'KW', 4, 'Current');

INSERT INTO AMIBillingReadLookup VALUES('MCT-430A', 'Analog', 1, 'KWH', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A', 'Analog', 3, 'KWH', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A', 'Analog', 5, 'KWH', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A', 'Analog', 7, 'KWH', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A', 'Analog', 9, 'KWH', 4, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A', 'Analog', 2, 'KW', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A', 'Analog', 4, 'KW', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A', 'Analog', 6, 'KW', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A', 'Analog', 8, 'KW', 4, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A', 'Analog', 11, 'KVARH', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A', 'Analog', 13, 'KVARH', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A', 'Analog', 15, 'KVARH', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A', 'Analog', 17, 'KVARH', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A', 'Analog', 19, 'KVARH', 4, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A', 'Analog', 12, 'KVAR', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A', 'Analog', 14, 'KVAR', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A', 'Analog', 16, 'KVAR', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A', 'Analog', 18, 'KVAR', 4, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A', 'Analog', 102, 'KW', 1, 'Frozen');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A', 'Analog', 103, 'KWH', 1, 'Frozen');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A', 'DemandAccumulator', 101, 'CDEM', 0, 'Current');

INSERT INTO AMIBillingReadLookup VALUES('MCT-430A3', 'Analog', 1, 'KWH', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A3', 'Analog', 3, 'KWH', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A3', 'Analog', 5, 'KWH', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A3', 'Analog', 7, 'KWH', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A3', 'Analog', 9, 'KWH', 4, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A3', 'Analog', 2, 'KW', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A3', 'Analog', 4, 'KW', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A3', 'Analog', 6, 'KW', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A3', 'Analog', 8, 'KW', 4, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A3', 'Analog', 11, 'KVARH', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A3', 'Analog', 13, 'KVARH', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A3', 'Analog', 15, 'KVARH', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A3', 'Analog', 17, 'KVARH', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A3', 'Analog', 19, 'KVARH', 4, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A3', 'Analog', 12, 'KVAR', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A3', 'Analog', 14, 'KVAR', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A3', 'Analog', 16, 'KVAR', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A3', 'Analog', 18, 'KVAR', 4, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A3', 'Analog', 102, 'KW', 1, 'Frozen');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A3', 'Analog', 103, 'KWH', 1, 'Frozen');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A3', 'DemandAccumulator', 101, 'CDEM', 0, 'Current');

INSERT INTO AMIBillingReadLookup VALUES('MCT-430S4', 'Analog', 1, 'KWH', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430S4', 'Analog', 3, 'KWH', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430S4', 'Analog', 5, 'KWH', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430S4', 'Analog', 7, 'KWH', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430S4', 'Analog', 9, 'KWH', 4, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430S4', 'Analog', 2, 'KW', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430S4', 'Analog', 4, 'KW', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430S4', 'Analog', 6, 'KW', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430S4', 'Analog', 8, 'KW', 4, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430S4', 'Analog', 11, 'KVARH', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430S4', 'Analog', 13, 'KVARH', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430S4', 'Analog', 15, 'KVARH', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430S4', 'Analog', 17, 'KVARH', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430S4', 'Analog', 19, 'KVARH', 4, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430S4', 'Analog', 12, 'KVAR', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430S4', 'Analog', 14, 'KVAR', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430S4', 'Analog', 16, 'KVAR', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430S4', 'Analog', 18, 'KVAR', 4, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430S4', 'Analog', 102, 'KW', 1, 'Frozen');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430S4', 'Analog', 103, 'KWH', 1, 'Frozen');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430S4', 'DemandAccumulator', 101, 'CDEM', 0, 'Current');

INSERT INTO AMIBillingReadLookup VALUES('MCT-430SL', 'Analog', 1, 'KWH', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430SL', 'Analog', 3, 'KWH', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430SL', 'Analog', 5, 'KWH', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430SL', 'Analog', 7, 'KWH', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430SL', 'Analog', 9, 'KWH', 4, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430SL', 'Analog', 2, 'KW', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430SL', 'Analog', 4, 'KW', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430SL', 'Analog', 6, 'KW', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430SL', 'Analog', 8, 'KW', 4, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430SL', 'Analog', 11, 'KVARH', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430SL', 'Analog', 13, 'KVARH', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430SL', 'Analog', 15, 'KVARH', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430SL', 'Analog', 17, 'KVARH', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430SL', 'Analog', 19, 'KVARH', 4, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430SL', 'Analog', 12, 'KVAR', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430SL', 'Analog', 14, 'KVAR', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430SL', 'Analog', 16, 'KVAR', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430SL', 'Analog', 18, 'KVAR', 4, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430SL', 'Analog', 102, 'KW', 1, 'Frozen');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430SL', 'Analog', 103, 'KWH', 1, 'Frozen');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430SL', 'DemandAccumulator', 101, 'CDEM', 0, 'Current');

INSERT INTO AMIBillingReadLookup VALUES('MCT-470', 'PulseAccumulator', 1, 'KWH', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-470', 'PulseAccumulator', 101, 'KWH', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-470', 'PulseAccumulator', 121, 'KWH', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-470', 'PulseAccumulator', 141, 'KWH', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-470', 'PulseAccumulator', 161, 'KWH', 4, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-470', 'DemandAccumulator', 11, 'KW', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-470', 'DemandAccumulator', 111, 'KW', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-470', 'DemandAccumulator', 131, 'KW', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-470', 'DemandAccumulator', 151, 'KW', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-470', 'DemandAccumulator', 171, 'KW', 4, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-470', 'Analog', 1, 'KWH', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-470', 'Analog', 3, 'KWH', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-470', 'Analog', 5, 'KWH', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-470', 'Analog', 7, 'KWH', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-470', 'Analog', 9, 'KWH', 4, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-470', 'Analog', 2, 'KW', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-470', 'Analog', 4, 'KW', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-470', 'Analog', 6, 'KW', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-470', 'Analog', 8, 'KW', 4, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-470', 'Analog', 11, 'KVARH', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-470', 'Analog', 13, 'KVARH', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-470', 'Analog', 15, 'KVARH', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-470', 'Analog', 17, 'KVARH', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-470', 'Analog', 19, 'KVARH', 4, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-470', 'Analog', 12, 'KVAR', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-470', 'Analog', 14, 'KVAR', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-470', 'Analog', 16, 'KVAR', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-470', 'Analog', 18, 'KVAR', 4, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-470', 'Analog', 102, 'KW', 1, 'Frozen');
INSERT INTO AMIBillingReadLookup VALUES('MCT-470', 'Analog', 103, 'KWH', 1, 'Frozen');

INSERT INTO AMIBillingReadLookup VALUES('MCT-420FL', 'PulseAccumulator', 1, 'KWH', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420FL', 'PulseAccumulator', 101, 'KWH', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420FL', 'PulseAccumulator', 121, 'KWH', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420FL', 'PulseAccumulator', 141, 'KWH', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420FL', 'PulseAccumulator', 161, 'KWH', 4, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420FL', 'DemandAccumulator', 11, 'KW', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420FL', 'DemandAccumulator', 111, 'KW', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420FL', 'DemandAccumulator', 131, 'KW', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420FL', 'DemandAccumulator', 151, 'KW', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420FL', 'DemandAccumulator', 171, 'KW', 4, 'Current');

INSERT INTO AMIBillingReadLookup VALUES('MCT-420FLD', 'PulseAccumulator', 1, 'KWH', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420FLD', 'PulseAccumulator', 101, 'KWH', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420FLD', 'PulseAccumulator', 121, 'KWH', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420FLD', 'PulseAccumulator', 141, 'KWH', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420FLD', 'PulseAccumulator', 161, 'KWH', 4, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420FLD', 'DemandAccumulator', 11, 'KW', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420FLD', 'DemandAccumulator', 111, 'KW', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420FLD', 'DemandAccumulator', 131, 'KW', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420FLD', 'DemandAccumulator', 151, 'KW', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420FLD', 'DemandAccumulator', 171, 'KW', 4, 'Current');

INSERT INTO AMIBillingReadLookup VALUES('MCT-420CL', 'PulseAccumulator', 1, 'KWH', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420CL', 'PulseAccumulator', 101, 'KWH', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420CL', 'PulseAccumulator', 121, 'KWH', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420CL', 'PulseAccumulator', 141, 'KWH', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420CL', 'PulseAccumulator', 161, 'KWH', 4, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420CL', 'DemandAccumulator', 11, 'KW', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420CL', 'DemandAccumulator', 111, 'KW', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420CL', 'DemandAccumulator', 131, 'KW', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420CL', 'DemandAccumulator', 151, 'KW', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420CL', 'DemandAccumulator', 171, 'KW', 4, 'Current');

INSERT INTO AMIBillingReadLookup VALUES('MCT-420CLD', 'PulseAccumulator', 1, 'KWH', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420CLD', 'PulseAccumulator', 101, 'KWH', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420CLD', 'PulseAccumulator', 121, 'KWH', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420CLD', 'PulseAccumulator', 141, 'KWH', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420CLD', 'PulseAccumulator', 161, 'KWH', 4, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420CLD', 'DemandAccumulator', 11, 'KW', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420CLD', 'DemandAccumulator', 111, 'KW', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420CLD', 'DemandAccumulator', 131, 'KW', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420CLD', 'DemandAccumulator', 151, 'KW', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420CLD', 'DemandAccumulator', 171, 'KW', 4, 'Current');

INSERT INTO AMIBillingReadLookup VALUES('RFN-410fL', 'Analog', 1, 'KWH', 0, 'Current');

INSERT INTO AMIBillingReadLookup VALUES('RFN-410fX', 'Analog', 1, 'KWH', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('RFN-410fX', 'Analog', 105, 'KW', 0, 'Current');

INSERT INTO AMIBillingReadLookup VALUES('RFN-410fD', 'Analog', 1, 'KWH', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('RFN-410fD', 'Analog', 105, 'KW', 0, 'Current');

INSERT INTO AMIBillingReadLookup VALUES('RFN-430A3', 'Analog', 1, 'KWH', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('RFN-430A3', 'Analog', 105, 'KW', 0, 'Current');

INSERT INTO AMIBillingReadLookup VALUES('RFN-430KV', 'Analog', 1, 'KWH', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('RFN-430KV', 'Analog', 105, 'KW', 0, 'Current');
--SQLServer
DROP VIEW AMIBillingRead;
GO

CREATE VIEW AMIBillingRead(METER_NO, READ_AMT, READ_DT, REGISTER_TP, REGISTER_NO, POINTID, REGISTER_LOC) as
SELECT MeterNumber, RPH.Value, RPH.Timestamp, RegisterTP, RegisterNO, P.PointId, RegisterLoc
FROM YukonPAObject PAO 
JOIN DeviceMeterGroup DMG ON PAO.PAObjectId = DMG.DeviceId
JOIN Point P on PAO.PAObjectId = P.PAObjectId
JOIN AMIBillingReadLookup LKUP ON (PAO.Type = LKUP.PAOType 
                                   AND P.PointType = LKUP.PointType 
                                   AND P.PointOffset = LKUP.PointOffset)
JOIN (SELECT Timestamp, Value, PointId 
      FROM RawPointHistory
      Where Timestamp > dateadd(d,datediff(d,0,getdate()),-62)) AS RPH ON P.PointId = RPH.PointId;

      

-- FOR UPDATE to add RegisterLoc column - 20090803 SNelson
ALTER TABLE AMIBillingReadLookup ADD RegisterLoc varchar(15);
GO
UPDATE AMIBillingReadLookup SET RegisterLoc = 'Current';
GO
ALTER TABLE AMIBillingReadLookup ALTER COLUMN RegisterLoc varchar(15) not null;

INSERT INTO AMIBillingReadLookup VALUES('MCT-430A', 'Analog', 102, 'KW', 1, 'Frozen');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A', 'Analog', 103, 'KWH', 1, 'Frozen');

INSERT INTO AMIBillingReadLookup VALUES('MCT-430A3', 'Analog', 102, 'KW', 1, 'Frozen');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430A3', 'Analog', 103, 'KWH', 1, 'Frozen');

INSERT INTO AMIBillingReadLookup VALUES('MCT-430S4', 'Analog', 102, 'KW', 1, 'Frozen');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430S4', 'Analog', 103, 'KWH', 1, 'Frozen');

INSERT INTO AMIBillingReadLookup VALUES('MCT-430SL', 'Analog', 102, 'KW', 1, 'Frozen');
INSERT INTO AMIBillingReadLookup VALUES('MCT-430SL', 'Analog', 103, 'KWH', 1, 'Frozen');

INSERT INTO AMIBillingReadLookup VALUES('MCT-470', 'Analog', 102, 'KW', 1, 'Frozen');
INSERT INTO AMIBillingReadLookup VALUES('MCT-470', 'Analog', 103, 'KWH', 1, 'Frozen');
GO

DROP VIEW AMIBillingRead;
GO

CREATE VIEW AMIBillingRead(METER_NO, READ_AMT, READ_DT, REGISTER_TP, REGISTER_NO, POINTID, REGISTER_LOC) as
SELECT MeterNumber, RPH.Value, RPH.Timestamp, RegisterTP, RegisterNO, P.PointId, RegisterLoc
FROM YukonPAObject PAO 
JOIN DeviceMeterGroup DMG ON PAO.PAObjectId = DMG.DeviceId
JOIN Point P on PAO.PAObjectId = P.PAObjectId
JOIN AMIBillingReadLookup LKUP ON (PAO.Type = LKUP.PAOType 
                                   AND P.PointType = LKUP.PointType 
                                   AND P.PointOffset = LKUP.PointOffset)
JOIN (SELECT Timestamp, Value, PointId 
      FROM RawPointHistory
      Where Timestamp > dateadd(d,datediff(d,0,getdate()),-62)) AS RPH ON P.PointId = RPH.PointId;


      
-- FOR UPDATE to add MCT420 and RFN types - 20110410 (Yukon 5.2)
INSERT INTO AMIBillingReadLookup VALUES('MCT-420FL', 'PulseAccumulator', 1, 'KWH', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420FL', 'PulseAccumulator', 101, 'KWH', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420FL', 'PulseAccumulator', 121, 'KWH', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420FL', 'PulseAccumulator', 141, 'KWH', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420FL', 'PulseAccumulator', 161, 'KWH', 4, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420FL', 'DemandAccumulator', 11, 'KW', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420FL', 'DemandAccumulator', 111, 'KW', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420FL', 'DemandAccumulator', 131, 'KW', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420FL', 'DemandAccumulator', 151, 'KW', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420FL', 'DemandAccumulator', 171, 'KW', 4, 'Current');

INSERT INTO AMIBillingReadLookup VALUES('MCT-420FLD', 'PulseAccumulator', 1, 'KWH', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420FLD', 'PulseAccumulator', 101, 'KWH', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420FLD', 'PulseAccumulator', 121, 'KWH', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420FLD', 'PulseAccumulator', 141, 'KWH', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420FLD', 'PulseAccumulator', 161, 'KWH', 4, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420FLD', 'DemandAccumulator', 11, 'KW', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420FLD', 'DemandAccumulator', 111, 'KW', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420FLD', 'DemandAccumulator', 131, 'KW', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420FLD', 'DemandAccumulator', 151, 'KW', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420FLD', 'DemandAccumulator', 171, 'KW', 4, 'Current');

INSERT INTO AMIBillingReadLookup VALUES('MCT-420CL', 'PulseAccumulator', 1, 'KWH', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420CL', 'PulseAccumulator', 101, 'KWH', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420CL', 'PulseAccumulator', 121, 'KWH', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420CL', 'PulseAccumulator', 141, 'KWH', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420CL', 'PulseAccumulator', 161, 'KWH', 4, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420CL', 'DemandAccumulator', 11, 'KW', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420CL', 'DemandAccumulator', 111, 'KW', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420CL', 'DemandAccumulator', 131, 'KW', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420CL', 'DemandAccumulator', 151, 'KW', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420CL', 'DemandAccumulator', 171, 'KW', 4, 'Current');

INSERT INTO AMIBillingReadLookup VALUES('MCT-420CLD', 'PulseAccumulator', 1, 'KWH', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420CLD', 'PulseAccumulator', 101, 'KWH', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420CLD', 'PulseAccumulator', 121, 'KWH', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420CLD', 'PulseAccumulator', 141, 'KWH', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420CLD', 'PulseAccumulator', 161, 'KWH', 4, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420CLD', 'DemandAccumulator', 11, 'KW', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420CLD', 'DemandAccumulator', 111, 'KW', 1, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420CLD', 'DemandAccumulator', 131, 'KW', 2, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420CLD', 'DemandAccumulator', 151, 'KW', 3, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('MCT-420CLD', 'DemandAccumulator', 171, 'KW', 4, 'Current');

INSERT INTO AMIBillingReadLookup VALUES('RFN-410fL', 'Analog', 1, 'KWH', 0, 'Current');

INSERT INTO AMIBillingReadLookup VALUES('RFN-410fX', 'Analog', 1, 'KWH', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('RFN-410fX', 'Analog', 105, 'KW', 0, 'Current');

INSERT INTO AMIBillingReadLookup VALUES('RFN-410fD', 'Analog', 1, 'KWH', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('RFN-410fD', 'Analog', 105, 'KW', 0, 'Current');

INSERT INTO AMIBillingReadLookup VALUES('RFN-430A3', 'Analog', 1, 'KWH', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('RFN-430A3', 'Analog', 105, 'KW', 0, 'Current');

INSERT INTO AMIBillingReadLookup VALUES('RFN-430KV', 'Analog', 1, 'KWH', 0, 'Current');
INSERT INTO AMIBillingReadLookup VALUES('RFN-430KV', 'Analog', 105, 'KW', 0, 'Current');