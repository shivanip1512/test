<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="cellConnection">

    <tags:alertBox type="success" key=".queryMsg" classes="js-refresh-msg dn" includeCloseButton="true"></tags:alertBox>
    <input type="hidden" id="gatewayId" value="${gateway.id}"/>
    <input type="hidden" id="deviceIds" value="${deviceIds}"/>
    <input type="hidden" id="connectedStatusValue" value="${connectedStatusValue}"/>
    <input type="hidden" id="baseUrl" value="/stars/cellConnection"/>

    <div>
        <form id="filterConnectionDevices">
            <hr/>
            <i:inline key="yukon.common.filterBy"/>&nbsp;
            <cti:msg2 var="allCellTypes" key="yukon.web.modules.operator.connectedDevices.allCellTypes"/>
            <select id="cellTypesFilter" name="deviceTypes" multiple="multiple" data-placeholder="${allCellTypes}">
                <c:forEach var="type" items="${cellTypes}">
                    <option value="${type}"><i:inline key="${type.formatKey}"/></option>
                </c:forEach>
            </select>&nbsp;&nbsp;
            
            <cti:msg2 var="allStatuses" key="yukon.web.modules.operator.connectedDevices.allStatuses"/>
            <select id="commStatusFilter" name="commStatuses" multiple="multiple" data-placeholder="${allStatuses}">
                <c:forEach var="status" items="${commStatusValues}">
                    <option value="${status.liteID}">${fn:escapeXml(status.stateText)}</option>
                </c:forEach>
            </select>
            <hr/>
        </form>
    </div>

    <div id="filtered-results">
        <%@ include file="cellConnectionDeviceTable.jsp" %>
    </div>

    <cti:includeScript link="/resources/js/lib/sortable/sortable.js"/>
    <cti:includeCss link="/resources/js/lib/sortable/sortable.css"/>
    <cti:includeScript link="/resources/js/pages/yukon.assets.gateway.connectedDevices.js"/>

</cti:standardPage>