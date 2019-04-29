<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="inventory.list">
    
    <%-- SEACRH WIDGET --%>
    <tags:sectionContainer2 nameKey="inventorySearch">
        <form:form modelAttribute="inventorySearch" action="search" method="get" >
            <div class="column-8-8-8 clearfix">
                <div class="column one">
                    <div class="stacked">
                        <div class="name"><i:inline key=".serialNumber"/></div>
                        <form:input path="serialNumber"/>
                    </div>
                    <div class="stacked">
                        <div class="name"><i:inline key=".meterNumber"/></div>
                        <form:input path="meterNumber"/>
                    </div>
                    <div>
                        <div class="name"><i:inline key=".accountNumber"/></div>
                        <form:input path="accountNumber"/>
                    </div>
                </div>
                <div class="column two">
                    <div class="stacked">
                        <div class="name"><i:inline key=".phoneNumber"/></div>
                        <form:input path="phoneNumber"/>
                    </div>
                    <div class="stacked">
                        <div class="name"><i:inline key=".lastName"/></div>
                        <form:input path="lastName"/>
                    </div>
                </div>
                <div class="column three nogutter">
                    <div class="stacked">
                        <div class="name"><i:inline key=".workOrderNumber"/></div>
                        <form:input path="workOrderNumber"/>
                    </div>
                    <div class="stacked">
                        <div class="name"><i:inline key=".altTrackingNumber"/></div>
                        <form:input path="altTrackingNumber"/>
                    </div>
                </div>
            </div>
            <div class="action-area">
                <cti:button type="submit" nameKey="search" classes="action primary"/>
            </div>
        </form:form>
    </tags:sectionContainer2>
    
    <%-- RESULTS --%>
    <c:if test="${!hasWarnings}">
        
        <cti:url var="url" value="search">
            <cti:param name="serialNumber" value="${inventorySearch.serialNumber}"/>
            <cti:param name="meterNumber" value="${inventorySearch.meterNumber}"/>
            <cti:param name="accountNumber" value="${inventorySearch.accountNumber}"/>
            <cti:param name="phoneNumber" value="${inventorySearch.phoneNumber}"/>
            <cti:param name="lastName" value="${inventorySearch.lastName}"/>
            <cti:param name="workOrderNumber" value="${inventorySearch.workOrderNumber}"/>
            <cti:param name="altTrackingNumber" value="${inventorySearch.altTrackingNumber}"/>
        </cti:url>
        <tags:sectionContainer2 nameKey="inventorySearchContainer">
            <c:if test="${results.hitCount > 0}">
                <div data-url="${url}" data-static>
                    <table class="compact-results-table dashed">
                        <thead>
                            <tr>
                                <th class="row-icon"/>
                                <th><i:inline key=".serialOrMeterNumber"/></th>
                                <th><i:inline key=".hardwareType"/></th>
                                <th><i:inline key=".label"/></th>
                                <c:if test="${showAccountNumber}">
                                    <th><i:inline key=".accountNumber"/></th>
                                </c:if>
                                <c:if test="${showPhoneNumber}">
                                    <th><i:inline key=".phoneNumber"/></th>
                                </c:if>
                                <c:if test="${fn:escapeXml(showLastName)}">
                                    <th><i:inline key=".lastName"/></th>
                                </c:if>
                                <c:if test="${showWorkOrder}">
                                    <th><i:inline key=".workOrderNumber"/></th>
                                </c:if>
                                <c:if test="${showAltTrackingNumber}">
                                    <th><i:inline key=".altTrackingNumber"/></th>
                                </c:if>
                                <c:if test="${showEc}">
                                    <th><i:inline key=".energyCompany"/></th>
                                </c:if>
                            </tr>
                        </thead>
                        <tfoot></tfoot>
                        <tbody>
                            <c:forEach var="result" items="${results.resultList}">
                                <tr>
                                    <td>
                                        <c:if test="${notesList.contains(result.deviceId)}">
                                            <cti:msg2 var="viewAllNotesTitle" key="yukon.web.common.paoNotesSearch.viewAllNotes"/>
                                            <cti:icon icon="icon-notes-pin" classes="js-view-all-notes cp" title="${viewAllNotesTitle}" data-pao-id="${result.deviceId}"/>
                                        </c:if>
                                    </td>
                                    <td>
                                        <cti:url var="inventoryUrl" value="/stars/operator/inventory/view">
                                            <cti:param name="inventoryId" value="${result.identifier.inventoryId}"/>
                                        </cti:url>
                                        <c:if test="${result.accountId > 0}">
                                            <c:choose>
                                                <c:when test="${result.paoType == 'SYSTEM'}">
                                                    <cti:url var="inventoryUrl" value="/stars/operator/hardware/mp/view">
                                                        <cti:param name="inventoryId" value="${result.identifier.inventoryId}"/>
                                                        <cti:param name="accountId" value="${result.accountId}"/>
                                                    </cti:url>
                                                    <a href="${inventoryUrl}">${result.label}</a>
                                                </c:when>
                                                <c:otherwise>
                                                    <cti:url var="inventoryUrl" value="/stars/operator/hardware/view">
                                                        <cti:param name="inventoryId" value="${result.identifier.inventoryId}"/>
                                                        <cti:param name="accountId" value="${result.accountId}"/>
                                                    </cti:url>
                                                    <a href="${inventoryUrl}">${result.serialNumber}</a>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:if>
                                    </td>
                                    
                                    <td>
                                        <c:choose>
                                            <c:when test="${result.identifier.hardwareType.meter && !starsMeters}">
                                                <cti:msg2 key="${result.paoType}"/>
                                            </c:when>
                                            <c:otherwise>
                                                <cti:msg2 key="${result.identifier.hardwareType}"/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    
                                    <td>
                                        ${fn:escapeXml(result.label)}
                                        <c:if test="${result.identifier.hardwareType.meter && !starsMeters}">
                                            &nbsp;<cti:deviceName deviceId="${result.deviceId}"/>
                                        </c:if>
                                    </td>
                                    
                                    <c:if test="${showAccountNumber}">
                                        <c:choose>
                                            <c:when test="${result.accountId > 0}">
                                                <td>
                                                    <cti:url var="accountUrl" value="/stars/operator/account/view">
                                                        <cti:param name="accountId" value="${result.accountId}"/>
                                                    </cti:url>
                                                    <a href="${accountUrl}">${fn:escapeXml(result.accountNumber)}</a>
                                                </td>
                                            </c:when>
                                            <c:otherwise>
                                                <i:inline key="yukon.common.none"/>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:if>
                                    
                                    <c:if test="${showPhoneNumber}">
                                        <td><cti:formatPhoneNumber value="${result.phoneNumber}"/></td>
                                    </c:if>
                                    
                                    <c:if test="${fn:escapeXml(showLastName)}">
                                        <td>${fn:escapeXml(result.lastName)}</td>
                                    </c:if>
                                    
                                    <c:if test="${showWorkOrder}">
                                        <td>${fn:escapeXml(result.workOrderNumber)}</td>
                                    </c:if>
                                    
                                    <c:if test="${showAltTrackingNumber}">
                                        <td>${fn:escapeXml(result.altTrackingNumber)}</td>
                                    </c:if>
                                    
                                    <c:if test="${showEc}">
                                        <td>${fn:escapeXml(result.energyCompanyName)}</td>
                                    </c:if>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                    <tags:pagingResultsControls result="${results}" hundreds="true" adjustPageCount="true"/>
                </div>
            </c:if>
            <c:if test="${results.hitCount == 0}">
                <span class="empty-list"><i:inline key=".notFound"/></span>
            </c:if>
        </tags:sectionContainer2>
        
    </c:if>
<div class="dn" id="js-pao-notes-popup"></div>
<cti:includeScript link="/resources/js/pages/yukon.tools.paonotespopup.js"/>
</cti:standardPage>