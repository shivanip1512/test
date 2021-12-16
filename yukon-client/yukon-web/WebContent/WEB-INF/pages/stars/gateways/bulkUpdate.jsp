<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.operator.gateways">
	<cti:url var="url" value="/stars/gateways/update"/>
	<form:form id="update-gateways-form" action="${url}" method="post" modelAttribute="settings">
	    <cti:csrfToken/>
	    <tags:nameValueContainer2>
		    <tags:nameValue2 nameKey=".list.gateways" valueClass="fl MB5">
		    	<tags:pickerDialog id="gatewayPicker" type="gatewayNmIpAddressPortPicker" multiSelectMode="true"
            		linkType="selection" selectionProperty="paoName" destinationFieldId="gatewayIds" initialIds="${settings.gatewayIds}"/>
            	<tags:hidden path="gatewayIds"/>
           		<jsp:include page="nmIPAddressPort.jsp"/>
		    </tags:nameValue2>
	    </tags:nameValueContainer2>
	</form:form>
</cti:msgScope>