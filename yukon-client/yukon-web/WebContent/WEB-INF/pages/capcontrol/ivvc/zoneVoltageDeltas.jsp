<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="capTags" tagdir="/WEB-INF/tags/capcontrol"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<cti:standardPage module="capcontrol" page="ivvc.zoneVoltageDeltas">
    <cti:includeScript link="/resources/js/pages/yukon.da.voltage.deltas.js"/>

    <cti:url var="action" value="/capcontrol/ivvc/zone/deltaUpdate"/>
    <form:form id="delta-form" action="${action}" method="POST" modelAttribute="zoneVoltageDeltas">
        <cti:csrfToken/>
        <input type="hidden" name="zoneId" id="zoneId" value="${zoneId}">

        <%@ include file="zoneVoltageDeltasTable.jsp" %>

        <div id="delta-form-buttons" class="action-area clear">
            <cti:button id="delta-submit" nameKey="update" type="submit" classes="primary action" disabled="true"/>
            <cti:button id="delta-reset" nameKey="cancel" type="reset" disabled="true"/>
        </div>
    </form:form>
</cti:standardPage>
