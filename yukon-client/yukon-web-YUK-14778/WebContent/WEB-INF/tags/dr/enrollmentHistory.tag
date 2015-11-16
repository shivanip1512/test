<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ attribute name="hardwareConfigActions" required="true" type="java.util.List" %>
<%@ attribute name="isHistoryPage" type="java.lang.Boolean" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:msgScope paths="modules.operator.enrollmentHistory">

    <tags:sectionContainer2 nameKey="history">
        <c:choose>
            <c:when test="${empty hardwareConfigActions}">
                <span class="empty-list"><i:inline key=".noHistory"/></span>
            </c:when>
            <c:otherwise>
                <table class="compact-results-table dashed">
                    <thead>
                        <tr>
                            <th><i:inline key=".date"/></th>
                            <th><i:inline key=".action"/></th>
                            <th><i:inline key=".program"/></th>
                            <cti:checkRolesAndProperties value="!TRACK_HARDWARE_ADDRESSING">
                                <th><i:inline key=".group"/></th>
                            </cti:checkRolesAndProperties>
                            <th><i:inline key=".hardware"/></th>
                            <th><i:inline key=".relay"/></th>
                        </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:set var="maxActions" value="5"/>
                        <c:if test="${isHistoryPage}">
                            <c:set var="maxActions" value="${fn:length(hardwareConfigActions) - 1}"/>
                        </c:if>
                        <c:forEach var="action" items="${hardwareConfigActions}" end="${maxActions}">
                            <tr>
                                <td><cti:formatDate value="${action.date}" type="BOTH"/></td>
                                <td><cti:msg key="${action.actionType}"/></td>
                                <td>${fn:escapeXml(action.programName)}</td>
                                <cti:checkRolesAndProperties value="!TRACK_HARDWARE_ADDRESSING">
                                    <td>${fn:escapeXml(action.loadGroupName)}</td>
                                </cti:checkRolesAndProperties>
                                <c:if test="${empty action.hardwareSerialNumber}">
                                    <td><i:inline key=".deviceRemoved"/></td>
                                    <td>&nbsp;</td>
                                </c:if>
                                <c:if test="${!empty action.hardwareSerialNumber}">
                                    <td>${fn:escapeXml(action.hardwareSerialNumber)}</td>
                                    <td>
                                        <c:if test="${action.relay != 0}">${action.relay}</c:if>
                                        <c:if test="${action.relay == 0}"><i:inline key=".noRelay"/></c:if>
                                    </td>
                                </c:if>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <c:if test="${!isHistoryPage && fn:length(hardwareConfigActions) > 6}">
                    <cti:url var="showCompleteHistoryUrl" value="/stars/operator/enrollment/history">
                        <cti:param name="accountId" value="${accountId}"/>
                    </cti:url>
                    <div class="action-area">
                        <a class="fl" href="${showCompleteHistoryUrl}"><i:inline key=".showCompleteHistory"/></a>
                    </div>
                </c:if>
            </c:otherwise>
        </c:choose>
    </tags:sectionContainer2>
</cti:msgScope>
