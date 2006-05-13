<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

<f:view>
<cti:standardPage title="Create Economic Event" module="commercialcurtailment">
<cti:standardMenu />

<h2>Create <t:outputText value="#{sEconomicCreate.program.programType.name} #{sEconomicCreate.program.name}"/> Event</h2>
<h3>Enter Parameters</h3>
<div> <t:messages showDetail="true" showSummary="false" /> </div>

<h:form binding="#{sEconomicCreate.form}">

<table class="horizBorders">
  <tr>
    <td>
      <t:outputLabel value="Notification Time" for="notifTimeInput"/>
    </td>
    <td>
      <t:inputText 
         id="notifTimeInput"
         value="#{sEconomicCreate.builder.event.notificationTime}"
         readonly="#{sEconomicCreate.builder.event.eventExtension}">
         <f:convertDateTime 
            timeStyle="short" 
            dateStyle="short"
            timeZone="#{sCommercialCurtailment.timeZone}"
            type="both" />
      </t:inputText>
      <t:outputText value="#{sCommercialCurtailment.timeZone.ID}"/>
    </td>
  </tr>
  <tr>
    <td>
      <t:outputLabel value="Start Time" for="startTimeInput"/>
    </td>
    <td>
      <t:inputText 
         id="startTimeInput"
         value="#{sEconomicCreate.builder.event.startTime}"
         readonly="#{sEconomicCreate.builder.event.eventExtension}">
         <f:convertDateTime 
            timeStyle="short" 
            dateStyle="short"
            timeZone="#{sCommercialCurtailment.timeZone}"
            type="both" />
      </t:inputText>
      <t:outputText value="#{sCommercialCurtailment.timeZone.ID}"/>
    </td>
  </tr>
  <tr>
    <td>
      <t:outputLabel value="Number of windows" for="windowInput"/>
    </td>
    <td>
      <t:inputText id="windowInput" value="#{sEconomicCreate.builder.numberOfWindows}" size="5">
        <f:validateLongRange minimum="1"/>
        <f:validateLength minimum="1"/>
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

