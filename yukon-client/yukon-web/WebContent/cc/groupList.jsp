<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<f:view>
<cti:standardPage title="Group List" module="commercialcurtailment">
<cti:standardMenu menuSelection="ccurt_setup|ccurt_groups"/>
<h2>Group Setup</h2>

<h:form>
<f:verbatim><cti:csrfToken/></f:verbatim>
<t:dataList id="groupList" value="#{rGroupList.groupList}" var="thisGroup" layout="unorderedList">
    <h:commandLink action="#{sGroupDetail.editGroup}">
      <f:param name="groupId" value="#{thisGroup.id}"/>
      <h:outputText value="#{thisGroup.name}" />
    </h:commandLink>
</t:dataList>

<t:commandButton action="#{sGroupDetail.newGroup}" value="Create New Group"/>
</h:form>

</cti:standardPage>
</f:view>

