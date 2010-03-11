<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>

<cti:standardPage module="operator" page="optOut">
    <cti:standardMenu />

<!-- Helper Popup -->
<c:url var="help" value="/WebConfig/yukon/Icons/help.gif"/>
<c:url var="helpOver" value="/WebConfig/yukon/Icons/help_over.gif"/>
<cti:uniqueIdentifier var="uniqueId" prefix="helpInfoPopup_"/>
<i:simplePopup id="${uniqueId}" titleKey=".helpInfoTitle">
    <table >
        <tr style="height: 10%"><td>
            <cti:img key="allowOne"/><i:inline key=".allowOneIconText" />
        </td></tr>
        <tr height="6px"></tr>
        <tr style="height: 10%"><td>
            <cti:img key="resetToLimit"/><i:inline key=".resetToLimitIconText" />
        </td></tr>
    </table>
</i:simplePopup>

<!-- Create an opt out -->
<tags:boxContainer2 key="optOuts">
<c:if test="${!empty currentOptOutList && allOptedOut}">
    <i:inline key=".allOptedOut"/>
</c:if>
<c:if test="${!empty currentOptOutList && !optOutsAvailable}">
    <i:inline key=".noOptOutsAvailable"/>
</c:if>

<c:if test="${!allOptedOut}">
	<i:inline key=".description"/><br><br>

	<form action="/spring/stars/operator/program/optOut/view2" method="POST">
		<input type="hidden" name="accountId" value="${customerAccount.accountId}" />
		<input type="hidden" name="energyCompanyId" value="${energyCompanyId}" />

		<tags:nameValueContainer style="width: 25%;">
		
			<tags:nameValue2 nameKey=".startDate">
				<cti:formatDate  value="${currentDate}" type="DATE" var="formattedDate"/>
                <c:choose>
                    <c:when test="${optOutTodayOnly}">
                        <input type="hidden" name="startDate" value="${formattedDate}" />
                        <spring:escapeBody htmlEscape="true">${formattedDate}</spring:escapeBody>
                    </c:when>
                    <c:otherwise>
                        <tags:dateInputCalendar fieldName="startDate" fieldValue="${formattedDate}"/>
                    </c:otherwise>
                </c:choose>
			</tags:nameValue2>
		
			<tags:nameValue2 nameKey=".duration">
				<select name="durationInDays">
                    <c:forEach var="optOutPeriod" items="${optOutPeriodList}">
                       <option value="${optOutPeriod}">
                           <spring:escapeBody htmlEscape="true">${optOutPeriod}</spring:escapeBody>
                           <c:choose>
                           	   <c:when test="${optOutPeriod == 1}">
                           	       <i:inline key=".day" />
                           	   </c:when>
                           	   <c:otherwise>
                           	       <i:inline key=".days" />
                           	   </c:otherwise>
                           </c:choose>
                       </option>
                    </c:forEach>
                </select>
			</tags:nameValue2>
		</tags:nameValueContainer>
        <br>
        <input type="submit" value="<i:inline key=".optOut" />" />
	</form>
</c:if>
</tags:boxContainer2>
<br><br>

<!-- Current Opt Outs -->
<tags:boxContainer2 key="currentOptOuts">
<c:choose>
    <c:when test="${fn:length(currentOptOutList) > 0}">
        <table id="deviceTable" class="compactResultsTable">
            <tr class="<tags:alternateRow odd="" even="altRow"/>">
                <th><i:inline key=".device"/></th>
                <th><i:inline key=".program"/></th>
                <th><i:inline key=".status"/></th>
                <th><i:inline key=".dateActive"/></th>
                <th class="nonwrapping"><i:inline key=".actions"/></th>
            </tr>
            
            <c:forEach var="optOut" items="${currentOptOutList}">
                <tr class="<tags:alternateRow odd="" even="altRow"/>">
                    <td valign="top"><spring:escapeBody htmlEscape="true">${optOut.inventory.displayName}</spring:escapeBody></td>
                    <td valign="top">
                        <c:forEach var="program" items="${optOut.programList}" varStatus="status">
                            <c:if test="${status.count != 1}">, </c:if>
                            <spring:escapeBody htmlEscape="true">${program.programName}</spring:escapeBody>
                        </c:forEach>
                    </td>
                    <td valign="top" 
                        <c:choose>
                    	    <c:when test="${optOut.state eq 'START_OPT_OUT_SENT'}">class="okGreen"</c:when>
                    	    <c:when test="${optOut.state eq 'CANCEL_SENT'}">class="subtleGray"</c:when>
                    	    <c:otherwise>class=""</c:otherwise>
                        </c:choose>
                    >
                        <cti:msg key="${optOut.state.formatKey}"/>
                    </td>
                    <td valign="top" class="nonwrapping">
                        <cti:formatDate value="${optOut.startDate}" type="DATEHM"/>
                    </td>
                    <td valign="top">

                        <cti:url var="cancelOptOutUrl" value="/spring/stars/operator/program/optOut/cancel">
                            <cti:param name="accountId" value="${customerAccount.accountId}"/>
                            <cti:param name="energyCompanyId" value="${energyCompanyId}" />
                            <cti:param name="eventId" value="${optOut.eventId}" />
                        </cti:url>
                        <i:simpleLink actionUrl="${cancelOptOutUrl}" 
                                      logoKey="cancelOptOut"/>

                        <c:choose>
                            <c:when test="${optOut.state == 'START_OPT_OUT_SENT'}">
                                <cti:url var="resendOptOutUrl" value="/spring/stars/operator/program/optOut/repeat">
                                    <cti:param name="accountId" value="${customerAccount.accountId}"/>
                                    <cti:param name="energyCompanyId" value="${energyCompanyId}" />
                                    <cti:param name="inventoryId" value="${optOut.inventory.inventoryId}" />
                                    <cti:param name="eventId" value="${optOut.eventId}" />
                                </cti:url>

                                <i:simpleLink actionUrl="${resendOptOutUrl}" 
                                              logoKey="resendOptOut"/>
                            </c:when>
                            <c:otherwise>
                                <cti:img key="resendOptOutDisabled"/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:when>
    <c:otherwise>
        <br><i:inline key=".noCurrentOptOuts"/>
    </c:otherwise>
</c:choose>
</tags:boxContainer2>
<br><br>

<!-- Opt Out Limits -->
<tags:boxContainer2 styleClass="boxContainer50percent" key="optOutLimits">
<table id="deviceTable" class="compactResultsTable">
    <tr class="<tags:alternateRow odd="" even="altRow"/>">
        <th><i:inline key=".device"/></th>
        <th><i:inline key=".used"/></th>
        <th><i:inline key=".remaining"/></th>
        <c:if test="${!noOptOutLimits}">
            <th><i:inline key=".actions"/>
                <a href="javascript:void(0);" onclick="$('${uniqueId}').toggle();">
                    <img src="${help}" onmouseover="javascript:this.src='${helpOver}'" onmouseout="javascript:this.src='${help}'">
                </a>
            </th>
        </c:if>
        
    </tr>
    
    <c:forEach var="inventory" items="${displayableInventories}">
        <tr class="<tags:alternateRow odd="" even="altRow"/>">
            <td><spring:escapeBody htmlEscape="true">${inventory.displayName}</spring:escapeBody></td>
            <td>${optOutCounts[inventory.inventoryId].usedOptOuts}</td>
            <td>
                <c:choose>
                    <c:when test="${noOptOutLimits}">
                        <i:inline key=".unlimitedRemaining"/>
                    </c:when>
                    <c:otherwise>
                        ${optOutCounts[inventory.inventoryId].remainingOptOuts}
                    </c:otherwise>
                </c:choose>
            </td>
	        
            <td>
                <c:if test="${!noOptOutLimits}">
            
                    <cti:url var="allowAnotherUrl" value="/spring/stars/operator/program/optOut/allowAnother">
                        <cti:param name="accountId" value="${customerAccount.accountId}"/>
                        <cti:param name="energyCompanyId" value="${energyCompanyId}" />
                        <cti:param name="inventoryId" value="${inventory.inventoryId}" />
                    </cti:url>

                    <i:simpleLink actionUrl="${allowAnotherUrl}" logoKey="allowOne"/>
                    <c:choose>
	                    <c:when test="${optOutLimit <= optOutCounts[inventory.inventoryId].remainingOptOuts}">
                            <cti:img key="resetToLimitDisabled"/>
	                    </c:when>
	                    <c:otherwise> 

                            <cti:url var="resetToLimitUrl" value="/spring/stars/operator/program/optOut/resetToLimit">
                                <cti:param name="accountId" value="${customerAccount.accountId}"/>
                                <cti:param name="energyCompanyId" value="${energyCompanyId}" />
                                <cti:param name="inventoryId" value="${inventory.inventoryId}" />
                            </cti:url>

                            <i:simpleLink actionUrl="${resetToLimitUrl}" logoKey="resetToLimit"/>
  	                   </c:otherwise>
	                </c:choose>
                                     
                </c:if>
            </td>
	        
        </tr>
    </c:forEach>
</table>
</tags:boxContainer2>
<br><br>

<!-- Opt Out History -->
<tags:boxContainer2 key="optOutHistory">
	<dr:optOutHistory previousOptOutList="${previousOptOutList}" />
	
	<c:if test="${fn:length(previousOptOutList) > 0}">
        <a href="optOut/optOutHistory?accountId=${customerAccount.accountId}&energyCompanyId=${energyCompanyId}" ><cti:msg key="yukon.dr.operator.optout.viewAll" /></a>
        <br><br>
    </c:if>
</tags:boxContainer2>
</cti:standardPage>