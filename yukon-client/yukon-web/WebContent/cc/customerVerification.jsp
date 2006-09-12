<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<f:view>
<cti:standardPage title="Group List" module="commercialcurtailment">
<cti:standardMenu />

<h2>Create <t:outputText value="#{sCustomerSelectionBean.eventBean.program.programType.name} #{sCustomerSelectionBean.eventBean.program.name}"/> Event</h2>
<h3>Verify Customers</h3>
<div class="jsfMessages"> 
<t:messages showSummary="false" showDetail="true" 
            errorClass="jsfError" 
            warnClass="jsfWarn" 
            infoClass="jsfInfo" 
            fatalClass="jsfFatal"/> 
</div>
<h:form>
<t:dataTable value="#{sCustomerSelectionBean.customerListModel}" var="thisCustomer">
  <t:column>
    <f:facet name="header">
      <t:outputText value="Customer"/>
    </f:facet>
    <t:selectBooleanCheckbox value="#{thisCustomer.selected}" disabled="#{!thisCustomer.allowOverride}"/>
    <t:outputText value="#{thisCustomer.customer.companyName}"/>
  </t:column>
  <t:column>
    <f:facet name="header">
      <t:outputText value="Current Load"/>
    </f:facet>
    <t:outputText value="#{sCustomerSelectionBean.load}">
      <f:convertNumber 
        groupingUsed="true" maxFractionDigits="0"/>
    </t:outputText>
  </t:column>
  <t:column>
    <t:outputText value="#{thisCustomer.reasonForExclusion}" 
                  rendered="#{thisCustomer.exclude}" 
                  styleClass="exclusionReason"/>
  </t:column>
</t:dataTable>

<div class="actionButtons">
<t:commandButton action="#{sCustomerSelectionBean.doCustomerVerificationComplete}" value="Next"/>
<h:commandButton action="#{sCustomerSelectionBean.cancel}" value="Cancel" immediate="true"/>
</div>
</h:form>

</cti:standardPage>
</f:view>

