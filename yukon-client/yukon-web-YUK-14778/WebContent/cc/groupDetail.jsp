<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<f:view>
<cti:standardPage title="Program Detail" module="commercialcurtailment">
<cti:standardMenu menuSelection="ccurt_setup"/>
<h2>Group Detail</h2>
<h:form>
<f:verbatim><cti:csrfToken/></f:verbatim>
<div><h:messages /></div>

<div class="section">
<table class="horizBorders">
  <tr>
    <td>Group Name</td>
    <td><t:inputText value="#{sGroupDetail.group.name}">
          <f:validateLength minimum="4" />
        </t:inputText>
    </td>
  </tr>
</table>
</div>

<div>
<div class="section" style="float: left;">
<div class="sectionTitle">Customers:</div>
<div class="sectionBody">
<t:dataTable value="#{sGroupDetail.customerNotifsModel}" 
             var="customerNotif" 
             forceIdIndexFormula="#{customerNotif.id}"
             styleClass="horizBorders">
  <t:column>
    <t:outputText value="#{customerNotif.customer.companyName}"/>
  </t:column>
  <t:column>
    <t:outputLabel> 
      <t:selectBooleanCheckbox value="#{customerNotif.notifMap.sendEmails}"/>
      <t:outputText value="Email"/>
    </t:outputLabel>
    <t:outputLabel>
      <t:selectBooleanCheckbox value="#{customerNotif.notifMap.sendOutboundCalls}"/>
      <t:outputText value="Voice"/>
    </t:outputLabel>
    <t:outputLabel>
      <t:selectBooleanCheckbox value="#{customerNotif.notifMap.sendSms}"/>
      <t:outputText value="SMS"/>
    </t:outputLabel>
  </t:column>
  <t:column width="25">
    <t:commandLink actionListener="#{sGroupDetail.deleteNotif}">
      <h:graphicImage value="/WebConfig/yukon/Icons/delete.png"/>
    </t:commandLink>
  </t:column>
</t:dataTable>
</div>
</div>

<div class="section" style="float: left;">
<div class="sectionTitle">Available Customers:</div>
<div class="sectionBody">
<t:dataTable value="#{sGroupDetail.customerModel}" 
             var="customerNotif" 
             forceIdIndexFormula="#{customerNotif.id}"
             styleClass="horizBorders">
  <t:column>
    <t:outputText value="#{customerNotif.customer.companyName}"/>
  </t:column>
  <t:column width="25">
    <t:commandLink actionListener="#{sGroupDetail.addNotif}">
      <h:graphicImage value="/WebConfig/yukon/Icons/add.png"/>
    </t:commandLink>
  </t:column>
</t:dataTable>
</div>
</div>
<div style="clear: both;">&nbsp;</div>
</div>

<div>
<h:commandButton action="#{sGroupDetail.save}" value="Save"/>
<h:commandButton action="#{sGroupDetail.delete}" value="Delete"/>
<h:commandButton action="#{sGroupDetail.cancel}" value="Cancel"/>
</div>

</h:form>

 
</cti:standardPage>
</f:view>

