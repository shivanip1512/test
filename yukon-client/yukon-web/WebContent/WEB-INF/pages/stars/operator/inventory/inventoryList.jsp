<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<cti:standardPage module="operator" page="inventory.list">
	
    <%-- SEACRH WIDGET --%>
    <tags:boxContainer2 nameKey="inventorySearch">
        <form:form commandName="inventorySearch" action="search" method="get" >
            <div class="fl" style="width:33%;">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".serialNumber">
                        <form:input path="serialNumber" size="20" class="f-focus"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".meterNumber">
                        <form:input path="meterNumber" size="20"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".accountNumber">
                        <form:input path="accountNumber" size="20"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </div>
            <div class="fl" style="width:33%;">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".phoneNumber">
                        <form:input path="phoneNumber" size="20"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".lastName">
                        <form:input path="lastName" size="20"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </div>
            <div class="fl" style="width:33%;">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".workOrderNumber">
                        <form:input path="workOrderNumber" size="20"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".altTrackingNumber">
                        <form:input path="altTrackingNumber" size="20"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </div>
            <div class="action-area clear"><cti:button type="submit" nameKey="search"/></div>
        </form:form>
    </tags:boxContainer2>
    
    <%-- RESULTS --%>
    <c:if test="${!hasWarnings}">

        <tags:pagedBox2 baseUrl="search" nameKey="inventorySearchContainer" searchResult="${results}">
            <c:if test="${results.hitCount > 0}">
                <table class="compact-results-table row-highlighting">
                    <thead>
	                    <tr>
	                        <th><i:inline key=".serialOrMeterNumber"/></th>
	                        <th><i:inline key=".hardwareType"/></th>
	                        <th><i:inline key=".label"/></th>
	                        <c:if test="${showAccountNumber}">
	                            <th><i:inline key=".accountNumber"/></th>
	                        </c:if>
	                        <c:if test="${showPhoneNumber}">
	                            <th><i:inline key=".phoneNumber"/></th>
	                        </c:if>
	                        <c:if test="${showLastName}">
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
                                    <cti:url var="inventoryUrl" value="/stars/operator/inventory/view">
                                        <cti:param name="inventoryId" value="${result.identifier.inventoryId}"/>
	                                </cti:url>
	                                <c:if test="${result.accountId > 0}">
	                                    <cti:url var="inventoryUrl" value="/stars/operator/hardware/view">
	                                        <cti:param name="inventoryId" value="${result.identifier.inventoryId}"/>
	                                        <cti:param name="accountId" value="${result.accountId}"/>
	                                    </cti:url>
	                                </c:if>
	                                <a href="${inventoryUrl}">${result.serialNumber}</a>
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
	                                        <i:inline key="yukon.web.defaults.none"/>
	                                    </c:otherwise>
	                                </c:choose>
	                            </c:if>
	
	                            <c:if test="${showPhoneNumber}">
	                                <td><cti:formatPhoneNumber value="${result.phoneNumber}"/></td>
	                            </c:if>
	
	                            <c:if test="${showLastName}">
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
            </c:if>
            <c:if test="${results.hitCount == 0}">
                <span class="empty-list"><i:inline key=".notFound"/></span>
            </c:if>
        </tags:pagedBox2>

    </c:if>

</cti:standardPage>