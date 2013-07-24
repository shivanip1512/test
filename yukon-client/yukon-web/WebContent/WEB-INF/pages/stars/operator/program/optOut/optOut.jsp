<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="dateTime" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog" %>

<cti:standardPage module="operator" page="optOut.main">

<!-- Helper Popup -->
<c:url var="help" value="/WebConfig/yukon/Icons/help.gif"/>
<c:url var="helpOver" value="/WebConfig/yukon/Icons/help_over.gif"/>
<cti:uniqueIdentifier var="uniqueId" prefix="helpInfoPopup_"/>
<i:simplePopup id="${uniqueId}" titleKey=".helpInfoTitle" on="#helpIcon">
    <table class="optOutActions">
        <tr>
            <td><cti:icon nameKey="allowOne" icon="icon-gray-add"/></td>
            <td><i:inline key=".allowOneIconText" /></td>
        </tr>
        <tr>
            <td><cti:icon nameKey="decrementAllowance" icon="icon-gray-subtract"/></td>
            <td><i:inline key=".decrementAllowanceIconText"/></td>
        <tr>
            <td><cti:icon nameKey="resetToLimit" icon="icon-arrow-undo"/></td>
            <td><i:inline key=".resetToLimitIconText" /></td>
        </tr>
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
    
    <form:form id="optOutForm" action="/stars/operator/program/optOut/deviceSelection" commandName="optOutBackingBean">

         <input type="hidden" name="accountId" value="${accountId}" />

         <tags:nameValueContainer2>
         
            <tags:nameValue2 nameKey=".startDate">
                <cti:checkRolesAndProperties value="OPERATOR_OPT_OUT_TODAY_ONLY">
                    <cti:formatDate  value="${optOutBackingBean.startDate}" type="DATE" 
                                     var="formattedDate"/>
                    <spring:escapeBody htmlEscape="true">${formattedDate}</spring:escapeBody>
                    <input type="hidden" name="startDate" value="${formattedDate}" />
                </cti:checkRolesAndProperties>
                <cti:checkRolesAndProperties value="!OPERATOR_OPT_OUT_TODAY_ONLY">
                     <dateTime:date path="startDate" value="${optOutBackingBean.startDate}"/>
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
        <input type="submit" value="<cti:msg2 key=".optOut"/>" class="button">
    </form:form>
</tags:boxContainer2>

<!-- Current Opt Outs -->
<tags:boxContainer2 nameKey="currentOptOuts">
<c:choose>
    <c:when test="${fn:length(currentOptOutList) > 0}">
        <table id="deviceTable" class="compactResultsTable rowHighlighting">
            <thead>
                <tr>
                    <th><i:inline key=".device"/></th>
                    <th><i:inline key=".program"/></th>
                    <th><i:inline key=".status"/></th>
                    <th><i:inline key=".dateActive"/></th>
                    <th class="nonwrapping"><i:inline key=".actions"/></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="optOut" items="${currentOptOutList}">
                    <tr>
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
                                 <c:when test="${optOut.state eq 'START_OPT_OUT_SENT'}">class="success"</c:when>
                                 <c:when test="${optOut.state eq 'CANCEL_SENT'}">class="subtle"</c:when>
                                 <c:otherwise>class=""</c:otherwise>
                            </c:choose>
                        >
                            <cti:msg key="${optOut.state.formatKey}"/>
                        </td>
                        <td valign="top" class="nonwrapping">
                            <cti:formatDate value="${optOut.startDate}" type="DATEHM"/>
                        </td>
                        <td valign="top">
                            <cti:icon nameKey="cancelOptOut" id="cancel${optOut.eventId}" icon="icon-cross"
                                data-href="/stars/operator/program/optOut/cancelOptOut?accountId=${accountId}&eventId=${optOut.eventId}"/>
                            <dialog:confirm on="#cancel${optOut.eventId}" nameKey="confirmCancelOptOut" argument="${optOut.inventory.displayName}"/>
                            <c:choose>
                                <c:when test="${optOut.state == 'START_OPT_OUT_SENT'}">
                                    <cti:icon nameKey="resendOptOut" id="resend${optOut.eventId}" icon="icon-control-repeat-blue"
                                        data-href="/stars/operator/program/optOut/resend?accountId=${accountId}&inventoryId=${optOut.inventory.inventoryId}"/>
                                    <dialog:confirm on="#resend${optOut.eventId}" nameKey="confirmResendOptOut" argument="${optOut.inventory.displayName}"/>
                                </c:when>
                                <c:otherwise>
                                    <cti:icon nameKey="resendOptOutDisabled" icon="icon-control-repeat-blue" classes="disabled" />
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        <i:inline key=".noCurrentOptOuts"/>
    </c:otherwise>
</c:choose>
</tags:boxContainer2>

<!-- Opt Out Limits -->
<tags:boxContainer2 nameKey="optOutLimits">
<c:choose>
    <c:when test="${fn:length(displayableInventories) > 0}">
        <table id="deviceTable" class="compactResultsTable rowHighlighting">
            <thead>
                <tr>
                    <th><i:inline key=".device"/></th>
                    <th><i:inline key=".used"/></th>
                    <th><i:inline key=".remaining"/></th>
                    <c:if test="${!noOptOutLimits}">
                        <th><i:inline key=".actions"/>
                            <cti:icon id="helpIcon" nameKey="help" icon="icon-help" href="javascript:void(0);"/>
                        </th>
                    </c:if>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="inventory" items="${displayableInventories}">
                    <tr>
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
                                <cti:icon nameKey="allowOne" id="allowOne${inventory.inventoryId}" icon="icon-gray-add"
                                    data-href="/stars/operator/program/optOut/allowAnother?accountId=${accountId}&inventoryId=${inventory.inventoryId}"/>
                                <dialog:confirm on="#allowOne${inventory.inventoryId}" nameKey="confirmAllowOne" argument="${inventory.displayName}"/>

                                <c:if test="${optOutCounts[inventory.inventoryId].remainingOptOuts > 0}">
                                    <cti:icon nameKey="decrementAllowance" id="decrementAllowance${inventory.inventoryId}" icon="icon-gray-subtract"
                                        data-href="/stars/operator/program/optOut/decrementAllowances?accountId=${accountId}&inventoryId=${inventory.inventoryId}"/>
                                    <dialog:confirm on="#decrementAllowance${inventory.inventoryId}" nameKey="confirmDecrementAllowance" argument="${inventory.displayName}"/>
                                </c:if>
                                <c:if test="${optOutCounts[inventory.inventoryId].remainingOptOuts <= 0}">
                                    <cti:icon nameKey="decrementAllowanceDisabled" icon="icon-gray-subtract" classes="disabled"/>
                                </c:if>

                                <c:choose>
                                     <c:when test="${optOutLimit <= optOutCounts[inventory.inventoryId].remainingOptOuts}">
                                        <cti:icon nameKey="resetToLimitDisabled" icon="icon-arrow-undo" classes="disabled"/>
                                     </c:when>
                                     <c:otherwise>
                                        <cti:icon nameKey="resetToLimit" id="resetToLimit${inventory.inventoryId}" icon="icon-arrow-undo"
                                            data-href="/stars/operator/program/optOut/resetToLimit?accountId=${accountId}&inventoryId=${inventory.inventoryId}"/>
                                        <dialog:confirm on="#resetToLimit${inventory.inventoryId}" nameKey="confirmResetToLimit" argument="${inventory.displayName}"/>
                                      </c:otherwise>
                                 </c:choose>

                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        <i:inline key=".noInventoryAttached" />
    </c:otherwise>
</c:choose>
</tags:boxContainer2>

<!-- Opt Out History -->
<tags:boxContainer2 nameKey="optOutHistory">
     <dr:optOutHistory previousOptOutList="${previousOptOutList}" />

     <c:if test="${fn:length(previousOptOutList) > 0}">
        <div class="pageActionArea">
            <cti:url var="optOutHistoryUrl" value="/stars/operator/program/optOut/optOutHistory">
                <cti:param name="accountId" value="${accountId}"/>
            </cti:url>
            <a href="${optOutHistoryUrl}" ><i:inline key=".viewCompleteHistory" /></a>
        </div>
    </c:if>
</tags:boxContainer2>
</cti:standardPage>