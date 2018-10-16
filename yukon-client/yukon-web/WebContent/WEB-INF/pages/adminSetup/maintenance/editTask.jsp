<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<cti:standardPage module="adminSetup" page="maintenance.dataPruning.EDIT">
    
    <d:confirm on=".js-toggle-task" nameKey="${confrimToggleTaskMsgKey}" argument="${msgArgument}"/>
    <d:confirm on=".js-update-task" nameKey="confirmUpdate" argument="${taskTypeMsg}"/>
    
    <form:form id="maintenance-task-form" action="updateTask" modelAttribute="maintenanceEditorBean" method="post">
        <cti:csrfToken/>
        <form:hidden path="taskDetails.taskType"/>
            <tags:sectionContainer2 nameKey="${maintenanceEditorBean.taskDetails.taskType}">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".status">
                    <c:if test="${!maintenanceEditorBean.taskDetails.enabled}">
                        <span class="fwb error"><i:inline key=".status.disabled" /></span>
                    </c:if>
                    <c:if test="${maintenanceEditorBean.taskDetails.enabled}">
                        <span class="fwb success"><i:inline key=".status.enabled" /></span>
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
        <br>
        <!-- Check if there are any other settings other than(hence > 1) the task status -->
        <c:if test="${not empty maintenanceEditorBean.settings && fn:length(maintenanceEditorBean.settings) > 1}">
            <cti:button nameKey="update" type="submit" classes="primary action js-update-task" data-ok-event="yukon:maintenance:update-task"/>
        </c:if>
        <c:if test="${!maintenanceEditorBean.taskDetails.enabled}">
            <cti:button nameKey="enable" classes="js-toggle-task" data-ok-event="yukon:maintenance:toggle-task"/>
        </c:if>
        <c:if test="${maintenanceEditorBean.taskDetails.enabled}">
            <cti:button nameKey="disable" classes="js-toggle-task" data-ok-event="yukon:maintenance:toggle-task"/>
        </c:if>
        <cti:button nameKey="cancel" href="view" />
    </form:form>
    
    <cti:includeScript link="/resources/js/pages/yukon.admin.maintenance.js"/>
</cti:standardPage>