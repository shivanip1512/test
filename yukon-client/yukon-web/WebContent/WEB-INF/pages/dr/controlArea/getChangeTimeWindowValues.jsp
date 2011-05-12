<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

	<cti:flashScopeMessages/>
   
    <p class="dialogQuestion">
        <cti:msg key="yukon.web.modules.dr.controlArea.getChangeTimeWindowValues.instructions" argument="${controlArea.name}" />
    </p>

    <cti:url var="submitUrl" value="/spring/dr/controlArea/sendChangeTimeWindowConfirm"/>
    
   
    <form:form id="getChangeTimeWindowValues" commandName="controlAreaTimeWindowDto" action="${submitUrl}" onsubmit="return submitFormViaAjax('drDialog', 'getChangeTimeWindowValues');">
    
        <input type="hidden" name="controlAreaId" value="${controlArea.paoIdentifier.paoId}"/>
        <table>
            <tr>
                <td><cti:msg key="yukon.web.modules.dr.controlArea.getChangeTimeWindowValues.startTime"/></td>
                <td><tags:input path="startTime" maxlength="5" size="5"/></td>
            </tr>
            <tr>
                <td><cti:msg key="yukon.web.modules.dr.controlArea.getChangeTimeWindowValues.stopTime"/></td>
                <td><tags:input path="stopTime" maxlength="5" size="5"/></td>
            </tr>
        </table>
        
        <p class="dialogFootnote">
            <cti:msg2 key="yukon.web.modules.dr.controlArea.getChangeTimeWindowValues.noteText"/>
        </p>
        
        <div class="actionArea">
            <input type="submit" value="<cti:msg key="yukon.web.modules.dr.controlArea.getChangeTimeWindowValues.okButton"/>"/>
            <input type="button" value="<cti:msg key="yukon.web.modules.dr.controlArea.getChangeTimeWindowValues.cancelButton"/>"
                onclick="parent.$('drDialog').hide()"/>
        </div>
    </form:form>
