<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<cti:standardPage module="amr" page="dataCollection.detail">

    <table class="compact-results-table row-highlighting has-actions">
        <th>Device Name</th>               
        <th>Meter/Serial Number</th>                
        <th>Device Type</th>
        <th>Address</th>
        <th>Route/Gateway</th>
        <th>Recent Reading</th>                
        <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
        <c:forEach var="device" items="${detail}">
            <tr>
                <td><cti:paoDetailUrl yukonPao="${device.paoIdentifier}">${device.deviceName}</cti:paoDetailUrl></td>
                <td>${device.meterSerialNumber}</td>
                <td>${device.paoIdentifier.paoType.paoTypeName}</td>
                <td>${device.address}</td>
                <td>${device.route}</td>
                <td>
                    <cti:uniqueIdentifier var="popupId" prefix="historical-readings-"/>
                    <cti:url var="historyUrl" value="/meter/historicalReadings/view">
                        <cti:param name="pointId" value="${device.value.id}"/>
                        <cti:param name="deviceId" value="${device.paoIdentifier.paoId}"/>
                    </cti:url>
                    <a href="javascript:void(0);" data-popup="#${popupId}" class="${pageScope.classes}">
                        <cti:pointValueFormatter format="VALUE_UNIT" value="${device.value}" />
                        &nbsp;<cti:formatDate type="BOTH" value="${device.value.pointDataTimeStamp}"/>
                    </a>
                    <div id="${popupId}" data-width="500" data-height="400" data-url="${historyUrl}" class="dn"></div>
                </td>
                <td></td>
            </tr>
        </c:forEach>
    
    </table>
</cti:standardPage>