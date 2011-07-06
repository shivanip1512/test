<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:url var="delete" value="/WebConfig/yukon/Icons/stop.gif"/>
<c:url var="deleteOver" value="/WebConfig/yukon/Icons/stop_over.gif"/>

<%-- THE LIST OF ONGOING/PENDING PROFILE REQUESTS --%>
<div id="${divId}">
<c:choose>
    <c:when test="${not empty pendingRequests}">
        <table class="compactResultsTable">
            <tr>
                <td>
                    <table>
                        <c:forEach var="pendingRequest" items="${pendingRequests}">
                            <tr valign="top">
                                <%-- MORE INFO HIDE REVEAL --%>
                                <td>
                                    <%-- <tags:hideReveal title="${pendingRequest.from} - ${pendingRequest.to}" showInitially="true"> --%>
                                        <table class="compactResultsTable">
                                            <tr>
                                                <td nowrap colspan="2">${pendingRequest.from} - ${pendingRequest.to}</td>
                                            </tr>
                                            <tr>
                                                <td class="label"><i:inline key=".channel"/></td>
                                                <td>${pendingRequest.channel}</td>
                                            </tr>
                                            <tr>
                                                <td class="label"><i:inline key=".requestedBy"/></td>
                                                <td>${pendingRequest.userName}</td>
                                            </tr>
                                            <tr>
                                                <td colspan="2">${pendingRequest.email}</td>
                                            </tr>
                                        </table>
                                    <%-- </tags:hideReveal> --%>
                                </td>
                                <%-- STATUS BAR --%>
                                <td>
                                    <tags:pendingProfileProgressBar requestId="${pendingRequest.requestId}" percentDone="${pendingRequest.percentDone}" lastReturnMsg="${lastReturnMsg}"/>
                                </td>
                                <%-- CANCEL ICON --%>
                                <td>
                                    <img onclick="javascript:cancelLoadProfile(${pendingRequest.requestId});" src="${delete}" onmouseover="javascript:this.src='${deleteOver}'" onmouseout="javascript:this.src='${delete}'">
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                </td>
            </tr>
        </table>
    </c:when>
    <c:otherwise>
        <i:inline key=".none"/>
    </c:otherwise>
</c:choose>
</div>