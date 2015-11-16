<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage page="groupMeterRead.results" module="tools">

    <c:if test="${empty resultWrappers}">
        <p><i:inline key="yukon.common.search.noResultsFound"/></p>
    </c:if>
    <c:if test="${not empty resultWrappers}">
        <table class="compact-results-table">
            <cti:msgScope paths=".tableHeader">
            <thead>
                <tr>
                    <th><i:inline key=".attributes"/></th>
                    <th><i:inline key=".devices"/></th>
                    <th class="tar"><i:inline key=".successCount"/></th>
                    <th class="tar"><i:inline key=".failureCount"/></th>
                    <th class="tar"><i:inline key=".unsupportedCount"/></th>
                    <th><i:inline key=".detail"/></th>
                    <th><i:inline key=".status"/></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            </cti:msgScope>
            <tbody>
                <c:forEach var="resultWrapper" items="${resultWrappers}">
                    <c:set var="resultKey" value="${resultWrapper.key.result.key}"/>
                    <cti:url var="resultDetailUrl" value="/group/groupMeterRead/resultDetail">
                        <cti:param name="resultKey" value="${resultKey}" />
                    </cti:url>
                    <tr>
                        <td>${resultWrapper.value}</td>
                        <td><cti:msg key="${resultWrapper.key.result.deviceCollection.description}"/></td>
                        <td class="tar"><cti:dataUpdaterValue type="GROUP_METER_READ" identifier="${resultKey}/SUCCESS_COUNT"/></td>
                        <td class="tar"><cti:dataUpdaterValue type="GROUP_METER_READ" identifier="${resultKey}/FAILURE_COUNT"/></td>
                        <td class="tar"><cti:dataUpdaterValue type="GROUP_METER_READ" identifier="${resultKey}/UNSUPPORTED_COUNT"/></td>
                        <td><a href="${resultDetailUrl}"><i:inline key="yukon.common.view"/></a></td>
                        <td>
                            <div id="statusDiv_${resultKey}">
                                <cti:classUpdater type="GROUP_METER_READ" identifier="${resultKey}/STATUS_CLASS">
                                    <cti:dataUpdaterValue type="GROUP_METER_READ" identifier="${resultKey}/STATUS_TEXT"/>
                                </cti:classUpdater>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>
    
</cti:standardPage>