<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="requestId" required="true" type="java.lang.Long"%>
<%@ attribute name="percentDone" required="true" type="java.lang.String"%>
<%@ attribute name="lastReturnMsg" required="false" type="java.lang.String"%>

<c:choose>

    <c:when test="${empty percentDone}">
        <div id="progressMsg_${requestId}" style='color:#FF0000;font-weight:bold;'>Request Failed</div>${pageScope.lastReturnMsg}
    </c:when>

    <c:when test="${percentDone >= 100.0}">
        <div id="progressMsg_${requestId}" class="fwb tac success">Completed</div>
    </c:when>
    
    <c:when test="${percentDone <= 0.0}">
        <div id="progressMsg_${requestId}" class="fwb tac">Pending</div>
    </c:when>

    <c:otherwise>
        <table cellpadding="0px" border="0px">
            <tr>
                <td>
                    <div id="progressBorder_${requestId}" class="progressBarBorder" align="left">
                        <div id="progressInner_${requestId}" class="progressBarInner" style="width: ${percentDone}px;">
                        </div>
                    </div>
                </td>
                <td>
                    <div id="percentDone_${requestId}" class="progressBarPercentComplete">
                        ${percentDone}%
                    </div>
                </td>
            </tr>
        </table>
    </c:otherwise>
    
</c:choose>