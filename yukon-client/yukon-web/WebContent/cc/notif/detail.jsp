<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://cannontech.com/tags/faces/ctif" prefix="ctif" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

<f:view>
<cti:standardPage title="Notification Event Detail" module="commercialcurtailment">
<cti:standardMenu />

<h2><t:outputText value="#{sEconomicCreate.program.programType.name} #{sEconomicCreate.program.name}"/> Event</h2>
<div> <t:messages showDetail="false" showSummary="true"/> </div>

<h:form>

<table class="horizBorders">
  <tr>
    <td>Notification Time</td>
    <td><t:outputText 
       value="#{sNotificationDetail.event.notificationTime}">
       <f:convertDateTime 
          timeStyle="short" 
          dateStyle="short"
          timeZone="#{sCommercialCurtailment.timeZone}"
          type="both" />
    </t:outputText>
    (<t:outputText value="#{sCommercialCurtailment.timeZone.ID}"/>)
    </td>
  </tr>
  <tr>
    <td>Start Time</td>
    <td><t:outputText 
       value="#{sNotificationDetail.event.startTime}">
       <f:convertDateTime 
          timeStyle="short" 
          dateStyle="short"
          timeZone="#{sCommercialCurtailment.timeZone}"
          type="both" />
    </t:outputText>
    (<t:outputText value="#{sCommercialCurtailment.timeZone.ID}"/>)
    </td>
  </tr>
  <tr>
    <td>Duration</td>
    <td><t:outputText value="#{sNotificationDetail.event.duration}"/></td>
  </tr>
  <tr>
    <td>Modification State</td>
    <td><t:outputText value="#{sNotificationDetail.event.state}"/></td>
  </tr>
  <tr>
    <td>Message</td>
    <td><t:outputText value="#{sNotificationDetail.event.message}"/></td>
  </tr>
  <tr>
    <td>Operations</td>
    <td>
      <t:commandButton action="#{sNotificationDetail.deleteEvent}" value="Delete" rendered="#{sNotificationDetail.showDeleteButton}"/>
      <t:commandButton action="#{sNotificationDetail.cancelEvent}" value="Cancel" rendered="#{sNotificationDetail.showCancelButton}"/>
      <t:commandButton action="#{sNotificationDetail.adjustEvent}" value="Adjust" rendered="#{sNotificationDetail.showAdjustButton}"/>
    </td>
  </tr>
</table>


<div>
<cti:titledContainer title="Customer Notification Status">
<ctif:autoUpdateDataTable 
     value="#{sNotificationDetailHelper.notifList}" 
     var="thisNotif" 
     styleClass="light_table" 
     frequency="5">
  <t:column>
    <f:facet name="header"><h:outputText value="Company"/></f:facet>
    <t:outputText value="#{thisNotif.participant.customer.companyName}"/>
  </t:column>
  <t:column>
    <f:facet name="header"><h:outputText value="Reason"/></f:facet>
    <t:outputText value="#{thisNotif.reason}"/>
  </t:column>
  <t:column>
    <f:facet name="header"><h:outputText value="NotifType"/></f:facet>
    <t:outputText value="#{thisNotif.notifTypeId}"/>
  </t:column>
  <t:column>
    <f:facet name="header"><h:outputText value="Time"/></f:facet>
    <t:outputText value="#{thisNotif.notificationTime}">
       <f:convertDateTime 
      timeStyle="medium" 
      dateStyle="medium"
      timeZone="#{sCommercialCurtailment.timeZone}"
      type="both" />
    </t:outputText>
  </t:column>
  <t:column>
    <f:facet name="header"><h:outputText value="State"/></f:facet>
    <t:outputText value="#{thisNotif.state}"/>
  </t:column>
</ctif:autoUpdateDataTable>
</cti:titledContainer>
</div>

</h:form>

 
</cti:standardPage>
</f:view>

