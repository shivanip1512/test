<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="capTags" tagdir="/WEB-INF/tags/capcontrol" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="flot" tagdir="/WEB-INF/tags/flotChart" %> <%-- Used in Trends --%>

<flot:defaultIncludes/>

<cti:msgScope paths="modules.capcontrol.areas, modules.capcontrol">
<c:if test="${empty areas}">
    <span class="empty-list"><i:inline key=".noResults"/></span>
</c:if>

<c:if test="${not empty areas}">
    <table id="areaTable" class="compact-results-table dashed has-alerts has-actions row-highlighting">
        <thead>
            <tr>
                <th>&nbsp;</th>
                <th><i:inline key=".name"/></th>
                <th></th>
                <th><i:inline key=".state"/></th>
                <th class="tar"><i:inline key=".availableKvars"/></th>
                <th class="tar"><i:inline key=".unavailableKvars"/></th>
                <th class="tar"><i:inline key=".closedKvars"/></th>
                <th class="tar"><i:inline key=".trippedKvars"/></th>
                <th class="tar"><i:inline key=".pfactorEstimatedOps"/></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>

            <c:forEach var="viewableArea" items="${areas}">
                <%--Setup Variables --%>
                <c:set var="areaId" value="${viewableArea.ccId}"/>

                <tr data-tooltip="#station-count-${areaId}" data-pao-id="${areaId}">
                    <td>
                        <capTags:warningImg paoId="${areaId}" type="${areaType.updaterType}"/>
                        <div id="station-count-${areaId}" class="dn"><i:inline key=".stationCount" arguments="${viewableArea.stationCount}"/></div>
                    </td>
                    <td>
                        <cti:url var="url" value="/capcontrol/areas/${areaId}"/>
                        <a href="${url}">${fn:escapeXml(viewableArea.ccName)}</a>
                    </td>
                    <td class="state-indicator">
                        <span class="box state-box js-cc-state-updater" data-pao-id="${areaId}">&nbsp;</span>
                        <cti:dataUpdaterCallback function="yukon.da.updaters.stateColor" initialize="true" value="${areaType.updaterType}/${areaId}/STATE_FLAGS"/>
                    </td>
                    <td>
                        <cti:capControlValue paoId="${areaId}" type="${areaType.updaterType}" format="STATE" />
                    </td>
                    <td class="tar"><cti:capControlValue paoId="${areaId}" type="${areaType.updaterType}" format="KVARS_AVAILABLE" initialize="false"/></td>
                    <td class="tar"><cti:capControlValue paoId="${areaId}" type="${areaType.updaterType}" format="KVARS_UNAVAILABLE" initialize="false"/></td>
                    <td class="tar"><cti:capControlValue paoId="${areaId}" type="${areaType.updaterType}" format="KVARS_CLOSED" initialize="false"/></td>
                    <td class="tar"><cti:capControlValue paoId="${areaId}" type="${areaType.updaterType}" format="KVARS_TRIPPED" initialize="false"/></td>
                    <td class="tar">
                        <span style="margin-right:10px;"><cti:capControlValue paoId="${areaId}" type="${areaType.updaterType}" format="PFACTOR"/></span>
                       <cm:dropdown icon="icon-cog" triggerClasses="fr">
                            <c:if test="${hasAreaControl}">
                                <li>
                                    <a id="areaState_${areaId}" href="javascript:void(0)" class="clearfix">
                                        <cti:icon icon="icon-cog" /><span class="dib"><i:inline key=".area.actions"/></span>
                                    </a>
                                </li>
                             </c:if>
                                
                            <c:choose>
                                <c:when test="${hasEditingRole}">
                                    <c:set var="editKey" value="edit"/>
                                </c:when>
                                <c:otherwise>
                                    <c:set var="editKey" value="info"/>
                                </c:otherwise>
                            </c:choose>
                            
                            <cti:url var="editUrl" value="/editor/cbcBase.jsf">
                                <cti:param name="type" value="2" />
                                <cti:param name="itemid" value="${areaId}" />
                            </cti:url>
                            <cm:dropdownOption key="components.button.${editKey}.label" icon="icon-pencil" href="${editUrl}" />

                            <c:if test="${showAnalysis}">
                                <i:simplePopup titleKey=".analysisTrends" id="analysisTrendsOptions_${areaId}" on="#showTrendsPopup_${areaId}">
                                    <%@ include file="analysisTrendsOptions.jspf" %>
                                </i:simplePopup>
                                <cm:dropdownOption key=".analysis.label" id="showTrendsPopup_${areaId}" icon="icon-chart-line" />
                            </c:if>
                             <cti:url var="recentEventsUrl" value="/capcontrol/search/recentEvents">
                                <cti:param name="value" value="${areaId}" />
                            </cti:url>
                            <cm:dropdownOption key=".recentEvents.label" icon="icon-application-view-columns" href="${recentEventsUrl}" />                               
                        </cm:dropdown>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

</c:if>
</cti:msgScope>