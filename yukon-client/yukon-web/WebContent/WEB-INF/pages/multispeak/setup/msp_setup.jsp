<%@ page import="com.cannontech.multispeak.client.*" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<cti:standardPage module="adminSetup"  page="multispeak.${mode}">
<tags:setFormEditMode mode="${mode}" />
<cti:msg2 var="pingTitle" key=".ping"/>
<cti:msg2 var="getMethods" key=".getMethods"/>
<cti:includeScript link="/resources/js/pages/yukon.admin.multispeak.js" />
<cti:linkTabbedContainer mode="section" id="page_header_tab_container">
    <cti:linkTab tabId="deviceTab" selectorKey="yukon.web.modules.adminSetup.multispeak.home.tab.title" initiallySelected="${true}">
        <c:url value="/multispeak/setup/home" />
    </cti:linkTab>
    
    <cti:linkTab tabId="vendorTab" selectorKey="yukon.web.modules.adminSetup.vendor.tab.title">
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
    <form:form modelAttribute="multispeak" id="mspForm" name="mspForm" method="post" action="${saveUrl}">
        <cti:csrfToken/>
        <c:set var="viewMode" value="${false}" />
        <cti:displayForPageEditModes modes="VIEW">
            <c:set var="viewMode" value="${true}" />
        </cti:displayForPageEditModes>
        <c:set var="tableClass" value="${viewMode ? '' : 'with-form-controls'}" />
        <form:hidden id="actionService" path="service" />
        <form:hidden id="endpointURL" path="endpointURL" />
        <form:hidden id="vendorID" path="mspVendor.vendorID" />
        <form:hidden id="userName" path="mspVendor.userName" />
        <form:hidden id="password" path="mspVendor.password" />
        <form:hidden id="userName" path="mspVendor.outUserName" />
        <form:hidden id="password" path="mspVendor.outPassword" />
        <tags:sectionContainer2 nameKey="setup" styleClass="stacked-lg">
            <div class="column-12-12 clearfix">
                <div class="column one">
                    <tags:nameValueContainer2 tableClass="natural-width ${tableClass}">
                        <c:if test="${showRoleProperties}">
                            <tags:nameValue2 nameKey=".companyName">
                                <span>${multispeak.mspVendor.companyName}</span>
                                <form:hidden path="mspVendor.companyName" />
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".appName">
                                <span>${multispeak.mspVendor.appName}</span>
                                <form:hidden path="mspVendor.appName" />
                            </tags:nameValue2>
                        </c:if>
                    </tags:nameValueContainer2>
                </div>
                
                <div class="column two nogutter">
                    <tags:nameValueContainer2 tableClass="natural-width ${tableClass}">
                        <c:if test="${showRoleProperties}">
                            <tags:nameValue2 nameKey=".primaryCIS">
                            <tags:selectWithItems path="mspPrimaryCIS"
                                items="${mspCISVendorList}"
                                itemLabel="companyName" itemValue="vendorID"
                                inputClass="with-option-hiding" />
                            </tags:nameValue2>
                            
                            <tags:nameValue2 nameKey=".deviceNameAlias">
                                <tags:selectWithItems id="paoNameAlias" path="paoNameAlias" items="${paoNameAliases}" 
                                inputClass="with-option-hiding"/>
                            </tags:nameValue2>
                            
                            <c:if test="${multispeak.paoNameUsesExtension || !viewMode}">
                            <tags:nameValue2 nameKey=".useExtension">
                            <tags:switchButton path="paoNameUsesExtension" offClasses="M0" hideValue="true"
                                toggleGroup="mspPaoNameAliasExtension" toggleAction="hide" inputClass="js-use-offset" />
                            
                            <tags:input path="paoNameAliasExtension" size="6" toggleGroup="mspPaoNameAliasExtension"/>
                            </tags:nameValue2>
                            </c:if>
                            <tags:nameValue2 nameKey=".meterLookupField">
                                <tags:selectWithItems id="meterLookupField" path="meterLookupField" items="${meterLookupFields}" 
                                inputClass="with-option-hiding"/>
                            </tags:nameValue2>
                        </c:if>
                    </tags:nameValueContainer2>
                
                </div>
            </div>
        </tags:sectionContainer2>
        <!-- Interfaces -->
        <tags:sectionContainer2 nameKey="mspInterfaces">
         <div class="column-12-1-11 clearfix">
                <div class="column one">
            <table class="compact-results-table row-highlighting">
                <tbody>
                    <tr style="border-bottom: solid 1px #ccc;"><td colspan="4"><b><i:inline key=".version3"/></b></td></tr>
                    <c:forEach var="multispeakInterface" items="${multispeak.mspInterfaceList}" varStatus="i">
                        <c:if test="${i.index == 5}">
                           <tr style="border-bottom: solid 1px #ccc;"><td colspan="4"><b><i:inline key=".version5"/></b></td></tr>
                        </c:if>
                        <c:if test="${(multispeakInterface.interfaceEnabled && viewMode) || !viewMode}">
                            <tr>
                                <c:set var="resultsCount" value="${resultsCount + 1}" />
                                <td>
                                    <c:out value="${multispeakInterface.mspInterface}" /> 
                                    <tags:hidden path="mspInterfaceList[${i.index}].mspInterface" /> 
                                    <tags:hidden path="mspInterfaceList[${i.index}].vendorID" /> 
                                    <tags:hidden path="mspInterfaceList[${i.index}].interfaceEnabled" />
                                </td>
                                <td class="wbba" style="max-width:320px;">
                                    <tags:input id="endpointURL_${multispeakInterface.version}_${multispeakInterface.mspInterface}" path="mspInterfaceList[${i.index}].mspEndpoint" size="35" />
                                </td>
                                <tags:hidden path="mspInterfaceList[${i.index}].version" />
                                <td style="width: 90px;">
                                    <div class="button-group fr wsnw oh">
                                        <cti:button icon="icon-ping" id="${multispeakInterface.mspInterface}" name="pingURL"
                                            renderMode="buttonImage" title="${pingTitle}" disabled="${disabled}"
                                            onclick="yukon.admin.multispeak.executeRequest(this.id,this.name,'${multispeakInterface.version}');" />
                                        <cti:button icon="icon-application-view-columns" id="${multispeakInterface.mspInterface}" name="getMethods"
                                            renderMode="buttonImage" title="${getMethods}" disabled="${disabled}"
                                            onclick="yukon.admin.multispeak.executeRequest(this.id,this.name,'${multispeakInterface.version}');" />
                                    </div>
                                </td>
                            </tr>      
                        </c:if>                  
                </c:forEach>
                </tbody>
         </table>
         </div>
         <div class="column two nogutter"> <tags:nameValueContainer2></tags:nameValueContainer2>
         </div>
         <div class="column three nogutter">
             <tags:nameValueContainer2><br>
                 <textarea cols="53" rows="${resultsCount * 2 + 1}" name="Results" id="results" readonly 
                           style='color:<c:out value="${resultColor}"/>;width: 400px;word-wrap:normal;overflow-x: scroll;'>
                     ${MSP_RESULT_MSG}
                 </textarea>
             </tags:nameValueContainer2>
         </div>
         </div>
        </tags:sectionContainer2>
            
        <div class="page-action-area">
            <cti:displayForPageEditModes modes="VIEW">
                <cti:url var="editUrl" value="/multispeak/setup/editYukonSetup" />
                <cti:button nameKey="edit" icon="icon-pencil" href="${editUrl}"/>
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="EDIT">
                <cti:button type="submit" nameKey="save" classes="primary action" busy="true"/>
                <cti:url var="viewUrl" value="/multispeak/setup/home?mspVendorId=${multispeak.mspVendor.vendorID}"/>
                <cti:button nameKey="cancel" href="${viewUrl}"/>
            </cti:displayForPageEditModes>
        </div>
    </form:form>
</cti:standardPage>