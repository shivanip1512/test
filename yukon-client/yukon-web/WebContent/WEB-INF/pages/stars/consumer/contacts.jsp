<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<cti:standardPage module="consumer" page="contacts">
<cti:standardMenu/>

   <h3><cti:msg key="yukon.dr.consumer.contacts.header" /></h3>
   
   <c:if test="${promptForEmail}">
        <tags:boxContainer2 nameKey="promptForEmail" hideEnabled="false" >
            <cti:msg2 key="yukon.web.modules.consumer.general.promptForEmail"/>
            <cti:msg2 key="yukon.dr.consumer.contacts.promptForEmail.instructions"/>
        </tags:boxContainer2>
        <br>
        <br>
    </c:if>
    
   <c:if test="${!empty param.failed}">
       <div class="errorRed">
          <cti:msg key="yukon.dr.consumer.contacts.invalidNotification" arguments="${param.notificationText},${param.notifCategory}"/>
       </div>
   </c:if>
    <!-- Display primary contact and notifications -->
    <dr:contact contact="${primaryContact}" titleKey="yukon.dr.consumer.contacts.primaryContact" options="${notificationOptionList}" primary="true" />
  
    <!-- Display additional contacts and notifications -->
    <c:forEach var="contact" items="${additionalContacts}">
        <dr:contact contact="${contact}" titleKey="yukon.dr.consumer.contacts.contact" options="${notificationOptionList}"  primary="false" />
    </c:forEach>
    
    <form action="/spring/stars/consumer/contacts/newContact" method="post">
        <input type="submit" value="<cti:msg key='yukon.dr.consumer.contacts.createNewContact'/>" />
    </form>

</cti:standardPage>