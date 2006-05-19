<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

<f:view>
<cti:standardPage title="Program Detail" module="commercialcurtailment">
<cti:standardMenu />
<div class="jsfMessages"> 
<t:messages showSummary="false" showDetail="true" 
            errorClass="jsfError" 
            warnClass="jsfWarn" 
            infoClass="jsfInfo" 
            fatalClass="jsfFatal"/> 
</div>
<h:form>
<div>
Program Name: <t:inputText value="#{sProgramDetail.program.name}">
  <f:validateLength minimum="4" />
</t:inputText>
</div>
<div>Program Type: 
<t:selectOneMenu value="#{sProgramDetail.program.programType}" >
  <f:selectItems value="#{sProgramDetail.availableTypes}"/>
  <f:converter converterId="objectConverter"/>
</t:selectOneMenu></div>

<div>
<h:commandButton action="#{sProgramDetail.saveNew}" value="Create"/>
<h:commandButton action="#{sProgramDetail.cancel}" value="Cancel"/>
</div>

</h:form>

 
</cti:standardPage>
</f:view>

