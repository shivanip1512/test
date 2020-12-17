<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="relayDetail">

    <input id="device-id" type="hidden" value="${deviceId}">

    <dt:pickerIncludes/>
    <cti:includeCss link="/resources/js/lib/dynatree/skin/device.group.css"/>

    <%-- Page Actions Button --%>
    <div id="page-actions" class="dn">
        <cti:checkRolesAndProperties value="MANAGE_INFRASTRUCTURE" level="VIEW">
            <!-- Actions: Map Network -->
            <cti:url var="mapNetworkUrl" value="/stars/mapNetwork/home?deviceId=${deviceId}"/>
            <cm:dropdownOption key=".mapNetwork" href="${mapNetworkUrl}" icon="icon-map"/>

            <!-- Other Collection Actions -->
            <cti:url var="url" value="/bulk/collectionActions">
                <cti:param name="collectionType" value="idList"/>
                <cti:param name="idList.ids" value="${fn:escapeXml(deviceId)}"/>
            </cti:url>
            <cm:dropdownOption key=".otherActions.label" href="${url}" icon="icon-cog-go" />
        </cti:checkRolesAndProperties>
        <cti:checkRolesAndProperties value="MANAGE_INFRASTRUCTURE" level="OWNER">
            <li class="divider"/>
            <cm:dropdownOption id="deleteRelay" icon="icon-cross" key="components.button.delete.label" 
                               data-ok-event="yukon:relay:delete" />
            <d:confirm on="#deleteRelay" nameKey="confirmDelete" argument="${deviceName}" />
            <cti:url var="deleteUrl" value="/stars/relay/${deviceId}"/>
            <form:form id="delete-relay-form" action="${deleteUrl}" method="delete">
                <cti:csrfToken/>
            </form:form>
        </cti:checkRolesAndProperties>
    </div>

    <tags:widgetContainer deviceId="${deviceId}" identify="false">
        <div class="column-12-12 clear">
            <div class="one column">
                <tags:widget bean="relayInformationWidget"/>
                <tags:widget bean="rfnDeviceMetadataWidget"/>
                <tags:widget bean="paoNotesWidget"/>
                <tags:widget bean="deviceGroupWidget"/>
            </div>

            <div class="column two nogutter">
                <cti:msg2 var="warningsTitle" key="yukon.web.widgets.infrastructureWarningsWidget"/>
                <tags:widget bean="deviceInfrastructureWarningsWidget" title="${warningsTitle}" deviceId="${deviceId}"/>
                <cti:msg2 var="eventsTitle" key=".relayDetail.relayEvents"/>
                <tags:widget bean="meterEventsWidget" title="${eventsTitle}"/>
                <tags:widget bean="rfnOutagesWidget"/>
                <!-- Including deviceGroupWidget's resources here since this particular
                     widget is being added to the page via ajax  -->
                <cti:includeScript link="JQUERY_TREE"/>
                <cti:includeScript link="JQUERY_TREE_HELPERS"/>
                <cti:includeCss link="/resources/js/lib/dynatree/skin/ui.dynatree.css"/>
            </div>
        </div>
    </tags:widgetContainer>
    <cti:includeScript link="/resources/js/pages/yukon.assets.relay.details.js"/>
</cti:standardPage>