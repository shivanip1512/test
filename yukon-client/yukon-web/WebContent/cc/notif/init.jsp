<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

<f:view>
<cti:standardPage title="Create Notification Event" module="commercialcurtailment">
<cti:standardMenu />

<h2>Create <t:outputText value="#{sEconomicCreate.program.programType.name} #{sEconomicCreate.program.name}"/> Event</h2>
<h3>Enter Parameters</h3>
<div class="jsfMessages"> 
<t:messages showSummary="false" showDetail="true" 
            errorClass="jsfError" 
            warnClass="jsfWarn" 
            infoClass="jsfInfo" 
            fatalClass="jsfFatal"/> 
</div>

<h:form binding="#{sNotificationCreate.form}">

<table class="horizBorders">
  <tr>
    <td>
<t:outputLabel value="Notification Time" for="notifTimeInput"/>
    </td>
    <td>
<t:inputText 
   id="notifTimeInput"
   value="#{sNotificationCreate.builder.notificationTime}">
   <f:convertDateTime 
      timeStyle="short" 
      dateStyle="short"
      timeZone="#{sCommercialCurtailment.timeZone}"
      type="both" />
</t:inputText>
(<t:outputText value="#{sCommercialCurtailment.timeZone.ID}"/>)
    </td>
  </tr>
  <tr>
    <td>
<t:outputLabel value="Start Time" for="startTimeInput"/>
    </td>
    <td>
<t:inputText 
   id="startTimeInput"
   value="#{sNotificationCreate.builder.startTime}">
   <f:convertDateTime 
      timeStyle="short" 
      dateStyle="short"
      timeZone="#{sCommercialCurtailment.timeZone}"
      type="both" />
</t:inputText>
(<t:outputText value="#{sCommercialCurtailment.timeZone.ID}"/>)
    </td>
  </tr>
  <tr>
    <td>
<t:outputLabel value="Duration" for="durationInput"/>
    </td>
    <td>
<t:inputText id="durationInput" value="#{sNotificationCreate.builder.eventDuration}" size="5">
        <f:validateLongRange minimum="1"/>
        <f:validateLength minimum="1"/></t:inputText>
    </td>
  </tr>
  <tr>
    <td>
<t:outputLabel value="Message" for="messageInput"/>
    </td>
    <td>
<t:inputTextarea id="messageInput" value="#{sNotificationCreate.builder.message}"/>
    </td>
  </tr>
</table>

<div>
<h:commandButton action="#{sNotificationCreate.doAfterParameterEntry}" value="Next"/>
<h:commandButton action="#{sNotificationCreate.cancel}" value="Cancel" immediate="true"/>
</div>

</h:form>

 
</cti:standardPage>
</f:view>

