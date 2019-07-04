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
                                        <c:choose>
                                            <c:when test="${empty program.status}">
                                                <td class="tar"><dr:programState programId="${programId}"/></td>
                                            </c:when>
                                            <c:otherwise>
                                                <td class="tar">${fn:escapeXml(program.status)}</td>
                                            </c:otherwise>
                                        </c:choose>
                                    </tr>
                                    <c:choose>
                                        <c:when test="${empty program.gears}">
                                            <tr class="dn"></tr>
                                            <tr>
                                                <td><cti:dataUpdaterValue identifier="${programId}/CURRENT_GEAR" type="DR_PROGRAM"/></td>
                                                <td class="tar"> <cti:dataUpdaterValue identifier="${programId}/START_TIME" type="DR_PROGRAM"/>
                                                   - <cti:dataUpdaterValue identifier="${programId}/STOP_TIME" type="DR_PROGRAM"/>
                                                </td>
                                            </tr>
                                        </c:when>
                                        <c:otherwise>
                                            <c:forEach var="gearData" varStatus="loop" items="${program.gears}">
                                                <tr class="dn"></tr>
                                                <tr>
                                                    <td>${fn:escapeXml(gearData.gearName)}</td>
                                                    <td class="tar"><cti:formatDate type="TIME24H" value="${gearData.startDateTime}"/>
                                                      - 
                                                    <c:choose>
                                                        <c:when test="${empty gearData.stopDateTime}">
                                                            <c:choose>
                                                                <c:when test="${gearData.knownGoodStopDateTime == false}">
                                                                    <span style="font-style: italic"><i:inline key="yukon.common.unknown"/></span>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <cti:dataUpdaterValue identifier="${program.programId}/STOP_TIME" type="DR_PROGRAM"/>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </c:when>
                                                        <c:otherwise>
                                                        <c:choose>
                                                            <c:when test="${gearData.knownGoodStopDateTime == false}">
                                                                <span style="font-style: italic"><i:inline key="yukon.common.unknown"/></span>
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
                                       </c:otherwise>
                                   </c:choose>
                           </c:forEach> 
                        </tbody>
                    </table>
                    <br>
                </c:forEach>
                <div class="PT20">
                    <cti:url value="/dr/program/list" var="detailsUrl"/>
                    <a href="${detailsUrl}" target="_blank"><i:inline key="yukon.common.viewDetails"/></a>
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