<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.dr.controlArea.getChangeTimeWindowValues">
	<cti:flashScopeMessages/>
   
    <p class="dialogQuestion stacked">
        <cti:msg2 key=".instructions" argument="${controlArea.name}" />
    </p>

    <cti:url var="submitUrl" value="/dr/controlArea/sendChangeTimeWindowConfirm"/>
    
    <form:form id="getChangeTimeWindowValues" modelAttribute="controlAreaTimeWindowDto" action="${submitUrl}" onsubmit="return submitFormViaAjax('drDialog', 'getChangeTimeWindowValues');">
        <cti:csrfToken/>
        <input type="hidden" name="controlAreaId" value="${controlArea.paoIdentifier.paoId}"/>
        <table class="stacked">
            <tr>
                <td><cti:msg2 key=".startTime"/></td>
                <td><tags:input path="startTime" maxlength="5" size="5"/></td>
            </tr>
            <tr>
                <td><cti:msg2 key=".stopTime"/></td>
                <td><tags:input path="stopTime" maxlength="5" size="5"/></td>
            </tr>
        </table>
        
        <p class="dialogFootnote">
            <cti:msg2 key=".noteText"/>
        </p>
        
        <div class="action-area">
            <cti:button nameKey="ok" classes="primary action" type="submit"/>
            <cti:button nameKey="cancel" onclick="$('#drDialog').dialog('close');"/>
        </div>
    </form:form>
</cti:msgScope>