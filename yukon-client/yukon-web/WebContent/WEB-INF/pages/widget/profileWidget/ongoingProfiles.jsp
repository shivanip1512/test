<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>


<c:if test="${! empty dateErrorMessage}">
    <br/>
    <div style="font-weight:bold;color:#BB0000">${dateErrorMessage}</div>
    <br/>
</c:if>

<%-- ONGOING PROFILES --%>
<c:choose>

	<c:when test="${empty pendingRequests}">
		<div class="compactResultTableDescription">Pending Requests: </div>None
	</c:when>

	<c:when test="${not empty pendingRequests}">
	
		<table class="compactResultsTable">
		
			<tr><th>Pending Requests:</th></tr>
			
			<tr>
				<td>
					<table>
						<c:forEach var="pendingRequest" items="${pendingRequests}">

							<tr valign="top" class="<tags:alternateRow odd="" even="altRow"/>">
								
								<%-- MORE INFO HIDE REVEAL --%>
								<td>
									<tags:hideReveal
										title="${pendingRequest.from} - ${pendingRequest.to}"
										showInitially="false">

										<table class="compactResultsTable" style="margin-left:20px">
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
									</tags:hideReveal>
								</td>
								
								<%-- STATUS BAR --%>
								<td>
									<div id="profileStatusBar${pendingRequest.requestId}" style="vertical-align: top"></div>

									<script type="text/javascript">
										progressUpdaters[${pendingRequest.requestId}] = new Ajax.PeriodicalUpdater('profileStatusBar${pendingRequest.requestId}', '/spring/widget/profileWidget/percentDoneProgressBarHTML?requestId=${pendingRequest.requestId}', {method: 'post', frequency: 2});
									</script>

								</td>
								
								<%-- CANCEL ICON --%>
								<td
									onClick="javascript:progressUpdaters[${pendingRequest.requestId}].stop();">
									<tags:widgetActionRefreshImage method="cancelLoadProfile"
										requestId="${pendingRequest.requestId}" title="Cancel"
										imgSrc="/WebConfig/yukon/Icons/action_stop.gif"
										imgSrcHover="/WebConfig/yukon/Icons/action_stop.gif" />
								</td>

							</tr>

						</c:forEach>
					</table>
				</td>
			</tr>
		</table>
	</c:when>
</c:choose>




