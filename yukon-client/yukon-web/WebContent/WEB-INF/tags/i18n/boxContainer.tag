<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="titleKey" required="false" type="java.lang.String"%>
<%@ attribute name="id" required="false" type="java.lang.String"%>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>
<%@ attribute name="hideEnabled" required="false" type="java.lang.Boolean"%>
<%@ attribute name="showInitially" required="false" type="java.lang.Boolean"%>
<%@ attribute name="helpTextKey" required="false" type="java.lang.String"%>

<cti:msg2 var="title" key="${pageScope.titleKey}" blankIfMissing="true"/>
<cti:msg2 var="helpText" key="${pageScope.helpTextKey}" blankIfMissing="true"/>

<tags:boxContainer title="${pageScope.title}" 
				   id="${pageScope.id}" 
				   styleClass="${pageScope.styleClass}" 
				   hideEnabled="${pageScope.hideEnabled}" 
				   showInitially="${pageScope.showInitially}"
				   helpText="${pageScope.helpText}">

	<jsp:doBody/>

</tags:boxContainer>