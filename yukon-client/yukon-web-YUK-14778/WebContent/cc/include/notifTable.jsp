<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<f:subview id="notifTableSubView">

<t:dataTable 
     value="#{notifTableData}" 
     var="thisNotif" 
     styleClass="light_table">
  <t:column>
    <f:facet name="header"><h:outputText value="Company"/></f:facet>
    <t:outputText value="#{thisNotif.participant.customer.companyName}"/>
  </t:column>
  <t:column>
    <f:facet name="header"><h:outputText value="Reason"/></f:facet>
    <t:outputText value="#{thisNotif.reason}"/>
  </t:column>
  <t:column>
    <f:facet name="header"><h:outputText value="NotifType"/></f:facet>
    <t:outputText value="#{thisNotif.notifType}"/>
  </t:column>
  <t:column>
    <f:facet name="header"><h:outputText value="Time"/></f:facet>
    <t:outputText value="#{thisNotif.notificationTime}">
      <f:convertDateTime 
            pattern="#{sCommercialCurtailment.notificationTimeFormat}" 
            timeZone="#{sCommercialCurtailment.timeZone}" />
    </t:outputText>
  </t:column>
  <t:column>
    <f:facet name="header"><h:outputText value="State"/></f:facet>
    <t:outputText value="#{thisNotif.state}"/>
  </t:column>
</t:dataTable>


 
</f:subview>
