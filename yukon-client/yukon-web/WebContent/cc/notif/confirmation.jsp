<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

<f:view>
<cti:standardPage title="Create Notification Event" module="commercialcurtailment">
<cti:standardMenu />

<h2>Create <t:outputText value="#{sNotificationCreate.program.name}"/> Event</h2>
<h3>Confirm</h3>
<div> <t:messages showDetail="false" showSummary="true"/> </div>

<h:form>

<div>
Notification Time:
<t:outputText 
   value="#{sNotificationCreate.builder.notificationTime}">
   <f:convertDateTime 
      timeStyle="short" 
      dateStyle="short"
      timeZone="#{sCommercialCurtailment.timeZone}"
      type="both" />
</t:outputText>
(<t:outputText value="#{sCommercialCurtailment.timeZone.ID}"/>)
</div>

<div>
Start Time:
<t:outputText 
   value="#{sNotificationCreate.builder.startTime}">
   <f:convertDateTime 
      timeStyle="short" 
      dateStyle="short"
      timeZone="#{sCommercialCurtailment.timeZone}"
      type="both" />
</t:outputText>
(<t:outputText value="#{sCommercialCurtailment.timeZone.ID}"/>)
</div>

<div>
Duration:
<t:outputText value="#{sNotificationCreate.builder.eventDuration}"/>
</div>

<div>
Message:
<t:outputText value="#{sNotificationCreate.builder.message}"/>
</div>

<div>
Customers:
<t:dataList value="#{sNotificationCreate.builder.customerList}" var="thisCustomer" layout="unorderedlist">
<t:outputText value="#{thisCustomer.customer.companyName}"/>
</t:dataList>

<div>
<h:commandButton action="#{sNotificationCreate.doCreateEvent}" value="Create"/>
<h:commandButton action="#{sNotificationCreate.cancel}" value="Cancel"/>
</div>

</h:form>

 
</cti:standardPage>
</f:view>

