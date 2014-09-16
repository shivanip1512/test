<%@ tag trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="escapeTitle" type="java.lang.Boolean" description="If true, html escapes the title text." %>
<%@ attribute name="helpText" description="The text to put inside of a help popup." %>
<%@ attribute name="helpUrl" description="A url used to load a help popup with content before showing." %>
<%@ attribute name="id" description="The id of the outer container element. If not provided, the id is generated." %>
<%@ attribute name="hideEnabled" type="java.lang.Boolean" %>
<%@ attribute name="showInitially" type="java.lang.Boolean" %>
<%@ attribute name="styleClass" description="CSS class names applied to the outer container element." %>
<%@ attribute name="title" required="true" %>
<%@ attribute name="titleLinkHtml" %>

<cti:includeScript link="/JavaScript/yukon.cookies.js"/>
<cti:includeScript link="/JavaScript/yukon.hide.reveal.js"/>

<cti:uniqueIdentifier prefix="titled-container-" var="thisId"/>
<c:if test="${!empty pageScope.id}">
    <c:set var="thisId" value="${id}"/>
</c:if>

<div class="titled-container box-container clearfix ${pageScope.styleClass}" <c:if test="${!empty pageScope.id}">id="${id}"</c:if>>

    <div class="title-bar clearfix">
        <c:if test="${!empty pageScope.titleLinkHtml}">${pageScope.titleLinkHtml}</c:if>
        <h3 class="title">
            <c:choose>
                <c:when test="${pageScope.escapeTitle}">${fn:escapeXml(pageScope.title)}</c:when>
                <c:otherwise>${pageScope.title}</c:otherwise>
            </c:choose>
        </h3>
        <c:if test="${not empty pageScope.helpText or not empty pageScope.helpUrl}">
            <cti:icon icon="icon-help" classes="cp" data-popup="#box-container-info-popup-${thisId}" data-popup-toggle=""/>
        </c:if>
        <c:if test="${(pageScope.hideEnabled == null) || pageScope.hideEnabled}">
            <div class="controls" id="${thisId}_control">
                <cti:icon icon="icon-collapse" id="${thisId}_minusImg" classes="minMax"/> 
                <cti:icon icon="icon-expand" id="${thisId}_plusImg" classes="minMax"/> 
              </div>
        </c:if>
    </div>
    
    <div id="${thisId}_content" class="content clearfix">
        <jsp:doBody/>
    </div>
</div>
<c:if test="${empty pageScope.showInitially}">
  <c:set var="showInitially" value="${true}"/> <%-- show by default --%>
</c:if>

<c:if test="${(pageScope.hideEnabled == null) || pageScope.hideEnabled}">
    <script type="text/javascript">
        hideRevealSectionSetup('${thisId}_plusImg', '${thisId}_minusImg', '${thisId}_control', '${thisId}_content', ${showInitially ? true : false}, '${cti:jsSafe(pageScope.title)}');
    </script>
</c:if>

<c:if test="${not empty pageScope.helpText or not empty pageScope.helpUrl}">
    <div id="box-container-info-popup-${thisId}" 
            class="dn" 
            data-title="${pageScope.title}"
            <c:if test="${not empty pageScope.helpUrl}">data-url="${helpUrl}"</c:if> 
            data-width="600">${helpText}</div>
</c:if>