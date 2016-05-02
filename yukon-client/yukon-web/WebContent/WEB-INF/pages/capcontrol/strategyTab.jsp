<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg2 var="strategyTab" key=".strategyTab"/>
<cti:tab title="${strategyTab}">

<div class="borderedTab">

    <%-- SEASON SCHEDULE --%>
    <tags:nameValueContainer2 tableClass="stacked">
        <tags:nameValue2 nameKey=".schedule.season">
            <c:set var="clazz" value="${seasonSchedule.exists ? 'hint' : ''}" />
            <span class="${clazz}">${fn:escapeXml(seasonSchedule.scheduleName)}</span>
        </tags:nameValue2>
    </tags:nameValueContainer2>
    <table class="full-width dashed stacked">
        <thead>
            <tr>
                <th><i:inline key=".season" /></th>
                <th><i:inline key=".strategy" /></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach var="season" items="${seasons.keySet()}">
                <c:set var="strat" value="${seasons[season]}" />
                <tr>
                    <td>${fn:escapeXml(season.seasonName)}</td>
                    <td><c:choose>
                            <c:when test="${empty strat}">
                                <span class="empty-list"><i:inline
                                        key="yukon.common.none.choice" /></span>
                            </c:when>
                            <c:otherwise>
                                <cti:url var="url" value="/capcontrol/strategies/${strat.id}" />
                                <a href="${url}">${fn:escapeXml(strat.name)}</a>
                            </c:otherwise>
                        </c:choose></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <%-- HOLIDAY SCHEDULE --%>
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".schedule.holiday">
            <c:set var="clazz" value="${holidaySchedule.exists ? 'hint' : ''}" />
            <span class="${clazz}">${fn:escapeXml(holidaySchedule.holidayScheduleName)}</span>
        </tags:nameValue2>
        <c:if test="${not empty holidayStrat}">
            <tags:nameValue2 nameKey=".strategy">
                <cti:url var="url" value="/capcontrol/strategies/${holidayStrat.id}" />
                <a href="${url}">${fn:escapeXml(holidayStrat.name)}</a>
            </tags:nameValue2>
        </c:if>
    </tags:nameValueContainer2>

    <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
        <div class="action-area">
            <cti:button nameKey="edit" icon="icon-pencil" data-popup=".js-edit-strat-popup"
                data-popup-toggle="" />
        </div>
    </cti:checkRolesAndProperties>
    
    </div>
</cti:tab>
