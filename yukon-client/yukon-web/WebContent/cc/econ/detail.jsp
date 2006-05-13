<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

<f:view>
<cti:standardPage title="Economic Event Detail" module="commercialcurtailment">
<cti:standardMenu />

<h2><t:outputText value="#{sEconomicCreate.program.programType.name} #{sEconomicCreate.program.name}"/> Event</h2>
<div> <t:messages showDetail="false" showSummary="true"/> </div>

<h:form>

<table class="horizBorders">
  <tr>
    <td>Start Date</td>
    <td>
      <t:outputText 
         value="#{sEconomicDetail.event.notificationTime}">
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
         value="#{sEconomicDetail.event.notificationTime}">
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
         value="#{sEconomicDetail.event.startTime}">
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
         value="#{sEconomicDetail.event.stopTime}">
         <f:convertDateTime 
            timeStyle="short" 
            timeZone="#{sCommercialCurtailment.timeZone}"
            type="time" />
      </t:outputText>
    </td>
  </tr>
  <tr>
    <td>Initial Event</td>
    <td><t:commandLink 
         value="#{sEconomicDetail.event.initialEvent.displayName}" 
         action="#{sEconomicDetail.showInitialEvent}"
         rendered="#{sEconomicDetail.event.eventExtension}"/>
        <t:outputText
         value="none" 
         rendered="#{!sEconomicDetail.event.eventExtension}"/>
    </td>
  </tr>
  <tr>
    <td>Most Recent Revision</td>
    <td>Revision 
      <t:outputText 
         value="#{sEconomicDetail.event.latestRevision.revision}"/>
    </td>
  </tr>
  <tr>
    <td>Operations</td>
    <td>
    <div class="eventControlButtons">
    <t:commandButton action="#{sEconomicDetail.deleteEvent}" value="Delete" rendered="#{sEconomicDetail.showDeleteButton}"/>
    <t:commandButton action="#{sEconomicDetail.cancelEvent}" value="Cancel" rendered="#{sEconomicDetail.showCancelButton}"/>
    <t:commandButton action="#{sEconomicDetail.reviseEvent}" value="Revise" rendered="#{sEconomicDetail.showReviseButton}"/>
    <t:commandButton action="#{sEconomicDetail.extendEvent}" value="Extend" rendered="#{sEconomicDetail.showExtendButton}"/>
    </div>
    </td>
  </tr>
</table>

<div>
<h3>Revision
  <t:outputText 
         value="#{sEconomicDetail.currentRevision.revision}"/>
</h3>
<t:dataTable 
     value="#{sEconomicDetail.participantModel}" 
     var="thisParticipant" 
     styleClass="econDetailTable">
  <t:column>
    <f:facet name="header"><h:outputText value="Customer"/></f:facet>
    <t:outputText value="#{thisParticipant.customer.companyName}"/>
  </t:column>
  <t:column>
    <f:facet name="header"><h:outputText value="ACK"/></f:facet>
    <t:outputText value="#{sEconomicDetail.customerAckForRow}"/>
  </t:column>
  <t:columns value="#{sEconomicDetail.windowModel}" var="window">
    <f:facet name="header">
      <h:panelGroup>
        <h:outputText value="#{window.startTime}">
           <f:convertDateTime 
              timeStyle="short" 
              timeZone="#{sCommercialCurtailment.timeZone}"
              type="time" />
        </h:outputText>
        <f:verbatim><br></f:verbatim>
        <h:outputText value="$#{sEconomicDetail.columnPrice} / kWH"/>
      </h:panelGroup>
    </f:facet>
    <t:outputText value="#{sEconomicDetail.columnValue}"/>  
  </t:columns>
</t:dataTable>
</div>

<t:dataList value="#{sEconomicDetail.otherRevisionsModel}" 
            var="thisRevision">
   <t:commandLink action="#{sEconomicDetail.switchRevision}">
     <t:outputText value="Revision #{thisRevision.revision}" />
   </t:commandLink>
</t:dataList>

</h:form>

 
</cti:standardPage>
</f:view>

