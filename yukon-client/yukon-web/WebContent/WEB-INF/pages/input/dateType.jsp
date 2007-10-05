<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>

<cti:includeScript link="/JavaScript/calendarControl.js"/>
<cti:includeCss link="/WebConfig/yukon/styles/calendarControl.css"/>

<spring:bind path="${input.field}">
	<ct:inputName input="${input}" error="${status.error}" />
		
	<input type="text" id="${status.expression}" name="${status.expression}" value="${status.value}" />

	<a href="javascript:showCalendarControl($('${status.expression}'))"> 
		<img src="<c:url value="/WebConfig/yukon/Icons/StartCalendar.gif"/>" width="20" height="15" align="ABSMIDDLE" border="0" />
	</a>
</spring:bind>