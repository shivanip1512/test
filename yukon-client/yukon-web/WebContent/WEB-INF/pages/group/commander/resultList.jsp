<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="groupCommand.results">

    <c:if test="${empty resultList}">
        <p><i:inline key="yukon.common.search.noResultsFound"/></p>
    </c:if>
    <c:if test="${not empty resultList}">
        <table class="compact-results-table">
            <cti:msgScope paths=".tableHeader">
                <thead>
                    <tr>
                        <th><i:inline key=".command"/></th>
                        <th><i:inline key=".devices"/></th>
                        <th><i:inline key=".resultCount"/></th>
                        <th><i:inline key=".detail"/></th>
                        <th><i:inline key=".status"/></th>
                        <th><i:inline key=".startTime"/></th>
                    </tr>
                </thead>
            </cti:msgScope>
            <tfoot></tfoot>
            <tbody>
                <c:forEach items="${resultList}" var="result">
                
                    <cti:url var="resultDetailUrl" value="/group/commander/resultDetail">
                        <cti:param name="resultKey" value="${result.key}" />
                    </cti:url>
                
                    <tr>
                        <td>${(result.command)}</td>
                        <td><cti:msg key="${result.deviceCollection.description}"/></td>
                        <td title="<cti:msg2 key=".tableTooltip"/>">
                            <span class="success"><cti:dataUpdaterValue type="COMMANDER" identifier="${result.key}/SUCCESS_COUNT"/></span> /
                            <span class="error"><cti:dataUpdaterValue type="COMMANDER" identifier="${result.key}/FAILURE_COUNT"/></span>
                        </td>
                        <td><a href="${resultDetailUrl}"><i:inline key="yukon.common.view"/></a></td>
                        <td>
                            <div id="statusDiv_${result.key}">
                                <cti:classUpdater type="COMMANDER" identifier="${result.key}/STATUS_CLASS">
                                    <cti:dataUpdaterValue type="COMMANDER" identifier="${result.key}/STATUS_TEXT"/>
                                </cti:classUpdater>
                            </div>
                        </td>

                        <td><span><cti:formatDate type="DATEHM" value="${result.startTime}"/></span></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>

</cti:standardPage>