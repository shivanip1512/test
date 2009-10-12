<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

    <h1 class="dialogQuestion">
        <cti:msg key="yukon.web.modules.dr.program.getChangeGearValue.instructions" argument="${program.name}"/>
    </h1>

    <form id="sendEnableForm" action="/spring/dr/program/changeGear">
        <input type="hidden" name="programId" value="${program.paoIdentifier.paoId}"/>
        <cti:msg key="yukon.web.modules.dr.program.getChangeGearValue.gearSelect"/>
        <select name="gearNumber">
            <c:forEach var="gear" items="${gears}">
                <option value="${gear.gearNumber}">
                    <spring:escapeBody htmlEscape="true">${gear.gearName}</spring:escapeBody>
                </option>
            </c:forEach>
        </select>
        
        <div class="actionArea">
            <input type="button" value="<cti:msg key="yukon.web.modules.dr.program.getChangeGearValue.okButton"/>"
                onclick="submitFormViaAjax('drDialog', 'sendEnableForm')"/>
            <input type="button" value="<cti:msg key="yukon.web.modules.dr.program.getChangeGearValue.cancelButton"/>"
                onclick="parent.$('drDialog').hide()"/>
        </div>
    </form>
