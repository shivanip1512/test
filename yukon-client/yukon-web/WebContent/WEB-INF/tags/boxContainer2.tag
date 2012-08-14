<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ attribute name="nameKey" required="true" description="Base i18n key. Available settings: .title (required), .helpText (optional)"%>
<%@ attribute name="id" required="false" type="java.lang.String"%>
<%@ attribute name="arguments" required="false" type="java.lang.Object"%>
<%@ attribute name="argumentSeparator" required="false" type="java.lang.String"%>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>
<%@ attribute name="hideEnabled" required="false" type="java.lang.Boolean"%>
<%@ attribute name="showInitially" required="false" type="java.lang.Boolean"%>

<cti:msgScope paths=".${nameKey},">
	<c:choose>
		<c:when test="${not empty argumentSeparator}">
			<cti:msg2 var="title" key=".title" arguments="${arguments}" argumentSeparator="${argumentSeparator}"/>
		</c:when>
		<c:otherwise>
			<cti:msg2 var="title" key=".title" arguments="${arguments}"/>
		</c:otherwise>
	</c:choose>
	<cti:msg2 var="helpText" key=".helpText" blankIfMissing="true"/>
</cti:msgScope>

<tags:boxContainer title="${pageScope.title}" 
				   id="${pageScope.id}" 
				   styleClass="${pageScope.styleClass}" 
				   hideEnabled="${pageScope.hideEnabled}" 
				   showInitially="${pageScope.showInitially}"
				   helpText="${pageScope.helpText}">
	<jsp:doBody/>

</tags:boxContainer>