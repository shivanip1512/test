<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>


<script type="text/javascript">
	function updateField(field, hourSelect, minuteSelect) {
		var time = $(hourSelect).value + ":" + $(minuteSelect).value;
		
		$(field).value = time;
	}
</script>

<spring:bind path="${input.field}">

	<c:set var="timeSplit" value="${fn:split(status.value, ':')}" />
	<c:set var="hourSplit" value="00" />
	<c:set var="minuteSplit" value="00" />

	<c:if test="${fn:length(timeSplit) == 2}">
		<c:set var="hourSplit" value="${timeSplit[0]}" />
		<c:set var="minuteSplit" value="${timeSplit[1]}" />
	</c:if>
	
	<span <c:if test="${status.error}">style="color: red"</c:if>>
		${input.displayName} 
	</span>
		
	<input type="hidden" size="5" id="${status.expression}" name="${status.expression}" value="${status.value}" />

	<select id="${status.expression}hourSelect" onchange="updateField('${status.expression}', '${status.expression}hourSelect', '${status.expression}minuteSelect')">
		<c:forEach var="hour" begin="0" end="23">
			<c:choose>
				<c:when test="${hour < 10}">
					<c:set var="currHour" value="0${hour}" />
					<option value="${currHour}" <c:if test="${currHour eq hourSplit}" >selected</c:if>>${currHour}</option>
				</c:when>
				<c:otherwise>
					<option value="${hour}" <c:if test="${hour == hourSplit}" >selected</c:if>>${hour}</option>
				</c:otherwise>
			</c:choose>
		
		</c:forEach>
	</select>
	:
	<select id="${status.expression}minuteSelect" onchange="updateField('${status.expression}', '${status.expression}hourSelect', '${status.expression}minuteSelect')">
		<c:forEach var="minute" begin="0" end="59">
			<c:choose>
				<c:when test="${minute < 10}">
					<c:set var="currMinute" value="0${minute}" />
					<option value="${currMinute}" <c:if test="${currMinute eq minuteSplit}" >selected</c:if>>${currMinute}</option>
				</c:when>
				<c:otherwise>
					<option value="${minute}" <c:if test="${minute eq minuteSplit}" >selected</c:if>>${minute}</option>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</select>	
					
</spring:bind>