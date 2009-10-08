<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<%@ attribute name="selectName" required="true" type="java.lang.String"%>
<%@ attribute name="fieldName" required="true" type="java.lang.String"%>
<%@ attribute name="commands" required="true" type="java.util.List"%>
<%@ attribute name="selectedSelectValue" required="false" type="java.lang.String"%>
<%@ attribute name="selectedCommandString" required="false" type="java.lang.String"%>
<%@ attribute name="includeDummyOption" required="false" type="java.lang.Boolean"%>

<c:if test="${empty pageScope.includeDummyOption}">
	<c:set var="includeDummyOption" value="false"/>
</c:if>

<cti:includeScript link="/JavaScript/extjs/ext-base.js"/>
<cti:includeScript link="/JavaScript/extjs/ext-all.js"/>
<cti:includeScript link="/JavaScript/commanderPrompter.js"/>

<cti:uniqueIdentifier var="uniqueId" prefix="commandSelector_"/>

<cti:msg var="noAuthorizedCommandsText" key="yukon.common.device.commander.commandSelector.noAuthorizedCommands"/>

<c:choose>
    <c:when test="${fn:length(commands) <= 0}">
        ${noAuthorizedCommandsText}
    </c:when>
    
    <c:otherwise>
        <select id="${uniqueId}" name="${selectName}" onChange="loadCommanderCommand(this, '${fieldName}');">
        
        	<c:if test="${pageScope.includeDummyOption}">
				<cti:msg var="selectOneLabel" key="yukon.common.device.commander.selector.selectOne"/>
				<option value="">${selectOneLabel}</option>
			</c:if>
        
            <c:forEach var="commandOption" items="${commands}">
            
            	<c:set var="selected" value=""/>
                <c:if test="${fn:escapeXml(commandOption.command) == fn:escapeXml(pageScope.selectedSelectValue)}">
                    <c:set var="selected" value="selected"/>
                </c:if>
            
                <option value="${fn:escapeXml(commandOption.command)}" ${selected}> ${commandOption.label}</option>
                
            </c:forEach>
        </select>
    </c:otherwise>
</c:choose>
<br>
<input type="text" id="${fieldName}" style="margin-top:8px;" name="${fieldName}" value="${pageScope.selectedCommandString}" <cti:isPropertyFalse property="CommanderRole.EXECUTE_MANUAL_COMMAND">readonly</cti:isPropertyFalse> size="60" />

