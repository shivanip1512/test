<%@ page import="com.cannontech.multispeak.client.*" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<cti:standardPage module="adminSetup"  page="interfaces">
<cti:msg2 var="pingTitle" key=".ping"/>
<cti:msg2 var="getMethods" key=".getMethods"/>
<cti:includeScript link="/resources/js/pages/yukon.admin.multispeak.js" />
<cti:linkTabbedContainer mode="section" id="page_header_tab_container">
    <cti:linkTab tabId="deviceTab" selectorKey="yukon.web.modules.adminSetup.interfaces.home.tab.title" initiallySelected="${true}">
        <c:url value="/multispeak/setup/home" />
    </cti:linkTab>
    
    <cti:linkTab tabId="vendorTab" selectorKey="yukon.web.modules.adminSetup.interfaces.vendor.tab.title">
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
        <input type="hidden" name="version">
        <tags:sectionContainer2 nameKey="setup">
            <div class="column-12-12 clearfix">
                <div class="column one">
                    <tags:nameValueContainer2>
                                <input type="hidden" name="mspVendorId" value="${defaultMspVendor.vendorID}">
                                <input type="hidden" name="mspUserName" value="${defaultMspVendor.userName}">
                                <input type="hidden" name="mspPassword" value="${defaultMspVendor.password}">
                                <input type="hidden" name="mspAppName" value="${defaultMspVendor.appName}">
                                <input type="hidden" name="outUserName" value="${defaultMspVendor.outUserName}">
                                <input type="hidden" name="outPassword" value="${defaultMspVendor.outPassword}">
                                <tags:nameValue2 nameKey=".companyName">
                                    <input type="hidden" name="mspCompanyName" value="${defaultMspVendor.companyName}">
                                        <span>${fn:escapeXml(defaultMspVendor.companyName)}</span>
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".appName">
                                   <span>${fn:escapeXml(defaultMspVendor.appName)}</span>
                                </tags:nameValue2>
                    </tags:nameValueContainer2>
                </div>
                
                <div class="column two nogutter">
                    <tags:nameValueContainer2>
                        <c:if test="${showRoleProperties}">
                            <tags:nameValue2 nameKey=".primaryCIS">
                                <select title="<cti:msg2 key=".primaryCIS.title"/>" name="mspPrimaryCIS">
                                    <option selected value='0'>(none)</option>
                                    <c:forEach var="mspVendorEntry" items="${mspCISVendorList}">
                                        <option <c:if test="${mspVendorEntry.vendorID == primaryCIS}">selected</c:if> value='<c:out value="${mspVendorEntry.vendorID}"/>'> 
                                            <c:out value="${mspVendorEntry.companyName}"/> <c:if test="${mspVendorEntry.appName != ''}">(<c:out value="${mspVendorEntry.appName}"/>)</c:if>
                                        </option>
                                    </c:forEach>
                                  </select>
                            </tags:nameValue2>
                            
                            <tags:nameValue2 nameKey=".deviceNameAlias">
                                <select name="mspPaoNameAlias">
                                    <c:forEach var="mspPaoNameAliasEntry" items="${mspVendor.paoNameAliases}" varStatus="status">
                                        <option <c:if test="${mspPaoNameAliasEntry == paoNameAlias}">selected</c:if> value='<c:out value="${mspPaoNameAliasEntry}"/>'> <c:out value="${mspPaoNameAliasEntry}"/></option>
                                    </c:forEach>
                                </select>
                            </tags:nameValue2>
                            
                            <tags:nameValue2 nameKey=".useExtension">
                                <input id="mspPaoNameUsesExtension" type="checkbox" <c:if test="${paoNameUsesExtension}">checked</c:if> name='mspPaoNameUsesExtension' value='true' onclick='yukon.admin.multispeak.enableExtension(this.checked);'>
                                <tags:nameValue2 nameKey=".extensionName">
                                    <input id="mspPaoNameAliasExtension" type="text" <c:if test="${!paoNameUsesExtension}">disabled</c:if> name="mspPaoNameAliasExtension" value="${paoNameAliasExtension}">
                                </tags:nameValue2>
                            </tags:nameValue2>
                            
                            <tags:nameValue2 nameKey=".meterLookupField">
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
        </tags:sectionContainer2>
            
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
         <tbody>
             <c:set var="interfacesMap" value="${mspVendor.mspInterfaceMap}" scope="page" />
                 <c:forEach var="mspPossibleInterface" items="${possibleInterfaces}" varStatus="status">
				     <c:set var="version" value="${mspPossibleInterface}5" scope="page" />
                     <c:set var="interfaceValue" value="${interfacesMap[mspPossibleInterface]}" scope="page" />    
                     <c:set var="interfaceValuev5" value="${interfacesMap[mspPossibleInterface]}" scope="page" /> 
                     <c:set var="disabled" value="${interfaceValuev5 == null}" scope="page" />
                     <c:choose>
                         <c:when test="${mspPossibleInterface.right.version== '3.0'}">
                             <c:set var="defaultURL" value="${mspVendor.url}/soap/${mspPossibleInterface.left}Soap" scope="page" />
                             </c:when>
                         <c:otherwise>
                             <c:set var="defaultURL" value="${mspVendor.url}/multispeak/v5/${mspPossibleInterface.left}" scope="page" />
                         </c:otherwise>
                     </c:choose>
                      <tr>
                        <td>
                            <c:if test="${mspPossibleInterface.right.version!= '5.0' || mspPossibleInterface.left=='NOT_Server'}">
                              <input id="${mspPossibleInterface.key}" type="checkbox" <c:if test="${!disabled}">checked</c:if> name='mspInterface' value='<c:out value="${mspPossibleInterface.key}"/>' 
                              onclick='yukon.admin.multispeak.enableEndpointValue(<c:out value="${disabled}"/>,this.checked, this.value)'>
                              <c:out value="${mspPossibleInterface.key}"/>
                            </c:if>
                        </td>
                        
                        <td>
                            <input id="<c:out value="${mspPossibleInterface.key}${mspPossibleInterface.right.version}"/>" type="text" name="mspEndpoint" size="30" 
                                value='<c:out value="${interfaceValue.mspEndpoint}" default="${defaultURL}"/>'
                                <c:if test="${disabled}">disabled</c:if>>
                        </td>
                        <td>
                                <span>${mspPossibleInterface.right.version}</span>
                        </td>
                            
                            <td>       
                            <div class="button-group fr wsnw oh">
                                    <cti:button icon="icon-ping" id="${mspPossibleInterface.left}" classes="${mspPossibleInterface.key}" name="pingURL" renderMode="buttonImage" title="${pingTitle}" disabled="${disabled}"
                                    onclick="yukon.admin.multispeak.executeRequest(this.id,this.name,${mspPossibleInterface.right.version});"/>
                                    <cti:button icon="icon-application-view-columns" id="${mspPossibleInterface.key}" classes="${mspPossibleInterface.key}" name="getMethods" renderMode="buttonImage" title="${getMethods}" disabled="${disabled}"
                                    onclick="yukon.admin.multispeak.executeRequest(this.id,this.name,${mspPossibleInterface.right.version});"/>
                                </div>
                            </td>
                        
                       <c:if test="${status.first}">
                       <c:set var="interfaceListLength" value="${fn:length(possibleInterfaces)}" />
                                <td rowspan='${interfaceListLength}'>
                                    <textarea cols="50" rows="${interfaceListLength*2+1}" name="Results" readonly wrap="VIRTUAL" style='color:<c:out value="${resultColor}"/>'>${MSP_RESULT_MSG}</textarea>
                                </td>
                      </c:if>
                      </tr>
                </c:forEach>
                </tbody>
         </table>
        </tags:sectionContainer2>
            
        <div class="page-action-area">
            <cti:button type="submit" nameKey="save" classes="primary action" busy="true"/>
                <cti:button type="submit" name="Cancel" value="Cancel" busy="true" nameKey="cancel"/>
        </div>
    </form>
</cti:standardPage>