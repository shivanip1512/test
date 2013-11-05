/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-12709 */
INSERT INTO ThemeProperty (ThemeId, Property, Value) 
(SELECT DISTINCT ThemeId, 'VISITED_COLOR', '#1c49a6' FROM Theme);

INSERT INTO ThemeProperty (ThemeId, Property, Value) 
(SELECT DISTINCT ThemeId, 'BUTTON_COLOR', '#777' FROM Theme);

INSERT INTO ThemeProperty (ThemeId, Property, Value) 
(SELECT DISTINCT ThemeId, 'BUTTON_COLOR_BORDER', '#666' FROM Theme);

INSERT INTO ThemeProperty (ThemeId, Property, Value) 
(SELECT DISTINCT ThemeId, 'BUTTON_COLOR_HOVER', '#888' FROM Theme);
/* End YUK-12709 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('6.0', '05-NOV-2013', 'Latest Update', 3, SYSDATE);