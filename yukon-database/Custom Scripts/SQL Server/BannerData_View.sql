/*
This query is provided to create a view of data available from New Bern's billing system (Banner).
This query is used by the Yukon Banner biling file format directly.
openquery requires a Linked Server configuration to be defined. See YUK-13198 for more information.
*/
CREATE VIEW BannerData_View AS
SELECT UIRDIAL_MINV_CODE AS MeterNumber, UCRSERV_PREM_CODE as PremiseNumber, UCRSERV_NUM as ServiceNumber,
UIRDIAL_SCAT_SEQ_NUM as ScatNumber, UCRSERV_ROUTE AS RouteNumber
FROM openquery(CISTEST, 'select UIRDIAL_MINV_CODE, UCRSERV_PREM_CODE, UCRSERV_NUM, UIRDIAL_SCAT_SEQ_NUM,UCRSERV_ROUTE
FROM ucrserv, uirdial
WHERE ucrserv_invn_code = uirdial_minv_code
AND ucrserv_status_ind = ''A''
AND ucrserv_invn_code like ''30000%''')