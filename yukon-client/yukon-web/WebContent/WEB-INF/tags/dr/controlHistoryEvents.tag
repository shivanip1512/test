<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>

<%@ attribute name="showControlSummary" required="true" type="java.lang.Boolean"%>
<%@ attribute name="consumer" type="java.lang.Boolean"%>
<%@ attribute name="groupedHistoryEventList" type="java.util.Set"%>
<%@ attribute name="showGroupedHistory" type="java.lang.Boolean"%>
<%@ attribute name="programId" required="true"%>

<c:choose>
    <c:when test="${consumer}">
        <c:set var="columns" value="3" />
    </c:when>
    <c:otherwise>
        <c:set var="columns" value="4" />
    </c:otherwise>
</c:choose>

<cti:msgScope paths=", .controlHistoryEvent, components.controlHistoryEvent">
    <c:set var="controlHistoryEventListSize" value="${fn:length(groupedHistoryEventList)}" />
    <table class="compact-results-table">
        <c:choose>
            <c:when test="${controlHistoryEventListSize > 0}">
                <tr>
                    <td colspan="${columns}">
                        <table class="name-value-table">
                            <c:set var="totalDuration" value="${0}" />
                            <thead>
                                <tr>
                                    <th><i:inline key=".startDate" /></th>
                                    <th><i:inline key=".endDate" /></th>
                                    <c:if test="${not consumer}">
                                        <th></th>
                                    </c:if>
                                    <th><i:inline key=".controlDuration" /></th>
                                </tr>
                            </thead>
                            <tfoot></tfoot>
                            <tbody>
                                <c:forEach var="groupedEvent" items="${groupedHistoryEventList}" varStatus="status">
                                    <jsp:useBean id="status" type="javax.servlet.jsp.jstl.core.LoopTagStatus" />
                                    <c:choose>
                                        <c:when test="${(!showGroupedHistory && consumer)}">
                                            <c:forEach var="detailEvent" items="${groupedEvent.controlHistory}">
                                                <tr>
                                                    <td><cti:formatDate value="${detailEvent.startDate}" type="BOTH" /></td>
                                                    <c:choose>
                                                        <c:when test="${detailEvent.controlling}">
                                                            <td>----</td>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <td><cti:formatDate value="${detailEvent.endDate}" type="BOTH" /></td>
                                                        </c:otherwise>
                                                    </c:choose>
                                                    <td><cti:formatDuration type="HM" startDate="${detailEvent.startDate}"
                                                            endDate="${detailEvent.endDate}" /></td>
                                                </tr>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <tr>
                                                <td><cti:formatDate value="${groupedEvent.startDate}" type="BOTH" /></td>
                                                <c:choose>
                                                    <c:when test="${groupedEvent.controlling}">
                                                        <td>----</td>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <td><cti:formatDate value="${groupedEvent.endDate}" type="BOTH" /></td>
                                                    </c:otherwise>
                                                </c:choose>
                                                <c:if test="${not consumer}">
                                                    <td>
                                                        <div title="<cti:msg2 key=".detailDialog.title"/>" class="dn">
                                                            <c:set var="detailDuration" value="${0}"/>
                                                            <table class="compact-results-table">
                                                                <thead>
                                                                    <tr>
                                                                        <th><i:inline key=".startDate" /></th>
                                                                        <th><i:inline key=".endDate" /></th>
                                                                        <th><i:inline key=".gears" /></th>
                                                                        <th><i:inline key=".controlDuration" /></th>
                                                                    </tr>
                                                                </thead>
                                                                <tfoot></tfoot>
                                                                <tbody>
                                                                    <c:forEach var="detailEvent" items="${groupedEvent.controlHistory}">
                                                                        <tr>
                                                                            <td><cti:formatDate value="${detailEvent.startDate}" type="BOTH" /></td>
                                                                            <c:choose>
                                                                                <c:when test="${detailEvent.controlling}">
                                                                                    <td>----</td>
                                                                                </c:when>
                                                                                <c:otherwise>
                                                                                    <td><cti:formatDate value="${detailEvent.endDate}" type="BOTH" /></td>
                                                                                </c:otherwise>
                                                                            </c:choose>
                                                                            <td>
                                                                                <div class="js-gear-names" 
                                                                                    data-program-id="${programId}"
                                                                                    data-start-date="${detailEvent.startDate.millis}"
                                                                                    data-end-date="${detailEvent.endDate.millis}">
                                                                                    <i:inline key="yukon.web.components.controlHistoryEvent.loading"/>
                                                                                </div>
                                                                            </td>
                                                                            <td><cti:formatDuration type="HM" startDate="${detailEvent.startDate}"
                                                                                    endDate="${detailEvent.endDate}" /></td>
                                                                            <c:set var="detailDuration" value="${detailEvent.duration.millis + detailDuration}" />
                                                                        </tr>
                                                                    </c:forEach>
                                                                    <c:if test="${showControlSummary}">
                                                                        <tr>
                                                                            <td></td>
                                                                            <td></td>
                                                                            <td></td>
                                                                            <td style="font-weight: bold">
                                                                                <cti:formatDuration type="HM" value="${detailDuration}"/>
                                                                            </td>
                                                                        </tr>
                                                                    </c:if>
                                                                </tbody>
                                                            </table>
                                                        </div> <a href="javascript:void(0);" class="js-show-details"><cti:msg2 key=".history" /></a></td>
                                                </c:if>
                                                <td><cti:formatDuration type="HM" startDate="${groupedEvent.startDate}"
                                                        endDate="${groupedEvent.endDate}" /></td>
                                        </c:otherwise>
                                    </c:choose>
                                    <c:set var="totalDuration" value="${groupedEvent.duration.millis + totalDuration}" />
                                    </tr>
                                </c:forEach>
                                <c:if test="${showControlSummary}">
                                    <tr>
                                        <td></td>
                                        <td></td>
                                        <c:if test="${not consumer}">
                                            <td></td>
                                        </c:if>
                                        <td style="font-weight: bold"><cti:formatDuration type="HM" value="${totalDuration}" /></td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </td>
                </tr>
            </c:when>
            <c:otherwise>
                <tr class="${rowClass}">
                    <td colspan="${columns}"><i:inline key=".noControlDuringPeriod" /></td>
                </tr>
            </c:otherwise>
        </c:choose>
    </table>
</cti:msgScope>