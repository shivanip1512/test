<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<f:view>
<cti:standardPage title="Program List" module="commercialcurtailment">
<cti:standardMenu menuSelection="ccurt_setup|ccurt_programs"/>
<h2>Program Setup</h2>

<h:form>
<t:dataList id="programList" value="#{rProgramList.programTypeList}" var="thisType" layout="unorderedList">
  <h:outputText value="#{thisType.self.name}"/>
  <t:dataList value="#{thisType.children}" var="thisProgram" layout="unorderedList" styleClass="indent list_circle">
    <h:commandLink action="#{sProgramDetail.editProgram}">
      <f:param name="programId" value="#{thisProgram.id}"/>
      <h:outputText value="#{thisProgram.name}" />
    </h:commandLink>
  </t:dataList>
</t:dataList>


<t:commandButton action="#{sProgramDetail.createNewProgram}" value="Create New Program"/>

</h:form>

</cti:standardPage>
</f:view>

