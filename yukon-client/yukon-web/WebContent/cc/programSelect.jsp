<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<f:view>
<cti:standardPage title="Select Program to Start" module="commercialcurtailment">
<cti:standardMenu />

<h2>Available Programs</h2>

<h:form>
<t:dataTable id="programList" value="#{rProgramList.programList}" var="thisProgram" 
   styleClass="horizBorders programSelectionTable">
  <t:column styleClass="programName"><h:outputText value="#{thisProgram.programType.name}"/></t:column>
  <t:column>
  <h:outputText value="#{thisProgram.name}"/>
  </t:column>
  <t:column>
    <h:commandLink action="#{rEventInit.initEvent}">
      <f:param name="programId" value="#{thisProgram.id}"/>
      <h:outputText value="Start" />
    </h:commandLink>
  </t:column>
  <t:column>
    <h:commandLink action="#{sEventList.showProgram}">
      <t:updateActionListener property="#{sEventList.program}" value="#{thisProgram}"/>
      <h:outputText value="History"/>
    </h:commandLink>
  </t:column>
</t:dataTable>


<h3>Current Events</h3>
<t:aliasBean alias="#{eventListModel}" value="#{rEventOverview.currentEventListModel}">
  <jsp:include page="include/operatorEventList.jsp"/>
</t:aliasBean>

<h3>Pending Events</h3>
<t:aliasBean alias="#{eventListModel}" value="#{rEventOverview.pendingEventListModel}">
  <jsp:include page="include/operatorEventList.jsp"/>
</t:aliasBean>

<h3>Recent Events</h3>
<t:aliasBean alias="#{eventListModel}" value="#{rEventOverview.recentEventListModel}">
  <jsp:include page="include/operatorEventList.jsp"/>
</t:aliasBean>

</h:form>

</cti:standardPage>
</f:view>

