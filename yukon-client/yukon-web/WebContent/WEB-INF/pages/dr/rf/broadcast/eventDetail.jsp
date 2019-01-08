<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<cti:standardPage module="dr" page="rf.broadcast.eventDetail">
    <cti:url var="formAction" value="/dr/rf/broadcast/eventDetail"/>
    <form id="js-broadcast-event-detail-filter-form" action="${formAction}" method="GET">
        <input type="hidden" name="eventId" value="${eventId}"/>
        <div class="column-12-12 clearfix">
            <div class="column one" style="margin-top:45px;">
                <tags:nameValueContainer2 tableClass="name-collapse">
                    <tags:nameValue2 nameKey=".eventTime">
                        <cti:url var="downloadAll" value="/dr/rf/broadcast/eventDetail/${eventId}/downloadAll"/>
                            <span>
                                <td><span class="fl"><cti:formatDate type="FULL" value="${event.timeMessageSent}"/></span>
                                <cti:icon icon="icon-csv" nameKey="download" href="${downloadAll}"/></td>
                            </span>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".results">
                        <td><dr:rfPerformanceStats test="${event.eventStats}" unReportedDeviceText="${unReportedDeviceText}"/></td>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </div>
            <div class="column two nogutter filter-container MT20">
                <span class="fr cp"><cti:icon icon="icon-help" data-popup="#filter-results-help"/></span>
                <cti:msg2 var="helpTitle" key=".helpTitle"/>
                <div id="filter-results-help" class="dn" data-width="600" data-height="360" data-title="${helpTitle}">${helpText}</div>
                <br/>
                <tags:nameValueContainer2 tableClass="name-collapse">
                    <tags:nameValue2 nameKey=".deviceGroups">
                        <cti:list var="groups">
                            <c:forEach var="subGroup" items="${deviceSubGroups}">
                                <cti:item value="${subGroup}"/>
                            </c:forEach>
                        </cti:list>
                        <tags:deviceGroupPicker inputName="deviceSubGroups" inputValue="${groups}" multi="true"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".status">
                        <div class="button-group stacked">
                            <c:forEach var="status" items="${statusTypes}">
                                <c:set var="checked" value="${false}"/>
                                <c:forEach var="statusEnum" items="${statuses}">
                                    <c:if test="${statusEnum eq status}">
                                        <c:set var="checked" value="${true}"/>
                                    </c:if>
                                </c:forEach>
                                <c:set var="buttonTextColor" value="green"/>
                                <c:if test="${status == 'FAILURE'}">
                                    <c:set var="buttonTextColor" value="orange"/>
                                </c:if>
                                <c:if test="${status == 'UNKNOWN'}">
                                    <c:set var="buttonTextColor" value="grey"/>
                                </c:if>
                                <tags:check name="statuses" key=".status.${status}" classes="M0" 
                                            buttonTextClasses="${buttonTextColor}" checked="${checked}" value="${status}"/>
                            </c:forEach>
                        </div>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
                <div class="action-area">
                    <cti:button classes="primary action js-filter-results" nameKey="filter" busy="true"/>
                </div>
            </div>
        </div>
    </form>

    <div id="js-filtered-results"></div>
    <cti:includeScript link="/resources/js/pages/yukon.tools.paonotespopup.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.dr.rf.broadcast.eventDetail.js"/>
</cti:standardPage>