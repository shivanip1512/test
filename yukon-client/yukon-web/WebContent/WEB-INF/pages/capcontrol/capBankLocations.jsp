<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="capTags" tagdir="/WEB-INF/tags/capcontrol"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="capcontrol" page="capbankLocations">
<%@ include file="/capcontrol/capcontrolHeader.jspf"%>

<jsp:setProperty name="CtiNavObject" property="moduleExitPage" value=""/>
<cti:includeScript link="/JavaScript/tableCreation.js" />

    <div class="padBottom">
        <table class="resultsTable drivingDirections">
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
                    <tr class="<tags:alternateRow odd="tableCell" even="altTableCell"/>">
                        <td><spring:escapeBody htmlEscape="true">${bank.name}</spring:escapeBody></td>
                        <td><spring:escapeBody htmlEscape="true">${bank.serialNumber}</spring:escapeBody></td>
                        <td><spring:escapeBody htmlEscape="true">${bank.address}</spring:escapeBody></td>
                        <td><spring:escapeBody htmlEscape="true">${bank.directions}</spring:escapeBody></td>
                    </tr>
                </c:forEach>
            </tbody>
            
        </table>
    </div>
</cti:standardPage>