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
    <cti:url var="waterLeakReportUrl" value="/amr/waterLeakReport/report">
        <cti:param name="collectionType" value="idList" />
        <cti:param name="idList.ids" value="${deviceId}" />
    </cti:url>
    <div id="f-page-actions" class="dn">
        <cm:dropdownOption key=".waterLeakReport.report.pageName" href="${waterLeakReportUrl}" />
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
                   <tags:widget bean="waterCsrTrendWidget" tabularDataViewer="archivedDataReport" helpText="${whatsThis}"/>
                </div>
            </div>
        </div>
    </tags:widgetContainer>

</cti:standardPage>