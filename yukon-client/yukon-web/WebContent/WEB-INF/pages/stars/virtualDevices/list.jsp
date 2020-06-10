<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="virtualDevices.list">

    <!-- Actions dropdown -->
    <cti:checkRolesAndProperties value="ENDPOINT_PERMISSION" level="CREATE">
        <div id="page-actions" class="dn">
            <cti:url var="createUrl" value="/stars/virtualDevices/create" />
            <cm:dropdownOption icon="icon-plus-green" key=".create" href="${createUrl}"/>
        </div>
    </cti:checkRolesAndProperties>
    
    <cti:url var="listUrl" value="/stars/virtualDevices"/>
    <div data-url="${listUrl}" data-static>
        <table class="compact-results-table row-highlighting">
            <thead>
                <tr>
                    <tags:sort column="${name}"/>
                    <tags:sort column="${status}"/>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="device" items="${virtualDevices.resultList}">
                    <tr>
                        <cti:url var="detailUrl" value="/stars/virtualDevices/${device.liteID}"/>
                        <td><a href="${detailUrl}">${fn:escapeXml(device.paoName)}</a></td>
                            <c:set var="cssClass" value="success" />
                            <cti:msg2 var="status" key="yukon.common.enabled"/>
                            <c:if test="${device.disableFlag == 'Y'}">
                                <c:set var="cssClass" value="error" />
                                <cti:msg2 var="status" key="yukon.common.disabled"/>
                            </c:if>
                        <td class="${cssClass}">${status}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <tags:pagingResultsControls result="${virtualDevices}" adjustPageCount="true" thousands="true"/>
    </div>
   
</cti:standardPage>