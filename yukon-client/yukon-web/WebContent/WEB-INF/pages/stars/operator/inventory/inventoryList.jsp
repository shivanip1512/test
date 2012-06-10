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
        <form:form commandName="inventorySearch" action="search" method="post">
            <div class="fl" style="width:33%;">
                <tags:nameValueContainer2 tableClass="nonwrapping">
                    <tags:nameValue2 nameKey=".serialNumber">
                        <form:input path="serialNumber" size="20" class="f_focus"/>
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
                <tags:nameValueContainer2 tableClass="nonwrapping">
                    <tags:nameValue2 nameKey=".phoneNumber">
                        <form:input path="phoneNumber" size="20"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".lastName">
                        <form:input path="lastName" size="20"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </div>
            <div class="fl" style="width:33%;">
                <tags:nameValueContainer2 tableClass="nonwrapping">
                    <tags:nameValue2 nameKey=".workOrderNumber">
                        <form:input path="workOrderNumber" size="20"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".altTrackingNumber">
                        <form:input path="altTrackingNumber" size="20"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </div>
            <div class="actionArea clear"><cti:button type="submit" nameKey="search"/></div>
        </form:form>
    </tags:boxContainer2>
    
    <br>

    <%-- RESULTS --%>
    <c:if test="${!hasWarnings}">

        <tags:pagedBox2 baseUrl="search" nameKey="inventorySearchContainer" searchResult="${results}">

            <table class="compactResultsTable rowHighlighting" style="width: 100%;">

                <c:if test="${results.hitCount > 0}">

                    <tr>
                        <th><i:inline key=".serialOrMeterNumber" /></th>
                        <th><i:inline key=".hardwareType" /></th>
                        <th><i:inline key=".label" /> </th>
                        <c:if test="${showAccountNumber}">
                            <th><i:inline key=".accountNumber" /></th>
                        </c:if>
                        <c:if test="${showPhoneNumber}">
                            <th><i:inline key=".phoneNumber" /></th>
                        </c:if>
                        <c:if test="${showLastName}">
                            <th><i:inline key=".lastName" /></th>
                        </c:if>
                        <c:if test="${showWorkOrder}">
                            <th><i:inline key=".workOrderNumber" /></th>
                        </c:if>
                        <c:if test="${showAltTrackingNumber}">
                            <th><i:inline key=".altTrackingNumber" /></th>
                        </c:if>
                        <c:if test="${showEc}">
                            <th><i:inline key=".energyCompany" /></th>
                        </c:if>
                    </tr>

                    <c:forEach var="result" items="${results.resultList}">

                        <tr style="vertical-align: top;" class="<tags:alternateRow odd="" even="altRow"/>">

                            <td><cti:url var="inventoryUrl" value="/spring/stars/operator/inventory/view">
                                    <cti:param name="inventoryId" value="${result.identifier.inventoryId}" />
                                </cti:url> <c:if test="${result.accountId > 0}">
                                    <cti:url var="inventoryUrl" value="/spring/stars/operator/hardware/view">
                                        <cti:param name="inventoryId" value="${result.identifier.inventoryId}" />
                                        <cti:param name="accountId" value="${result.accountId}" />
                                    </cti:url>
                                </c:if> <a href="${inventoryUrl}">${result.serialNumber}</a>
                            </td>

                            <td>
                                <c:choose>
                                    <c:when test="${result.identifier.hardwareType.meter && !starsMeters}">
                                        <cti:msg2 key="${result.paoType}" />
                                    </c:when>
                                    <c:otherwise>
                                        <cti:msg2 key="${result.identifier.hardwareType}" />
                                    </c:otherwise>
                                </c:choose>
                            </td>

                            <td><spring:escapeBody htmlEscape="true">${result.label}</spring:escapeBody> <c:if
                                    test="${result.identifier.hardwareType.meter && !starsMeters}">
                                &nbsp;<cti:deviceName deviceId="${result.deviceId}" />
                                </c:if>
                            </td>

                            <c:if test="${showAccountNumber}">
                                <c:choose>
                                    <c:when test="${result.accountId > 0}">
                                        <td><cti:url var="accountUrl" value="/spring/stars/operator/account/view">
                                                <cti:param name="accountId" value="${result.accountId}" />
                                            </cti:url> <a href="${accountUrl}"><spring:escapeBody htmlEscape="true">${result.accountNumber}</spring:escapeBody>
                                        </a>
                                        </td>
                                    </c:when>
                                    <c:otherwise>
                                        <i:inline key="yukon.web.defaults.none" />
                                    </c:otherwise>
                                </c:choose>
                            </c:if>

                            <c:if test="${showPhoneNumber}">
                                <td><cti:formatPhoneNumber value="${result.phoneNumber}" /></td>
                            </c:if>

                            <c:if test="${showLastName}">
                                <td><spring:escapeBody htmlEscape="true">${result.lastName}</spring:escapeBody></td>
                            </c:if>

                            <c:if test="${showWorkOrder}">
                                <td><spring:escapeBody htmlEscape="true">${result.workOrderNumber}</spring:escapeBody></td>
                            </c:if>

                            <c:if test="${showAltTrackingNumber}">
                                <td><spring:escapeBody htmlEscape="true">${result.altTrackingNumber}</spring:escapeBody></td>
                            </c:if>

                            <c:if test="${showEc}">
                                <td><spring:escapeBody htmlEscape="true">${result.energyCompanyName}</spring:escapeBody></td>
                            </c:if>

                        </tr>

                    </c:forEach>

                </c:if>

                <c:if test="${results.hitCount == 0}">
                    <tr>
                        <td><i:inline key=".notFound" /></td>
                    <tr>
                </c:if>

            </table>

        </tags:pagedBox2>

    </c:if>

</cti:standardPage>