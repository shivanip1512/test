<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<cti:url var="controlHistoryView" value="/stars/consumer/controlhistory"/>

<cti:standardPage module="consumer" page="completecontrolhistory">
    <cti:includeScript link="/resources/js/pages/yukon.assets.controlhistory.consumer.js"/>
    <cti:standardMenu />
    <c:set var="controls">    
        <i:inline key="yukon.dr.consumer.completecontrolhistory.viewTitle"/>
        <select onchange="yukon.assets.controlHistory.consumer.updateControlEvents(this.options[this.options.selectedIndex].value)">
            <c:forEach var="controlPeriod" items="${controlPeriods}" >
                <option value="${controlPeriod}">
                    <i:inline key="${controlPeriod.formatKey}"/>
                </option>
            </c:forEach>
        </select>
    </c:set>

    <cti:msg key="${program.displayName}" htmlEscape="true" var="controlEventsTitle"/>
    <tags:sectionContainer title="${controlEventsTitle}" escapeTitle="true" controls="${controls}" styleClass="form-controls">
        <div id="controlEventsDiv" class="js-block-this" data-program-id="${program.programId}">
            <cti:msg key="yukon.dr.consumer.completecontrolhistory.loading"/>
        </div>
    </tags:sectionContainer>

    <div class="page-action-area">
        <cti:msg key="yukon.dr.consumer.completecontrolhistory.back" var="backText"/>
        <cti:button label="${backText}" onclick="location.href='${controlHistoryView}';"/>
    </div>
</cti:standardPage>    