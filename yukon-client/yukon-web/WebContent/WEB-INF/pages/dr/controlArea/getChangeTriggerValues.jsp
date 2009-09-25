<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

    <p>
        <cti:msg key="yukon.web.modules.dr.controlArea.getChangeTriggerValues.instructions"
            argument="${controlArea.name}" />
    </p>

    <form id="getChangeTimeWindowValues" action="/spring/dr/controlArea/triggerChange">
        <input type="hidden" name="controlAreaId" value="${controlArea.paoIdentifier.paoId}"/>
        
        <cti:msg var="thresholdName" key="yukon.web.modules.dr.controlArea.getChangeTriggerValues.threshold"/>
        <cti:msg var="offsetName" key="yukon.web.modules.dr.controlArea.getChangeTriggerValues.offset"/>
        <c:forEach var="trigger" items="${triggers}">
          <cti:msg key="yukon.web.modules.dr.controlArea.getChangeTriggerValues.trigger" argument="${trigger.triggerNumber}"/>
          <div style="border: 1px solid gray; width: 80%;">
              <tags:nameValueContainer>
                  <tags:nameValue name="${thresholdName}">
                      <input type="text" name="threshold${trigger.triggerNumber}" value="${trigger.threshold}"/><br>
                  </tags:nameValue>
                  <tags:nameValue name="${offsetName}">
                      <input type="text" name="offset${trigger.triggerNumber}" value="${trigger.minRestoreOffset}"/><br>
                  </tags:nameValue>
              </tags:nameValueContainer>
          </div>
          <br><br>
        </c:forEach>
        
        <br><br>
        <div class="actionArea">
            <input type="button" value="<cti:msg key="yukon.web.modules.dr.controlArea.getChangeTriggerValues.okButton"/>"
                onclick="submitFormViaAjax('drDialog', 'getChangeTimeWindowValues')"/>
            <input type="button" value="<cti:msg key="yukon.web.modules.dr.controlArea.getChangeTriggerValues.cancelButton"/>"
                onclick="parent.$('drDialog').hide()"/>
        </div>
    </form>
