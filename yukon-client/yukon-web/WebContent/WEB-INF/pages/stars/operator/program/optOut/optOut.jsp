<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>

<cti:standardPage module="operator" page="optOut.main">

<!-- Helper Popup -->
<c:url var="help" value="/WebConfig/yukon/Icons/help.gif"/>
<c:url var="helpOver" value="/WebConfig/yukon/Icons/help_over.gif"/>
<cti:uniqueIdentifier var="uniqueId" prefix="helpInfoPopup_"/>
<i:simplePopup id="${uniqueId}" titleKey=".helpInfoTitle">
    <table >
        <tr><td>
            <cti:img key="allowOne"/><i:inline key=".allowOneIconText" />
        </td></tr>
        <tr height="6px"></tr>
        <tr><td>
            <cti:img key="resetToLimit"/><i:inline key=".resetToLimitIconText" />
        </td></tr>
    </table>
</i:simplePopup>

<!-- Create an opt out -->
<tags:boxContainer2 nameKey="optOuts">
<c:if test="${!empty currentOptOutList && allOptedOut}">
    <i:inline key=".allOptedOut"/>
</c:if>
<c:if test="${!empty currentOptOutList && !optOutsAvailable}">
    <i:inline key=".noOptOutsAvailable"/>
</c:if>

<c:if test="${!allOptedOut}">
	<i:inline key=".description"/><br><br>

    <form:form id="optOutForm" action="/spring/stars/operator/program/optOut/view2" commandName="optOutBackingBean">

		<input type="hidden" name="accountId" value="${accountId}" />

		<tags:nameValueContainer2>
		
            <tags:nameValue2 nameKey=".startDate">
                <cti:checkRolesAndProperties value="OPERATOR_OPT_OUT_TODAY_ONLY">
                    <cti:formatDate  value="${optOutBackingBean.startDate}" type="DATE" var="formattedDate"/>
                    <spring:escapeBody htmlEscape="true">${formattedDate}</spring:escapeBody>
                </cti:checkRolesAndProperties>
                <cti:checkRolesAndProperties value="!OPERATOR_OPT_OUT_TODAY_ONLY">
                    <tags:dateInputCalendar fieldName="startDate" fieldValue="${formattedDate}" springInput="true" />
                </cti:checkRolesAndProperties>
            </tags:nameValue2>
		
			<tags:nameValue2 nameKey=".duration">
				<select name="durationInDays">
                    <c:forEach var="optOutPeriod" items="${optOutPeriodList}">
                       <option value="${optOutPeriod}">
                           <i:inline key=".optOutDays" arguments="${optOutPeriod}" />
                       </option>
                    </c:forEach>
                </select>
			</tags:nameValue2>
		</tags:nameValueContainer2>
        <br>
        <input type="submit" value="<cti:msg2 key=".optOut"/>" class="formSubmit">
	</form:form>
</c:if>
</tags:boxContainer2>
<br><br>

<!-- Current Opt Outs -->
<tags:boxContainer2 nameKey="currentOptOuts">
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
                <tr class="<tags:alternateRow odd="altRow" even=""/>">
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
                            <cti:param name="accountId" value="${accountId}"/>
                            <cti:param name="eventId" value="${optOut.eventId}" />
                        </cti:url>
                        <i:simpleLink actionUrl="${cancelOptOutUrl}" 
                                      logoKey="cancelOptOut"/>

                        <c:choose>
                            <c:when test="${optOut.state == 'START_OPT_OUT_SENT'}">
                                <cti:url var="resendOptOutUrl" value="/spring/stars/operator/program/optOut/repeat">
                                    <cti:param name="accountId" value="${accountId}"/>
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
<tags:alternateRowReset/>
<tags:boxContainer2 nameKey="optOutLimits">
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
        <tr class="<tags:alternateRow odd="altRow" even=""/>">
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
                        <cti:param name="accountId" value="${accountId}"/>
                        <cti:param name="inventoryId" value="${inventory.inventoryId}" />
                    </cti:url>

                    <i:simpleLink actionUrl="${allowAnotherUrl}" logoKey="allowOne"/>
                    <c:choose>
	                    <c:when test="${optOutLimit <= optOutCounts[inventory.inventoryId].remainingOptOuts}">
                            <cti:img key="resetToLimitDisabled"/>
	                    </c:when>
	                    <c:otherwise> 

                            <cti:url var="resetToLimitUrl" value="/spring/stars/operator/program/optOut/resetToLimit">
                                <cti:param name="accountId" value="${accountId}"/>
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
<tags:alternateRowReset/>
<tags:boxContainer2 nameKey="optOutHistory">
	<dr:optOutHistory previousOptOutList="${previousOptOutList}" />
	
	<c:if test="${fn:length(previousOptOutList) > 0}">
        <cti:url var="optOutHistoryUrl" value="/spring/stars/operator/program/optOut/optOutHistory">
            <cti:param name="accountId" value="${accountId}"/>
        </cti:url>
        <a href="${optOutHistoryUrl}" ><cti:msg key="yukon.dr.operator.optout.viewAll" /></a>
        <br><br>
    </c:if>
</tags:boxContainer2>
</cti:standardPage>