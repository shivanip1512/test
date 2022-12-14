<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<cti:msgScope paths="modules.adminSetup.maintenance,modules.adminSetup.maintenance.dataPruning,yukon.web.modules.adminSetup.maintenance,yukon.web.components.ajaxConfirm">
    
    <cti:msg2 var="confirmEnableTitle" key=".confirmEnable.title"/>
    <cti:msg2 var="confirmDisableTitle" key=".confirmDisable.title"/>
    
    <input id="confirmEnableText" type="hidden" value="${confirmEnableTitle}"/>
    <input id="confirmDisableText" type="hidden" value="${confirmDisableTitle}"/>
    
    <tags:sectionContainer2 nameKey="dataPruning" styleClass="stacked-md">
        <div class="stacked">
            <em><i:inline key=".tasksInfo"/></em>
        </div>
        <div class="column-10-14 clearfix">
            <div class="column one">
                <table class="compact-results-table">
                    <thead>
                        <tr>
                            <th><i:inline key=".tableHeader.scheduleName"/></th>
                            <th></th>
                        </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:forEach var="task" items="${tasks}">
                            <cti:msg2 var="taskTypeMsg" key=".${task.taskType}.title"/>
                                <tr>
                                    <cti:url var="editTaskDetailsUrl" value="/admin/maintenance/editTask" >
                                        <cti:param name="taskType" value="${task.taskType}"/>
                                    </cti:url>
                                    <td>
                                        <a href="${editTaskDetailsUrl}" title="<cti:msg2 key=".edit.hoverText" 
                                           arguments="${taskTypeMsg}"/>"><i:inline key=".${task.taskType}.title"/></a>
                                    </td>
                                    <td class="fr">
                                        <tags:switch checked="${task.enabled}" name="toggle" data-task-type="${task.taskType}"
                                                     classes="js-toggle-maintenance-task toggle-sm js-toggle-maintenance-task-${task.taskType}"
                                                     data-popup="#js-api-show-popup-${task.taskType}" />
                                        <c:choose>
                                            <c:when test="${task.taskType=='POINT_DATA_PRUNING'}">
                                                <cti:msg2 var="confirmEnableMessage" key=".POINT_DATA_PRUNING.confirmEnable.message" 
                                                          argument="${pointDataPruningDuration}" />
                                                <cti:msg2 var="confirmDisableMessage" key=".POINT_DATA_PRUNING.confirmDisable.message" 
                                                          argument="${pointDataPruningDuration}" />
                                            </c:when>
                                            <c:otherwise>
                                                <cti:msg2 var="confirmEnableMessage" key=".confirmEnable.message" argument="${taskTypeMsg}" />
                                                <cti:msg2 var="confirmDisableMessage" key=".confirmDisable.message" argument="${taskTypeMsg}" />
                                            </c:otherwise>
                                        </c:choose>
                                        <div class="dn" id="js-api-show-popup-${task.taskType}" data-width="400" 
                                             data-target=".js-toggle-maintenance-task-${task.taskType}" data-task-type="${task.taskType}"
                                             data-confirm-enable-message="${fn:escapeXml(confirmEnableMessage)}"
                                             data-confirm-disable-message="${fn:escapeXml(confirmDisableMessage)}"></div>
                                    </td>
                                </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
            <!-- Add exclusion hours section -->
            <div class="column two nogutter">
                <tags:boxContainer2 nameKey="exclusionHours" styleClass="largeContainer">
                    <cti:url var="saveUrl" value="/admin/maintenance/updateMaintenanceSettings"/>
                    <form:form action="${saveUrl}" id="settingsForm" method="post">
                        <cti:csrfToken/>
                        <tags:nameValueContainer2 naturalWidth="false">
                            <c:forEach items="${mappedPropertiesHelper.mappableProperties}" var="setting" 
                                       varStatus="loopStatus">
                                <tags:nameValue2 nameKey="yukon.common.setting.${setting.extra.type}">
                                    <tags:simpleInputType id="${setting.extra.type}" input="${setting.valueType}"
                                                          path="${setting.path}" />
                                    <tags:hidden path="comments[${setting.extra.type}]" /> 
                                </tags:nameValue2>
                            </c:forEach>
                        </tags:nameValueContainer2>
                        <div class="action-area">
                            <cti:button nameKey="save" name="save" type="submit" classes="primary action"/>
                        </div>
                    </form:form>
                </tags:boxContainer2>
            </div>
        </div>
    </tags:sectionContainer2>
</cti:msgScope>