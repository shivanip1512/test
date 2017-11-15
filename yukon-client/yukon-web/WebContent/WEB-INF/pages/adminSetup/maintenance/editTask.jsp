<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<cti:standardPage module="adminSetup" page="maintenance.dataPruning.EDIT">
    <form:form action="updateTask" commandName="maintenanceEditorBean" method="post">
        <cti:csrfToken/>
        <tags:sectionContainer2 nameKey="${maintenanceEditorBean.taskDetails.taskName}">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".status">
                    <c:if test="${maintenanceEditorBean.taskDetails.disabled}">
                        <span class="fwb error"><i:inline key=".status.disabled" /> </span>
                    </c:if>
                    <c:if test="${!maintenanceEditorBean.taskDetails.disabled}">
                        <span class="fwb success"><i:inline key=".status.enabled" /> </span>
                    </c:if>
                </tags:nameValue2>
                <c:forEach items="${maintenanceEditorBean.settings}" var="setting" varStatus="status" begin="0">
                    <c:set var="indexValue" value="${status.index}" />
                    <tags:nameValue2 nameKey=".maintenance.${setting.attribute}">
                        <tags:simpleInputType input="${setting.attribute.type}" path="settings[${indexValue}].attributeValue"/>
                        <form:hidden path="settings[${indexValue}].taskId" />
                        <form:hidden path="settings[${indexValue}].attribute" />
                        <form:hidden path="settings[${indexValue}].taskPropertyId" />
                    </tags:nameValue2>
                </c:forEach>
                <tags:nameValue2 nameKey=".nextRun">
                    <!-- TODO: Make changes to calculate the next run time after YUK-17337 is done -->
                    <span>11/18/2017 22:00</span>
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </tags:sectionContainer2>
        <cti:button nameKey="update" type="submit" classes="primary action" busy="true"/>
        <cti:url value="toggleDataPruningJobEnabled" var="toggleDataPruningJobEnabled">
            <cti:param name="taskId" value="${maintenanceEditorBean.taskDetails.taskId}" />
        </cti:url>
        <c:if test="${maintenanceEditorBean.taskDetails.disabled}">
            <cti:button nameKey="enable" href="${toggleDataPruningJobEnabled}" />
        </c:if>
        <c:if test="${!maintenanceEditorBean.taskDetails.disabled}">
            <cti:button nameKey="disable" href="${toggleDataPruningJobEnabled}" />
        </c:if>
        <cti:button nameKey="cancel" href="view" />
    </form:form>
</cti:standardPage>