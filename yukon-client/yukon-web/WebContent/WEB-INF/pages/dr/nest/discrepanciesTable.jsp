<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.dr.nest">

    <input type="hidden" id="badgeCount" value="${discrepancies.hitCount}"/>
    <c:choose>
        <c:when test="${discrepancies.hitCount > 0}">
            <table class="compact-results-table row-highlighting">
                <tr>
                    <tags:sort column="${type}" />
                    <th><i:inline key=".reason"/></th>
                    <th><i:inline key=".action"/></th>
                </tr>
                <c:forEach var="discrepancy" items="${discrepancies.resultList}">
                    <tr>
                        <td>
                            <i:inline key=".${discrepancy.type}"/>
                        </td>
                        <td>
                            <!-- Leave this in for testing for now -->
                            <cti:msg2 var="reasonValue" key=".${discrepancy.reasonKey}" argument="${discrepancy.reasonValue}" blankIfMissing="true"/>
                            <c:choose>
                                <c:when test="${empty reasonValue}">
                                    ****Key needed yukon.modules.dr.nest.${discrepancy.reasonKey}****
                                </c:when>
                                <c:otherwise>
                                    <i:inline key=".${discrepancy.reasonKey}" arguments="${discrepancy.reasonValue}"/>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <!-- Leave this in for testing for now -->
                            <cti:msg2 var="actionValue" key=".${discrepancy.actionKey}" argument="${discrepancy.actionValue}" blankIfMissing="true"/>
                            <c:choose>
                                <c:when test="${empty actionValue}">
                                    ****Key needed yukon.modules.dr.nest.${discrepancy.actionKey}****
                                </c:when>
                                <c:otherwise>
                                    <i:inline key=".${discrepancy.actionKey}" arguments="${discrepancy.actionValue}"/>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
            </table>

        </c:when>
        <c:otherwise>
            <br/>
            <span class="empty-list"><i:inline key=".noDiscrepancies"/></span>
        </c:otherwise>
    </c:choose>
    <c:set var="displayClass" value="${discrepancies.hitCount > 0 ? '' : 'dn'}"/>
    <tags:pagingResultsControls result="${discrepancies}" adjustPageCount="true" thousands="true" classes="${displayClass}"/>
    
</cti:msgScope>
