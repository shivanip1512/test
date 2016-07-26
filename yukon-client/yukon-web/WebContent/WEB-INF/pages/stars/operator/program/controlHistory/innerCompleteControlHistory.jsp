<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<cti:msgScope paths="modules.operator.controlHistory">
<cti:checkAccountEnergyCompanyOperator showError="true" >
   <dr:controlHistory groupedControlHistory="${groupedControlHistory}" programId="${programId}" />
</cti:checkAccountEnergyCompanyOperator>
</cti:msgScope>
