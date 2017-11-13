<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<cti:msgScope paths="modules.adminSetup.maintenance.dataPruning,yukon.web.modules.adminSetup.maintenance">
<tags:sectionContainer2 nameKey="dataPruning" styleClass="stacked-md">
    <div class="column-12-12 clearfix">
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
                    <c:forEach var="task" items="${tasks}">
                        <cti:msg2 var="taskNameMsg" key=".${task.taskName}.title"/>
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
                    </c:forEach>
                </tbody>
                </table>
                </div>
            <!-- Add exclusion hours section -->
            <div class="column two nogutter">
            
            </div>
</tags:sectionContainer2>
</cti:msgScope>