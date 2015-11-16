<%@ page import="com.cannontech.multispeak.client.*" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="adminSetup"  page="interfaces">

<cti:linkTabbedContainer mode="section" id="page_header_tab_container">
    <cti:linkTab tabId="deviceTab" selectorKey="yukon.web.modules.adminSetup.interfaces.home.tab.title" initiallySelected="${true}">
        <c:url value="/multispeak/setup/home" />
    </cti:linkTab>

    <cti:checkGlobalSetting setting="MSP_LM_MAPPING_SETUP">
        <cti:linkTab tabId="loadMgtTab" selectorKey="yukon.web.modules.adminSetup.lmMappings.tab.title">
            <c:url value="/multispeak/setup/lmMappings/home" />
        </cti:linkTab>
    </cti:checkGlobalSetting>

    <cti:linkTab tabId="deviceGroupTab" selectorKey="yukon.web.modules.adminSetup.deviceGroupSyncHome.tab.title">
        <c:url value="/multispeak/setup/deviceGroupSync/home" />
    </cti:linkTab>
</cti:linkTabbedContainer>

    <c:set var="interfaceListLength" value="${fn:length(possibleInterfaces)}" />
    
    <script type="text/javascript">
        function confirmDelete(){
            if (confirm("Are you sure you want to delete this interface?")) {
                $('#delete-form').submit();
            }
        }
        
        function enableEndpointValue(isNew, selected, interface) {
         if (!isNew) {
             if( selected ) {
                document.getElementById("mspPing"+interface).style.cursor = 'pointer';
                document.getElementById("mspMethods"+interface).style.cursor = 'pointer';
                document.getElementById("mspPing"+interface).href = 'JavaScript:serviceSubmit(\"'+interface+'\", \"pingURL\");'
                document.getElementById("mspMethods"+interface).href = 'JavaScript:document.mspForm.submit()'
            } else {
                document.getElementById("mspPing"+interface).style.cursor = 'default';
                document.getElementById("mspMethods"+interface).style.cursor = 'default';
                document.getElementById("mspPing"+interface).href = 'javascript:;'
                document.getElementById("mspMethods"+interface).href = 'javascript:;'
            }
         }
            document.getElementById("mspEndpoint"+interface).disabled = !selected;
        }
        
        function enableExtension(selected) {
            
            document.getElementById("mspPaoNameAliasExtension").disabled = !selected;
        }
        
        function serviceSubmit(service, value)
        {
          document.mspForm.actionService.value=service;
          document.mspForm.action = yukon.url("/multispeak/setup/" + value);
          document.mspForm.submit();
        }
        
        function dispStatusMsg(msgStr) { //v1.0
          status=msgStr;
          document.statVal = true;
        }
        
        function vendorChanged()
        {
            document.mspForm.action = yukon.url("/multispeak/setup/home");
            document.mspForm.submit();
        }

    </script>
    <cti:url var="saveUrl" value="/multispeak/setup/save"/>
    <form name="mspForm" method="post" action="${saveUrl}">
        <cti:csrfToken/>
        <input type="hidden" name="actionEndpoint">
        <input type="hidden" name="actionService">
        
        <tags:sectionContainer2 nameKey="mspSetup">
            <div class="column-12-12 clearfix">
                <div class="column one">
                    <tags:nameValueContainer>
                    
                        <c:choose>
                            <c:when test="${isCreateNew}">
                                <tags:nameValue name="Company Name*">
                                    <input type="hidden" name="isCreateNew" value="true">
                                    <input title="Enter the Company Name" type="text" name="mspCompanyName">
                                </tags:nameValue>
                            </c:when>
                            
                            <c:otherwise>
                                <tags:nameValue name="Company Name">
                                    <input type="hidden" name="mspCompanyName" value="${mspVendor.companyName}">
                                    <select title="Select vendor" name="mspVendorId" onChange="vendorChanged();">
                                        <c:forEach var="mspVendorEntry" items="${mspVendorList}">
                                            <option <c:if test="${mspVendorEntry.vendorID == mspVendor.vendorID}">selected</c:if> value='<c:out value="${mspVendorEntry.vendorID}"/>'> <spring:escapeBody htmlEscape="true">${mspVendorEntry.companyName}</spring:escapeBody></option>
                                        </c:forEach>
                                    </select>
                                </tags:nameValue>
                            </c:otherwise>
                        </c:choose>
                        
                        <tags:nameValue name="App Name">
                          <input title="Enter the Application Name" type="text" name="mspAppName" value='<spring:escapeBody htmlEscape="true">${mspVendor.appName}</spring:escapeBody>'>
                        </tags:nameValue>
                        
                        <tags:nameValue name="MSP UserName">
                          <input title="Enter the Username" type="text" name="mspUserName" value='<spring:escapeBody htmlEscape="true">${mspVendor.userName}</spring:escapeBody>'>
                        </tags:nameValue>
                        
                        <tags:nameValue name="MSP Password">
                          <input title="Enter the Password" type="text" name="mspPassword" value='<spring:escapeBody htmlEscape="true">${mspVendor.password}</spring:escapeBody>'>
                        </tags:nameValue>
                        
                        <tags:nameValue name="MSP Max Return Records*">
                          <input title="Enter the Max Return Records" type="text" name="mspMaxReturnRecords" value='<spring:escapeBody htmlEscape="true">${mspVendor.maxReturnRecords}</spring:escapeBody>'>
                        </tags:nameValue>
                        
                        <tags:nameValue name="MSP Request Message Timeout*">
                          <input title="Enter the Request Message Timeout" type="text" name="mspRequestMessageTimeout" value='<spring:escapeBody htmlEscape="true">${mspVendor.requestMessageTimeout}</spring:escapeBody>'>
                        </tags:nameValue>
                        
                        <tags:nameValue name="MSP Max Initiate Request Objects*">
                          <input title="Enter the Max Initiate Request Objects" type="text" name="mspMaxInitiateRequestObjects" value='<spring:escapeBody htmlEscape="true">${mspVendor.maxInitiateRequestObjects}</spring:escapeBody>'>
                        </tags:nameValue>
                        
                        <tags:nameValue name="MSP Template Name Default">
                          <input title="Enter the Template Name Default" type="text" name="mspTemplateNameDefault" value='<spring:escapeBody htmlEscape="true">${mspVendor.templateNameDefault}</spring:escapeBody>'>
                        </tags:nameValue>
                        
                    </tags:nameValueContainer>
                    <div class="smallText">* required</div>
                
                </div>
                <div class="column two nogutter">
                
                    <h5>Response Message Login</h5>
                    <tags:nameValueContainer>
                        <tags:nameValue name="UserName">
                          <input title="Enter the Username for Outgoing Yukon messages" type="text" name="outUserName" value='<spring:escapeBody htmlEscape="true">${mspVendor.outUserName}</spring:escapeBody>'>
                        </tags:nameValue>
                        
                        <tags:nameValue name="Password">
                          <input title="Enter the Password for Outgoing Yukon messages" type="text" name="outPassword" value='<spring:escapeBody htmlEscape="true">${mspVendor.outPassword}</spring:escapeBody>'>
                        </tags:nameValue>
                        
                        <c:if test="${showRoleProperties}">
                            <tags:nameValue name="Primary CIS">
                                <select title="Select the Primary CIS vendor" name="mspPrimaryCIS">
                                    <option selected value='0'>(none)</option>
                                    <c:forEach var="mspVendorEntry" items="${mspCISVendorList}">
                                        <option <c:if test="${mspVendorEntry.vendorID == primaryCIS}">selected</c:if> value='<c:out value="${mspVendorEntry.vendorID}"/>'> 
                                            <c:out value="${mspVendorEntry.companyName}"/> <c:if test="${mspVendorEntry.appName != ''}">(<c:out value="${mspVendorEntry.appName}"/>)</c:if>
                                        </option>
                                    </c:forEach>
                                  </select>
                            </tags:nameValue>
                            
                            <tags:nameValue name="DeviceName Alias">
                                <select title="Select the DeviceName Alias field" name="mspPaoNameAlias">
                                    <c:forEach var="mspPaoNameAliasEntry" items="${mspVendor.paoNameAliases}" varStatus="status">
                                        <option <c:if test="${mspPaoNameAliasEntry == paoNameAlias}">selected</c:if> value='<c:out value="${mspPaoNameAliasEntry}"/>'> <c:out value="${mspPaoNameAliasEntry}"/></option>
                                    </c:forEach>
                                </select>
                            </tags:nameValue>
                            
                            <tags:nameValue name="Use Extension">
                                <input id="mspPaoNameUsesExtension" type="checkbox" <c:if test="${paoNameUsesExtension}">checked</c:if> name='mspPaoNameUsesExtension' value='true' onclick='enableExtension(this.checked);'>
                                <tags:nameValue name="Extension Name">
                                    <input id="mspPaoNameAliasExtension" title="Enter the name of the Extension to append to DevicName" type="text" <c:if test="${!paoNameUsesExtension}">disabled</c:if> name="mspPaoNameAliasExtension" value='<spring:escapeBody htmlEscape="true">${paoNameAliasExtension}</spring:escapeBody>'>
                                </tags:nameValue>
                            </tags:nameValue>
                            
                            <tags:nameValue name="Meter Lookup Field">
                                <select title="Select the Meter Lookup field" name="mspMeterLookupField">
                                    <c:forEach var="mspMeterLookupFieldEntry" items="${mspVendor.meterLookupFields}">
                                        <option <c:if test="${mspMeterLookupFieldEntry == meterLookupField}">selected</c:if> value='<c:out value="${mspMeterLookupFieldEntry}"/>'> <c:out value="${mspMeterLookupFieldEntry}"/></option>
                                    </c:forEach>
                                </select>
                            </tags:nameValue>
                        </c:if>
                    </tags:nameValueContainer>
                
                </div>
            </div>
        </tags:sectionContainer2>
            
        <tags:sectionContainer title="Interfaces">
            
            <span title="Enter the MultiSpeak url. EX: http://127.0.0.1:80/soap/">
                MSP URL*
                <input type="text" name="mspURL" size="35" value='<spring:escapeBody htmlEscape="true">${mspVendor.url}</spring:escapeBody>'>
            </span>
            
            <table>
                <%------ INTERFACES ------%>
                <c:if test="${!isCreateNew }">
                    <c:set var="interfacesMap" value="${mspVendor.mspInterfaceMap}" scope="page" />
                </c:if>
                <c:forEach var="mspPossibleInterface" items="${possibleInterfaces}" varStatus="status">

                    <c:choose>
                        <c:when test="${!isCreateNew}">
                            <c:set var="interfaceValue" value="${interfacesMap[mspPossibleInterface]}" scope="page" />    
                            <c:set var="disabled" value="${interfaceValue == null}" scope="page" />
                        </c:when>
                        <c:otherwise><c:set var="disabled" value="true" scope="page" /></c:otherwise>
                    </c:choose>
                    
                      <tr>
                          <%-- checkbox --%>
                        <td>
                              <input id="mspInterface" type="checkbox" <c:if test="${!disabled}">checked</c:if> name='mspInterface' value='<c:out value="${mspPossibleInterface}"/>' onclick='enableEndpointValue(<c:out value="${disabled}"/>,this.checked, this.value)'>
                        </td>
                    
                        <%-- interface name --%>
                        <td>
                            <c:out value="${mspPossibleInterface}"/>
                        </td>
                    
                        <%-- endpoint --%>
                        <td>
                              <input id="mspEndpoint<c:out value="${mspPossibleInterface}"/>" type="text" name="mspEndpoint" size="30" 
                                value='<c:out value="${interfaceValue.mspEndpoint}" default="${mspPossibleInterface}Soap"/>'
                                <c:if test="${disabled}">disabled</c:if>>  
                        </td>
                            
                        <c:if test="${!isCreateNew}">
                            <%-- ping, method links --%>  
                            <td>       
                                <c:choose>
                                    <c:when test="${disabled}">
                                        <a id="mspPing<c:out value="${mspPossibleInterface}"/>" href='JavaScript:;' style='cursor:default' class="Link4" name="pingURL">Ping</a>
                                        <a id="mspMethods<c:out value="${mspPossibleInterface}"/>" href='JavaScript:;' style='cursor:default' class="Link4" name="getMethods">Methods</a>
                                    </c:when>
                                    <c:otherwise>
                                        <a id="mspPing<c:out value="${mspPossibleInterface}"/>" style='cursor:pointer' href='JavaScript:serviceSubmit("<c:out value='${interfaceValue.mspInterface}'/>", "pingURL");' class="Link4" name="pingURL">Ping</a>
                                        <a id="mspMethods<c:out value="${mspPossibleInterface}"/>" style='cursor:pointer' href='JavaScript:serviceSubmit("<c:out value='${interfaceValue.mspInterface}'/>", "getMethods");' class="Link4" name="getMethods">Methods</a>
                                    </c:otherwise>
                                </c:choose>  
                            </td>
                            <%-- results column --%>
                            <c:if test="${status.first}">
                                <td rowspan='${interfaceListLength}'>
                                    <textarea cols="50" rows="${interfaceListLength * 2}" name="Results" readonly wrap="VIRTUAL" style='color:<c:out value="${resultColor}"/>'><spring:escapeBody htmlEscape="true">${MSP_RESULT_MSG}</spring:escapeBody></textarea>
                                </td>
                            </c:if>
                        </c:if>
                         
                    </tr>
                </c:forEach>
            </table>
        </tags:sectionContainer>
            
        <div class="page-action-area">
            <cti:button type="submit" nameKey="save" classes="primary action" busy="true"/>
            <c:if test="${isCreateNew}">
                <cti:button type="submit" name="Cancel" value="Cancel" busy="true" nameKey="cancel"/>
            </c:if>
            
            <c:if test="${!isCreateNew}">
                <cti:button nameKey="delete" classes="delete" onclick="confirmDelete()"/>
            </c:if>
            <cti:button nameKey="create" busy="true" onclick="$('#create-form').submit();"/>
        </div>
    </form>
    <cti:url var="createUrl" value="/multispeak/setup/home"/>    
    <form id="create-form" name="mspCreateForm" method="post" action="${createUrl}">
        <input type="hidden" name="New" value="New">
        <cti:csrfToken/>
    </form>
    <cti:url var="deleteUrl" value="/multispeak/setup/delete"/>
    <form id="delete-form" name="mspDeleteForm" method="post" action="${deleteUrl}">
        <cti:csrfToken/>
        <input type="hidden" name="mspVendorId" value="${mspVendor.vendorID}">
    </form>
</cti:standardPage>