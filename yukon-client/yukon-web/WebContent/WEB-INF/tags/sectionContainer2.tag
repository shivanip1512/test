<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="nameKey" required="true" description="Base i18n key. Available settings: .title (required), .helpText (optional)"%>
<%@ attribute name="id" required="false" type="java.lang.String"%>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>
<%@ attribute name="hideEnabled" required="false" type="java.lang.Boolean" %>

<cti:msgScope paths=".${nameKey},">
	<cti:msg2 var="title" key=".title"/>
	<cti:msg2 var="helpText" key=".helpText" blankIfMissing="true"/>
</cti:msgScope>

<tags:sectionContainer title="${pageScope.title}" id="${pageScope.id}" styleClass="${pageScope.styleClass}" helpText="${pageScope.helpText}" hideEnabled="${pageScope.hideEnabled}">
	<jsp:doBody/>
</tags:sectionContainer>