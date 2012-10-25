<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="consumer" page="contacts">
	<cti:standardMenu />

	<cti:flashScopeMessages />
	<tags:setFormEditMode mode="${mode}" />

	<h3>
		<cti:msg key="yukon.dr.consumer.contacts.header" />
	</h3>
	
	<c:if test="${promptForEmail and primaryContact}">
        <tags:boxContainer2 nameKey="promptForEmail" hideEnabled="false" >
            <cti:msg2 key="yukon.web.modules.consumer.general.promptForEmail"/>
            <cti:msg2 key=".promptForEmail.editPrimaryInstructions"/>
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
			<span class="icon icon_remove f_removeNotification" title="">::DELETE::</span>
		</div>
	</div>
	
	<script>
	jQuery(function(){
		jQuery(document).delegate(".f_removeNotification", "click", function(event){
			jQuery(this).closest(".notification").remove();
			updateIndicies();
		});
		
		jQuery(document).delegate(".f_addNotification", "click", function(event){
			jQuery("#notifications").append(jQuery("#notification_template > div").clone());
			updateIndicies();
		});
	});
	
	updateIndicies = function(){
		jQuery("#notifications > div").each(function(idx, elem){
			jQuery(":input", elem).each(function(inputIndex, input){
				jQuery(input).attr('name', jQuery(input).attr('name').gsub(/\d+/, idx));
			});
		});
	}
	</script>
	
	<div id="contactForm">
		<form:form commandName="contact" action="${actionUrl}" >
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
										<form:options items="${notificationOptionList}" />
									</form:select>
									<div class="fl">
										<p>
											<form:input path="liteContactNotifications[${row.index}].notification" maxlength="120" cssClass="fl" cssErrorClass="error fl" />
											<span class="icon icon_remove f_removeNotification" title="">::DELETE::</span>
										</p>
										<p>
											<form:errors path="liteContactNotifications[${row.index}].notification" cssClass="errorMessage fl" />
										</p>
									</div>
								</div>
							</c:forEach>
						</div>
						
						<cti:button nameKey="addNotification" styleClass="f_addNotification"/>
					</tags:nameValue2>
				</tags:nameValueContainer2>
			</tags:sectionContainer2>
		
			<cti:displayForPageEditModes modes="EDIT,CREATE">
		           <cti:button nameKey="save" type="submit"/>
		           <cti:url value="/spring/stars/consumer/contacts" var="cancelUrl" />
		           <a href="${cancelUrl}"><i:inline key="yukon.common.cancel"/></a>
		       </cti:displayForPageEditModes>
		</form:form>
	</div>
</cti:standardPage>