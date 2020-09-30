<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="aggregateIntervalReport">
    <form:form id="filter-form" action="exportNow" method="post" modelAttribute="filter">
        <cti:csrfToken/>
        <tags:nameValueContainer2 tableClass="MT5">
            <tags:nameValue2 nameKey="yukon.common.Devices">
                <tags:selectDevicesTabbed deviceCollection="${deviceCollection}" 
                    pickerType="meterPicker" defaultGroupTab="true"
                    groupCallback="yukon.tools.aggregateIntervalReport.groupSelected" 
                    deviceCallback="yukon.tools.aggregateIntervalReport.devicesSelected"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey="yukon.common.attribute">
                 <spring:bind path="attribute">
                    <tags:attributeSelector attributes="${groupedAttributes}" 
                        selectedAttributes="${selectedAttributes}"
                        name="attribute"
                        groupItems="true"
                        htmlEscape="true" />
                    <c:if test="${status.error}">
                        <div><form:errors path="attribute" cssClass="error"/></div>
                    </c:if>
                </spring:bind>
            </tags:nameValue2>
            <tags:nameValue2 nameKey="yukon.common.dateRange">
                <dt:dateTime name="startDate" value="${filter.startDate}"/>
                <dt:dateTime name="endDate" value="${filter.endDate}"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey="yukon.common.interval">
                <form:select path="interval">
                    <c:forEach var="interval" items="${intervals}">
                        <form:option value="${interval}"><i:inline key=".interval.${interval}"/></form:option>
                    </c:forEach>
                </form:select>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".missingIntervalData">
                <tags:selectWithItems path="missingIntervalData" items="${missingDataOptions}" inputClass="js-missing-data MR0"/>
                <input type="hidden" id="fixedValueOption" value="${fixedValueOption}"/>
                <c:set var="missingDataValueClass" value="${filter.missingIntervalData == fixedValueOption ? '' : 'dn'}"/>
                <spring:bind path="missingIntervalDataValue">
                    <c:set var="errorClass" value="${status.error ? 'error' : ''}"/>
                    <form:input path="missingIntervalDataValue" cssClass="js-missing-data-value ${missingDataValueClass} ${errorClass}"/>
                </spring:bind>
                <cti:icon icon="icon-help" data-popup="#missing-data-help" classes="fn cp vam"/>
                <cti:msg2 var="helpTitle" key=".missingData.helpTitle"/>
                <div id="missing-data-help" class="dn" data-title="${helpTitle}"><cti:msg2 key=".missingData.helpText"/></div>
                <spring:bind path="missingIntervalDataValue">
                    <c:if test="${status.error}">
                        <div style="padding-left:105px;"><form:errors path="missingIntervalDataValue" cssClass="error"/></div>
                    </c:if>
                </spring:bind>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".operation">
               <tags:selectWithItems path="operation" items="${operations}" inputClass="MR0"/>
               <cti:icon icon="icon-help" data-popup="#operation-help" classes="fn cp vam"/>
               <cti:msg2 var="helpTitle" key=".operation.helpTitle"/>
               <div id="operation-help" class="dn" data-title="${helpTitle}"><cti:msg2 key=".operation.helpText"/></div>
            </tags:nameValue2>
        </tags:nameValueContainer2>
        <div class="page-action-area">
            <cti:button type="submit" nameKey="exportNow" classes="action" icon="icon-page-white-excel"/>
        </div>    
    </form:form>
    
    <cti:includeScript link="/resources/js/pages/yukon.tools.aggregateIntervalReport.js"/>
    
</cti:standardPage>