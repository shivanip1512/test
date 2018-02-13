<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>

<cti:standardPage module="adminSetup" page="maintenance.EDIT">
    <form id="maintenance-job-form" action="update" method="post">
        <cti:csrfToken/>
        <c:set var="cronUniqueId" value="cronUniqueId_${job.id}" />
        <input type="hidden" name="jobId" value="${job.id}"> <input type="hidden" name="cronUniqueId"
            value="${cronUniqueId}">
        <tags:sectionContainer2 nameKey="${job.beanName}">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".status">
                    <c:if test="${job.disabled}">
                        <span class="fwb error"><i:inline key=".status.disabled" /> </span>
                    </c:if>
                    <c:if test="${!job.disabled}">
                        <span class="fwb success"><i:inline key=".status.enabled" /> </span>
                    </c:if>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".runSchedule">
                    <tags:cronExpressionData state="${expressionTagState}" id="${cronUniqueId}"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".nextRun">
                    <amr:scheduledGroupRequestExecutionJobNextRunDate jobId="${job.id}" />
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </tags:sectionContainer2>
        <cti:button nameKey="update" type="submit" classes="primary action" busy="true"/>
        <cti:url value="toggleJobEnabled" var="toggleJobEnabled">
            <cti:param name="jobId" value="${job.id}" />
        </cti:url>
        <c:if test="${job.disabled}">
            <cti:button nameKey="enable" classes="js-toggle-job"/>
        </c:if>
        <c:if test="${!job.disabled}">
            <cti:button nameKey="disable" classes="js-toggle-job"/>
        </c:if>
        <cti:button nameKey="cancel" href="view" />
    </form>
    
    <cti:includeScript link="/resources/js/pages/yukon.admin.maintenance.js"/>
</cti:standardPage>