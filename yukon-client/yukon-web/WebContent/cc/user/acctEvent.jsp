<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<f:view>
<cti:standardPage title="Event Overview" module="commercialcurtailment_user">
<cti:standardMenu />

<div> <t:messages showDetail="true" showSummary="false" /> </div>

<h:form>

<table class="horizBorders">
  <tr>
    <td>Event Number</td>
    <td>
      <t:outputText 
         value="#{sAccountingDetail.event.displayName}"/>
    </td>
  </tr>
  <tr>
    <td>Start Time</td>
    <td><t:outputText 
       value="#{sAccountingDetail.event.startTime}">
       <f:convertDateTime 
          pattern="#{sCommercialCurtailment.dateTimeFormat}"
          timeZone="#{sCommercialCurtailment.timeZone}"/>
    </t:outputText>
    (<t:outputText value="#{sCommercialCurtailment.timeZone.ID}"/>)
    </td>
  </tr>
  <tr>
    <td>Duration</td>
    <td><t:outputText value="#{sAccountingDetail.event.duration}"/></td>
  </tr>
  <tr>
    <td>Reason</td>
    <td><t:outputText value="#{sAccountingDetail.event.reason}"/></td>
  </tr>
  <tr>
</table>

</h:form>

</cti:standardPage>
</f:view>