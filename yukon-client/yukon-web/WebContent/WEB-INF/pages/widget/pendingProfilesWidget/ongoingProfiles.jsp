<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%-- THE LIST OF ONGOING/PENDING PROFILE REQUESTS --%>
<cti:msg2 key=".cancel.profile.request.tooltip" var="cancelRequest"/>
<cti:msg2 key=".acknowledge.profile.request.completion.tooltip" var="ackRequest"/>
<div id="${divId}">
<c:choose>
    <c:when test="${not empty pendingRequests}">
        <div class="separated-sections">
            <c:forEach var="pendingRequest" items="${pendingRequests}">
                <div class="section">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey="yukon.common.range">${pendingRequest.from}&nbsp;-&nbsp;${pendingRequest.to}</tags:nameValue2>
                        <c:if test="${ not isRfn}">
                        <tags:nameValue2 nameKey=".channel">${pendingRequest.channel}</tags:nameValue2>
                        </c:if>
                        <tags:nameValue2 nameKey=".requestedBy">${pendingRequest.userName}</tags:nameValue2>
                        <tags:nameValue2 nameKey="yukon.common.email">${pendingRequest.email}</tags:nameValue2>
                        <tags:nameValue2 nameKey="yukon.common.status">
                            <table>
                                <tr>
                                    <c:if test="${not isRfn}">
                                        <td><tags:pendingProfileProgressBar requestId="${pendingRequest.requestId}" percentDone="${pendingRequest.percentDone}" lastReturnMsg="${lastReturnMsg}"/></td>
                                    </c:if>
                                    <c:if test="${isRfn}">
                                        <td>
	                                        <c:if test="${empty pendingRequest.percentDone}">
	                                            <div class="error"><i:inline key="yukon.common.requestFailed"/></div><div>${pageScope.lastReturnMsg}</div><div class="error">${pendingRequest.requestFailureMessage}</div>
	                                        </c:if>
	                                        <c:if test="${pendingRequest.percentDone >= 100.0}">
	                                           <div class="success"><i:inline key="yukon.common.completed"/></div>
	                                        </c:if>
	                                        <c:if test="${pendingRequest.percentDone <= 0.0}">
	                                            <div><i:inline key="yukon.common.inProgress"/></div>
	                                        </c:if>
                                        </td>
                                     </c:if>
                                     <td>
                                        <c:choose>
                                            <c:when test="${pendingRequest.percentDone < 100.0}">
	                                            <td><cti:button title="${cancelRequest}" onclick="javascript:cancelLoadProfileOrAcknowledgeResults(${pendingRequest.requestId});" icon="icon-cross" renderMode="buttonImage"/></td>
                                            </c:when>
                                            <c:otherwise>
                                                <td><cti:button title="${ackRequest}" onclick="javascript:cancelLoadProfileOrAcknowledgeResults(${pendingRequest.requestId});" icon="icon-tick" renderMode="buttonImage"/></td>
                                            </c:otherwise>
                                        </c:choose>   
                                    </td>
                                </tr>
                            </table>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </div>
            </c:forEach>
        </div>
    </c:when>
    <c:otherwise>
        <span class="empty-list"><i:inline key="yukon.common.none.choice"/></span>
    </c:otherwise>
</c:choose>
</div>