<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<f:view>
<cti:standardPage title="Customer Profile" module="commercialcurtailment_user">
<cti:includeCss link="include/customerProfile.css"/>
<cti:standardMenu />

<h2>Customer Profile</h2>
<h:form>
<f:verbatim><cti:csrfToken/></f:verbatim>
<t:commandButton value="Save" action="#{sCustomerProfile.save}"/>

<h3>Primary Contact</h3>
<t:aliasBean alias="#{thisContact}" value="#{sCustomerProfile.primaryContact}">
  <jsp:include page="include/contactEdit.jsp"/>
</t:aliasBean>


<h3>Additional Contacts</h3>
<t:dataList value="#{sCustomerProfile.additionalContactList}"
  var="additionalContact"
  layout="simple">

<t:aliasBean alias="#{thisContact}" value="#{additionalContact}">
  <jsp:include page="include/contactEdit.jsp"/>
</t:aliasBean>

</t:dataList>

<t:panelGrid columns="1" styleClass="contactEditor">
  <t:panelGrid columns="2">
    <t:outputLabel value="First Name:"/>
    <t:inputText value="#{sCustomerProfile.newContact.contact.contFirstName}"/>
    
    <t:outputLabel value="Last Name:"/>
    <t:inputText value="#{sCustomerProfile.newContact.contact.contLastName}"/>

  </t:panelGrid>
    <t:commandButton value="Add Contact" action="#{sCustomerProfile.addContact}"/>
</t:panelGrid>
</h:form>

</cti:standardPage>
</f:view>

