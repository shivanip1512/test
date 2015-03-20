<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="capTags" tagdir="/WEB-INF/tags/capcontrol" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.capcontrol.areas, modules.capcontrol">
<c:if test="${empty areas}">
    <span class="empty-list"><i:inline key=".noResults"/></span>
</c:if>
<c:if test="${not empty areas}">
    <table id="areaTable" class="compact-results-table dashed has-alerts">
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
                <th class="tar"><i:inline key=".pfactorEstimated"/></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>

            <c:forEach var="viewableArea" items="${areas}">
                <%--Setup Variables --%>
                <c:set var="areaId" value="${viewableArea.ccId}"/>

                <cti:url var="substationUrl" value="/capcontrol/tier/substations">
                    <cti:param name="bc_areaId" value="${areaId}"/>
                </cti:url>
                <tr data-has-row-tooltip>
                    <td>
                        <capTags:warningImg paoId="${areaId}" type="${areaType.updaterType}"/>
                        <span id="station-count-${areaId}" class="dn"><i:inline key=".stationCount" arguments="${viewableArea.stationCount}"/></span>
                    </td>
                    <td data-tooltip="#station-count-${areaId}" data-row-tooltip>
                        <a href="${substationUrl}">${fn:escapeXml(viewableArea.ccName)}</a>
                    </td>
                    <td class="state-indicator">
                        <span class="box state-box js-cc-state-updater" data-pao-id="${areaId}">&nbsp;</span>
                        <cti:dataUpdaterCallback function="yukon.da.updaters.stateColor" initialize="true" value="${areaType.updaterType}/${areaId}/STATE_FLAGS"/>
                    </td>
                    <td>
                        <c:if test="${hasAreaControl}"><a id="areaState_${areaId}" href="javascript:void(0);" class="subtle-link"></c:if>
                            <cti:capControlValue paoId="${areaId}" type="${areaType.updaterType}" format="STATE" />
                        <c:if test="${hasAreaControl}"></a></c:if>
                    </td>
                    <td class="tar"><cti:capControlValue paoId="${areaId}" type="${areaType.updaterType}" format="KVARS_AVAILABLE" initialize="false"/></td>
                    <td class="tar"><cti:capControlValue paoId="${areaId}" type="${areaType.updaterType}" format="KVARS_UNAVAILABLE" initialize="false"/></td>
                    <td class="tar"><cti:capControlValue paoId="${areaId}" type="${areaType.updaterType}" format="KVARS_CLOSED" initialize="false"/></td>
                    <td class="tar"><cti:capControlValue paoId="${areaId}" type="${areaType.updaterType}" format="KVARS_TRIPPED" initialize="false"/></td>
                    <td class="tar"><cti:capControlValue paoId="${areaId}" type="${areaType.updaterType}" format="PFACTOR"/></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</c:if>
</cti:msgScope>