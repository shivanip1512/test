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
                    <tags:widgetContainer deviceId="${deviceId}">
                       <tags:widget bean="meterInformationWidget" />
                    </tags:widgetContainer>
                    <tags:boxContainer2 nameKey="executeCommand" hideEnabled="false" styleClass="widgetContainer">
                        <form name="commandForm" method="POST" action="/servlet/CommanderServlet">
                
                            <input type="hidden" name="deviceID" value="${device.yukonID}">
                            <input type="hidden" name="timeOut" value="8000">
                            <input id="redirect" type="hidden" name="REDIRECT" value="/amr/manualCommand/home?deviceId=${deviceId}">
                            <input id="referrer" type="hidden" name="REFERRER" value="/amr/manualCommand/home?deviceId=${deviceId}">
                
                            <tags:nameValueContainer2> <!--  altRowOn="true"> -->
                                <tags:nameValue2 nameKey=".commonCommands">
                                    <tags:commanderPrompter/>
                                    <select name="commonCommand" class="f_loadCommanderCommand" data-cmdfield="command">
                                        <option value=""><i:inline key=".selectCommand"/></option>                
                                        
                                        <c:forEach var="command" items="${commandList}">
                                            <option value="${command.command}" >${command.label}</option>
                                        </c:forEach>
                                    </select>
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".executeCommand">
                                    <input type="text" id="command" name="command" <cti:isPropertyFalse property="EXECUTE_MANUAL_COMMAND">readonly</cti:isPropertyFalse> size="60" value="${YC_BEAN.commandString}">
                                    <cti:button nameKey="execute" name="execute" onclick="disableButton(this)" type="submit" />
                                </tags:nameValue2>
                            </tags:nameValueContainer2>  
							<br>
                            <div class="scroll">
                                <c:out value="${YC_BEAN.resultText}" escapeXml="false"/>
                            </div>
                            <div>
                                <cti:button nameKey="clearResults" name="clearText" type="submit"/>
                                <cti:button nameKey="refresh" name="refresh" onclick="window.location.reload()"/>
                            </div>
                        </form>
                    </tags:boxContainer2>
    </tags:widgetContainer>
    
</cti:standardPage>		
