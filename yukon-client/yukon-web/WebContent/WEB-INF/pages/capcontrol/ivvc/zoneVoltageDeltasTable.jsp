<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="modules.capcontrol.ivvc.zoneVoltageDeltas">
<c:if test="${searchResults.hitCount == 0}">
    <span class="empty-list"><i:inline key=".deltas.emptyTable"/></span>
</c:if>
<c:if test="${searchResults.hitCount != 0}">
    <div data-reloadable>
        <table id="deltaTable" class="compact-results-table row-highlighting">
            <thead>
                <tr>
                    <th><i:inline key=".deltas.cbcName" /></th>
                    <th><i:inline key=".deltas.bankName" /></th>
                    <th><i:inline key=".deltas.deviceName" /></th>
                    <th><i:inline key=".deltas.pointName" /></th>
                    <th><i:inline key=".deltas.preOp" /></th>
                    <th><i:inline key=".deltas.static" /></th>
                    <th style="width: 15%"><i:inline key=".deltas.delta" /></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="pointDelta" items="${searchResults.resultList}" varStatus="status">
                    <tr>
                        <td class="dn bankAndPointIds">
                            <input class="pointDeltaBankId" type="hidden" value="${pointDelta.bankId}"/>
                            <input class="pointDeltaPointId" type="hidden" value="${pointDelta.pointId}"/>
                        </td>
                        <td>
                            ${fn:escapeXml(pointDelta.cbcName)}
                        </td>
                        <td>
                            ${fn:escapeXml(pointDelta.bankName)}
                        </td>
                        <td>
                            ${fn:escapeXml(pointDelta.affectedDeviceName)}
                        </td>
                        <td>
                            ${fn:escapeXml(pointDelta.affectedPointName)}
                        </td>
                        <td>
                            ${fn:escapeXml(pointDelta.preOpValue)}
                        </td>
                        <c:choose>
                            <c:when test="${hasEditingRole}">
                                <td><label class="staticDeltaLabel"><input type="checkbox" class="editableStaticDelta"
                                    <c:choose>
                                        <c:when test="${pointDelta.staticDelta}">
                                            checked="checked"
                                        </c:when>
                                    </c:choose>></label>
                                </td>
                                <td class="editable">
                                    <cti:msg2 var="deltaTitle" key=".deltas.deltaTitle" />
                                    <div class="viewDelta anchorUnderlineHover" title="${deltaTitle}">
                                        <input type="hidden" value="${pointDelta.delta}"/>
                                        ${fn:escapeXml(pointDelta.deltaRounded)}
                                    </div>
                                    <div class="editDelta" style="display: none;">
                                        <input type="text" style="margin-right: 5px; width: 30px;"
                                            name="editDeltaInput"
                                            value="${fn:escapeXml(pointDelta.delta)}">
                                        <a href="javascript:void(0);" class="cancelEdit"><i:inline key="yukon.common.cancel"/></a>
                                    </div>
                                </td>
                            </c:when>
                            <c:otherwise>
                                <td><input type="checkbox"
                                    disabled="disabled"
                                    <c:choose>
                                            <c:when test="${pointDelta.staticDelta}">
                                                checked="checked"
                                            </c:when>
                                        </c:choose>>
                                </td>
                                <td>
                                    <div>
                                        ${fn:escapeXml(pointDelta.deltaRounded)}
                                    </div>
                                </td>
                            </c:otherwise>
                        </c:choose>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <cti:url var="baseUrl" value="/capcontrol/ivvc/zone/voltageDeltasTable">
            <cti:param name="zoneId" value="${zoneId}" />
        </cti:url>
        <tags:pagingResultsControls baseUrl="${baseUrl}"  result="${searchResults}" adjustPageCount="true"/>
    </div>
</c:if>
</cti:msgScope>