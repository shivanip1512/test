<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<f:view>
<cti:standardPage title="Group List" module="commercialcurtailment">
<cti:standardMenu />

<cti:includeScript link="/JavaScript/yukon.curtailment.js"/>

<h2>Remove Customer(s) from <t:outputText value="#{sNotificationDetail.event.program.programType.name} #{sNotificationDetail.event.program.name}"/> Event</h2>
<h3>Verify Customers</h3>
<div class="jsfMessages"> 
<t:messages showSummary="false" showDetail="true" 
            errorClass="jsfError" 
            warnClass="jsfWarn" 
            infoClass="jsfInfo" 
            fatalClass="jsfFatal"/> 
</div>
<h:form>
<f:verbatim><cti:csrfToken/></f:verbatim>
<div id="customerTableDiv"> 
<t:dataTable value="#{sNotificationDetail.removeCustomerListModel}" var="thisCustomer" cellspacing="5">
  <t:column>
    <f:facet name="header">
      <t:outputText value="Customer"/>
    </f:facet>
    <t:selectBooleanCheckbox onclick="yukon.curtailment.doCalcSelectedLoad();" value="#{thisCustomer.selected}" disabled="#{!thisCustomer.allowOverride}"/>
    <t:outputText value="#{thisCustomer.customer.companyName}"/>
    <f:facet name="footer">
      <t:outputText value="Load Reduction Selected:"/>
    </f:facet>
  </t:column>
  <t:column>
    <f:facet name="header">
      <t:outputText value="Current Load"/>
    </f:facet>
    <t:outputText escape="false" value="#{sNotificationDetail.loadPointUpdaterStr}" styleClass="curLoad">
    </t:outputText>
  </t:column>
  <t:column>
    <f:facet name="header">
      <t:outputText value="CFD"/>
    </f:facet>
    <t:outputText escape="false" value="#{sNotificationDetail.contractFirmDemandUpdaterStr}" styleClass="fsl">
    </t:outputText>
  </t:column>
  <t:column>
    <f:facet name="header">
      <t:outputText value="Load Reduction"/>
    </f:facet>
    <t:outputText value="---" styleClass="loadReduct">
    </t:outputText>
    <f:facet name="footer">
      <t:outputText value="---" styleClass="loadReductFoot"/>
    </f:facet>
  </t:column>
  <t:column>
    <f:facet name="header">
      <t:outputText value="Constraint Status"/>
    </f:facet>
    <t:outputText escape="false" value="#{sNotificationDetail.constraintStatus}">
    </t:outputText>
  </t:column>
  <t:column>
    <t:outputText value="#{thisCustomer.reasonForExclusion}" 
                  rendered="#{thisCustomer.exclude}" 
                  styleClass="exclusionReason"/>
  </t:column>
</t:dataTable>
</div>
<div class="actionButtons">
<t:commandButton action="#{sNotificationDetail.doCustomerRemoveComplete}" value="Remove"/>
<h:commandButton action="#{sNotificationDetail.cancelRemove}" value="Cancel" immediate="true"/>
</div>
</h:form>
  <cti:dataUpdaterCallback
    function="yukon.curtailment.doCalcSelectedLoad"
    initialize="true"/>
</cti:standardPage>
</f:view>