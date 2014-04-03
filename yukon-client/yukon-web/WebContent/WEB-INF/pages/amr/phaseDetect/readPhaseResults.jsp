<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<c:choose>
    <c:when test="${not empty errorMsg}">
        <span class="error" style="font-weight: bold;"><i:inline key="yukon.web.modules.amr.phaseDetect.errorRead"/>${errorMsg}</span>
    </c:when>
    <c:when test="${readCanceled}">
        <span class="error" style="font-weight: bold;"><i:inline key="yukon.web.modules.amr.phaseDetect.readCanceled"/></span>
    </c:when>
    <c:otherwise>
        <div style="float: left;padding-right: 10px;">
            <div style="float: left;">
                <table>
                    <tr>
                        <td style="vertical-align: middle;padding-top: 0px;">
                            <cti:msg2 key="yukon.web.modules.amr.phaseDetect.progressBarLabel" />:
                        </td>
                        <td style="padding-top: 0px;">
                            <tags:updateableProgressBar totalCount="${totalCount}" countKey="PHASE_DETECT/${id}/RESULTS_COUNT" />
                            <cti:dataUpdaterEventCallback function="readFinished" id="PHASE_DETECT/${id}/IS_COMPLETE"/>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
        <div style="float: left;" id ="cancelReadDiv">
            <cti:url var="cancelReadUrl" value="/amr/phaseDetect/cancelRead"/>
            <form action="${cancelReadUrl}" method="get">
                <cti:msg2 var="cancelRead" key="yukon.web.modules.amr.phaseDetect.cancelRead"/>
                <c:choose>
                    <c:when test="${readComplete}"><c:set var="disabled" value="true"/></c:when>
                    <c:otherwise><c:set var="disabled" value="false"/></c:otherwise>
                </c:choose>
                <cti:button type="submit" value="${cancelRead}" label="${cancelRead}" id="cancelReadButton" disabled="${disabled}"/>
            </form>
        </div>
    </c:otherwise>
</c:choose>
