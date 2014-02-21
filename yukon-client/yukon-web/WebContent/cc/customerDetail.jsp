<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<f:view>
<cti:standardPage title="Customer List" module="commercialcurtailment">
<cti:standardMenu menuSelection="ccurt_setup"/>

<h2>Customer Detail: <h:outputText value="#{sCustomerDetail.customer.companyName}"/></h2>
<h:form>
<f:verbatim><cti:csrfToken/></f:verbatim>
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
  <t:column>
    <h:outputText value="#{thisType.unit}"
                  rendered="#{sCustomerDetail.rowPointExists}"/>
  </t:column>
</t:dataTable>

<h3>Assigned Programs</h3>
<div>
<div class="section" style="float: left;">
<div class="sectionTitle">Active Load Control Programs:</div>
<div class="sectionBody">
<t:dataTable value="#{sCustomerDetail.assignedProgramList}" 
             var="thisProgram" 
             styleClass="horizBorders">
  <t:column width="20">
    <t:commandLink actionListener="#{sCustomerDetail.deleteProgram}">
      <t:updateActionListener property="#{sCustomerDetail.selectedProgram}" value="#{thisProgram}"/>
      <h:graphicImage value="/WebConfig/yukon/Icons/delete.png"/>
    </t:commandLink>
  </t:column>
  <t:column><t:outputText value="#{thisProgram.paoName}"/></t:column>
</t:dataTable>
</div>
</div>
<div class="section" style="float: left">
<div class="sectionTitle">Available Load Control Programs:</div>
<div class="sectionBody">
<t:dataTable value="#{sCustomerDetail.unassignedProgramList}" 
             var="thisProgram" 
             styleClass="horizBorders">
  <t:column width="20">
    <t:commandLink actionListener="#{sCustomerDetail.addProgram}">
      <t:updateActionListener property="#{sCustomerDetail.selectedProgram}" value="#{thisProgram}"/>
      <h:graphicImage value="/WebConfig/yukon/Icons/add.png"/>
    </t:commandLink>
  </t:column>
  <t:column><t:outputText value="#{thisProgram.paoName}"/></t:column>
</t:dataTable>
</div>
</div>
<div style="clear: both;">&nbsp;</div>
</div>

<div>
<h:commandButton action="#{sCustomerDetail.save}" value="Save"/>
<h:commandButton action="#{sCustomerDetail.cancel}" value="Cancel"/>
</div>

</h:form>

</cti:standardPage>
</f:view>

