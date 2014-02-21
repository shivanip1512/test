<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<f:view>
<cti:standardPage title="Program List" module="commercialcurtailment">
<cti:standardMenu />

<h2>Events for <t:outputText value="#{sEventList.program.programType.name}"/> / <t:outputText value="#{sEventList.program.name}"/></h2>

<h:form>
<f:verbatim><cti:csrfToken/></f:verbatim>
<t:dataTable id="eventList" 
     value="#{sEventList.eventList}" 
     var="thisEvent"
     styleClass="light_table"
     renderedIfEmpty="false">
  <t:column>
    <f:facet name="header"><h:outputText value="Event #"/></f:facet>
    <h:commandLink action="#{rEventOverview.showDetail}">
      <t:updateActionListener property="#{rEventOverview.selectedEvent}" value="#{thisEvent}"/>
      <h:outputText value="#{thisEvent.displayName}"/>
    </h:commandLink>
  </t:column>
  <t:column>
    <f:facet name="header"><h:outputText value="State"/></f:facet>
    <h:outputText value="#{thisEvent.stateDescription}"/>
  </t:column>
  <t:column>
    <f:facet name="header"><h:outputText value="Start"/></f:facet>
    <h:outputText value="#{thisEvent.startTime}">
      <f:convertDateTime 
              pattern="#{sCommercialCurtailment.dateFormat}" 
              timeZone="#{sCommercialCurtailment.timeZone}" />
    </h:outputText>
    <f:verbatim> </f:verbatim>
    <h:outputText value="#{thisEvent.startTime}">
      <f:convertDateTime 
              pattern="#{sCommercialCurtailment.timeFormat}" 
              timeZone="#{sCommercialCurtailment.timeZone}" />
    </h:outputText>
  </t:column>
  <t:column>
    <f:facet name="header"><h:outputText value="Duration (Minutes)"/></f:facet>
    <h:outputText value="#{thisEvent.duration}"/>
  </t:column>
  <t:column>
    <f:facet name="header"><h:outputText value="Action"/></f:facet>
    <h:commandButton action="#{rEventOverview.deleteEvent}" 
        onclick="return confirm('Are you sure you want to permanently delete this event?')"
        value="Delete"
        rendered="#{sCommercialCurtailment.adminUser}" >
      <t:updateActionListener property="#{rEventOverview.selectedEvent}" value="#{thisEvent}"/>
    </h:commandButton>
  </t:column>
</t:dataTable>


</h:form>

</cti:standardPage>
</f:view>

