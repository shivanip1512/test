<%@ attribute name="path" required="true"%>
<%@ attribute name="fieldId"%>
<%@ attribute name="fieldValue" type="java.lang.Object"%>
<%@ attribute name="disabled" type="java.lang.Boolean"%>
<%@ attribute name="inline" type="java.lang.Boolean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:includeScript link="/JavaScript/calendarTagFuncs.js"/>

<c:if test="${empty fieldId}">
    <c:set var="fieldId" value="${path}"/>
</c:if>

<c:if test="${!empty pageScope.fieldValue}">
    <cti:formatDate var="datePart" value="${pageScope.fieldValue}" type="DATE"/>
    <cti:formatDate var="timePart" value="${pageScope.fieldValue}" type="TIME24H"/>
</c:if>

<c:set var="disabledStr" value=""/>
<c:if test="${!empty pageScope.disabled}">
    <c:set var="disabledStr" value=" disabled"/>
</c:if>

<c:choose>

	<c:when test="${not inline}">
	
		<table>
		    <tr>
                <cti:displayForPageEditModes modes="VIEW">
                    <td>${datePart}</td>
                </cti:displayForPageEditModes>
                <cti:displayForPageEditModes modes="EDIT,CREATE">
                    <td><cti:msg key="yukon.common.calendarcontrol.date"/></td>
                    <td><tags:dateInputCalendar fieldId="${fieldId}DatePart"
                                                fieldName="${fieldId}DatePart" 
                                                fieldValue="${datePart}"
		                                        disabled="${pageScope.disabled}"/></td>
                </cti:displayForPageEditModes>
		    </tr>
		    <tr>
		        <td><cti:msg key="yukon.common.calendarcontrol.time"/></td>
		        <td>
		        	<cti:displayForPageEditModes modes="VIEW">
				        ${timePart}
		            </cti:displayForPageEditModes>
		        	<cti:displayForPageEditModes modes="EDIT,CREATE">
				        <input id="${fieldId}TimePart" name="${fieldId}TimePart" type="text" maxlength="5" size="10" style="width:70px;" value="${timePart}"${disabledStr}/>
		            </cti:displayForPageEditModes>
		            
		        </td>
		    </tr>
		</table>
	
	</c:when>
	
	<c:otherwise>
	
		<cti:displayForPageEditModes modes="VIEW">
      		${datePart} ${timePart}
        </cti:displayForPageEditModes>
        <cti:displayForPageEditModes modes="EDIT,CREATE">
            <tags:dateInputCalendar fieldId="${fieldId}DatePart" fieldName="${fieldId}DatePart" 
                                    fieldValue="${datePart}" disabled="${pageScope.disabled}"/>
    		&nbsp;-&nbsp;
			<input id="${fieldId}TimePart" name="${fieldId}TimePart" type="text" maxlength="5" size="10" style="width:70px;" value="${timePart}"${disabledStr}/>
		</cti:displayForPageEditModes>
	
	</c:otherwise>

</c:choose>



<spring:bind path="${path}">
    <form:hidden id="${fieldId}" path="${path}"/>
    <c:if test="${status.error}">
        <br>
        <form:errors path="${path}" cssClass="error"/>
    </c:if>
</spring:bind>