<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<f:view>
<cti:standardPage title="Program Detail" module="commercialcurtailment">
<cti:standardMenu menuSelection="ccurt_setup"/>
<h2>Program Detail</h2>
<h:form>
<f:verbatim><cti:csrfToken/></f:verbatim>
<div><h:messages /></div>

<div class="section">
<table class="horizBorders">
<tr>
  <td><t:outputLabel value="Program Type" styleClass="propertyEntry"/></td>
  <td><t:outputText value="#{sProgramDetail.program.programType.name}"/></td>
</tr>

<tr>
  <td><t:outputLabel value="Program Name" for="programName" styleClass="propertyEntry"/></td>
  <td><t:inputText id="programName" value="#{sProgramDetail.program.name}" >
    <f:validateLength minimum="2" />
    </t:inputText></td>
</tr>
<tr>
  <td><t:outputLabel value="Identifier Prefix" for="identifierPrefix" styleClass="propertyEntry"/></td>
  <td><t:inputText id="identifierPrefix" value="#{sProgramDetail.program.identifierPrefix}" >
    <f:validateLength minimum="1" />
    </t:inputText></td>
</tr>
<tr>
  <td><t:outputLabel value="Last Identifier" for="lastIdentifier" styleClass="propertyEntry"/></td>
  <td><t:inputText id="lastIdentifier" value="#{sProgramDetail.program.lastIdentifier}" >
    <f:validateLongRange minimum="0" />
    </t:inputText></td>
</tr>
</table>
</div>

<div class="section">
<div class="sectionTitle">Parameters:</div>
<t:dataTable value="#{sProgramDetail.programParameters}" 
             var="thisParameter" 
             forceIdIndexFormula="#{thisParameter.id}"
             styleClass="horizBorders">
  <t:column><t:outputLabel for="parameterInput" 
                           styleClass="propertyEntry"
                           value="#{thisParameter.parameterKey.description}"/></t:column>
  <t:column><t:inputText id="parameterInput" value="#{thisParameter.parameterValue}"/></t:column>
</t:dataTable>
</div>

<div>
<div class="section" style="float: left;">
<div class="sectionTitle">Assigned Groups:</div>
<div class="sectionBody">
<t:dataTable value="#{sProgramDetail.assignedGroupModel}" 
             var="group" 
             styleClass="horizBorders"
             forceIdIndexFormula="#{group.id}">
  <t:column width="20">
    <t:commandLink actionListener="#{sProgramDetail.deleteGroup}">
      <h:graphicImage value="/WebConfig/yukon/Icons/delete.png" />
    </t:commandLink>
  </t:column>
  <t:column><t:outputText value="#{group.name}"/></t:column>
</t:dataTable>
</div>
</div>
<div class="section" style="float: left">
<div class="sectionTitle">Available Groups:</div>
<div class="sectionBody">
<t:dataTable value="#{sProgramDetail.unassignedGroupModel}" 
             var="group" 
             styleClass="horizBorders"
             forceIdIndexFormula="#{group.id}">
  <t:column width="20">
    <t:commandLink actionListener="#{sProgramDetail.addGroup}">
      <h:graphicImage value="/WebConfig/yukon/Icons/add.png" />
    </t:commandLink>
  </t:column>
  <t:column><t:outputText value="#{group.name}"/></t:column>
</t:dataTable>
</div>
</div>
<div style="clear: both;">&nbsp;</div>
</div>

<div>
<div class="section" style="float: left;">
<div class="sectionTitle">Assigned Notification Groups:</div>
<div class="sectionBody">
<t:dataTable value="#{sProgramDetail.assignedNotificationGroups}" 
             var="notifGroup" 
             styleClass="horizBorders">
  <t:column width="20">
    <t:commandLink actionListener="#{sProgramDetail.deleteNotificationGroup}">
      <t:updateActionListener property="#{sProgramDetail.selectedNotificationGroup}" value="#{notifGroup}"/>
      <h:graphicImage value="/WebConfig/yukon/Icons/delete.png" />
    </t:commandLink>
  </t:column>
  <t:column><t:outputText value="#{notifGroup.notificationGroupName}"/></t:column>
</t:dataTable>
</div>
</div>
<div class="section" style="float: left">
<div class="sectionTitle">Available Notification Groups:</div>
<div class="sectionBody">
<t:dataTable value="#{sProgramDetail.unassignedNotificationGroups}" 
             var="notifGroup" 
             styleClass="horizBorders">
  <t:column width="20">
    <t:commandLink actionListener="#{sProgramDetail.addNotificationGroup}">
      <t:updateActionListener property="#{sProgramDetail.selectedNotificationGroup}" value="#{notifGroup}"/>
      <h:graphicImage value="/WebConfig/yukon/Icons/add.png" />
    </t:commandLink>
  </t:column>
  <t:column><t:outputText value="#{notifGroup.notificationGroupName}"/></t:column>
</t:dataTable>
</div>
</div>
<div style="clear: both;">&nbsp;</div>
</div>

<div>
<h:commandButton action="#{sProgramDetail.save}" value="Save"/>
<h:commandButton action="#{sProgramDetail.delete}" value="Delete" 
  rendered="#{sProgramDetail.programDeletable}" />
<h:commandButton action="#{sProgramDetail.cancel}" value="Cancel"/>
</div>

</h:form>

 
</cti:standardPage>
</f:view>

