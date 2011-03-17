<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>

<cti:standardPage module="operator" page="optOut.main">

<script type="text/javascript">

YEvent.observeSelectorClick('img.cancel', function(event) {
    var confirmCancelPopup = $('confirmCancel');
    var theRow = Event.findElement(event, 'tr');
    var confirmMsg = theRow.down('span.confirmCancelMessage').innerHTML;
    var eventId = Event.element(event).getAttribute('name');
    confirmCancelPopup.down("input[name='eventId']").value = eventId;
    $('cancelConfirmMessage').innerHTML = confirmMsg;
    confirmCancelPopup.show();
});

YEvent.observeSelectorClick('img.resend', function(event) {
    var confirmResendPopup = $('confirmResend');
    var theRow = Event.findElement(event, 'tr');
    var confirmMsg = theRow.down('span.confirmResendMessage').innerHTML;
    var inventoryId = Event.element(event).getAttribute('name');
    confirmResendPopup.down("input[name='inventoryId']").value = inventoryId;
    $('resendConfirmMessage').innerHTML = confirmMsg;
    confirmResendPopup.show();
});

YEvent.observeSelectorClick('img.allowOne', function(event) {
    var confirmAllowOnePopup = $('confirmAllowOne');
    var theRow = Event.findElement(event, 'tr');
    var confirmMsg = theRow.down('span.confirmAllowOneMessage').innerHTML;
    var inventoryId = Event.element(event).getAttribute('name');
    confirmAllowOnePopup.down("input[name='inventoryId']").value = inventoryId;
    $('allowOneConfirmMessage').innerHTML = confirmMsg;
    confirmAllowOnePopup.show();
});

YEvent.observeSelectorClick('img.decrementAllowance', function(event) {
    var confirmDecrementAllowancePopup = $('confirmDecrementAllowance');
    var theRow = Event.findElement(event, 'tr');
    var confirmMsg = theRow.down('span.confirmDecrementAllowanceMessage').innerHTML;
    var inventoryId = Event.element(event).getAttribute('name');
    confirmDecrementAllowancePopup.down("input[name='inventoryId']").value = inventoryId;
    $('decrementAllowanceConfirmMessage').innerHTML = confirmMsg;
    confirmDecrementAllowancePopup.show();
});

YEvent.observeSelectorClick('img.resetToLimit', function(event) {
    var confirmResetToLimitPopup = $('confirmResetToLimit');
    var theRow = Event.findElement(event, 'tr');
    var confirmMsg = theRow.down('span.confirmResetToLimitMessage').innerHTML;
    var inventoryId = Event.element(event).getAttribute('name');
    confirmResetToLimitPopup.down("input[name='inventoryId']").value = inventoryId;
    $('resetToLimitConfirmMessage').innerHTML = confirmMsg;
    confirmResetToLimitPopup.show();
});

</script>

<!-- Cancel Opt Out Confirmation -->
<i:simplePopup id="confirmCancel" titleKey=".cancelOptOut.confirmTitle" styleClass="mediumSimplePopup">
    <div id="cancelConfirmMessage"></div>
    <form action="/spring/stars/operator/program/optOut/cancelOptOut">
        <input type="hidden" name="accountId" value="${accountId}"/>
        <input type="hidden" name="eventId"/>
    
        <div class="actionArea">
            <cti:button type="submit" key="ok" /> 
            <cti:button type="button" key="cancel" onclick="$('confirmCancel').hide()"/>
        </div>
    </form>
</i:simplePopup>

<!-- Resend Opt Out Confirmation -->
<i:simplePopup id="confirmResend" titleKey=".resendOptOut.confirmTitle" styleClass="mediumSimplePopup">
    <div id="resendConfirmMessage"></div>
    <form action="/spring/stars/operator/program/optOut/resend">
        <input type="hidden" name="accountId" value="${accountId}"/>
        <input type="hidden" name="inventoryId"/>
    
        <div class="actionArea">
            <cti:button type="submit" key="ok" /> 
            <cti:button type="button" key="cancel" onclick="$('confirmResend').hide()"/>
        </div>
    </form>
</i:simplePopup>

<!-- Allow One Opt Out Confirmation -->
<i:simplePopup id="confirmAllowOne" titleKey=".allowOne.confirmTitle" styleClass="mediumSimplePopup">
    <div id="allowOneConfirmMessage"></div>
    <form action="/spring/stars/operator/program/optOut/allowAnother">
        <input type="hidden" name="accountId" value="${accountId}"/>
        <input type="hidden" name="inventoryId"/>
    
        <div class="actionArea">
            <cti:button type="submit" key="ok" /> 
            <cti:button type="button" key="cancel" onclick="$('confirmAllowOne').hide()"/>
        </div>
    </form>
</i:simplePopup>

<!-- Decrement Opt Out Allowance Confirmation -->
<i:simplePopup id="confirmDecrementAllowance" titleKey=".decrementAllowance.confirmTitle" styleClass="mediumSimplePopup">
    <div id="decrementAllowanceConfirmMessage"></div>
    <form action="/spring/stars/operator/program/optOut/decrementAllowances">
        <input type="hidden" name="accountId" value="${accountId}"/>
        <input type="hidden" name="inventoryId"/>
    
        <div class="actionArea">
            <cti:button type="submit" key="ok" /> 
            <cti:button type="button" key="cancel" onclick="$('confirmDecrementAllowance').hide()"/>
        </div>
    </form>
</i:simplePopup>

<!-- Reset To Limit Confirmation -->
<i:simplePopup id="confirmResetToLimit" titleKey=".resetToLimit.confirmTitle" styleClass="mediumSimplePopup">
    <div id="resetToLimitConfirmMessage"></div>
    <form action="/spring/stars/operator/program/optOut/resetToLimit">
        <input type="hidden" name="accountId" value="${accountId}"/>
        <input type="hidden" name="inventoryId"/>
    
        <div class="actionArea">
            <cti:button type="submit" key="ok" /> 
            <cti:button type="button" key="cancel" onclick="$('confirmResetToLimit').hide()"/>
        </div>
    </form>
</i:simplePopup>

<!-- Helper Popup -->
<c:url var="help" value="/WebConfig/yukon/Icons/help.gif"/>
<c:url var="helpOver" value="/WebConfig/yukon/Icons/help_over.gif"/>
<cti:uniqueIdentifier var="uniqueId" prefix="helpInfoPopup_"/>
<i:simplePopup id="${uniqueId}" titleKey=".helpInfoTitle">
    <table>
        <tr><td>
            <cti:img key="allowOne"/>
        </td><td>
            <i:inline key=".allowOneIconText" />
        </td></tr>
        <tr height="6px"></tr>
        <tr><td>
            <cti:img key="resetToLimit"/>
        </td><td>
            <i:inline key=".resetToLimitIconText" />
        </td></tr>
    </table>
</i:simplePopup>

<!-- Create an opt out -->
<tags:boxContainer2 nameKey="optOuts">
    <c:if test="${!empty currentOptOutList && !optOutsAvailable}">
        <i:inline key=".noOptOutsAvailable"/>
        <br/>
    </c:if>
    
    <cti:checkRolesAndProperties value="!OPERATOR_OPT_OUT_TODAY_ONLY">
        <i:inline key=".description"/><br><br>
    </cti:checkRolesAndProperties>
    
    <form:form id="optOutForm" action="/spring/stars/operator/program/optOut/deviceSelection" commandName="optOutBackingBean">
    
    	<input type="hidden" name="accountId" value="${accountId}" />
    
    	<tags:nameValueContainer2>
    	
            <tags:nameValue2 nameKey=".startDate">
                <cti:checkRolesAndProperties value="OPERATOR_OPT_OUT_TODAY_ONLY">
                    <cti:formatDate  value="${optOutBackingBean.startDate}" type="DATE" 
                                     var="formattedDate"/>
                    <spring:escapeBody htmlEscape="true">${formattedDate}</spring:escapeBody>
                </cti:checkRolesAndProperties>
                <cti:checkRolesAndProperties value="!OPERATOR_OPT_OUT_TODAY_ONLY">
                    <tags:dateInputCalendar fieldName="startDate" fieldValue="${formattedDate}" 
                                            springInput="true" />
                </cti:checkRolesAndProperties>
            </tags:nameValue2>
    	
    		<tags:nameValue2 nameKey=".duration">
                <c:choose>
                    <c:when test="${fn:length(optOutPeriodList) > 1}">
        				<select name="durationInDays">
                            <c:forEach var="optOutPeriod" items="${optOutPeriodList}">
                               <option value="${optOutPeriod}">
                                   <i:inline key=".optOutDays" arguments="${optOutPeriod}" />
                               </option>
                            </c:forEach>
                        </select>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="optOutPeriod" items="${optOutPeriodList}">
                            <input type="hidden" name="durationInDays" value="${optOutPeriod}" />
                            <i:inline key=".optOutDays" arguments="${optOutPeriod}" />
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            
            </tags:nameValue2>
    	</tags:nameValueContainer2>
        <br>
        <input type="submit" value="<cti:msg2 key=".optOut"/>" class="formSubmit">
    </form:form>
</tags:boxContainer2>
<br><br>

<!-- Current Opt Outs -->
<tags:boxContainer2 nameKey="currentOptOuts">
<c:choose>
    <c:when test="${fn:length(currentOptOutList) > 0}">
        <table id="deviceTable" class="compactResultsTable rowHighlighting">
            <tr class="<tags:alternateRow odd="" even="altRow"/>">
                <th><i:inline key=".device"/></th>
                <th><i:inline key=".program"/></th>
                <th><i:inline key=".status"/></th>
                <th><i:inline key=".dateActive"/></th>
                <th class="nonwrapping"><i:inline key=".actions"/></th>
            </tr>
            
            <c:forEach var="optOut" items="${currentOptOutList}">
                <tr class="<tags:alternateRow odd="altRow" even=""/>">
                    <td valign="top">
                        <spring:escapeBody htmlEscape="true">${optOut.inventory.displayName}</spring:escapeBody>
                    </td>
                    <td valign="top">
                        <c:forEach var="program" items="${optOut.programList}" varStatus="status">
                            <c:if test="${status.count != 1}"><br></c:if>
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
                        <cti:img key="cancelOptOut" styleClass="cancel hoverableImage" name="${optOut.eventId}"/>
                        <span class="confirmCancelMessage" style="display:none;">
                            <i:inline key=".cancelOptOut.confirm" arguments="${optOut.inventory.displayName}"/>
                        </span>

                        <c:choose>
                            <c:when test="${optOut.state == 'START_OPT_OUT_SENT'}">
                                <cti:img key="resendOptOut" styleClass="resend hoverableImage" name="${optOut.inventory.inventoryId}"/>
                                <span class="confirmResendMessage" style="display:none;">
                                    <i:inline key=".resendOptOut.confirm" arguments="${optOut.inventory.displayName}"/>
                                </span>
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
        <i:inline key=".noCurrentOptOuts"/>
    </c:otherwise>
</c:choose>
</tags:boxContainer2>
<br><br>

<!-- Opt Out Limits -->
<tags:alternateRowReset/>
<tags:boxContainer2 nameKey="optOutLimits">
<c:choose>
    <c:when test="${fn:length(displayableInventories) > 0}">
        <table id="deviceTable" class="compactResultsTable rowHighlighting">
            <tr class="<tags:alternateRow odd="" even="altRow"/>">
                <th><i:inline key=".device"/></th>
                <th><i:inline key=".used"/></th>
                <th><i:inline key=".remaining"/></th>
                <c:if test="${!noOptOutLimits}">
                    <th><i:inline key=".actions"/>
                        <a href="javascript:void(0);" onclick="$('${uniqueId}').toggle();">
                            <cti:img key="help" styleClass="hoverableImage"/>
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
                            <cti:img key="allowOne" styleClass="allowOne hoverableImage" name="${inventory.inventoryId}"/>
                            <span class="confirmAllowOneMessage" style="display:none;">
                                <i:inline key=".allowOne.confirm" arguments="${inventory.displayName}"/>
                            </span>

                            <c:if test="${optOutCounts[inventory.inventoryId].remainingOptOuts > 0}">
                                <cti:img key="decrementAllowance" styleClass="decrementAllowance hoverableImage" name="${inventory.inventoryId}"/>
                                <span class="confirmDecrementAllowanceMessage" style="display:none;">
                                    <i:inline key=".decrementAllowance.confirm" arguments="${inventory.displayName}"/>
                                </span>
                            </c:if>
                            <c:if test="${optOutCounts[inventory.inventoryId].remainingOptOuts <= 0}">
                                <cti:img key="decrementAllowanceDisabled"/>
                            </c:if>

                            <c:choose>
        	                    <c:when test="${optOutLimit <= optOutCounts[inventory.inventoryId].remainingOptOuts}">
                                    <cti:img key="resetToLimitDisabled"/>
        	                    </c:when>
        	                    <c:otherwise>
                                    <cti:img key="resetToLimit" styleClass="resetToLimit hoverableImage" name="${inventory.inventoryId}"/>
                                    <span class="confirmResetToLimitMessage" style="display:none;">
                                        <i:inline key=".resetToLimit.confirm" arguments="${inventory.displayName}"/>
                                    </span>
          	                   </c:otherwise>
        	                </c:choose>
                                             
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:when>
    <c:otherwise>
        <i:inline key=".noInventoryAttached" />
    </c:otherwise>
</c:choose>
</tags:boxContainer2>
<br><br>

<!-- Opt Out History -->
<tags:alternateRowReset/>
<tags:boxContainer2 nameKey="optOutHistory">
	<dr:optOutHistory previousOptOutList="${previousOptOutList}" />

	<c:if test="${fn:length(previousOptOutList) > 0}">
        <table class="actionArea">
            <tr>
                <td class="leftActions">
                    <cti:url var="optOutHistoryUrl" value="/spring/stars/operator/program/optOut/optOutHistory">
                        <cti:param name="accountId" value="${accountId}"/>
                    </cti:url>
                    <a href="${optOutHistoryUrl}" ><i:inline key=".viewCompleteHistory" /></a>
                </td>
            </tr>
        </table>
    </c:if>
    
</tags:boxContainer2>
</cti:standardPage>