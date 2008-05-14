<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>

<cti:standardPage module="consumer" page="contacts">
<cti:standardMenu/>

    <h3><cti:msg key="yukon.dr.consumer.contacts.header" /></h3>
    
    <!-- Display primary contact and notifications -->
    <dr:contact contact="${primaryContact}" titleKey="yukon.dr.consumer.contacts.primaryContact" options="${notificationOptionList}" primary="true" />
  
    <!-- Display additional contacts and notifications -->
    <c:forEach var="contact" items="${additionalContacts}">
        <dr:contact contact="${contact}" titleKey="yukon.dr.consumer.contacts.contact" options="${notificationOptionList}"  primary="false" />
    </c:forEach>
    
    <form action="/spring/stars/consumer/contacts/newContact" method="post">
        <input type="submit" value="Create New Contact" />
    </form>

</cti:standardPage>