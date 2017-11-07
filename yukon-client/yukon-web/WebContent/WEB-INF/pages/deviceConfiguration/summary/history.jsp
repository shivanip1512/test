<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:msgScope paths="modules.tools.configs.summary">
<div class="scroll-md">
    <c:choose>
        <c:when test="${details.size() > 0}">
            <table class="compact-results-table">
                <thead>
                    <tr>
                        <th><i:inline key=".viewHistory.action" /></th>
                        <th><i:inline key=".viewHistory.actionStatus" /></th>
                        <th><i:inline key=".viewHistory.start" /></th>
                        <th><i:inline key=".viewHistory.end" /></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:forEach var="detail" items="${details}">
                        <tr>
                            <td><i:inline key=".actionType.${detail.action}" /></td>
                            <td><i:inline key=".statusType.${detail.status}" /></td>
                            <td><cti:formatDate type="BOTH" value="${detail.actionStart}" /></td>
                            <td><cti:formatDate type="BOTH" value="${detail.actionEnd}" /></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:when>
        <c:otherwise>
            <tr>
                <td colspan="4"><span class="empty-list"><i:inline key=".viewHistory.none" /></span></td>
            </tr>
        </c:otherwise>
    </c:choose>
</div>
</cti:msgScope>
    