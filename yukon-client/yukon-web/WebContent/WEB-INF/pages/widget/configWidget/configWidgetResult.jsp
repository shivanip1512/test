<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:msg var="failureResult" key="yukon.common.device.bulk.verifyConfigResults.failureResult"/>

<c:choose>
    <c:when test="${verifyResult != null}">
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".verifyResult" nameColumnWidth="150px">
                <c:choose>
                    <c:when test="${verifyResult.synced}">
                        <span class="success dib"><i:inline key=".inSync"/></span>
                    </c:when>
                    <c:otherwise>
                        <span class="error dib">${failureResult} ${fn:escapeXml(verifyResult.discrepancies)}</span>
                    </c:otherwise>
                </c:choose>
            </tags:nameValue2>
        </tags:nameValueContainer2>
    </c:when>
    <c:when test="${sendResult != null}">
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".sendResult" nameColumnWidth="150px">
                <c:choose>
                    <c:when test="${sendResult.anyErrorOrException}"><span class="error dib"><i:inline key=".fail"/></span></c:when>
                    <c:otherwise><span class="success dib"><i:inline key=".sendConfigSuccess"/></span></c:otherwise>
                </c:choose>
                <amr:meterReadingsResult result="${sendResult}" styleClass="error" titleClass="error"/>
            </tags:nameValue2>
        </tags:nameValueContainer2>
    </c:when>
    <c:when test="${readResult != null}">
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".readResult" nameColumnWidth="150px">
                <c:choose>
                    <c:when test="${readResult.anyErrorOrException}"><span class="error dib"><i:inline key=".fail"/></span></c:when>
                    <c:otherwise><span class="success dib"><i:inline key=".readSentSuccess"/></span></c:otherwise>
                </c:choose>
                <amr:meterReadingsResult result="${readResult}" styleClass="error" titleClass="error"/>
            </tags:nameValue2>
        </tags:nameValueContainer2>
    </c:when>
</c:choose>