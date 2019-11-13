<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="amr" page="meterDetail.electric">

<!-- Popup for create meter -->
    <cti:msg2 key="yukon.web.modules.amr.create" var="popupTitle"/>
    <cti:msg2 key="yukon.web.modules.amr.copy" var="copyTitle"/>
    <div id="contentPopup" class="dn"
        data-create-title="${popupTitle}"
        data-copy-title="${copyTitle}"></div>

    <input id="device-id" type="hidden" value="${deviceId}">
    
    <dt:pickerIncludes/>
    <cti:includeCss link="/resources/js/lib/dynatree/skin/device.group.css"/>
    
    <c:if test="${not hasActions}">
        <%-- Let them at least get to the collection actions page. --%>
        <div id="page-buttons">
            <cti:url var="url" value="/bulk/collectionActions">
                <cti:param name="collectionType" value="idList"/>
                <cti:param name="idList.ids" value="${deviceId}"/>
            </cti:url>
            <cti:button icon="icon-cog" href="${url}" nameKey="otherActions"/> 
        </div>
    </c:if>
    
    <%-- Page Actions Button --%>
    
    <c:if test="${hasActions}">
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
                <cm:dropdownOption id="deleteMeter" key="yukon.web.modules.amr.delete" classes="js-hide-dropdown" 
                                   icon="icon-cross" data-ok-event="yukon:meter:delete" />
                <d:confirm on="#deleteMeter"  nameKey="confirmDelete" argument="${deviceName}"/>
                <cti:url var="deleteUrl" value="/meter/${deviceId}"/>
                <form:form id="delete-meter-form" action="${deleteUrl}" method="delete">
                    <cti:csrfToken/>
                </form:form>
            </cti:checkRolesAndProperties>
            <li class="divider"/>
            
            <!-- Actions: Manual Commander -->
            <c:if test="${showCommander}">
                <cm:dropdownOption key=".commander" id="commander-menu-option" icon="icon-ping"/>
            </c:if>
                        
            <!-- Actions: High Bill Complaint -->
            <c:if test="${showHighBill}">
                <cti:url var="url" value="/meter/highBill/view">
                    <cti:param name="deviceId" value="${deviceId}"/>
                </cti:url>
                <cm:dropdownOption key=".highBill" href="${url}" icon="icon-email-error"/>
            </c:if>
            
            <!-- Actions: Locate Route -->
            <c:if test="${showLocateRoute}">
                <cti:url var="url" value="/bulk/routeLocate/home">
                    <cti:param name="collectionType" value="idList"/>
                    <cti:param name="idList.ids" value="${deviceId}"/>
                </cti:url>
                <cm:dropdownOption key=".locateRoute" href="${url}" icon="icon-connect"/>
            </c:if>    
            
            <!-- Actions: Map Network -->
            <c:if test="${showMapNetwork}">
                <cti:url var="mapNetworkUrl" value="/stars/mapNetwork/home?deviceId=${deviceId}"/>
                <cm:dropdownOption key=".mapNetwork" href="${mapNetworkUrl}" icon="icon-map"/>
            </c:if>
            
            <c:if test="${showMoveInOut}">
                <cti:url var="url" value="/meter/moveIn">
                    <cti:param name="deviceId" value="${deviceId}"/>
                </cti:url>
                <cm:dropdownOption key=".moveIn" href="${url}" icon="icon-door-in"/>
                <cti:url var="url" value="/meter/moveOut">
                    <cti:param name="deviceId" value="${deviceId}"/>
                </cti:url>
                <cm:dropdownOption key=".moveOut" href="${url}" icon="icon-door-out"/>
            </c:if>
            
            <!-- Actions: Profile -->
            <c:if test="${showProfile}">
                <cti:url var="url" value="/amr/profile/home">
                    <cti:param name="deviceId" value="${deviceId}"/>
                </cti:url>
                <cm:dropdownOption key=".profile" href="${url}" icon="icon-application-form-magnify"/>
            </c:if >
            
            <!-- Actions: Voltage & TOU -->
            <c:if test="${showVoltageAndTou}">
                <cti:url var="url" value="/amr/voltageAndTou/home">
                    <cti:param name="deviceId" value="${deviceId}"/>
                </cti:url>
                <cm:dropdownOption key=".voltageAndTou" href="${url}" icon="icon-lightning"/>
            </c:if>
            
            <li class="divider"/>
            
            <!-- Other Collection Actions -->
            <cti:url var="url" value="/bulk/collectionActions">
                <cti:param name="collectionType" value="idList"/>
                <cti:param name="idList.ids" value="${deviceId}"/>
            </cti:url>
            <cm:dropdownOption key=".otherActions.label" href="${url}" icon="icon-cog-go"/>
        </div>
    </c:if>
    
    <tags:widgetContainer deviceId="${deviceId}" identify="false">
        <div class="column-12-12 clear">
            <div class="one column">
                <tags:widget bean="meterInformationWidget"/>
                
                <c:choose>
                    <c:when test="${showPolyphaseReadings}">
                       <tags:widget bean="polyphaseMeterReadingsWidget"/>
                    </c:when>
                    <c:otherwise>
                       <tags:widget bean="meterReadingsWidget"/>
                    </c:otherwise>
                </c:choose>
                
                <c:if test="${showRfMetadata}"><tags:widget bean="rfnDeviceMetadataWidget"/></c:if>
                <c:if test="${showCis}"><tags:widget bean="${cisInfoWidgetName}"/></c:if>
                <tags:widget bean="paoNotesWidget"/>
                
                <!-- Including deviceGroupWidget's resources here since this particular
                     widget is being added to the page via ajax  -->
                <cti:includeScript link="JQUERY_TREE"/>
                <cti:includeScript link="JQUERY_TREE_HELPERS"/>
                <cti:includeCss link="/resources/js/lib/dynatree/skin/ui.dynatree.css"/>
                <tags:widget bean="deviceGroupWidget"/>
            </div>
            
            <div class="column two nogutter">
                <div id="trendWidget">
                   <tags:widget bean="csrTrendWidget" tabularDataViewer="archivedDataReport" showHelpIcon="true"/>
                </div>
                
                <c:if test="${showDisconnect}">
                    <cti:url var="url" value="/widget/disconnectMeterWidget/helpInfo">
                        <cti:param name="deviceId" value="${deviceId}"/>
                        <cti:param name="shortName" value="disconnectMeterWidget"/>
                    </cti:url>
                    <tags:widget bean="disconnectMeterWidget" helpUrl="${url}"/>
                </c:if>
                
                <c:if test="${showEvents}"><tags:widget bean="meterEventsWidget"/></c:if>
                <c:if test="${showOutage}"><tags:widget bean="meterOutagesWidget"/></c:if>
                <c:if test="${showRfOutage}"><tags:widget bean="rfnOutagesWidget"/></c:if>
                <c:if test="${showTou}"><tags:widget bean="touWidget"/></c:if>
                <cti:msg var="widgetHelpText" key="yukon.web.widgets.configWidget.helpText"/>
                <c:if test="${showConfig}"><tags:widget bean="configWidget" helpText="${widgetHelpText}"/></c:if>
            </div>
        </div>
    </tags:widgetContainer>
    <cti:includeScript link="/resources/js/pages/yukon.ami.meter.details.js"/>
</cti:standardPage>
    
