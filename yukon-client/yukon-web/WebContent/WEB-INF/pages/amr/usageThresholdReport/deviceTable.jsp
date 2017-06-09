<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<cti:msgScope paths="modules.amr.usageThresholdReport.results">

    <cti:url var="dataUrl" value="/amr/usageThresholdReport/results">
        <cti:param name="reportId" value="${reportId}"/>
        <c:forEach var="subGroup" items="${deviceSubGroups}">
            <cti:param name="deviceSubGroups" value="${subGroup}"/>
        </c:forEach>
        <cti:param name="includeDisabled" value="${includeDisabled}"/>
        <cti:param name="thresholdDescriptor" value="${thresholdDescriptor}"/>
        <cti:param name="threshold" value="${threshold}"/>
        <c:forEach var="avail" items="${availability}">
            <cti:param name="availability" value="${avail}"/>
        </c:forEach>
    </cti:url>
    
    <div data-url="${dataUrl}">
        <span class="fwn"><i:inline key=".devices"/>:</span>
        <span class="badge">${detail.hitCount}</span>
        
        <c:if test="${detail.hitCount > 0}">
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
            <tags:sort column="${deviceName}" />                
            <tags:sort column="${meterNumber}" />                
            <tags:sort column="${deviceType}" />                
            <tags:sort column="${serialNumberAddress}" />
            <tags:sort column="${delta}" />    
            <tags:sort column="${earliestReading}" />                
            <tags:sort column="${latestReading}" />                            
            <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
            <c:forEach var="device" items="${detail.resultList}">
                <c:set var="deviceId" value="${device.paoIdentifier.paoId}"/>
                <tr>
                    <td>
                        <cti:msg2 var="availabilityText" key=".dataAvailability.${device.availability}"/>
                        <div class="small-circle" title="${availabilityText}" style="background-color:${device.availability.color}"></div>
                    </td>
                    <td><cti:paoDetailUrl yukonPao="${device.paoIdentifier}" newTab="true">${device.deviceName}</cti:paoDetailUrl></td>
                    <td>${device.meterNumber}</td>
                    <td>${device.paoIdentifier.paoType.paoTypeName}</td>
                    <td>${device.addressSerialNumber}</td>
                    <td><fmt:formatNumber pattern="###.#" value="${device.delta}"/></td>
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
                                <cti:msg2 key=".noRecentReadingFound"/>
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

    </div>

</cti:msgScope>

