<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="capcontrol" page="ivvc.voltagePoints">
	<%@include file="/capcontrol/capcontrolHeader.jspf"%>
	<cti:includeCss link="/WebConfig/yukon/styles/da/ivvc.css" />
	
	<script>
		jQuery(document).ready(function() {
			jQuery("table.compactResultsTable tbody tr td input:checkbox").change(function() {
				jQuery(this).closest("tr").find("input:text").toggleDisabled();
			});
			
		    /**
		     * If the user checked the Override Strategy checkbox.. then entered invalid limit values,
		     * then hit save. Since the Override Strategy value is not yet in the database the resulting
		     * error page would show this checkbox as unchecked. This code iterates through these and 
		     * "checks" them for the user.
		     */
			jQuery("input.lowerLimit.error, input.upperLimit.error").each(function() {
			    this.disabled = false;
			    jQuery(this).closest("tr").find("input:checkbox").attr("checked", true);
			});
			Yukon.ui.focusFirstError();
		});
	</script>

	<tags:boxContainer2 nameKey=".title">
		<form:form id="voltagePointsForm" action="updateVoltagePoints" commandName="zoneVoltagePointsHolder">
			<input name="zoneId" value="${zoneId}" type="hidden"/>
			<tags:alternateRowReset />
			<table class="compactResultsTable ">
				<thead>
				<tr>
					<th><i:inline key=".table.header.deviceName"/></th>
					<th><i:inline key=".table.header.pointName"/></th>
					<th><i:inline key=".table.header.phase"/></th>
					<th><i:inline key=".table.header.lowerLimit"/></th>
					<th><i:inline key=".table.header.currentVoltage"/></th>
					<th><i:inline key=".table.header.upperLimit"/></th>
					<th>
						<span class="fl"><i:inline key=".table.header.overrideStrategy"/>&nbsp;</span>
					    <cti:msg2 var="titleText" key=".editStrategy"/>
					    <cti:url var="strategyUrl" value="/editor/cbcBase.jsf">
					        <cti:param name="type" value="5"/>
					        <cti:param name="itemid" value="${strategy.strategyID}"/>
					    </cti:url>
					    <span class="sub">(<a href="${strategyUrl}" title="${titleText}">${strategy.strategyName}</a>)</span>
					</th>
				</tr>
				</thead>
				<tbody>
				<c:forEach var="voltagePoint" items="${zoneVoltagePointsHolder.points}" varStatus="status">
					<form:hidden path="points[${status.index}].paoName"/>
					<form:hidden path="points[${status.index}].parentPaoIdentifier"/>
					<form:hidden path="points[${status.index}].pointId"/>
					<form:hidden path="points[${status.index}].pointName"/>
					
					<c:set var="disabledInput" value="false"/>
					<c:if test="${voltagePoint.overrideStrategy == false || !hasEditingRole}">
						<c:set var="disabledInput" value="true"/>
					</c:if>
				
					<tr class="<tags:alternateRow even="altTableCell" odd="tableCell"/>">
						<td><spring:escapeBody htmlEscape="true">${voltagePoint.paoName}</spring:escapeBody></td>
						<td>
		                    <cti:url value="/editor/pointBase.jsf" var="pointLink">
		                       <cti:param name="parentId" value="${voltagePoint.parentPaoIdentifier.paoId}"/>
		                       <cti:param name="itemid" value="${voltagePoint.pointId}"/>
		                    </cti:url>
		                    <a href="${pointLink}">
		                       <spring:escapeBody htmlEscape="true">${voltagePoint.pointName}</spring:escapeBody>
		                    </a>
                        </td>
						<td>
							<c:choose>
								<c:when test="${!voltagePoint.regulator}">
									<form:select path="points[${status.index}].phase" disabled="${!hasEditingRole}">
										<form:option value="A"><cti:msg2 key="yukon.common.phase.phaseA"/></form:option>
										<form:option value="B"><cti:msg2 key="yukon.common.phase.phaseB"/></form:option>
										<form:option value="C"><cti:msg2 key="yukon.common.phase.phaseC"/></form:option>
									</form:select>
								</c:when>
								<c:otherwise>
								    <form:hidden path="points[${status.index}].phase"/>
									<i:inline key="yukon.common.phase.phase${voltagePoint.phase}"/>
								</c:otherwise>
							</c:choose>
						</td>
						<td>
							<tags:input path="points[${status.index}].lowerLimit" size="4"
									disabled="${disabledInput}" inputClass="lowerLimit" />
						</td>
						<td>
							<c:choose>
								<c:when test="${voltagePoint.pointId > 0}">
									<cti:pointValue pointId="${voltagePoint.pointId}" format="VALUE" />
								</c:when>
								<c:otherwise>
									<i:inline key="yukon.web.defaults.dashes" />
								</c:otherwise>
							</c:choose>
						</td>
						<td>
							<tags:input path="points[${status.index}].upperLimit" size="4"
									disabled="${disabledInput}" inputClass="upperLimit" />
						</td>
						<td><form:checkbox path="points[${status.index}].overrideStrategy" disabled="${!hasEditingRole}"/></td>
					</tr>
				</c:forEach>
				</tbody>
			</table>
			<div class="pageActionArea">
                <c:if test="${hasEditingRole}">
					<cti:button nameKey="save" type="submit"/>
					
					<cti:url var="zoneVoltagePointsUrl" value="/capcontrol/ivvc/zone/voltagePoints">
				    	<cti:param name="zoneId" value="${zoneId}"/>
				    </cti:url>
					<cti:button nameKey="reset" href="${zoneVoltagePointsUrl}"/>
		
					<cti:url var="zoneDetailUrl" value="/capcontrol/ivvc/zone/detail">
				    	<cti:param name="zoneId" value="${zoneId}"/>
				    </cti:url>
					<cti:button nameKey="back" id="backBtn" href="${zoneDetailUrl}"/>
                </c:if>
			</div>
		</form:form>
	</tags:boxContainer2>
</cti:standardPage>