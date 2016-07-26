<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr"%>

<cti:standardPage module="operator" page="enrollmentHistory">
<cti:checkAccountEnergyCompanyOperator showError="true" >
<dr:enrollmentHistory hardwareConfigActions="${hardwareConfigActions}"
    isHistoryPage="true"/>
</cti:checkAccountEnergyCompanyOperator>
</cti:standardPage>
