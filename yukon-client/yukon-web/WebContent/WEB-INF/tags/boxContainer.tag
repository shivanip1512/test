<%@ tag trimDirectiveWhitespaces="true"%>

<%@ attribute name="escapeTitle" required="false" type="java.lang.Boolean" %>
<%@ attribute name="helpText" required="false" type="java.lang.String"%>
<%@ attribute name="id" required="false" type="java.lang.String"%>
<%@ attribute name="hideEnabled" required="false" type="java.lang.Boolean"%>
<%@ attribute name="showInitially" required="false" type="java.lang.Boolean"%>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>
<%@ attribute name="title" required="false" type="java.lang.String"%>
<%@ attribute name="titleLinkHtml" required="false" type="java.lang.String" %>

<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:includeScript link="/JavaScript/simpleCookies.js"/>
<cti:includeScript link="/JavaScript/hideReveal.js"/>

<cti:uniqueIdentifier prefix="titledContainer_" var="thisId"/>
<c:if test="${!empty pageScope.id}">
    <c:set var="thisId" value="${pageScope.id}"/>
</c:if>

<div class="titledContainer boxContainer ${pageScope.styleClass}" <c:if test="${!empty pageScope.id}">id="${pageScope.id}"</c:if>>

    <div class="title-bar">
        <c:if test="${!empty pageScope.titleLinkHtml}">${pageScope.titleLinkHtml}</c:if>
        <h3 class="title">
            <c:choose>
                <c:when test="${pageScope.escapeTitle}">${fn:escapeXml(pageScope.title)}</c:when>
                <c:otherwise>${pageScope.title}</c:otherwise>
            </c:choose>
        </h3>
        <c:if test="${not empty pageScope.helpText}">
            <cti:icon icon="icon-help" id="help_icon_${thisId}" classes="cp"/>
        </c:if>
        <c:if test="${(pageScope.hideEnabled == null) || pageScope.hideEnabled}">
            <div class="controls" id="${thisId}_control">
                <cti:icon icon="icon-collapse" id="${thisId}_minusImg" classes="minMax"/> 
                <cti:icon icon="icon-expand" id="${thisId}_plusImg" classes="minMax"/> 
              </div>
        </c:if>
    </div>
    
    <div id="${thisId}_content" class="content">
        <jsp:doBody/>
    </div>    
                
</div>
<c:if test="${empty pageScope.showInitially}">
  <c:set var="showInitially" value="${true}"/> <%-- show by default --%>
</c:if>

<c:if test="${(pageScope.hideEnabled == null) || pageScope.hideEnabled}">
    <script type="text/javascript">
        hideRevealSectionSetup('${thisId}_plusImg', '${thisId}_minusImg', '${thisId}_control', '${thisId}_content', ${pageScope.showInitially ? true : false}, '${cti:jsSafe(pageScope.title)}');
    </script>
</c:if>

<c:if test="${not empty pageScope.helpText}">
    <tags:simplePopup title="${pageScope.title}" id="boxContainerInfoPopup_${thisId}" on="#help_icon_${thisId}" options="{width: 600}">${helpText}</tags:simplePopup>
</c:if>
