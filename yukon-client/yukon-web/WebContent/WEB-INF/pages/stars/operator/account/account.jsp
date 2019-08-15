<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="account.${mode}">

<cti:includeScript link="/resources/js/pages/yukon.assets.account.js"/>
    
<!-- TODO: make this a picker and delete style -->
<style type="text/css">.group-dropdown {max-width: 260px;}</style>
    
    <input type="hidden" class="js-page-mode" value="${mode}">
    <input type="hidden" class="js-password-mismatch" value="<cti:msg2 key=".loginInfoError.passwordNoMatch"/>">
    
    <cti:checkEnergyCompanyOperator showError="true" accountId="${accountId}">
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
        
        <cti:msg2 var="naLabel" key="yukon.common.na"/>
        
        <cti:displayForPageEditModes modes="EDIT">
            <cti:url value="/stars/operator/account/updateAccount" var="action"/>
        </cti:displayForPageEditModes>
        <cti:displayForPageEditModes modes="CREATE">
            <cti:url value="/stars/operator/account/createAccount" var="action"/>
        </cti:displayForPageEditModes>
        
        <form:form id="updateForm" modelAttribute="accountGeneral" action="${action}">
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
                                <tags:nameValue2 excludeColon="true">
                                    <label>
                                        <tags:checkbox path="accountDto.isCommercial" styleClass="js-is-commercial-acct"/>
                                        <i:inline key=".commercialLabel"/>
                                    </label>
                                </tags:nameValue2>
                                <tags:inputNameValue nameKey=".companyLabel" path="accountDto.companyName" 
                                    inputClass="js-company-name"/>
                                <tags:yukonListEntrySelectNameValue id="accountDto.commercialTypeEntryId"
                                    nameKey=".commercialTypeLabel" path="accountDto.commercialTypeEntryId" 
                                    energyCompanyId="${energyCompanyId}" listName="CI_CUST_TYPE"
                                    inputClass="js-company-type"/>
                            </c:if>
                            
                            <tags:inputNameValue nameKey=".customerNumberLabel" path="accountDto.customerNumber"/>
                            <tags:inputNameValue nameKey=".lastNameLabel" path="accountDto.lastName"/>
                            <tags:inputNameValue nameKey=".firstNameLabel" path="accountDto.firstName"/>
                            <tags:inputNameValue nameKey=".homePhoneLabel" path="accountDto.homePhone"/>
                            <tags:inputNameValue nameKey=".workPhoneLabel" path="accountDto.workPhone"/>
                            <tags:inputNameValue nameKey=".emailLabel" path="accountDto.emailAddress"/>
                            <tags:inputNameValue nameKey=".altTrackingNumberLabel" path="accountDto.altTrackingNumber"/>
                            <cti:checkRolesAndProperties value="ODDS_FOR_CONTROL">
                                <tags:checkboxNameValue nameKey=".notifyOddsForControlLabel" 
                                    path="operatorGeneralUiExtras.notifyOddsForControl" 
                                    id="notifyOddsForControlCheckbox"/>
                            </cti:checkRolesAndProperties>
                            <tags:textareaNameValue nameKey=".notesLabel" path="accountDto.accountNotes" 
                                rows="3" cols="20"/>
                        </tags:nameValueContainer2>
                        
                    </tags:sectionContainer2>
                    
                    <%-- SERVICE INFORMATION --%>
                    <tags:sectionContainer2 nameKey="serviceInformationSection" styleClass="stacked">
                
                        <tags:nameValueContainer2 id="serviceInformationTable">
                        
                            <tags:yukonListEntrySelectNameValue nameKey=".rateScheduleLabel" 
                                path="accountDto.rateScheduleEntryId" energyCompanyId="${energyCompanyId}" 
                                listName="RATE_SCHEDULE" defaultItemValue="0" defaultItemLabel="${naLabel}"/>
                            <tags:checkboxNameValue nameKey=".presenceRequiredLabel" path="accountDto.isCustAtHome" 
                                id="isCustAtHomeCheckbox"/>
                            <tags:inputNameValue nameKey=".customerStatusLabel" path="accountDto.customerStatus" 
                                size="1" maxlength="1"/>
                            <tags:selectNameValue nameKey=".substationLabel" path="accountDto.siteInfo.substationName" 
                                items="${substations}" itemValue="name" itemLabel="name" 
                                defaultItemValue="(none)" defaultItemLabel="${naLabel}"/>
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
                            
                            <tags:inputNameValue nameKey=".address1Label" path="accountDto.streetAddress.locationAddress1" 
                                inputClass="js-service-address-1" maxlength="100"/>
                            <tags:inputNameValue nameKey=".address2Label" path="accountDto.streetAddress.locationAddress2" 
                                inputClass="js-service-address-2" maxlength="100"/>
                            <tags:inputNameValue nameKey=".cityLabel" path="accountDto.streetAddress.cityName" 
                                inputClass="js-service-city" maxlength="32"/>
                            <tags:inputNameValue nameKey=".stateLabel" path="accountDto.streetAddress.stateCode" size="3" 
                                inputClass="js-service-state" maxlength="2"/>
                            <tags:inputNameValue nameKey=".zipLabel" path="accountDto.streetAddress.zipCode" maxlength="12"
                                inputClass="js-service-zip"/>
                            <tags:inputNameValue nameKey=".mapNumberLabel" path="accountDto.mapNumber"/>
                            <tags:inputNameValue nameKey=".countyLabel" path="accountDto.streetAddress.county" 
                                maxlength="30" inputClass="js-service-county"/>
                            <tags:textareaNameValue nameKey=".addressNotesLabel" path="accountDto.propertyNotes" 
                                rows="3" cols="20"/>
                            
                        </tags:nameValueContainer2>
                        
                    </tags:sectionContainer2>
                    
                    <%-- BILLING ADDRESS --%>
                    <tags:sectionContainer2 nameKey="billingAddressSection" styleClass="stacked">
                        
                        <tags:nameValueContainer2 id="billingAddressTable">
                        
                            <cti:displayForPageEditModes modes="EDIT,CREATE">
                                <tags:checkboxNameValue nameKey="yukon.common.blank" 
                                    path="operatorGeneralUiExtras.usePrimaryAddressForBilling" 
                                    inputClass="js-use-same-address" 
                                    checkBoxDescriptionNameKey=".usePrimaryAddressForBillingLabel" excludeColon="true"/>
                            </cti:displayForPageEditModes>
                            
                            <tags:inputNameValue nameKey=".billingAddress1Label"
                                path="accountDto.billingAddress.locationAddress1" maxlength="100"
                                inputClass="js-billing js-billing-address-1"/>
                            <tags:inputNameValue nameKey=".billingAddress2Label" 
                                path="accountDto.billingAddress.locationAddress2" maxlength="100"
                                inputClass="js-billing js-billing-address-2"/>
                            <tags:inputNameValue nameKey=".billingCityLabel" path="accountDto.billingAddress.cityName" 
                                maxlength="32" inputClass="js-billing js-billing-city"/>
                            <tags:inputNameValue nameKey=".billingStateLabel" path="accountDto.billingAddress.stateCode" 
                                size="3" maxlength="2" inputClass="js-billing js-billing-state"/>
                            <tags:inputNameValue nameKey=".billingZipLabel" path="accountDto.billingAddress.zipCode" 
                                maxlength="12" inputClass="js-billing js-billing-zip"/>
                            <form:hidden path="accountDto.billingAddress.county" maxlength="30" 
                                inputClass="js-billing js-billing-county"/>
                            
                            <%-- For temporary storage of billing previous values, 
                                 has no impact on actual form processing. --%>
                            <input type="hidden" class="js-temp-billing-address-1" 
                                value="${fn:escapeXml(accountGeneral.accountDto.billingAddress.locationAddress1)}">
                            <input type="hidden" class="js-temp-billing-address-2" 
                                value="${fn:escapeXml(accountGeneral.accountDto.billingAddress.locationAddress2)}">
                            <input type="hidden" class="js-temp-billing-city" 
                                value="${fn:escapeXml(accountGeneral.accountDto.billingAddress.cityName)}">
                            <input type="hidden" class="js-temp-billing-state" 
                                value="${fn:escapeXml(accountGeneral.accountDto.billingAddress.stateCode)}">
                            <input type="hidden" class="js-temp-billing-zip" 
                                value="${fn:escapeXml(accountGeneral.accountDto.billingAddress.zipCode)}">
                            
                        </tags:nameValueContainer2>
                        
                    </tags:sectionContainer2>
                    
                    <c:if test="${showLoginSection}">
                    
                        <%-- LOGIN INFO --%>
                        <tags:sectionContainer2 nameKey="userInfoSection" styleClass="stacked">
                            
                            <tags:nameValueContainer2>
                                
                                <tags:nameValue2 excludeColon="true">
                                    <label>
                                        <tags:checkbox path="loginBackingBean.loginEnabled"/>
                                        <i:inline key=".loginEnabled"/>
                                    </label>
                                </tags:nameValue2>
                                
                                <cti:msg2 var="none" key="yukon.common.none.choice"/>
                                <tags:selectNameValue nameKey=".customerGroup" 
                                    path="loginBackingBean.userGroupName" items="${ecResidentialUserGroups}" 
                                    itemValue="userGroupName" itemLabel="userGroupName" defaultItemLabel="${none}"
                                    inputClass="group-dropdown"/>
                                
                                <!-- Username Field -->
                                <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_USERNAME">
                                    <tags:nameValue2 nameKey=".userName">
                                        <tags:input path="loginBackingBean.username" autocomplete="off"/>
                                    </tags:nameValue2>
                                </cti:checkRolesAndProperties>
                                <cti:checkRolesAndProperties value="!OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_USERNAME">
                                    <tags:hidden path="loginBackingBean.username"/>
                                    <tags:nameValue2 nameKey=".userName">
                                        ${fn:escapeXml(accountGeneral.loginBackingBean.username)}
                                    </tags:nameValue2>
                                </cti:checkRolesAndProperties>
                                
                                <%-- The corresponding dialog (which contains a form) is at the end of the page.
                                this was done to avoid the confusion of an apparent nested form.  $ UI
                                (as of 1.8.16) will remove the specified element from the DOM create a new element
                                that represents the dialog at the end of the DOM and put the specified element inside
                                thus avoiding a nested form, but we are not guaranteed this to be true in the future.  --%>
                                <cti:displayForPageEditModes modes="EDIT">
                                    <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_PASSWORD">
                                        <c:if test="${not empty login}">
                                            <tags:nameValue2 nameKey=".password">
                                                <a href="javascript:void(0);" data-popup="#change-password-popup" 
                                                    class="js-reset-password-dialog"
                                                ><i:inline key=".changePassword"/></a>
                                            </tags:nameValue2>
                                        </c:if>
                                    </cti:checkRolesAndProperties>
                                </cti:displayForPageEditModes>
                                
                                <cti:displayForPageEditModes modes="CREATE,EDIT">
                                    <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_PASSWORD">
                                        <c:if test="${empty login}">
                                            
                                            <!-- IE does not support changing the 'type' attribute on input fields.
                                                 hence, what you see below in regards to the duplicate password fields -->
                                            <div class="password_editor">
                                            <tags:nameValue2 nameKey=".newPassword">
                                                <tags:password path="loginBackingBean.password1" showPassword="true" includeShowHideButton="false" cssClass="js-password-editor-field"/>
                                            </tags:nameValue2>
                                            <tags:nameValue2 nameKey=".confirmPassword">
                                                <tags:password path="loginBackingBean.password2" showPassword="true" includeShowHideButton="false" cssClass="js-password-editor-field"/>
                                            </tags:nameValue2>
                                        </c:if>
                                    </cti:checkRolesAndProperties>
                                </cti:displayForPageEditModes>
                            </tags:nameValueContainer2>
                            
                            <cti:displayForPageEditModes modes="CREATE,EDIT">
                                <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_PASSWORD">
                                    <c:if test="${empty login}">
                                        <div class="action-area">
                                            <label>
                                                <input class="js-show-password-checkbox" type="checkbox">
                                                <i:inline key=".showPassword"/>
                                            </label>
                                            <cti:msg2 key=".generatePassword" var="generatePword"/>
                                            <cti:button classes="js-generatePassword" label="${generatePword}"/>
                                        </div>
                                    </c:if>
                                </cti:checkRolesAndProperties>
                            </cti:displayForPageEditModes>
                            
                            <cti:displayForPageEditModes modes="EDIT">
                                <c:if test="${loginMode eq 'EDIT'}">
                                    <div class="action-area">
                                        <cti:button nameKey="delete.user" data-form="#deleteUserForm" 
                                            classes="js-delete-user delete"/>
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
                            <cti:button nameKey="save" type="submit" busy="true" 
                                classes="js-prep-password-fields primary action"/>
                            <cti:button nameKey="delete" classes="js-delete delete" data-form="#deleteAccountForm"/>
                             <cti:url value="/stars/operator/account/view" var="viewUrl">
                                <cti:param name="accountId" value="${accountId}"/>
                            </cti:url>
                            <cti:button nameKey="cancel" href="${viewUrl}"/>
                        </cti:displayForPageEditModes>
                    </cti:checkRolesAndProperties>
                    
                    <cti:displayForPageEditModes modes="CREATE">
                        <cti:button nameKey="create" type="submit" busy="true"
                            classes="js-prep-password-fields primary action"/>
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
                <c:if test="${not empty login}">
                    <cti:url var="url" value="/stars/operator/account/updatePassword"/>
                    <div class="dn" id="change-password-popup" data-dialog
                        data-title="<cti:msg2 key=".changePassword"/>" 
                        data-event="yukon:assets:account:password:save"
                        data-ok-text="<cti:msg2 key="yukon.common.save"/>">
                        <form:form id="change-password-form" modelAttribute="login" action="${url}">
                            <cti:csrfToken/>
                            <input type="hidden" name="accountId" value="${accountId}">
                            <input type="hidden" name="loginMode" value="${loginMode}">
                            
                            <form:hidden path="userId" cssClass="js-user-id"/>
                            <form:hidden path="userGroupName" cssClass="js-user-group"/>
                            <form:hidden path="loginEnabled"/>
                            <form:hidden path="username"/>
                            
                            <tags:nameValueContainer2 id="passwordFields">
                                <tags:nameValue2 nameKey=".newPassword">
                                    <input type="text" class="js-password-editor-field" 
                                        autocomplete="off" maxlength="64" style="display:none;">
                                    <tags:password path="password1" cssClass="js-password-editor-field js-password-1" 
                                        autocomplete="off" maxlength="64"/>
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".confirmPassword">
                                    <input type="text" class="js-password-editor-field" 
                                        autocomplete="off" maxlength="64" style="display:none;">
                                    <tags:password path="password2" cssClass="js-password-editor-field js-password-2" 
                                        autocomplete="off" maxlength="64"/>
                                </tags:nameValue2>
                            </tags:nameValueContainer2>
                            <div class="action-area">
                                <label>
                                    <input class="js-show-password-checkbox" type="checkbox">
                                    <i:inline key=".showPassword"/>
                                </label>
                                <cti:msg2 key=".generatePassword" var="generatePword"/>
                                <cti:button classes="js-generatePassword" label="${generatePword}"/>
                            </div>
                        </form:form>
                    </div>
                </c:if>
            </cti:checkRolesAndProperties>
        </cti:displayForPageEditModes>
        
    </cti:checkEnergyCompanyOperator>
</cti:standardPage>