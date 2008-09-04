<%@ attribute name="address" required="true" type="com.cannontech.amr.account.model.Address"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>

<tags:notNullDataLine value="${address.locationAddress1}"/>
<tags:notNullDataLine value="${address.locationAddress2}"/>
<c:if test="${not empty address.cityName}">
	${address.cityName},
</c:if>
${address.stateCode} ${address.zipCode}
