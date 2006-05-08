<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

<f:view>
<cti:standardPage title="Create Economic Event" module="commercialcurtailment">
<cti:standardMenu />

<h2>Create <t:outputText value="#{sEconomicCreate.program.name}"/> Event</h2>
<h3>Confirm</h3>
<div> <t:messages showDetail="false" showSummary="true"/> </div>

<h:form>

<table class="horizBorders">
  <tr>
    <td>Notification Time</td>
    <td>
      <t:outputText 
         value="#{sEconomicCreate.builder.event.notificationTime}">
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
    <td>
      <t:outputText 
         value="#{sEconomicCreate.builder.event.startTime}">
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
      timeStyle="short" 
      timeZone="#{sCommercialCurtailment.timeZone}"
      type="time" />
    </t:outputText>
  </t:column>
  <t:column>
    <f:facet name="header">
      <t:outputText value="Energy Price ($/MWh)"/>
      <br>
    </f:facet>
    <t:outputText value="#{thisPrice.energyPrice}"/>
  </t:column>
</t:dataTable>
</div>


<div>
<h3>Customers</h3>
<t:dataList value="#{sEconomicCreate.builder.customerList}" var="thisCustomer" layout="unorderedList">
  <t:outputText value="#{thisCustomer.customer.companyName}"/>
</t:dataList>
</div>

<div class="actionButtons">
<h:commandButton action="#{sEconomicCreate.doCreateEvent}" value="Create"/>
<h:commandButton action="#{sEconomicCreate.cancel}" value="Cancel" immediate="true"/>
</div>

</h:form>

 
</cti:standardPage>
</f:view>

