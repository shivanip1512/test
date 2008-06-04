<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="ext" tagdir="/WEB-INF/tags/ext" %>

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
	
	Event.observe (window, 'load', selectCommand);
	
</script>
	
	<h2>Group Processing</h2>
  <br>
  
  
	
	<c:if test="${errorMsg != null}">
		<div style="color: red;margin: 10px 0px;">Error: ${errorMsg}</div>
		<c:set var="errorMsg" value="" scope="request"/>
	</c:if>
	
	<br>
	<div style="width: 700px;">
		<form action="<c:url value="/spring/group/commander/executeGroupCommand" />">
      
			<tags:nameValueContainer altRowOn="true">
	
				<tags:nameValue name="Select command">
					
					<c:choose>
						<c:when test="${fn:length(commands) <= 0}">
							No pre-defined commands authorized for this user.
						</c:when>
						
						<c:otherwise>
							<select id="commandSelect" name="commandSelect" onchange="selectCommand()">
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
						</c:otherwise>
					</c:choose>
					
					<br>
					<input style="margin-top: .25em;" type="text" id="commandString" name="commandString" <cti:isPropertyFalse property="CommanderRole.EXECUTE_MANUAL_COMMAND">readonly</cti:isPropertyFalse> size="40" />
				</tags:nameValue>

    <tags:nameValue name="Group">
      <%-- SELECT DEVICE GROUP TREE INPUT --%>
      <ext:nodeValueSelectingInlineTree fieldId="groupName" fieldName="groupName"
        nodeValueName="groupName" id="selectGroupTree" dataJson="${dataJson}" width="500"
        height="400" treeAttributes="{'border':true}" />
    </tags:nameValue>

  </tags:nameValueContainer>
			<br>
			<input type="submit" name="execute" value="Execute" />
			
		</form>
	</div>
  
	
</cti:standardPage>