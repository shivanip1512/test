<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<%@ attribute name="tag" required="false" type="com.cannontech.common.validation.model.RphTag"%>

<cti:url var="PUimg" value="/WebConfig/yukon/Icons/arrow_up_red.gif"/>
<cti:url var="PDimg" value="/WebConfig/yukon/Icons/arrow_down_red.gif"/>
<cti:url var="UUimg" value="/WebConfig/yukon/Icons/arrow_trend_up_right.gif"/>
<cti:url var="UDimg" value="/WebConfig/yukon/Icons/arrow_trend_down_right.gif"/>
<cti:url var="UDCimg" value="/WebConfig/yukon/Icons/changeout.gif"/>

<c:choose>
	<c:when test="${tag == 'PU'}">
		<img src="${PUimg}">
	</c:when>
	<c:when test="${tag == 'PD'}">
		<img src="${PDimg}">
	</c:when>
	<c:when test="${tag == 'UU'}">
		<img src="${UUimg}">
	</c:when>
	<c:when test="${tag == 'UD'}">
		<img src="${UDimg}">
	</c:when>
	<c:when test="${tag == 'UDC'}">
		<img src="${UDCimg}">
	</c:when>
	<c:otherwise>
	</c:otherwise>
</c:choose>
