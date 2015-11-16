<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<%@ attribute name="requestId" required="true" type="java.lang.Long" %>
<%@ attribute name="percentDone" required="true" %>
<%@ attribute name="lastReturnMsg" %>

<c:choose>

    <c:when test="${empty percentDone}">
        <div id="progressMsg_${requestId}" class="error"><i:inline key="yukon.common.requestFailed"/></div><div>${pageScope.lastReturnMsg}</div>
    </c:when>

    <c:when test="${percentDone >= 100.0}">
        <div id="progressMsg_${requestId}" class="success"><i:inline key="yukon.common.completed"/></div>
    </c:when>
    
    <c:when test="${percentDone <= 0.0}">
        <div id="progressMsg_${requestId}"><i:inline key="yukon.common.pending"/></div>
    </c:when>

    <c:otherwise>
        <table>
            <tr>
                <td>
                    <div id="progressBorder_${requestId}" class="progress" align="left">
                        <div id="progressInner_${requestId}" class="progress-bar" style="width: ${percentDone}%;">
                        </div>
                    </div>
                </td>
                <td>
                    <div id="percentDone_${requestId}" class="progressbar-percent-complete">
                        <fmt:formatNumber type="percent" value="${percentDone / 100}"/>
                    </div>
                </td>
            </tr>
        </table>
    </c:otherwise>
    
</c:choose>