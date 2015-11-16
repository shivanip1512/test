<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<f:view>
<cti:standardPage title="Create Economic Event" module="commercialcurtailment">
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
<h:form>
<f:verbatim><cti:csrfToken/></f:verbatim>
<table class="horizBorders">
  <tr>
    <td>
      <t:outputLabel value="Notification Time" for="notifTimeInput"/>
    </td>
    <td>
      <t:inputDate 
         id="notifTimeInput"
         value="#{sEconomicCreate.builder.event.notificationTime}"
         popupCalendar="false"
         ampm="false"
         type="both"
         timeZone="#{sCommercialCurtailment.timeZone.ID}"
         rendered="#{!sEconomicCreate.builder.event.eventExtension}"/>
      <t:outputText
         id="notifTimeInput2"
         value="#{sEconomicCreate.builder.event.notificationTime}"
         rendered="#{sEconomicCreate.builder.event.eventExtension}">
         <f:convertDateTime
           timeStyle="short" 
           dateStyle="medium"
           timeZone="#{sCommercialCurtailment.timeZone}"
           type="both"/>
      </t:outputText>
      <t:outputText value="#{sCommercialCurtailment.timeZone.ID}"/>
    </td>
  </tr>
  <tr>
    <td>
      <t:outputLabel value="Start Time" for="startTimeInput"/>
    </td>
    <td>
      <t:inputDate
         id="startTimeInput"
         value="#{sEconomicCreate.builder.event.startTime}"
         rendered="#{!sEconomicCreate.builder.event.eventExtension}"
         popupCalendar="false"
         ampm="false"
         timeZone="#{sCommercialCurtailment.timeZone.ID}"
         type="both" />
      <t:outputText
         id="startTimeInput2"
         value="#{sEconomicCreate.builder.event.startTime}"
         rendered="#{sEconomicCreate.builder.event.eventExtension}">
         <f:convertDateTime
           timeStyle="short" 
           dateStyle="medium"
           timeZone="#{sCommercialCurtailment.timeZone}"
           type="both"/>
      </t:outputText>
      <t:outputText value="#{sCommercialCurtailment.timeZone.ID}"/>
    </td>
  </tr>
  <tr>
    <td>
      <t:outputLabel value="Number of windows" for="windowInput"/>
    </td>
    <td>
      <t:inputText id="windowInput" value="#{sEconomicCreate.builder.numberOfWindows}" 
            size="5"
            required="true">
        <f:validateLongRange minimum="1"/>
      </t:inputText>
      (Each window is <t:outputText value="#{sEconomicCreate.windowLength}"/> minutes long)
    </td>
  </tr>
</table>

<div class="actionButtons">
<h:commandButton action="#{sEconomicCreate.doAfterInitialEntry}" value="Next"/>
<h:commandButton action="#{sEconomicCreate.cancel}" value="Cancel" immediate="true"/>
</div>

</h:form>

 
</cti:standardPage>
</f:view>

