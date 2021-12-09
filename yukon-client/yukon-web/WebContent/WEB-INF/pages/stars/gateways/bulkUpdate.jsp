<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:msgScope paths="modules.operator.gateways">

<cti:url var="url" value="/stars/gateways/bulkUpdate"/>
	<form:form id="update-gateways-form" action="${url}" method="post" modelAttribute="schedule">
	    <cti:csrfToken/>
	    	    
	</form:form>

</cti:msgScope>