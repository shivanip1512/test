<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<c:choose>
    <c:when test="${verifyResult != null}">
        <ct:nameValueContainer2>
            <ct:nameValue2 nameKey=".verifyResult">
                <c:choose>
                    <c:when test="${verifyResult.synced}">
                        <span class="fwb success"><i:inline key=".inSync"/></span>
                    </c:when>
                    <c:otherwise>
                        <span class="fwb error"><c:out value="${verifyResult.discrepancies}"/></span>
                    </c:otherwise>
                </c:choose>
            </ct:nameValue2>
        </ct:nameValueContainer2>
    </c:when>
    <c:when test="${sendResult != null}">
        <ct:nameValueContainer2>
            <ct:nameValue2 nameClass="wsnw" nameKey=".sendResult">
                <cti:msg2 var="sendConfigFail" key=".fail"/>
                <cti:msg2 var="sendConfigSuccess" key=".sendConfigSuccess"/>
            	<amr:meterReadingsResult result="${sendResult}" errorMsg="${sendConfigFail}" successMsg="${sendConfigSuccess}"/>
            </ct:nameValue2>
        </ct:nameValueContainer2>
    </c:when>
    <c:when test="${readResult != null}">
        <ct:nameValueContainer2>
            <ct:nameValue2 nameKey=".readResult">
                <cti:msg2 var="readSentFail" key=".fail"/>
                <cti:msg2 var="readSentSuccess" key=".readSentSuccess"/>
            	<amr:meterReadingsResult result="${readResult}" errorMsg="${readSentFail}" successMsg="${readSentSuccess}"/>
            </ct:nameValue2>
        </ct:nameValueContainer2>
    </c:when>
</c:choose>