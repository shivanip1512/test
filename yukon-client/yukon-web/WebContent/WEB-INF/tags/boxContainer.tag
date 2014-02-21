<%@ tag trimDirectiveWhitespaces="true" %>

<%@ attribute name="escapeTitle" type="java.lang.Boolean" %>
<%@ attribute name="helpText" %>
<%@ attribute name="id" %>
<%@ attribute name="hideEnabled" type="java.lang.Boolean" %>
<%@ attribute name="showInitially" type="java.lang.Boolean" %>
<%@ attribute name="styleClass" %>
<%@ attribute name="title" required="true" %>
<%@ attribute name="titleLinkHtml" %>

<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:includeScript link="/JavaScript/simpleCookies.js"/>
<cti:includeScript link="/JavaScript/hideReveal.js"/>

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
        <c:if test="${not empty pageScope.helpText}">
            <cti:icon icon="icon-help" id="help_icon_${thisId}" classes="cp show-on-hover"/>
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

<c:if test="${not empty pageScope.helpText}">
    <tags:simplePopup title="${pageScope.title}" id="box-container-info-popup-${thisId}" on="#help_icon_${thisId}" options="{width: 600}">${helpText}</tags:simplePopup>
</c:if>
