<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<c:if test="${fn:length(gears) > 1}">
    <h1 class="dialogQuestion">
        <cti:msg key="yukon.web.modules.dr.program.getChangeGearValue.instructions" argument="${program.name}"/>
    </h1>

    <form id="sendEnableForm" action="/dr/program/changeGear">
        <input type="hidden" name="programId" value="${program.paoIdentifier.paoId}"/>
        <cti:msg key="yukon.web.modules.dr.program.getChangeGearValue.gearSelect"/>
        <select name="gearNumber">
            <c:forEach var="gear" items="${gears}">
                <c:if test="${currentGear.gearNumber != gear.gearNumber}">
                    <option value="${gear.gearNumber}">
                        <spring:escapeBody htmlEscape="true">${gear.gearName}</spring:escapeBody>
                    </option>
                </c:if>
            </c:forEach>
        </select>
        
        <div class="actionArea">
            <input type="button" value="<cti:msg key="yukon.web.modules.dr.program.getChangeGearValue.okButton"/>"
                onclick="submitFormViaAjax('drDialog', 'sendEnableForm')"/>
            <input type="button" value="<cti:msg key="yukon.web.modules.dr.program.getChangeGearValue.cancelButton"/>"
                onclick="parent.$('drDialog').hide()"/>
        </div>
    </form>
</c:if>

<c:if test="${fn:length(gears) < 2}">
    <p><cti:msg key="yukon.web.modules.dr.program.getChangeGearValue.notEnoughGears" argument="${program.name}"/></p>

    <div class="actionArea">
        <input type="button" value="<cti:msg key="yukon.web.modules.dr.program.getChangeGearValue.okButton"/>"
            onclick="parent.$('drDialog').hide()"/>
    </div>
</c:if>
