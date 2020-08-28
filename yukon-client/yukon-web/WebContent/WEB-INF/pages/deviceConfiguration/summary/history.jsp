<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:msgScope paths="modules.tools.configs.summary">
    <c:choose>
        <c:when test="${details.size() > 0}">
            <table id="history-table" class="compact-results-table" data-sortable>
                <thead>
                    <tr>
                        <th data-sorted="false">
                            <i:inline key=".viewHistory.action" /><i class="icon icon=blank"/>    
                        </th>
                        <th data-sorted="false">
                            <i:inline key=".viewHistory.actionStatus" /><i class="icon icon=blank"/>    
                        </th>
                        <th data-sorted="true" data-sorted-direction="descending">
                            <i:inline key=".viewHistory.start" /><i class="icon icon=blank"/>    
                        </th>
                        <th data-sorted="false">
                            <i:inline key=".viewHistory.end" /><i class="icon icon=blank"/>    
                        </th>
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
</cti:msgScope>

    