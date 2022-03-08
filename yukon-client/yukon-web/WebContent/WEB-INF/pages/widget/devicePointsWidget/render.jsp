<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:pointCreation paoId="${device.liteID}" buttonClasses="PB10 fr"/>

<hr>
<cti:url var="dataUrl" value="/widget/devicePointsWidget/pointsTable">
    <cti:param name="deviceId" value="${device.liteID}"/>
</cti:url>
<form id="devicePointsForm" action="${dataUrl}" method="get">
    <input type="hidden" name="deviceId" value="${device.liteID}"/>
    <i:inline key="yukon.common.filterBy"/>
    <cti:msg2 var="selectTypes" key="yukon.common.selectPointTypes"/>
    <select id="pointTypeSelector" name="pointTypes" data-placeholder="${selectTypes}" multiple="multiple">
        <c:forEach var="type" items="${pointTypes}">
            <option value="${type}"><i:inline key="${type.formatKey}"/></option>
        </c:forEach>
    </select>
    <cti:button nameKey="filter" classes="action primary js-filter fn vab ML15"/>
    <cti:button nameKey="download" icon="icon-page-white-excel" classes="js-download fn vab"/>
</form>
<hr>

<div data-url="${dataUrl}" class="js-points-table">
    <%@ include file="pointsTable.jsp" %>
</div>

<dt:pickerIncludes/>
<tags:simplePopup id="manual-entry-popup" title=""/>
<tags:simplePopup id="manual-control-popup" title=""/>

<cti:includeScript link="/resources/js/pages/yukon.points.js"/>
<cti:includeScript link="/resources/js/widgets/yukon.widget.devicePoints.js"/>