<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/functions' prefix='fn' %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<!-- Iterate through each of the 6 time/rate pairs -->

<span style="font-size: 10px;">
<c:forEach var="i" begin="0" end="${fn:length(input.inputMap) - 1}">

	<c:set var="key" value="timeRateList[${i}]" />
	<spring:nestedPath path="${key}">
	
		<div style="margin: 0px 0px 5px 15px;">
			<cti:renderInput input="${input.inputMap[key]}"/>
		</div>
	</spring:nestedPath>
	
</c:forEach>
</span>