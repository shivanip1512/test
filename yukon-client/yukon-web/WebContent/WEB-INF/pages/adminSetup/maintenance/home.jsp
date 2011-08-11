<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>

<cti:standardPage module="adminSetup" page="maintenance.${mode}">
    <tags:setFormEditMode mode="${mode}"/>
    <cti:dataGrid cols="2" tableClasses="twoColumnLayout">
        <cti:dataGridCell>
            <form action="update" method="post">
                <c:set var="cronUniqueId" value="cronUniqueId_${job.id}"/>
                <input type="hidden" name="jobId" value="${job.id}">
                <input type="hidden" name="cronUniqueId" value="${cronUniqueId}">
                <tags:formElementContainer nameKey="rphDuplicateDeletion">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".status" rowClass="fwb">
                            <c:if test="${job.disabled}">
                                <span class="errorMessage"><i:inline key=".status.disabled"/></span>
                            </c:if>
                            <c:if test="${!job.disabled}">
                                <span class="successMessage"><i:inline key=".status.enabled"/></span>
                            </c:if>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".runSchedule">
                            <cti:displayForPageEditModes modes="VIEW">
                                <cti:dataUpdaterValue type="JOB" identifier="${job.id}/SCHEDULE_DESCRIPTION"/>
                            </cti:displayForPageEditModes>
                            <cti:displayForPageEditModes modes="EDIT">
                                <tags:cronExpressionData state="${expressionTagState}"
                                    id="${cronUniqueId}" allowTypeChange="false" />
                            </cti:displayForPageEditModes>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".nextRun">
                            <amr:scheduledGroupRequestExecutionJobNextRunDate jobId="${job.id}"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".executions">
                            <cti:link href="/spring/common/eventLog/viewByCategory"
                                key="yukon.web.modules.adminSetup.maintenance.executionsLink">
                                <cti:param name="categories" value="system.maintenance.rphDeleteDuplicates" />
                            </cti:link>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:formElementContainer>
                <cti:displayForPageEditModes modes="VIEW">
                    <cti:button key="edit" href="edit"/>
                </cti:displayForPageEditModes>
                <cti:displayForPageEditModes modes="EDIT">
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
                </cti:displayForPageEditModes>
            </form>
        </cti:dataGridCell>
    </cti:dataGrid>
</cti:standardPage>