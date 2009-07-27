<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<%@ attribute name="selectName" required="true" type="java.lang.String"%>
<%@ attribute name="fieldName" required="true" type="java.lang.String"%>
<%@ attribute name="commands" required="true" type="java.util.List"%>
<%@ attribute name="selectedSelectValue" required="false" type="java.lang.String"%>
<%@ attribute name="selectedCommandString" required="false" type="java.lang.String"%>

<cti:includeScript link="/JavaScript/extjs/ext-base.js"/>
<cti:includeScript link="/JavaScript/extjs/ext-all.js"/>
<cti:includeScript link="/JavaScript/commanderPrompter.js"/>

<cti:uniqueIdentifier var="uniqueId" prefix="commandSelector_"/>

<cti:msg var="selectCommandLabel" key="yukon.common.device.commander.commandSelector.selectCommand"/>
<cti:msg var="noAuthorizedCommandsText" key="yukon.common.device.commander.commandSelector.noAuthorizedCommands"/>

<script type="text/javascript">
        	
	function selectCommand() {
		//$('${fieldName}').value = $F('${selectName}');
		//loadCommanderCommand($('${selectName}'), '${fieldName}');
	}
	
	Event.observe (window, 'load', selectCommand);

</script>

<div class="largeBoldLabel">${selectCommandLabel}</div>
            
<c:choose>
    <c:when test="${fn:length(commands) <= 0}">
        ${noAuthorizedCommandsText}
    </c:when>
    
    <c:otherwise>
        <select id="${uniqueId}" name="${selectName}" onChange="loadCommanderCommand(this, '${fieldName}');">
            <c:forEach var="commandOption" items="${commands}">
            
            	<c:set var="selected" value=""/>
                <c:if test="${fn:escapeXml(commandOption.command) == fn:escapeXml(selectedSelectValue)}">
                    <c:set var="selected" value="selected"/>
                </c:if>
            
                <option value="${fn:escapeXml(commandOption.command)}" ${selected}> ${commandOption.label}</option>
                
            </c:forEach>
        </select>
    </c:otherwise>
</c:choose>
<br>
<input type="text" id="${fieldName}" style="margin-top:8px;" name="${fieldName}" value="${selectedCommandString}" <cti:isPropertyFalse property="CommanderRole.EXECUTE_MANUAL_COMMAND">readonly</cti:isPropertyFalse> size="60" />

