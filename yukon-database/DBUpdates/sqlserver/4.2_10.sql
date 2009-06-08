/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

/* Start YUK-7529 */
DROP VIEW CCOperationsASent_View;
DROP VIEW CCOperationsBConfirmed_View;
/* @error ignore-begin */
/* We want to use schema binding if possible.  This will give us a performance boost. */
SET NUMERIC_ROUNDABORT OFF;
SET ANSI_PADDING, ANSI_WARNINGS, CONCAT_NULL_YIELDS_NULL, ARITHABORT ON;
SET QUOTED_IDENTIFIER, ANSI_NULLS ON;
GO
CREATE VIEW [dbo].[CCOperationsASent_View] with schemabinding
AS
SELECT LogId, PointId, ActionId, DateTime, Text, FeederId, SubId, AdditionalInfo
FROM dbo.CCEventLog
WHERE EventType = 1
AND ActionId > -1;
GO

SET NUMERIC_ROUNDABORT OFF;
SET ANSI_PADDING, ANSI_WARNINGS, CONCAT_NULL_YIELDS_NULL, ARITHABORT ON;
SET QUOTED_IDENTIFIER, ANSI_NULLS ON;
GO
CREATE VIEW [dbo].[CCOperationsBConfirmed_View] with schemabinding
AS
SELECT LogId, PointId, ActionId, DateTime, Text, KvarBefore, KvarAfter, KvarChange, CapBankStateInfo
FROM dbo.CCEventLog
WHERE EventType = 0
AND ActionId > -1;
GO

CREATE VIEW CCOperationsASent_View
AS
SELECT LogId, PointId, ActionId, DateTime, Text, FeederId, SubId, AdditionalInfo
FROM CCEventLog
WHERE EventType = 1
AND ActionId > -1;
GO

CREATE VIEW CCOperationsBConfirmed_View
AS
SELECT LogId, PointId, ActionId, DateTime, Text, KvarBefore, KvarAfter, KvarChange, CapBankStateInfo
FROM CCEventLog
WHERE EventType = 0
AND ActionId > -1;
GO
/* @error ignore-end */
/* End YUK-7529 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
INSERT INTO CTIDatabase VALUES('4.2', 'Matt K', '05-JUN-2009', 'Latest Update', 10);
