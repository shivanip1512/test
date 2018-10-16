<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<cti:verifyRolesAndProperties value="APPLICATION_BILLING"/>

<cti:msgScope paths="modules.amr.billing.schedule">

<div class="error dn js-errors"></div>
<div class="success dn js-success"></div>

	<form:form id="scheduleForm" modelAttribute="exportData" action="/scheduledBilling/scheduleExport" cssClass="stacked">
        <cti:csrfToken/>
        <div class="column-12-12 clearfix">
            <div class="column one">
                <tags:sectionContainer2 nameKey="billingParameters">
                    <tags:nameValueContainer2 id="billingParametersContainer">
                        <c:forEach var="group" items="${deviceGroups}">
                            <input type="hidden" name="deviceGroups" value="${fn:escapeXml(group)}">
                        </c:forEach>
                        <input type="hidden" name="fileFormat" value="${fn:escapeXml(fileFormat)}">
                        <input type="hidden" name="demandDays" value="${demandDays}">
                        <input type="hidden" name="energyDays" value="${energyDays}">
                        <input type="hidden" name="removeMultiplier" value="${removeMultiplier}">
                        <c:if test="${not empty jobId}">
                            <input type="hidden" name="jobId" value="${jobId}">
                        </c:if>
                        
                        <tags:nameValue2 nameKey=".deviceGroups">
                            ${fn:escapeXml(groupNames)}
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey="yukon.web.billing.fileFormat">
                            ${fn:escapeXml(formatName)}
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
                </tags:sectionContainer2>
            </div>
            <div class="column two nogutter">
                <tags:sectionContainer2 nameKey="exportParameters">
                    <tags:nameValueContainer2 id="exportParametersContainer">
                        <tags:scheduledFileExportInputs cronExpressionTagState="${cronExpressionTagState}" exportData="${exportData}" />
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
            </div>
        </div>
		
        <div class="action-area">
    		<c:if test="${not empty jobId}">
    			<c:set var="nameKey" value="update"/>
    		</c:if>
    		<c:if test="${empty jobId}">
    			<c:set var="nameKey" value="save"/>
    		</c:if>
            <cti:button type="submit" nameKey="${nameKey}" classes="action primary"/>
    		<cti:button classes="js-cancel" nameKey="cancelButton"/>
        </div>
	</form:form>
</cti:msgScope>