<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<f:view>
<cti:standardPage title="Program List" module="commercialcurtailment">
<cti:standardMenu />

<h2>Events for <t:outputText value="#{sEventList.program.name}"/></h2>

<h:form>
<t:dataTable id="eventList" value="#{sEventList.eventListModel}" var="thisEvent">
  <t:column>
    <h:commandLink action="#{sEventList.showDetail}">
      <h:outputText value="#{thisEvent.displayName}"/>
    </h:commandLink>
  </t:column>
</t:dataTable>


</h:form>

</cti:standardPage>
</f:view>

