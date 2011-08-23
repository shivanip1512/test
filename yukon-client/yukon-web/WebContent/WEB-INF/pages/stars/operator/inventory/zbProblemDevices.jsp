<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="operator" page="zbProblemDevices">

    <div class="tar padBottom">
        <cti:button nameKey="csv" renderMode="labeledImage" href="/spring/stars/operator/inventory/zbProblemDevices/csv"/>
    </div>
    
    <br>
    
    <table class="resultsTable" style="width:100%;">
        <tr>
            <th><i:inline key=".serialNumber"/></th>
            <th><i:inline key=".hardwareType"/></th>
            <th><i:inline key=".label"/></th>
            <th><i:inline key=".state"/></th>
            <th><i:inline key=".timestamp"/></th>
        </tr>
        
        <c:forEach var="device" items="${devices}">
            <tr>
                <cti:url value="/spring/stars/operator/hardware/view" var="hardwareUrl">
                    <cti:param name="inventoryId" value="${device.first.identifier.inventoryId}"/>
                    <cti:param name="accountId" value="${device.first.accountId}"/>
                    <cti:param name="energyCompanyId" value="${device.first.energyCompanyId}"/>
                </cti:url>
                <td><a href="${hardwareUrl}"><spring:escapeBody htmlEscape="true">${device.first.serialNumber}</spring:escapeBody></a></td>
                <td><i:inline key="${device.first.identifier.hardwareType}"/></td>
                <td><spring:escapeBody htmlEscape="true">${device.first.label}</spring:escapeBody></td>
                <td class="fwb" style="color:${stateColorMap[device.second.value]};"><cti:pointValueFormatter value="${device.second}" format="VALUE"/></td>
                <td><cti:pointValueFormatter value="${device.second}" format="DATE"/></td>
            </tr>
        </c:forEach>
        
    </table>

</cti:standardPage>