<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>

<cti:standardPage module="dr" page="cc.programDetail">
<cti:includeScript link="/resources/js/pages/yukon.dr.curtailment.js"/>

<cti:url var="formUrl" value="/dr/cc/programSave/${program.id}"/>
<form id="program" name="program" data-program-id="${program.id}" method="POST" action="${formUrl}">
<cti:csrfToken/>
<div class="column-24">
    <h3><i:inline key=".programDetail"/></h3>
    <div class="column one">
        <table class="name-value-table natural-width">
            <tr>
                <td class="name">
                    <i:inline key=".programType"/>
                </td>
                <td class="value">
                    ${program.programType.name}
                </td>
            </tr>
            <tr>
                <td class="name">
                    <i:inline key=".programName"/>
                </td>
                <td class="value">
                    <c:if test="${not empty nameError}">
                        <c:set var="clazz" value="error"/>
                    </c:if>
                    <input class="${clazz}" id="program-name" type="text" maxlength="255" name="programName" value="${program.name}">
                    <c:if test="${not empty nameError}">
                        <span class="error"><i:inline key="${nameError}"/></span>
                    </c:if>
                </td>
            </tr>
            <tr>
                <td class="name">
                    <i:inline key=".identifierPrefix"/>
                </td>
                <td class="value">
                    <c:if test="${not empty prefixError}">
                        <c:set var="clazz2" value="error"/>
                    </c:if>
                    <input class="${clazz2}" id="program-identifier-prefix" type="text" maxlength="32" name="programIdentifierPrefix" value="${program.identifierPrefix}">
                    <c:if test="${not empty prefixError}">
                        <span class="error"><i:inline key="${prefixError}"/></span>
                    </c:if>
                </td>
            </tr>
            <tr>
                <td class="name">
                    <i:inline key=".identifierPostfix"/>
                </td>
                <td class="value">
                    <c:if test="${not empty postfixError}">
                        <c:set var="clazz3" value="error"/>
                    </c:if>
                    <input class="${clazz3}" id="program-last-identifier" type="text" name="programLastIdentifier" value="${program.lastIdentifier}">
                    <c:if test="${not empty postfixError}">
                        <span class="error"><i:inline key="${postfixError}"/></span>
                    </c:if>
                    <div id="program-error" class="error dn"><i:inline key="yukon.web.error.isNotPositive"/></div>
                </td>
            </tr>
        </table>
        <div>
        <h3><i:inline key=".parameters"/></h3>
            <table class="name-value-table natural-width" id="program-parameters">
            <c:forEach var="parameter" items="${programParameters}" varStatus="loop">
            <c:set var="errorkey" value="${requestScope['error_'.concat(parameter.parameterKey)]}" />
                 <tr>
                    <td>${parameter.parameterKey.description}</td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty errorkey}">
                                <c:set var="clazz4" value="error"/>
                            </c:when>
                            <c:otherwise>
                                <c:set var="clazz4" value=""/>
                            </c:otherwise>
                        </c:choose>
                        <input class="${clazz4}" id="parameter-value-${loop.index}" type="text" name="${parameter.parameterKey}" value="${parameter.parameterValue}" data-parameter-id="${parameter.id}">
                        <c:if test="${not empty errorkey}">
                            <span class="error"><i:inline key="${errorkey}"/></span>
                        </c:if>
                        <div id="parameter-error-${loop.index}" class="error dn"><i:inline key="yukon.web.error.isNotPositive"/></div>
                    </td>
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
                <tbody>
                <c:forEach var="assignedProgramGroup" items="${assignedProgramGroups}">
                    <tr>
                        <td>
                            <cti:button renderMode="buttonImage" icon="icon-delete" data-group="${assignedProgramGroup.id}"/>
                            <input type="hidden" name="assignedGroup" value="${assignedProgramGroup.id}">
                            ${fn:escapeXml(assignedProgramGroup.name)}
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
                <tbody>
                <c:forEach var="unassignedGroup" items="${unassignedProgramGroups}">
                    <tr>
                        <td>
                            <cti:button renderMode="buttonImage" icon="icon-add" data-group="${unassignedGroup.id}"/>
                            <input type="hidden" name="unassignedGroup" value="${unassignedGroup.id}">
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
                <tbody>
                <c:forEach var="assignedNotifGroupEntry" items="${assignedNotificationGroups}">
                    <tr>
                        <td>
                            <cti:button renderMode="buttonImage" icon="icon-delete" data-notif-group="${assignedNotifGroupEntry.notificationGroupID}"/>
                            <input type="hidden" name="assignedNotifGroup" value="${assignedNotifGroupEntry.notificationGroupID}">
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
                <tbody>
                <c:forEach var="unassignedProgramGroup" items="${unassignedNotificationGroups}">
                    <tr>
                        <td>
                            <cti:button renderMode="buttonImage" icon="icon-add" data-notif-group="${unassignedProgramGroup.notificationGroupID}"/>
                            <input type="hidden" name="unassignedNotifGroup" value="${unassignedProgramGroup.notificationGroupID}">
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
    <cti:button nameKey="save" classes="action primary" type="submit" id="program-save"/>
    <c:if test="${deletable}">
        <cti:url value="/dr/cc/programDelete/${program.id}" var="deleteUrl"/>
        <cti:button nameKey="delete" classes="delete" href="${deleteUrl}" id="delete-program"/>
        <d:confirm on="#delete-program" nameKey="confirmDelete" argument="${program.name}" />
    </c:if>
    <cti:url value="/dr/cc/programList" var="cancelUrl"/>
    <cti:button nameKey="cancel" href="${cancelUrl}"/>
</div>

</form>
</cti:standardPage>