<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<cti:standardPage module="dr" page="cc.home">
<cti:standardMenu menuSelection=".ccurt_setup"/>
<cti:includeScript link="/resources/js/pages/yukon.curtailment.js"/>
<cti:msgScope paths="yukon.web.modules.commercialcurtailment.ccurtSetup">
    
    <div class="column-24">
        <div class="column one nogutter">
            <cti:tabs>
                <cti:msg2 var="programsName" key=".ccurtPrograms"/>
                <cti:tab title="${programsName}" selected="true">
                <h2><i:inline key=".ccurtAvailable"/></h2>
                <div class="column one">
                    <table class="compact-results-table" id="programs">
                        <thead>
                            <tr>
                            </tr>
                        </thead>
                        <tfoot></tfoot>
                        <tbody>
                        <c:forEach var="program" items="${programs}">
                            <tr data-program-id="${program.id}">
                                <td>${program.programType.name}&nbsp;</td>
                                <td>${fn:escapeXml(program.name)}</td>
                                <td><a href="program/${program.id}/init">Start</a></td>
                                <td><a href="program/${program.id}/history">History</a></td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                    <c:forEach var="eventGroup" varStatus="allEvents" items="${eventGroups}">
                    <h3>${eventTypes[allEvents.index]}</h3>
                    <c:set var="groupOfEvents" value="${eventGroup}"/>
                    <c:choose>
                    <c:when test="${not empty groupOfEvents}">
                    <table class="compact-results-table">
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
                            <c:forEach var="item" varStatus="oneEvent" items="${groupOfEvents}">
                            <tr>
                                <td>${item.program.programType.name}</td>
                                <td>${item.program.name}</td>
                                <td><a href="program/${item.program.id}/event/${item.id}/detail">${item.displayName}</a></td>
                                <td>${item.stateDescription}</td>
                                <td><cti:formatDate value="${item.startTime}" type="DATEHM"/></td>
                                <td><cti:formatDate value="${item.stopTime}" type="DATEHM"/></td>
                            </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                    </c:when>
                    <c:otherwise>
                        <div>no ${event.key} events</div>
                    </c:otherwise>
                    </c:choose>
                    </c:forEach>
                </div>
                </cti:tab>
                <cti:msg2 var="setupName" key=".setup"/>
                <cti:tab title="${setupName}">
                    <table class="compact-results-table" id="programs">
                        <thead>
                            <tr></tr>
                        </thead>
                        <tfoot></tfoot>
                        <tbody>
                            <tr>
                                <td><a href="programList"><i:inline key=".programList"/></a></td>
                            </tr>
                            <tr>
                                <td><a href="groupList"><i:inline key=".groupList"/></a></td>
                            </tr>
                            <tr>
                                <td><a href="customerList"><i:inline key=".customerList"/></a></td>
                            </tr>
                        </tbody>
                    </table>
                 </cti:tab>
                <cti:msg2 var="trendName" key=".ccurtTrends"/>
                <cti:tab title="${trendName}">
                </cti:tab>
            </cti:tabs>
        </div>
    </div>
</cti:msgScope>
</cti:standardPage>