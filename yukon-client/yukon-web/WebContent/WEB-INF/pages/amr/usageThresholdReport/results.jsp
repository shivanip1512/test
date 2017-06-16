<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<cti:standardPage module="amr" page="usageThresholdReport.results">

    <div class="column-12-12 clearfix">
        <div class="column one" style="border:1px solid #ccc;padding:5px;min-height:160px;">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".attribute">
                     <cti:msg2 key="${criteria.attribute.formatKey}"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".startDate">
                    <cti:formatDate type="DATE" value="${criteria.startDate}"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".endDate">
                    <cti:formatDate type="DATE" value="${criteria.endDate}"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".reportRunDate">
                    <cti:formatDate type="DATEHM_12" value="${criteria.runTime}"/>
                    <cti:url var="downloadAllUrl" value="downloadAll">
                        <cti:param name="reportId" value="${criteria.reportId}"/>
                    </cti:url>
                    <cti:icon icon="icon-csv" href="${downloadAllUrl}" classes="fr" nameKey="downloadAll"/>
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </div>
        <div class="column two nogutter" style="border:1px solid #ccc;padding:5px;min-height:160px;">
            <span class="fr cp"><cti:icon icon="icon-help" data-popup="#results-help"/></span>
            <cti:msg2 var="helpTitle" key=".helpTitle"/>
            <div id="results-help" class="dn" data-dialog data-title="${helpTitle}"><cti:msg2 key=".helpText"/></div>
            <form:form id="filter-results-form" action="results" method="get" commandName="filter">
                <cti:csrfToken/>
                <input type="hidden" name="reportId" value="${criteria.reportId}"/>
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".filter.deviceGroups">
                        <tags:deviceGroupPicker inputName="deviceSubGroups" multi="true"/>
                    </tags:nameValue2>
                    <tags:nameValue2 excludeColon="true">
                        <tags:checkbox path="includeDisabled"/><cti:msg2 key=".filter.includeDisabled"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".filter.threshold">
                        <tags:selectWithItems items="${thresholdOptions}" path="thresholdDescriptor" itemLabel="value"/>
                        <tags:input path="threshold"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".filter.dataAvailability">
                        <div class="button-group stacked">
                            <c:forEach var="availability" items="${dataAvailabilityOptions}">
                                <tags:check name="availability" key=".dataAvailability.${availability}" classes="M0" value="${availability}" checked="true" buttonTextStyle="color:${availability.color}"></tags:check>
                            </c:forEach>
                        </div>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
                <cti:button classes="primary action js-filter fr" nameKey="filter"/>
            </form:form>
        </div>
    </div>
    
    <br/>
    <div id="usage-device-table">
    
    </div>

    <cti:includeScript link="/resources/js/pages/yukon.ami.usage.threshold.report.js"/>

</cti:standardPage>