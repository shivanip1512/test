<%@ tag body-content="empty" %>
<%@ attribute name="dialogId" required="true"%>
<%@ attribute name="titleKey" required="true"%>
<%@ attribute name="actionUrl" required="true"%>
<%@ attribute name="icon" %>
<%@ attribute name="labelKey" description="i18n key of label text"%>
<%@ attribute name="labelText" description="plain text of label text"%>
<%@ attribute name="styleClass" description="link class, defaults to 'simpleLink'"%>
<%@ attribute name="disabled" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<cti:includeScript link="/JavaScript/simpleDialog.js"/>

<cti:uniqueIdentifier var="dialogTitle" prefix="simpleDialogTitle"/>

<script type="text/javascript">
    var ${dialogTitle} = '<cti:msg key="${pageScope.titleKey}" javaScriptEscape="true" />';
</script>

<a href="javascript:void(0)" class="clearfix ${pageScope.styleClass}" onclick="openSimpleDialog('${pageScope.dialogId}', '${pageScope.actionUrl}', ${dialogTitle})">
    <c:if test="${not empty pageScope.icon}">
        <c:choose>
            <c:when test="${not empty pageScope.disabled && disabled == 'true'}">
                <cti:icon icon="${icon}" classes="disabled"/>
            </c:when>
            <c:otherwise>
                <cti:icon icon="${icon}"/>
            </c:otherwise>
        </c:choose>
    </c:if>
    <span class="fl dib <c:if test="${not empty pageScope.disabled && disabled == 'true'}">disabled</c:if>">
        <c:choose>
            <c:when test="${not empty pageScope.labelKey}">
                <cti:msg key="${pageScope.labelKey}" />
            </c:when>
            <c:when test="${not empty pageScope.labelText}">
                ${fn:escapeXml(pageScope.labelText)}
            </c:when>
            <c:otherwise>
                <%-- no label --%>
            </c:otherwise>
        </c:choose>
    </span>
</a>