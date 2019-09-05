<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<cti:standardPage module="amr" page="meterProgramming.summary">

    <div id="page-actions" class="dn">
        <cti:url var="programOthersUrl" value="/collectionActions/home?redirectUrl=/bulk/meterProgramming/inputs"/>
        <cm:dropdownOption key=".programOtherDevices" href="${programOthersUrl}" icon="icon-cog-add"/>
    </div>

</cti:standardPage>