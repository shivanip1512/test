<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<cti:msgScope paths="modules.operator.controlHistory">
<cti:checkAccountEnergyCompanyOperator showError="true" >
    <dr:controlHistorySummary displayableProgramList="${currentControlHistory}"
                              showControlHistorySummary="${true}" 
                              past="false"
                              completeHistoryUrl="/stars/operator/program/controlHistory/completeHistoryView"/>
</cti:checkAccountEnergyCompanyOperator>
</cti:msgScope>