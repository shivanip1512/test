<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="amr" page="manualCommand">

<script language="JavaScript">
function loadCommand() {
    document.commandForm.command.value = document.commandForm.commonCommand.value;
}
</script>

    <tags:widgetContainer deviceId="${deviceId}">
        <div class="column-12-12 clearfix">
            <div class="one column">
                <tags:widget bean="meterInformationWidget" container="section"/>
            </div>
            <div class="two column nogutter">
                <tags:widget bean="csrTrendWidget" tabularDataViewer="archivedDataReport" container="section"/>
            </div>
        </div>
    </tags:widgetContainer>
    
    <tags:sectionContainer2 nameKey="executeCommand" styleClass="clear">
    
        <form name="commandForm" method="POST" action="<cti:url value="/servlet/CommanderServlet"/>">
            <cti:csrfToken/>
            <input type="hidden" name="deviceID" value="${device.yukonID}">
            <input type="hidden" name="timeOut" value="8000">
            <input id="redirect" type="hidden" name="REDIRECT" value="/amr/manualCommand/home?deviceId=${deviceId}">
            <input id="referrer" type="hidden" name="REFERRER" value="/amr/manualCommand/home?deviceId=${deviceId}">

            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".commonCommands">
                    <tags:commanderPrompter/>
                    <select name="commonCommand" class="js-loadCommanderCommand" data-cmdfield="command">
                        <option value=""><i:inline key=".selectCommand"/></option>
                        
                        <c:forEach var="command" items="${commandList}">
                            <option value="${command.command}" >${command.label}</option>
                        </c:forEach>
                    </select>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".executeCommand">
                    <input type="text" id="command" name="command" <cti:isPropertyFalse property="EXECUTE_MANUAL_COMMAND">readonly</cti:isPropertyFalse> size="60" value="${YC_BEAN.commandString}">
                </tags:nameValue2>
            </tags:nameValueContainer2>
            <div class="page-action-area">
                <cti:button nameKey="execute" name="execute" type="submit" classes="primary action js-disable-after-click"/>
                <cti:button nameKey="clear" name="clearText" type="submit"/>
                <cti:button nameKey="refresh" name="refresh" onclick="window.location.reload()"/>
            </div>
            <div id="command_results" class="lite-container stacked code scroll-lg" style="min-height: 200px;">
                <div class="console"><h4><i:inline key="yukon.common.console"/></h4></div>
                <c:out value="${YC_BEAN.resultText}" escapeXml="false"/>
            </div>
        </form>
    </tags:sectionContainer2>
    
</cti:standardPage>