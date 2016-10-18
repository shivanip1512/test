<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<c:set var="pageName" value="capbankLocations"/>
<c:if test="${orphan}">
    <c:set var="pageName" value="capbankLocations.orphan"/>
</c:if>
<cti:standardPage module="capcontrol" page="${pageName}">
<%@ include file="/capcontrol/capcontrolHeader.jspf"%>

<jsp:setProperty name="CtiNavObject" property="moduleExitPage" value=""/>

    <table class="results-table drivingDirections">
        <thead>
            <tr id="header">
                <th><i:inline key=".capbankName"/></th>
                <th><i:inline key=".cbcSN"/></th>
                <th><i:inline key=".address"/></th>
                <th><i:inline key=".drivingDirections"/></th>
            </tr>
        </thead>

        <tbody id="tableBody">
            <c:forEach var="bank" items="${capBankList}" varStatus="i">
                <tr>
                    <td>${fn:escapeXml(bank.name)}</td>
                    <td>${fn:escapeXml(bank.serialNumber)}</td>
                    <td>${fn:escapeXml(bank.address)}</td>
                    <td>${fn:escapeXml(bank.directions)}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</cti:standardPage>