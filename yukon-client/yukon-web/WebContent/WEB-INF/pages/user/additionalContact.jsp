<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="user" page="profile.contactInfo">

    <cti:msgScope paths="yukon.web.modules.user,yukon.web.modules.operator.contact">

        <tags:setFormEditMode mode="${mode}"/>
        
        <div class="column-12-12">
        
            <div class="column one">
    
                <cti:url var="contactUpdateUrl" value="/user/contacts/save"/>
                <form:form id="contactsUpdateForm" modelAttribute="contactDto" action="${contactUpdateUrl}" method="post">
                    <cti:csrfToken/>
                    <input type="hidden" name="contactId" value="${contactDto.contactId}">
                    
                    <tags:sectionContainer2 nameKey="userInfo" styleClass="stacked">
                    
                        <tags:nameValueContainer2>
                        
                            <tags:inputNameValue nameKey=".firstNameLabel" path="firstName"/>
                            <tags:inputNameValue nameKey=".lastNameLabel" path="lastName"/>
                            <tags:inputNameValue nameKey="yukon.web.modules.operator.contactNotificationEnum.HOME_PHONE" path="homePhone" inputClass="js-format-phone"/>
                            <tags:inputNameValue nameKey="yukon.web.modules.operator.contactNotificationEnum.WORK_PHONE" path="workPhone" inputClass="js-format-phone"/>
                            <tags:inputNameValue nameKey="yukon.web.modules.operator.contactNotificationEnum.EMAIL" path="email"/>
                        
                        </tags:nameValueContainer2>
                        
                    </tags:sectionContainer2>
                    
                    <%-- NOTIFICATIONS TABLE --%>
                    <tags:sectionContainer2 nameKey="additionContactInfo">
                        
                        <%-- EXISTING NOTIFICATIONS --%>
                        <tags:nameValueContainer2 id="contact-notif-table">
                        
                            <cti:msg2 var="noneText" key="yukon.common.selector.selectOne"/>
                            <c:forEach var="notif" items="${contactDto.otherNotifications}" varStatus="notifRow">
                                <c:set var="newNotification" value="${notif.notificationId <= 0}"/>
                                <tr>
                                    <td class="name">
                                        <form:hidden path="otherNotifications[${notifRow.index}].notificationId"/>
                                        <c:choose>
                                            <c:when test="${newNotification}">
                                                <tags:selectWithItems items="${notificationTypes}" path="otherNotifications[${notifRow.index}].contactNotificationType" 
                                                    defaultItemLabel="${noneText}" defaultItemValue="" inputClass="js-notif-type"/>
                                            </c:when>
                                            <c:otherwise>
                                                <tags:selectWithItems items="${notificationTypes}" path="otherNotifications[${notifRow.index}].contactNotificationType" 
                                                    inputClass="js-notif-type"/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    
                                    <td class="value">
                                        <tags:input path="otherNotifications[${notifRow.index}].notificationValue" inputClass="fl js-notif-value"/>
                                    </td>
                                    <td>
                                        <cti:button nameKey="delete" icon="icon-cross" renderMode="buttonImage" classes="js-remove fn"/>
                                    </td>
                            </c:forEach>
                            
                        </tags:nameValueContainer2>
                    </tags:sectionContainer2>
                    
                    <div class="action-area">
                        <cti:button nameKey="add" icon="icon-add" id="add-notif-btn"/>
                    </div>
                    
                    <div class="page-action-area">
                        <cti:button nameKey="save" type="submit" classes="primary action"/>
                        <cti:url value="/user/profile" var="cancelUrl"/>
                        <cti:button nameKey="cancel" href="${cancelUrl}"/>
                    </div>
                </form:form>
                
                <table class="dn">
                    <tr id="contact-notif-template">
                        <td class="name">
                            <cti:msg2 key="yukon.common.selector.selectOne" var="txt_selectOne"/>
                            <tags:simpleSelect name="otherNotifications[?].contactNotificationType" 
                                items="${notificationTypes}" itemLabelKey="formatKey" 
                                defaultItemValue="-1" 
                                defaultItemLabel="${txt_selectOne}" 
                                cssClass="js-notif-type"/>
                        </td>
                        <td class="value">
                            <input type="text" name="otherNotifications[?].notificationValue" class="js-notif-value fl">
                        </td>
                        <td>
                            <cti:button renderMode="buttonImage" icon="icon-cross" classes="js-remove fn"/>
                        </td>
                    </tr>
                </table>
            
            </div>
        
        </div>
    
    </cti:msgScope>
    
    <cti:includeScript link="/resources/js/pages/yukon.user.profile.js"/>
    
</cti:standardPage>