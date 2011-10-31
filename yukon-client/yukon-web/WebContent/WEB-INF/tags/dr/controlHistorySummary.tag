<%@ attribute name="displayableProgramList" required="true" type="java.util.List" %>
<%@ attribute name="showControlHistorySummary" required="false" type="java.lang.Boolean" %>
<%@ attribute name="past" required="false" type="java.lang.Boolean" %>
<%@ attribute name="completeHistoryUrl" required="true" type="java.lang.String" %>
<%@ attribute name="titleKey" required="true" type="java.lang.String" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i18n"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<cti:msgScope paths=".controlHistorySummary, components.controlHistorySummary">
	<c:choose>
		<c:when test="${isNotEnrolled && not past}">
			<span id="notEnrolledMessageSpan"> <i18n:inline key=".notEnrolledMessage" /></span>
		</c:when>
		<c:otherwise>
		
            <!-- Help Info Popup -->
			<c:url var="help" value="/WebConfig/yukon/Icons/help.gif"/>
			<c:url var="helpOver" value="/WebConfig/yukon/Icons/help_over.gif"/>
			<cti:uniqueIdentifier var="uniqueId" prefix="helpInfoPopup_"/>
			<c:if test="${showControlHistorySummary}">
				<i18n:simplePopup id="${uniqueId}" titleKey=".helpInfoTitle">
				     <tags:nameValueContainer2>
					     <tags:nameValue2 nameKey=".helpInfoText.programBasedControlHistory">
						     <i18n:inline key=".helpInfoText.programBasedControlHistoryText" /> <br>
					     </tags:nameValue2>
					     <tags:nameValue2 nameKey=".helpInfoText.inventoryBasedControlHistory" >
					     	<i18n:inline key=".helpInfoText.inventoryBasedControlHistoryText" />
					     </tags:nameValue2>
				     </tags:nameValueContainer2>
				</i18n:simplePopup>
			</c:if>
			
			<table cellspacing="0" class="resultsTable controlHistorySummaryTable">
                 
                <c:if test="${showControlHistorySummary}">
					<tr>
						<th colspan="3" style="text-align: left;"><i18n:inline key="${titleKey}"/></th>
						<th>
						    <i18n:inline key=".day" />
							<a href="javascript:void(0);" onclick="$('${uniqueId}').toggle();">
								<img src="${help}" onmouseover="javascript:this.src='${helpOver}'" 
                                                   onmouseout="javascript:this.src='${help}'">
							</a>
						</th>
						<th>
						    <i18n:inline key=".month" />
						    <a href="javascript:void(0);" onclick="$('${uniqueId}').toggle();">
								<img src="${help}" onmouseover="javascript:this.src='${helpOver}'" 
                                                   onmouseout="javascript:this.src='${help}'">
							</a>
						</th>
						<th>
							<i18n:inline key=".year" />
							<a href="javascript:void(0);" onclick="$('${uniqueId}').toggle();">
								<img src="${help}" onmouseover="javascript:this.src='${helpOver}'" 
                                                   onmouseout="javascript:this.src='${help}'">
							</a>
						</th>
					</tr>
				</c:if>
	
				<c:forEach var="displayableProgram" items="${displayableProgramList}">
					<c:set var="program" value="${displayableProgram.program}" />
					<tr>
						<td colspan="3" class="programLabel">
						    <span class="programLabel"><spring:escapeBody htmlEscape="true"><cti:msg key="${program.displayName}" /></spring:escapeBody></span>
							<span class="detailsLink"><a href="${completeHistoryUrl}?programId=${program.programId}&past=${past}<c:if test="${not empty accountId}">&accountId=${accountId}</c:if>"><i18n:inline key='.details' /></a></span>
						</td>
						<c:if test="${showControlHistorySummary}">
							<c:set var="programControlHistorySummary" value="${displayableProgram.displayableControlHistoryList[0].controlHistory.programControlHistorySummary}" />
							<td class="programSummary">
								<c:if test="${programControlHistorySummary.dailySummary.millis != 0}">
									<cti:formatDuration type="HM_ABBR" value="${programControlHistorySummary.dailySummary.millis}" />
								</c:if>
							</td>
							<td class="programSummary">
								<c:if test="${programControlHistorySummary.monthlySummary.millis != 0}">
									<cti:formatDuration type="HM_ABBR" value="${programControlHistorySummary.monthlySummary.millis}" />
								</c:if>
							</td>
							<td class="programSummary">
								<c:if test="${programControlHistorySummary.yearlySummary.millis != 0}">
									<cti:formatDuration type="HM_ABBR" value="${programControlHistorySummary.yearlySummary.millis}" />
								</c:if>
							</td>
						</c:if>
					</tr>
					
					<c:set var="controlHistorySize" value="${fn:length(displayableProgram.displayableControlHistoryList)}" />
					<c:forEach var="displayableControlHistory" items="${displayableProgram.displayableControlHistoryList}" varStatus="status">
						<c:set var="controlHistory" value="${displayableControlHistory.controlHistory}" />
						<c:set var="controlHistorySummary" value="${controlHistory.controlHistorySummary}" />
						<tr>
							<c:set var="imgRowSpan" value="${controlHistorySize}" />
							<c:if test="${imgRowSpan < 3}">
								<c:set var="imgRowSpan" value="${3}" />
							</c:if>
							
							<c:if test="${status.count eq 1}">
								<td rowspan="${controlHistorySize}" class="programIcon">
									<img src="/WebConfig/${program.applianceCategoryLogo}">
								</td>
							</c:if>
						
							<c:choose>
								<c:when test="${displayableControlHistory.controlStatusDisplay or 
								              displayableControlHistory.deviceLabelControlStatusDisplay}">
									<td class="inventoryLabel">
                                        <c:choose>
                                            <c:when test="${not empty controlHistory.displayName}">
                                                <spring:escapeBody htmlEscape="true">${controlHistory.displayName}</spring:escapeBody>
                                            </c:when>
                                            <c:otherwise>
                                                <i18n:inline key=".deviceRemoved"/>
                                            </c:otherwise>
                                        </c:choose>
									</td>
									<td>
										<c:choose>
											<c:when test="${not empty controlHistory.lastControlHistoryEvent.endDate}">
												<cti:formatDate type="DATEHM" var="lastControlledEndDate" value="${controlHistory.lastControlHistoryEvent.endDate}"/>
												<i18n:inline key="${controlHistory.currentStatus.formatKey}" arguments="${lastControlledEndDate}" /> 
											</c:when>
											<c:otherwise>
												<i18n:inline key="${controlHistory.currentStatus.formatKey}" /> 
											</c:otherwise>
										</c:choose>
									</td>
								</c:when>
							</c:choose>
							
							<c:if test="${showControlHistorySummary}">
								<td class="inventorySummary">
									<c:if test="${controlHistorySummary.dailySummary.millis != 0}">
										<cti:formatDuration type="HM_ABBR" value="${controlHistorySummary.dailySummary.millis}" />
									</c:if>
								</td>
								<td class="inventorySummary">
									<c:if test="${controlHistorySummary.monthlySummary.millis != 0}">
										<cti:formatDuration type="HM_ABBR" value="${controlHistorySummary.monthlySummary.millis}" />
									</c:if>
								</td>
								<td class="inventorySummary">
									<c:if test="${controlHistorySummary.yearlySummary.millis != 0}">
										<cti:formatDuration type="HM_ABBR" value="${controlHistorySummary.yearlySummary.millis}" />
									</c:if>
								</td>
							</c:if>
						</tr>
					</c:forEach>
				</c:forEach>
			</table>
		</c:otherwise>
	</c:choose>
</cti:msgScope>