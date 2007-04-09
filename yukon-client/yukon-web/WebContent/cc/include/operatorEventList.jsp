<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<f:subview id="operatorEventList">
<t:dataTable value="#{eventListModel}" 
             var="thisEvent"
             styleClass="light_table"
             renderedIfEmpty="false"
             forceIdIndexFormula="#{thisEvent.id}">
  <t:column>
    <f:facet name="header"><h:outputText value="Program Type"/></f:facet>
    <h:outputText value="#{thisEvent.program.programType.name}"/>
  </t:column>
  <t:column>
    <f:facet name="header"><h:outputText value="Program"/></f:facet>
    <h:outputText value="#{thisEvent.program.name}"/>
  </t:column>
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
     <f:facet name="header"><h:outputText value="Start Date"/></f:facet>
    <h:outputText value="#{thisEvent.startTime}">
           <f:convertDateTime 
              pattern="#{sCommercialCurtailment.dateFormat}" 
              timeZone="#{sCommercialCurtailment.timeZone}" />
    </h:outputText>
  </t:column>
  <t:column>
    <f:facet name="header"><h:outputText value="Start Time"/></f:facet>
    <h:outputText value="#{thisEvent.startTime}">
           <f:convertDateTime 
              pattern="#{sCommercialCurtailment.timeFormat}" 
              timeZone="#{sCommercialCurtailment.timeZone}" />
    </h:outputText>
  </t:column>
  <t:column>
    <f:facet name="header"><h:outputText value="Stop Time"/></f:facet>
    <h:outputText value="#{thisEvent.stopTime}">
           <f:convertDateTime 
              pattern="#{sCommercialCurtailment.timeFormat}" 
              timeZone="#{sCommercialCurtailment.timeZone}" />
    </h:outputText>
  </t:column>
</t:dataTable>

<t:outputText value="(none)" rendered="#{empty eventListModel}"/>
 
</f:subview>
