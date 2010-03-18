<%@ attribute name="displayableProgramList" required="true" type="java.util.List" %>
<%@ attribute name="showControlHistorySummary" required="false" type="java.lang.Boolean" %>
<%@ attribute name="completeHistoryUrl" required="true" type="java.lang.String" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i18n"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<cti:msgScope paths=".controlHistorySummary, components.controlHistorySummary">
	<c:choose>
		<c:when test="${isNotEnrolled}">
			<span id="notEnrolledMessageSpan"> <i18n:inline key=".notEnrolledMessage" /></span>
		</c:when>
		<c:otherwise>
		
			<c:url var="help" value="/WebConfig/yukon/Icons/help.gif"/>
			<c:url var="helpOver" value="/WebConfig/yukon/Icons/help_over.gif"/>
			<cti:uniqueIdentifier var="uniqueId" prefix="helpInfoPopup_"/>
			<i18n:simplePopup id="${uniqueId}" titleKey=".helpInfoTitle">
			     <tags:nameValueContainer2 nameColumnWidth="20%">
				     <tags:nameValue2 nameKey=".helpInfoText.programBasedControlHistory">
					     <i18n:inline key=".helpInfoText.programBasedControlHistoryText" /> <br>
				     </tags:nameValue2>
				     <tags:nameValue2 nameKey=".helpInfoText.inventoryBasedControlHistory" >
				     	<i18n:inline key=".helpInfoText.inventoryBasedControlHistoryText" />
				     </tags:nameValue2>
			     </tags:nameValueContainer2>
			</i18n:simplePopup>

			<table cellspacing="0" class="miniResultsTable">
                <c:if test="${showControlHistorySummary}">
					<tr>
						<th colspan="3" width="70%"></th>
						<th width="10%" class="nonwrapping" style="text-align: center;">
						    <i18n:inline key=".day" />
							<a href="javascript:void(0);" onclick="$('${uniqueId}').toggle();">
								<img src="${help}" onmouseover="javascript:this.src='${helpOver}'" onmouseout="javascript:this.src='${help}'">
							</a>
						</th>
						<th width="10%" class="nonwrapping" style="text-align: center;">
						    <i18n:inline key=".month" />
						    <a href="javascript:void(0);" onclick="$('${uniqueId}').toggle();">
								<img src="${help}" onmouseover="javascript:this.src='${helpOver}'" onmouseout="javascript:this.src='${help}'">
							</a>
						</th>
						<th width="10%" class="nonwrapping" style="text-align: center;">
							<i18n:inline key=".year" />
							<a href="javascript:void(0);" onclick="$('${uniqueId}').toggle();">
								<img src="${help}" onmouseover="javascript:this.src='${helpOver}'" onmouseout="javascript:this.src='${help}'">
							</a>
						</th>
					</tr>
				</c:if>
	
				<c:forEach var="displayableProgram" items="${displayablePrograms}">
					<c:set var="program" value="${displayableProgram.program}" />
					<tr>
						<td class="tdHeader nonwrapping"  width="100%" colspan="3">
						    <span style="float: left;"><spring:escapeBody htmlEscape="true"><cti:msg key="${program.displayName}" /></spring:escapeBody></span>
							<span style="float: right; font-weight: normal;"><a href="${completeHistoryUrl}?programId=${program.programId}<c:if test="${not empty accountId}">&accountId=${accountId}</c:if><c:if test="${not empty energyCompanyId}">&energyCompanyId=${energyCompanyId}</c:if>"><i18n:inline key='.details' /></a></span>
						</td>
						<c:if test="${showControlHistorySummary}">
							<c:set var="programControlHistorySummary" value="${displayableProgram.displayableControlHistoryList[0].controlHistory.programControlHistorySummary}" />
							<td class="tdHeader nonwrapping">
								<c:if test="${programControlHistorySummary.dailySummary.millis != 0}">
									<cti:formatDuration type="HM_ABBR" value="${programControlHistorySummary.dailySummary.millis}" />
								</c:if>
							</td>
							<td class="tdHeader nonwrapping">
								<c:if test="${programControlHistorySummary.monthlySummary.millis != 0}">
									<cti:formatDuration type="HM_ABBR" value="${programControlHistorySummary.monthlySummary.millis}" />
								</c:if>
							</td>
							<td class="tdHeader nonwrapping">
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
						<tr style="font-size: .9em; white-space: nowrap;">
							<c:set var="imgRowSpan" value="${controlHistorySize}" />
							<c:if test="${imgRowSpan < 3}">
								<c:set var="imgRowSpan" value="${3}" />
							</c:if>
							
							<c:if test="${status.count eq 1}">
								<td valign="top" rowspan="${controlHistorySize}" width="5%">
									<img src="/WebConfig/${program.applianceCategoryLogo}">
								</td>
							</c:if>
							
			                <c:if test="${empty lastControlHistoryEntry or 
			                          not (lastControlHistoryEntry eq controlHistory.displayName)}">
								<c:choose>
									<c:when test="${displayableControlHistory.controlStatusDisplay or 
									              displayableControlHistory.deviceLabelControlStatusDisplay}">
										<td valign="top" width="5%">
											<spring:escapeBody htmlEscape="true">${controlHistory.displayName}</spring:escapeBody>
										</td>
										<td valign="top">
											<c:choose>
												<c:when test="${not empty controlHistory.lastControlHistoryEvent.endDate}">
													<cti:formatDate type="DATEHM" var="lastControledEndDate" value="${controlHistory.lastControlHistoryEvent.endDate}"/>
													<i18n:inline key="${controlHistory.currentStatus.formatKey}" arguments="${lastControledEndDate}" /> 
												</c:when>
												<c:otherwise>
													<i18n:inline key="${controlHistory.currentStatus.formatKey}" /> 
												</c:otherwise>
											</c:choose>
										</td>
									</c:when>
								</c:choose>
								
								<c:if test="${showControlHistorySummary}">
									<td valign="top">
										<c:if test="${controlHistorySummary.dailySummary.millis != 0}">
											<cti:formatDuration type="HM_ABBR" value="${controlHistorySummary.dailySummary.millis}" />
										</c:if>
									</td>
									<td valign="top">
										<c:if test="${controlHistorySummary.monthlySummary.millis != 0}">
											<cti:formatDuration type="HM_ABBR" value="${controlHistorySummary.monthlySummary.millis}" />
										</c:if>
									</td>
									<td valign="top">
										<c:if test="${controlHistorySummary.yearlySummary.millis != 0}">
											<cti:formatDuration type="HM_ABBR" value="${controlHistorySummary.yearlySummary.millis}" />
										</c:if>
									</td>
								</c:if>
								<c:set var="lastControlHistoryEntry" value="${controlHistory.displayName}" />
                            </c:if>
						</tr>
					</c:forEach>
				</c:forEach>
			</table>
		</c:otherwise>
	</c:choose>
</cti:msgScope>