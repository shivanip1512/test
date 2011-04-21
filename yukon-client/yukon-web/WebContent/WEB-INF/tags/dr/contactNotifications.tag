<%@ attribute name="options" required="true" type="java.util.List"%>
<%@ attribute name="contact" required="true" type="com.cannontech.database.data.lite.LiteContact"%>

<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<div>
    <c:choose>
        <c:when test="${fn:length(contact.liteContactNotifications) > 0}">
            <c:set var="count" value="0" />
            <c:forEach var="notification" items="${contact.liteContactNotifications}">
                <c:set var="count" value="${count + 1}" />
                
                <div>
                    <select name="notificationType_${count}">
                        <c:forEach var="option" items="${options}">
                            <option value="${option.notificationId}" ${(option.notificationId == notification.notificationCategoryID)? "selected" : ""}>
                                <cti:msg key="${option.optionText}" />
                            </option>
                        </c:forEach>
                    </select>
                    <cti:formatNotification var="notif" value="${notification}"/>
                    <input name="notificationText_${count}" type="text" value="<spring:escapeBody htmlEscape="true">${notif}</spring:escapeBody>"/>
                    <input name="notificationId_${count}" type="hidden" value="${notification.liteID}"/>
                    
                    <cti:msg var="removeTitle" key="yukon.dr.consumer.contacts.removeNotificationTitle" />
                    
                    <div class="dib vab">
                        <input type="image" title="${removeTitle}" name="removeNotification_${count}" class="icon icon_remove fr"/>
                    </div>
                </div>
            </c:forEach>
        </c:when>
        <c:otherwise>
                <div>
                    <cti:msg key="yukon.dr.consumer.contacts.noNotifications" />
                </div>        
        </c:otherwise>
    </c:choose>
</div>
