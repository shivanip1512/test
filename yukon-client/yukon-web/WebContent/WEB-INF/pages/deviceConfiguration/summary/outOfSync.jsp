<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:msg var="failureResult" key="yukon.common.device.bulk.verifyConfigResults.failureResult"/>
<cti:msgScope paths="yukon.web.widgets.configWidget">
    <c:choose>
        <c:when test="${!empty verifyResult}">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".verifyResult">
                    <c:choose>
                        <c:when test="${verifyResult.synced}">
                            <span class="success"><i:inline key=".inSync" /></span>
                        </c:when>
                        <c:otherwise>
                            <span class="error">${failureResult}
                                ${fn:escapeXml(verifyResult.discrepancies)}</span>
                        </c:otherwise>
                    </c:choose>
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </c:when>
        <c:otherwise>
            ${needsUploadMessage}
        </c:otherwise>
    </c:choose>
</cti:msgScope>
