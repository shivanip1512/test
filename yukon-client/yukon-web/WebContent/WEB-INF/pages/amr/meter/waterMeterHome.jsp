<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>

<cti:standardPage module="amr" page="meterDetail.water">

	<div style="float: right;">
		<amr:searchResultsLink></amr:searchResultsLink>
	</div>

	<ct:widgetContainer deviceId="${deviceId}" identify="false">

		<table class="widgetColumns">
			<tr>
				<td class="widgetColumnCell first" valign="top">
				    <ct:widget bean="meterInformationWidget" />
	
					<ct:widget bean="waterMeterReadingsWidget" />
                    
                    <c:if test="${showRfMetadata}">
                        <ct:widget bean="rfnDeviceMetadataWidget"/>
                    </c:if>
                    
					<c:if test="${cisInfoWidgetName != null}">
						<ct:widget bean="${cisInfoWidgetName}" />
					</c:if>
	
					<!-- Including deviceGroupWidget's resources here since this particular
					     widget is being added to the page via ajax  -->
					<cti:includeScript link="JQUERY_COOKIE" />
					<cti:includeScript link="JQUERY_SCROLLTO" />
					<cti:includeScript link="JQUERY_TREE" />
					<cti:includeScript link="JQUERY_TREE_HELPERS" />
					<cti:includeCss link="/WebConfig/yukon/styles/lib/dynatree/ui.dynatree.css"/>
	                <ct:widget bean="deviceGroupWidget"/>

					<ct:boxContainer2 nameKey="actions" styleClass="widgetContainer">
	                
	                    <!-- Actions: Other Collection actions -->
	                    <cti:url var="collectionActionsUrl" value="/bulk/collectionActions">
	                        <cti:param name="collectionType" value="idList" />
	                        <cti:param name="idList.ids" value="${deviceId}" />
	                    </cti:url>
	                    <a href="${collectionActionsUrl}"><i:inline key=".otherActions"/></a><br/>
	                    
					</ct:boxContainer2>
	
				</td>
				<td class="widgetColumnCell last" valign="top">
	
					<ct:widget bean="waterCsrTrendWidget" tabularDataViewer="archivedDataReport" />
				</td>
			</tr>
		</table>

	</ct:widgetContainer>

</cti:standardPage>
