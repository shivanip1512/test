<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<f:view>
<cti:standardPage title="Create Accounting Event" module="commercialcurtailment">
<cti:standardMenu />

<h2>Create <t:outputText value="#{sAccountingCreate.program.programType.name} #{sAccountingCreate.program.name}"/> Event</h2>
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
<h:commandButton action="#{sAccountingCreate.doCreateEvent}" value="Create"/>
<h:commandButton action="#{sAccountingCreate.cancel}" value="Cancel" immediate="true"/>
</div>

<table class="horizBorders">
  <tr>
    <td>Start Time</td>
    <td>
<t:outputText 
   value="#{sAccountingCreate.builder.startTime}">
   <f:convertDateTime 
      pattern="#{sCommercialCurtailment.dateTimeFormat}"
      timeZone="#{sCommercialCurtailment.timeZone}" />
</t:outputText>
(<t:outputText value="#{sCommercialCurtailment.timeZone.ID}"/>)
    </td>
  </tr>

  <tr>
    <td>Duration</td>
    <td>
<t:outputText value="#{sAccountingCreate.builder.eventDuration}"/>
    </td>
  </tr>

  <tr>
    <td>Reason</td>
    <td>
<t:outputText value="#{sAccountingCreate.builder.reason}"/>
    </td>
  </tr>
</table>

<div>
<h3>Customers</h3>
<t:dataList value="#{sAccountingCreate.builder.customerList}" var="thisCustomer" layout="unorderedList">
  <t:outputText value="#{thisCustomer.customer.companyName}"/>
</t:dataList>
</div>

</h:form>

 
</cti:standardPage>
</f:view>

