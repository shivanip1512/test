<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>

<cti:standardPage module="amr" page="meterDetail.electric">
    
    <dt:pickerIncludes/>
    
    <cti:includeCss link="/resources/js/lib/dynatree/skin/device.group.css"/>
    
    <c:if test="${not hasActions}">
        <div id="page-buttons">
            <cti:url var="collectionActionsUrl" value="/bulk/collectionActions">
                <cti:param name="collectionType" value="idList"/>
                <cti:param name="idList.ids" value="${deviceId}"/>
            </cti:url>
            <cti:button icon="icon-cog" href="${collectionActionsUrl}" nameKey="otherActions"/> 
        </div>
    </c:if>
    
    <c:if test="${hasActions}">
        <div id="page-actions" class="dn">
            <c:if test="${moveSupported}">
                <cti:url var="moveInUrl" value="/meter/moveIn">
                    <cti:param name="deviceId" value="${deviceId}"/>
                </cti:url>
                <cti:url var="moveOutUrl" value="/meter/moveOut">
                    <cti:param name="deviceId" value="${deviceId}"/>
                </cti:url>
                <cm:dropdownOption key=".moveIn" href="${moveInUrl}"/>
                <cm:dropdownOption key=".moveOut" href="${moveOutUrl}"/>
            </c:if>
            
            <!-- Actions: High Bill Complaint -->
            <cti:checkRolesAndProperties value="HIGH_BILL_COMPLAINT">
                <c:if test="${highBillSupported}">
                    <cti:url var="highBillUrl" value="/meter/highBill/view">
                        <cti:param name="deviceId" value="${deviceId}"/>
                    </cti:url>
                    <cm:dropdownOption key=".highBill" href="${highBillUrl}"/>
                </c:if>
            </cti:checkRolesAndProperties>

            <!-- Actions: Profile -->
            <%-- need one of these at least for the profile page to display anything --%>
            <c:if test="${lpSupported || peakReportSupported}">
                <cti:url var="profileUrl" value="/amr/profile/home">
                    <cti:param name="deviceId" value="${deviceId}"/>
                </cti:url>
                <cm:dropdownOption key=".profile" href="${profileUrl}"/>
            </c:if >

            <!-- Actions: Voltage & TOU -->
            <c:if test="${showVoltageAndTou}">
                <cti:url var="voltageTouUrl" value="/amr/voltageAndTou/home">
                    <cti:param name="deviceId" value="${deviceId}"/>
                </cti:url>
                <cm:dropdownOption key=".voltageAndTou" href="${voltageTouUrl}"/>
            </c:if>

            <!-- Actions: Manual Commander -->
            <cti:checkRolesAndProperties value="ENABLE_WEB_COMMANDER">
                    <cti:url var="commanderUrl" value="/amr/manualCommand/home">
                        <cti:param name="deviceId" value="${deviceId}"/>
                    </cti:url>
                    <cm:dropdownOption key=".manualCommander" href="${commanderUrl}"/>
            </cti:checkRolesAndProperties>

            <!-- Actions: Locate Route -->
            <cti:checkRolesAndProperties value="LOCATE_ROUTE">
                <c:if test="${porterCommandRequestsSupported}">
                    <cti:url var="routeLocateUrl" value="/bulk/routeLocate/home">
                        <cti:param name="collectionType" value="idList"/>
                        <cti:param name="idList.ids" value="${deviceId}"/>
                    </cti:url>
                    <cm:dropdownOption key=".locateRoute" href="${routeLocateUrl}"/>
                </c:if>
            </cti:checkRolesAndProperties>

            <!-- Actions: Other Collection actions -->
            <cti:url var="collectionActionsUrl" value="/bulk/collectionActions">
                <cti:param name="collectionType" value="idList"/>
                <cti:param name="idList.ids" value="${deviceId}"/>
            </cti:url>
            <li class="divider"/>
            <cm:dropdownOption key=".otherActions.label" href="${collectionActionsUrl}"/>
        </div>
    </c:if>

    <tags:widgetContainer deviceId="${deviceId}" identify="false">
        
        <div class="column-12-12 clear">
            <div class="one column">
                
                <tags:widget bean="meterInformationWidget"/>
                
                <c:choose>
                    <c:when test="${threePhaseVoltageOrCurrentSupported}">
                       <tags:widget bean="polyphaseMeterReadingsWidget"/>
                    </c:when>
                    <c:otherwise>
                       <tags:widget bean="meterReadingsWidget"/>
                    </c:otherwise>
                </c:choose>
                
                <c:if test="${showRfMetadata}">
                    <tags:widget bean="rfnDeviceMetadataWidget"/>
                </c:if>
                
                <c:if test="${cisInfoWidgetName != null}">
                    <tags:widget bean="${cisInfoWidgetName}"/>
                </c:if>
                
                <!-- Including deviceGroupWidget's resources here since this particular
                     widget is being added to the page via ajax  -->
                <cti:includeScript link="JQUERY_COOKIE"/>
                <cti:includeScript link="JQUERY_SCROLLTO"/>
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
                    
                    <c:if test="${disconnectSupported}">
                        <cti:url var="disconnectHelpUrl" value="/widget/disconnectMeterWidget/helpInfo">
                            <cti:param name="deviceId" value="${deviceId}"/>
                            <cti:param name="shortName" value="disconnectMeterWidget"/>
                        </cti:url>
                        <tags:widget bean="disconnectMeterWidget" helpUrl="${disconnectHelpUrl}"/>
                    </c:if>
                    
                    <c:if test="${rfnEventsSupported}">
                        <tags:widget bean="meterEventsWidget"/>
                    </c:if>
                    
                    <c:if test="${outageSupported}">
                        <tags:widget bean="meterOutagesWidget"/>
                    </c:if>
                    
                    <c:if test="${rfnOutageSupported}">
                        <tags:widget bean="rfnOutagesWidget"/>
                    </c:if>
                    
                    <c:if test="${touSupported}">
                        <tags:widget bean="touWidget"/>
                    </c:if>
                    
                    <c:if test="${configSupported}">
                        <tags:widget bean="configWidget"/>
                    </c:if>
            </div>
        </div>

    </tags:widgetContainer>
    
</cti:standardPage>