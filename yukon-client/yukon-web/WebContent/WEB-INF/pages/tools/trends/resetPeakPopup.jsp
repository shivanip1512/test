<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:flashScopeMessages/>
<cti:uniqueIdentifier var="uniqueId"/>
<cti:msgScope paths="yukon.common,modules.tools.trend">

    <input class="js-enum-val-today" type="hidden" value="${todayEnumVal}" />
    <input class="js-enum-val-first-day-of-month" type="hidden" value="${firstDayOfMonthEnumVal}" />
    <input class="js-enum-val-first-day-of-year" type="hidden" value="${firstDayOfYearEnumVal}" />
    <input class="js-enum-val-selected-date" type="hidden" value="${selectedDateEnumVal}" />
    
    <input class="js-todays-date-val" type="hidden" value="${todaysDate}" />
    <input class="js-first-date-of-month-val" type="hidden" value="${firstDateOfMonth}" />
    <input class="js-first-date-of-year-val" type="hidden" value="${firstDateOfYear}" />

    <cti:url value="/tools/trend/resetPeak" var="resetPeakUrl" />
    <form:form modelAttribute="resetPeakPopupModel" method="POST" action="${resetPeakUrl}" cssClass="js-reset-peak-form">
        <cti:csrfToken />
        <tags:hidden path="trendId" />
        <tags:nameValueContainer2 tableClass="MT5">
            <tags:nameValue2 nameKey=".resetPeakTo">
                <tags:selectWithItems items="${resetPeakDurationValues}" path="resetPeakDuration" inputClass="js-reset-peak-duration"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".date">
                <dt:date cssClass="js-reset-peak-date" maxDate="${now}" disabled="${!isDurationSelectedDate}"
                                id="js-date_${uniqueId}" path="startDate"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".resetPeakForAllTrend">
                <div class="button-group">
                    <tags:radio path="resetPeakForAllTrends" value="true" key=".yes" classes="M0 yes"/>
                    <tags:radio path="resetPeakForAllTrends" value="false" checked="true" key=".no" classes="no"/>
                </div>
            </tags:nameValue2>
        </tags:nameValueContainer2>
    </form:form>
</cti:msgScope>