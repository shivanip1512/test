<%@ tag description="Create an image link which displays a confirmation dialog before doing an action." %>
<%@ attribute name="key" required="true"
    description="key passed on to cti:img or cti:labeledImg" %>
<%@ attribute name="action"
    description="bit of javascript code to execute after confirmation; exactly one of action or href is required" %>
<%@ attribute name="href"
    description="a URL to go to after confirmation; exactly one of action or href is required" %>
<%@ attribute name="id" description="a unique id for the dialog" %>
<%@ attribute name="linkType"
    description="type of link to create: img (default; uses cti:img), labeledImg (uses cti:labeledImg) or button (creates an html button)" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:includeScript link="/JavaScript/simpleDialog.js"/>
<c:if test="${empty pageScope.id}"><cti:uniqueIdentifier var="id"/></c:if>
<c:set var="confirmDialogUsed" value="true" scope="request"/>

<c:choose>
    <c:when test="${!empty pageScope.href && !empty pageScope.action}">
        <span class="errorMessage">Cannot use both href and action at the same time in confirmDialogImg tag.</span>
    </c:when>
    <c:when test="${empty pageScope.href && empty pageScope.action}">
        <span class="errorMessage">Must specify either href or action for confirmDialogImg tag.</span>
    </c:when>
    <c:otherwise>
        <c:if test="${!empty pageScope.action}">
            <c:set var="showConfirmAction" value="showConfirm('${pageScope.id}', function() {${pageScope.action}})"/>
        </c:if>
        <c:if test="${!empty pageScope.href}">
            <c:set var="showConfirmAction" value="showConfirm('${pageScope.id}', function() {window.location='${pageScope.href}'})"/>
        </c:if>
        <span id="confirmDialogQuestion${pageScope.id}" style="display:none"><jsp:doBody/></span>
        <c:choose>
            <c:when test="${pageScope.linkType == 'labeledImg'}">
                <cti:labeledImg href="javascript:${showConfirmAction}" key="${pageScope.key}"/>
            </c:when>
            <c:when test="${pageScope.linkType == 'button'}">
                <button type="button"
                    onclick="${showConfirmAction}"><cti:msg2 key="${pageScope.key}"/></button>
            </c:when>
            <c:otherwise>
                <cti:img href="javascript:${showConfirmAction}" key="${pageScope.key}"/>
            </c:otherwise>
        </c:choose>
    </c:otherwise>
</c:choose>
