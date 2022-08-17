<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="consumer" page="contacts">

    <cti:standardMenu/>

    <cti:flashScopeMessages/>
    <tags:setFormEditMode mode="${mode}"/>

    <h3><i:inline key="yukon.dr.consumer.contacts.header"/></h3>
    
    <c:if test="${promptForEmail and primaryContact}">
        <tags:boxContainer2 nameKey="promptForEmail" hideEnabled="false">
            <i:inline key="yukon.web.modules.consumer.general.promptForEmail"/>
            <i:inline key=".promptForEmail.editPrimaryInstructions"/>
        </tags:boxContainer2>
        <br>
    </c:if>
    
    <div id="notification_template" class="dn">
        <div class="notification">
            <input type="hidden" name="liteContactNotifications[0].contactNotifID" value="${notificationTemplate.contactNotifID}"/>
            <input type="hidden" name="liteContactNotifications[0].contactID" value="${notificationTemplate.contactID}"/>
            <input type="hidden" name="liteContactNotifications[0].disableFlag" value="${notificationTemplate.disableFlag}"/>
            <input type="hidden" name="liteContactNotifications[0].order" value="${notificationTemplate.order}"/>
            <select class="fl" name="liteContactNotifications[0].notificationCategoryID">
                <c:forEach var="option" items="${notificationOptionList}">
                    <option value="${option.key}" <c:if test="${option.key eq notificationTemplate.notificationCategoryID}">selected</c:if>>${option.value}</option>
                </c:forEach>
            </select>
            <div class="fl">
                <input type="text" name="liteContactNotifications[0].notification" maxlength="120"/>
            </div>
            <span class="js-removeNotification"><i class="icon icon-cross"></i></span>
        </div>
    </div>
    
    <script>
    $(function(){
        $(document).on('click', '.js-removeNotification', function(event){
            $(this).closest(".notification").remove();
            updateIndicies();
        });
        
        $(document).on('click', '.js-addNotification', function(event){
            $("#notifications").append($("#notification_template > div").clone());
            updateIndicies();
        });
    });
    
    updateIndicies = function(){
        $("#notifications > div").each(function (idx, elem) {
            $(":input", elem).each(function (inputIndex, input) {
                var name;
                input = $(input);
                
                if (input.is('[name]')) {
                    name = input.attr('name');
                    input.attr('name', name.replace(/\[(\d+|\?)\]/, '[' + idx + ']'));
                }
            });
        });
    }
    </script>
    
    <div id="contactForm">
        <cti:url var="actionUrl" value="${actionUrl}"/>
        <form:form modelAttribute="contact" action="${actionUrl}" >
            <cti:csrfToken/>
            <tags:sectionContainer2 nameKey="${mode}">
                <form:hidden path="contactID"/>
                <form:hidden path="loginID"/>
                <form:hidden path="addressID"/>
                <tags:nameValueContainer2>
                    <tags:inputNameValue nameKey=".firstName" path="contFirstName" maxlength="120"/>
                    <tags:inputNameValue nameKey=".lastName" path="contLastName" maxlength="120"/>
                    <tags:nameValue2 nameKey=".notifications">
                        <div id="notifications">
                            <c:forEach var="notification" items="${contact.liteContactNotifications}" varStatus="row">
                                <div class="notification">
                                    <form:hidden path="liteContactNotifications[${row.index}].contactNotifID"/>
                                    <form:hidden path="liteContactNotifications[${row.index}].contactID"/>
                                    <form:hidden path="liteContactNotifications[${row.index}].disableFlag"/>
                                    <form:hidden path="liteContactNotifications[${row.index}].order"/>
                                    <form:select path="liteContactNotifications[${row.index}].notificationCategoryID" cssClass="fl">
                                        <form:options items="${notificationOptionList}"/>
                                    </form:select>
                                    <div class="fl">
                                        <p>
                                            <form:input path="liteContactNotifications[${row.index}].notification" maxlength="120" cssClass="fl" cssErrorClass="error fl"/>
                                            <cti:button classes="js-removeNotification" icon="icon-cross" renderMode="buttonImage"/>
                                        </p>
                                        <p>
                                            <form:errors path="liteContactNotifications[${row.index}].notification" cssClass="error fl"/>
                                        </p>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                        
                        <cti:button nameKey="addNotification" classes="js-addNotification" icon="icon-add"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
        
            <cti:displayForPageEditModes modes="EDIT,CREATE">
                   <cti:button nameKey="save" type="submit"/>
                   <cti:url value="/stars/consumer/contacts" var="cancelUrl"/>
                   <cti:button nameKey="cancel" href="${cancelUrl}"/>
               </cti:displayForPageEditModes>
        </form:form>
    </div>
</cti:standardPage>