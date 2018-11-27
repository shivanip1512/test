<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="assetAvailability.detail">
    <cti:toJson id="js-asset-availability-summary" object="${summary}"/>
    <c:forEach var="status" items="${statusTypes}">
        <input type="hidden" class="js-asset-${status}" value="<cti:msg2 key=".assetDetails.status.${status}"/>"/>
    </c:forEach>
        
    <cti:url var="formAction" value="/dr/assetAvailability/detail"/>
    <form id="js-asset-availability-filter-form" action="${formAction}" method="GET">
        <input type="hidden" name="controlAreaOrProgramOrScenarioId" value="${controlAreaOrProgramOrScenarioId}"/>
        <div class="column-12-12 clearfix">
            <div class="column one">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".targetLevel">
                        ${paoName} (${totalDevices}&nbsp;<i:inline key="yukon.common.Devices"/>)
                    </tags:nameValue2>
                </tags:nameValueContainer2>
                <div style="max-height: 200px;" class="js-asset-availability-pie-chart-summary"></div>
            </div>
            <div class="column two nogutter filter-container" style="margin-top:40px;">
                <span class="fr cp"><cti:icon icon="icon-help" data-popup="#results-help"/></span>
                <cti:msg2 var="helpTitle" key=".detail.helpTitle"/>
                <div id="results-help" class="dn" data-title="${helpTitle}" style="max-width: 700px;">
                    <cti:msg2 key=".detail.helpText"/>
                </div>
                <br/>
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".deviceGroups">
                        <cti:list var="groups">
                            <c:forEach var="subGroup" items="${deviceSubGroups}">
                                <cti:item value="${subGroup}"/>
                            </c:forEach>
                        </cti:list>
                        <tags:deviceGroupPicker inputName="deviceSubGroups" inputValue="${groups}" multi="true"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".range">
                        <div class="button-group stacked">
                            <c:forEach var="status" items="${statusTypes}">
                                <c:set var="checked" value="${false}"/>
                                <c:forEach var="statusEnum" items="${statuses}">
                                    <c:if test="${statusEnum eq status}">
                                        <c:set var="checked" value="${true}"/>
                                    </c:if>
                                </c:forEach>
                                <c:set var="buttonTextColor" value="green"/>
                                <c:if test="${status == 'OPTED_OUT'}">
                                    <c:set var="buttonTextColor" value="pie-blue"/>
                                </c:if>
                                <c:if test="${status == 'INACTIVE'}">
                                    <c:set var="buttonTextColor" value="orange"/>
                                </c:if>
                                <c:if test="${status == 'UNAVAILABLE'}">
                                    <c:set var="buttonTextColor" value="grey"/>
                                </c:if>
                                <tags:check name="statuses" key=".assetDetails.status.${status}" classes="M0" 
                                            buttonTextClasses="${buttonTextColor}" checked="${checked}" value="${status}"/>
                            </c:forEach>
                        </div>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
                <div class="action-area">
                    <cti:button classes="primary action" nameKey="filter" type="submit" busy="true"/>
                </div>
            </div>
        </div>
    </form>
    
    <span class="fwn"><i:inline key="yukon.common.filteredResults"/></span>
    <span class="badge">${searchResults.hitCount}</span>&nbsp;<i:inline key="yukon.common.Devices"/>
    <c:choose>
        <c:when test="${searchResults.hitCount > 0}">
            <span class="js-cog-menu">
                <cm:dropdown icon="icon-cog">
                    <!-- Collection Actions -->
                    <cti:url var="collectionActionsUrl" value="/bulk/collectionActions">
                        <c:forEach items="${deviceCollection.collectionParameters}" var="collectionParameter">
                            <cti:param name="${collectionParameter.key}" value="${collectionParameter.value}"/>
                        </c:forEach>
                    </cti:url>
                    <cm:dropdownOption key="yukon.common.collectionActions" icon="icon-cog-go" newTab="true" href="${collectionActionsUrl}"/>
                
                    <!-- Download -->
                    <cm:dropdownOption icon="icon-csv" key="yukon.common.download" classes="js-download"/>
                
                    <!-- Map Devices -->
                    <cti:url var="mapUrl" value="/tools/map">
                        <cti:mapParam value="${deviceCollection.collectionParameters}"/>
                    </cti:url>
                    <cm:dropdownOption icon="icon-map-sat" key="yukon.common.mapDevices" href="${mapUrl}" newTab="true"/>
                
                    <cti:checkRolesAndProperties value="DEVICE_ACTIONS">
                        <!-- Read Attribute -->
                        <cti:url var="readUrl" value="/group/groupMeterRead/homeCollection">
                            <c:forEach items="${deviceCollection.collectionParameters}" var="collectionParameter">
                                <cti:param name="${collectionParameter.key}" value="${collectionParameter.value}"/>
                            </c:forEach>
                        </cti:url>
                        <cm:dropdownOption icon="icon-read" key="yukon.common.readAttribute" href="${readUrl}" newTab="true"/>
                        
                        <!-- Send Command -->
                        <cti:checkRolesAndProperties value="GROUP_COMMANDER">
                            <cti:url var="commandUrl" value="/group/commander/collectionProcessing">
                                <c:forEach items="${deviceCollection.collectionParameters}" var="collectionParameter">
                                    <cti:param name="${collectionParameter.key}" value="${collectionParameter.value}"/>
                                </c:forEach>
                            </cti:url>
                            <cm:dropdownOption icon="icon-ping" key="yukon.common.sendCommand" href="${commandUrl}" newTab="true"/>
                        </cti:checkRolesAndProperties>
                    </cti:checkRolesAndProperties>
                </cm:dropdown>
            </span>
        
            <cti:url var="searchUrl" value="/dr/assetAvailability/detail">
                <cti:param name="controlAreaOrProgramOrScenarioId" value="${controlAreaOrProgramOrScenarioId}"/>
                <c:forEach var="subGroup" items="${deviceSubGroups}">
                    <cti:param name="deviceSubGroups" value="${subGroup}"/>
                </c:forEach>
                <c:forEach var="status" items="${statuses}">
                    <cti:param name="statuses" value="${status}"/>
                </c:forEach>
            </cti:url>
        
            <div id="js-filtered-results" data-url="${searchUrl}" data-static>
                <table class="compact-results-table row-highlighting has-alerts has-actions">
                    <thead>
                        <th class="row-icon M0"/>
                        <th class="row-icon M0"/>
                        <tags:sort column="${serialNumber}"/>
                        <tags:sort column="${deviceType}"/>
                        <tags:sort column="${lastCommunication}"/>
                        <tags:sort column="${lastRun}"/>
                        <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
                    </thead>
                    <tbody>
                        <c:forEach var="assetAvailabilitySearchResult" items="${searchResults.resultList}">
                            <tr>
                                <td class="vam">
                                    <c:if test="${assetAvailabilitySearchResult.availability == 'ACTIVE'}">
                                        <c:set var="circleColor" value="green-background"/>
                                    </c:if>
                                    <c:if test="${assetAvailabilitySearchResult.availability == 'OPTED_OUT'}">
                                        <c:set var="circleColor" value="blue-background"/>
                                    </c:if>
                                    <c:if test="${assetAvailabilitySearchResult.availability == 'INACTIVE'}">
                                        <c:set var="circleColor" value="orange-background"/>
                                    </c:if>
                                    <c:if test="${assetAvailabilitySearchResult.availability == 'UNAVAILABLE'}">
                                        <c:set var="circleColor" value="grey-background"/>
                                    </c:if>
                                    <cti:msg2 var="statusText" key=".assetDetails.status.${assetAvailabilitySearchResult.availability}"/>
                                    <div class="small-circle ${circleColor}" title="${statusText}"></div>
                                </td>
                                <td>
                                    <c:if test="${notesList.contains(assetAvailabilitySearchResult.deviceId)}">
                                        <cti:msg2 var="viewAllNotesTitle" key="yukon.web.common.paoNotesSearch.viewAllNotes"/>
                                        <cti:icon icon="icon-notes-pin" classes="js-view-all-notes cp" title="${viewAllNotesTitle}" 
                                                  data-pao-id="${assetAvailabilitySearchResult.deviceId}"/>
                                    </c:if>
                                </td>
                                <td>
                                    <cti:url var="inventoryViewUrl" value="/stars/operator/inventory/view">
                                        <cti:param name="inventoryId" value="${assetAvailabilitySearchResult.inventoryId}"/>
                                    </cti:url>
                                    <a href="${inventoryViewUrl}">${fn:escapeXml(assetAvailabilitySearchResult.serialNumber)}</a>
                                </td>
                                <td><i:inline key="${assetAvailabilitySearchResult.type}"/></td>
                                <td>
                                    <cti:formatDate type="BOTH" value="${assetAvailabilitySearchResult.lastComm}" 
                                                    var="lastCommunitation"/>
                                    ${lastCommunitation}
                                </td>
                                <td>
                                    <cti:formatDate type="BOTH" value="${assetAvailabilitySearchResult.lastRun}" 
                                                    var="lastRun"/>
                                    ${lastRun}
                                </td>
                                <td>
                                    <c:if test="${assetAvailabilitySearchResult.deviceId != 0}">
                                        <cm:dropdown icon="icon-cog">
                                            <cti:url var="collectionActionsUrl" value="/bulk/collectionActions">
                                                <cti:param name="collectionType" value="idList"/>
                                                <cti:param name="idList.ids" value="${assetAvailabilitySearchResult.deviceId}"/>
                                            </cti:url>
                                            <cm:dropdownOption key="yukon.common.collectionActions" href="${collectionActionsUrl}"
                                                               icon="icon-cog-go" newTab="true"/>
                                            <cti:url var="mapUrl" value="/tools/map">
                                                <cti:param name="collectionType" value="idList"/>
                                                <cti:param name="idList.ids" value="${assetAvailabilitySearchResult.deviceId}"/>
                                            </cti:url>
                                            <cm:dropdownOption icon="icon-map-sat" key="yukon.common.mapDevices" href="${mapUrl}" newTab="true"/>
                                            <cti:checkRolesAndProperties value="DEVICE_ACTIONS">
                                                <cti:url var="readUrl" value="/group/groupMeterRead/homeCollection">
                                                    <cti:param name="collectionType" value="idList"/>
                                                    <cti:param name="idList.ids" value="${assetAvailabilitySearchResult.deviceId}"/>
                                                </cti:url>
                                                <cm:dropdownOption icon="icon-read" key="yukon.common.readAttribute"
                                                                   href="${readUrl}" newTab="true"/>
                                                <cti:checkRolesAndProperties value="GROUP_COMMANDER">
                                                    <cti:url var="commandUrl" value="/group/commander/collectionProcessing">
                                                        <cti:param name="collectionType" value="idList"/>
                                                        <cti:param name="idList.ids" value="${assetAvailabilitySearchResult.deviceId}"/>
                                                    </cti:url>
                                                    <cm:dropdownOption icon="icon-ping" key="yukon.common.sendCommand"
                                                                       href="${commandUrl}" newTab="true"/>
                                                </cti:checkRolesAndProperties>
                                            </cti:checkRolesAndProperties>
                                        </cm:dropdown>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <tags:pagingResultsControls result="${searchResults}" adjustPageCount="true" thousands="true"/>
                <div class="dn" id="js-pao-notes-popup"></div>
            </div>
        </c:when>
        <c:otherwise>
            <span class="empty-list"><i:inline key=".noResults"/></span>
        </c:otherwise>
    </c:choose>
    
    <cti:includeScript link="HIGH_STOCK"/>
    <cti:includeScript link="/resources/js/pages/yukon.tools.paonotespopup.js"/>
    <cti:includeScript link="/resources/js/widgets/yukon.widget.assetAvailability.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.dr.assetavailability.detail.js"/>
</cti:standardPage>