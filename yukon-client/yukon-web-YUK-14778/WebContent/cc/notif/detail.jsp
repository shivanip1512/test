<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://cannontech.com/tags/faces/ctif" prefix="ctif" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<f:view>
<cti:standardPage title="Notification Event Detail" module="commercialcurtailment">
<cti:standardMenu />

<h2><t:outputText value="#{sNotificationDetail.event.program.programType.name} #{sNotificationDetail.event.program.name}"/> Event</h2>
<div> <t:messages showDetail="false" showSummary="true"/> </div>

<h:form>
<f:verbatim><cti:csrfToken/></f:verbatim>
<table class="horizBorders">
  <tr>
    <td>Event Number</td>
    <td><t:outputText 
       value="#{sNotificationDetail.event.displayName}"/>
    </td>
  </tr>
  <tr>
    <td>Notification Time</td>
    <td><t:outputText 
       value="#{sNotificationDetail.event.notificationTime}">
       <f:convertDateTime 
          pattern="#{sCommercialCurtailment.dateTimeFormat}"
          timeZone="#{sCommercialCurtailment.timeZone}"/>
    </t:outputText>
    (<t:outputText value="#{sCommercialCurtailment.timeZone.ID}"/>)
    </td>
  </tr>
  <tr>
    <td>Start Time</td>
    <td><t:outputText 
       value="#{sNotificationDetail.event.startTime}">
       <f:convertDateTime 
          pattern="#{sCommercialCurtailment.dateTimeFormat}"
          timeZone="#{sCommercialCurtailment.timeZone}"/>
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
      <t:commandButton action="#{sNotificationDetail.prepareAdjustEvent}" value="Adjust" rendered="#{sNotificationDetail.showAdjustButton}"/>
      <t:commandButton action="#{sNotificationDetail.refresh}" value="Refresh"/>
      <t:commandButton action="#{sNotificationDetail.prepareSplitEvent}" value="Remove" rendered="#{sNotificationDetail.showRemoveButton}"/>
    </td>
  </tr>
</table>

<h3>Customer Notification Status</h3>
<div id="detailInner"> 
  <t:aliasBean alias="#{notifTableData}" value="#{sNotificationDetailHelper.notifList}">
    <jsp:include page="../include/notifTable.jsp"/>
  </t:aliasBean>
</div>



</h:form>

 
</cti:standardPage>
</f:view>

