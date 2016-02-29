<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="dr" page="cc.user.event.detail">

<div class="column-24">
    <div class="column one nogutter">
        <c:if test="${buyThroughExpired}">
            <i:inline key=".buyThroughExpired"/>
        </c:if>
        <table class="name-value-table natural-width stacked-md">
            <tr>
                <td class="name"><i:inline key=".eventNumber"/></td>
                <td class="value">${fn:escapeXml(displayName)}</td>
            </tr>
            <c:if test="${not empty startDate}">
                <tr>
                    <td class="name"><i:inline key=".startDate"/></td>
                    <td class="value"><cti:formatDate value="${startDate}" type="DATE"/>&nbsp;${tz}</td>
                </tr>
            </c:if>
            <c:if test="${not empty notificationTime}">
                <tr>
                    <td class="name"><i:inline key=".notificationTime"/></td>
                    <td class="value"><cti:formatDate value="${notificationTime}" type="DATEHM"/>&nbsp;${tz}</td>
                </tr>
            </c:if>
            <tr>
                <td class="name"><i:inline key=".startTime"/></td>
                <td class="value"><cti:formatDate value="${startTime}" type="DATEHM"/>&nbsp;${tz}</td>
            </tr>
            <c:if test="${not empty stopTime}">
                <tr>
                    <td class="name"><i:inline key=".stopTime"/></td>
                    <td class="value"><cti:formatDate value="${stopTime}" type="DATEHM"/>&nbsp;${tz}</td>
                </tr>
            </c:if>
            <c:if test="${not empty duration}">
                <tr>
                    <td class="name"><i:inline key=".duration"/></td>
                    <td class="value"><i:inline key=".durationMinutes" arguments="${duration}"/></td>
                </tr>
            </c:if>
            <c:if test="${not empty message}">
                <tr>
                    <td class="name"><i:inline key=".message"/></td>
                    <td class="value">${message}</td>
                </tr>
            </c:if>
            <c:if test="${not empty reason}">
                <tr>
                    <td class="name"><i:inline key=".reason"/></td>
                    <td class="value">${reason}</td>
                </tr>
            </c:if>
            <c:if test="${not empty latestRevision}">
                <tr>
                    <td class="name"><i:inline key=".latestRevision"/></td>
                    <td class="value"><i:inline key=".revision" arguments="${latestRevision}"/></td>
                </tr>
            </c:if>
        </table>
        
        <c:if test="${not empty pricingRows}">
            <cti:url var="url" value="/dr/cc/user/program/${programId}/event/${eventId}"/>
            <form:form modelAttribute="priceMap" action="${url}">
                <cti:csrfToken/>
                <table class="compact-results-table">
                    <thead>
                        <tr>
                            <th><i:inline key=".startTime"/>&nbsp;${tz}</th>
                            <th><i:inline key=".currentPrice"/></th>
                            <th><i:inline key=".energyBuyThrough"/></th>
                        </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:forEach var="row" items="${pricingRows}">
                            <tr>
                                <td><cti:formatDate value="${row.startTime}" type="DATEHM"/>&nbsp;${tz}</td>
                                <td>${row.energyPrice}</td>
                                <c:choose>
                                    <c:when test="${row.priceEditable}">
                                        <td>
                                            <form:input path="windowPrices['${row.windowId}']" value="${row.buyThrough}"/>
                                        </td>
                                    </c:when>
                                    <c:otherwise>
                                        <td>${row.buyThrough}</td>
                                    </c:otherwise>
                                </c:choose>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <c:if test="${atLeastOneWindowEditable}">
                    <div class="action-area">
                        <cti:button type="submit" nameKey="saveBuyThrough" classes="primary"/>
                    </div>
                </c:if>
            </form:form>
        </c:if>
    </div>
</div>

</cti:standardPage>