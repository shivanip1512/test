<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:msg var="failureResult" key="yukon.common.device.bulk.verifyConfigResults.failureResult"/>
<cti:msgScope paths="yukon.web.widgets.configWidget">
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
</cti:msgScope>
