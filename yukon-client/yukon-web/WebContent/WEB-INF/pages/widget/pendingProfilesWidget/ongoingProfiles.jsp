<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

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
                                                <td class="label">Channel:</td>
                                                <td>${pendingRequest.channel}</td>
                                            </tr>
                                            <tr>
                                                <td class="label">Requested By:</td>
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
                                    <img src="/WebConfig/yukon/Icons/action_stop.gif" onclick="javascript:cancelLoadProfile(${pendingRequest.requestId});">
                                </td>

                            </tr>

                        </c:forEach>
                    </table>
                </td>
            </tr>
        </table>
    </c:when>
    
    <c:otherwise>
        None
    </c:otherwise>
    
</c:choose>

</div>