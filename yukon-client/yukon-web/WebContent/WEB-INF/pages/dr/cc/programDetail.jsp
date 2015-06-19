<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="dr" page="cc.programDetail">
<cti:includeScript link="/resources/js/pages/yukon.curtailment.js"/>

<form id="program" name="program" data-program-id="${program.id}" method="POST" action="<cti:url value="/dr/cc/programSave/${program.id}"/>">
<cti:csrfToken/>
<div class="column-24">
    <h3><i:inline key=".programDetail"/></h3>
    <div class="column one">
        <table class="name-value-table natural-width">
            <tr>
                <td class="name"><i:inline key=".programType"/></td>
                <td class="value">${program.programType.name}</td>
            </tr>
            <tr>
                <td class="name"><i:inline key=".programName"/></td>
                <td class="value"><input id="program-name" type="text" name="programName" value="${program.name}" /></td>
            </tr>
            <tr>
                <td class="name"><i:inline key=".identifierPrefix"/></td>
                <td class="value"><input id="program-identifier-prefix" type="text" name="programIdentifierPrefix" value="${program.identifierPrefix}" /></td>
            </tr>
            <tr>
                <td class="name"><i:inline key=".identifierPostfix"/></td>
                <td class="value"><input id="program-last-identifier" type="text" name="programLastIdentifier" value="${program.lastIdentifier}" /></td>
            </tr>
        </table>
        <div>
        <h3><i:inline key=".parameters"/></h3>
            <table class="name-value-table natural-width" id="program-parameters">
            <c:forEach var="parameter" items="${programParameters}" varStatus="loop">
                <tr>
                    <td>${parameter.parameterKey.description}</td>
                    <td><input id="parameter-value-${loop.index}" type="text" name="${parameter.parameterKey}" value="${parameter.parameterValue}" data-parameter-id="${parameter.id}" /></td>
                </tr>
            </c:forEach>
            </table>
        </div>
    </div>
</div>
<div class="column-12-12">
    <div class="column one">
        <div>
            <h3><i:inline key=".assignedGroups"/></h3>
            <table class="compact-results-table" id="assigned-groups">
                <thead>
                    <tr>
                        <td></td>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                <c:forEach var="assignedProgramGroup" items="${assignedProgramGroups}">
                    <tr>
                        <td>
                            <cti:button renderMode="buttonImage" icon="icon-delete" data-group="${assignedProgramGroup.group.id}"/>
                            <input type="hidden" name="${assignedProgramGroup.group.name}" value="${assignedProgramGroup.group.id}" />
                            ${fn:escapeXml(assignedProgramGroup.group.name)}
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
    <div class="column two nogutter">
        <div>
            <h3><i:inline key=".availableGroups"/></h3>
            <table class="compact-results-table" id="unassigned-groups">
                <thead>
                    <tr>
                        <td></td>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                <c:forEach var="unassignedGroup" items="${unassignedProgramGroups}">
                    <tr>
                        <td>
                            <cti:button renderMode="buttonImage" icon="icon-add" data-group="${unassignedGroup.id}"/>
                            <input type="hidden" name="${unassignedProgramGroup.group.name}" value="${unassignedProgramGroup.group.id}" />
                        ${fn:escapeXml(unassignedGroup.name)}
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>
<div class="column-12-12">
    <div class="column one">
        <div>
        <h3><i:inline key=".assignedNotifGroups"/></h3>
            <table class="compact-results-table" id="assigned-notification-groups">
                <thead>
                    <tr>
                        <td></td>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                <c:forEach var="assignedNotifGroupEntry" items="${assignedNotificationGroups}">
                    <tr>
                        <td>
                            <cti:button renderMode="buttonImage" icon="icon-delete" data-notif-group="${assignedNotifGroupEntry.notificationGroupID}"/>
                            <input type="hidden" name="${assignedNotifGroupEntry.notificationGroupName}" value="${assignedNotifGroupEntry.notificationGroupID}" />
                            ${fn:escapeXml(assignedNotifGroupEntry.notificationGroupName)}
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
    <div class="column two nogutter">
        <div>
        <h3><i:inline key=".availableNotifGroups"/></h3>
            <table class="compact-results-table" id="unassigned-notification-groups">
                <thead>
                    <tr>
                        <td></td>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                <c:forEach var="unassignedProgramGroup" items="${unassignedNotificationGroups}">
                    <tr>
                        <td>
                            <cti:button renderMode="buttonImage" icon="icon-add" data-notif-group="${unassignedProgramGroup.notificationGroupID}"/>
                            <input type="hidden" name="${unassignedProgramGroup.notificationGroupName}" value="${unassignedProgramGroup.notificationGroupID}" />
                            ${fn:escapeXml(unassignedProgramGroup.notificationGroupName)}
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>
    <div class="page-action-area">
        <cti:url value="/dr/cc/programSave/${program.id}" var="saveUrl"/>
        <cti:button nameKey="save" classes="action primary" type="submit" id="program-save" href="${saveUrl}"/>
        <c:if test="${deletable}">
            <cti:url value="/dr/cc/programDelete/${program.id}" var="deleteUrl"/>
            <cti:button nameKey="delete" classes="delete" href="${deleteUrl}" id="delete-program"/>
        </c:if>
        <cti:url value="/dr/cc/programList" var="cancelUrl"/>
        <cti:button nameKey="cancel" href="${cancelUrl}"/>
    </div>

</form>
</cti:standardPage>