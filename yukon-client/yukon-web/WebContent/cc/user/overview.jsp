<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<f:view>
<cti:standardPage title="Event Overview" module="commercialcurtailment_user">
<cti:standardMenu />


<h:form>
<h3>Current Events</h3>
<t:dataTable value="#{sCustomerEventBean.currentEventList}" 
             var="thisEvent">
  <t:column>
    <h:outputText value="#{thisEvent.program.name}"/>
  </t:column>
  <t:column>
    <h:outputText value="#{thisEvent.program.programType.name}"/>
  </t:column>
  <t:column>
    <h:commandLink action="#{sCustomerEventBean.showCurrentEventDetail}">
      <t:updateActionListener property="#{sCustomerEventBean.selectedEvent}" value="#{thisEvent}"/>
      <h:outputText value="#{thisEvent.displayName}"/>
    </h:commandLink>
  </t:column>
  <t:column>
    <h:outputText value="#{thisEvent.startTime}">
      <f:convertDateTime 
            timeStyle="short" 
            dateStyle="short"
            timeZone="#{sCommercialCurtailment.timeZone}"
            type="date" />
    </h:outputText>
  </t:column>
  <t:column>
    <h:outputText value="#{thisEvent.startTime}">
      <f:convertDateTime 
            timeStyle="short" 
            dateStyle="short"
            timeZone="#{sCommercialCurtailment.timeZone}"
            type="time" />
    </h:outputText>
  </t:column>
  <t:column>
    <h:outputText value="#{thisEvent.stopTime}">
      <f:convertDateTime 
            timeStyle="short" 
            dateStyle="short"
            timeZone="#{sCommercialCurtailment.timeZone}"
            type="time" />
    </h:outputText>
  </t:column>
</t:dataTable>
<t:outputText rendered="#{empty sCustomerEventBean.currentEventList}" value="(none)"/>

<h3>Pending Events</h3>
<t:dataTable value="#{sCustomerEventBean.pendingEventList}" 
             var="thisEvent">
  <t:column>
    <h:outputText value="#{thisEvent.program.programType.name}"/>
  </t:column>
  <t:column>
    <h:outputText value="#{thisEvent.program.name}"/>
  </t:column>
  <t:column>
    <h:commandLink action="#{sCustomerEventBean.showEventDetail}">
      <t:updateActionListener property="#{sCustomerEventBean.selectedEvent}" value="#{thisEvent}"/>
      <h:outputText value="#{thisEvent.displayName}"/>
    </h:commandLink>
  </t:column>
  <t:column>
    <h:outputText value="#{thisEvent.startTime}">
      <f:convertDateTime 
            timeStyle="short" 
            dateStyle="short"
            timeZone="#{sCommercialCurtailment.timeZone}"
            type="date" />
    </h:outputText>
  </t:column>
  <t:column>
    <h:outputText value="#{thisEvent.startTime}">
      <f:convertDateTime 
            timeStyle="short" 
            dateStyle="short"
            timeZone="#{sCommercialCurtailment.timeZone}"
            type="time" />
    </h:outputText>
  </t:column>
  <t:column>
    <h:outputText value="#{thisEvent.stopTime}">
      <f:convertDateTime 
            timeStyle="short" 
            dateStyle="short"
            timeZone="#{sCommercialCurtailment.timeZone}"
            type="time" />
    </h:outputText>
  </t:column>
</t:dataTable>
<t:outputText rendered="#{empty sCustomerEventBean.pendingEventList}" value="(none)"/>

<h3>Recent Events</h3>
<t:dataTable value="#{sCustomerEventBean.recentEventList}" 
             var="thisEvent">
  <t:column>
    <h:outputText value="#{thisEvent.program.programType.name}"/>
  </t:column>
  <t:column>
    <h:outputText value="#{thisEvent.program.name}"/>
  </t:column>
  <t:column>
    <h:commandLink action="#{sCustomerEventBean.showRecentEventDetail}">
      <t:updateActionListener property="#{sCustomerEventBean.selectedEvent}" value="#{thisEvent}"/>
      <h:outputText value="#{thisEvent.displayName}"/>
    </h:commandLink>
  </t:column>
  <t:column>
    <h:outputText value="#{thisEvent.startTime}">
      <f:convertDateTime 
            timeStyle="short" 
            dateStyle="short"
            timeZone="#{sCommercialCurtailment.timeZone}"
            type="date" />
    </h:outputText>
  </t:column>
  <t:column>
    <h:outputText value="#{thisEvent.startTime}">
      <f:convertDateTime 
            timeStyle="short" 
            dateStyle="short"
            timeZone="#{sCommercialCurtailment.timeZone}"
            type="time" />
    </h:outputText>
  </t:column>
  <t:column>
    <h:outputText value="#{thisEvent.stopTime}">
      <f:convertDateTime 
            timeStyle="short" 
            dateStyle="short"
            timeZone="#{sCommercialCurtailment.timeZone}"
            type="time" />
    </h:outputText>
  </t:column>
</t:dataTable>
<t:outputText rendered="#{empty sCustomerEventBean.recentEventList}" value="(none)"/>

</h:form>

</cti:standardPage>
</f:view>

