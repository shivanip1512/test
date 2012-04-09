<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="capcontrol" page="ivvc.voltagePoints">
	<%@include file="/capcontrol/capcontrolHeader.jspf"%>
	<cti:includeCss link="/capcontrol/css/ivvc.css" />
	
	<script>
		jQuery(document).ready(function() {
			jQuery("table.compactResultsTable tbody tr td input:checkbox").click(function() {
				jQuery(this).closest("tr").find("input:text").toggleDisabled();
			});
			
			jQuery("form#voltagePointsForm").submit(function() {
				jQuery("table.compactResultsTable tbody tr td input:text:disabled").each(function() {
					var input = jQuery(this);
					var hiddenInput = "<input type='hidden' name='" + input.attr("name") + "' value='" + input.val() + "'/>";
					jQuery("form#voltagePointsForm").append(hiddenInput);
				});
				return true;
			});
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
					    <span class="sub">(<a href="/editor/cbcBase.jsf?type=5&amp;itemid=${strategy.strategyID}" title="${titleText}">${strategy.strategyName}</a>)</span>
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
					<c:if test="${voltagePoint.overrideStrategy == false}">
						<c:set var="disabledInput" value="true"/>
					</c:if>
				
					<tr class="<tags:alternateRow even="altTableCell" odd="tableCell"/>">
						<td>
							<a href="/editor/cbcBase.jsf?type=2&amp;itemid=${voltagePoint.parentPaoIdentifier.paoId}">
								<spring:escapeBody htmlEscape="true">
									${voltagePoint.paoName}
								</spring:escapeBody>
							</a>
						</td>
						<td><spring:escapeBody htmlEscape="true">${voltagePoint.pointName}</spring:escapeBody></td>
						<td>
							<c:choose>
								<c:when test="${!voltagePoint.regulator}">
									<form:select path="points[${status.index}].phase">
										<form:option value="A"><cti:msg2 key="yukon.common.phase.phaseA"/></form:option>
										<form:option value="B"><cti:msg2 key="yukon.common.phase.phaseB"/></form:option>
										<form:option value="C"><cti:msg2 key="yukon.common.phase.phaseC"/></form:option>
									</form:select>
								</c:when>
								<c:otherwise>
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
						<td><form:checkbox path="points[${status.index}].overrideStrategy"/></td>
					</tr>
				</c:forEach>
				</tbody>
			</table>
			<div class="pageActionArea">
				<cti:button nameKey="save" type="submit"/>
				
				<cti:url var="zoneVoltagePointsUrl" value="/spring/capcontrol/ivvc/zone/voltagePoints">
			    	<cti:param name="zoneId" value="${zoneId}"/>
			    </cti:url>
				<cti:button nameKey="reset" href="${zoneVoltagePointsUrl}"/>
		
				<cti:url var="zoneDetailUrl" value="/spring/capcontrol/ivvc/zone/detail">
			    	<cti:param name="zoneId" value="${zoneId}"/>
			    </cti:url>
				<cti:button nameKey="back" id="backBtn" href="${zoneDetailUrl}"/>
			</div>
		</form:form>
	</tags:boxContainer2>
</cti:standardPage>