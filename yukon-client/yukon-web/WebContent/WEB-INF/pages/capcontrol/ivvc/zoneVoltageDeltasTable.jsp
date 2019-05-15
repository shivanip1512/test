<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="modules.capcontrol.ivvc.zoneVoltageDeltas">

<c:choose>
    <c:when test="${fn:length(pointDeltas) == 0}">
        <span class="empty-list"><i:inline key=".deltas.emptyTable"/></span>
    </c:when>
    <c:otherwise>
        <table id="deltaTable" class="compact-results-table">
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
                <c:forEach var="pointDelta" items="${pointDeltas}" varStatus="status">
                    <tr data-bank-id="${pointDelta.bankId}" data-point-id="${pointDelta.pointId}">
                        <td>${fn:escapeXml(pointDelta.cbcName)}</td>
                        <td>${fn:escapeXml(pointDelta.bankName)}</td>
                        <td>${fn:escapeXml(pointDelta.affectedDeviceName)}</td>
                        <td>${fn:escapeXml(pointDelta.affectedPointName)}</td>
                        <td><cti:dataUpdaterValue type="VOLTAGE_DELTA" identifier="${pointDelta.bankId}/${pointDelta.pointId}/PRE_OP"/></td>
                        <c:choose>
                            <c:when test="${hasEditingRole}">
                                <td><label><input type="checkbox" class="js-static-delta"
                                    <c:choose>
                                        <c:when test="${pointDelta.staticDelta}">
                                            checked="checked"
                                        </c:when>
                                    </c:choose>></label>
                                </td>
                                <td class="editable">
                                    <cti:msg2 var="deltaTitle" key=".deltas.deltaTitle" />
                                    <div class="js-view-delta anchorUnderlineHover" title="${deltaTitle}">
                                        <cti:dataUpdaterValue type="VOLTAGE_DELTA" identifier="${pointDelta.bankId}/${pointDelta.pointId}/VOLTAGE_DELTA"/>
                                    </div>
                                    <div class="js-edit-delta dn">
                                        <input type="text" style="margin-right: 5px; width: 30px;"
                                            name="editDeltaInput"
                                            value="${fn:escapeXml(pointDelta.delta)}">
                                        <a href="javascript:void(0);" class="js-cancel-edit"><i:inline key="yukon.common.cancel"/></a>
                                    </div>
                                </td>
                            </c:when>
                            <c:otherwise>
                                <td><input type="checkbox" disabled="disabled" <c:if test="${pointDelta.staticDelta}">checked="checked"</c:if>></td>
                                <td><div><cti:dataUpdaterValue type="VOLTAGE_DELTA" identifier="${pointDelta.bankId}/${pointDelta.pointId}/VOLTAGE_DELTA"/></div></td>
                            </c:otherwise>
                        </c:choose>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:otherwise>
</c:choose>

</cti:msgScope>