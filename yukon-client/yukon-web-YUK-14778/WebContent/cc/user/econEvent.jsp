<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<f:view>
<cti:standardPage title="Event Overview" module="commercialcurtailment_user">
<cti:standardMenu />

<div class="jsfMessages"> 
<t:messages showSummary="false" showDetail="true" 
            errorClass="jsfError" 
            warnClass="jsfWarn" 
            infoClass="jsfInfo" 
            fatalClass="jsfFatal"/> 
</div>

<h:form>
<f:verbatim><cti:csrfToken/></f:verbatim>
<t:outputText rendered="#{sEconomicUserDetail.buyThroughExpired}" styleClass="jsfInfo" value="* The Buy Through election period has expired"/>
<table class="horizBorders">
  <tr>
    <td>Event Number</td>
    <td>
      <t:outputText 
         value="#{sEconomicUserDetail.event.displayName}"/>
    </td>
  </tr>
  <tr>
    <td>Start Date</td>
    <td>
      <t:outputText 
         value="#{sEconomicUserDetail.event.notificationTime}">
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
         value="#{sEconomicUserDetail.event.notificationTime}">
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
         value="#{sEconomicUserDetail.event.startTime}">
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
         value="#{sEconomicUserDetail.event.stopTime}">
         <f:convertDateTime 
            timeStyle="short" 
            timeZone="#{sCommercialCurtailment.timeZone}"
            type="time" />
      </t:outputText>
    </td>
  </tr>
  <tr>
    <td>Most Recent Revision</td>
    <td>Revision 
      <t:outputText 
         value="#{sEconomicUserDetail.event.latestRevision.revision}"/>
    </td>
  </tr>
</table>

<t:dataTable value="#{sEconomicUserDetail.windowModel}"
             var="thisWindow"
             styleClass="econDetailTable">
  <t:column>
    <f:facet name="header">
      <t:outputText value="Start Time"/>
    </f:facet>
    <t:outputText value="#{thisWindow.startTime}">
      <f:convertDateTime 
        timeStyle="short" 
        timeZone="#{sCommercialCurtailment.timeZone}"
        type="time" />
    </t:outputText>
  </t:column>
  <t:column>
    <f:facet name="header">
      <t:outputText value="Current Price ($/kW)"/>
    </f:facet>
    <t:outputText value="#{sEconomicUserDetail.currentRowPrice}" />
  </t:column>
  <t:column>
    <f:facet name="header">
      <t:outputText value="Energy Buy Through (kW)"/>
    </f:facet>
    <t:inputText value="#{sEconomicUserDetail.currentRowBuyThrough}" 
                 displayValueOnly="#{!sEconomicUserDetail.currentRowPriceEditable}"
                 required="true">
      <f:validateLongRange minimum="0" maximum="100000"/>
    </t:inputText>
  </t:column>
</t:dataTable>

<t:commandButton value="Save Buy Through" rendered="#{sEconomicUserDetail.showSaveButton}" action="#{sEconomicUserDetail.savePrices}" />


</h:form>

</cti:standardPage>
</f:view>


