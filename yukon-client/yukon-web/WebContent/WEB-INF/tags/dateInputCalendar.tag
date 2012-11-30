<%@ tag body-content="empty" description="DEPRECATED!!!! Use tags/dateTime/date instead." %>
<%@ attribute name="fieldId" required="false" type="java.lang.String" description="Ends up being the id of the input, not used when springInput = true."%>
<%@ attribute name="fieldName" required="true" type="java.lang.String" description="Used as the path and id of the input when springInput = true, otherwise used as the name attribute of the input."%>
<%@ attribute name="fieldValue" required="false" type="java.lang.String"%>
<%@ attribute name="disabled" required="false" type="java.lang.Boolean"%>
<%@ attribute name="springInput" required="false" type="java.lang.Boolean"%>
<%@ attribute name="showErrorOnNextLine" type="java.lang.Boolean" %>
<%@ attribute name="inputClass"%>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<cti:includeScript link="/JavaScript/calendarControl.js"/>
<cti:includeCss link="/WebConfig/yukon/styles/calendarControl.css"/>

<cti:msg key="yukon.common.calendarcontrol.months" var="months"/>
<cti:msg key="yukon.common.calendarcontrol.days" var="days"/>
<cti:msg key="yukon.common.calendarcontrol.clear" var="clear"/>
<cti:msg key="yukon.common.calendarcontrol.close" var="close"/>

<cti:uniqueIdentifier var="uniqueId" prefix="dateInputCalendarId_"/>
<c:if test="${pageScope.springInput}">
    <c:set var="uniqueId" value="${fieldName}" />
</c:if>
<c:if test="${!pageScope.springInput && not empty pageScope.fieldId}">
    <c:set var="uniqueId" value="${pageScope.fieldId}" />
</c:if>

<c:set var="disabledStr" value=""/>
<c:if test="${pageScope.disabled}">
    <c:set var="disabledStr" value=" disabled"/>
    <script type="text/javascript">disabledCalendars['${uniqueId}'] = true;</script>
</c:if>

<span style="white-space:nowrap;">

<c:choose>
    <c:when test="${pageScope.springInput}">
        <!-- Spring Input Version -->
        <spring:bind path="${fieldName}">
        
        	<cti:displayForPageEditModes modes="VIEW">
				<spring:escapeBody htmlEscape="true">${status.value}</spring:escapeBody>
			</cti:displayForPageEditModes>
			
			<cti:displayForPageEditModes modes="EDIT,CREATE">
	
	            <c:set var="statusError" value=""/>
	            <c:if test="${status.error}">
	                <c:set var="statusError" value="error"/>
	            </c:if>
	            
	            <form:input  path="${fieldName}" id="${fieldName}" size="10" maxlength="10" cssClass="calendarInput ${statusError} ${inputClass}"/>
	            
	            <c:url var="calImgUrl" value="/WebConfig/yukon/Icons/StartCalendar.png"/>
	            <span onclick="javascript:showCalendarControl('${uniqueId}', '${months}', '${days}', '${clear}', '${close}');" style="cursor:pointer;">
	                <img id="calImg_${uniqueId}" src="${calImgUrl}" width="20" height="15" border="0" />
	            </span>
	            
	            <c:if test="${status.error}">
                    <c:if test="${empty pageScope.showErrorOnNextLine or
                                  pageScope.showErrorOnNextLine eq true}">
    	                <br>
                    </c:if>
                    
	                <form:errors path="${fieldName}" cssClass="errorMessage"/>
	            </c:if>
	            
			</cti:displayForPageEditModes>
        
        </spring:bind>
        
    </c:when>
    <c:otherwise>
        <!-- Normal Input Version -->
        <input id="${uniqueId}" name="${fieldName}"${disabledStr} type="text" size="10" maxlength="10" value="${pageScope.fieldValue}" class="calendarInput ${inputClass}">&nbsp;
        
        <c:url var="calImgUrl" value="/WebConfig/yukon/Icons/StartCalendar.png"/>
        <span onclick="javascript:showCalendarControl('${uniqueId}', '${months}', '${days}', '${clear}', '${close}');" style="cursor:pointer;">
            <img id="calImg_${uniqueId}" src="${calImgUrl}" width="20" height="15" border="0" />
        </span>
    </c:otherwise>
</c:choose>

</span>
