<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="amr" page="meterDetail.electric">
    
    <cti:includeScript link="/resources/js/pages/yukon.ami.meter.details.js"/>
    
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
            <c:if test="${showMoveInOut}">
                <cti:url var="url" value="/meter/moveIn">
                    <cti:param name="deviceId" value="${deviceId}"/>
                </cti:url>
                <cm:dropdownOption key=".moveIn" href="${url}"/>
                <cti:url var="url" value="/meter/moveOut">
                    <cti:param name="deviceId" value="${deviceId}"/>
                </cti:url>
                <cm:dropdownOption key=".moveOut" href="${url}"/>
            </c:if>
            
            <!-- Actions: High Bill Complaint -->
            <c:if test="${showHighBill}">
                <cti:url var="url" value="/meter/highBill/view">
                    <cti:param name="deviceId" value="${deviceId}"/>
                </cti:url>
                <cm:dropdownOption key=".highBill" href="${url}"/>
            </c:if>
            
            <!-- Actions: Profile -->
            <c:if test="${showProfile}">
                <cti:url var="url" value="/amr/profile/home">
                    <cti:param name="deviceId" value="${deviceId}"/>
                </cti:url>
                <cm:dropdownOption key=".profile" href="${url}"/>
            </c:if >
            
            <!-- Actions: Voltage & TOU -->
            <c:if test="${showVoltageAndTou}">
                <cti:url var="url" value="/amr/voltageAndTou/home">
                    <cti:param name="deviceId" value="${deviceId}"/>
                </cti:url>
                <cm:dropdownOption key=".voltageAndTou" href="${url}"/>
            </c:if>
            
            <!-- Actions: Manual Commander -->
            <c:if test="${showCommander}">
                <cm:dropdownOption key=".commander" id="commander-menu-option"/>
            </c:if>
            
            <!-- Actions: Locate Route -->
            <c:if test="${showLocateRoute}">
                <cti:url var="url" value="/bulk/routeLocate/home">
                    <cti:param name="collectionType" value="idList"/>
                    <cti:param name="idList.ids" value="${deviceId}"/>
                </cti:url>
                <cm:dropdownOption key=".locateRoute" href="${url}"/>
            </c:if>
            
            <!-- Actions: Map Network -->
            <c:if test="${showMapNetwork}">
                <cti:url var="mapNetworkUrl" value="/stars/mapNetwork/home?deviceId=${deviceId}"/>
                <cm:dropdownOption key=".mapNetwork" href="${mapNetworkUrl}"/>
            </c:if>
            
            <li class="divider"/>
            
            <!-- Other Collection Actions -->
            <cti:url var="url" value="/bulk/collectionActions">
                <cti:param name="collectionType" value="idList"/>
                <cti:param name="idList.ids" value="${deviceId}"/>
            </cti:url>
            <cm:dropdownOption key=".otherActions.label" href="${url}"/>
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
                
                <!-- Including deviceGroupWidget's resources here since this particular
                     widget is being added to the page via ajax  -->
                <cti:includeScript link="JQUERY_TREE"/>
                <cti:includeScript link="JQUERY_TREE_HELPERS"/>
                <cti:includeCss link="/resources/js/lib/dynatree/skin/ui.dynatree.css"/>
                <tags:widget bean="deviceGroupWidget"/>
            </div>
            
            <div class="column two nogutter">
                <c:set var="whatsThis" value="<div id='trendWidgetWhatsThisText'></div>"/>
                <div id="trendWidget">
                   <tags:widget bean="csrTrendWidget" tabularDataViewer="archivedDataReport" helpText="${whatsThis}"/>
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
                <c:if test="${showConfig}"><tags:widget bean="configWidget"/></c:if>
            </div>
        </div>
    </tags:widgetContainer>
    
</cti:standardPage>