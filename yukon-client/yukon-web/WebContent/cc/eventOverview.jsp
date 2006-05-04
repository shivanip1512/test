<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<f:view>
<cti:standardPage title="Recent Events" module="commercialcurtailment">
<cti:standardMenu />


<h:form>
<h3>Current Events</h3>
<t:dataTable value="#{sEventOverview.currentEventListModel}" 
             var="thisEvent"
             forceIdIndexFormula="#{thisEvent.id}">
  <t:column>
    <h:outputText value="#{thisEvent.program.name}"/>
  </t:column>
  <t:column>
    <h:commandLink action="#{sEventOverview.showDetailCurrent}">
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

<h3>Pending Events</h3>
<t:dataTable value="#{sEventOverview.pendingEventListModel}" 
             var="thisEvent"
             forceIdIndexFormula="#{thisEvent.id}">
  <t:column>
    <h:outputText value="#{thisEvent.program.name}"/>
  </t:column>
  <t:column>
    <h:commandLink action="#{sEventOverview.showDetailPending}">
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

<h3>Recent Events</h3>
<t:dataTable value="#{sEventOverview.recentEventListModel}" 
             var="thisEvent"
             forceIdIndexFormula="#{thisEvent.id}">
  <t:column>
    <h:outputText value="#{thisEvent.program.name}"/>
  </t:column>
  <t:column>
    <h:commandLink action="#{sEventOverview.showDetailRecent}">
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


</h:form>

</cti:standardPage>
</f:view>

