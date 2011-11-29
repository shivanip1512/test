<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i"%>

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
            <cti:button nameKey="confirmDeleteCancel" onclick="$('confirmDeleteDialog').hide()" />
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
            <cti:button nameKey="confirmDeleteCancel" onclick="$('confirmDeleteLoginDialog').hide()"/>
        </div>
    </i:simplePopup>

    <script type="text/javascript">

    	alignTableColumnsByTable('#customerContactTable', '#serviceInformationTable');
    	alignTableColumnsByTable('#serviceAddressTable', '#billingAddressTable');
    
    	Event.observe(window, 'load', function() {

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
        		var sameAsAbove = $('usePrimaryAddressForBillingCheckBox').checked;
        		if (sameAsAbove) {
        			setBillingFieldsDisabled(true);
        		}
    		}
    		
    	});

    	function toggleCommercialInputs(isCommercial) {
        	
    		if (isCommercial) {
				$('accountDto.companyName').disabled = false;
				$('accountDto.commercialTypeEntryId').disabled = false;
    		} else {
				$('accountDto.companyName').disabled = true;
				$('accountDto.commercialTypeEntryId').disabled = true;
    		}
    	}

    	// when "same as above" is checked, it really does not matter what the billing fields contain, the controller will figure that out.
    	// this game is just to make the user feel warm and fuzzy
    	function toggleBillingAddress() {

			var sameAsAbove = $('usePrimaryAddressForBillingCheckBox').checked;

			if (sameAsAbove) {

				saveBillingToTempFields();
				
				$('accountDto.billingAddress.locationAddress1').value = $('accountDto.streetAddress.locationAddress1').value;
				$('accountDto.billingAddress.locationAddress2').value = $('accountDto.streetAddress.locationAddress2').value;
				$('accountDto.billingAddress.cityName').value = $('accountDto.streetAddress.cityName').value;
				$('accountDto.billingAddress.stateCode').value = $('accountDto.streetAddress.stateCode').value;
				$('accountDto.billingAddress.zipCode').value = $('accountDto.streetAddress.zipCode').value;

				setBillingFieldsDisabled(true);
				
			} else {

				$('accountDto.billingAddress.locationAddress1').value = $('temp_accountDto.billingAddress.locationAddress1').value;
				$('accountDto.billingAddress.locationAddress2').value = $('temp_accountDto.billingAddress.locationAddress2').value;
				$('accountDto.billingAddress.cityName').value = $('temp_accountDto.billingAddress.cityName').value;
				$('accountDto.billingAddress.stateCode').value = $('temp_accountDto.billingAddress.stateCode').value;
				$('accountDto.billingAddress.zipCode').value = $('temp_accountDto.billingAddress.zipCode').value;

				setBillingFieldsDisabled(false);
			}
    	}

    	function saveBillingToTempFields() {

    		$('temp_accountDto.billingAddress.locationAddress1').value = $('accountDto.billingAddress.locationAddress1').value;
			$('temp_accountDto.billingAddress.locationAddress2').value = $('accountDto.billingAddress.locationAddress2').value;
			$('temp_accountDto.billingAddress.cityName').value = $('accountDto.billingAddress.cityName').value;
			$('temp_accountDto.billingAddress.stateCode').value = $('accountDto.billingAddress.stateCode').value;
			$('temp_accountDto.billingAddress.zipCode').value = $('accountDto.billingAddress.zipCode').value;
    	}

    	function setBillingFieldsDisabled(disabled) {

    		$$('input[id^="accountDto.billingAddress."]').each(function(el) {
				el.disabled = disabled;
			});
    	}

    	function generatePassword() {
            new Ajax.Request('/spring/stars/operator/account/generatePassword', {
                onSuccess: function(response) {
                     var generatedPassword = response.responseText;

                     var password1 = $('loginBackingBean.password1');
                     password1.value = generatedPassword;

                     var password2 = $('loginBackingBean.password2');
                     password2.value = generatedPassword;
                 }
            });

            // Check and show the password fields
            var showPasswordCheckbox = $('showPasswordCheckbox');
            showPasswordCheckbox.checked = true;
            showPassword();
        }

        function showPassword() {
            var showPasswordCheckbox = $('showPasswordCheckbox');
            if (showPasswordCheckbox.checked) {
                changeInputType('loginBackingBean.password1', 'text');
                changeInputType('loginBackingBean.password2', 'text');
            } else {
                changeInputType('loginBackingBean.password1', 'password');
                changeInputType('loginBackingBean.password2', 'password');
            }
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
                            
                            <!-- Password Fields -->
                            <cti:displayForPageEditModes modes="EDIT,CREATE">
                                <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_PASSWORD">
                                    <c:if test="${supportsPasswordSet}">
                                        <tags:nameValue2 nameKey=".newPassword">
                                            <tags:password path="loginBackingBean.password1" autocomplete="false"/>
                                        </tags:nameValue2>
                                        <tags:nameValue2 nameKey=".confirmPassword">
                                            <tags:password path="loginBackingBean.password2" autocomplete="false"/>
                                        </tags:nameValue2>
                                        <tags:nameValue2 nameKey="defaults.blank" excludeColon="true">
                                            <button type="button" onclick="generatePassword();"><i:inline key=".generatePassword"/></button>
                                            <br>
                                            <input id="showPasswordCheckbox" type="checkbox" onclick="showPassword()" /> <i:inline key=".showPassword"/>
                                        </tags:nameValue2>
                                    </c:if>
                                </cti:checkRolesAndProperties>
                            </cti:displayForPageEditModes>
                            
                        </tags:nameValueContainer2>
                        
                        <cti:displayForPageEditModes modes="EDIT">
                            <c:if test="${loginMode eq 'EDIT'}">
                                <span style="float:right;padding-top:3px;"><button type="button" onclick="$('confirmDeleteLoginDialog').show()"><i:inline key=".deleteLogin"/></button></span>
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
                    <cti:button nameKey="save" type="submit" styleClass="f_blocker"/>
                    <button type="button" onclick="$('confirmDeleteDialog').show()"><cti:msg2 key=".delete"/></button>
 		            <cti:url value="/spring/stars/operator/account/view" var="viewUrl">
			            <cti:param name="accountId" value="${accountId}"/>
			        </cti:url>
            		<cti:button nameKey="cancel" href="${viewUrl}"/>
                </cti:displayForPageEditModes>
            </cti:checkRolesAndProperties>
            <cti:displayForPageEditModes modes="CREATE">
                <cti:button nameKey="create" type="submit" styleClass="f_blocker"/>
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
    
</cti:standardPage>