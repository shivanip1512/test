<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="titleKey" required="true" %>
<%@ attribute name="id" required="false" type="java.lang.String"%>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>
<%@ attribute name="helpTextKey" required="false" type="java.lang.String"%>

<cti:msg2 var="title" key="${pageScope.titleKey}"/>
<cti:msg2 var="helpText" key="${pageScope.helpTextKey}" blankIfMissing="true"/>

<tags:sectionContainer title="${pageScope.title}" id="${pageScope.id}" styleClass="${pageScope.styleClass}" helpText="${pageScope.helpText}">

	<jsp:doBody/>

</tags:sectionContainer>