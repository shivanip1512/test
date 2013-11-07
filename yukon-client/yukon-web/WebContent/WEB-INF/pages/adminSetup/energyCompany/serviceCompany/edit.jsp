<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>

<cti:standardPage module="adminSetup" page="serviceCompany.${mode}">
    <cti:url var="userInfoUrl" value="${baseUrl}/userInfo"/>
    
        <script>
        
            /*
                User is an array, however we are using multiSelectMode="false" in the picker
                so it is reasonable to assume that we only care about the first item.  Still,
                this feels bad.
            */
            var DC_INDEX = 0;
            var FIND_ZIP_MSG = "<cti:msg key="yukon.web.modules.adminSetup.serviceCompany.findZipCode" javaScriptEscape="true" />";
            
            function removeDesignationCode(event, elem) {
                event.stopPropagation();
                // ul > li > span > a > i event originates with the i element (the red X), we want to remove the li
                jQuery(event.target).parent().parent().parent().remove();
            }
            
            function addDesignationCode() {
                var designationCode =  jQuery('#newDesignationCode').val(),
                    str = [],
                    curMarkup = jQuery('#designationCodes').html();
                str.push('<li>');
                    str.push('<span class="remove fr">');
                    str.push("<a class='f-remove' href='javascript:removeDesignationCode()'><span class='icon icon-cross'></span></a>");
                    str.push('</span>');
                    str.push(designationCode);
                    str.push('<input type="hidden" value="0" name="designationCodes['+ ++DC_INDEX +'].id"/>');
                    str.push('<input type="hidden" value="'+ designationCode +'" name="designationCodes['+ DC_INDEX +'].value"/>');
                    str.push('<input type="hidden" value="${serviceCompany.companyId}" name="designationCodes['+ DC_INDEX +'].serviceCompanyId"/>');
                str.push('</li>');
                
                jQuery('#designationCodes').html(curMarkup + str.join(''));
                jQuery('#newDesignationCode').val('');
                jQuery('#addDesignationCodeButton').prop({'disabled': true});
            }
            
            jQuery(function() {
                //get the number of designation codes.  we don't 'really' care about the order,
                //however spring needs the list to be uniquely indexed
                DC_INDEX = jQuery('#designationCodes li').length;
                
                if (jQuery('#addDesignationCodeButton').length) {
                    jQuery('#addDesignationCodeButton').on('click', function (event) {
                        event.stopPropagation();
                        addDesignationCode(event);
                    });
                    jQuery('#addDesignationCodeButton').prop({'disabled': true});
                }
                
                if (jQuery('#newDesignationCode').length) {
                    jQuery('#newDesignationCode').on('keyup', function (e) {
                        e.stopPropagation();
                        if (this.value.length == 0) {
                            jQuery('#addDesignationCodeButton').prop({'disabled': true});
                        } else {
                            jQuery('#addDesignationCodeButton').prop({'disabled': false});
                            if (e.keyCode == 13) {
                                addDesignationCode();
                            }
                        }
                        return false;
                    });
                }
                
             // TODO: the classes used below are not declared anywhere
                jQuery('#findDesignationCode').on('focus', function () {
                    if (jQuery(this).hasClass('default')) {
                       this.value = "";
                       this.removeClassName('default');
                    }
                });
                
                jQuery('#findDesignationCode').on('blur', function () {
                    if (this.value.length == 0) {
                        this.value = FIND_ZIP_MSG;
                        this.addClassName('default');
                        jQuery(this).removeClass('filtered').removeClass('found').removeClass('notFound');
                        jQuery('#designationCodes').removeClass('filtering');
                        jQuery('#designationCodes li').each (function (index, obj) {
                            jQuery(obj).removeClass('filtered');
                        });
                    }
                 });
                
                jQuery('#findDesignationCode').on('keyup', function () {
                    //search list for this zip
                    var find = this.value,
                        found = false;
                    
                    if (find.length > 0) {
                        jQuery('#designationCodes').addClass('filtering');
                        
                        jQuery('#designationCodes li input:hidden[name*=".value"]').each(function (index, obj) {
                            if (0 === jQuery(obj).val().indexOf(find, 0)) {
                                // add filtered class to parent li
                                jQuery(obj).parent().addClass('filtered');
                                found = true;
                            } else {
                                // remove filtered class from parent li
                                jQuery(obj).parent().removeClass('filtered');
                            }
                        });
                        
                        if (found) {
                            jQuery('#findDesignationCode').addClass('found');
                            jQuery('#findDesignationCode').removeClass('notFound');
                        } else {
                            jQuery('#findDesignationCode').removeClass('found');
                            jQuery('#findDesignationCode').addClass('notFound');
                        }
                        
                    } else {
                        jQuery('#findDesignationCode').removeClass('found');
                        jQuery('#findDesignationCode').removeClass('notFound');
                        jQuery('#designationCodes').removeClass('filtering');
                    }
                 });
                
                function preventSubmit(e) {
                    if(e.keyCode == 13) {
                        e.stopPropagation();
                    }
                };
                
                if(jQuery('#newDesignationCode').length) {
                    jQuery('#newDesignationCode').on('keydown', preventSubmit);
                }
                
                jQuery('findDesignationCode').on('keydown', preventSubmit);
                
                jQuery(document).on('click', '.remove.fr', removeDesignationCode);
            });
        </script>

     <tags:setFormEditMode mode="${mode}"/>
     
    <cti:url value="${baseUrl}/update" var="action">
        <cti:param name="ecId" value="${ecId}"/>
    </cti:url>
    
     <form:form commandName="serviceCompany" action="${action}" name="serviceCompanyForm">
        
        <tags:hidden path="companyId"/>
            
        <cti:dataGrid cols="2" rowStyle="vertical-align:top;" cellStyle="padding-right:20px;">
        
            <%-- General Info --%>
            <cti:dataGridCell>
                <tags:sectionContainer2 nameKey="generalInfoSection">
                    <tags:nameValueContainer2 id="generalInfoTable">
                        <tags:inputNameValue nameKey=".name" path="companyName" inputClass="string" size="45" maxlength="60"/>
                        <tags:inputPhone nameKey=".mainPhone" path="mainPhoneNumber" inputClass="f-formatPhone" maxlength="16"/>
                        <tags:inputPhone nameKey=".mainFax" path="mainFaxNumber" inputClass="f-formatPhone"  maxlength="16"/>
                        <tags:inputNameValue nameKey=".email" path="emailContactNotification" inputClass="String" maxlength="130"/>
                        <tags:inputNameValue nameKey=".HIType" path="hiType" size="35" maxlength="40"/>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
                <br/>
                <br/>
                <c:if test="${canViewDesignationCodes}">
                    <tags:sectionContainer2 nameKey="designationCodeSection">
                        <!-- Contractor Zip Codes -->
                        <input type="text" id="findDesignationCode" value="<cti:msg key="yukon.web.modules.adminSetup.serviceCompany.findZipCode" javaScriptEscape="true" />" class="default" />
                        <div class="vertical_scrollbox search_list">
                        <ul id="designationCodes">
                            <c:forEach items="${serviceCompany.designationCodes}" varStatus="row">
                                <!-- If we are editing and error any designation codes pending deletion will exist in the list
                                     this ensures that we do not have a visual representation of that -->
                                <c:if
                                    test="${not empty serviceCompany.designationCodes[row.index].value}">
                                    <li>
                                    <cti:displayForPageEditModes modes="EDIT,CREATE">
                                        <c:if test="${canEditDesignationCodes}">
                                            <span class="remove fr"><a href="#" class="remove"><i class="icon icon-cross"></i></a></span>
                                        </c:if>
                                        <span class="value"><spring:escapeBody htmlEscape="true">${serviceCompany.designationCodes[row.index].value}</spring:escapeBody></span>
                                        <tags:hidden path="designationCodes[${row.index}].id" />
                                        <tags:hidden path="designationCodes[${row.index}].value" />
                                        <tags:hidden path="designationCodes[${row.index}].serviceCompanyId" />
                                    </cti:displayForPageEditModes>
                                    <cti:displayForPageEditModes modes="VIEW">
                                        <span class="value"><spring:escapeBody htmlEscape="true">${serviceCompany.designationCodes[row.index].value}</spring:escapeBody></span>
                                        <tags:hidden path="designationCodes[${row.index}].value" />
                                    </cti:displayForPageEditModes>
                                    </li>
                                </c:if>
                            </c:forEach>
                        </ul>
                        </div>

                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <c:if test="${canEditDesignationCodes}">
                            <div><input type="text" maxlength=60 id="newDesignationCode"
                                class="zip"> <cti:button nameKey="assignDesignationCode"
                                id="addDesignationCodeButton" /></div>
                            </c:if>
                        </cti:displayForPageEditModes>
                    </tags:sectionContainer2>
                </c:if>
            </cti:dataGridCell>
            
            <cti:dataGridCell>                        
                <tags:sectionContainer2 nameKey="addressSection">
                    <tags:nameValueContainer2>
                        <tags:hidden path="address.addressID"/>
                        <tags:inputNameValue nameKey=".locationAddress1" path="address.locationAddress1" inputClass="string" maxlength="40"/>
                        <tags:inputNameValue nameKey=".locationAddress2" path="address.locationAddress2" inputClass="string" maxlength="40"/>
                        <tags:inputNameValue nameKey=".cityName" path="address.cityName"  inputClass="string" maxlength="32"/>
                        <tags:inputNameValue nameKey=".stateCode" path="address.stateCode" inputClass="state" maxlength="2"/>
                        <tags:inputNameValue nameKey=".zipCode" path="address.zipCode" inputClass="zip" maxlength="12"/>
                        <tags:hidden path="address.county"/>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
                <br/>
                <br/>
                <tags:sectionContainer2 nameKey="primaryContactSection">
                    <tags:nameValueContainer2>
                        <tags:hidden path="primaryContact.contactID"/>
                        <tags:inputNameValue nameKey=".primaryContact.firstName" path="primaryContact.contFirstName" inputClass="string" maxlength="120"/>
                        <tags:inputNameValue nameKey=".primaryContact.lastName" path="primaryContact.contLastName" inputClass="string" maxlength="120"/>
                        <tags:selectNameValue items="${availableLogins}" itemLabel="username" itemValue="userID"
                                    nameKey=".availableUsers" path="primaryContact.loginID" />
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
            </cti:dataGridCell>
        </cti:dataGrid>
        
        <div class="page-action-area">
        
            <!-- Edit Link -->
            <cti:displayForPageEditModes modes="VIEW">
                <cti:url var="serviceCompanyEditUrl" value="${baseUrl}/edit">
                    <cti:param name="ecId" value="${ecId}"/>
                    <cti:param name="serviceCompanyId" value="${serviceCompany.companyId}"/>
                </cti:url>
                <cti:button nameKey="edit" icon="icon-pencil" href="${serviceCompanyEditUrl}"/>
            </cti:displayForPageEditModes>
            
            <!-- Save/Update -->
            <cti:displayForPageEditModes modes="CREATE">
                <cti:button nameKey="save" name="create" type="submit" classes="primary action"/>
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="EDIT">
                <cti:button nameKey="save" name="update" type="submit"/>
                <cti:button id="deleteButton" nameKey="delete" name="delete" type="submit" />
                <d:confirm on="#deleteButton" nameKey="confirmDelete" argument="${numberOfInventoryInServiceCompany}"/>
            </cti:displayForPageEditModes>

            <!-- Cancel -->
            <cti:url var="serviceCompanyIndexUrl" value="${baseUrl}/list">
                <cti:param name="ecId" value="${ecId}"/>
            </cti:url>
            <cti:button nameKey="cancel" href="${serviceCompanyIndexUrl}"/>
        </div>
     </form:form>
  
</cti:standardPage>