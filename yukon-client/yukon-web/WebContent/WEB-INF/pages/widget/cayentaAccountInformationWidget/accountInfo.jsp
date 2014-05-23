<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<c:choose>
	<c:when test="${msg != null}">${msg}</c:when>
	<c:otherwise>
        <cti:msg2 key=".custInfo" var="custInfo"/>
        <tags:sectionContainer title="${custInfo}">
    		<tags:nameValueContainer2 tableClass="row-highlighting striped two-column-table">
		    	<tags:nameValue2 nameKey=".acctNumber">${fn:escapeXml(meterInfo.accountNumber)}</tags:nameValue2>
		    	<tags:nameValue2 nameKey=".name">${fn:escapeXml(meterInfo.name)}</tags:nameValue2>
			    <tags:nameValue2 nameKey=".locationNum">${fn:escapeXml(meterInfo.locationNumber)}</tags:nameValue2>
			    <tags:nameValue2 nameKey=".serviceAddr"><tags:address address="${address}"/></tags:nameValue2>
			    <tags:nameValue2 nameKey=".meterSerial">${fn:escapeXml(meterInfo.serialNumber)}</tags:nameValue2>
			    <tags:nameValue2 nameKey=".phoneNum"><cti:formatPhoneNumber value="${phoneInfo.phoneNumber}" htmlEscape="true"/></tags:nameValue2>
			    <tags:nameValue2 nameKey=".mapNum">${locationInfo.mapNumber}</tags:nameValue2>
		    </tags:nameValueContainer2>
        </tags:sectionContainer>
	</c:otherwise>
</c:choose>