<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="amr" page="meterDetail.water">
    <dt:pickerIncludes/>

    <cti:url var="collectionActionsUrl" value="/bulk/collectionActions">
        <cti:param name="collectionType" value="idList" />
        <cti:param name="idList.ids" value="${deviceId}" />
    </cti:url>
    <cti:checkRolesAndProperties value="WATER_LEAK_REPORT">
        <cti:url var="waterLeakReportUrl" value="/amr/waterLeakReport/report">
            <cti:param name="collectionType" value="idList" />
            <cti:param name="idList.ids" value="${deviceId}" />
        </cti:url>
    </cti:checkRolesAndProperties>
    <div id="page-actions" class="dn">
        <cm:dropdownOption key=".waterLeakReport.report.pageName" href="${waterLeakReportUrl}" />
        <!-- Actions: Map Network -->
        <c:if test="${showMapNetwork}">
            <cti:url var="mapNetworkUrl" value="/stars/mapNetwork/home?deviceId=${deviceId}"/>
            <cm:dropdownOption key=".mapNetwork" href="${mapNetworkUrl}"/>
        </c:if>
        <cm:dropdownOption key=".otherActions.label" href="${collectionActionsUrl}" />
    </div>

	<tags:widgetContainer deviceId="${deviceId}" identify="false">

        <div class="column-12-12">
            <div class="column one">
				    <tags:widget bean="meterInformationWidget" />
	
					<tags:widget bean="waterMeterReadingsWidget" />
                    
                    <c:if test="${showRfMetadata}">
                        <tags:widget bean="rfnDeviceMetadataWidget"/>
                    </c:if>
                    
					<c:if test="${cisInfoWidgetName != null}">
						<tags:widget bean="${cisInfoWidgetName}" />
					</c:if>
	
					<!-- Including deviceGroupWidget's resources here since this particular
					     widget is being added to the page via ajax  -->
					<cti:includeScript link="JQUERY_TREE" />
					<cti:includeScript link="JQUERY_TREE_HELPERS" />
					<cti:includeCss link="/resources/js/lib/dynatree/skin/ui.dynatree.css"/>
	                <tags:widget bean="deviceGroupWidget"/>
            </div>
            <div class="column two nogutter">
                <c:set var="whatsThis" value="<div id='trendWidgetWhatsThisText'></div>"/>
                <div id="trendWidget">
                   <tags:widget bean="waterCsrTrendWidget" tabularDataViewer="archivedDataReport" helpText="${whatsThis}"/>
                </div>
                <c:if test="${deviceConfigSupported}"><tags:widget bean="configWidget"/></c:if>
            </div>
        </div>
    </tags:widgetContainer>

</cti:standardPage>