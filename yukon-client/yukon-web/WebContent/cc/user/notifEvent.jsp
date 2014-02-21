<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<f:view>
<cti:standardPage title="Event Overview" module="commercialcurtailment_user">
<cti:standardMenu />

<div> <t:messages showDetail="true" showSummary="false" /> </div>

<h:form>
<f:verbatim><cti:csrfToken/></f:verbatim>
<table class="horizBorders">
  <tr>
    <td>Event Number</td>
    <td>
      <t:outputText 
         value="#{sNotificationDetail.event.displayName}"/>
    </td>
  </tr>
  <tr>
    <td>Start Date</td>
    <td>
      <t:outputText 
         value="#{sNotificationDetail.event.notificationTime}">
         <f:convertDateTime 
            dateStyle="long"
            timeZone="#{sCommercialCurtailment.timeZone}"
            type="date" />
      </t:outputText>
      (all times <t:outputText value="#{sCommercialCurtailment.timeZone.ID}"/>)
    </td>
  </tr>
  <tr>
    <td>Notification Time</td>
    <td>
      <t:outputText 
         value="#{sNotificationDetail.event.notificationTime}">
         <f:convertDateTime 
            timeStyle="short" 
            timeZone="#{sCommercialCurtailment.timeZone}"
            type="time" />
      </t:outputText>
    </td>
  </tr>
  <tr>
    <td>Start Time</td>
    <td>
      <t:outputText 
         value="#{sNotificationDetail.event.startTime}">
         <f:convertDateTime 
            timeStyle="short" 
            timeZone="#{sCommercialCurtailment.timeZone}"
            type="time" />
      </t:outputText>
    </td>
  </tr>
  <tr>
    <td>Stop Time</td>
    <td>
      <t:outputText 
         value="#{sNotificationDetail.event.stopTime}">
         <f:convertDateTime 
            timeStyle="short" 
            timeZone="#{sCommercialCurtailment.timeZone}"
            type="time" />
      </t:outputText>
    </td>
  </tr>
  <tr>
    <td>Message</td>
    <td>
      <t:outputText 
         value="#{sNotificationDetail.event.message}"/>
    </td>
  </tr>
</table>

</h:form>

</cti:standardPage>
</f:view>


