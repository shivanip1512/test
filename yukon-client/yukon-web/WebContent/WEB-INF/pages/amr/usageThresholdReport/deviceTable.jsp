<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:msgScope paths="modules.amr.usageThresholdReport.results">

    <cti:url var="dataUrl" value="/amr/usageThresholdReport/results">
        <cti:param name="reportId" value="${reportId}"/>
        <c:forEach var="subGroup" items="${filter.groups}">
            <cti:param name="deviceSubGroups" value="${subGroup.fullName}"/>
        </c:forEach>
        <cti:param name="includeDisabled" value="${filter.includeDisabled}"/>
        <cti:param name="thresholdDescriptor" value="${filter.thresholdDescriptor}"/>
        <cti:param name="threshold" value="${filter.threshold}"/>
        <c:forEach var="avail" items="${filter.availability}">
            <cti:param name="availability" value="${avail}"/>
        </c:forEach>
    </cti:url>
    
    <div data-url="${dataUrl}">
        <span class="fwn"><i:inline key=".filteredResults"/></span>
        <span class="badge">${report.detail.hitCount}</span>&nbsp;<i:inline key=".devices"/>
        <span class="fr">
            <c:forEach var="availability" items="${dataAvailabilityOptions}">
                <cti:msg2 var="availabilityText" key=".dataAvailability.${availability}"/>
                <span class="label" style="background-color:${availability.color}" title="${availabilityText}">${report.getAvailabilityCount(availability)}</span>
            </c:forEach>
        </span>
        
        <c:if test="${report.detail.hitCount > 0}">
            <span class="js-cog-menu">
                <cm:dropdown icon="icon-cog">
                    <cti:url var="collectionActionsUrl" value="/bulk/collectionActions">
                        <c:forEach items="${deviceCollection.collectionParameters}" var="cp">
                            <cti:param name="${cp.key}" value="${cp.value}"/>
                        </c:forEach>
                    </cti:url>
                    <cm:dropdownOption key=".collectionActions" href="${collectionActionsUrl}" icon="icon-cog-go" newTab="true"/> 
                    <cm:dropdownOption icon="icon-csv" key=".download" classes="js-download"/>  
                    <cti:url var="mapUrl" value="/tools/map">
                        <cti:mapParam value="${deviceCollection.collectionParameters}"/>
                    </cti:url>
                    <cm:dropdownOption icon="icon-map-sat" key=".mapDevices" href="${mapUrl}" newTab="true"/>
                    <cti:url var="readUrl" value="/group/groupMeterRead/homeCollection">
                        <c:forEach items="${deviceCollection.collectionParameters}" var="cp">
                            <cti:param name="${cp.key}" value="${cp.value}"/>
                        </c:forEach>                
                    </cti:url>
                    <cm:dropdownOption icon="icon-read" key=".readAttribute" href="${readUrl}" newTab="true"/>          
                    <cti:url var="commandUrl" value="/group/commander/collectionProcessing">
                        <c:forEach items="${deviceCollection.collectionParameters}" var="cp">
                            <cti:param name="${cp.key}" value="${cp.value}"/>
                        </c:forEach>                
                    </cti:url>
                    <cm:dropdownOption icon="icon-ping" key=".sendCommand" href="${commandUrl}" newTab="true"/>
                </cm:dropdown>
            </span>
        </c:if>
            
        <table class="compact-results-table row-highlighting has-alerts has-actions">
            <th></th>
            <th class="row-icon"/>
            <tags:sort column="${deviceName}" />                
            <tags:sort column="${meterNumber}" />                
            <tags:sort column="${deviceType}" />                
            <tags:sort column="${primaryGateway}" />
            <tags:sort column="${delta}" />    
            <tags:sort column="${earliestReading}" />                
            <tags:sort column="${latestReading}" />                            
            <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
            <c:forEach var="device" items="${report.detail.resultList}">
                <c:set var="deviceId" value="${device.paoIdentifier.paoId}"/>
                <tr>
                    <td>
                        <cti:msg2 var="availabilityText" key=".dataAvailability.${device.availability}"/>
                        <div class="small-circle" title="${availabilityText}" style="background-color:${device.availability.color}"></div>
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
                    <td><cti:paoDetailUrl yukonPao="${device.gatewayPaoIdentifier}" newTab="true">${fn:escapeXml(device.gatewayName)}</cti:paoDetailUrl></td>
                    <td class="wsnw">
                        <fmt:formatNumber pattern="###.#" value="${device.delta}"/>
                         <c:if test="${device.earliestReading != null}">
                            &nbsp;<cti:pointValueFormatter format="UNIT" value="${device.earliestReading}" />
                         </c:if>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${device.earliestReading != null}">
                                <cti:uniqueIdentifier var="popupId" prefix="historical-readings-"/>
                                <cti:url var="historyUrl" value="/meter/historicalReadings/view">
                                    <cti:param name="pointId" value="${device.earliestReading.id}"/>
                                    <cti:param name="deviceId" value="${deviceId}"/>
                                </cti:url>
                                <a href="javascript:void(0);" data-popup="#${popupId}" class="${pageScope.classes}">
                                    <cti:pointValueFormatter format="VALUE_UNIT" value="${device.earliestReading}" />
                                    &nbsp;<cti:formatDate type="BOTH" value="${device.earliestReading.pointDataTimeStamp}"/>
                                </a>
                                <div id="${popupId}" data-width="500" data-height="400" data-url="${historyUrl}" class="dn"></div>
                            </c:when>
                            <c:otherwise>
                                <cti:msg2 key=".noReadingFound"/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${device.latestReading != null}">
                                <cti:uniqueIdentifier var="popupId" prefix="historical-readings-"/>
                                <cti:url var="historyUrl" value="/meter/historicalReadings/view">
                                    <cti:param name="pointId" value="${device.latestReading.id}"/>
                                    <cti:param name="deviceId" value="${deviceId}"/>
                                </cti:url>
                                <a href="javascript:void(0);" data-popup="#${popupId}" class="${pageScope.classes}">
                                    <cti:pointValueFormatter format="VALUE_UNIT" value="${device.latestReading}" />
                                    &nbsp;<cti:formatDate type="BOTH" value="${device.latestReading.pointDataTimeStamp}"/>
                                </a>
                                <div id="${popupId}" data-width="500" data-height="400" data-url="${historyUrl}" class="dn"></div>
                            </c:when>
                            <c:otherwise>
                                <cti:msg2 key=".noReadingFound"/>
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
        <tags:pagingResultsControls result="${report.detail}" adjustPageCount="true" thousands="true"/>

    </div>
    <div class="dn" id="js-pao-notes-popup"></div>
</cti:msgScope>

