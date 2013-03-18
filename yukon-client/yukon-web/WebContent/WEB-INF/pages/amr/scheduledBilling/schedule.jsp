<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:verifyRolesAndProperties value="APPLICATION_BILLING"/>

<cti:msgScope paths="modules.amr.billing.schedule">

<cti:msg2 key=".pageName" var="pageName"/>
<cti:standardPage title="${pageName}" module="amr">
	<cti:standardMenu menuSelection="billing|generation"/>
	<cti:breadCrumbs>
		<cti:msg2 key="yukon.web.components.button.home.label" var="homeLabel"/>
	    <cti:crumbLink url="/operator/Operations.jsp" title="${homeLabel}" />
	    <cti:crumbLink><cti:msg2 key=".pageTitle"/></cti:crumbLink>
	</cti:breadCrumbs>
	
	<form:form id="scheduleForm" commandName="exportData" action="scheduleExport">
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
				<tags:nameValue2 nameKey=".scheduleName">
					<form:input path="scheduleName" />
				</tags:nameValue2>
				<tags:nameValue2 nameKey=".exportPath">
					<form:input path="exportPath" />
					<cti:url var="infoImg" value="/WebConfig/yukon/Icons/information.gif"/>
                    <img src="${infoImg}" title="<cti:msg2 key=".exportPathInfo"/>"/>
				</tags:nameValue2>
				<tags:nameValue2 nameKey=".exportFileName">
					<form:input path="exportFileName" />
					<img src="${infoImg}" title="<cti:msg2 key=".exportFileNameInfo"/>"/>
				</tags:nameValue2>
				<tags:nameValue2 nameKey=".appendDateToFileName" excludeColon="true">
					<form:checkbox path="appendDateToFileName" />
				</tags:nameValue2>
		
				<tags:nameValue2 nameKey=".scheduleCronString">
					<tags:cronExpressionData id="scheduleCronString" state="${cronExpressionTagState}" allowTypeChange="false" />
				</tags:nameValue2>
		
				<tags:nameValue2 nameKey=".notificationEmailAddresses">
					<form:input path="notificationEmailAddresses" />
				</tags:nameValue2>
			</tags:nameValueContainer2>
		</tags:boxContainer2>
		
		<c:if test="${not empty jobId}">
			<cti:msg2 key=".updateButton" var="submitText"/>
			<cti:url var="cancelUrl" value="jobs"/>
		</c:if>
		<c:if test="${empty jobId}">
			<cti:msg2 key=".submitButton" var="submitText"/>
			<cti:url var="cancelUrl" value="/operator/Metering/Billing.jsp"/>
		</c:if>
		
		<cti:button nameKey="cancelButton" href="${cancelUrl}"/>
		<input type="submit" value="${submitText}" />
	</form:form>
	
</cti:standardPage>
</cti:msgScope>