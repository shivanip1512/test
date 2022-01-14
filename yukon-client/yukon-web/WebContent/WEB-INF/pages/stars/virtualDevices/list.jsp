<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="virtualDevices.list">

    <!-- Actions dropdown -->
    <cti:checkRolesAndProperties value="ENDPOINT_PERMISSION" level="CREATE">
        <div id="page-actions" class="dn">
            <cm:dropdownOption icon="icon-plus-green" key="yukon.common.create" data-popup="#js-create-virtual-device-popup"/>
            <cti:url var="createUrl" value="/widget/virtualDeviceInfoWidget/create" />
            <cti:msg2 var="saveText" key="components.button.save.label"/>
            <cti:msg2 var="createPopupTitle" key="yukon.web.modules.operator.virtualDevices.list.create"/>
            <div class="dn" id="js-create-virtual-device-popup" data-title="${createPopupTitle}" data-dialog data-ok-text="${saveText}"
                data-event="yukon:virtualDevice:save" data-url="${createUrl}"></div>
        </div>
    </cti:checkRolesAndProperties>
    
    <cti:url var="listUrl" value="/stars/virtualDevices"/>
    <div data-url="${listUrl}" data-static>
        <table class="compact-results-table row-highlighting">
            <thead>
                <tr>
                    <tags:sort column="${name}"/>
                    <tags:sort column="${meterNumber}"/>
                    <tags:sort column="${status}"/>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="device" items="${virtualDevices.items}">
                    <tr>
                        <cti:url var="detailUrl" value="/stars/virtualDevice/${device.deviceId}"/>
                        <td><a href="${detailUrl}">${fn:escapeXml(device.deviceName)}</a></td>
                        <td>
                            <c:if test="${device.deviceType == virtualMeterType}">
                                ${fn:escapeXml(device.meterNumber)}
                            </c:if>
                        </td>
                        <c:set var="cssClass" value="success" />
                        <cti:msg2 var="status" key="yukon.common.enabled"/>
                        <c:if test="${!device.enable}">
                            <c:set var="cssClass" value="error" />
                            <cti:msg2 var="status" key="yukon.common.disabled"/>
                        </c:if>
                        <td class="${cssClass}">${status}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <c:if test="${empty virtualDevices.items}">
            <span class="empty-list compact-results-table"><i:inline key="yukon.common.search.noResultsFound"/></span>
        </c:if>
        <tags:paginatedResponseControls response="${virtualDevices}" adjustPageCount="true" thousands="true"/>
    </div>

    <cti:includeScript link="/resources/js/pages/yukon.assets.virtualDevice.js"/>
</cti:standardPage>