<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="id" %>
<%@ attribute name="selectName" required="true" %>
<%@ attribute name="fieldName" required="true" %>
<%@ attribute name="commands" required="true" type="java.util.List"%>
<%@ attribute name="selectedSelectValue" %>
<%@ attribute name="selectedCommandString" %>

<cti:uniqueIdentifier var="uniqueId" prefix="commandSelector_"/>
<cti:default var="id" value="uniqueId"/>

<cti:msg var="selectOneLabel" key="yukon.common.device.commander.selector.selectOne"/>
<cti:msgScope paths="yukon.web.modules.tools.commander">
<tags:nameValueContainer2 tableClass="with-form-controls">
    <tags:nameValue2 nameKey=".availableCommands" nameClass="vab">
        <c:choose>
            <c:when test="${empty commands}">
                <div class="empty-list">
                    <i:inline key="yukon.common.device.commander.commandSelector.noAuthorizedCommands"/>
                </div>
            </c:when>
            <c:otherwise>
                <div>
                <tags:commanderPrompter/>
                <select id="${id}" data-placeholder="${selectOneLabel}"
                    name="${selectName}" class="js-loadCommanderCommand js-init-chosen" data-cmdfield="${fieldName}">
                    <option value="">${selectOneLabel}</option>
                    <c:forEach var="commandOption" items="${commands}">
                        <c:set var="selected" value=""/>
                        <c:if test="${fn:escapeXml(commandOption.command) == fn:escapeXml(pageScope.selectedSelectValue)}">
                            <c:set var="selected" value="selected"/>
                        </c:if>
                        <option value="${fn:escapeXml(commandOption.command)}" ${selected}>${commandOption.label}</option>
                    </c:forEach>
                </select>
                </div>
            </c:otherwise>
        </c:choose>
    </tags:nameValue2>
    <tags:nameValue2 nameKey=".command" nameClass="vab">
        <c:set var="manualCommands" value="false" />
        <cti:checkRolesAndProperties value="EXECUTE_MANUAL_COMMAND">
            <c:set var="manualCommands" value="true"/>
        </cti:checkRolesAndProperties>
        <input type="text"
               <c:if test="${not manualCommands}">readonly</c:if>
               id="${fieldName}"
               style="margin-top:8px;"
               name="${fieldName}"
               value="${pageScope.selectedCommandString}"
               size="60">
    </tags:nameValue2>
</tags:nameValueContainer2>
</cti:msgScope>