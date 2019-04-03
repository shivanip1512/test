<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:standardPage module="dr" page="cc.home">

    <div class="column-24">
        <div class="column one nogutter">
            <cti:tabs>
            
                <%-- PROGRAMS TAB --%>
                <cti:msg2 var="programsName" key=".programs"/>
                <cti:tab title="${programsName}" selected="true">
                <h2><i:inline key=".availablePrograms"/></h2>
                <div class="column one">
                    <table class="compact-results-table stacked-lg" id="programs">
                        <c:forEach var="program" items="${programs}">
                            <tr data-program-id="${program.id}">
                                <td>${program.programType.name}&nbsp;</td>
                                <td>${fn:escapeXml(program.name)}</td>
                                <cti:url var="startUrl" value="/dr/cc/program/${program.id}/init"/>
                                <td><a href="${startUrl}"><i:inline key=".start"/></a></td>
                                <cti:url var="historyUrl" value="/dr/cc/program/${program.id}/history"/>
                                <td><a href="${historyUrl}"><i:inline key=".history"/></a></td>
                            </tr>
                        </c:forEach>
                    </table>
                    <c:forEach var="eventEntry" items="${events}">
                        <h3><i:inline key="${eventEntry.key}"/></h3>
                        <c:choose>
                            <c:when test="${not empty eventEntry.value}">
                                <table class="compact-results-table stacked-md">
                                    <thead>
                                        <tr>
                                      <th><i:inline key=".programType"/></th>
                                      <th><i:inline key=".programName"/></th>
                                      <th><i:inline key=".programEvent"/></th>
                                      <th><i:inline key=".programState"/></th>
                                      <th><i:inline key=".programStart"/></th>
                                      <th><i:inline key=".programStop"/></th>
                                  </tr>
                                    </thead>
                                    <tfoot></tfoot>
                                    <tbody>
                                        <c:forEach var="event" items="${eventEntry.value}">
                                            <tr>
                                       <td>${event.program.programType.name}</td>
                                       <td>${fn:escapeXml(event.program.name)}</td>
                                       <cti:url var="eventDetailUrl" value="/dr/cc/program/${event.program.id}/event/${event.id}/detail"/>
                                       <td><a href="${eventDetailUrl}">${fn:escapeXml(event.displayName)}</a></td>
                                       <td>${event.stateDescription}</td>
                                       <td><cti:formatDate value="${event.startTime}" type="DATEHM"/></td>
                                       <td><cti:formatDate value="${event.stopTime}" type="DATEHM"/></td>
                                   </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </c:when>
                            <c:otherwise>
                                <div class="empty-list stacked-md">
                                    <i:inline key=".noEvents"/>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </div>
                </cti:tab>
                
                <%-- SETUP TAB --%>
                <cti:msg2 var="setupName" key=".setup"/>
                <cti:tab title="${setupName}">
                    <table class="compact-results-table" id="programs">
                     <tr>
                         <cti:url var="programsUrl" value="/dr/cc/programList"/>
                         <td><a href="${programsUrl}"><i:inline key=".programs"/></a></td>
                     </tr>
                     <tr>
                         <cti:url var="groupsUrl" value="/dr/cc/groupList"/>
                         <td><a href="${groupsUrl}"><i:inline key=".groups"/></a></td>
                     </tr>
                     <tr>
                         <cti:url var="customersUrl" value="/dr/cc/customerList"/>
                         <td><a href="${customersUrl}"><i:inline key=".customers"/></a></td>
                     </tr>
                    </table>
                </cti:tab>
                
                <%-- TRENDS TAB --%>
                <cti:msg2 var="trendName" key=".trends"/>
                <cti:tab title="${trendName}" selected="${showTrends}">
                    <div id="label-json" class="dn">${fn:escapeXml(labels)}</div>
    
                    <div class="column-7-17 clearfix">
                        <div class="column one">
                            <div class="side-nav trend-list">
                                <ul>
                                    <c:forEach var="trend" items="${trends}">
                                        <c:if test="${trendId == trend.graphDefinitionID}"><c:set var="selected" value="selected"/></c:if>
                                        <c:if test="${trendId != trend.graphDefinitionID}"><c:set var="selected" value=""/></c:if>
                                        <li data-graph-id="${trend.graphDefinitionID}" class="${selected}">
                                            <cti:url var="trendUrl" value="/dr/cc/home">
                                                <cti:param name="trendId" value="${trend.graphDefinitionID}"/>
                                            </cti:url>
                                            <a href="${trendUrl}">${fn:escapeXml(trend.name)}</a>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </div>
                        </div>
                        
                        <div class="column two nogutter trend-container pr" data-trend="${trendId}">
                            <div class="glass"></div>
                        </div>
                    </div>
                </cti:tab>
            </cti:tabs>
        </div>
    </div>

    <cti:includeScript link="/resources/js/pages/yukon.dr.curtailment.js"/>
    <cti:includeScript link="/resources/js/common/yukon.trends.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.tools.trends.js"/>
    <cti:includeScript link="JQUERY_SCROLLTO"/>
    <cti:includeScript link="HIGH_STOCK"/>
    <cti:includeScript link="HIGH_STOCK_EXPORTING"/>
    <cti:includeScript link="HIGH_STOCK_NO_DATA"/>

</cti:standardPage>