<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="capcontrol" page="ivvc.voltagePoints">
    <%@include file="/capcontrol/capcontrolHeader.jspf"%>
    
    <cti:includeScript link="/resources/js/pages/yukon.da.voltage.points.edit.js"/>
    
    <tags:boxContainer2 nameKey=".title">
        <form:form id="voltagePointsForm" action="updateVoltagePoints" modelAttribute="zoneVoltagePointsHolder">
            <cti:csrfToken/>
            <input name="zoneId" value="${zoneId}" type="hidden"/>
            <table class="compact-results-table no-stripes js-voltage-points">
                <thead>
                    <tr>
                        <th class="vab"><i:inline key=".table.header.deviceName"/></th>
                        <th class="vab"><i:inline key=".table.header.pointName"/></th>
                        <th class="vab"><i:inline key=".table.header.phase"/></th>
                        <th class="vab"><i:inline key=".table.header.lowerLimit"/></th>
                        <th class="vab"><i:inline key=".table.header.currentVoltage"/></th>
                        <th class="vab"><i:inline key=".table.header.upperLimit"/></th>
                        <th>
                            <span class="fl"><i:inline key=".table.header.overrideStrategy"/>&nbsp;</span><br/>
                            <cti:msg2 var="titleText" key=".editStrategy"/>
                            <cti:url var="strategyUrl" value="/capcontrol/strategies/${strategy.id}" />
                            <span class="sub">(<a href="${strategyUrl}" title="${titleText}">${strategy.name}</a>)</span>
                        </th>
                        <th class="vab"><i:inline key=".table.header.ignore"/></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:forEach var="voltagePoint" items="${zoneVoltagePointsHolder.points}" varStatus="status">
                        <c:set var="disabledInput" value="false"/>
                        <c:if test="${voltagePoint.overrideStrategy == false || !hasEditingRole}">
                            <c:set var="disabledInput" value="true"/>
                        </c:if>
                    
                        <tr>
                            <td>
                                <form:hidden path="points[${status.index}].paoName"/>
                                <form:hidden path="points[${status.index}].parentPaoIdentifier"/>
                                <form:hidden path="points[${status.index}].pointId"/>
                                <form:hidden path="points[${status.index}].pointName"/>
                                <spring:escapeBody htmlEscape="true">${voltagePoint.paoName}</spring:escapeBody></td>
                            <td>
                                <cti:url var="pointLink" value="/tools/points/${voltagePoint.pointId}" />
                                <a href="${pointLink}">
                                   <spring:escapeBody htmlEscape="true">${voltagePoint.pointName}</spring:escapeBody>
                                </a>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${!voltagePoint.regulator}">
                                        <form:select path="points[${status.index}].phase" disabled="${!hasEditingRole}">
                                            <form:option value="A"><cti:msg2 key="yukon.common.phase.A"/></form:option>
                                            <form:option value="B"><cti:msg2 key="yukon.common.phase.B"/></form:option>
                                            <form:option value="C"><cti:msg2 key="yukon.common.phase.C"/></form:option>
                                        </form:select>
                                    </c:when>
                                    <c:otherwise>
                                        <form:hidden path="points[${status.index}].phase"/>
                                        <i:inline key="${voltagePoint.phase}"/>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <tags:input path="points[${status.index}].lowerLimit" size="4"
                                        disabled="${disabledInput}" inputClass="lowerLimit" />
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${voltagePoint.pointId > 0}">
                                        <cti:pointValue pointId="${voltagePoint.pointId}" format="VALUE" />
                                    </c:when>
                                    <c:otherwise>
                                        <i:inline key="yukon.common.dashes" />
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <tags:input path="points[${status.index}].upperLimit" size="4"
                                        disabled="${disabledInput}" inputClass="upperLimit" />
                            </td>
                            <td><form:checkbox cssClass="js-override-strategy" path="points[${status.index}].overrideStrategy" disabled="${!hasEditingRole}"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${voltagePoint.ignoreSupported}">
                                        <form:checkbox path="points[${status.index}].ignore" disabled="${!hasEditingRole}"/>
                                    </c:when>
                                    <c:otherwise>
                                        <cti:msg2 var="notSupported" key=".ignoreNotSupported"/>
                                        <form:checkbox path="points[${status.index}].ignore" disabled="true" title="${notSupported}"/>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <div class="page-action-area">
                <c:if test="${hasEditingRole}">
                    <cti:button nameKey="save" type="submit" classes="primary action"/>
                    
                    <cti:url var="zoneVoltagePointsUrl" value="/capcontrol/ivvc/zone/voltagePoints">
                        <cti:param name="zoneId" value="${zoneId}"/>
                    </cti:url>
                    <cti:button nameKey="reset" href="${zoneVoltagePointsUrl}"/>
        
                    <cti:url var="zoneDetailUrl" value="/capcontrol/ivvc/zone/detail">
                        <cti:param name="zoneId" value="${zoneId}"/>
                    </cti:url>
                    <cti:button nameKey="back" id="backBtn" href="${zoneDetailUrl}"/>
                </c:if>
            </div>
        </form:form>
    </tags:boxContainer2>
</cti:standardPage>