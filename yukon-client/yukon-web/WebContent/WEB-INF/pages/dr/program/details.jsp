<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="dr" page="program.details">

    <c:set var="unknownGearStopTime"><i:inline key="yukon.web.modules.dr.program.details.unKnownStopTime"/></c:set>
    <input type="hidden" id="unknownmsg" value="${unknownGearStopTime}"/>
    <table class="compact-results-table" id="js-scroll-details-table">
        <thead>
            <tr>
                <th><i:inline key=".day"/></th>
                <th><i:inline key=".programName"/></th>
                <th><i:inline key=".status"/></th>
                <th><i:inline key=".gearName"/></th>
                <th><i:inline key=".eventTime"/></th>

            </tr>
        </thead>
        <tbody>
            <c:forEach var="programDataEntry" items="${programsData}">
                <c:forEach var="programData" varStatus="programDataStatusVar" items="${programDataEntry.value}">
                    <tr class="${striped}">
                        <td style="font-weight:bold">${programDataStatusVar.first ? programDataEntry.key: ""}</td>
                        <c:url var="programURL" value="/dr/program/detail">
                            <c:param name="programId" value="${programData.programId}"/>
                        </c:url>
                        <td><a href="${programURL}">${fn:escapeXml(programData.programName)}</a></td>
                        <td>
                            <c:choose>
                                <c:when test="${empty programData.status}">
                                    <dr:programState programId="${programData.programId}"/>
                                </c:when>
                                <c:otherwise>
                                    ${programData.status}
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <c:choose>
                            <c:when test="${empty programData.gears}">
                                <td><cti:dataUpdaterValue identifier="${programData.programId}/CURRENT_GEAR" type="DR_PROGRAM"/></td>
                                <td>
                                    <cti:dataUpdaterValue identifier="${programData.programId}/START_TIME" type="DR_PROGRAM"/> - 
                                    <cti:dataUpdaterValue identifier="${programData.programId}/STOP_TIME" type="DR_PROGRAM"/>
                                </td>
                            </c:when>
                            <c:otherwise>
                                <td>
                                    <table style="margin-left: -5px;">
                                        <c:forEach var="gearData" items="${programData.gears}">
                                            <tr><td>${fn:escapeXml(gearData.gearName)}</td></tr>
                                        </c:forEach>
                                    </table>
                                </td>
                                <td>
                                    <table style="margin-left: -5px;">
                                        <c:forEach var="gearData" items="${programData.gears}">
                                            <tr>
                                                <td data-start-date-time="${gearData.startDateTime}" class="js-event-start-time">
                                                    <cti:formatDate type="TIME24H" value="${gearData.startDateTime}"/>
                                                </td>
                                                <td>
                                                    -
                                                </td>
                                                <td data-stop-date-time="${gearData.stopDateTime}" class="js-event-stop-time">
                                                    <c:choose>
                                                        <c:when test="${empty gearData.stopDateTime && gearData.knownGoodStopDateTime == false}">
                                                            <span style="font-style: italic"><i:inline key="yukon.common.unknown"/></span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <c:choose>
                                                                <c:when test="${empty gearData.stopDateTime}">
                                                                    <cti:dataUpdaterValue identifier="${programData.programId}/STOP_TIME" type="DR_PROGRAM"/>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <cti:formatDate type="TIME24H" value="${gearData.stopDateTime}"/>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </table>
                                </td>
                            </c:otherwise>
                        </c:choose>
                    </tr>
                </c:forEach>
            </c:forEach>
        </tbody>
    </table>

    <cti:includeScript link="/resources/js/pages/yukon.dr.program.details.js" />
    <cti:includeScript link="JQUERY_SCROLL_TABLE_BODY"/>

</cti:standardPage>
