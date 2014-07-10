<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:msgScope paths="modules.dr.program">

<script>
$(function() {
    $('#disableAllCheckbox').click(function(){
        var disable = $(this).is(':checked');
        $('.disableProgramCheckbox').prop("checked", disable);
    });
    
    $('.disableProgramCheckbox').click(function(event) {
        var allChecked = event.currentTarget.checked;
        if (event.currentTarget.checked) {
            $('.disableProgramCheckbox').each(function(index, checkbox) {
                if (!checkbox.checked) {
                    allChecked = false;
                }
            });
        }
        $('#disableAllCheckbox')[0].checked = allChecked;
    });
});
</script>

<c:choose>
    <c:when test="${enable}">
        <cti:msg2 var="dialogHeader" key=".sendEnableProgramsConfirm.confirmQuestion" htmlEscape="true" argument="${parent.name}"/>
        <cti:msg2 var="boxTitle" key=".sendEnableProgramsConfirm.programSelectTitle"/>
    </c:when>
    <c:otherwise>
        <cti:msg2 var="dialogHeader" key=".sendDisableProgramsConfirm.confirmQuestion" htmlEscape="true" argument="${parent.name}"/>
        <cti:msg2 var="boxTitle" key=".sendDisableProgramsConfirm.programSelectTitle"/>
    </c:otherwise>
</c:choose>

<h4 class="dialogQuestion stacked">${dialogHeader}</h4>

<cti:url var="submitUrl" value="/dr/program/enableDisablePrograms"/>
<form id="disableProgramsForm" action="${submitUrl}" onsubmit="return submitFormViaAjax('drDialog', 'disableProgramsForm');">
    <input type="hidden" name="enable" value="${enable}">
    <tags:sectionContainer title="${boxTitle}">
        <div class="scroll-md">
            <table class="compact-results-table dashed">
                <thead>
                    <tr>
                        <th><i:inline key=".sendDisableProgramsConfirm.programName"/></th>
                        <th><i:inline key=".sendDisableProgramsConfirm.gears"/></th>
                        <th><i:inline key=".sendDisableProgramsConfirm.currentState"/></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:forEach var="program" varStatus="status" items="${programs}">
                        <tr>
                            <td>
                                <label>
                                    <input type="checkbox" class="disableProgramCheckbox" name="disableProgram" value="${program.paoIdentifier.paoId}">
                                    ${fn:escapeXml(program.name)}
                                </label>
                            </td>
                            <td>${fn:escapeXml(gears[status.index])}</td>
                            <td class="${states[status.index].styleString}"><i:inline key="${states[status.index]}"/></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </tags:sectionContainer>

    <label>
        <input type="checkbox" id="disableAllCheckbox">
        <c:if test="${enable}">
            <cti:msg2 key=".sendEnableProgramsConfirm.enableAllPrograms"/>
        </c:if>
        <c:if test="${!enable}">
            <cti:msg2 key=".sendDisableProgramsConfirm.disableAllPrograms"/>
        </c:if>
    </label>
    <c:if test="${!enable}">
        <br>
        <label>
            <input type="checkbox" name="supressRestoration" value="true">
            <cti:msg2 key=".sendDisableProgramsConfirm.supressRestoration"/>
        </label>
    </c:if>
    
    <div class="action-area">
        <cti:button nameKey="ok" classes="primary action" type="submit"/>
        <cti:button nameKey="cancel" onclick="$('#drDialog').dialog('close');"/>
    </div>
</form>

</cti:msgScope>