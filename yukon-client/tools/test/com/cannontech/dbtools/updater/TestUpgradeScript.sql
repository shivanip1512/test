/******************************************/
/**** SQL Server / Oracle DBupdates    ****/
/******************************************/

/*** NEW YUKID CHANGE, MUST EXECUTE AS YUK-21 DOES NOT EXIST IN DBUPDATES TABLE ***/
/* @start YUK-21 */
UPDATE state SET foregroundcolor = 4 WHERE stategroupid = -28 AND rawstate = 1;
INSERT INTO DBUpdates VALUES ('YUK-21', '7.0.1', GETDATE());
/* @end YUK-21 */

/*** NEW YUKID CHANGE, MUST EXECUTE AS YUK-26 DOES NOT EXIST IN DBUPDATES TABLE ***/
/* @start YUK-26 */
/* @start-block */
UPDATE state SET foregroundcolor = 4 WHERE stategroupid = -28 AND rawstate = 1;
INSERT INTO DBUpdates VALUES ('YUK-26', '7.0.1', GETDATE());
/* @end-block */
/* @end YUK-26 */

/*** SHOULD NOT EXECUTE AS YUK-111 ALREADY EXISTS IN DBUPDATES TABLE ***/
/* @start YUK-111 */
UPDATE state SET foregroundcolor = 4 WHERE stategroupid = -28 AND rawstate = 1;
INSERT INTO DBUpdates VALUES ('YUK-111', '7.0.1', GETDATE());
/* @end YUK-111 */

/*** SHOULD NOT EXECUTE AS YUK-116 ALREADY EXISTS IN DBUPDATES TABLE ***/
/* @start YUK-116 */
/* @start-block */
UPDATE state SET foregroundcolor = 4 WHERE stategroupid = -28 AND rawstate = 1;
INSERT INTO DBUpdates VALUES ('YUK-116', '7.0.1', GETDATE());
/* @end-block */
/* @end YUK-116 */

/*** SHOULD EXECUTE AS YUK-16225 IS ALREADY AVAILABLE AND YUK-22 IS NOT AVAILABLE IN DBUPDATES TABLE ***/
/* @start YUK-22 if YUK-16225 */
UPDATE state SET foregroundcolor = 4 WHERE stategroupid = -28 AND rawstate = 1;
INSERT INTO DBUpdates VALUES ('YUK-22', '7.0.1', GETDATE());
/* @end YUK-22 */

/*** SHOULD NOT EXECUTE AS YUK-6 IS NOT AVAILABLE IN DBUPDATES TABLE ***/
/* @start YUK-23 if 6 */
UPDATE state SET foregroundcolor = 1 WHERE stategroupid = -28 AND rawstate = 0;
UPDATE state SET foregroundcolor = 4 WHERE stategroupid = -28 AND rawstate = 1;
INSERT INTO DBUpdates VALUES ('YUK-23', '7.0.1', GETDATE());
/* @end YUK-23 */

/*** SHOULD NOT EXECUTE AS YUK-6 IS NOT AVAILABLE IN DBUPDATES TABLE ***/
/* @start YUK-24 if YUK-6 */
/* @start-block */
UPDATE state SET foregroundcolor = 1 WHERE stategroupid = -28 AND rawstate = 0;
UPDATE state SET foregroundcolor = 4 WHERE stategroupid = -28 AND rawstate = 1;
INSERT INTO DBUpdates VALUES ('YUK-24', '7.0.1', GETDATE());
/* @end-block */
/* @end YUK-24 */

/*** SHOULD EXECUTE AS YUK-16225 IS ALREADY AVAILABLE AND YUK-25 IS NOT AVAILABLE IN DBUPDATES TABLE ***/
/* @start YUK-25 if YUK-16225 */
/* @start-block */
UPDATE state SET foregroundcolor = 1 WHERE stategroupid = -28 AND rawstate = 0;
UPDATE state SET foregroundcolor = 4 WHERE stategroupid = -28 AND rawstate = 1;
INSERT INTO DBUpdates VALUES ('YUK-25', '7.0.1', GETDATE());
/* @end-block */
/* @end YUK-25 */

/*** SHOULD NOT EXECUTE AS YUK-111 IS ALREADY AVAILABLE IN DBUPDATES TABLE ***/
/* @start YUK-111 if YUK-16225 */
UPDATE state SET foregroundcolor = 4 WHERE stategroupid = -28 AND rawstate = 1;
INSERT INTO DBUpdates VALUES ('YUK-111', '7.0.1', GETDATE());
/* @end YUK-111 */

/* @start YUK-32 if YUK-16225 */
/* @error ignore-begin */
UPDATE state SET foregroundcolor = 1 WHERE stategroupid = -28  rawstate = 0;
UPDATE state SET foregroundcolor = 4 WHERE stategroupid = -28 AND rawstate = 1;
/* @error ignore-end */
INSERT INTO DBUpdates VALUES ('YUK-32', '7.0.1', GETDATE());
/* @end YUK-32 */

/* @start YUK-33 */
/* @error ignore-begin */
UPDATE state SET foregroundcolor = 1 WHERE stategroupid = -28 rawstate = 0;
INSERT INTO DBUpdates VALUES ('YUK-33', '7.0.1', GETDATE());
/* @error ignore-end */
/* @end YUK-33 */

/* @start YUK-30 */
/* @error warn-once */
/* @start-block */
UPDATE state SET foregroundcolor = 4 WHERE stategroupid = -28 AND rawstate = 1;
INSERT INTO DBUpdates VALUES ('YUK-30', '7.0.1', GETDATE());
/* @end-block */
/* @end YUK-30 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('7.0', '26-FEB-2018', 'Latest Update', 1, GETDATE());