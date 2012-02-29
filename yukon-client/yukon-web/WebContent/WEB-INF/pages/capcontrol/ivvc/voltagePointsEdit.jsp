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

	<tags:boxContainer2 nameKey=".title">
		<form:form action="updateVoltagePoints" commandName="zoneVoltagePointsHolder">
			<input name="zoneId" value="${zoneId}" type="hidden"/>
			<tags:alternateRowReset />
			<table class="compactResultsTable ">
				<tr>
					<th><i:inline key=".table.header.deviceName"/></th>
					<th><i:inline key=".table.header.pointName"/></th>
					<th><i:inline key=".table.header.phase"/></th>
					<th><i:inline key=".table.header.lowerLimit"/></th>
					<th><i:inline key=".table.header.currentVoltage"/></th>
					<th><i:inline key=".table.header.upperLimit"/></th>
					<th><i:inline key=".table.header.overrideStrategy"/></th>
				</tr>
				<c:forEach var="voltagePoint" items="${zoneVoltagePointsHolder.points}" varStatus="status">
					<form:hidden path="points[${status.index}].paoName"/>
					<form:hidden path="points[${status.index}].paoIdentifier"/>
					<form:hidden path="points[${status.index}].pointId"/>
					<form:hidden path="points[${status.index}].pointName"/>
				
					<tr class="<tags:alternateRow even="altTableCell" odd="tableCell"/>">
						<td>
							<a href="/editor/cbcBase.jsf?type=2&amp;itemid=${voltagePoint.paoIdentifier.paoId}">
								<spring:escapeBody htmlEscape="true">
									${voltagePoint.paoName}
								</spring:escapeBody>
							</a>
						</td>
						<td><spring:escapeBody htmlEscape="true">${voltagePoint.pointName}</spring:escapeBody></td>
						<td>
							<form:select path="points[${status.index}].phase">
								<form:option value=""><cti:msg2 key="yukon.web.defaults.dashes"/></form:option>
								<form:option value="A"><cti:msg2 key="yukon.common.phase.phaseA"/></form:option>
								<form:option value="B"><cti:msg2 key="yukon.common.phase.phaseB"/></form:option>
								<form:option value="C"><cti:msg2 key="yukon.common.phase.phaseC"/></form:option>
							</form:select>
						</td>
						<td>
							<tags:input path="points[${status.index}].lowerLimit" size="4"/>
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
							<tags:input path="points[${status.index}].upperLimit" size="4"/>
						</td>
						<td><form:checkbox path="points[${status.index}].overrideStrategy"/></td>
					</tr>
				</c:forEach>
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