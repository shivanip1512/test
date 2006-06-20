<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

<f:subview id="operatorEventList">
<t:dataTable value="#{eventListModel}" 
             var="thisEvent"
             forceIdIndexFormula="#{thisEvent.id}">
  <t:column>
    <h:outputText value="#{thisEvent.program.programType.name}"/>
  </t:column>
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
              pattern="#{sCommercialCurtailment.dateFormat}" 
              timeZone="#{sCommercialCurtailment.timeZone}" />
    </h:outputText>
  </t:column>
  <t:column>
    <h:outputText value="#{thisEvent.startTime}">
           <f:convertDateTime 
              pattern="#{sCommercialCurtailment.timeFormat}" 
              timeZone="#{sCommercialCurtailment.timeZone}" />
    </h:outputText>
  </t:column>
  <t:column>
    <h:outputText value="#{thisEvent.stopTime}">
           <f:convertDateTime 
              pattern="#{sCommercialCurtailment.timeFormat}" 
              timeZone="#{sCommercialCurtailment.timeZone}" />
    </h:outputText>
  </t:column>
</t:dataTable>

 
</f:subview>
