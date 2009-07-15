<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:includeScript link="/JavaScript/calendarControl.js"/>
<cti:includeCss link="/WebConfig/yukon/styles/calendarControl.css"/>

<spring:bind path="${input.field}">
	<tags:inputName input="${input}" error="${status.error}" />
		
    <tags:dateInputCalendar fieldId="${status.expression}"
                            fieldName="${status.expression}"
                            fieldValue="${status.value}" />
</spring:bind>