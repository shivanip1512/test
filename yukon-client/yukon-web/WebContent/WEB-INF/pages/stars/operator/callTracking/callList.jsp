<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="callList">
<cti:checkAccountEnergyCompanyOperator showError="true" >
<tags:setFormEditMode mode="${mode}"/>

    <c:choose>
        <c:when test="${fn:length(callReportsWrappers) <= 0}">
            <span class="empty-list"><i:inline key=".noCalls"/></span>
        </c:when>
        <c:otherwise>
            <table class="compact-results-table row-highlighting">
               <thead>
                    <tr>
                        <th><i:inline key=".header.callNumber"/></th>
                        <th><i:inline key=".header.dateTime"/></th>
                        <th><i:inline key=".header.type"/></th>
                        <th><i:inline key=".header.description"/></th>
                        <th><i:inline key=".header.takenBy"/></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    
                    <c:forEach var="callReportWrapper" items="${callReportsWrappers}">
                        <tr>
                            <td>
                                <%-- callNumber with edit link --%>
                                <cti:displayForPageEditModes modes="EDIT,CREATE,VIEW">
                                    <cti:url var="viewCallUrl" value="/stars/operator/callTracking/view">
                                        <cti:param name="accountId">${accountId}</cti:param>
                                        <cti:param name="callId">${callReportWrapper.callReport.callId}</cti:param>
                                    </cti:url>
                                    <a href="${viewCallUrl}">${fn:escapeXml(callReportWrapper.callReport.callNumber)}</a>
                                </cti:displayForPageEditModes>
                            </td>
                            <td><cti:formatDate value="${callReportWrapper.callReport.dateTaken}" type="BOTH"/></td>
                            <td>${callReportWrapper.type}</td>
                            <td class="description wbba">${fn:escapeXml(callReportWrapper.callReport.description)}</td>
                            <td>${fn:escapeXml(callReportWrapper.callReport.takenBy)}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
    
    <%-- create button --%>
    <cti:displayForPageEditModes modes="CREATE">
        <div class="page-action-area">
            <form id="createCallForm" action="<cti:url value="/stars/operator/callTracking/create"/>" method="get">
                <input type="hidden" name="accountId" value="${accountId}">
                <cti:button nameKey="create" icon="icon-plus-green" type="submit"/>
            </form>
        </div>
    </cti:displayForPageEditModes>
</cti:checkAccountEnergyCompanyOperator>
</cti:standardPage>