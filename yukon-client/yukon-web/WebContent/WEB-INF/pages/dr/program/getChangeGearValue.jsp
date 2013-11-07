<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<cti:msgScope paths="modules.dr.program.getChangeGearValue">

<c:if test="${fn:length(gears) > 1}">
    <h4 class="dialogQuestion"><cti:msg2 key=".instructions" argument="${program.name}"/></h4>

    <form id="sendEnableForm" action="/dr/program/changeGear">
        <input type="hidden" name="programId" value="${program.paoIdentifier.paoId}"/>
        <cti:msg2 key=".gearSelect"/>
        <select name="gearNumber">
            <c:forEach var="gear" items="${gears}">
                <c:if test="${currentGear.gearNumber != gear.gearNumber}">
                    <option value="${gear.gearNumber}">${fn:escapeXml(gear.gearName)}</option>
                </c:if>
            </c:forEach>
        </select>
        
        <div class="action-area">
            <cti:button nameKey="cancel" onclick="jQuery('#drDialog').dialog('close');"/>
            <cti:button nameKey="ok" classes="primary action" onclick="submitFormViaAjax('drDialog', 'sendEnableForm');"/>
        </div>
    </form>
</c:if>

<c:if test="${fn:length(gears) < 2}">
    <p><cti:msg2 key=".notEnoughGears" argument="${program.name}"/></p>

    <div class="action-area">
        <cti:button nameKey="ok" onclick="jQuery('#drDialog').dialog('close');"/>
    </div>
</c:if>
</cti:msgScope>