<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:verifyRolesAndProperties value="APPLICATION_BILLING"/>

<%--
    REQUIRES CONTAINER INCLUDES:
        <cti:includeScript link="/JavaScript/cronExpressionData.js" />
 --%>

<cti:msgScope paths="modules.amr.billing.schedule">

<div class="error dn f-errors"></div>
<div class="success dn f-success"></div>

	<form:form id="scheduleForm" commandName="exportData" action="/scheduledBilling/scheduleExport">
		<tags:boxContainer2 nameKey="billingParameters">
			<tags:nameValueContainer2 id="billingParametersContainer">
				<c:forEach var="group" items="${deviceGroups}">
					<input type="hidden" name="deviceGroups" value="${group}">
				</c:forEach>
				<input type="hidden" name="fileFormat" value="${fileFormat}">
				<input type="hidden" name="demandDays" value="${demandDays}">
				<input type="hidden" name="energyDays" value="${energyDays}">
				<input type="hidden" name="removeMultiplier" value="${removeMultiplier}">
				<c:if test="${not empty jobId}">
					<input type="hidden" name="jobId" value="${jobId}">
				</c:if>
				
				<tags:nameValue2 nameKey=".deviceGroups">
					${groupNames}
				</tags:nameValue2>
				<tags:nameValue2 nameKey="yukon.web.billing.fileFormat">
					${formatName}
				</tags:nameValue2>
				<tags:nameValue2 nameKey="yukon.web.billing.demandDaysPrevious">
					${demandDays}
				</tags:nameValue2>
				<tags:nameValue2 nameKey="yukon.web.billing.energyDaysPrevious">
					${energyDays}
				</tags:nameValue2>
				<tags:nameValue2 nameKey="yukon.web.billing.removeMultiplier">
					${removeMultiplier}
				</tags:nameValue2>
			</tags:nameValueContainer2>
		</tags:boxContainer2>

		<tags:boxContainer2 nameKey="exportParameters">
			<tags:nameValueContainer2 id="exportParametersContainer">
				<tags:scheduledFileExportInputs cronExpressionTagState="${cronExpressionTagState}" exportData="${exportData}" />
			</tags:nameValueContainer2>
		</tags:boxContainer2>

		<c:if test="${not empty jobId}">
			<cti:msg2 key=".updateButton" var="submitText"/>
			<cti:url var="cancelUrl" value="jobs"/>
		</c:if>
		<c:if test="${empty jobId}">
			<cti:msg2 key=".submitButton" var="submitText"/>
			<cti:url var="cancelUrl" value="/billing/home"/>
		</c:if>

		<cti:button classes="f-cancel" nameKey="cancelButton"/>
		<input type="submit" value="${submitText}" />
	</form:form>
</cti:msgScope>