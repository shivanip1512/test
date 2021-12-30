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
    
    
    <cti:msg var="pointNameText" key="yukon.common.pointName"/>
    <cti:msg2 var="chartText" key=".chart"/>
    <div class="column-12-12 clearfix">
        <div class="column one">
            <!-- TODO: Alternate rows in the table are highlighted. Remove that styling -->
            <table class="compact-results-table js-chart-table-left">
                <thead>
                    <tr>
                        <th>${pointNameText}</th>
                        <th>${chartText}</th>
                    </tr>
                </thead>
                <tbody></tbody>
            </table>
        </div>
        <div class="column two nogutter">
            <!-- TODO: Alternate rows in the table are highlighted. Remove that styling -->
            <table class="compact-results-table js-chart-table-right">
                <thead>
                    <tr>
                        <th>${pointNameText}</th>
                        <th>${chartText}</th>
                    </tr>
                </thead>
                <tbody></tbody>
            </table>
        </div>
    </div>
    
    <cti:includeScript link="/resources/js/pages/yukon.support.systemPerformanceMetrics.js" />
</cti:standardPage>