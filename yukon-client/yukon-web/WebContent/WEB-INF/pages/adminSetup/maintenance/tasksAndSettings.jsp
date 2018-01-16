<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<cti:msgScope paths="modules.adminSetup.maintenance.dataPruning,yukon.web.modules.adminSetup.maintenance">
<tags:sectionContainer2 nameKey="dataPruning" styleClass="stacked-md">
    <div class="stacked">
        <em><i:inline key=".tasksInfo"/></em>
    </div>
    <div class="column-10-14 clearfix">
        <div class="column one">
            <table class="compact-results-table has-alerts">
                <thead>
                    <tr>
                        <th>&nbsp;</th>
                        <th><i:inline key=".tableHeader.scheduleName"/></th>
                        <th></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:set var="showDuplicateDataPruning" value="${false}"/>
                        <c:forEach var="task" items="${tasks}">
                          <cti:msg2 var="taskNameMsg" key=".${task.taskName}.title"/>
                            <c:if test="${task.taskName=='DUPLICATE_POINT_DATA_PRUNING'}">
                              <cti:checkGlobalRolesAndProperties value="DEVELOPMENT_MODE">
                                <c:set var="showDuplicateDataPruning" value="${true}"/>
                              </cti:checkGlobalRolesAndProperties>
                            </c:if>
                            <c:if test="${showDuplicateDataPruning=='true'|| 
                                          task.taskName!='DUPLICATE_POINT_DATA_PRUNING'}" >
                              <tr>
                                <cti:url var="editTaskDetailsUrl" value="/admin/maintenance/editTask" >
                                  <cti:param name="taskId" value="${task.taskId}"/>
                                  <cti:param name="taskName" value="${task.taskName}"/>
                                </cti:url>
                                <td>
                                  <a href="${editTaskDetailsUrl}" title="<cti:msg2 key=".edit.hoverText" 
                                     arguments="${taskNameMsg}"/>"><i class="icon icon-script"></i></a>
                                </td>
                                <td>
                                  <a href="${editTaskDetailsUrl}" title="<cti:msg2 key=".edit.hoverText" 
                                     arguments="${taskNameMsg}"/>">
                                  <i:inline key=".${task.taskName}.title"/>
                                  </a>
                                </td>
                                <td class="fr">
                                  <tags:switch checked="${not task.disabled}" name="toggle" data-task-id="${task.taskId}"
                                               classes="js-toggleDataPruningJobEnabled toggle-sm"/>
                                </td>
                              </tr>
                            </c:if>
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
</tags:sectionContainer2>
</cti:msgScope>