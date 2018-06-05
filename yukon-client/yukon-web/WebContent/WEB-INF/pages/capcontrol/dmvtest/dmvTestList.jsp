<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="capcontrol" page="dmvTest">
    <%@ include file="/capcontrol/capcontrolHeader.jspf" %>

    <c:choose>
        <c:when test="${fn:length(dmvTestList) == 0}">
            <span class="empty-list"><i:inline key=".dmvTest.noDmvTest"/></span>
        </c:when>
        <c:otherwise>
            <table class="compact-results-table" id="dmvTest-table">
                <thead>
                    <tr>
                        <th><i:inline key=".name"/></th>
                        <th><i:inline key=".dataArchivingInterval"/></th>
                        <th><i:inline key=".intervalDataGatheringDuration"/></th>
                        <th><i:inline key=".stepSize"/></th>
                        <th><i:inline key=".commSuccPercentage"/></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="dmvTest" items="${dmvTestList}">
                        <tr data-strategy-id="${dmvTest.dmvTestId}">
                            <td>
                                <cti:url var="viewUrl" value="/capcontrol/dmvTest/${dmvTest.dmvTestId}" />
                                <a href="${viewUrl}">${fn:escapeXml(dmvTest.name)}</a>
                            </td>
                            <td>
                                <c:out value="${dmvTest.dataArchivingInterval} "/> 
                                <i:inline key="yukon.common.units.SECONDS"/>                                                        
                            </td>
                            <td>
                                <c:out value="${dmvTest.intervalDataGatheringDuration} " />
                                <i:inline key="yukon.common.units.MINUTES"/>                                                        
                            </td>                            
                            <td>
                                <c:out value="${dmvTest.stepSize} " />
                                <i:inline key="yukon.common.units.VOLTS"/>                                                        
                            </td>
                            <td>
                                ${dmvTest.commSuccPercentage}
                                <i:inline key="yukon.common.units.PERCENT"/>                                                        
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</cti:standardPage>