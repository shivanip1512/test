<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i"%>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>

<cti:standardPage module="operator" page="account.${mode}">

	<tags:setFormEditMode mode="${mode}"/>
	
	<cti:includeScript link="/JavaScript/yukonGeneral.js"/>

    <cti:url var="deleteUrl" value="/spring/stars/operator/account/deleteAccount">
        <cti:param name="accountId" value="${accountId}"/>
    </cti:url>
    
    <i:simplePopup titleKey=".confirmDeleteDialogTitle" id="confirmDeleteDialog" styleClass="mediumSimplePopup">
        <cti:msg2 key=".confirmDelete" arguments="${accountGeneral.accountDto.accountNumber}"/>
        <div class="actionArea">
            <cti:button nameKey="confirmDeleteOk" href="${deleteUrl}" styleClass="f_blocker" />
            <cti:button nameKey="confirmDeleteCancel" onclick="jQuery('#confirmDeleteDialog').hide()" />
        </div>
    </i:simplePopup>

    <cti:url var="deleteLoginUrl" value="/spring/stars/operator/account/deleteLogin">
        <cti:param name="accountId" value="${accountId}"/>
        <cti:param name="loginMode" value="${loginMode}"/>
    </cti:url>
    <i:simplePopup titleKey=".confirmDeleteDialogTitle" id="confirmDeleteLoginDialog" styleClass="mediumSimplePopup">
        <cti:msg2 key=".confirmDeleteLogin" arguments="${accountGeneral.accountDto.accountNumber}"/>
        <div class="actionArea">
            <cti:button nameKey="confirmDeleteOk" href="${deleteLoginUrl}" styleClass="f_blocker"/>
            <cti:button nameKey="confirmDeleteCancel" onclick="jQuery('#confirmDeleteLoginDialog').hide()"/>
        </div>
    </i:simplePopup>

    <cti:url var="generatedPasswordUrl" value="/spring/stars/operator/account/generatePassword" />

    <script type="text/javascript">

        //YukonGeneral.js
    	alignTableColumnsByTable('#customerContactTable', '#serviceInformationTable');
    	alignTableColumnsByTable('#serviceAddressTable', '#billingAddressTable');
    	//END YukonGeneral.js
    
    	jQuery(function() {

    		// commercial setup
    		// when in VIEW mode, don't show commercial values unless the account is commercial
    		var viewMode = ${cti:jsonString(mode) == 'VIEW'};
    		if (!viewMode) {
	    		var isCommercial = ${cti:jsonString(accountGeneral.accountDto.isCommercial)};
	    	    toggleCommercialInputs(isCommercial);

        		// billing setup
        		// if sameAsAbove is checked on page load, we know the address fields all match, set them to disabled
        		toggleBillingAddress();
        		saveBillingToTempFields();
        		var sameAsAbove = document.getElementById('usePrimaryAddressForBillingCheckBox').checked;
        		if (sameAsAbove) {
        			setBillingFieldsDisabled(true);
        		}
    		}
    		
    		jQuery(document).delegate("#passwordDialog", 'e_updatePassword', updatePassword);
            jQuery(document).delegate("a.f_resetPasswordDialog", 'click', resetPasswordDialog);
            jQuery(document).delegate(".f_prepPasswordFields", 'click', prepPasswordFields);
            jQuery(document).delegate(".f_generatePassword", 'click', generatePassword);
            jQuery(document).delegate(".f_deleteLogin", "click", function(){jQuery('#confirmDeleteLoginDialog').show();});
           
    	});

    	function toggleCommercialInputs(isCommercial) {
		    document.getElementById('accountDto.companyName').disabled = !isCommercial;
			document.getElementById('accountDto.commercialTypeEntryId').disabled = !isCommercial;
    	}

    	// when "same as above" is checked, it really does not matter what the billing fields contain, the controller will figure that out.
    	// this game is just to make the user feel warm and fuzzy
    	function toggleBillingAddress() {
    	    //same as above?
			if (jQuery('#usePrimaryAddressForBillingCheckBox').is(':checked')) {
				saveBillingToTempFields();
				document.getElementById('accountDto.billingAddress.locationAddress1').value = document.getElementById('accountDto.streetAddress.locationAddress1').value;
				document.getElementById('accountDto.billingAddress.locationAddress2').value = document.getElementById('accountDto.streetAddress.locationAddress2').value;
				document.getElementById('accountDto.billingAddress.cityName').value = document.getElementById('accountDto.streetAddress.cityName').value;
				document.getElementById('accountDto.billingAddress.stateCode').value = document.getElementById('accountDto.streetAddress.stateCode').value;
				document.getElementById('accountDto.billingAddress.zipCode').value = document.getElementById('accountDto.streetAddress.zipCode').value;
				setBillingFieldsDisabled(true);
			} else {
				document.getElementById('accountDto.billingAddress.locationAddress1').value = document.getElementById('temp_accountDto.billingAddress.locationAddress1').value;
				document.getElementById('accountDto.billingAddress.locationAddress2').value = document.getElementById('temp_accountDto.billingAddress.locationAddress2').value;
				document.getElementById('accountDto.billingAddress.cityName').value = document.getElementById('temp_accountDto.billingAddress.cityName').value;
				document.getElementById('accountDto.billingAddress.stateCode').value = document.getElementById('temp_accountDto.billingAddress.stateCode').value;
				document.getElementById('accountDto.billingAddress.zipCode').value = document.getElementById('temp_accountDto.billingAddress.zipCode').value;
				setBillingFieldsDisabled(false);
			}
    	}

    	function saveBillingToTempFields() {
    		document.getElementById('temp_accountDto.billingAddress.locationAddress1').value = document.getElementById('accountDto.billingAddress.locationAddress1').value;
			document.getElementById('temp_accountDto.billingAddress.locationAddress2').value = document.getElementById('accountDto.billingAddress.locationAddress2').value;
			document.getElementById('temp_accountDto.billingAddress.cityName').value = document.getElementById('accountDto.billingAddress.cityName').value;
			document.getElementById('temp_accountDto.billingAddress.stateCode').value = document.getElementById('accountDto.billingAddress.stateCode').value;
			document.getElementById('temp_accountDto.billingAddress.zipCode').value = document.getElementById('accountDto.billingAddress.zipCode').value;
    	}

    	function setBillingFieldsDisabled(disabled) {
    		jQuery('input[id^="accountDto.billingAddress."]').each(function() {
				this.disabled = disabled;
			});
    	}

    	function generatePassword() {
            var dataHash = {loginGroupName : $('loginBackingBean.loginGroupName').value}
            var userId = $('userId');
            if (userId != null && userId.value != 0) {
                dataHash[ 'userId'] = userId.value;
            }
            
            new jQuery.ajax({
                url: '${generatedPasswordUrl}',
                data: dataHash,
                success: function(data) {
                     jQuery(".password_editor_field").each(function(){
                         this.value = data;
                     });
                    // Check and show the password fields
                    jQuery('#showPasswordCheckbox').attr('checked', true);
                    showPassword();
                }
            });

        }

        function showPassword() {
            var type = jQuery("#showPasswordCheckbox:checked").length ? 'text' : 'password'; 
            jQuery(".password_editor_field:password").each(function(){
                var input = jQuery(this);
                if(type == 'text'){
                    input.hide();
                    input.siblings("input:text").show().val(input.val());
                }else{
                    input.siblings("input:text").hide();
                    //ugly, but works because we have a 1:1 mapping on the fields
                    input.show().val(input.siblings("input:text").val());
                }
            });
        }
        
        function updatePassword(event){
            jQuery("#passwordDialog").removeMessage();
            jQuery("#passwordDialog .error").removeClass('error');
            
            prepPasswordFields();
            
            if(jQuery("#password1").val().length == 0 && jQuery("#password2").val().length == 0){
                jQuery("#passwordDialog").addMessage({
                    message: '<cti:msg2 key=".loginInfoError.passwordTooShort" javaScriptEscape="true"/>', 
                    messageClass: 'ERROR'});
                jQuery("#password1, #password2").addClass('error');
            }else if(jQuery("#password1").val() != jQuery("#password2").val()){
                jQuery("#passwordDialog").addMessage({
                    message: '<cti:msg2 key=".loginInfoError.passwordNoMatch" javaScriptEscape="true"/>', 
                    messageClass: 'ERROR'});
                jQuery("#password1, #password2").addClass('error');
            }else{
                jQuery("#updatePasswordForm").ajaxSubmit({
                    dataType:  'json',
                    success:    function(data){
                        //reset the dialog and clear the errors
                        jQuery("#passwordDialog").dialog('close');
                        Yukon.ui.flashSuccess(data.flash);
                    },
                    error:      function(xhr){
                        var data = jQuery.parseJSON(xhr.responseText);
                        data = data.fieldErrors;
                        if(data.password1 == data.password2){
                            jQuery("#password1, #password2").addClass('error');
                            delete data.password2;
                        }
                        jQuery("#passwordDialog").addMessage({message: data, messageClass: 'error'});
                }});
            }
        }
        
        function prepPasswordFields(){
          //if we are showing the plain-text fields, copy them over to the password fields
            if(jQuery("#showPasswordCheckbox:checked").length > 0){
                jQuery(".password_editor_field:password").each(function(){
                    var input = jQuery(this);
                    //ugly, but works because we have a 1:1 mapping on the fields
                    input.val(input.siblings("input:text").val());
                });
            }
        }
        
        function resetPasswordDialog(){
            var dialog = jQuery("#passwordDialog");
            dialog.find("input:text, input:password").val("");
            dialog.removeMessage();
            dialog.find(".error").removeClass('error');
        }
    </script>
    
	<cti:msg2 var="naLabel" key="defaults.na"/>
    
    <cti:displayForPageEditModes modes="EDIT">
        <cti:url value="/spring/stars/operator/account/updateAccount" var="action"/>
    </cti:displayForPageEditModes>
    <cti:displayForPageEditModes modes="CREATE">
        <cti:url value="/spring/stars/operator/account/createAccount" var="action"/>
    </cti:displayForPageEditModes>
    
    <form:form id="updateForm" commandName="accountGeneral" action="${action}">
    
    	<input type="hidden" name="accountId" value="${accountId}">
        <input type="hidden" name="loginMode" value="${loginMode}">
    	
        <cti:dataGrid cols="2" rowStyle="vertical-align:top;" cellStyle="padding-right:20px;">
    	
    		<%-- CUSTOMER CONTACT --%>
    		<cti:dataGridCell>
    		
    			<tags:formElementContainer nameKey="customerContactSection">
	    			
	    				<tags:nameValueContainer2 id="customerContactTable">
	    				
	    					<tags:inputNameValue nameKey=".accountNumberLabel" path="accountDto.accountNumber"/>
	    					
	    					<c:if test="${mode == 'EDIT' || mode == 'CREATE' || accountGeneral.accountDto.isCommercial}">
		    					<tags:checkboxNameValue nameKey=".commercialLabel" path="accountDto.isCommercial" onclick="toggleCommercialInputs(this.checked);" id="isCommercialCheckbox"/>
		    					<tags:inputNameValue nameKey=".companyLabel" path="accountDto.companyName"/>
		    					<tags:yukonListEntrySelectNameValue nameKey=".commercialTypeLabel" path="accountDto.commercialTypeEntryId" energyCompanyId="${energyCompanyId}" listName="CI_CUST_TYPE"/>
	    					</c:if>
	    					
	    					<tags:inputNameValue nameKey=".customerNumberLabel" path="accountDto.customerNumber"/>
	    					<tags:inputNameValue nameKey=".lastNameLabel" path="accountDto.lastName"/>
	    					<tags:inputNameValue nameKey=".firstNameLabel" path="accountDto.firstName"/>
	    					<tags:inputNameValue nameKey=".homePhoneLabel" path="accountDto.homePhone"/>
	    					<tags:inputNameValue nameKey=".workPhoneLabel" path="accountDto.workPhone"/>
	    					<tags:inputNameValue nameKey=".emailLabel" path="accountDto.emailAddress"/>
	    					<tags:inputNameValue nameKey=".altTrackingNumberLabel" path="accountDto.altTrackingNumber"/>
	    					<cti:checkRolesAndProperties value="ODDS_FOR_CONTROL">
	    						<tags:checkboxNameValue nameKey=".notifyOddsForControlLabel" path="operatorGeneralUiExtras.notifyOddsForControl" id="notifyOddsForControlCheckbox"/>
	    					</cti:checkRolesAndProperties>
	    					<tags:textareaNameValue nameKey=".notesLabel" path="accountDto.accountNotes" rows="3" cols="20"/>
	    				
	    				</tags:nameValueContainer2>
	    			
	    			</tags:formElementContainer>
                    
                    <br>
                    
                    <%-- SERVICE INFORMATION --%>
                    <tags:formElementContainer nameKey="serviceInformationSection">
                
                        <tags:nameValueContainer2 id="serviceInformationTable">
                        
                            <tags:yukonListEntrySelectNameValue nameKey=".rateScheduleLabel" path="accountDto.rateScheduleEntryId" energyCompanyId="${energyCompanyId}" listName="RATE_SCHEDULE" defaultItemValue="0" defaultItemLabel="${naLabel}"/>
                            <tags:checkboxNameValue nameKey=".presenceRequiredLabel" path="accountDto.isCustAtHome" id="isCustAtHomeCheckbox"/>
                            <tags:inputNameValue nameKey=".customerStatusLabel" path="accountDto.customerStatus" size="1" maxlength="1"/>
                            <tags:selectNameValue nameKey=".substationLabel" path="accountDto.siteInfo.substationName" items="${substations}" itemValue="name" itemLabel="name" defaultItemValue="(none)" defaultItemLabel="${naLabel}"/>
                            <tags:inputNameValue nameKey=".feederLabel" path="accountDto.siteInfo.feeder"/>
                            <tags:inputNameValue nameKey=".poleLabel" path="accountDto.siteInfo.pole"/>
                            <tags:inputNameValue nameKey=".transformerSizeLabel" path="accountDto.siteInfo.transformerSize"/>
                            <tags:inputNameValue nameKey=".serviceVoltageLabel" path="accountDto.siteInfo.serviceVoltage"/>
                            
                        </tags:nameValueContainer2>
                        
                    </tags:formElementContainer>
    		
    		</cti:dataGridCell>
    		
            <cti:dataGridCell>
    		
                <%--SERVICE ADDRESS --%>
    			<tags:formElementContainer nameKey="serviceAddressSection">
	    			
    				<tags:nameValueContainer2 id="serviceAddressTable">
    				
    					<tags:inputNameValue nameKey=".address1Label" path="accountDto.streetAddress.locationAddress1"/>
    					<tags:inputNameValue nameKey=".address2Label" path="accountDto.streetAddress.locationAddress2"/>
    					<tags:inputNameValue nameKey=".cityLabel" path="accountDto.streetAddress.cityName"/>
    					<tags:inputNameValue nameKey=".stateLabel" path="accountDto.streetAddress.stateCode" size="2" maxlength="2"/>
    					<tags:inputNameValue nameKey=".zipLabel" path="accountDto.streetAddress.zipCode"/>
    					<tags:inputNameValue nameKey=".mapNumberLabel" path="accountDto.mapNumber"/>
    					<tags:inputNameValue nameKey=".countyLabel" path="accountDto.streetAddress.county"/>
						<tags:textareaNameValue nameKey=".addressNotesLabel" path="accountDto.propertyNotes" rows="3" cols="20"/>	    					
    				
    				</tags:nameValueContainer2>
    			
    			</tags:formElementContainer>
                
                <br>
    		
                <%-- BILLING ADDRESS --%>
    			<tags:formElementContainer nameKey="billingAddressSection">
	    			
                    <tags:nameValueContainer2 id="billingAddressTable">
    				
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
    						<tags:checkboxNameValue nameKey="defaults.blank" path="operatorGeneralUiExtras.usePrimaryAddressForBilling" id="usePrimaryAddressForBillingCheckBox" 
    						                        onclick="toggleBillingAddress();" checkBoxDescriptionNameKey=".usePrimaryAddressForBillingLabel" excludeColon="true"/>
						</cti:displayForPageEditModes>
						
    					<tags:inputNameValue nameKey=".billingAddress1Label" path="accountDto.billingAddress.locationAddress1"/>
    					<tags:inputNameValue nameKey=".billingAddress2Label" path="accountDto.billingAddress.locationAddress2"/>
    					<tags:inputNameValue nameKey=".billingCityLabel" path="accountDto.billingAddress.cityName"/>
    					<tags:inputNameValue nameKey=".billingStateLabel" path="accountDto.billingAddress.stateCode" size="2" maxlength="2"/>
    					<tags:inputNameValue nameKey=".billingZipLabel" path="accountDto.billingAddress.zipCode"/>
    					<form:hidden path="accountDto.billingAddress.county"/>
    					
    					<%-- for temporary storage of billing previous values, has no impact on actual form processing --%>
    					<input type="hidden" id="temp_accountDto.billingAddress.locationAddress1" value="${accountGeneral.accountDto.billingAddress.locationAddress1}"> 
    					<input type="hidden" id="temp_accountDto.billingAddress.locationAddress2" value="${accountGeneral.accountDto.billingAddress.locationAddress2}"> 
    					<input type="hidden" id="temp_accountDto.billingAddress.cityName" value="${accountGeneral.accountDto.billingAddress.cityName}"> 
    					<input type="hidden" id="temp_accountDto.billingAddress.stateCode" value="${accountGeneral.accountDto.billingAddress.stateCode}"> 
    					<input type="hidden" id="temp_accountDto.billingAddress.zipCode" value="${accountGeneral.accountDto.billingAddress.zipCode}"> 
    					
    				</tags:nameValueContainer2>
	    			
	    		</tags:formElementContainer>
    		
                <c:if test="${showLoginSection}">
                
                    <br>
                
                    <%-- LOGIN INFO --%>
                    <tags:formElementContainer nameKey="loginInfoSection">
                        
                        <tags:nameValueContainer2>
                            
                            <tags:selectNameValue nameKey=".customerGroup" 
                                path="loginBackingBean.loginGroupName" items="${ecResidentialGroups}" itemValue="groupName" itemLabel="groupName"/>
                            <tags:nameValue2 nameKey=".loginEnabled"><tags:checkbox path="loginBackingBean.loginEnabled"/></tags:nameValue2>
                            
                            <!-- Username Field -->
                            <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_USERNAME">
                                <tags:nameValue2 nameKey=".userName"><tags:input path="loginBackingBean.username" autocomplete="false"/></tags:nameValue2>
                            </cti:checkRolesAndProperties>
                            <cti:checkRolesAndProperties value="!OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_USERNAME">
                                <tags:hidden path="loginBackingBean.username"/>
                                <tags:nameValue2 nameKey=".userName">
                                    <spring:htmlEscape defaultHtmlEscape="true">${accountGeneral.loginBackingBean.username}</spring:htmlEscape>
                                </tags:nameValue2>
                            </cti:checkRolesAndProperties>
                            
                            <%-- The corresponding dialog (which contains a form) is at the end of the page.
                            this was done to avoid the confusion of an apparent nested form.  jQuery UI
                            (as of 1.8.16) will remove the specified element from the DOM create a new element
                            that represents the dialog at the end of the DOM and put the specified element inside
                            thus avoiding a nested form, but we are not guaranteed this to be true in the future.  --%>
                            <cti:displayForPageEditModes modes="EDIT">
                                <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_PASSWORD">
                                    <c:if test="${supportsPasswordSet and not empty passwordBean}">
                                        <tags:nameValue2 nameKey=".password">
                                            <small><a href="javascript:void(0);" class="f_editPassword f_resetPasswordDialog"><i:inline key=".changePassword" /></a></small>
                                        </tags:nameValue2>
                                    </c:if>
                                </cti:checkRolesAndProperties>
                            </cti:displayForPageEditModes>

                            <cti:displayForPageEditModes modes="CREATE,EDIT">
                                <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_PASSWORD">
                                    <c:if test="${supportsPasswordSet and empty passwordBean}">
                                        
                                        <!-- IE does not support changing the 'type' attribute on input fields.
                                             hence, what you see below in regards to the duplicate password fields -->
                                        <div class="password_editor">
                                        <tags:nameValue2 nameKey=".newPassword">
                                            <input type="text" class="dn password_editor_field" id="password1-plain" autocomplete="false" maxlength="64"/>
                                            <tags:password path="loginBackingBean.password1" cssClass="password_editor_field" autocomplete="false"/>
                                        </tags:nameValue2>
                                        <tags:nameValue2 nameKey=".confirmPassword">
                                            <input type="text" class="dn password_editor_field" id="password2-plain" autocomplete="false" maxlength="64"/>
                                            <tags:password path="loginBackingBean.password2" cssClass="password_editor_field" autocomplete="false"/>
                                        </tags:nameValue2>
                                        <tags:nameValue2 nameKey="defaults.blank" excludeColon="true">
                                            <button type="button" class="f_generatePassword"><i:inline key=".generatePassword"/></button>
                                            <br>
                                            <label>
                                                <input id="showPasswordCheckbox" type="checkbox" onclick="showPassword()"/>
                                                <i:inline key=".showPassword"/>
                                            </label>
                                        </tags:nameValue2>
                                    </c:if>
                                </cti:checkRolesAndProperties>
                            </cti:displayForPageEditModes>
                        </tags:nameValueContainer2>
                        
                        <cti:displayForPageEditModes modes="EDIT">
                            <c:if test="${loginMode eq 'EDIT'}">
                                <br/>
                                <br/>
                                <button type="button" class="f_deleteLogin fr"><i:inline key=".deleteLogin"/></button>
                            </c:if>
                        </cti:displayForPageEditModes>
                        
                    </tags:formElementContainer>
                </c:if>
    	
            </cti:dataGridCell>
        </cti:dataGrid>
        
        <br>
        <br>

        <%-- BUTTONS --%>
        <cti:displayForPageEditModes modes="CREATE,EDIT">
            <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                <cti:displayForPageEditModes modes="EDIT">
                    <cti:button nameKey="save" type="submit" styleClass="f_blocker f_prepPasswordFields"/>
                    <button type="button" onclick="jQuery('#confirmDeleteDialog').show()"><cti:msg2 key=".delete"/></button>
 		            <cti:url value="/spring/stars/operator/account/view" var="viewUrl">
			            <cti:param name="accountId" value="${accountId}"/>
			        </cti:url>
            		<cti:button nameKey="cancel" href="${viewUrl}"/>
                </cti:displayForPageEditModes>
            </cti:checkRolesAndProperties>
            <cti:displayForPageEditModes modes="CREATE">
                <cti:button nameKey="create" type="submit" styleClass="f_blocker f_prepPasswordFields"/>
                <input name="cancelCreation" type="submit" class="formSubmit" value="<cti:msg2 key="yukon.web.components.slowInput.cancel.label"/>">
            </cti:displayForPageEditModes>
        </cti:displayForPageEditModes>
        
        <cti:displayForPageEditModes modes="VIEW">
            <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                <cti:url value="/spring/stars/operator/account/edit" var="editUrl">
                    <cti:param name="accountId" value="${accountId}"/>
                </cti:url>
                <cti:button nameKey="edit" href="${editUrl}"/>
            </cti:checkRolesAndProperties>
        </cti:displayForPageEditModes>
	</form:form>

    <!-- Password Fields -->
    <cti:displayForPageEditModes modes="EDIT">
        <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_PASSWORD">
            <c:if test="${supportsPasswordSet and not empty passwordBean}">
                <dialog:inline id="passwordDialog" okEvent="e_updatePassword" on="a.f_editPassword" nameKey="passwordDialog">
                    <form:form id="updatePasswordForm" commandName="passwordBean" action="/spring/stars/operator/account/updatePassword">
                        <input type="hidden" name="accountId" value="${accountId}">
                        <input type="hidden" name="loginMode" value="${loginMode}">

                        <form:hidden path="userId" />
                        <form:hidden path="loginGroupName" />
                        <form:hidden path="loginEnabled" />
                        <form:hidden path="username" />
                        <form:hidden path="authType" />

                        <tags:nameValueContainer2 id="passwordFields">
                            <tags:nameValue2 nameKey=".newPassword">
                                <input type="text" class="dn password_editor_field" id="password1-plain" autocomplete="false" maxlength="64"/>
                                <tags:password path="password1" cssClass="password_editor_field" autocomplete="false" maxlength="64" />
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".confirmPassword">
                                <input type="text" class="dn password_editor_field" id="password2-plain" autocomplete="false" maxlength="64"/>
                                <tags:password path="password2" cssClass="password_editor_field" autocomplete="false" maxlength="64" />
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey="defaults.blank" excludeColon="true">
                                <button type="button" class="f_generatePassword">
                                    <i:inline key=".generatePassword" />
                                </button>
                                <br>
                                <label><input id="showPasswordCheckbox" type="checkbox"
                                    onclick="showPassword()"/>
                                <i:inline key=".showPassword"/></label>
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                    </form:form>
                </dialog:inline>
            </c:if>
        </cti:checkRolesAndProperties>
    </cti:displayForPageEditModes>

</cti:standardPage>