<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:msgScope paths="widgets.programWidget">
    <c:choose>
        <c:when test="${not empty programsData}">
            <div class="js-program-widget">
                 <cti:toJson object="${widgetUpdateDate}" id="js-widget-json-data"/>
                 <c:forEach var="entry" varStatus="loop" items="${programsData}">
                     <table class="compact-results-table dashed">
                         <thead>
                             <tr>
                                 <th>${entry.key}</th>
                                 <th></th>
                             </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="program" varStatus="loop" items="${entry.value}">
                                <c:set var="programId" value="${program.programId}"/>
                                    <tr>
                                        <c:url var="programURL" value="/dr/program/detail">
                                            <c:param name="programId" value="${programId}"/>
                                        </c:url>
                                        <td><a href="${programURL}">${fn:escapeXml(program.programName)}</a></td>
                                        <td class="tar"><cti:msg key="${program.originSource.formatKey}"/></td>
                                    </tr>
                                    <c:choose>
                                        <c:when test="${empty program.gears}">
                                            <tr class="dn"></tr>
                                            <tr>
                                                <td><cti:dataUpdaterValue identifier="${programId}/CURRENT_GEAR" type="DR_PROGRAM"/></td>
                                                <td class="tar"> 
                                                    <span id="js-start-time-td-${programId}">
                                                        <cti:dataUpdaterValue identifier="${programId}/START_TIME" type="DR_PROGRAM"/>
                                                        <cti:dataUpdaterCallback function="yukon.widget.program.setStartTimeTooltip($('#js-start-time-td-${programId}'))"
                                                                         initialize="true" value="DR_PROGRAM/${programId}/START"/>
                                                    </span>
                                                   -<span id="js-stop-time-td-${programId}">
                                                       <cti:dataUpdaterValue identifier="${programId}/STOP_TIME" type="DR_PROGRAM"/>
                                                       <cti:dataUpdaterCallback function="yukon.widget.program.setStopTimeTooltip($('#js-stop-time-td-${programId}'))"
                                                                         initialize="true" value="DR_PROGRAM/${programId}/STOP"/>
                                                    </span>
                                                </td>
                                            </tr>
                                        </c:when>
                                        <c:otherwise>
                                            <c:forEach var="gearData" varStatus="loop" items="${program.gears}">
                                                <tr class="dn"></tr>
                                                <tr>
                                                    <td>${fn:escapeXml(gearData.gearName)}</td>
                                                    <td class="tar">
                                                    <cti:formatDate type="DATEHMS_12" value="${gearData.startDateTime}" var="eventStartTime"/>
                                                    <span title="${eventStartTime}"><cti:formatDate type="TIME24H" value="${gearData.startDateTime}"/></span>
                                                        <c:choose>
                                                            <c:when test="${gearData.startedOnSameDay == false}">
                                                                <cti:msg key="yukon.web.modules.dr.program.time.mismatchIndicator"/>
                                                            </c:when>
                                                        </c:choose>
                                                      - 
                                                        <c:choose>
                                                            <c:when test="${empty gearData.stopDateTime}">
                                                                <c:choose>
                                                                    <c:when test="${gearData.knownGoodStopDateTime == false}">
                                                                        <span style="font-style: italic"><i:inline key="yukon.common.unknown"/></span>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span id="js-stop-time-td-${programId}">
                                                                            <cti:dataUpdaterValue identifier="${program.programId}/STOP_TIME" type="DR_PROGRAM"/>
                                                                            <cti:dataUpdaterCallback function="yukon.widget.program.setStopTimeTooltip($('#js-stop-time-td-${programId}'))"
                                                                             initialize="true" value="DR_PROGRAM/${programId}/STOP"/>
                                                                        </span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </c:when>
                                                            <c:otherwise>
                                                            <c:choose>
                                                                <c:when test="${gearData.knownGoodStopDateTime == false}">
                                                                    <span style="font-style: italic"><i:inline key="yukon.common.unknown"/></span>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <cti:formatDate type="DATEHMS_12" value="${gearData.stopDateTime}" var="eventStopTime"/>
                                                                    <span title="${eventStopTime}">
                                                                    <cti:formatDate type="TIME24H" value="${gearData.stopDateTime}"/>
                                                                        <c:choose>
                                                                            <c:when test="${gearData.stoppedOnSameDay == false}">
                                                                                 <cti:msg key="yukon.web.modules.dr.program.time.mismatchIndicator"/>
                                                                            </c:when>
                                                                        </c:choose>
                                                                    </span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                            </c:otherwise>
                                                       </c:choose>
                                                   </td> 
                                               </tr>
                                           </c:forEach>
                                       </c:otherwise>
                                   </c:choose>
                           </c:forEach> 
                        </tbody>
                    </table>
                    <br>
                </c:forEach>
                <div class="PT20">
                    <cti:url value="/dr/program/activityDetails" var="activityDetailsUrl"/>
                    <a href="${activityDetailsUrl}" target="_blank"><i:inline key="yukon.common.viewDetails"/></a>
                    <span class="fr">
                        <cti:msg2 key="yukon.web.widgets.lastUpdated" var="lastUpdatedMsg"/>
                        <span class="fl js-last-updated" style="font-size:11px" title="${lastUpdatedMsg}"></span>
                        <cti:button renderMode="image" icon="icon-arrow-refresh" classes="js-update-program" disabled="true"></cti:button>
                    </span>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <i:inline key="yukon.web.widgets.programWidget.noProgramFound"/>
        </c:otherwise>
    </c:choose>
    <cti:includeScript link="/resources/js/widgets/yukon.widget.program.js"/>
</cti:msgScope>