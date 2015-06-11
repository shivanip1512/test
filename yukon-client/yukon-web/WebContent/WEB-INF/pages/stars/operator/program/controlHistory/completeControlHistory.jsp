<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<cti:standardPage module="operator" page="completeControlHistory">
    <cti:includeScript link="/resources/js/pages/yukon.assets.controlhistory.detail.js"/>
    
    <c:set var="controls">
        <i:inline key=".viewTitle"/>
        <div data-account-id="${accountId}"
             data-program-id="${program.programId}"
             data-past="${past}">
            <select onchange="yukon.assets.controlHistory.detail.updateControlEvents(this.options[this.options.selectedIndex].value);">
                <c:forEach var="controlPeriod" items="${controlPeriods}" >
                    <option value="${controlPeriod}">
                        <i:inline key="${controlPeriod.formatKey}"/>
                    </option>
                </c:forEach>
            </select>
        </div>
    </c:set>
    
    <tags:sectionContainer2 nameKey="controlEventsTitle" controls="${controls}" styleClass="form-controls">
        <div class="js-block-this js-control-events" style="min-height: 50px;"><i:inline key=".loading"/></div>
    </tags:sectionContainer2>

</cti:standardPage>