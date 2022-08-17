<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:msgScope paths="modules.amr.dataCollection.detail">

<table class="compact-results-table row-highlighting has-alerts has-actions">
    <th></th>
    <th class="row-icon"/>
    <tags:sort column="${deviceName}" />                
    <tags:sort column="${meterNumber}" />                
    <tags:sort column="${deviceType}" />                
    <tags:sort column="${serialNumberAddress}" />
    <tags:sort column="${primaryGateway}" />                
    <tags:sort column="${recentReading}" />                                
    <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
    <c:forEach var="device" items="${detail.resultList}">
        <c:set var="deviceId" value="${device.paoIdentifier.paoId}"/>
        <tr>
            <td style="vertical-align:middle">
                <c:set var="circleColor" value="green-background"/>
                <c:if test="${device.range eq 'EXPECTED'}">
                    <c:set var="circleColor" value="blue-background"/>
                </c:if>
                <c:if test="${device.range eq 'OUTDATED'}">
                    <c:set var="circleColor" value="orange-background"/>
                </c:if>
                <c:if test="${device.range eq 'UNAVAILABLE'}">
                    <c:set var="circleColor" value="grey-background"/>
                </c:if>
                <cti:msg2 var="rangeText" key=".rangeType.${device.range}"/>
                <div class="small-circle ${circleColor}" title="${rangeText}"></div>
            </td>
            <td>
                <c:if test="${notesList.contains(device.paoIdentifier.paoId)}">
                    <cti:msg2 var="viewAllNotesTitle" key="yukon.web.common.paoNotesSearch.viewAllNotes"/>
                    <cti:icon icon="icon-notes-pin" classes="js-view-all-notes cp" title="${viewAllNotesTitle}" data-pao-id="${device.paoIdentifier.paoId}"/>
                </c:if> 
            </td>
            <td><cti:paoDetailUrl yukonPao="${device.paoIdentifier}" newTab="true">${fn:escapeXml(device.deviceName)}</cti:paoDetailUrl></td>
            <td>${fn:escapeXml(device.meterNumber)}</td>
            <td>${fn:escapeXml(device.paoIdentifier.paoType.paoTypeName)}</td>
            <td>${fn:escapeXml(device.addressSerialNumber)}</td>
            <td><cti:paoDetailUrl yukonPao="${device.gatewayPaoIdentifier}" newTab="true">${fn:escapeXml(device.gatewayName)}</cti:paoDetailUrl></td>
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
                        <cti:msg2 key=".noRecentReadingFound"/>
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
    <div class="dn" id="js-pao-notes-popup"></div>
    <cti:includeScript link="/resources/js/pages/yukon.tools.paonotespopup.js"/>
</cti:msgScope>
