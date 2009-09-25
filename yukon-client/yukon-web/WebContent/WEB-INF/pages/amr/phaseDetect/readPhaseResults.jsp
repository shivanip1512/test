<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
    <c:when test="${not empty errorMsg}">
        <span class="errorRed" style="font-weight: bold;">Error Sending Read Command: ${errorMsg}</span>
    </c:when>
    <c:when test="${readCanceled}">
        <span class="errorRed" style="font-weight: bold;">Read Canceled</span>
    </c:when>
    <c:otherwise>
        <div style="float: left;padding-right: 10px;">
            <div style="float: left;">
                <table>
                    <tr>
                        <td style="vertical-align: middle;padding-top: 0px;">
                            <cti:msg key="yukon.web.modules.amr.phaseDetect.step4.progressBarLabel" />:
                        </td>
                        <td style="padding-top: 0px;">
                            <tags:updateableProgressBar totalCount="${totalCount}" countKey="PHASE_DETECT/${id}/RESULTS_COUNT" />
                            <cti:dataUpdaterEventCallback function="readFinished" id="PHASE_DETECT/${id}/IS_COMPLETE" />
                        </td>
                    </tr>
                </table>
            </div>
        </div>
        <div style="float: left;" id ="cancelReadDiv">
            <form action="/spring/amr/phaseDetect/cancelRead" method="get">
                <input type="submit" value="Cancel Read" <c:if test="${readComplete}">disabled</c:if> id="cancelReadButton">
            </form>
        </div>
    </c:otherwise>
</c:choose>
