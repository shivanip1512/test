<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="amr" page="meterDetail.water">
    <dt:pickerIncludes/>
    <!-- Meter Create Popup -->
    <cti:msg2 key="yukon.web.modules.amr.create" var="popupTitle"/>
    <cti:msg2 key="yukon.web.modules.amr.copy" var="copyTitle"/>
    <div id="contentPopup" class="dn"
        data-create-title="${popupTitle}"
        data-copy-title="${copyTitle}"></div>
    
    <input id="device-id" type="hidden" value="${deviceId}">
    
    <cti:url var="collectionActionsUrl" value="/bulk/collectionActions">
        <cti:param name="collectionType" value="idList" />
        <cti:param name="idList.ids" value="${deviceId}" />
    </cti:url>
    <cti:url var="waterLeakReportUrl" value="/amr/waterLeakReport/report">
        <cti:param name="collectionType" value="idList" />
        <cti:param name="idList.ids" value="${deviceId}" />
    </cti:url>    
    <div id="page-actions" class="dn">
        <!--  Meter Create Button -->
        <cti:checkRolesAndProperties value="ENDPOINT_PERMISSION" level="CREATE">
            <cm:dropdownOption key="yukon.web.modules.amr.create" classes="js-create-meter" icon="icon-plus-green" data-popup-title="${popupTitle}"/>
        </cti:checkRolesAndProperties>
        <!--  Meter Copy Button -->
        <cti:checkRolesAndProperties value="ENDPOINT_PERMISSION" level="CREATE">
            <cm:dropdownOption key="yukon.web.modules.amr.copy" classes="js-copy-meter" icon="icon-disk-multiple" data-popup-title="${copyTitle}"/>
        </cti:checkRolesAndProperties>
        <!-- Delete Meter Button -->
        <cti:checkRolesAndProperties value="ENDPOINT_PERMISSION" level="OWNER">
            <cm:dropdownOption id="deleteMeter" key="yukon.web.modules.amr.delete" classes="js-hide-dropdown" icon="icon-cross"  
                               data-ok-event="yukon:meter:delete"/>
            <d:confirm on="#deleteMeter"  nameKey="confirmDelete" argument="${deviceName}"/>
            <cti:url var="deleteUrl" value="/meter/${deviceId}"/>
            <form:form id="delete-meter-form" action="${deleteUrl}" method="delete">
                <cti:csrfToken/>
            </form:form>
        </cti:checkRolesAndProperties>
        
        <li class="divider"/>
        
        <!-- Actions: Map Network -->
        <c:if test="${showMapNetwork}">
            <cti:url var="mapNetworkUrl" value="/stars/mapNetwork/home?deviceId=${deviceId}"/>
            <cm:dropdownOption key=".mapNetwork" href="${mapNetworkUrl}" icon="icon-map"/>
        </c:if>
        
        <!-- Water Leak Report -->
        <cti:checkRolesAndProperties value="WATER_LEAK_REPORT">
            <cm:dropdownOption key=".waterLeakReport.report.pageName" href="${waterLeakReportUrl}" icon="icon-application-view-columns"/>
        </cti:checkRolesAndProperties>
        
        <li class="divider"/>
        
        <cm:dropdownOption key=".otherActions.label" href="${collectionActionsUrl}" icon="icon-cog-go"/>
    </div>

	<tags:widgetContainer deviceId="${deviceId}" identify="false">

        <div class="column-12-12">
            <div class="column one">
                <tags:widget bean="meterInformationWidget" />
                <tags:widget bean="waterMeterReadingsWidget" />

                <c:if test="${showRfMetadata}">
                    <tags:widget bean="rfnDeviceMetadataWidget" />
                </c:if>

                <c:if test="${cisInfoWidgetName != null}">
                    <tags:widget bean="${cisInfoWidgetName}" />
                </c:if>
                
                <tags:widget bean="paoNotesWidget"/>

                <!-- Including deviceGroupWidget's resources here since this particular widget is being added to the page via ajax  -->
                <cti:includeScript link="JQUERY_TREE" />
                <cti:includeScript link="JQUERY_TREE_HELPERS" />
                <cti:includeCss link="/resources/js/lib/dynatree/skin/ui.dynatree.css" />
                <tags:widget bean="deviceGroupWidget" />
            </div>
            <div class="column two nogutter">
                <div id="trendWidget">
                    <tags:widget bean="waterCsrTrendWidget" tabularDataViewer="archivedDataReport" showHelpIcon="true"/>
                </div>

                <c:if test="${showEvents}"><tags:widget bean="meterEventsWidget"/></c:if>
                <c:if test="${deviceConfigSupported}"><tags:widget bean="configWidget"/></c:if>
            </div>
        </div>
    </tags:widgetContainer>
    <cti:includeScript link="/resources/js/pages/yukon.ami.meter.details.js"/>
</cti:standardPage>