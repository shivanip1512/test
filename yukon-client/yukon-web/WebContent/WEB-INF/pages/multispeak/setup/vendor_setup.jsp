<%@ page import="com.cannontech.multispeak.client.*" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<cti:standardPage module="adminSetup"  page="interfaces">
<cti:msg2 var="pingTitle" key=".ping"/>
<cti:msg2 var="getMethods" key=".getMethods"/>
<cti:msg2 var="controlsMessage" key=".setup.controls"/>
<cti:includeScript link="/resources/js/pages/yukon.admin.multispeak.js" />
<cti:linkTabbedContainer mode="section" id="page_header_tab_container">
    <cti:linkTab tabId="deviceTab" selectorKey="yukon.web.modules.adminSetup.interfaces.home.tab.title">
        <c:url value="/multispeak/setup/home" />
    </cti:linkTab>
    
    <cti:linkTab tabId="vendorTab" selectorKey="yukon.web.modules.adminSetup.interfaces.vendor.tab.title" initiallySelected="${true}">
        <c:url value="/multispeak/setup/vendorHome" />
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
    
    <cti:url var="saveUrl" value="/multispeak/setup/save"/>
    <form name="mspForm" method="post" action="${saveUrl}">
        <cti:csrfToken/>
        <input type="hidden" name="actionEndpoint">
        <input type="hidden" name="actionService">
        <input type="hidden" name="source" value="vendorSetup">
        <input type="hidden" name="version">
        <tags:sectionContainer2 nameKey="vendorSetup" displayRequiredFieldsNotice="${controlsMessage}">
        <c:if test="${!noVendorsExist}">
            <div class="column-12-12 clearfix">
                <div class="column one">
                    <tags:nameValueContainer2>
                    
                        <c:choose>
                            <c:when test="${isCreateNew}">
                                <tags:nameValue2 nameKey=".companyName">
                                    <input type="hidden" name="isCreateNew" value="true">
                                    <input name="mspCompanyName">
                               </tags:nameValue2>
                            </c:when>
                            
                            <c:otherwise>
                                <tags:nameValue2 nameKey=".companyName">
                                    <input type="hidden" name="mspCompanyName" value="${mspVendor.companyName}">
                                    <select title="<cti:msg2 key=".selectVendor.title"/>" name="mspVendorId" onChange="yukon.admin.multispeak.vendorChanged();">
                                        <c:forEach var="mspVendorEntry" items="${mspVendorList}">
                                            <option <c:if test="${mspVendorEntry.vendorID == mspVendor.vendorID}">selected</c:if> value='<c:out value="${mspVendorEntry.vendorID}"/>'> <spring:escapeBody htmlEscape="true">${mspVendorEntry.companyName}</spring:escapeBody></option>
                                        </c:forEach>
                                    </select>
                                </tags:nameValue2>
                            </c:otherwise>
                        </c:choose>
                        
                        <tags:nameValue2 nameKey=".appName">
                          <input name="mspAppName" value='<spring:escapeBody htmlEscape="true">${mspVendor.appName}</spring:escapeBody>'>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".maxRecords" requiredField="true">
                          <input name="mspMaxReturnRecords" value='<spring:escapeBody htmlEscape="true">${mspVendor.maxReturnRecords}</spring:escapeBody>'>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".reqMsgTimeout" requiredField="true">
                          <input name="mspRequestMessageTimeout" value='<spring:escapeBody htmlEscape="true">${mspVendor.requestMessageTimeout}</spring:escapeBody>'>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".initRequestTimeout" requiredField="true">
                          <input name="mspMaxInitiateRequestObjects" value='<spring:escapeBody htmlEscape="true">${mspVendor.maxInitiateRequestObjects}</spring:escapeBody>'>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".template">
                          <input name="mspTemplateNameDefault" value='<spring:escapeBody htmlEscape="true">${mspVendor.templateNameDefault}</spring:escapeBody>'>
                        </tags:nameValue2>
                        
                    </tags:nameValueContainer2>
                
                </div>
                <div class="column two nogutter">
                
                    <h5><i:inline key=".incomingAuthentication"/></h5>
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".userName">
                          <input name="mspUserName" value='<spring:escapeBody htmlEscape="true">${mspVendor.userName}</spring:escapeBody>'>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".password">
                          <div class="dib M0">
                             <input name="mspPassword" id="mspPassword" value='<spring:escapeBody htmlEscape="true">${mspVendor.password}</spring:escapeBody>'
                             type="password">
                                 <tags:check id="mspPassword" name="showHideButton_mspPassword" classes="fr M0 js-eye-icon">
                                     <i title="<i:inline key="yukon.web.modules.adminSetup.config.showHideData"/>" class="icon icon-eye" id="showhide_mspPassword"></i>
                                 </tags:check>
                          </div>
                        </tags:nameValue2>
                        
                    </tags:nameValueContainer2>
                
                <h5><i:inline key=".outcomingAuthentication"/></h5>
                <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".userName">
                          <input name="outUserName" value='<spring:escapeBody htmlEscape="true">${mspVendor.outUserName}</spring:escapeBody>'>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".password">
                          
                        <div class="dib M0">
                            <input name="outPassword" id="outPassword" value='<spring:escapeBody 
                            htmlEscape="true">${mspVendor.outPassword}</spring:escapeBody>' type="password">
                             
                                 <tags:check id="outPassword" name="showHideButton_outPassword" classes="fr M0 js-eye-icon">
                                     <i title="<i:inline key="yukon.web.modules.adminSetup.config.showHideData"/>" class="icon icon-eye" id="showhide_outPassword"></i>
                                 </tags:check>
                          </div>
                        </tags:nameValue2>
                        
                        <c:if test="${showRoleProperties}">
                            <tags:nameValue2 nameKey = ".primaryCIS">
                                <select title="<cti:msg2 key=".primaryCIS.title"/>" name="mspPrimaryCIS">
                                    <option selected value='0'>(none)</option>
                                    <c:forEach var="mspVendorEntry" items="${mspCISVendorList}">
                                        <option <c:if test="${mspVendorEntry.vendorID == primaryCIS}">selected</c:if> value='<c:out value="${mspVendorEntry.vendorID}"/>'> 
                                            <c:out value="${mspVendorEntry.companyName}"/> <c:if test="${mspVendorEntry.appName != ''}">(<c:out value="${mspVendorEntry.appName}"/>)</c:if>
                                        </option>
                                    </c:forEach>
                                  </select>
                            </tags:nameValue2>
                            
                            <tags:nameValue2 nameKey = ".deviceNameAlias">
                                <select title="<cti:msg2 key=".deviceNameAlias.title"/>" name="mspPaoNameAlias">
                                    <c:forEach var="mspPaoNameAliasEntry" items="${mspVendor.paoNameAliases}" varStatus="status">
                                        <option <c:if test="${mspPaoNameAliasEntry == paoNameAlias}">selected</c:if> value='<c:out value="${mspPaoNameAliasEntry}"/>'> <c:out value="${mspPaoNameAliasEntry}"/></option>
                                    </c:forEach>
                                </select>
                            </tags:nameValue2>
                            
                            <tags:nameValue2 nameKey = ".useExtension">
                                <input id="mspPaoNameUsesExtension" type="checkbox" <c:if test="${paoNameUsesExtension}">checked</c:if> name='mspPaoNameUsesExtension' value='true' onclick='enableExtension(this.checked);'>
                                <tags:nameValue2 nameKey=".extensionName">
                                    <input id="mspPaoNameAliasExtension" <c:if test="${!paoNameUsesExtension}">disabled</c:if> name="mspPaoNameAliasExtension" value='<spring:escapeBody htmlEscape="true">${paoNameAliasExtension}</spring:escapeBody>'>
                                </tags:nameValue2>
                            </tags:nameValue2>
                            
                            <tags:nameValue2 nameKey = ".meterLookupField">
                                <select title="<cti:msg2 key=".meterLookupField.title"/>" name="mspMeterLookupField">
                                    <c:forEach var="mspMeterLookupFieldEntry" items="${mspVendor.meterLookupFields}">
                                        <option <c:if test="${mspMeterLookupFieldEntry == meterLookupField}">selected</c:if> value='<c:out value="${mspMeterLookupFieldEntry}"/>'> <c:out value="${mspMeterLookupFieldEntry}"/></option>
                                    </c:forEach>
                                </select>
                            </tags:nameValue2>
                        </c:if>
                    </tags:nameValueContainer2>
                </div>
            </div>
            </c:if>
        </tags:sectionContainer2>
        <c:choose>
        <c:when test="${!noVendorsExist}">
        <tags:sectionContainer2 nameKey="mspInterfaces">
        <table class="compact-results-table row-highlighting">
            <thead>
         	    <tr>
                        <th><i:inline key=".interface"/></th>
                        <th><i:inline key=".url"/></th>
                        <th><i:inline key=".version"/></th>
                        <th><i:inline key=".actions"/></th>
                        <th><i:inline key=".results"/></th>
            </tr>
         </thead>
         
         <c:if test="${!isCreateNew }">
             <c:set var="interfacesMap" value="${mspVendor.mspInterfaceMap}" scope="page" />
         </c:if>
         <c:forEach var="mspPossibleInterfacev5" items="${possibleInterfaces}" varStatus="status" step="2">
   	         <c:set var="interfaceDisabledv5" value="${interfacesMap[mspPossibleInterfacev5] == null}" scope="page" />
			 <c:set var="possibleInterfacev3" value="${possibleInterfaces[status.index+1]}" scope="page" />
			 <c:set var="interfaceDisabledv3" value="${interfacesMap[possibleInterfacev3] == null}" scope="page" />
					
					<c:choose>
                        <c:when test="${!isCreateNew}">
                        <c:choose>
                        <c:when test="${!interfaceDisabledv5}">
                            <c:set var="interfaceValue" value="${interfacesMap[mspPossibleInterfacev5]}" scope="page" />   
                            <c:set var="mspVersionSelected" value="${mspPossibleInterfacev5.right}" scope="page" />
                            <c:set var="possibleInterface" value="${mspPossibleInterfacev5}" scope="page" />   
                        </c:when>
                        <c:otherwise>
                            <c:set var="interfaceValue" value="${interfacesMap[possibleInterfacev3]}" scope="page" /> 
                             <c:set var="mspVersionSelected" value="${possibleInterfacev3.right}" scope="page" /> 
                             <c:set var="possibleInterface" value="${possibleInterfacev3}" scope="page" />  
                        </c:otherwise>
                    </c:choose> 
                    <c:set var="interfaceValuev5" value="${interfacesMap[version]}" scope="page" /> 
                    <c:set var="disabled" value="${interfaceValue == null}" scope="page" />
                    </c:when>
                        <c:otherwise><c:set var="disabled" value="true" scope="page" />
                        </c:otherwise>
                    </c:choose>
					<c:choose>
                         <c:when test="${interfaceDisabledv5 && interfaceDisabledv3}">
                             <c:set var="defaultURL" value="${mspVendor.url}${mspPossibleInterfacev5.left}Soap" scope="page" />
                         </c:when>
                         <c:otherwise>
                             <c:set var="defaultURL" value="${mspVendor.url}/multispeak/v5/${mspPossibleInterfacev5.left}" scope="page" />
                         </c:otherwise>
                     </c:choose>
                    
                      <tr>
                          <td>
                              <c:if test="${mspPossibleInterfacev5.right.version!= '3.0' || mspPossibleInterfacev5.left=='NOT_Server'}">
                                  <input id="${mspPossibleInterfacev5.key}" type="checkbox" <c:if test="${!disabled}">checked</c:if> name='mspInterface' 
                                  value='<c:out value="${mspPossibleInterfacev5.key}"/>' 
                                  onclick='yukon.admin.multispeak.enableEndpointValue(<c:out value="${disabled}"/>,this.checked, this.value)'>
                                  <c:out value="${mspPossibleInterfacev5.key}"/>
                              </c:if>
                          </td>
                          <c:if test="${interfaceDisabledv5 || interfaceDisabledv3}"> 
                              <td>
                                  <input id="<c:out value="${mspPossibleInterfacev5.key}${mspPossibleInterfacev5.right.version}"/>"  name="mspEndpoint" size="30" 
                                  value='<c:out value="${interfaceValue.mspEndpoint}" default="${defaultURL}"/>'
                                  <c:if test="${interfaceDisabledv5 && interfaceDisabledv3}">disabled</c:if>>
                              </td>
                          </c:if>
                        
                          <td>
                              <select title="Multispeak Version" name="mspVersions" id="<c:out value="select${mspPossibleInterfacev5.key}"/>" <c:if test="${disabled}">disabled</c:if>>
                                  <c:forEach var="mspVersion" items="${mspVersionList}">
                                      <c:if test="${!(mspVersion.version==3.0 && mspPossibleInterfacev5.left=='NOT_Server')}">
                                          <option <c:if test="${interfacesMap[mspPossibleInterfacev5].version.databaseRepresentation == mspVersion.version}">selected</c:if> value='<c:out value="${mspVersion}"/>'> <c:out value="${mspVersion.version}"/></option>
                                      </c:if>
                                  </c:forEach>
                              </select>
                          </td>
                          <c:if test="${!isCreateNew}">
                              <td>
                                  <div class="button-group fr wsnw oh">
                                      <cti:button icon="icon-ping" id="${possibleInterface.key}" classes="${possibleInterface}" name="pingURL" renderMode="buttonImage" title="${pingTitle}" 
                                        disabled="${disabled}" onclick="yukon.admin.multispeak.executeRequest(this.id,this.name, '${mspVersionSelected}');"/>
                                      <cti:button icon="icon-application-view-columns" id="${possibleInterface.key}" classes="${possibleInterface}" name="getMethods" renderMode="buttonImage" 
                                        title="${getMethods}" disabled="${disabled}" onclick="yukon.admin.multispeak.executeRequest(this.id,this.name, '${mspVersionSelected}');"/>
                                  </div>
                              </td>
                            
                        </c:if>
                        <c:if test="${status.first}">
                            <c:set var="interfaceListLength" value="${fn:length(possibleInterfaces)}" />
                            <td rowspan='${interfaceListLength}'>
                                <textarea cols="50" rows="${interfaceListLength + 1}" name="Results" readonly wrap="VIRTUAL" 
                                    style='color:<c:out value="${resultColor}"/>'>${MSP_RESULT_MSG}</textarea>
                            </td>
                      </c:if>
                      </tr>
                      
                </c:forEach>
        </table>
        </tags:sectionContainer2>
        </c:when>
        <c:otherwise>
        <i:inline key=".noVendorsExist"/>
        </c:otherwise>
        </c:choose>
        <div class="page-action-area">
            <cti:button type="submit" nameKey="save" classes="primary action" busy="true"/>
            <c:if test="${isCreateNew}">
                <cti:button type="reset" name="Cancel" value="Cancel" nameKey="cancel"/>
            </c:if>
            
            <c:if test="${!isCreateNew}">
                <cti:button nameKey="delete" classes="delete js-delete" data-ok-event="yukon:multispeak:vendor:delete"/>
                <d:confirm on=".js-delete" nameKey="confirmDelete" />
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
        <input type="hidden" name="source" value="vendorSetup">
    </form>
</cti:standardPage>