<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

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
<f:verbatim><cti:csrfToken/></f:verbatim>
<div class="section">
<table class="horizBorders">
<tr>
  <td><t:outputLabel value="Program Type" styleClass="propertyEntry"/></td>
  <td><t:selectOneMenu value="#{sProgramDetail.program.programType}" >
  <f:selectItems value="#{sProgramDetail.availableTypes}"/>
  <f:converter converterId="objectConverter"/>
</t:selectOneMenu>
  </td>
</tr>
<tr>
  <td><t:outputLabel value="Program Name" styleClass="propertyEntry"/></td>
  <td><t:inputText value="#{sProgramDetail.program.name}">
        <f:validateLength minimum="2" />
      </t:inputText>
  </td>
</tr>
</table>
</div>


<div>
<h:commandButton action="#{sProgramDetail.saveNew}" value="Create" disabled="#{empty sProgramDetail.availableTypes}"/>
<h:commandButton action="#{sProgramDetail.cancel}" value="Cancel"/>
</div>

</h:form>

 
</cti:standardPage>
</f:view>

