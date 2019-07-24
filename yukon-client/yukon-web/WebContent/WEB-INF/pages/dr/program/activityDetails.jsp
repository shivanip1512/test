<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="dr" page="program.activityDetails">

    <table class="compact-results-table no-stripes" id="js-scroll-details-table">
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
            <c:set var="count" value="0"/>
            <c:forEach var="programDataEntry" items="${programsData}">
                <c:forEach var="programData" items="${programDataEntry.value}" varStatus="programDataStatusVar" >
                    <c:set var="count" value="${count+1}"/>
                    <c:choose>
                        <c:when test="${count % 2 == 0}">
                            <c:set var="striped" value="alt-row"/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="striped" value=""/>
                        </c:otherwise>
                    </c:choose>
                    <tr class="${striped}">
                        <td class="fwb">${programDataStatusVar.first ? programDataEntry.key: ""}</td>
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
                                    <table style="margin-left: -5px;">
                                        <tr>
                                            <td class="js-start-time-td">
                                                <cti:dataUpdaterValue identifier="${programData.programId}/START_TIME" type="DR_PROGRAM"/>
                                                <cti:dataUpdaterValue identifier="${programData.programId}/START" type="DR_PROGRAM" styleClass="js-start-time-span dn"/>
                                            </td>
                                            <td>-</td>
                                            <td class="js-stop-time-td">
                                                <cti:dataUpdaterValue identifier="${programData.programId}/STOP_TIME" type="DR_PROGRAM"/>
                                                <cti:dataUpdaterValue identifier="${programData.programId}/STOP" type="DR_PROGRAM" styleClass="js-stop-time-span dn"/>
                                            </td>
                                        </tr>
                                    </table>
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
                                                <cti:formatDate type="DATEHMS_12" value="${gearData.startDateTime}" var="eventStartTime"/>
                                                <td title="${eventStartTime}">
                                                    <cti:formatDate type="TIME24H" value="${gearData.startDateTime}"/>
                                                </td>
                                                <td>
                                                    -
                                                </td>
                                                <c:choose>
                                                    <c:when test="${empty gearData.stopDateTime}">
                                                        <cti:msg key="yukon.web.modules.dr.program.activityDetails.unKnownStopTime" var="eventStopTime"/> 
                                                    </c:when>
                                                    <c:otherwise>
                                                        <cti:formatDate type="DATEHMS_12" value="${gearData.stopDateTime}" var="eventStopTime"/>
                                                    </c:otherwise>
                                                </c:choose>
                                                <td title="${eventStopTime}">
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

    <cti:includeScript link="/resources/js/pages/yukon.dr.program.activityDetails.js" />
    <cti:includeScript link="JQUERY_SCROLL_TABLE_BODY"/>

</cti:standardPage>
