<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr"%>

<cti:standardPage module="operator" page="enrollmentHistory">

<dr:enrollmentHistory hardwareConfigActions="${hardwareConfigActions}"
    isHistoryPage="true"/>

</cti:standardPage>
