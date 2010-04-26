<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="accountEdit">

	<tags:setFormEditMode mode="${mode}"/>
	
	<cti:includeScript link="/JavaScript/yukonGeneral.js"/>

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
    		}

    		// billing setup
    		// if sameAsAbove is checked on page load, we know the address fields all match, set them to disabled
    		var sameAsAbove = $('usePrimaryAddressForBillingCheckBox').checked;
    		if (sameAsAbove) {
    			setBillingFieldsDisabled(true);
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

				$('temp_accountDto.billingAddress.locationAddress1').value = $('accountDto.billingAddress.locationAddress1').value;
				$('temp_accountDto.billingAddress.locationAddress2').value = $('accountDto.billingAddress.locationAddress2').value;
				$('temp_accountDto.billingAddress.cityName').value = $('accountDto.billingAddress.cityName').value;
				$('temp_accountDto.billingAddress.stateCode').value = $('accountDto.billingAddress.stateCode').value;
				$('temp_accountDto.billingAddress.zipCode').value = $('accountDto.billingAddress.zipCode').value;
				
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

    	function setBillingFieldsDisabled(disabled) {

    		$$('input[id^="accountDto.billingAddress."]').each(function(el) {
				el.disabled = disabled;
			});
    	}
    	
    </script>
    
	<cti:msg2 var="naLabel" key="defaults.na"/>
    
    <form id="deleteForm" action="/spring/stars/operator/account/accountDelete" method="post">
    	<input type="hidden" name="accountId" value="${accountId}">
    </form>
    
    <form:form id="updateForm" commandName="accountGeneral" action="/spring/stars/operator/account/accountUpdate">
    
    	<input type="hidden" name="accountId" value="${accountId}">
    	
    	<cti:dataGrid cols="2" rowStyle="vertical-align:top;" cellStyle="padding-right:40px;">
    	
    		<%-- CUSTOMER CONTACT --%>
    		<cti:dataGridCell>
    		
    			<tags:formElementContainer nameKey="customerContactSection">
	    			
	    				<tags:nameValueContainer2 id="customerContactTable">
	    				
	    					<tags:inputNameValue nameKey=".accountNumberLabel" path="accountDto.accountNumber"/>
	    					
	    					<c:if test="${mode == 'EDIT' || accountGeneral.accountDto.isCommercial}">
		    					<tags:checkboxNameValue nameKey=".commercialLabel" path="accountDto.isCommercial" onclick="toggleCommercialInputs(this.checked);" id="isCommercialCheckbox"/>
		    					<tags:inputNameValue nameKey=".companyLabel" path="accountDto.companyName"/>
		    					<tags:yukonListEntrySelectNameValue nameKey=".commercialTypeLabel" path="accountDto.commercialTypeEntryId" accountId="${accountId}" listName="CI_CUST_TYPE"/>
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
	    					<tags:textareaNameValue nameKey=".notesLabel" path="operatorGeneralUiExtras.notes" rows="3" cols="20"/>
	    				
	    				</tags:nameValueContainer2>
	    			
	    			</tags:formElementContainer>
    		
    		</cti:dataGridCell>
    		
    		<%--SERVICE ADDRESS --%>
    		<cti:dataGridCell>
    		
    			<tags:formElementContainer nameKey="serviceAddressSection">
	    			
	    				<tags:nameValueContainer2 id="serviceAddressTable">
	    				
	    					<tags:inputNameValue nameKey=".address1Label" path="accountDto.streetAddress.locationAddress1"/>
	    					<tags:inputNameValue nameKey=".address2Label" path="accountDto.streetAddress.locationAddress2"/>
	    					<tags:inputNameValue nameKey=".cityLabel" path="accountDto.streetAddress.cityName"/>
	    					<tags:inputNameValue nameKey=".stateLabel" path="accountDto.streetAddress.stateCode" size="2" maxlength="2"/>
	    					<tags:inputNameValue nameKey=".zipLabel" path="accountDto.streetAddress.zipCode"/>
	    					<tags:inputNameValue nameKey=".mapNumberLabel" path="accountDto.mapNumber"/>
	    					<tags:inputNameValue nameKey=".countyLabel" path="accountDto.streetAddress.county"/>
							<tags:textareaNameValue nameKey=".addressNotesLabel" path="operatorGeneralUiExtras.accountSiteNotes" rows="3" cols="20"/>	    					
	    				
	    				</tags:nameValueContainer2>
	    			
	    			</tags:formElementContainer>
    		
    		</cti:dataGridCell>
    		
    		<%-- SERVICE INFORMATION --%>
    		<cti:dataGridCell>
    		
    			<tags:formElementContainer nameKey="serviceInformationSection">
	    			
	    				<tags:nameValueContainer2 id="serviceInformationTable">
	    				
	    					<tags:yukonListEntrySelectNameValue nameKey=".rateScheduleLabel" path="accountDto.rateScheduleEntryId" accountId="${accountId}" listName="RATE_SCHEDULE" defaultItemValue="0" defaultItemLabel="${naLabel}"/>
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
    	
    		<%-- BILLING ADDRESS --%>
    		<cti:dataGridCell>
    		
    			<tags:formElementContainer nameKey="billingAddressSection">
	    			
	    				<tags:nameValueContainer2 id="billingAddressTable">
	    				
	    					<cti:displayForPageEditModes modes="EDIT">
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
    		
    		</cti:dataGridCell>
    	
    	</cti:dataGrid>
    	
    	<%-- BUTTONS --%>
    	<cti:displayForPageEditModes modes="EDIT">
	    	<br>
		    <tags:slowInput2 myFormId="updateForm" key="save" />
		    <tags:slowInput2 myFormId="deleteForm" key="delete" />
	    </cti:displayForPageEditModes>
	    
	</form:form>
    
    
    
</cti:standardPage>