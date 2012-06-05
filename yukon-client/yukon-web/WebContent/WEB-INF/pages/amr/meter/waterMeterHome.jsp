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
				<td class="widgetColumnCell" valign="top">
				    <ct:widget bean="meterInformation" />
	
					<ct:widget bean="waterMeterReadings" />
                    
                    <c:if test="${isRFMesh_JUST_HIDE_FOR_NOW}">
                        <ct:widget bean="rfnMeterInfo" />
                    </c:if>
                    
					<c:if test="${cisInfoWidgetName != null}">
						<ct:widget bean="${cisInfoWidgetName}" />
					</c:if>
	
	                <ct:widget bean="deviceGroup"/>

					<ct:boxContainer2 nameKey="actions" styleClass="widgetContainer">
	                
	                    <!-- Actions: Other Collection actions -->
	                    <cti:url var="collectionActionsUrl" value="/spring/bulk/collectionActions">
	                        <cti:param name="collectionType" value="idList" />
	                        <cti:param name="idList.ids" value="${deviceId}" />
	                    </cti:url>
	                    <a href="${collectionActionsUrl}"><i:inline key=".otherActions"/></a><br/>
	                    
					</ct:boxContainer2>
	
				</td>
				<td class="widgetColumnCell" valign="top">
	
					<ct:widget bean="waterCsrTrend" tabularDataViewer="archivedDataReport" />
				</td>
			</tr>
		</table>

	</ct:widgetContainer>

</cti:standardPage>
