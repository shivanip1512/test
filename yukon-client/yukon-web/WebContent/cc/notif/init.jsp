<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

<f:view>
<cti:standardPage title="Create Notification Event" module="commercialcurtailment">
<cti:standardMenu />

<h2>Create <t:outputText value="#{sNotificationCreate.program.name}"/> Event</h2>
<h3>Enter Parameters</h3>
<div> <t:messages showDetail="false" showSummary="true"/> </div>

<h:form binding="#{sNotificationCreate.form}">

<div>
<t:outputLabel value="Notification Time" for="notifTimeInput"/>:
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
<t:message for="notifTimeInput" showDetail="true" showSummary="false" detailFormat="{0}" />
</div>

<div>
<t:outputLabel value="Start Time" for="startTimeInput"/>:
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
<t:message for="startTimeInput" showDetail="true" showSummary="false" detailFormat="{0}" />
</div>

<div>
<t:outputLabel value="Duration" for="durationInput"/>:
<t:inputText id="durationInput" value="#{sNotificationCreate.builder.eventDuration}" size="5">
</t:inputText>
<t:message for="durationInput" showDetail="true" showSummary="false" detailFormat="{0}" />
</div>

<div>
<t:outputLabel value="Message" for="messageInput"/>:
<t:inputTextarea id="messageInput" value="#{sNotificationCreate.builder.message}"/>
<t:message for="messageInput" showDetail="true" showSummary="false" detailFormat="{0}" />
</div>

<div>
<h:commandButton action="#{sNotificationCreate.doAfterParameterEntry}" value="Next"/>
<h:commandButton action="#{sNotificationCreate.cancel}" value="Cancel" immediate="true"/>
</div>

</h:form>

 
</cti:standardPage>
</f:view>

