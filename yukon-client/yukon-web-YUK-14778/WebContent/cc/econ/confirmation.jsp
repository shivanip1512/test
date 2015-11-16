<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<f:view>
<cti:standardPage title="Create Economic Event" module="commercialcurtailment">
<cti:standardMenu />

<h2>Create <t:outputText value="#{sEconomicCreate.program.programType.name} #{sEconomicCreate.program.name}"/> Event</h2>
<h3>Confirm</h3>
<div class="jsfMessages"> 
<t:messages showSummary="false" showDetail="true" 
            errorClass="jsfError" 
            warnClass="jsfWarn" 
            infoClass="jsfInfo" 
            fatalClass="jsfFatal"/> 
</div>

<h:form>
<f:verbatim><cti:csrfToken/></f:verbatim>
<div class="actionButtons">
<h:commandButton action="#{sEconomicCreate.doCreateEvent}" value="Create"/>
<h:commandButton action="#{sEconomicCreate.cancel}" value="Cancel" immediate="true"/>
</div>

<table class="horizBorders">
  <tr>
    <td>Notification Time</td>
    <td>
      <t:outputText 
         value="#{sEconomicCreate.builder.event.notificationTime}">
         <f:convertDateTime 
            pattern="#{sCommercialCurtailment.dateTimeFormat}"
            timeZone="#{sCommercialCurtailment.timeZone}" />
      </t:outputText>
      (<t:outputText value="#{sCommercialCurtailment.timeZone.ID}"/>)
    </td>
  </tr>
  <tr>
    <td>Start Time</td>
    <td>
      <t:outputText 
         value="#{sEconomicCreate.builder.event.startTime}">
         <f:convertDateTime 
            pattern="#{sCommercialCurtailment.dateTimeFormat}"
            timeZone="#{sCommercialCurtailment.timeZone}" />
      </t:outputText>
      (<t:outputText value="#{sCommercialCurtailment.timeZone.ID}"/>)
    </td>
  </tr>

  <tr>
    <td>Duration</td>
    <td><t:outputText value="#{sEconomicCreate.eventHours}"/></td>
  </tr>
</table>
<div>
<h3>Prices</h3>
<t:dataTable value="#{sEconomicCreate.builder.prices}" 
             var="thisPrice" 
             forceIdIndexFormula="#{thisPrice.offset}"
             styleClass="light_table">
  <t:column>
    <f:facet name="header">
      <t:outputText value="Start Time (#{sCommercialCurtailment.timeZone.ID})"/>
    </f:facet>
    <t:outputText value="#{thisPrice.startTime}">
       <f:convertDateTime 
            pattern="#{sCommercialCurtailment.timeFormat}"
            timeZone="#{sCommercialCurtailment.timeZone}" />
    </t:outputText>
  </t:column>
  <t:column>
    <f:facet name="header">
      <t:outputText value="Energy Price ($/kWh)"/>
      <br>
    </f:facet>
    <t:outputText value="#{thisPrice.energyPrice}"/>
  </t:column>
</t:dataTable>
</div>


<div>
<h3>Customers</h3>
<t:dataList value="#{sEconomicCreate.builder.participantList}" var="thisParticipant" layout="unorderedList">
  <t:outputText value="#{thisParticipant.customer.companyName}"/>
</t:dataList>
</div>


</h:form>

 
</cti:standardPage>
</f:view>

