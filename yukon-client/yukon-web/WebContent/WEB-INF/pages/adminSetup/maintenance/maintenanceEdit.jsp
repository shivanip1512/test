<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>

<cti:standardPage module="adminSetup" page="maintenance.EDIT">
    <cti:dataGrid cols="2" tableClasses="twoColumnLayout">
        <cti:dataGridCell>
            <form action="update" method="post">
                <c:set var="cronUniqueId" value="cronUniqueId_${job.id}"/>
                <input type="hidden" name="jobId" value="${job.id}">
                <input type="hidden" name="cronUniqueId" value="${cronUniqueId}">
                <tags:formElementContainer nameKey="rphDuplicateDeletion">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".status">
                            <c:if test="${job.disabled}">
                                <i:inline key=".status.disabled"/>
                            </c:if>
                            <c:if test="${!job.disabled}">
                                <i:inline key=".status.enabled"/>
                            </c:if>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".runSchedule">
                            <tags:cronExpressionData state="${expressionTagState}"
                                id="${cronUniqueId}" allowTypeChange="false" />
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".nextRun">
                            <amr:scheduledGroupRequestExecutionJobNextRunDate jobId="${job.id}"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".executions">
                            <cti:link href="/spring/common/eventLog/viewByCategory"
                                key="yukon.web.modules.adminSetup.maintenance.executionsLink">
                                <cti:param name="categories" value="systemAdmin.maintenance.rphDuplicateDeletion" />
                                <cti:param name="startDate" value="${startDate}" />
                                <cti:param name="stopDate" value="${stopDate}" />
                            </cti:link>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:formElementContainer>
                <cti:button key="update" type="submit"/>
                <cti:url value="toggleJobEnabled" var="toggleJobEnabled">
                    <cti:param name="jobId" value="${job.id}"/>
                </cti:url>
                <c:if test="${job.disabled}">
                    <cti:button key="enable" href="${toggleJobEnabled}"/>
                </c:if>
                <c:if test="${!job.disabled}">
                    <cti:button key="disable" href="${toggleJobEnabled}"/>
                </c:if>
                <cti:button key="cancel" href="view"/>
            </form>
        </cti:dataGridCell>
    </cti:dataGrid>

</cti:standardPage>