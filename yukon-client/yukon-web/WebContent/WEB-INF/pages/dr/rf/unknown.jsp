<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.dr.rf.details">
    
    <div class="column-12-12">
        <div class="column one">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey="yukon.common.rfPerformance.unknownStatus.ACTIVE">${unknownDevices.totalActive}</tags:nameValue2>
                <tags:nameValue2 nameKey="yukon.common.rfPerformance.unknownStatus.INACTIVE">${unknownDevices.totalInactive}</tags:nameValue2>
                <tags:nameValue2 nameKey="yukon.common.rfPerformance.unknownStatus.UNAVAILABLE">${unknownDevices.totalUnavailable}</tags:nameValue2>
                <tags:nameValue2 nameKey="yukon.common.rfPerformance.unknownStatus.UNREPORTED_NEW">${unknownDevices.totalUnreportedNew}</tags:nameValue2>
                <tags:nameValue2 nameKey="yukon.common.rfPerformance.unknownStatus.UNREPORTED_OLD">${unknownDevices.totalUnreportedOld}</tags:nameValue2>
            </tags:nameValueContainer2>
        </div>
        <div class="column two nogutter">
            <div class="f-unknown-pie flotchart" style="min-height: 100px;"></div>
        </div>
    </div>
    
    <table class="compact-results-table">
        <thead>
            <tr>
                <th><i:inline key="yukon.common.deviceName"/></th>
                <th><i:inline key="yukon.common.deviceType"/></th>
                <th><i:inline key=".unknownType"/></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach items="${result.resultList}" var="row">
                <tr>
                    <td><cti:deviceName deviceId="${row.pao.paoIdentifier.paoId}"/></td>
                    <td>${row.pao.paoIdentifier.paoType.paoTypeName}</td>
                    <td><i:inline key="${row.unknownStatus}"/></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <tags:pagingResultsControls result="${result}" baseUrl="/dr/rf/details/unknown/${test}"/>
    <div class="action-area">
        <cti:button nameKey="download" icon="icon-page-white-excel"/>
        <cti:button nameKey="actionUnknown" icon="icon-cog-go"/>
    </div>
</cti:msgScope>