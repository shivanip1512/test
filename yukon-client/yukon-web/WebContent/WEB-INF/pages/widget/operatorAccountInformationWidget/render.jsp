<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>


<div style="font-size:11px;">

	<%-- ACCOUNT NUMBER --%>
	<b><i:inline key="yukon.web.widgets.operatorAccountInformationWidget.accountNumberPrefix"></i:inline>${accountNumber}</b>

	<%-- COMPANY NAME --%>
	<c:if test="${not empty dto.companyName}">
		<br>
		${dto.companyName}
	</c:if>
	
	<%-- NAME, PHONES --%>
	<br>
	${dto.firstName} ${dto.lastName}, ${dto.homePhone}(H)
	<c:if test="${not empty dto.workPhone}">
	,${dto.workPhone}(W)
	</c:if>
	
	<%-- ADDRESS --%>
	<br>
	${dto.streetAddress.locationAddress1}
	<c:if test="${not empty dto.streetAddress.locationAddress2}">
	,${dto.streetAddress.locationAddress1}
	</c:if>
	${dto.streetAddress.cityName}, ${dto.streetAddress.stateCode} ${dto.streetAddress.zipCode}

</div>