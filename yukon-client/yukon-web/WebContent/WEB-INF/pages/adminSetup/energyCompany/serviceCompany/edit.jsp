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
            // 'eq true' forces a boolean output even if EL var is empty
            var canEditDesignationCodes = ${canEditDesignationCodes eq true};

            function addDesignationCode() {
                var designationCode =  jQuery('#newDesignationCode').val();

                var str = [];
                str.push('<div class="f-designation-code f-removable-designation-code">');
                str.push('<span class="f-remove-designation-code fr"><a href="#"><i class="icon icon-cross"></i></a></span>');
                str.push(designationCode);
                str.push('<input type="hidden" value="0" name="designationCodes[].id"/>');
                str.push('<input type="hidden" value="'+ designationCode +'" name="designationCodes[].value" class="f-designation-code-value"/>');
                str.push('<input type="hidden" value="${serviceCompany.companyId}" name="designationCodes[].serviceCompanyId"/>');
                str.push('</div>');

                jQuery('#designationCodes').append(str.join(''));
                jQuery('#newDesignationCode').val('');
                adjustIndexes();
            }

            // adjust indexes to be sequential for java List
            function adjustIndexes() {
                jQuery('#designationCodes').children(".f-designation-code").each(function (index) {
                    jQuery(this).find(":input").each(function() {
                        var name = jQuery(this).attr("name").replace(/\[.*?\]/, "["+index+"]");
                        jQuery(this).attr("name", name);
                    });
                });
            }

            jQuery(function() {
                if (canEditDesignationCodes) {
                    jQuery('#addDesignationCodeButton').on('click', function (event) {
                        event.stopPropagation();
                        addDesignationCode();
                        jQuery('#addDesignationCodeButton').prop('disabled', true);
                    });

                    jQuery('#newDesignationCode').on('keyup', function (e) {
                        e.stopPropagation();
                        if (this.value.length === 0) {
                            jQuery('#addDesignationCodeButton').prop('disabled', true);
                        } else {
                            if (e.keyCode === 13) {
                                addDesignationCode();
                                jQuery('#addDesignationCodeButton').prop('disabled', true);
                            } else {
                                jQuery('#addDesignationCodeButton').prop('disabled', false);
                            }
                        }
                        return false;
                    });

                    jQuery('#newDesignationCode').on('keydown', preventSubmitForEnter);
                }

                jQuery('#findDesignationCode').on('keyup', function () {
                    //search list for this zip
                    var zipCodeToFind = this.value;
                    var found = false;
                    
                    if (zipCodeToFind.length > 0) {
                        jQuery('#designationCodes').children('.f-designation-code')
                            .find('.f-designation-code-value').each(function () {
                                var thisCode = this.value;
                                if(0 === thisCode.indexOf(zipCodeToFind, 0)) {
                                    found = true;
                                    jQuery(this).parents('.f-designation-code')
                                        .addClass('success fwb')
                                        .removeClass('disabled');
                                } else {
                                    jQuery(this).parents('.f-designation-code')
                                        .addClass('disabled')
                                        .removeClass('success fwb');
                                }
                            });

                        if (found) {
                            jQuery('#findDesignationCode').addClass('success').removeClass('error');
                        } else {
                            jQuery('#findDesignationCode').addClass('error').removeClass('success');
                        }

                    } else {
                        jQuery('#findDesignationCode').removeClass('error success');
                        jQuery('.f-designation-code').removeClass('disabled error success fwb');
                    }
                     });

                function preventSubmitForEnter(e) {
                    if(e.keyCode === 13) {
                        e.stopPropagation();
                        return false;
                    }
                };

                jQuery('#findDesignationCode').on('keydown', preventSubmitForEnter);
                jQuery('#serviceCompanyForm').on('click', '.f-remove-designation-code', function() {
                    jQuery(this).parent('.f-removable-designation-code').slideUp(150)
                        .promise().done(function () {
                            this.remove();
                            adjustIndexes();
                        });
                });
            });
        </script>

     <tags:setFormEditMode mode="${mode}"/>
     
    <cti:url value="${baseUrl}/update" var="action">
        <cti:param name="ecId" value="${ecId}"/>
    </cti:url>
    
     <form:form id="serviceCompanyForm" commandName="serviceCompany" action="${action}" name="serviceCompanyForm">
        
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
                        <input type="text" id="findDesignationCode"
                            placeholder="<cti:msg key="yukon.web.modules.adminSetup.serviceCompany.findZipCode"/>"
                            autocomplete="off"/>
                        <div class="column-10-14 clearfix">
                            <div class="column one">
                                <div id="designationCodes" class="scroll-small">
                                    <c:forEach items="${serviceCompany.designationCodes}" varStatus="row">
                                        <%-- If we are editing and error any designation codes pending deletion will exist in the list
                                             this ensures that we do not have a visual representation of that --%>
                                        <c:if test="${not empty serviceCompany.designationCodes[row.index].value}">
                                            <div class="f-designation-code f-removable-designation-code">
                                                <cti:displayForPageEditModes modes="EDIT,CREATE">
                                                    <c:if test="${canEditDesignationCodes}">
                                                        <span class="f-remove-designation-code fr"><a href="#"><i class="icon icon-cross"></i></a></span>
                                                    </c:if>
                                                    ${fn:escapeXml(serviceCompany.designationCodes[row.index].value)}
                                                    <input type="hidden" name="designationCodes[${row.index}].id" 
                                                        value="${serviceCompany.designationCodes[row.index].id}"/>
                                                    <input type="hidden"
                                                        name="designationCodes[${row.index}].value"
                                                        class="f-designation-code-value"
                                                        value="${serviceCompany.designationCodes[row.index].value}"/>
                                                    <input type="hidden" name="designationCodes[${row.index}].serviceCompanyId"
                                                        value="${serviceCompany.designationCodes[row.index].serviceCompanyId}"/>
                                                </cti:displayForPageEditModes>
                                                <cti:displayForPageEditModes modes="VIEW">
                                                    ${fn:escapeXml(serviceCompany.designationCodes[row.index].value)}
                                                    <input type="hidden"
                                                        name="designationCodes[${row.index}].value"
                                                        class="f-designation-code-value"
                                                        value="${serviceCompany.designationCodes[row.index].value}"/>
                                                </cti:displayForPageEditModes>
                                            </div>
                                        </c:if>
                                    </c:forEach>
                                </div>
                            </div>
                            <div class="column two nogutter">
                                <%-- empty --%>
                            </div>
                        </div>
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <c:if test="${canEditDesignationCodes}">
                                <input type="text" maxlength=60 id="newDesignationCode" class="fl" autocomplete="off">
                                <cti:button id="addDesignationCodeButton" nameKey="assignDesignationCode" disabled="true"/>
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
                <cti:button nameKey="save" name="create" type="submit" classes="primary action" busy="true"/>
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="EDIT">
                <cti:button nameKey="save" name="update" type="submit" classes="primary action" busy="true"/>
                <cti:button id="deleteButton" nameKey="delete" name="delete" type="submit" classes="delete"/>
                <d:confirm on="#deleteButton" nameKey="confirmDelete" argument="${numberOfInventoryInServiceCompany}"/>
            </cti:displayForPageEditModes>
            
            <!-- Cancel -->
            <cti:displayForPageEditModes modes="CREATE,EDIT">
                <cti:url var="serviceCompanyIndexUrl" value="${baseUrl}/list">
                    <cti:param name="ecId" value="${ecId}"/>
                </cti:url>
                <cti:button nameKey="cancel" href="${serviceCompanyIndexUrl}"/>
            </cti:displayForPageEditModes>
            
            <!-- Back -->
            <cti:displayForPageEditModes modes="VIEW">
                <cti:url var="serviceCompanyIndexUrl" value="${baseUrl}/list">
                    <cti:param name="ecId" value="${ecId}"/>
                </cti:url>
                <cti:button nameKey="back" href="${serviceCompanyIndexUrl}"/>
            </cti:displayForPageEditModes>
        </div>
     </form:form>
  
</cti:standardPage>