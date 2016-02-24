<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:standardPage module="dr" page="cc.user.event.detail">

<div class="column-24">
    <div class="column one nogutter">
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
            <c:if test="${not empty stateDescription}">
                <tr>
                    <td class="name"><i:inline key=".state"/></td>
                    <td class="value">${stateDescription}</td>
                </tr>
            </c:if>
            <c:if test="${not empty message}">
                <tr>
                    <td class="name"><i:inline key=".message"/></td>
                    <td class="value">${message}</td>
                </tr>
            </c:if>
            <c:if test="${not empty economicDetail}">
                <tr>
                    <td class="name"><i:inline key=".initialEvent"/></td>
                    <td class="value">${event.initialEvent.displayName}</td>
                </tr>
                <tr>
                    <td class="name"><i:inline key=".latestRevision"/></td>
                    <td class="value">Revision&nbsp;${event.latestRevision.revision}</td>
                </tr>
            </c:if>
            <c:if test="${not empty reason}">
                <tr>
                    <td class="name"><i:inline key=".reason"/></td>
                    <td class="value">${reason}</td>
                </tr>
            </c:if>
        </table>
        
        <%-- TODO: support economic events
        <c:if test="${not empty pricingTableHead}">
            <%@ include file="economicDetail.jspf" %> 
        </c:if>
        --%>
    </div>
</div>

</cti:standardPage>