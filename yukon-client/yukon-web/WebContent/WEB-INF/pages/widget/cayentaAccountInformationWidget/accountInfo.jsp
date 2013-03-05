<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<c:choose>
	<c:when test="${msg != null}">${msg}</c:when>
	<c:otherwise>
        <cti:msg2 key=".custInfo" var="custInfo"/>
        <tags:sectionContainer title="${custInfo}">
    		<tags:nameValueContainer2 tableClass="rowHighlighting striped two-column-split">
		    	<tags:nameValue2 nameKey=".acctNumber">${meterInfo.accountNumber}</tags:nameValue2>
		    	<tags:nameValue2 nameKey=".name">${meterInfo.name}</tags:nameValue2>
			    <tags:nameValue2 nameKey=".locationNum">${meterInfo.locationNumber}</tags:nameValue2>
			    <tags:nameValue2 nameKey=".serviceAddr"><tags:address address="${address}"/></tags:nameValue2>
			    <tags:nameValue2 nameKey=".meterSerial">${meterInfo.serialNumber}</tags:nameValue2>
			    <tags:nameValue2 nameKey=".phoneNum"><cti:formatPhoneNumber value="${phoneInfo.phoneNumber}" htmlEscape="true"/></tags:nameValue2>
			    <tags:nameValue2 nameKey=".mapNum">${locationInfo.mapNumber}</tags:nameValue2>
		    </tags:nameValueContainer2>
        </tags:sectionContainer>
	</c:otherwise>
</c:choose>