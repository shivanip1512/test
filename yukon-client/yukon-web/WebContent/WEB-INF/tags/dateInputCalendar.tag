<%@ attribute name="fieldName" required="true" type="java.lang.String"%>
<%@ attribute name="fieldValue" required="false" type="java.lang.String"%>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<input id="${fieldName}" name="${fieldName}" type="text" size="10" maxlength="10" value="${fieldValue}" style="width:64px;height:14px;">&nbsp;

<span onclick="javascript:showCalendarControl($('${fieldName}'));">
	<img src="<c:url value="/WebConfig/yukon/Icons/StartCalendar.gif"/>" width="20" height="15" border="0" />
</span>



