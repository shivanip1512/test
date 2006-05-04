<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<f:view>
<cti:standardPage title="Customer List" module="commercialcurtailment">
<cti:standardMenu />

<h2>Customer Detail: <h:outputText value="#{sCustomerDetail.customer.companyName}"/></h2>
<h:form>
<div><h:messages /></div>

<h3>Customer Data</h3>
<table class="horizBorders">
<tr>
<td>Satisfied Point Groups</td>
<td><t:outputText value="#{sCustomerDetail.satisfiedPointGroups}"/></td>
</tr>
</table>

<h3>Attached Points</h3>
<t:dataTable 
 value="#{sCustomerDetail.pointsModel}"
 var="thisType"
 forceIdIndexFormula="#{thisType}"
 styleClass="horizBorders">
  <t:column><h:outputText value="#{thisType}"/></t:column>
  <t:column>
    <h:inputText 
     value="#{sCustomerDetail.pointValueCache[thisType]}" 
     rendered="#{sCustomerDetail.rowPointExists}"/>
    <h:commandButton 
     action="#{sCustomerDetail.createPoint}" 
     value="Create Point" 
     rendered="#{!sCustomerDetail.rowPointExists}"/>
  </t:column>
</t:dataTable>

<div>
<h:commandButton action="#{sCustomerDetail.apply}" value="Apply"/>
<h:commandButton action="#{sCustomerDetail.save}" value="Save"/>
</div>

</h:form>

</cti:standardPage>
</f:view>

