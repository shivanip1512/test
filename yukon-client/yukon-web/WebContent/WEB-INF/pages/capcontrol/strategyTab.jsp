
<cti:tab title="Strategy">

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

    <c:if test="${canEdit}">
        <div class="action-area">
            <cti:button nameKey="edit" icon="icon-pencil" data-popup=".js-edit-strat-popup"
                data-popup-toggle="" />
        </div>
    </c:if>
</cti:tab>
