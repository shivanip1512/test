<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="amr" page="meterDetail.virtual">
    <dt:pickerIncludes/>
    <!-- Meter Create Popup -->
    <cti:msg2 key="yukon.web.modules.amr.create" var="popupTitle"/>
    <cti:msg2 key="yukon.web.modules.amr.copy" var="copyTitle"/>
    <div id="contentPopup" class="dn"
        data-create-title="${popupTitle}"
        data-copy-title="${copyTitle}">
    </div>

    <input id="device-id" type="hidden" value="${id}">

    <cti:url var="collectionActionsUrl" value="/bulk/collectionActions">
        <cti:param name="collectionType" value="idList" />
        <cti:param name="idList.ids" value="${id}" />
    </cti:url>
    
    <div id="page-actions" class="dn">
    <!--  Meter Create Button -->
        <cti:checkRolesAndProperties value="ENDPOINT_PERMISSION" level="CREATE">
            <cm:dropdownOption key="yukon.web.modules.amr.create" classes="js-create-meter" icon="icon-plus-green" data-popup-title="${popupTitle}"/>
        </cti:checkRolesAndProperties>
        <!-- Delete Meter Button -->
        <cti:checkRolesAndProperties value="ENDPOINT_PERMISSION" level="OWNER">
            <cm:dropdownOption id="deleteMeter" key="yukon.web.modules.amr.delete" classes="js-hide-dropdown" icon="icon-delete"  
                               data-ok-event="yukon:meter:delete"/>
            <d:confirm on="#deleteMeter"  nameKey="confirmDelete" argument="${deviceName}"/>
            <cti:url var="deleteUrl" value="/meter/${id}"/>
            <form:form id="delete-meter-form" action="${deleteUrl}" method="delete">
                <cti:csrfToken/>
            </form:form>
        </cti:checkRolesAndProperties>
        
        <li class="divider"/>
                
        <cm:dropdownOption key=".otherActions.label" href="${collectionActionsUrl}" icon="icon-cog-go"/>
    </div>

    <%@ include file="../../stars/virtualDevices/virtualDeviceCommon.jsp" %>
    <cti:includeScript link="/resources/js/pages/yukon.ami.meter.details.js"/>
</cti:standardPage>
