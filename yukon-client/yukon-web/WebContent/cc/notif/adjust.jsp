<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://cannontech.com/tags/faces/ctif" prefix="ctif" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<f:view>
<cti:standardPage title="Notification Event Adjust" module="commercialcurtailment">
<cti:standardMenu />

<h2>Adjust <t:outputText value="#{sNotificationDetail.event.program.programType.name} #{sNotificationDetail.event.program.name}"/> Event</h2>
<div> <t:messages showDetail="false" showSummary="true"/> </div>

<h:form>
<f:verbatim><cti:csrfToken/></f:verbatim>
<table class="horizBorders">
  <tr>
    <td>
<t:outputLabel value="Start Time" for="startTimeInput"/>
    </td>
    <td>
<t:outputText 
   id="startTimeInput"
   value="#{sNotificationDetail.changeBuilder.newStartTime}">
    <f:convertDateTime 
      pattern="#{sCommercialCurtailment.timeFormat}" 
      timeZone="#{sCommercialCurtailment.timeZone}" />
   </t:outputText>
(<t:outputText value="#{sCommercialCurtailment.timeZone.ID}"/>)
    </td>
  </tr>
  <tr>
    <td>
<t:outputLabel value="Duration" for="durationInput"/>
    </td>
    <td>
<t:inputText id="durationInput" 
     value="#{sNotificationDetail.changeBuilder.newLength}" 
     size="5"
     required="true">
        <f:validateLongRange minimum="1"/>
</t:inputText>
    </td>
  </tr>
  <tr>
    <td>
<t:outputLabel value="Message" for="messageInput"/>
    </td>
    <td>
<t:inputTextarea id="messageInput" value="#{sNotificationDetail.changeBuilder.newMessage}"/>
    </td>
  </tr>
</table>

<div>
<h:commandButton action="#{sNotificationDetail.adjustEvent}" value="Next"/>
<h:commandButton action="#{sNotificationDetail.cancelAdjust}" value="Cancel" immediate="true"/>
</div>




</h:form>

 
</cti:standardPage>
</f:view>

