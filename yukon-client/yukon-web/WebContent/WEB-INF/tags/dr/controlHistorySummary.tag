<%@ attribute name="completeHistoryUrl" required="true" %>
<%@ attribute name="displayableProgramList" required="true" type="java.util.List" %>
<%@ attribute name="past" type="java.lang.Boolean" %>
<%@ attribute name="showControlHistorySummary" type="java.lang.Boolean" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths=".controlHistorySummary, components.controlHistorySummary">
<style>.program-buffer{height:15px;}</style>
    <c:choose>
        <c:when test="${isNotEnrolled && not past}">
            <span id="notEnrolledMessageSpan"> <i:inline key=".notEnrolledMessage" /></span>
        </c:when>
        <c:otherwise>
        
            <!-- Info Popup -->
            <cti:uniqueIdentifier var="uniqueId" prefix="info-popup-"/>
            <i:simplePopup id="${uniqueId}" titleKey=".helpInfoTitle" on=".js-help-${uniqueId}" options="{width:600}">
                 <tags:nameValueContainer2>
                     <tags:nameValue2 nameKey=".helpInfoText.programBasedControlHistory">
                         <i:inline key=".helpInfoText.programBasedControlHistoryText" />
                     </tags:nameValue2>
                     <tags:nameValue2 nameKey=".helpInfoText.inventoryBasedControlHistory" >
                         <i:inline key=".helpInfoText.inventoryBasedControlHistoryText" />
                     </tags:nameValue2>
                 </tags:nameValueContainer2>
            </i:simplePopup>
            
            <table class="compact-results-table no-stripes no-borders">
                <thead>
                    <c:if test="${showControlHistorySummary}">
                        <tr>
                            <th colspan="3">
                                <cti:icon icon="icon-help" classes="cp fn js-help-${uniqueId}"/>
                            </th>
                            <th><i:inline key=".day"/></th>
                            <th><i:inline key=".month"/></th>
                            <th><i:inline key=".year"/></th>
                        </tr>
                    </c:if>
                </thead>
                <tfoot></tfoot>
                <tbody>
                
                    <c:forEach var="displayableProgram" items="${displayableProgramList}">
                        <c:set var="program" value="${displayableProgram.program}" />
                        <tr>
                            <td colspan="3">
                                <i:inline key="${program.displayName}"/>
                                <cti:url value="${completeHistoryUrl}" var="detailsUrl">
                                    <cti:param name="programId" value="${program.programId}"/>
                                    <cti:param name="past" value="${past}"/>
                                    <c:if test="${not empty accountId}">
                                        <cti:param name="accountId" value="${accountId}"/>
                                    </c:if>
                                </cti:url>
                                <a href="${detailsUrl}" class="fr"><i:inline key=".details"/></a>
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
                        <tr><td colspan="6"><div class="divider dashed"></div></td></tr>
                        
                        <c:set var="controlHistorySize" value="${fn:length(displayableProgram.displayableControlHistoryList)}"/>
                        <c:forEach var="displayableControlHistory" items="${displayableProgram.displayableControlHistoryList}" varStatus="status">
                            <c:set var="controlHistory" value="${displayableControlHistory.controlHistory}" />
                            <c:set var="controlHistorySummary" value="${controlHistory.controlHistorySummary}" />
                            <tr>
                                <c:set var="imgRowSpan" value="${controlHistorySize}" />
                                <c:if test="${imgRowSpan < 3}">
                                    <c:set var="imgRowSpan" value="${3}"/>
                                </c:if>
                                
                                <c:if test="${status.count eq 1}">
                                    <td rowspan="${controlHistorySize}">
                                        <c:if test="${program.logoAssigned}">
                                           <img src="<cti:url value="/WebConfig/${program.applianceCategoryLogo}"/>">
                                        </c:if>
                                    </td>
                                </c:if>
                            
                                <c:choose>
                                    <c:when test="${displayableControlHistory.controlStatusDisplay or displayableControlHistory.deviceLabelControlStatusDisplay}">
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty controlHistory.displayName}">${fn:escapeXml(controlHistory.displayName)}</c:when>
                                                <c:otherwise>
                                                    <i:inline key=".deviceRemoved"/>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty controlHistory.lastControlHistoryEvent.endDate}">
                                                    <cti:formatDate type="DATEHM" var="lastControlledEndDate" value="${controlHistory.lastControlHistoryEvent.endDate}"/>
                                                    <i:inline key="${controlHistory.currentStatus.formatKey}" arguments="${lastControlledEndDate}" /> 
                                                </c:when>
                                                <c:otherwise>
                                                    <i:inline key="${controlHistory.currentStatus.formatKey}" /> 
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </c:when>
                                </c:choose>
                                
                                <c:if test="${showControlHistorySummary}">
                                    <td>
                                        <c:if test="${controlHistorySummary.dailySummary.millis != 0}">
                                            <cti:formatDuration type="HM_ABBR" value="${controlHistorySummary.dailySummary.millis}" />
                                        </c:if>
                                    </td>
                                    <td>
                                        <c:if test="${controlHistorySummary.monthlySummary.millis != 0}">
                                            <cti:formatDuration type="HM_ABBR" value="${controlHistorySummary.monthlySummary.millis}" />
                                        </c:if>
                                    </td>
                                    <td>
                                        <c:if test="${controlHistorySummary.yearlySummary.millis != 0}">
                                            <cti:formatDuration type="HM_ABBR" value="${controlHistorySummary.yearlySummary.millis}" />
                                        </c:if>
                                    </td>
                                </c:if>
                            </tr>
                        </c:forEach>
                        <tr><td colspan="6" class="program-buffer"></td></tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</cti:msgScope>