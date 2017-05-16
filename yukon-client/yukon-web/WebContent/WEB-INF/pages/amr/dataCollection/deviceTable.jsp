<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<table class="compact-results-table row-highlighting has-actions">
    <tags:sort column="${deviceName}" />                
    <tags:sort column="${meterSerialNumber}" />                
    <tags:sort column="${deviceType}" />                
    <tags:sort column="${address}" />                
    <tags:sort column="${recentReading}" />                                
    <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
    <c:forEach var="device" items="${detail.resultList}">
        <c:set var="deviceId" value="${device.paoIdentifier.paoId}"/>
        <tr>
            <td><cti:paoDetailUrl yukonPao="${device.paoIdentifier}">${device.deviceName}</cti:paoDetailUrl></td>
            <td>${device.meterSerialNumber}</td>
            <td>${device.paoIdentifier.paoType.paoTypeName}</td>
            <td><c:if test="${device.address != 0}">${device.address}</c:if></td>
            <td>
                <cti:uniqueIdentifier var="popupId" prefix="historical-readings-"/>
                <cti:url var="historyUrl" value="/meter/historicalReadings/view">
                    <cti:param name="pointId" value="${device.value.id}"/>
                    <cti:param name="deviceId" value="${deviceId}"/>
                </cti:url>
                <a href="javascript:void(0);" data-popup="#${popupId}" class="${pageScope.classes}">
                    <cti:pointValueFormatter format="VALUE_UNIT" value="${device.value}" />
                    <cti:msg2 key=".noRecentReadingFound" var="notFound"/>
                    &nbsp;<cti:formatDate type="BOTH" value="${device.value.pointDataTimeStamp}" nullText="${notFound}"/>
                </a>
                <div id="${popupId}" data-width="500" data-height="400" data-url="${historyUrl}" class="dn"></div>
            </td>
            <td>
            <cm:dropdown icon="icon-cog">
                <cti:url var="collectionActionsUrl" value="/bulk/collectionActions">
                    <cti:param name="collectionType" value="idList"/>
                    <cti:param name="idList.ids" value="${deviceId}"/>
                </cti:url>
                <cm:dropdownOption key=".collectionActions" href="${collectionActionsUrl}" icon="icon-cog-go"/>    
                <cti:url var="mapUrl" value="/tools/map">
                    <cti:param name="collectionType" value="idList"/>
                    <cti:param name="idList.ids" value="${deviceId}"/>
                </cti:url>
                <cm:dropdownOption icon="icon-map-sat" key=".mapDevices" href="${mapUrl}"/>     
                <cti:url var="readUrl" value="/group/groupMeterRead/homeCollection">
                    <cti:param name="collectionType" value="idList"/>
                    <cti:param name="idList.ids" value="${deviceId}"/>           
                </cti:url>
                <cm:dropdownOption icon="icon-read" key=".readAttribute" href="${readUrl}"/>          
                <cti:url var="commandUrl" value="/group/commander/collectionProcessing">
                    <cti:param name="collectionType" value="idList"/>
                    <cti:param name="idList.ids" value="${deviceId}"/>               
                </cti:url>
                <cm:dropdownOption icon="icon-ping" key=".sendCommand" href="${commandUrl}"/>   
                <cti:url var="locateRouteUrl" value="/bulk/routeLocate/home">
                    <cti:param name="collectionType" value="idList"/>
                    <cti:param name="idList.ids" value="${deviceId}"/>              
                </cti:url>
                <cm:dropdownOption icon="icon-connect" key=".locateRoute" href="${locateRouteUrl}"/>   
            </cm:dropdown>
            </td>
        </tr>
    </c:forEach>
</table>
<tags:pagingResultsControls result="${detail}" adjustPageCount="true" thousands="true"/>
