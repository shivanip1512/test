<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<f:view>
<cti:standardPage title="Group List" module="commercialcurtailment">
<cti:standardMenu />

<h2>Create <t:outputText value="#{sCustomerSelectionBean.eventBean.program.programType.name} #{sCustomerSelectionBean.eventBean.program.name}"/> Event</h2>
<h3>Select Groups</h3>
<div class="jsfMessages"> 
<t:messages showSummary="false" showDetail="true" 
            errorClass="jsfError" 
            warnClass="jsfWarn" 
            infoClass="jsfInfo" 
            fatalClass="jsfFatal"/> 
</div>
<h:form>
<f:verbatim><cti:csrfToken/></f:verbatim>
<t:selectManyCheckbox 
   id="groupList" 
   value="#{sCustomerSelectionBean.selectedGroupList}"
   layout="pageDirection">
  <f:selectItems value="#{sCustomerSelectionBean.availableGroupList}"/>
  <f:converter converterId="objectConverter"/>
</t:selectManyCheckbox>

<div class="actionButtons">
<t:commandButton action="#{sCustomerSelectionBean.doGroupSelectionComplete}" value="Next"/>
<h:commandButton action="#{sCustomerSelectionBean.cancel}" value="Cancel" immediate="true"/>
</div>
</h:form>

</cti:standardPage>
</f:view>

