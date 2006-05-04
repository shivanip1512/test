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
  <t:column styleClass="programName">
  <h:outputText value="#{thisProgram.name}"/>
  </t:column>
  <t:column><h:outputText value="#{thisProgram.programType.name}"/></t:column>
  <t:column>
    <h:commandLink action="#{sProgramDetail.editEvent}">
      <f:param name="programId" value="#{thisProgram.id}"/>
      <h:outputText value="Edit"/>
    </h:commandLink>
  </t:column>
  <t:column>
    <h:commandLink action="#{rEventInit.initEvent}">
      <f:param name="programId" value="#{thisProgram.id}"/>
      <h:outputText value="Start" />
    </h:commandLink>
  </t:column>
  <t:column>
    <h:commandLink action="#{sEventList.showProgram}">
      <f:param name="programId" value="#{thisProgram.id}"/>
      <h:outputText value="History"/>
    </h:commandLink>
  </t:column>
</t:dataTable>

</h:form>

</cti:standardPage>
</f:view>

