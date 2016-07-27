<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="recentImports">

    <cti:checkAccountEnergyCompanyOperator showError="true">
        <cti:verifyRolesAndProperties value="OPERATOR_IMPORT_CUSTOMER_ACCOUNT"/>
        
        <div class="column-12-12">
            <div class="column one">
                <tags:sectionContainer2 nameKey="completed">
                    <c:choose>
                        <c:when test="${fn:length(completed) > 0}">
                            <table class="compact-results-table dashed">
                                <thead>
                                    <tr>
                                        <th><i:inline key=".files"/></th>
                                        <th><i:inline key=".start"/></th>
                                        <th><i:inline key=".duration"/></th>
                                    </tr>
                                </thead>
                                <tfoot></tfoot>
                                <tbody>
                                    <c:forEach items="${completed}" var="result">
                                        <tr>
                                            <td>
                                                <a href="<cti:url value="/stars/operator/account/imports/${result.resultId}"/>">${fn:escapeXml(result.fileNames)}</a>
                                            </td>
                                            <td><cti:formatDate value="${result.startTime}" type="DATEHM"/></td>
                                            <td><cti:formatDuration type="DHMS_REDUCED" value="${result.duration}"/></td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:when>
                        <c:otherwise><span class="empty-list"><i:inline key=".noCompleted"/></span></c:otherwise>
                    </c:choose>
                </tags:sectionContainer2>
            </div>
            <div class="column two nogutter">
                <tags:sectionContainer2 nameKey="pending">
                    <c:choose>
                        <c:when test="${fn:length(pending) > 0}">
                            <table class="compact-results-table dashed">
                                <thead>
                                    <tr>
                                        <th><i:inline key=".files"/></th>
                                        <th><i:inline key=".start"/></th>
                                        <th></th>
                                    </tr>
                                </thead>
                                <tfoot></tfoot>
                                <tbody>
                                    <c:forEach items="${pending}" var="result">
                                        <tr>
                                            <td>
                                                <a href="<cti:url value="/stars/operator/account/imports/${result.resultId}"/>">${fn:escapeXml(result.fileNames)}</a>
                                            </td>
                                            <td><cti:formatDate value="${result.startTime}" type="DATEHM"/></td>
                                            <td><tags:updateableProgressBar totalCount="${result.totalCount}" countKey="ACCOUNT_IMPORT/${result.resultId}/COMPLETED_COUNT" hideCount="true"/></td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:when>
                        <c:otherwise><span class="empty-list"><i:inline key=".noPending"/></span></c:otherwise>
                    </c:choose>
                </tags:sectionContainer2>
            </div>
        </div>
        
    </cti:checkAccountEnergyCompanyOperator>
</cti:standardPage>