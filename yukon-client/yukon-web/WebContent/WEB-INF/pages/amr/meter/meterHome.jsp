<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>

<cti:standardPage module="amr" page="meterDetail.electric">
    <dt:pickerIncludes/>

    <cti:includeCss link="/WebConfig/yukon/styles/lib/dynatree/deviceGroup.css"/>

    <c:if test="${not hasActions}">
        <div id="f-page-buttons">
            <cti:url var="collectionActionsUrl" value="/bulk/collectionActions">
                <cti:param name="collectionType" value="idList" />
                <cti:param name="idList.ids" value="${deviceId}" />
            </cti:url>
            <cti:button icon="icon-cog" href="${collectionActionsUrl}" nameKey="otherActions"/> 
        </div>
    </c:if>

    <c:if test="${hasActions}">
        <div id="f-page-actions" class="dn">
            <c:if test="${moveSupported}">
                <cti:url var="moveInUrl" value="/meter/moveIn">
                    <cti:param name="deviceId" value="${deviceId}" />
                </cti:url>
                <cti:url var="moveOutUrl" value="/meter/moveOut">
                    <cti:param name="deviceId" value="${deviceId}" />
                </cti:url>
                <li>
                    <a href="${moveInUrl}"><i:inline key=".moveIn"/></a>
                </li>
                <li>
                    <a href="${moveOutUrl}"><i:inline key=".moveOut"/></a>
                </li>
            </c:if>

            <!-- Actions: High Bill Complaint -->
            <cti:checkRolesAndProperties value="HIGH_BILL_COMPLAINT">
                <c:if test="${highBillSupported}">
                    <cti:url var="highBillUrl" value="/meter/highBill/view">
                        <cti:param name="deviceId" value="${deviceId}" />
                    </cti:url>
                    <li>
                        <a href="${highBillUrl}"><i:inline key=".highBill"/></a>
                    </li>
                </c:if>
            </cti:checkRolesAndProperties>

            <!-- Actions: Profile -->
            <%-- need one of these at least for the profile page to display anything --%>
            <c:if test="${lpSupported || peakReportSupported}">
                <cti:url var="profileUrl" value="/amr/profile/home">
                    <cti:param name="deviceId" value="${deviceId}" />
                </cti:url>
                <li>
                    <a href="${profileUrl}"><i:inline key=".profile"/></a>
                </li>
            </c:if >

            <!-- Actions: Voltage & TOU -->
            <c:if test="${showVoltageAndTou}">
                <cti:url var="voltageTouUrl" value="/amr/voltageAndTou/home">
                    <cti:param name="deviceId" value="${deviceId}" />
                </cti:url>
                <li>
                    <a href="${voltageTouUrl}"><i:inline key=".voltageAndTou"/></a>
                </li>
            </c:if>

            <!-- Actions: Manual Commander -->
            <cti:checkRolesAndProperties value="ENABLE_WEB_COMMANDER">
                    <cti:url var="commanderUrl" value="/amr/manualCommand/home">
                        <cti:param name="deviceId" value="${deviceId}" />
                    </cti:url>
                    <li>
                        <a href="${commanderUrl}"><i:inline key=".manualCommander"/></a>
                    </li>
            </cti:checkRolesAndProperties>

            <!-- Actions: Locate Route -->
            <cti:checkRolesAndProperties value="LOCATE_ROUTE">
                <c:if test="${porterCommandRequestsSupported}">
                    <cti:url var="routeLocateUrl" value="/bulk/routeLocate/home">
                        <cti:param name="collectionType" value="idList" />
                        <cti:param name="idList.ids" value="${deviceId}" />
                    </cti:url>
                    <li>
                        <a href="${routeLocateUrl}"><i:inline key=".locateRoute"/></a>
                    </li>
                </c:if>
            </cti:checkRolesAndProperties>

            <!-- Actions: Other Collection actions -->
            <cti:url var="collectionActionsUrl" value="/bulk/collectionActions">
                <cti:param name="collectionType" value="idList" />
                <cti:param name="idList.ids" value="${deviceId}" />
            </cti:url>
            <li class="divider"/>
            <li>
                <a href="${collectionActionsUrl}"><i:inline key=".otherActions.label"/></a>
            </li>
        </div>
    </c:if>

	<tags:widgetContainer deviceId="${deviceId}" identify="false">

		<div class="column_12_12 clear">
            <div class="one column">
				
				    <tags:widget bean="meterInformationWidget" />

                    <c:choose>
                        <c:when test="${threePhaseVoltageOrCurrentSupported}">
                           <tags:widget bean="polyphaseMeterReadingsWidget" />
                        </c:when>
                        <c:otherwise>
    					   <tags:widget bean="meterReadingsWidget" />
                        </c:otherwise>
                    </c:choose>

                    <c:if test="${showRfMetadata}">
                        <tags:widget bean="rfnDeviceMetadataWidget"/>
                    </c:if>

					<c:if test="${cisInfoWidgetName != null}">
						<tags:widget bean="${cisInfoWidgetName}" />
					</c:if>
	
					<!-- Including deviceGroupWidget's resources here since this particular
					     widget is being added to the page via ajax  -->
					<cti:includeScript link="JQUERY_COOKIE" />
					<cti:includeScript link="JQUERY_SCROLLTO" />
					<cti:includeScript link="JQUERY_TREE" />
					<cti:includeScript link="JQUERY_TREE_HELPERS" />
					<cti:includeCss link="/WebConfig/yukon/styles/lib/dynatree/ui.dynatree.css"/>
	                <tags:widget bean="deviceGroupWidget"/>

            </div>
            
            <div class="column two nogutter">
                    <c:set var="whatsThis" value="<div id='trendWidgetWhatsThisText'></div>"/>
                    <div id="trendWidget">
					   <tags:widget bean="csrTrendWidget" tabularDataViewer="archivedDataReport" helpText="${whatsThis}"/>
					</div>
					
					<c:if test="${disconnectSupported}">
						<tags:widget bean="disconnectMeterWidget"/>
					</c:if>
					
					<c:if test="${rfnDisconnectSupported}">
						<tags:widget bean="rfnMeterDisconnectWidget"/>
					</c:if>
					
                    <c:if test="${rfnEventsSupported}">
    					<tags:widget bean="meterEventsWidget"/>
                    </c:if>
	
					<c:if test="${outageSupported}">
						<tags:widget bean="meterOutagesWidget" />
					</c:if>
                    
                    <c:if test="${rfnOutageSupported}">
                        <tags:widget bean="rfnOutagesWidget"/>
                    </c:if>
	
					<c:if test="${touSupported}">
						<tags:widget bean="touWidget" />
					</c:if>
					
					<c:if test="${configSupported}">
	                    <tags:widget bean="configWidget" />
	                </c:if>
	        </div>
	    </div>

	</tags:widgetContainer>
    
</cti:standardPage>