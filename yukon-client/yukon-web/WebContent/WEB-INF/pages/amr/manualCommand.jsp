<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
    
<cti:standardPage title="Manual Commander Page" module="amr">
	<cti:standardMenu menuSelection="meters" />
	<cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
	    <cti:crumbLink url="/spring/meter/search" title="Meters"  />
	    <cti:crumbLink url="/spring/meter/home?deviceId=${deviceId}">
            <cti:deviceName deviceId="${deviceId}"></cti:deviceName>
        </cti:crumbLink>
	    &gt; Manual Commander
	</cti:breadCrumbs>

<style type="text/css">
	div.scroll { font-size: 12px; 
				 height: 350px;  
				 overflow: scroll; 
				 border: 1px solid #CCCCCC; 
				 text-align:left; 
				 padding: 8px;
				}
</style>

<cti:includeScript link="/JavaScript/extjs/ext-base.js"/>
<cti:includeScript link="/JavaScript/extjs/ext-all.js"/>
<cti:includeScript link="/JavaScript/commanderPrompter.js"/>

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
    <tags:widgetContainer deviceId="${deviceId}" identify="false">
        <table class="widgetColumns">
            <tr>
                <td class="widgetColumnCell" valign="top">
                    <div style="width:600px;">
                    <tags:widgetContainer deviceId="${deviceId}">
                       <tags:widget bean="meterInformationWidget" />
                    </tags:widgetContainer>
                    </div>
                    <tags:boxContainer title="Execute Command" hideEnabled="false" styleClass="widgetContainer">
                        <form name="commandForm" method="POST" action="/servlet/CommanderServlet">
                
                            <input type="hidden" name="deviceID" value="${device.yukonID}">
                            <input type="hidden" name="timeOut" value="8000">
                            <input id="redirect" type="hidden" name="REDIRECT" value="/spring/amr/manualCommand/home?deviceId=${deviceId}">
                            <input id="referrer" type="hidden" name="REFERRER" value="/spring/amr/manualCommand/home?deviceId=${deviceId}">
                
                            <tags:nameValueContainer altRowOn="true">
                                <tags:nameValue name="Common Commands">
                                    <select name="commonCommand" onchange="loadCommanderCommand(this, 'command');">
                                        <option value="">Select a Command</option>                
                                        
                                        <c:forEach var="command" items="${commandList}">
                                            <option value="${command.command}" >${command.label}</option>
                                        </c:forEach>
                                    </select>
                                </tags:nameValue>
                                <tags:nameValue name="Execute Command">
                                    <input type="text" id="command" name="command" <cti:isPropertyFalse property="CommanderRole.EXECUTE_MANUAL_COMMAND">readonly</cti:isPropertyFalse> size="60" value="${YC_BEAN.commandString}">
                                    <input type="submit" name="execute" value="Execute" onClick="disableButton(this)">                  
                                    
                                </tags:nameValue>
                            </tags:nameValueContainer>  
							<br>
                            <div class="scroll">
                                <c:out value="${YC_BEAN.resultText}" escapeXml="false"/>
                            </div>
                            <div>
                                <input type="submit" name="clearText" value="Clear Results">
                                <input type="reset" name="refresh" value="Refresh" onClick="window.location.reload()">
                            </div>
                            
                        </form>
                    </tags:boxContainer>
        
                </td>
            </tr>
        </table>
    
    </tags:widgetContainer>
    
</cti:standardPage>		
