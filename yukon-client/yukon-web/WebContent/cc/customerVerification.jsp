<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<f:view>
<cti:standardPage title="Group List" module="commercialcurtailment">
<cti:standardMenu />

<h2>Create <t:outputText value="#{sCustomerSelectionBean.eventBean.program.name}"/> Event</h2>
<h3>Verify Customers</h3>
<div> <t:messages showDetail="false" showSummary="true"/> </div>

<h:form>
<t:dataList value="#{sCustomerSelectionBean.customerList}" var="thisCustomer">
  <f:verbatim><div></f:verbatim>
  <t:selectBooleanCheckbox value="#{thisCustomer.selected}" disabled="#{!thisCustomer.allowOverride}"/>
  <t:outputText value="#{thisCustomer.customer.companyName}"/>
  <t:outputText value=" - " rendered="#{thisCustomer.exclude}"/>
  <t:outputText value="#{thisCustomer.reasonForExclusion}" rendered="#{thisCustomer.exclude}"/>
  <f:verbatim></div></f:verbatim>
</t:dataList>

<div class="actionButtons">
<t:commandButton action="#{sCustomerSelectionBean.doCustomerVerificationComplete}" value="Next"/>
<h:commandButton action="#{sCustomerSelectionBean.cancel}" value="Cancel"/>
</div>
</h:form>

</cti:standardPage>
</f:view>

