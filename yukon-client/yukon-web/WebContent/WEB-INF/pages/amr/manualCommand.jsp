<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
    
<cti:standardPage module="amr" page="manualCommand">

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

    <tags:widgetContainer deviceId="${deviceId}" identify="false">
        <table class="widgetColumns">
            <tr>
                <td class="widgetColumnCell" valign="top">
                    <div style="width:600px;">
                    <tags:widgetContainer deviceId="${deviceId}">
                       <tags:widget bean="meterInformationWidget" />
                    </tags:widgetContainer>
                    </div>
                    <tags:boxContainer2 nameKey="executeCommand" hideEnabled="false" styleClass="widgetContainer">
                        <form name="commandForm" method="POST" action="/servlet/CommanderServlet">
                
                            <input type="hidden" name="deviceID" value="${device.yukonID}">
                            <input type="hidden" name="timeOut" value="8000">
                            <input id="redirect" type="hidden" name="REDIRECT" value="/spring/amr/manualCommand/home?deviceId=${deviceId}">
                            <input id="referrer" type="hidden" name="REFERRER" value="/spring/amr/manualCommand/home?deviceId=${deviceId}">
                
                            <tags:nameValueContainer2> <!--  altRowOn="true"> -->
                                <tags:nameValue2 nameKey=".commonCommands">
                                    <select name="commonCommand" onchange="loadCommanderCommand(this, 'command');">
                                        <option value=""><i:inline key=".selectCommand"/></option>                
                                        
                                        <c:forEach var="command" items="${commandList}">
                                            <option value="${command.command}" >${command.label}</option>
                                        </c:forEach>
                                    </select>
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".executeCommand">
                                    <input type="text" id="command" name="command" <cti:isPropertyFalse property="CommanderRole.EXECUTE_MANUAL_COMMAND">readonly</cti:isPropertyFalse> size="60" value="${YC_BEAN.commandString}">
                                    <cti:button key="execute" name="execute" onclick="disableButton(this)" type="submit" />
                                </tags:nameValue2>
                            </tags:nameValueContainer2>  
							<br>
                            <div class="scroll">
                                <c:out value="${YC_BEAN.resultText}" escapeXml="false"/>
                            </div>
                            <div>
                                <cti:button key="clearResults" name="clearText" type="submit"/>
                                <cti:button key="refresh" name="refresh" onclick="window.location.reload()"/>
                            </div>
                        </form>
                    </tags:boxContainer2>
        
                </td>
            </tr>
        </table>
    
    </tags:widgetContainer>
    
</cti:standardPage>		
