<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:standardPage module="operator" page="account.${mode}">

<!-- TODO: make this a picker and delete style -->
<style type="text/css">.group-dropdown {max-width: 260px;}</style>

    <cti:checkEnergyCompanyOperator showError="true" >
        <tags:setFormEditMode mode="${mode}"/>
        
        <form id="deleteAccountForm" action="deleteAccount" method="post">
            <cti:csrfToken/>
            <input type="hidden" name="accountId" value="${accountId}">
        </form>
        <d:confirm on=".js-delete" nameKey="delete" argument="${accountGeneral.accountDto.accountNumber}"/>
        
        <form id="deleteUserForm" action="deleteLogin" method="post">
          <cti:csrfToken/>
          <input type="hidden" name="accountId" value="${accountId}">
          <input type="hidden" name="loginMode" value="${loginMode}">
        </form>
        <d:confirm on=".js-delete-user" nameKey="delete.user" argument="${accountGeneral.accountDto.accountNumber}"/>
    
        <cti:url var="generatedPasswordUrl" value="generatePassword" />
    
        <script type="text/javascript">
    
            $(function() {
    
                // commercial setup
                // when in VIEW mode, don't show commercial values unless the account is commercial
                var viewMode = ${mode == 'VIEW'};
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
                
                $(document).on('e_updatePassword', '#passwordDialog', updatePassword);
                $(document).on('click', 'a.js-resetPasswordDialog', resetPasswordDialog);
                $(document).on('click', '.js-prepPasswordFields', prepPasswordFields);
                $(document).on('click', '.js-generatePassword', generatePassword);
            });
    
            function toggleCommercialInputs(isCommercial) {
                document.getElementById('accountDto.companyName').disabled = !isCommercial;
                document.getElementById('accountDto.commercialTypeEntryId').disabled = !isCommercial;
            }
    
            // when "same as above" is checked, it really does not matter what the billing fields contain, the controller will figure that out.
            // this game is just to make the user feel warm and fuzzy
            function toggleBillingAddress() {
                //same as above?
                if ($('#usePrimaryAddressForBillingCheckBox').is(':checked')) {
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
                $('input[id^="accountDto.billingAddress."]').each(function() {
                    this.disabled = disabled;
                });
            }
    
            function generatePassword() {
                var dataHash = {userGroupName : $('#loginBackingBean.userGroupName').val()};
                var userId = $('#userId');
                if (0 !== userId.length && parseInt(userId.val(),10) !== 0) {
                    dataHash[ 'userId'] = userId.val();
                }
                
                new $.ajax({
                    url: '${generatedPasswordUrl}',
                    data: dataHash
                }).done(function(data) {
                    $(".password_editor_field").each(function(){
                        this.value = data;
                    });
                   // Check and show the password fields
                   $('#showPasswordCheckbox').attr('checked', true);
                   showPassword();
               });
    
            }
    
            function showPassword() {
                var type = $("#showPasswordCheckbox:checked").length ? 'text' : 'password'; 
                $(".password_editor_field:password").each(function(){
                    var input = $(this);
                    if(type === 'text'){
                        input.hide();
                        input.siblings("input:text").show().val(input.val());
                    } else {
                        input.siblings("input:text").hide();
                        //ugly, but works because we have a 1:1 mapping on the fields
                        input.show().val(input.siblings("input:text").val());
                    }
                });
            }
            
            function updatePassword(event){
                $("#passwordDialog").removeMessages();
                $("#passwordDialog .error").removeClass('error');
                
                prepPasswordFields();
                
                 if($("#password1").val() !== $("#password2").val()){
                    $("#passwordDialog").addMessage({
                        message: '<cti:msg2 key=".loginInfoError.passwordNoMatch" javaScriptEscape="true"/>', 
                        messageClass: 'error'});
                    $("#password1, #password2").addClass('error');
                }else{
                    $("#updatePasswordForm").ajaxSubmit({
                        dataType:  'json',
                        success:    function(data){
                            //reset the dialog and clear the errors
                            $("#passwordDialog").dialog('close');
                            yukon.ui.alertSuccess(data.flash);
                        },
                        error:      function(xhr){
                            var data = $.parseJSON(xhr.responseText);
                            data = data.fieldErrors;
                            $("#password1, #password2").addClass('error');
                            if(data.password1 === data.password2){
                                delete data.password2;
                            }
                            $("#passwordDialog").addMessage({message: data, messageClass: 'error'});
                    }});
                }
            }
            
            function prepPasswordFields(){
              //if we are showing the plain-text fields, copy them over to the password fields
                if($("#showPasswordCheckbox:checked").length > 0){
                    $(".password_editor_field:password").each(function(){
                        var input = $(this);
                        //ugly, but works because we have a 1:1 mapping on the fields
                        input.val(input.siblings("input:text").val());
                    });
                }
            }
            
            function resetPasswordDialog(){
                var dialog = $("#passwordDialog");
                dialog.find("input:text, input:password").val("");
                dialog.removeMessages();
                dialog.find(".error").removeClass('error');
            }
        </script>
        
        <cti:msg2 var="naLabel" key="defaults.na"/>
        
        <cti:displayForPageEditModes modes="EDIT">
            <cti:url value="/stars/operator/account/updateAccount" var="action"/>
        </cti:displayForPageEditModes>
        <cti:displayForPageEditModes modes="CREATE">
            <cti:url value="/stars/operator/account/createAccount" var="action"/>
        </cti:displayForPageEditModes>
        
        <form:form id="updateForm" commandName="accountGeneral" action="${action}">
            <cti:csrfToken/>
            <input type="hidden" name="accountId" value="${accountId}">
            <input type="hidden" name="loginMode" value="${loginMode}">
            
            <div class="column-12-12 clearfix">
                <div class="column one">
                
                    <%-- CUSTOMER CONTACT --%>
                    <tags:sectionContainer2 nameKey="customerContactSection" styleClass="stacked">
                    
                        <tags:nameValueContainer2 id="customerContactTable">
                        
                            <tags:inputNameValue nameKey=".accountNumberLabel" path="accountDto.accountNumber"/>
                            
                            <c:if test="${mode == 'EDIT' || mode == 'CREATE' || accountGeneral.accountDto.isCommercial}">
                                <tags:checkboxNameValue nameKey=".commercialLabel" path="accountDto.isCommercial" onclick="toggleCommercialInputs(this.checked);" id="isCommercialCheckbox"/>
                                <tags:inputNameValue nameKey=".companyLabel" path="accountDto.companyName"/>
                                <tags:yukonListEntrySelectNameValue id="accountDto.commercialTypeEntryId" nameKey=".commercialTypeLabel" path="accountDto.commercialTypeEntryId" energyCompanyId="${energyCompanyId}" listName="CI_CUST_TYPE"/>
                            </c:if>
                            
                            <tags:inputNameValue nameKey=".customerNumberLabel" path="accountDto.customerNumber" nameClass="bob"/>
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
                    
                    </tags:sectionContainer2>
                    
                    <%-- SERVICE INFORMATION --%>
                    <tags:sectionContainer2 nameKey="serviceInformationSection" styleClass="stacked">
                
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
                        
                    </tags:sectionContainer2>
                    
                </div>
                <div class="column two nogutter">
                
                    <%--SERVICE ADDRESS --%>
                    <tags:sectionContainer2 nameKey="serviceAddressSection" styleClass="stacked">
                        
                        <tags:nameValueContainer2 id="serviceAddressTable">
                        
                            <tags:inputNameValue nameKey=".address1Label" path="accountDto.streetAddress.locationAddress1" maxlength="100"/>
                            <tags:inputNameValue nameKey=".address2Label" path="accountDto.streetAddress.locationAddress2" maxlength="100"/>
                            <tags:inputNameValue nameKey=".cityLabel" path="accountDto.streetAddress.cityName" maxlength="32"/>
                            <tags:inputNameValue nameKey=".stateLabel" path="accountDto.streetAddress.stateCode" size="3" maxlength="2"/>
                            <tags:inputNameValue nameKey=".zipLabel" path="accountDto.streetAddress.zipCode" maxlength="12"/>
                            <tags:inputNameValue nameKey=".mapNumberLabel" path="accountDto.mapNumber"/>
                            <tags:inputNameValue nameKey=".countyLabel" path="accountDto.streetAddress.county" maxlength="30"/>
                            <tags:textareaNameValue nameKey=".addressNotesLabel" path="accountDto.propertyNotes" rows="3" cols="20"/>                            
                        
                        </tags:nameValueContainer2>
                    
                    </tags:sectionContainer2>
                    
                    <%-- BILLING ADDRESS --%>
                    <tags:sectionContainer2 nameKey="billingAddressSection" styleClass="stacked">
                        
                        <tags:nameValueContainer2 id="billingAddressTable">
                        
                            <cti:displayForPageEditModes modes="EDIT,CREATE">
                                <tags:checkboxNameValue nameKey="defaults.blank" path="operatorGeneralUiExtras.usePrimaryAddressForBilling" id="usePrimaryAddressForBillingCheckBox" 
                                                        onclick="toggleBillingAddress();" checkBoxDescriptionNameKey=".usePrimaryAddressForBillingLabel" excludeColon="true"/>
                            </cti:displayForPageEditModes>
                            
                            <tags:inputNameValue nameKey=".billingAddress1Label" path="accountDto.billingAddress.locationAddress1" maxlength="100"/>
                            <tags:inputNameValue nameKey=".billingAddress2Label" path="accountDto.billingAddress.locationAddress2" maxlength="100"/>
                            <tags:inputNameValue nameKey=".billingCityLabel" path="accountDto.billingAddress.cityName" maxlength="32"/>
                            <tags:inputNameValue nameKey=".billingStateLabel" path="accountDto.billingAddress.stateCode" size="3" maxlength="2"/>
                            <tags:inputNameValue nameKey=".billingZipLabel" path="accountDto.billingAddress.zipCode" maxlength="12"/>
                            <form:hidden path="accountDto.billingAddress.county" maxlength="30"/>
                            
                            <%-- for temporary storage of billing previous values, has no impact on actual form processing --%>
                            <input type="hidden" id="temp_accountDto.billingAddress.locationAddress1" value="${fn:escapeXml(accountGeneral.accountDto.billingAddress.locationAddress1)}"/> 
                            <input type="hidden" id="temp_accountDto.billingAddress.locationAddress2" value="${fn:escapeXml(accountGeneral.accountDto.billingAddress.locationAddress2)}"/> 
                            <input type="hidden" id="temp_accountDto.billingAddress.cityName" value="${fn:escapeXml(accountGeneral.accountDto.billingAddress.cityName)}"/> 
                            <input type="hidden" id="temp_accountDto.billingAddress.stateCode" value="${fn:escapeXml(accountGeneral.accountDto.billingAddress.stateCode)}"/> 
                            <input type="hidden" id="temp_accountDto.billingAddress.zipCode" value="${fn:escapeXml(accountGeneral.accountDto.billingAddress.zipCode)}"/> 
                            
                        </tags:nameValueContainer2>
                        
                    </tags:sectionContainer2>
                    
                    <c:if test="${showLoginSection}">
                    
                        <%-- LOGIN INFO --%>
                        <tags:sectionContainer2 nameKey="userInfoSection" styleClass="stacked">
                            
                            <tags:nameValueContainer2>
                                <cti:msg2 var="none" key="defaults.none"/>
                                <tags:selectNameValue nameKey=".customerGroup" path="loginBackingBean.userGroupName" items="${ecResidentialUserGroups}" 
                                                                     itemValue="userGroupName" itemLabel="userGroupName" defaultItemLabel="${none}"
                                                                     inputClass="group-dropdown"/>
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
                                this was done to avoid the confusion of an apparent nested form.  $ UI
                                (as of 1.8.16) will remove the specified element from the DOM create a new element
                                that represents the dialog at the end of the DOM and put the specified element inside
                                thus avoiding a nested form, but we are not guaranteed this to be true in the future.  --%>
                                <cti:displayForPageEditModes modes="EDIT">
                                    <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_PASSWORD">
                                        <c:if test="${supportsPasswordSet and not empty passwordBean}">
                                            <tags:nameValue2 nameKey=".password">
                                                <small><a href="javascript:void(0);" class="js-editPassword js-resetPasswordDialog"><i:inline key=".changePassword" /></a></small>
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
                                                <input type="text" class="password_editor_field" id="password1-plain" autocomplete="false" maxlength="64" style="display:none;"/>
                                                <tags:password path="loginBackingBean.password1" cssClass="password_editor_field" autocomplete="false"/>
                                            </tags:nameValue2>
                                            <tags:nameValue2 nameKey=".confirmPassword">
                                                <input type="text" class="password_editor_field" id="password2-plain" autocomplete="false" maxlength="64" style="display:none;"/>
                                                <tags:password path="loginBackingBean.password2" cssClass="password_editor_field" autocomplete="false"/>
                                            </tags:nameValue2>
                                        </c:if>
                                    </cti:checkRolesAndProperties>
                                </cti:displayForPageEditModes>
                            </tags:nameValueContainer2>
                            
                            <cti:displayForPageEditModes modes="CREATE,EDIT">
                                <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_PASSWORD">
                                    <c:if test="${supportsPasswordSet and empty passwordBean}">
                                        <div class="action-area">
                                            <label><input id="showPasswordCheckbox" type="checkbox" onclick="showPassword()"/><i:inline key=".showPassword"/></label>
                                            <cti:msg2 key=".generatePassword" var="generatePword"/>
                                            <cti:button classes="js-generatePassword" label="${generatePword}"/>
                                        </div>
                                    </c:if>
                                </cti:checkRolesAndProperties>
                            </cti:displayForPageEditModes>
                            
                            <cti:displayForPageEditModes modes="EDIT">
                                <c:if test="${loginMode eq 'EDIT'}">
                                    <div class="action-area">
                                        <cti:button nameKey="delete.user" data-form="#deleteUserForm" classes="js-delete-user delete"/>
                                    </div>
                                </c:if>
                            </cti:displayForPageEditModes>
                            
                        </tags:sectionContainer2>
                    </c:if>
                
                </div>
            </div>
            
            <%-- BUTTONS --%>
            <div class="page-action-area">
            
                <cti:displayForPageEditModes modes="CREATE,EDIT">
                    <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                        <cti:displayForPageEditModes modes="EDIT">
                            <cti:button nameKey="save" type="submit" classes="js-blocker js-prepPasswordFields primary action"/>
                            
                            <cti:button nameKey="delete" classes="js-delete delete" data-form="#deleteAccountForm"/>
                            
                             <cti:url value="/stars/operator/account/view" var="viewUrl">
                                <cti:param name="accountId" value="${accountId}"/>
                            </cti:url>
                            <cti:button nameKey="cancel" href="${viewUrl}"/>
                        </cti:displayForPageEditModes>
                    </cti:checkRolesAndProperties>
                    
                    <cti:displayForPageEditModes modes="CREATE">
                        <cti:button nameKey="create" type="submit" classes="js-blocker js-prepPasswordFields primary action"/>
                        <cti:url value="/stars/operator/inventory/home" var="homeUrl"/>
                        <cti:button nameKey="cancel" href="${homeUrl}"/>
                    </cti:displayForPageEditModes>
                </cti:displayForPageEditModes>
                
                <cti:displayForPageEditModes modes="VIEW">
                    <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                        <cti:url value="/stars/operator/account/edit" var="editUrl">
                            <cti:param name="accountId" value="${accountId}"/>
                        </cti:url>
                        <cti:button nameKey="edit" icon="icon-pencil" href="${editUrl}"/>
                    </cti:checkRolesAndProperties>
                </cti:displayForPageEditModes>
            </div>
            
        </form:form>
    
        <!-- Password Fields -->
        <cti:displayForPageEditModes modes="EDIT">
            <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_PASSWORD">
                <c:if test="${supportsPasswordSet and not empty passwordBean}">
                    <d:inline id="passwordDialog" okEvent="e_updatePassword" on="a.js-editPassword" nameKey="passwordDialog">
                        <form:form id="updatePasswordForm" commandName="passwordBean" action="/stars/operator/account/updatePassword">
                            <cti:csrfToken/>
                            <input type="hidden" name="accountId" value="${accountId}">
                            <input type="hidden" name="loginMode" value="${loginMode}">
    
                            <form:hidden path="userId" />
                            <form:hidden path="userGroupName" />
                            <form:hidden path="loginEnabled" />
                            <form:hidden path="username" />
    
                            <tags:nameValueContainer2 id="passwordFields">
                                <tags:nameValue2 nameKey=".newPassword">
                                    <input type="text" class="password_editor_field" id="password1-plain" autocomplete="false" maxlength="64" style="display:none;"/>
                                    <tags:password path="password1" cssClass="password_editor_field" autocomplete="false" maxlength="64" />
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".confirmPassword">
                                    <input type="text" class="password_editor_field" id="password2-plain" autocomplete="false" maxlength="64" style="display:none;"/>
                                    <tags:password path="password2" cssClass="password_editor_field" autocomplete="false" maxlength="64" />
                                </tags:nameValue2>
                            </tags:nameValueContainer2>
                            <div class="action-area">
                                <label><input id="showPasswordCheckbox" type="checkbox" onclick="showPassword()"/><i:inline key=".showPassword"/></label>
                                <cti:msg2 key=".generatePassword" var="generatePword"/>
                                <cti:button classes="js-generatePassword" label="${generatePword}"/>
                            </div>
                        </form:form>
                    </d:inline>
                </c:if>
            </cti:checkRolesAndProperties>
        </cti:displayForPageEditModes>
        
    </cti:checkEnergyCompanyOperator>
</cti:standardPage>