<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="Commander Results" module="amr">
<cti:standardMenu menuSelection="devicegroups|commander"/>
   	<cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
	    &gt; Group Processing
	</cti:breadCrumbs>

<script type="text/javascript">
	
	function selectCommand() {
		$('commandString').value = $F('commandSelect');
	}
	
	function selectEmailSubject() {
		$('emailSubject').value = 'Group Processing for ' + $F('groupSelect') + ' completed. (' + $F('commandString') + ')';
	}
	
	Event.observe (window, 'load', selectCommand);
	
</script>
	
	<h2>Group Processing</h2>
	
	<c:if test="${errorMsg != null}">
		<div style="color: red;margin: 10px 0px;">Error: ${errorMsg}</div>
		<c:set var="errorMsg" value="" scope="request"/>
	</c:if>
	
	<br>
	<div style="width: 700px;">
		<form action="<c:url value="/spring/group/executeCommand" />">
			
			<ct:nameValueContainer altRowOn="true">
				<ct:nameValue name="Select group">
					<c:set var="selected" value="" scope="page"></c:set>
					<select id="groupSelect" name="groupSelect" onchange="selectEmailSubject()">
						<c:forEach var="groupOption" items="${groups}">
						
							<c:if test="${groupOption.fullName == group}">
								<c:set var="selected" value="selected" scope="page"></c:set>
							</c:if>
							<c:if test="${groupOption.fullName != group}">
								<c:set var="selected" value="" scope="page"></c:set>
							</c:if>
						
							<option value="${fn:escapeXml(groupOption.fullName)}" ${selected}> ${fn:escapeXml(groupOption.fullName)}</option>
						</c:forEach>
					</select>
				</ct:nameValue>
	
				<ct:nameValue name="Select command">
					<select id="commandSelect" name="commandSelect" onchange="selectCommand();selectEmailSubject()">
						<c:forEach var="commandOption" items="${commands}">
						
							<c:if test="${commandOption.command == command}">
								<c:set var="selected" value="selected" scope="page"></c:set>
							</c:if>
							<c:if test="${commandOption.command != command}">
								<c:set var="selected" value="" scope="page"></c:set>
							</c:if>
						
							<option value="${commandOption.command}" ${selected}> ${commandOption.label}</option>
						</c:forEach>
					</select>
					<br>
					<input style="margin-top: .25em;" type="text" id="commandString" name="commandString" <cti:isPropertyFalse property="CommanderRole.EXECUTE_MANUAL_COMMAND">readonly</cti:isPropertyFalse> size="40" />
				</ct:nameValue>
	
				<ct:nameValue name="Email results to">
					<input type="text" id="emailAddresses" name="emailAddresses" value="${emailAddresses}" size="40" />
					<br>
					ex: support@cannontech.com,help@cannontech.com
				</ct:nameValue>
	
				<ct:nameValue name="Email subject">
					<input type="text" id="emailSubject" name="emailSubject" value="${emailSubject}" size="80" />
				</ct:nameValue>
	
			</ct:nameValueContainer>
			<br>
			<input type="submit" name="execute" value="Execute" />
			
		</form>
	</div>
	
</cti:standardPage>