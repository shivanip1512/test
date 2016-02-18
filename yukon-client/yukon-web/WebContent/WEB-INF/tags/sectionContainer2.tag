<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="nameKey" required="true" 
    description="Base i18n key. Available settings: .title (required), .helpText (optional)" %>
    
<%@ attribute name="arguments" type="java.lang.Object" %>
<%@ attribute name="id" %>
<%@ attribute name="styleClass" %>
<%@ attribute name="hideEnabled" type="java.lang.Boolean" %>
<%@ attribute name="hideInitially" type="java.lang.Boolean" %>
<%@ attribute name="controls" %>

<cti:msgScope paths=".${nameKey},">
    <cti:msg2 var="title" key=".title" arguments="${arguments}"/>
    <cti:msg2 var="helpText" key=".helpText" arguments="${arguments}" blankIfMissing="true"/>
    <cti:msg2 var="infoText" key=".infoText" arguments="${arguments}" blankIfMissing="true"/>
</cti:msgScope>
<tags:sectionContainer title="${pageScope.title}" id="${pageScope.id}" styleClass="${pageScope.styleClass}" 
    helpText="${pageScope.helpText}" infoText="${pageScope.infoText}" hideEnabled="${pageScope.hideEnabled}" hideInitially="${pageScope.hideInitially}" 
    controls="${pageScope.controls}"
    ><jsp:doBody/>
</tags:sectionContainer>