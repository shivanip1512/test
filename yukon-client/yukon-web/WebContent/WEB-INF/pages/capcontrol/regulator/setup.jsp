<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="capcontrol" page="regulator.setup">
<cti:includeScript link="/resources/js/pages/yukon.da.regulator.setup.js"/>

<tags:pickerDialog type="regulatorPicker" id="mappingPicker" linkType="none"
            endEvent="yukon:da:regulator:mapping:build"
            multiSelectMode="true"/>

<div id="page-buttons">
    <cti:button nameKey="mapping.new" classes="js-new-mappings" icon="icon-page-white-excel"/>
</div>

<tags:sectionContainer2 nameKey="recentTasks" styleClass="stacked">
    <c:set var="clazz" value="${not empty tasks ? 'dn' : ''}"/>
    <div class="empty-list ${clazz}"><i:inline key=".recentTasks.none"/></div>
    <c:set var="clazz" value="${empty tasks ? 'dn' : ''}"/>
    <table class="full-width with-form-controls selectable dashed striped piped js-task-table ${clazz}">
        <thead>
            <th colspan="2"><i:inline key="yukon.common.start"/></th>
            <th><i:inline key="yukon.common.user"/></th>
            <th><i:inline key=".regulators"/></th>
            <th><i:inline key="yukon.common.failed"/></th>
            <th><i:inline key="yukon.common.successful.partial"/></th>
            <th><i:inline key="yukon.common.successful"/></th>
            <th><i:inline key="yukon.common.status"/></th>
        </thead>
        <tfoot>
            <%-- Template Row --%>
            <tr class="js-recent-task-template dn" data-task="">
                <td></td>
                <td class="js-task-start"><a class="cp"></a></td>
                <td class="js-user"></td>
                <td><span class="badge js-count"></span></td>
                <td><span class="badge badge-error js-count-failed"></span></td>
                <td><span class="badge badge-warning js-count-partial"></span></td>
                <td><span class="badge badge-success js-count-success"></span></td>
                <td class="wsnw">
                    <span class="js-complete success dn"><i:inline key="yukon.common.complete"/></span>
                    <div class="js-progress-bar progress-bar-container progress-sm vam dn">
                        <div class="progress">
                            <div class="progress-bar progress-bar-info progress-bar-striped active"></div>
                        </div>
                    </div>
                    <span class="js-progress-text percent-value dn"></span>
                    <cti:button renderMode="buttonImage" icon="icon-cross" classes="js-delete show-on-hover fr M0"/>
                </td>
            </tr>
        </tfoot>
        <tbody>
            <c:forEach var="task" items="${tasks}">
                <tr data-task="${task.taskId}">
                    <td></td>
                    <td class="js-task-start"><a class="cp"><cti:formatDate value="${task.start}" type="BOTH"/></a></td>
                    <td class="js-user">${fn:escapeXml(task.userContext.yukonUser)}</td>
                    <td><span class="badge js-count">${fn:length(task.regulators)}</span></td>
                    <td><span class="badge badge-error js-count-failed">${task.failedCount}</span></td>
                    <td><span class="badge badge-warning js-count-partial">${task.partialSuccessCount}</span></td>
                    <td><span class="badge badge-success js-count-success">${task.successCount}</span></td>
                    <td class="wsnw">
                        <c:set var="clazz" value="${!task.complete ? 'dn' : ''}"/>
                        <span class="js-complete success ${clazz}">
                            <i:inline key="yukon.common.complete"/>
                        </span>
                        <c:set var="clazz" value="${task.complete ? 'dn' : ''}"/>
                        <div class="js-progress-bar progress-bar-container progress-sm vam ${clazz}">
                            <div class="progress">
                                <div class="progress-bar progress-bar-info progress-bar-striped active"
                                style="width: ${task.progress}"></div>
                            </div>
                        </div>
                        <span class="js-progress-text percent-value ${clazz}">${task.progress}</span>
                        <cti:button renderMode="buttonImage" icon="icon-cross" classes="js-delete show-on-hover fr M0"/>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <div class="action-area">
        <tags:pickerDialog type="regulatorPicker" id="regulatorPicker" linkType="none"
            endEvent="yukon:da:regulator:mapping:start"
            multiSelectMode="true"/>
        <cti:button nameKey="start" classes="action primary js-new-task"/>
    </div>
</tags:sectionContainer2>

<div class="js-mapping-results-container"></div>

</cti:standardPage>