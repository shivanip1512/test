<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>

<cti:standardPage module="operator" page="optOut.main">
<cti:checkEnergyCompanyOperator showError="true" accountId="${accountId}">
<!-- Helper Popup -->
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
<tags:sectionContainer2 nameKey="createOptOut" styleClass="stacked">
    <c:if test="${!empty currentOptOutList && !optOutsAvailable}">
        <i:inline key=".noOptOutsAvailable"/>
        <br/>
    </c:if>
    
    <cti:checkRolesAndProperties value="!OPERATOR_OPT_OUT_TODAY_ONLY">
        <i:inline key=".description"/><br><br>
    </cti:checkRolesAndProperties>
    <cti:url var="optOutUrl" value="/stars/operator/program/optOut/deviceSelection"/>
    <form:form id="optOutForm" action="${optOutUrl}" modelAttribute="optOutBackingBean">
         <cti:csrfToken/>   
         <input type="hidden" name="accountId" value="${accountId}" />

         <tags:nameValueContainer2>
         
            <tags:nameValue2 nameKey=".startDate">
                <cti:checkRolesAndProperties value="OPERATOR_OPT_OUT_TODAY_ONLY">
                    <cti:formatDate value="${optOutBackingBean.startDate}" type="DATE" var="formattedDate"/>
                    ${fn:escapeXml(formattedDate)}
                    <input type="hidden" name="startDate" value="${formattedDate}" />
                </cti:checkRolesAndProperties>
                <cti:checkRolesAndProperties value="!OPERATOR_OPT_OUT_TODAY_ONLY">
                     <dt:date path="startDate" value="${optOutBackingBean.startDate}"/>
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
        <cti:msg2 key=".optOut" var="optoutButton"/>
        <cti:button type="submit" label="${optoutButton}" classes="primary action"/>
    </form:form>
</tags:sectionContainer2>

<!-- Current Opt Outs -->
<tags:sectionContainer2 nameKey="currentOptOuts" styleClass="stacked">
<c:choose>
    <c:when test="${fn:length(currentOptOutList) > 0}">
        <table id="deviceTable" class="compact-results-table row-highlighting dashed">
            <thead>
                <tr>
                    <th><i:inline key=".device"/></th>
                    <th><i:inline key=".program"/></th>
                    <th><i:inline key=".status"/></th>
                    <th><i:inline key=".dateActive"/></th>
                    <th><i:inline key=".actions"/></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="optOut" items="${currentOptOutList}">
                    <tr>
                        <td>${fn:escapeXml(optOut.inventory.displayName)}</td>
                        <td>
                            <c:forEach var="program" items="${optOut.programList}" varStatus="status">
                                <c:if test="${status.count != 1}"><br></c:if>
                                ${fn:escapeXml(program.programName)}
                            </c:forEach>
                        </td>
                        <c:choose>
                            <c:when test="${optOut.state eq 'START_OPT_OUT_SENT'}"><c:set var="classes" value="success"/></c:when>
                            <c:when test="${optOut.state eq 'CANCEL_SENT'}"><c:set var="classes" value="subtle"/></c:when>
                            <c:otherwise><c:set var="classes" value=""/></c:otherwise>
                        </c:choose>
                        <td class="${classes}">
                            <cti:msg key="${optOut.state.formatKey}"/>
                        </td>
                        <td>
                            <cti:formatDate value="${optOut.startDate}" type="DATEHM"/>
                        </td>
                        <td>
                            <cti:url var="cancelUrl" value="/stars/operator/program/optOut/cancelOptOut">
                                <cti:param name="accountId" value="${accountId}"/>
                                <cti:param name="eventId" value="${optOut.eventId}"/>
                            </cti:url>
                            <cti:icon nameKey="cancelOptOut" id="cancel${optOut.eventId}" icon="icon-cross" classes="cp"
                                data-href="${cancelUrl}"/>
                            <d:confirm on="#cancel${optOut.eventId}" nameKey="confirmCancelOptOut" argument="${optOut.inventory.displayName}"/>
                            <c:choose>
                                <c:when test="${optOut.state == 'START_OPT_OUT_SENT'}">
                                    <cti:url var="resendUrl" value="/stars/operator/program/optOut/resend">
                                        <cti:param name="accountId" value="${accountId}"/>
                                        <cti:param name="inventoryId" value="${optOut.inventory.inventoryId}"/>
                                    </cti:url>
                                    <cti:icon nameKey="resendOptOut" id="resend${optOut.eventId}" icon="icon-control-repeat-blue" classes="cp"
                                        data-href="${resendUrl}"/>
                                    <d:confirm on="#resend${optOut.eventId}" nameKey="confirmResendOptOut" argument="${optOut.inventory.displayName}"/>
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
</tags:sectionContainer2>

<!-- Opt Out Limits -->
<tags:sectionContainer2 nameKey="optOutLimits" styleClass="stacked">
<c:choose>
    <c:when test="${fn:length(displayableInventories) > 0}">
        <table id="deviceTable" class="compact-results-table row-highlighting dashed">
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
                        <td>${fn:escapeXml(inventory.displayName)}</td>
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
                                <cti:url var="allowAnotherUrl" value="/stars/operator/program/optOut/allowAnother">
                                    <cti:param name="accountId" value="${accountId}"/>
                                    <cti:param name="inventoryId" value="${inventory.inventoryId}"/>
                                </cti:url>
                                <cti:icon nameKey="allowOne" id="allowOne${inventory.inventoryId}" icon="icon-gray-add"
                                    data-href="${allowAnotherUrl}"/>
                                <d:confirm on="#allowOne${inventory.inventoryId}" nameKey="confirmAllowOne" argument="${inventory.displayName}"/>

                                <c:if test="${optOutCounts[inventory.inventoryId].remainingOptOuts > 0}">
                                    <cti:url var="decrementAllowancesUrl" value="/stars/operator/program/optOut/decrementAllowances">
                                        <cti:param name="accountId" value="${accountId}"/>
                                        <cti:param name="inventoryId" value="${inventory.inventoryId}"/>
                                    </cti:url>
                                    <cti:icon nameKey="decrementAllowance" id="decrementAllowance${inventory.inventoryId}" icon="icon-gray-subtract"
                                        data-href="${decrementAllowancesUrl}"/>
                                    <d:confirm on="#decrementAllowance${inventory.inventoryId}" nameKey="confirmDecrementAllowance" argument="${inventory.displayName}"/>
                                </c:if>
                                <c:if test="${optOutCounts[inventory.inventoryId].remainingOptOuts <= 0}">
                                    <cti:icon nameKey="decrementAllowanceDisabled" icon="icon-gray-subtract" classes="disabled"/>
                                </c:if>

                                <c:choose>
                                     <c:when test="${optOutLimit <= optOutCounts[inventory.inventoryId].remainingOptOuts}">
                                        <cti:icon nameKey="resetToLimitDisabled" icon="icon-arrow-undo" classes="disabled"/>
                                     </c:when>
                                     <c:otherwise>
                                        <cti:url var="resetToLimitUrl" value="/stars/operator/program/optOut/resetToLimit">
                                            <cti:param name="accountId" value="${accountId}"/>
                                            <cti:param name="inventoryId" value="${inventory.inventoryId}"/>
                                        </cti:url>
                                        <cti:icon nameKey="resetToLimit" id="resetToLimit${inventory.inventoryId}" icon="icon-arrow-undo"
                                            data-href="${resetToLimitUrl}"/>
                                        <d:confirm on="#resetToLimit${inventory.inventoryId}" nameKey="confirmResetToLimit" argument="${inventory.displayName}"/>
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
</tags:sectionContainer2>

<!-- Opt Out History -->
<tags:sectionContainer2 nameKey="optOutHistory" styleClass="stacked">
     <dr:optOutHistory previousOptOutList="${previousOptOutList}" />

     <c:if test="${fn:length(previousOptOutList) > 0}">
        <div class="page-action-area">
            <cti:url var="optOutHistoryUrl" value="/stars/operator/program/optOut/optOutHistory">
                <cti:param name="accountId" value="${accountId}"/>
            </cti:url>
            <a href="${optOutHistoryUrl}" ><i:inline key=".viewCompleteHistory" /></a>
        </div>
    </c:if>
</tags:sectionContainer2>
</cti:checkEnergyCompanyOperator>
</cti:standardPage>
