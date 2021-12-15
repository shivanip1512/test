<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="support" page="systemPerformanceMetrics">
    <hr>
        <cti:url value="/support/systemPerformanceMetrics/view" var="url"/>
        <form:form action="${url}" method="GET">
        <div class="MT10 MB10">
            <div class="dib vam MR5">
                <i:inline key="yukon.common.selectDateRange"/>:
            </div>
            <div class="dib vam">
                <cti:msg key="yukon.common.to" var="toText"/>
                <dt:dateRange startValue="${startDate}" endValue="${endDate}" toText="${toText}" 
                              toStyle="margin-left: 5px; margin-right: 10px; margin-top: 10px;" 
                              startName="startDate" endName="endDate"/>
            </div>
            <div class="dib fr">
                <cti:button nameKey="view" classes="primary" type="submit"/>
            </div>
        </div>
        </form:form>
    <hr>
</cti:standardPage>