<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i"tagdir="/WEB-INF/tags/i18n" %>

<script>
jQuery(function() {
    jQuery('#disableAllCheckbox').click(function(){
        var disable = jQuery(this).is(':checked')
        jQuery('.disableProgramCheckbox').prop("checked", disable);
    });
    
    jQuery('#cancelButton').click(function(){
        jQuery('#drDialog').hide();
    });
    
    jQuery('.disableProgramCheckbox').click(function(event) {
        var allChecked = event.currentTarget.checked;
        if (event.currentTarget.checked) {
            jQuery('.disableProgramCheckbox').each(function(index, checkbox) {
                if (!checkbox.checked) {
                    allChecked = false;
                }
            });
        }
        jQuery('#disableAllCheckbox')[0].checked = allChecked;
    });
});
</script>

<c:choose>
    <c:when test="${enable}">
        <cti:msg var="dialogHeader" key="yukon.web.modules.dr.program.sendEnableProgramsConfirm.confirmQuestion"
            htmlEscape="true" argument="${parent.name}"/>
        <cti:msg var="boxTitle" key="yukon.web.modules.dr.program.sendEnableProgramsConfirm.programSelectTitle"/>
    </c:when>
    <c:otherwise>
        <cti:msg var="dialogHeader" key="yukon.web.modules.dr.program.sendDisableProgramsConfirm.confirmQuestion"
            htmlEscape="true" argument="${parent.name}"/>
        <cti:msg var="boxTitle" key="yukon.web.modules.dr.program.sendDisableProgramsConfirm.programSelectTitle"/>
    </c:otherwise>
</c:choose>

<h1 class="dialogQuestion">
    ${dialogHeader}
</h1>
<br>
<cti:url var="submitUrl" value="/spring/dr/program/enableDisablePrograms"/>
<form id="disableProgramsForm" action="${submitUrl}" onsubmit="return submitFormViaAjax('drDialog', 'disableProgramsForm');">
    <input type="hidden" name="enable" value="${enable}">
    <tags:abstractContainer type="box" title="${boxTitle}">
        <div class="dialogScrollArea">
            <table class="compactResultsTable">
                <tr class="<tags:alternateRow odd="" even="altRow"/>">
                    <th><cti:msg key="yukon.web.modules.dr.program.sendDisableProgramsConfirm.programName"/></th>
                    <th><cti:msg key="yukon.web.modules.dr.program.sendDisableProgramsConfirm.gears"/></th>
                    <th><cti:msg key="yukon.web.modules.dr.program.sendDisableProgramsConfirm.currentState"/></th>
                </tr>
                <c:forEach var="program" varStatus="status" items="${programs}">
                    <tr class="<tags:alternateRow odd="" even="altRow"/>">
                        <td>
                            <label>
                                <input type="checkbox" class="disableProgramCheckbox" name="disableProgram" value="${program.paoIdentifier.paoId}">
                                ${fn:escapeXml(program.name)}
                            </label>
                        </td><td>
                            ${fn:escapeXml(gears[status.index])}
                        </td><td>
                            <i:inline key="${states[status.index]}"/>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </tags:abstractContainer>
    <br>
    <label>
        <input type="checkbox" id="disableAllCheckbox">
        <c:if test="${enable}">
            <cti:msg key="yukon.web.modules.dr.program.sendEnableProgramsConfirm.enableAllPrograms"/>
        </c:if>
        <c:if test="${!enable}">
            <cti:msg key="yukon.web.modules.dr.program.sendDisableProgramsConfirm.disableAllPrograms"/>
        </c:if>
    </label>
    <c:if test="${!enable}">
        <br>
        <label>
            <input type="checkbox" name="supressRestoration" value="true">
            <cti:msg key="yukon.web.modules.dr.program.sendDisableProgramsConfirm.supressRestoration"/>
        </label>
    </c:if>
    
    <div class="actionArea">
        <input type="submit" value="<cti:msg key="yukon.web.modules.dr.program.sendDisableProgramsConfirm.okButton"/>"/>
        <input type="button" id="cancelButton" value="<cti:msg key="yukon.web.modules.dr.program.sendDisableProgramsConfirm.cancelButton"/>"/>
    </div>
</form>