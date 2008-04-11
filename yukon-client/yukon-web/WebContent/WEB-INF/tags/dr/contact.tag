<%@ attribute name="titleKey" required="true" type="java.lang.String"%>
<%@ attribute name="contact" required="true" type="com.cannontech.database.data.lite.LiteContact"%>
<%@ attribute name="options" required="true" type="java.util.List"%>
<%@ attribute name="primary" required="true" type="java.lang.Boolean"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>

<form action="/spring/stars/consumer/contacts/updateContact" method="post">
    <input type="hidden" name="contactId" value="${contact.liteID}" />
    
    <table class="contactTable">
        <tr>
            <th colspan="2">
                <cti:msg key="${titleKey}" />
            </th>
        </tr>
        <tr>
            <td class="contactLeft">
                <div>
                    <cti:msg key="yukon.dr.consumer.contacts.firstName" />
                    <input type="text" name="firstName" value="${contact.contFirstName}" /><br>
                </div>
                <div>
                    <cti:msg key="yukon.dr.consumer.contacts.lastName" />
                    <input type="text" name="lastName" value="${contact.contLastName}" />
                </div>
            </td>
            <td class="contactRight">
                <dr:contactNotifications options="${options}" contact="${contact}" />
            </td>
        </tr>
        <tr>
            <td colspan="2" style="text-align: center; padding-top: 2em;">
                
                <c:if test="${not primary}">
                    <cti:msg var="removeText" key="yukon.dr.consumer.contacts.removeContact" />
                    <input type="submit" name="remove" value="${removeText}" />
                </c:if>

                <cti:msg var="updateText" key="yukon.dr.consumer.contacts.updateContact" />
                <input type="submit" name="update" value="${updateText}" />
                
                <cti:msg var="addText" key="yukon.dr.consumer.contacts.addNotification" />
                <input type="submit" name="addNotification" value="${addText}" />
            </td>
        </tr>
    </table>
</form>
