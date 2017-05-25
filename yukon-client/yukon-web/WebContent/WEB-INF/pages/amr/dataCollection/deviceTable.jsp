<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<table class="compact-results-table row-highlighting has-alerts has-actions">
    <th></th>
    <tags:sort column="${deviceName}" />                
    <tags:sort column="${meterNumber}" />                
    <tags:sort column="${deviceType}" />                
    <tags:sort column="${serialNumberAddress}" />                
    <tags:sort column="${recentReading}" />                                
    <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
    <c:forEach var="device" items="${detail.resultList}">
        <c:set var="deviceId" value="${device.paoIdentifier.paoId}"/>
        <tr>
            <td style="vertical-align:middle">
                <c:set var="circleColor" value="green-background"/>
                <c:if test="${device.range eq 'EXPECTED'}">
                    <c:set var="circleColor" value="yellow-background"/>
                </c:if>
                <c:if test="${device.range eq 'OUTDATED'}">
                    <c:set var="circleColor" value="orange-background"/>
                </c:if>
                <c:if test="${device.range eq 'UNAVAILABLE'}">
                    <c:set var="circleColor" value="green-background"/>
                </c:if>
                <div class="small-circle ${circleColor}"></div>
            </td>
            <td><cti:paoDetailUrl yukonPao="${device.paoIdentifier}" newTab="true">${device.deviceName}</cti:paoDetailUrl></td>
            <td>${device.meterNumber}</td>
            <td>${device.paoIdentifier.paoType.paoTypeName}</td>
            <td>${device.addressSerialNumber}</td>
            <td>
                <c:choose>
                    <c:when test="${device.value != null}">
                        <cti:uniqueIdentifier var="popupId" prefix="historical-readings-"/>
                        <cti:url var="historyUrl" value="/meter/historicalReadings/view">
                            <cti:param name="pointId" value="${device.value.id}"/>
                            <cti:param name="deviceId" value="${deviceId}"/>
                        </cti:url>
                        <a href="javascript:void(0);" data-popup="#${popupId}" class="${pageScope.classes}">
                            <cti:pointValueFormatter format="VALUE_UNIT" value="${device.value}" />
                            &nbsp;<cti:formatDate type="BOTH" value="${device.value.pointDataTimeStamp}"/>
                        </a>
                        <div id="${popupId}" data-width="500" data-height="400" data-url="${historyUrl}" class="dn"></div>
                    </c:when>
                    <c:otherwise>
                        <cti:msg2 key="yukon.web.modules.amr.dataCollection.detail.noRecentReadingFound"/>
                    </c:otherwise>
                </c:choose>
            </td>
            <td>
            <cm:dropdown icon="icon-cog">
                <cti:url var="collectionActionsUrl" value="/bulk/collectionActions">
                    <cti:param name="collectionType" value="idList"/>
                    <cti:param name="idList.ids" value="${deviceId}"/>
                </cti:url>
                <cm:dropdownOption key=".collectionActions" href="${collectionActionsUrl}" icon="icon-cog-go" newTab="true"/>
                <c:if test="${device.paoIdentifier.paoType.isMct()}">
                    <cti:url var="locateRouteUrl" value="/bulk/routeLocate/home">
                        <cti:param name="collectionType" value="idList"/>
                        <cti:param name="idList.ids" value="${deviceId}"/>              
                    </cti:url>
                    <cm:dropdownOption icon="icon-connect" key=".locateRoute" href="${locateRouteUrl}" newTab="true"/>
                </c:if>
                <cti:url var="mapUrl" value="/tools/map">
                    <cti:param name="collectionType" value="idList"/>
                    <cti:param name="idList.ids" value="${deviceId}"/>
                </cti:url>
                <cm:dropdownOption icon="icon-map-sat" key=".mapDevices" href="${mapUrl}" newTab="true"/>     
                <cti:url var="readUrl" value="/group/groupMeterRead/homeCollection">
                    <cti:param name="collectionType" value="idList"/>
                    <cti:param name="idList.ids" value="${deviceId}"/>           
                </cti:url>
                <cm:dropdownOption icon="icon-read" key=".readAttribute" href="${readUrl}" newTab="true"/>          
                <cti:url var="commandUrl" value="/group/commander/collectionProcessing">
                    <cti:param name="collectionType" value="idList"/>
                    <cti:param name="idList.ids" value="${deviceId}"/>               
                </cti:url>
                <cm:dropdownOption icon="icon-ping" key=".sendCommand" href="${commandUrl}" newTab="true"/>   

            </cm:dropdown>
            </td>
        </tr>
    </c:forEach>
</table>
<tags:pagingResultsControls result="${detail}" adjustPageCount="true" thousands="true"/>
