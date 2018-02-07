<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<cti:standardPage module="adminSetup" page="maintenance.dataPruning.EDIT">
    <form:form action="updateTask" commandName="maintenanceEditorBean" method="post">
        <cti:csrfToken/>
        <form:hidden path="taskDetails.taskType" />
        <tags:sectionContainer2 nameKey="${maintenanceEditorBean.taskDetails.taskType}">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".status">
                    <c:if test="${!maintenanceEditorBean.taskDetails.enabled}">
                        <span class="fwb error"><i:inline key=".status.disabled" /> </span>
                    </c:if>
                    <c:if test="${maintenanceEditorBean.taskDetails.enabled}">
                        <span class="fwb success"><i:inline key=".status.enabled" /> </span>
                    </c:if>
                </tags:nameValue2>
                <c:forEach items="${maintenanceEditorBean.settings}" var="setting" varStatus="status" begin="0">
                    <c:if test = "${setting.attribute.displayable}">
                        <c:set var="indexValue" value="${status.index}" />
                        <tags:nameValue2 nameKey=".maintenance.${setting.attribute}">
                            <tags:simpleInputType input="${setting.attribute.type}" path="settings[${indexValue}].attributeValue"/>
                            <form:hidden path="settings[${indexValue}].attribute" />
                         </tags:nameValue2>
                    </c:if>
                </c:forEach>
            </tags:nameValueContainer2>
        </tags:sectionContainer2>
        <cti:button nameKey="update" type="submit" classes="primary action" busy="true"/>
        <cti:url value="toggleDataPruningJobEnabled" var="toggleDataPruningJobEnabled">
            <cti:param name="taskType" value="${maintenanceEditorBean.taskDetails.taskType}" />
        </cti:url>
        <c:if test="${!maintenanceEditorBean.taskDetails.enabled}">
            <cti:button nameKey="enable" href="${toggleDataPruningJobEnabled}" />
        </c:if>
        <c:if test="${maintenanceEditorBean.taskDetails.enabled}">
            <cti:button nameKey="disable" href="${toggleDataPruningJobEnabled}" />
        </c:if>
        <cti:button nameKey="cancel" href="view" />
    </form:form>
</cti:standardPage>