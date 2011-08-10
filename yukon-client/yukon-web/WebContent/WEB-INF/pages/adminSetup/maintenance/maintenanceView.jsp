<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="adminSetup" page="maintenance.VIEW">
    <cti:dataGrid cols="2" tableClasses="twoColumnLayout">
        <cti:dataGridCell>
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
                        <cti:dataUpdaterValue type="JOB" identifier="${job.id}/SCHEDULE_DESCRIPTION"/>
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
            <cti:button key="edit" href="edit"/>
        </cti:dataGridCell>
    </cti:dataGrid>

</cti:standardPage>