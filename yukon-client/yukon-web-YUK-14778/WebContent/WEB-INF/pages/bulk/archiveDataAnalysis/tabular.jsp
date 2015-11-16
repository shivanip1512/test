<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="tools" page="bulk.analysis.tabular">
    
    <div style="max-width: 100%;overflow-x: scroll;">
        <table class="results-table tabular">
            <thead>
                <th></th>
                <c:forEach var="dateTime" items="${dateTimeList}">
                    <th>
                        <cti:formatDate type="DATEHM" value="${dateTime}"/>
                    </th>
                </c:forEach>
            </thead>
            <tfoot></tfoot>
            <tbody>
            <c:forEach var="devicePointValues" items="${devicePointValuesList}">
                <tr>
                    <td class="tabular">${fn:escapeXml(devicePointValues.displayablePao.name)}</td>
                    <c:forEach var="pointValue" items="${devicePointValues.pointValues}">
                        <td>
                            <c:choose>
                                <c:when test="${empty pointValue}">
                                    <i:inline key="yukon.common.dashesTwo"/>
                                </c:when>
                                <c:otherwise>
                                    <cti:pointValueFormatter value="${pointValue}" format="SHORT"/>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </c:forEach>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</cti:standardPage>