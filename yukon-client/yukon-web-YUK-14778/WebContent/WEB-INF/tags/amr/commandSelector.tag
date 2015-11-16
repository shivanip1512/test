<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="selectName" required="true" %>
<%@ attribute name="fieldName" required="true" %>
<%@ attribute name="commands" required="true" type="java.util.List"%>
<%@ attribute name="selectedSelectValue" %>
<%@ attribute name="selectedCommandString" %>

<cti:uniqueIdentifier var="uniqueId" prefix="commandSelector_"/>

<cti:msg var="noAuthorizedCommandsText" key="yukon.common.device.commander.commandSelector.noAuthorizedCommands"/>
<cti:msg var="selectOneLabel" key="yukon.common.device.commander.selector.selectOne"/>

<c:choose>
    <c:when test="${fn:length(commands) <= 0}">${noAuthorizedCommandsText}</c:when>
    <c:otherwise>
        <tags:commanderPrompter/>
        <select id="${uniqueId}" data-placeholder="${selectOneLabel}" 
            name="${selectName}" class="js-loadCommanderCommand js-init-chosen" data-cmdfield="${fieldName}">
            <option value="">${selectOneLabel}</option>
            <c:forEach var="commandOption" items="${commands}">
                <c:set var="selected" value=""/>
                <c:if test="${fn:escapeXml(commandOption.command) == fn:escapeXml(pageScope.selectedSelectValue)}">
                    <c:set var="selected" value="selected"/>
                </c:if>
                <option value="${fn:escapeXml(commandOption.command)}" ${selected}>${commandOption.label}</option>
            </c:forEach>
        </select><br>
    </c:otherwise>
</c:choose>
<input type="text" <cti:isPropertyFalse property="EXECUTE_MANUAL_COMMAND">readonly</cti:isPropertyFalse>
       id="${fieldName}"  
       style="margin-top:8px;" 
       name="${fieldName}" 
       value="${pageScope.selectedCommandString}" 
       size="60">