<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://cannontech.com/tags/faces/ctif" prefix="ctif" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<f:view>
<cti:standardPage title="Accounting Event Detail" module="commercialcurtailment">
<cti:standardMenu />

<h2><t:outputText value="#{sAccountingDetail.event.program.programType.name} #{sAccountingDetail.event.program.name}"/> Event</h2>
<div> <t:messages showDetail="false" showSummary="true"/> </div>

<h:form>
<f:verbatim><cti:csrfToken/></f:verbatim>
<table class="horizBorders">
  <tr>
    <td>Event Number</td>
    <td><t:outputText 
       value="#{sAccountingDetail.event.displayName}"/>
    </td>
  </tr>
  <tr>
    <td>Start Time</td>
    <td><t:outputText 
       value="#{sAccountingDetail.event.startTime}">
       <f:convertDateTime 
          pattern="#{sCommercialCurtailment.dateTimeFormat}"
          timeZone="#{sCommercialCurtailment.timeZone}"/>
    </t:outputText>
    (<t:outputText value="#{sCommercialCurtailment.timeZone.ID}"/>)
    </td>
  </tr>
  <tr>
    <td>Duration</td>
    <td><t:outputText value="#{sAccountingDetail.event.duration}"/></td>
  </tr>
  <tr>
    <td>Reason</td>
    <td><t:outputText value="#{sAccountingDetail.event.reason}"/></td>
  </tr>
  <tr>
    <td>Operations</td>
    <td>
      <t:commandButton action="#{sAccountingDetail.deleteEvent}" value="Delete" rendered="#{sAccountingDetail.showDeleteButton}"/>
      <t:commandButton action="#{sAccountingDetail.refresh}" value="Refresh"/>
    </td>
  </tr>
</table>

<h3>Affected Customers</h3>
<div id="detailInner"> 
  <t:dataTable 
     value="#{sAccountingDetail.participants}" 
     var="thisNotif" 
     styleClass="light_table">
    <t:column>
      <f:facet name="header"><h:outputText value="Company"/></f:facet>
      <t:outputText value="#{thisNotif.customer.companyName}"/>
    </t:column>
  </t:dataTable>
</div>

</h:form>

 
</cti:standardPage>
</f:view>

