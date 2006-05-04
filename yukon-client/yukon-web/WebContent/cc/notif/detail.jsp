<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://cannontech.com/tags/faces/ctif" prefix="ctif" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

<f:view>
<cti:standardPage title="Notification Event Detail" module="commercialcurtailment">
<cti:standardMenu />

<h2><t:outputText value="#{rNotificationDetail.event.program.name}"/> Event</h2>
<div> <t:messages showDetail="false" showSummary="true"/> </div>

<h:form>
<t:inputHidden id="eventId" forceId="true" value="#{rNotificationDetail.eventId}"/>

<div>
Notification Time:
<t:outputText 
   value="#{rNotificationDetail.event.notificationTime}">
   <f:convertDateTime 
      timeStyle="short" 
      dateStyle="short"
      timeZone="#{sCommercialCurtailment.timeZone}"
      type="both" />
</t:outputText>
(<t:outputText value="#{sCommercialCurtailment.timeZone.ID}"/>)
</div>

<div>
Start Time:
<t:outputText 
   value="#{rNotificationDetail.event.startTime}">
   <f:convertDateTime 
      timeStyle="short" 
      dateStyle="short"
      timeZone="#{sCommercialCurtailment.timeZone}"
      type="both" />
</t:outputText>
(<t:outputText value="#{sCommercialCurtailment.timeZone.ID}"/>)
</div>

<div>
Duration:
<t:outputText value="#{rNotificationDetail.event.duration}"/>
</div>

<div>
Modification State:
<t:outputText value="#{rNotificationDetail.event.state}"/>
</div>

<div>
Message:
<t:outputText value="#{rNotificationDetail.event.message}"/>
</div>

<div>
<t:commandButton action="#{rNotificationDetail.deleteEvent}" value="Delete" rendered="#{rNotificationDetail.showDeleteButton}"/>
<t:commandButton action="#{rNotificationDetail.cancelEvent}" value="Cancel" rendered="#{rNotificationDetail.showCancelButton}"/>
<t:commandButton action="#{rNotificationDetail.adjustEvent}" value="Adjust" rendered="#{rNotificationDetail.showAdjustButton}"/>
</div>

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
      timeZone="#{rNotificationDetail.timeZone}"
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

