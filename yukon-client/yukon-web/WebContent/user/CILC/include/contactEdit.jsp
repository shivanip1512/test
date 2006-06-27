<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

<f:subview id="contactEdit">

<t:panelGrid columns="2" styleClass="contactEditor">
  <t:panelGrid columns="2">
    <t:outputLabel value="First Name:" title="ContactId: #{thisContact.contact.contactID}"/>
    <t:inputText value="#{thisContact.contact.contFirstName}"/>
    
    <t:outputLabel value="Last Name:"/>
    <t:inputText value="#{thisContact.contact.contLastName}"/>

    <t:outputLabel value="Username:" title="UserId: #{sCustomerProfile.userLookup[thisContact].yukonUser.userID}"/>
    <t:inputText value="#{sCustomerProfile.userLookup[thisContact].yukonUser.username}"/>

    <t:outputLabel value="New Password:"/>
    <t:inputSecret value="#{sCustomerProfile.userLookup[thisContact].yukonUser.password}"/>
  </t:panelGrid>
  <t:panelGroup>
  <t:dataTable value="#{thisContact.contactNotifVect}" var="thisNotif">
    <t:column>
      <t:selectOneMenu  value="#{thisNotif.notificationCatID}">
        <f:selectItems value="#{sCustomerProfile.notificationSelections}"/>
      </t:selectOneMenu>
    </t:column>
    <t:column>
      <t:inputText value="#{thisNotif.notification}" size="45"/>
    </t:column>
  </t:dataTable> 
  <t:commandButton value="Add Notification" action="#{sCustomerProfile.addNotification}"> 
    <t:updateActionListener value="#{thisContact}" property="#{sCustomerProfile.selectedContact}"/>
  </t:commandButton>
  </t:panelGroup>
</t:panelGrid>


 
</f:subview>
