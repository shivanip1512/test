<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="controls" %>
<%@ attribute name="escapeTitle" type="java.lang.Boolean" description="If true, html escapes the title text." %>
<%@ attribute name="helpText" description="The text to put inside of a help popup." %>
<%@ attribute name="infoText" description="The text to put inside of a information popup." %>
<%@ attribute name="helpUrl" description="A url used to load a help popup with content before showing." %>
<%@ attribute name="showHelpIcon" required="false" type="java.lang.Boolean" 
              description="Show help icon even if the helpText or helpUrl is empty. The help text has to be loaded using some js code in this case." %>
<%@ attribute name="hideEnabled" type="java.lang.Boolean" %>
<%@ attribute name="hideInitially" type="java.lang.Boolean" %>
<%@ attribute name="id" description="The id of the outer container element. If not provided, the id is generated." %>
<%@ attribute name="styleClass" description="CSS class names applied to the outer container element." %>
<%@ attribute name="title" %>
<%@ attribute name="helpWidth" %>
<%@ attribute name="infoWidth" %>
<%@ attribute name="displayRequiredFieldsNotice" %>

<cti:uniqueIdentifier prefix="section-container-" var="thisId"/>
<cti:default var="id" value="${thisId}"/>
<cti:default var="styleClass" value=""/>

<c:if test="${hideEnabled}">
    <c:set var="h3Class" value="toggle-title"/>
    <c:if test="${hideInitially}">
        <c:set var="collapsed" value="collapsed"/>
        <c:set var="hideMe" value="dn"/>
    </c:if>
</c:if>

<div id="${id}" class="titled-container section-container clearfix ${collapsed} ${styleClass}">
    <%-- Title Bar --%>
    <div class="title-bar clearfix">
        <c:choose>
          <c:when test="${pageScope.escapeTitle}">
            <h3 class="title ${h3Class}">${fn:escapeXml(pageScope.title)}</h3>
          </c:when>
          <c:otherwise>
            <h3 class="title ${h3Class}">${pageScope.title}</h3>
          </c:otherwise>
        </c:choose>
        
        <c:if test="${not empty pageScope.helpText or not empty pageScope.helpUrl or showHelpIcon}">
            <cti:icon icon="icon-help" classes="cp" 
                data-popup="#section-container-info-popup-${id}"
                data-popup-toggle=""/>
        </c:if>
        
        <c:if test="${not empty pageScope.infoText}">
            <cti:icon icon="icon-information" classes="fn cp" 
                data-popup="#section-container-information-popup-${id}"
                data-popup-toggle=""/>
        </c:if>
        <div class="controls">${controls}</div>
        <div class="displayRequiredFieldsNotice"><i><span class="red">${displayRequiredFieldsNotice}</span></i></div>
    </div>
    <%-- Body --%>
    <div id="${id}_content" class="content clearfix ${hideMe}"><jsp:doBody/></div>
</div>
<%-- Help Popup --%>
<c:if test="${not empty pageScope.helpText or not empty pageScope.helpUrl or showHelpIcon}">
    <div id="section-container-info-popup-${id}" class="dn" 
            data-title="${pageScope.title}" 
            <c:if test="${not empty pageScope.helpUrl}">data-url="${helpUrl}"</c:if>
            data-width="${(not empty helpWidth)? helpWidth: 600}">${helpText}</div>
</c:if>
<%-- Information Popup --%>
<c:if test="${not empty pageScope.infoText}">
    <div id="section-container-information-popup-${id}" class="dn" 
            data-title="${pageScope.title}" 
           data-width="${(not empty infoWidth && infoWidth > 0)? infoWidth: 500}">${infoText}</div>
</c:if>