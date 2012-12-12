<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="alternateEnrollment">

<dialog:confirm nameKey="confirmAlternate" on="#enroll_alternate"/>
<dialog:confirm nameKey="confirmNormal" on="#enroll_normal"/>

<tags:boxContainer2 nameKey="available">
    <c:choose>
        <c:when test="${empty available}">
            <span class="list-none"><i:inline key=".available.none"/></span>
        </c:when>
        <c:otherwise>
            <c:set var="multiple" value="${fn:length(available) > 1 ? true : false}"/>
            <form action="enroll" method="post">
                <input type="hidden" name="accountId" value="${accountId}">
	            <table class="compactResultsTable">
	                <thead>
	                    <tr>
	                        <th><i:inline key=".device"/></th>
	                        <th><i:inline key=".enrolled"/></th>
	                        <th><i:inline key=".alternate"/></th>
	                    </tr>
	                </thead>
	                <tfoot></tfoot>
	                <tbody>
	                    <c:forEach var="row" items="${available}">
	                        <tr>
		                        <c:set var="inventory" value="${row.key}"/>
		                        <c:set var="pair" value="${row.value}"/>
		                        <td>
		                           <input id="inv_${inventory.inventoryId}" type="checkbox" value="${inventory.inventoryId}" name="alternate" <c:if test="${!multiple}">checked="checked" class="dn"</c:if>
		                           ><label for="inv_${inventory.inventoryId}">${fn:escapeXml(inventory.displayName)}</label></td>
		                        <td>
		                            <c:forEach var="normal" items="${pair.first}" varStatus="status">
	                                    <span class="wsnw"><i:inline key="${normal.displayName}"/><c:if test="${not status.last}"><i:inline key="yukon.common.comma"/>&nbsp;</c:if></span>
		                            </c:forEach>
		                        </td>
		                        <td>
		                            <c:forEach var="alternate" items="${pair.second}" varStatus="status">
	                                    <span class="wsnw"><i:inline key="${alternate.displayName}"/><c:if test="${not status.last}"><i:inline key="yukon.common.comma"/>&nbsp;</c:if></span>
		                            </c:forEach>
		                        </td>
	                        </tr>
	                    </c:forEach>
	                </tbody>
	            </table>
	            <div class="actionArea">
	                <cti:button nameKey="switch" id="enroll_alternate" type="submit"/>
	            </div>
            </form>
        </c:otherwise>
    </c:choose>
</tags:boxContainer2>
<br><br>
<tags:boxContainer2 nameKey="active">
    <c:choose>
        <c:when test="${empty active}">
            <span class="list-none"><i:inline key=".active.none"/></span>
        </c:when>
        <c:otherwise>
            <c:set var="multiple" value="${fn:length(active) > 1 ? true : false}"/>
            <form action="enroll" method="post">
                <input type="hidden" name="accountId" value="${accountId}">
	            <table class="compactResultsTable">
	                <thead>
	                    <tr>
	                        <th><i:inline key=".device"/></th>
	                        <th><i:inline key=".enrolled"/></th>
	                        <th><i:inline key=".original"/></th>
	                        <th></th>
	                    </tr>
	                </thead>
	                <tfoot></tfoot>
	                <tbody>
	                    <c:forEach var="row" items="${active}">
	                        <tr>
		                        <c:set var="inventory" value="${row.key}"/>
		                        <c:set var="pair" value="${row.value}"/>
		                        <td>
                                    <input id="inv_${inventory.inventoryId}" type="checkbox" value="${inventory.inventoryId}" name="normal" <c:if test="${!multiple}">checked="checked" class="dn"</c:if>
                                    ><label for="inv_${inventory.inventoryId}">${fn:escapeXml(inventory.displayName)}</label></td>
                                </td>
		                        <td>
		                            <c:forEach var="alternate" items="${pair.first}" varStatus="status">
		                               <span class="wsnw"><i:inline key="${alternate.displayName}"/><c:if test="${not status.last}"><i:inline key="yukon.common.comma"/>&nbsp;</c:if></span>
		                            </c:forEach>
		                        </td>
		                        <td>
		                            <c:forEach var="normal" items="${pair.second}" varStatus="status">
		                               <span class="wsnw"><i:inline key="${normal.displayName}"/><c:if test="${not status.last}"><i:inline key="yukon.common.comma"/>&nbsp;</c:if></span>
		                            </c:forEach>
	                            </td>
	                        </tr>
	                    </c:forEach>
	                </tbody>
	            </table>
	            <div class="actionArea">
	                <cti:button nameKey="revert" id="enroll_normal" type="submit"/>
	            </div>
            </form>
        </c:otherwise>
    </c:choose>
</tags:boxContainer2>

</cti:standardPage>