<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<f:subview id="userEventList">
<t:dataTable value="#{eventList}" 
             var="thisEvent"             
             styleClass="light_table"
             renderedIfEmpty="false">
  <t:column>
    <h:outputText value="#{thisEvent.program.name}"/>
  </t:column>
  <t:column>
    <h:outputText value="#{thisEvent.program.programType.name}"/>
  </t:column>
  <t:column>
    <h:commandLink action="#{sCustomerEventBean.showEventDetail}">
      <t:updateActionListener property="#{sCustomerEventBean.selectedEvent}" value="#{thisEvent}"/>
      <h:outputText value="#{thisEvent.displayName}"/>
    </h:commandLink>
  </t:column>
  <t:column>
    <h:outputText value="#{thisEvent.stateDescription}"/>
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
<t:outputText rendered="#{empty eventList}" value="(none)"/>

 
</f:subview>
