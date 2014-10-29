<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="capTags" tagdir="/WEB-INF/tags/capcontrol" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.capcontrol.areas, modules.capcontrol">
<c:if test="${searchResults.hitCount == 0}">
    <span class="empty-list"><i:inline key=".noResults"/></span>
</c:if>
<c:if test="${searchResults.hitCount > 0}">
    <table id="areaTable" class="compact-results-table dashed">
        <thead>
            <tr>
                <th width="16px">&nbsp;</th>
                <th><i:inline key=".name"/></th>
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

            <c:forEach var="viewableArea" items="${searchResults.resultList}">
                <%--Setup Variables --%>
                <c:set var="thisAreaId" value="${viewableArea.area.ccId}"/>

                <cti:url var="substationUrl" value="/capcontrol/tier/substations">
                    <cti:param name="bc_areaId" value="${thisAreaId}"/>
                </cti:url>
                <tr>
                    <td>
                        <capTags:warningImg paoId="${thisAreaId}" type="${areaType.updaterType}"/>
                    </td>
                    <td>
                        <a href="${substationUrl}">${fn:escapeXml(viewableArea.area.ccName)}</a>
                    </td>
                    <td>
                        <c:if test="${hasAreaControl}"><a id="areaState_${thisAreaId}" href="javascript:void(0);" class="subtle-link"></c:if>
                        <c:if test="${!hasAreaControl}"><span></c:if>
                            <span class="box state-box js-state" data-pao-id="${thisAreaId}">&nbsp;</span>
                            <cti:dataUpdaterCallback function="yukon.da.updaters.stateColor" initialize="true" value="${areaType.updaterType}/${thisAreaId}/STATE_FLAGS"/>
                            <cti:capControlValue paoId="${thisAreaId}" type="${areaType.updaterType}" format="STATE" />
                        <c:if test="${hasAreaControl}"></a></c:if>
                        <c:if test="${!hasAreaControl}"></span></c:if>
                    </td>
                    <td class="tar"><cti:capControlValue paoId="${thisAreaId}" type="${areaType.updaterType}" format="KVARS_AVAILABLE"/></td>
                    <td class="tar"><cti:capControlValue paoId="${thisAreaId}" type="${areaType.updaterType}" format="KVARS_UNAVAILABLE"/></td>
                    <td class="tar"><cti:capControlValue paoId="${thisAreaId}" type="${areaType.updaterType}" format="KVARS_CLOSED"/></td>
                    <td class="tar"><cti:capControlValue paoId="${thisAreaId}" type="${areaType.updaterType}" format="KVARS_TRIPPED"/></td>
                    <td class="tar"><cti:capControlValue paoId="${thisAreaId}" type="${areaType.updaterType}" format="PFACTOR"/></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <tags:pagingResultsControls result="${searchResults}" adjustPageCount="true"/>
</c:if>
</cti:msgScope>