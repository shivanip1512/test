<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
    
<cti:standardPage title="Manual Commander Page" module="amr">
	<cti:standardMenu menuSelection="deviceselection" />
	<cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
	    <cti:crumbLink url="/spring/csr/search" title="Device Selection"  />
	    <cti:crumbLink url="/spring/csr/home?deviceId=${deviceId}" title="Device Detail"  />
	    &gt; Manual Commander
	</cti:breadCrumbs>    

<style type="text/css">
	div.scroll { font-size: 12px; 
				 height: 350px;  
				 width: 550px; 
				 overflow: scroll; 
				 border: 1px solid #CCCCCC; 
				 text-align:left; 
				 padding: 8px;
				}
</style>

<script language="JavaScript">
    function disableButton(x) {
        x.disabled = true;
        document.commandForm.submit();
    }
    function loadCommand() {
        document.commandForm.command.value = document.commandForm.commonCommand.value;
    }
</script>


	<h2>Manual Commander</h2>
	<br>
	
	<div style="width: 600px">
	
      <tags:widgetContainer deviceId="${deviceId}">
		<tags:widget bean="meterInformationWidget" />
      </tags:widgetContainer>
	  <BR>
		<tags:boxContainer title="Execute Command" hideEnabled="false">
			<form name="commandForm" method="POST" action="/servlet/CommanderServlet">
	
				<input type="hidden" name="deviceID" value="${device.yukonID}">
			    <input type="hidden" name="timeOut" value="8000">
			    <input id="redirect" type="hidden" name="REDIRECT" value="/spring/amr/manualCommand/home?deviceId=${deviceId}">
			    <input id="referrer" type="hidden" name="REFERRER" value="/spring/amr/manualCommand/home?deviceId=${deviceId}">
	
				<tags:nameValueContainer altRowOn="true">
					<tags:nameValue name="Type">
						${deviceType}
					</tags:nameValue>
					<tags:nameValue name="Common Commands">
		            	<select name="commonCommand" onChange="loadCommand()" onInit="loadCommand()">
		                	<option value="">Select a Command</option>                
							
							<c:forEach var="command" items="${commandList}">
								<option value="${command.command}" >${command.label}</option>
							</c:forEach>
	
						</select>
					</tags:nameValue>
					<tags:nameValue name="Execute Command">
		            	<input type="text" name="command" <cti:isPropertyFalse property="CommanderRole.EXECUTE_MANUAL_COMMAND">readonly</cti:isPropertyFalse> size="40" value="${YC_BEAN.commandString}">
					</tags:nameValue>
				</tags:nameValueContainer>	
				
				<br>
				<input type="submit" name="execute" value="Execute" onClick="disableButton(this)">					
				<br><br>
	    		
	    		<div class="scroll">
	   	    		<c:out value="${YC_BEAN.resultText}" escapeXml="false"/>
	        	</div>
	        	<div>
		    		<input type="submit" name="clearText" value="Clear Results">
		       		<input type="reset" name="refresh" value="Refresh" onClick="window.location.reload()">
		       	</div>
				
			</form>
		</tags:boxContainer>
		
	</div>
		
</cti:standardPage>		
