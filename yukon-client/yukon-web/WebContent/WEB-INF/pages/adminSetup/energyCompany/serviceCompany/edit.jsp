<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="serviceCompany.${mode}">
    <cti:url var="userInfoUrl" value="${baseUrl}/userInfo"/>
    <cti:includeCss link="/WebConfig/yukon/styles/admin/energyCompany.css"/>
    
        <script>
        
            /*
                User is an array, however we are using multiSelectMode="false" in the picker
                so it is reasonable to assume that we only care about the first item.  Still,
                this feels bad.
            */
            var DC_INDEX = 0;
            var FIND_ZIP_MSG = "<cti:msg key="yukon.web.modules.adminSetup.serviceCompany.findZipCode" javaScriptEscape="true" />";
            
            function removeDesignationCode(event, elem) {
                event.stop();
                $(event.target).up(1).remove();
            }
            
            function addDesignationCode() {
                var designationCode =  $('newDesignationCode').value;
                var str = [];
                str.push('<li>');
                    str.push('<span class="remove fr">');
                        str.push('<a class="simpleLink icon icon_remove" title="" href="javascript:removeDesignationCode()">remove</a>');
                    str.push('</span>');
                    str.push(designationCode);
                    str.push('<input type="hidden" value="0" name="designationCodes['+ ++DC_INDEX +'].id"/>');
                    str.push('<input type="hidden" value="'+ designationCode +'" name="designationCodes['+ DC_INDEX +'].value"/>');
                    str.push('<input type="hidden" value="${serviceCompany.companyId}" name="designationCodes['+ DC_INDEX +'].serviceCompanyId"/>');
                str.push('</li>');
                
                $('designationCodes').insert(str.join(''));
                $('newDesignationCode').value = "";
                $('addDesignationCodeButton').disable();
            }
            
            Event.observe(window, 'load', function(){
                //get the number of designation codes.  we don't 'really' care about the order,
                //however spring needs the list to be uniquely indexed
                DC_INDEX = $$('#designationCodes li').length;
                
                if($('addDesignationCodeButton')) {
                    Event.observe($('addDesignationCodeButton'), 'click', function(event) {
                        Event.stop(event);
                        addDesignationCode(event);
                    });
                    $('addDesignationCodeButton').disable();
                }
                
                if($('newDesignationCode')) {
                    Event.observe('newDesignationCode', 'keyup', function(e){
                        Event.stop(e);
                        if(this.value.length == 0) {
                            $('addDesignationCodeButton').disable();
                        } else {
                            $('addDesignationCodeButton').enable();
                            if(e.keyCode == 13) {
                                addDesignationCode();
                            }
                        }
                        return false;
                    });
                }
                
                
                Event.observe('findDesignationCode', 'focus', function(){
                   this.value = "";
                   this.removeClassName('default');
                });
                
                Event.observe('findDesignationCode', 'blur', function(){
                    this.value = FIND_ZIP_MSG;
                    this.addClassName('default');
                 });
                
                Event.observe('findDesignationCode', 'keyup', function(){
                    //search list for this zip
                    var find = this.value;
                    
                    if(find.length > 0) {
                        var found = false;
                        $("designationCodes").addClassName('filtering');
                        
                        $$("#designationCodes li input:hidden[name*=.value]").each(function(obj, iter){
                            if(obj.value.startsWith(find)) {
                                // add filtered class to parent li
                                obj.up().addClassName('filtered');
                                found = true;
                            } else {
                                // remove filtered class from parent li
                                obj.up().removeClassName('filtered');
                            }
                        });
                        
                        if(found) {
                            $("findDesignationCode").addClassName("found");
                            $("findDesignationCode").removeClassName("notFound");
                        } else {
                            $("findDesignationCode").removeClassName("found");
                            $("findDesignationCode").addClassName("notFound");
                        }
                        
                    } else {
                        $("findDesignationCode").removeClassName("found");
                        $("findDesignationCode").removeClassName("notFound");
                        $("designationCodes").removeClassName('filtering');
                    }
                 });
                
                function preventSubmit(e) {
                    if(e.keyCode == 13) {
                        Event.stop(e);
                    }
                };
                
                if($('newDesignationCode')) {
                    Event.observe('newDesignationCode', 'keydown', preventSubmit);
                }
                
                Event.observe('findDesignationCode', 'keydown', preventSubmit);
                
                YEvent.observeSelectorClick('a.icon_remove', removeDesignationCode);
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
                <tags:formElementContainer nameKey="generalInfoSection">
                    <tags:nameValueContainer2 id="generalInfoTable">
                        <tags:inputNameValue nameKey=".name" path="companyName" inputClass="string" size="60" maxlength="60"/>
                        <tags:inputNameValue nameKey=".mainPhone" path="mainPhoneNumber" inputClass="phone" maxlength="14"/>
                        <tags:inputNameValue nameKey=".mainFax" path="mainFaxNumber" inputClass="phone" maxlength="14"/>
                        <tags:inputNameValue nameKey=".email" path="emailContactNotification" inputClass="String" maxlength="130"/>
                        <tags:inputNameValue nameKey=".HIType" path="hiType" size="40" maxlength="40"/>
                    </tags:nameValueContainer2>
                </tags:formElementContainer>
                <br/>
                <br/>
                <c:if test="${allowContractorZipCodes}">
                    <tags:formElementContainer nameKey="designationCodeSection">
                        <!-- Contractor Zip Codes -->
                        <input type="text" id="findDesignationCode" value="<cti:msg key="yukon.web.modules.adminSetup.serviceCompany.findZipCode" javaScriptEscape="true" />" class="default" />
                        <div class="vertical_scrollbox search_list">
                        <ul id="designationCodes">
                            <c:forEach items="${serviceCompany.designationCodes}" varStatus="row">
                                <!-- If we are editing and error any designation codes pending deletion will exist in the list
                                     this ensures that we do not have a visual representation of that -->
                                <c:if
                                    test="${not empty serviceCompany.designationCodes[row.index].value}">
                                    <li><cti:displayForPageEditModes modes="EDIT,CREATE">
                                        <span class="remove fr"><a href="#" class="remove icon icon_remove simpleLink">remove</a></span>
                                        <span class="value"><spring:escapeBody htmlEscape="true">${serviceCompany.designationCodes[row.index].value}</spring:escapeBody></span>
                                        <tags:hidden path="designationCodes[${row.index}].id" />
                                        <tags:hidden path="designationCodes[${row.index}].value" />
                                        <tags:hidden path="designationCodes[${row.index}].serviceCompanyId" />
                                    </cti:displayForPageEditModes> <cti:displayForPageEditModes modes="VIEW">
                                        <span class="value"><spring:escapeBody htmlEscape="true">${serviceCompany.designationCodes[row.index].value}</spring:escapeBody></span>
                                        <tags:hidden path="designationCodes[${row.index}].value" />
                                    </cti:displayForPageEditModes></li>
                                </c:if>
                            </c:forEach>
                        </ul>
                        </div>

                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <div><input type="text" maxlength=60 id="newDesignationCode"
                                class="zip"> <cti:button key="assignDesignationCode"
                                id="addDesignationCodeButton" /></div>
                        </cti:displayForPageEditModes>
                    </tags:formElementContainer>
                </c:if>
            </cti:dataGridCell>
            
            <cti:dataGridCell>                        
                <tags:formElementContainer nameKey="addressSection">
                    <tags:nameValueContainer2>
                        <tags:hidden path="address.addressID"/>
                        <tags:inputNameValue nameKey=".locationAddress1" path="address.locationAddress1" inputClass="string" maxlength="40"/>
                        <tags:inputNameValue nameKey=".locationAddress2" path="address.locationAddress2" inputClass="string" maxlength="40"/>
                        <tags:inputNameValue nameKey=".cityName" path="address.cityName"  inputClass="string" maxlength="32"/>
                        <tags:inputNameValue nameKey=".stateCode" path="address.stateCode" inputClass="state" maxlength="2"/>
                        <tags:inputNameValue nameKey=".zipCode" path="address.zipCode" inputClass="zip" maxlength="12"/>
                        <tags:hidden path="address.county"/>
                    </tags:nameValueContainer2>
                </tags:formElementContainer>
                <br/>
                <br/>
                <tags:formElementContainer nameKey="primaryContactSection">
                    <tags:nameValueContainer2>
                        <tags:hidden path="primaryContact.contactID"/>
                        <tags:inputNameValue nameKey=".primaryContact.firstName" path="primaryContact.contFirstName" inputClass="string" maxlength="120"/>
                        <tags:inputNameValue nameKey=".primaryContact.lastName" path="primaryContact.contLastName" inputClass="string" maxlength="120"/>
                        <tags:selectNameValue items="${availableLogins}" itemLabel="username" itemValue="userID"
                                    nameKey=".availableLogins" path="primaryContact.loginID" />
                    </tags:nameValueContainer2>
                </tags:formElementContainer>
            </cti:dataGridCell>
        </cti:dataGrid>
        
        <div class="pageActionArea">
        
            <!-- Edit Link -->
            <cti:displayForPageEditModes modes="VIEW">
                <cti:url var="serviceCompanyEditUrl" value="${baseUrl}/edit">
                    <cti:param name="ecId" value="${ecId}"/>
                    <cti:param name="serviceCompanyId" value="${serviceCompany.companyId}"/>
                </cti:url>
                <cti:button key="edit" href="${serviceCompanyEditUrl}"/>
            </cti:displayForPageEditModes>
            
            <!-- Save/Update -->
            <cti:displayForPageEditModes modes="CREATE">
                <cti:button key="save" name="create" type="submit"/>
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="EDIT">
                <cti:button key="save" name="update" type="submit"/>
            </cti:displayForPageEditModes>
            
            <!-- Cancel -->
            <cti:url var="serviceCompanyIndexUrl" value="${baseUrl}/list">
                <cti:param name="ecId" value="${ecId}"/>
            </cti:url>
            <cti:button key="cancel" href="${serviceCompanyIndexUrl}"/>
        
            <cti:displayForPageEditModes modes="EDIT">
                <cti:button key="delete" styleClass="delete"/>
                <tags:confirmDialog nameKey="confirmDelete" id="delete" submitName="delete" on="button.delete" />
            </cti:displayForPageEditModes>
        </div>
     </form:form>
  
</cti:standardPage>