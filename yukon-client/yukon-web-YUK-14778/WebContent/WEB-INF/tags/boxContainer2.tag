<%@ tag trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="nameKey" required="true" description="Base i18n key. Available settings: .title (required), .helpText (optional)" %>
<%@ attribute name="id" %>
<%@ attribute name="arguments" type="java.lang.Object" %>
<%@ attribute name="styleClass" %>
<%@ attribute name="hideEnabled" type="java.lang.Boolean" %>
<%@ attribute name="showInitially" type="java.lang.Boolean" %>
<%@ attribute name="titleLinkHtml" %>

<cti:msgScope paths=".${nameKey},">
    <cti:msg2 var="title" key=".title" arguments="${arguments}"/>
    <cti:msg2 var="helpText" key=".helpText" blankIfMissing="true"/>
</cti:msgScope>

<tags:boxContainer title="${pageScope.title}" 
                   id="${pageScope.id}" 
                   styleClass="${pageScope.styleClass}" 
                   hideEnabled="${pageScope.hideEnabled}" 
                   showInitially="${pageScope.showInitially}"
                   titleLinkHtml="${pageScope.titleLinkHtml}"
                   helpText="${pageScope.helpText}">
    <jsp:doBody/>
</tags:boxContainer>