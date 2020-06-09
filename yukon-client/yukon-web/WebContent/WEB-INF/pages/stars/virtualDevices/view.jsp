<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="virtualDevice.detail">

    <!-- Actions dropdown -->
    <div id="page-actions" class="dn">
        <cti:checkRolesAndProperties value="ENDPOINT_PERMISSION" level="CREATE">
            <cm:dropdownOption icon="icon-plus-green" key="yukon.common.create" data-popup="#js-create-virtual-device-popup"/>
            <cti:url var="createUrl" value="/widget/virtualDeviceInfoWidget/create" />
            <cti:msg2 var="saveText" key="components.button.save.label"/>
            <cti:msg2 var="createPopupTitle" key="yukon.web.modules.operator.virtualDevices.list.create"/>
            <div class="dn" 
                 id="js-create-virtual-device-popup" 
                 data-title="${createPopupTitle}" 
                 data-dialog
                 data-ok-text="${saveText}"
                 data-event="yukon:virtualDevice:save" 
                 data-url="${createUrl}"/>
            </div>
        </cti:checkRolesAndProperties>
        <cti:url var="actionsUrl" value="/bulk/collectionActions">
            <cti:param name="collectionType" value="idList"/>
            <cti:param name="idList.ids" value="${virtualDevice.liteID}"/>
        </cti:url>
        <cm:dropdownOption icon="icon-cog" href="${actionsUrl}" key=".otherActions"/>
        <cti:checkRolesAndProperties value="ENDPOINT_PERMISSION" level="OWNER">
            <li class="divider"/>
            <cm:dropdownOption key="yukon.web.components.button.delete.label" classes="js-hide-dropdown" id="deleteVirtualDevice"
                                   icon="icon-cross" data-ok-event="yukon:virtualDevice:delete" />
                <d:confirm on="#deleteVirtualDevice" nameKey="confirmDelete" argument="${virtualDevice.paoName}"/>
                <cti:url var="deleteUrl" value="/stars/virtualDevice/${virtualDevice.liteID}/delete"/>
                <form:form id="delete-virtualDevice-form" action="${deleteUrl}" method="delete">
                    <cti:csrfToken/>
                </form:form>
        </cti:checkRolesAndProperties>
    </div>
    
    <tags:widgetContainer deviceId="${virtualDevice.liteID}" identify="false">
        <div class="column-12-12 clearfix">
            <div class="one column">
                <tags:widget bean="virtualDeviceInfoWidget" classes="js-virtual-device-info-widget"/>
            </div>
        </div>
    </tags:widgetContainer>
    <cti:includeScript link="/resources/js/pages/yukon.assets.virtualDevice.js"/>
</cti:standardPage>