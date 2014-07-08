<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.dr.rf.details">
    <table class="compact-results-table">
        <thead>
            <tr>
                <th><i:inline key="yukon.common.deviceName"/></th>
                <th><i:inline key="yukon.common.deviceType"/></th>
                <th><i:inline key=".accountNo"/></th>
                <c:if test="${unknown}"><th><i:inline key=".unknownType"/></th></c:if>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach items="${result.resultList}" var="row">
                <tr>
                    <td>
                        <cti:url value="/stars/operator/inventory/view" var="url">
                            <cti:param name="inventoryId" value="${row.identifier.inventoryId}"/>
                        </cti:url>
                        <a href="${url}"><cti:deviceName deviceId="${row.deviceId}"/></a>
                    </td>
                    <td><i:inline key="${row.identifier.hardwareType}"/></td>
                    <td>
                        <c:if test="${row.accountId > 0}">
                            <cti:url value="/stars/operator/account/view" var="url">
                                <cti:param name="accountId" value="${row.accountId}"/>
                            </cti:url>
                            <a href="${url}">${fn:escapeXml(row.accountNo)}</a>
                        </c:if>
                        <c:if test="${row.accountId == 0}">${fn:escapeXml(row.accountNo)}</c:if>
                    </td>
                    <c:if test="${unknown}">
                        <td><i:inline key="${unknowns[row.deviceId].unknownStatus}"/></td>
                    </c:if>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <tags:pagingResultsControls result="${result}"/>
</cti:msgScope>