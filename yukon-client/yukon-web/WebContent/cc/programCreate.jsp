<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

<f:view>
<cti:standardPage title="Program Detail" module="commercialcurtailment">
<cti:standardMenu />
<div> Messages: <h:messages /></div>
<h:form>
<div>
Program Name: <t:inputText value="#{rProgramDetail.program.name}">
  <f:validateLength minimum="4" />
</t:inputText>
</div>
<div>Program Type: 
<t:selectOneMenu value="#{rProgramDetail.program.programType}" >
  <f:selectItems value="#{rProgramDetail.availableTypes}"/>
  <f:converter converterId="objectConverter"/>
</t:selectOneMenu></div>

<div>
<h:commandButton action="#{rProgramDetail.save}" value="Create"/>
<h:commandButton action="#{rProgramDetail.cancel}" value="Cancel"/>
</div>

</h:form>

 
</cti:standardPage>
</f:view>

