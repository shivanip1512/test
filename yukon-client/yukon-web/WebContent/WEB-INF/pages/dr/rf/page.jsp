<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.dr.rf.details">
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
    <tags:pagingResultsControls result="${result}" baseUrl="/dr/rf/details/unknown/${test}/page"/>
</cti:msgScope>