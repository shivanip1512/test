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

    		var isCommercial = ${cti:jsonString(accountGeneral.accountDto.isCommercial)};
    	    toggleCommercialInputs(isCommercial);
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
	
    </script>
    
    
	
    <c:if test="${not empty errorMsg}">
            <div class="errorRed">${errorMsg}</div>
        <br>
    </c:if>
    
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
	    					<tags:checkboxNameValue nameKey=".commercialLabel" path="accountDto.isCommercial" onclick="toggleCommercialInputs(this.checked);" id="isCommercialCheckbox"/>
	    					<tags:inputNameValue nameKey=".companyLabel" path="accountDto.companyName"/>
	    					<tags:yukonListEntrySelectNameValue nameKey=".commercialTypeLabel" path="accountDto.commercialTypeEntryId" accountId="${accountId}" listName="CI_CUST_TYPE"/>
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
	    				
	    					<tags:yukonListEntrySelectNameValue nameKey=".rateScheduleLabel" path="accountDto.rateScheduleEntryId" accountId="${accountId}" listName="RATE_SCHEDULE" defaultItemValue="0" defaultItemLabel="(none)"/>
	    					<tags:checkboxNameValue nameKey=".presenceRequiredLabel" path="accountDto.isCustAtHome" id="isCustAtHomeCheckbox"/>
	    					<tags:inputNameValue nameKey=".customerStatusLabel" path="accountDto.customerStatus" size="1" maxlength="1"/>
	   						<tags:selectNameValue nameKey=".substationLabel" path="accountDto.siteInfo.substationName" items="${substations.starsSubstationList}" itemValue="substationName" itemLabel="substationName"/>
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
		    					<tags:nameValue2 nameKey="defaults.blank">
			    					<tags:checkbox path="operatorGeneralUiExtras.usePrimaryAddressForBilling" id="usePrimaryAddressForBillingCheckBox"/>
									<label for="usePrimaryAddressForBillingCheckBox"><i:inline key=".usePrimaryAddressForBillingLabel"/></label>
								</tags:nameValue2>
							</cti:displayForPageEditModes>
							
	    					<tags:inputNameValue nameKey=".billingAddress1Label" path="accountDto.billingAddress.locationAddress1"/>
	    					<tags:inputNameValue nameKey=".billingAddress2Label" path="accountDto.billingAddress.locationAddress2"/>
	    					<tags:inputNameValue nameKey=".billingCityLabel" path="accountDto.billingAddress.cityName"/>
	    					<tags:inputNameValue nameKey=".billingStateLabel" path="accountDto.billingAddress.stateCode" size="2" maxlength="2"/>
	    					<tags:inputNameValue nameKey=".billingZipLabel" path="accountDto.billingAddress.zipCode"/>
	    					<form:hidden path="accountDto.billingAddress.county"/>
	    					
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