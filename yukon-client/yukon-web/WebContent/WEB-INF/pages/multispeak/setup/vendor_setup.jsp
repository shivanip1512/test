<%@ page import="com.cannontech.multispeak.client.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="adminSetup" page="vendor.${mode}" >
    <tags:setFormEditMode mode="${mode}" />
    <cti:msg2 var="pingTitle" key=".ping" />
    <cti:msg2 var="getMethods" key=".getMethods" />
    <cti:includeScript link="/resources/js/pages/yukon.admin.multispeak.js" />
    <cti:linkTabbedContainer mode="section" id="page_header_tab_container">
        <cti:linkTab tabId="deviceTab" selectorKey="yukon.web.modules.adminSetup.multispeak.home.tab.title">
            <c:url value="/multispeak/setup/home" />
        </cti:linkTab>

        <cti:linkTab tabId="vendorTab" selectorKey="yukon.web.modules.adminSetup.vendor.tab.title"
            initiallySelected="${true}">
            <c:url value="/multispeak/setup/vendorHome" />
        </cti:linkTab>

        <cti:checkGlobalSetting setting="MSP_LM_MAPPING_SETUP">
            <cti:linkTab tabId="loadMgtTab" selectorKey="yukon.web.modules.adminSetup.lmMappings.tab.title">
                <c:url value="/multispeak/setup/lmMappings/home" />
            </cti:linkTab>
        </cti:checkGlobalSetting>

        <cti:linkTab tabId="synchronizationTab" selectorKey="yukon.web.modules.adminSetup.multispeakSyncHome.tab.title">
            <c:url value="/multispeak/setup/multispeakSync/home" />
        </cti:linkTab>
    </cti:linkTabbedContainer>
    <c:set var="interfaceListLength" value="${fn:length(possibleInterfaces)}" />
    <cti:url var="saveUrl" value="/multispeak/setup/save" />
    <form:form modelAttribute="multispeak" id="mspForm" name="mspForm" method="post" action="${saveUrl}">
        <cti:csrfToken />
        <c:set var="viewMode" value="${false}" />
        <cti:displayForPageEditModes modes="VIEW">
            <c:set var="viewMode" value="${true}" />
        </cti:displayForPageEditModes>
        <c:set var="viewMode" value="${false}" />
        <cti:displayForPageEditModes modes="CREATE">
            <c:set var="createMode" value="${true}" />
        </cti:displayForPageEditModes>
        <c:set var="tableClass" value="${viewMode ? '' : 'with-form-controls'}" />
        <form:hidden id="actionService" path="service" />
        <form:hidden id="vendorID" path="mspVendor.vendorID" />
        <form:hidden id="endpointURL" path="endpointURL" />
        <input type="hidden" class="js-vendor-id" value="${mspVendor.vendorID}"/>
        <tags:sectionContainer2 nameKey="vendorSetup" styleClass="stacked-lg">
            <c:if test="${!noVendorsExist || createMode}">
                <div class="column-12-12 clearfix">
                    <div class="column one">
                        <tags:nameValueContainer2 tableClass="natural-width ${tableClass}">

                            <tags:nameValue2 nameKey=".companyName" requiredField="true">
                                <c:choose>
                                    <c:when test="${createMode}">
                                        <tags:input path="mspVendor.companyName" maxlength="32" />
                                    </c:when>
                                    <c:otherwise>
                                        <select title="<cti:msg2 key=".selectVendor.title"/>" name="mspVendorId"
                                            onChange="yukon.admin.multispeak.vendorChanged();" id="mspVendorId">
                                            <c:forEach var="mspVendorEntry" items="${mspVendorList}">
                                                <option
                                                    <c:if test="${mspVendorEntry.vendorID == mspVendor.vendorID}">selected</c:if>
                                                    value='<c:out value="${mspVendorEntry.vendorID}"/>'>
                                                    <spring:escapeBody htmlEscape="true">${mspVendorEntry.companyName}</spring:escapeBody></option>
                                            </c:forEach>
                                        </select>
                                        <form:hidden path="mspVendor.companyName" />
                                    </c:otherwise>
                                </c:choose>
                                <form:hidden path="mspVendor.vendorID" />
                            </tags:nameValue2>

                            <tags:nameValue2 nameKey=".appName">
                                <tags:input path="mspVendor.appName" maxlength="32" />
                            </tags:nameValue2>

                            <tags:nameValue2 nameKey=".maxRecords" requiredField="true">
                                <tags:input path="mspVendor.maxReturnRecords" maxlength="32" />
                            </tags:nameValue2>

                            <tags:nameValue2 nameKey=".reqMsgTimeout" requiredField="true">
                                <tags:input path="mspVendor.requestMessageTimeout" maxlength="32" />
                            </tags:nameValue2>

                            <tags:nameValue2 nameKey=".initRequestTimeout" requiredField="true">
                                <tags:input path="mspVendor.maxInitiateRequestObjects" maxlength="32" />
                            </tags:nameValue2>

                            <tags:nameValue2 nameKey=".template">
                                <tags:input path="mspVendor.templateNameDefault" maxlength="32" />
                            </tags:nameValue2>
                            
                            <tags:nameValue2 nameKey=".attributes"  requiredField="true">
                                <cti:displayForPageEditModes modes="CREATE,EDIT">
                                    <cti:msg2 key="yukon.web.modules.adminSetup.vendor.selectAttr" var="selectLbl" />
                                       <tags:selectWithItems items="${allAttributeList}" path="mspVendor.attributes" dataPlaceholder="${selectLbl}" id="attributes"/>
                                </cti:displayForPageEditModes>
                                <cti:displayForPageEditModes modes="VIEW">
                                    <c:forEach var="mspattribute" items="${mspVendor.attributes}" varStatus="loop">
                                        <i:inline key="${mspattribute}"/>
                                        <c:if test="${!loop.last}">,</c:if>
                                    </c:forEach>
                                </cti:displayForPageEditModes>
                                <span id="vendorAttributes" style="display: none;">${vendorAttributes}</span>
                            </tags:nameValue2>

                        </tags:nameValueContainer2>
                    </div>

                    <div class="column two nogutter">
                        <h5>
                            <i:inline key=".incomingAuthentication" />
                        </h5>
                        <tags:nameValueContainer2 tableClass="natural-width ${tableClass}">
                            <tags:nameValue2 nameKey=".userName">
                                <tags:input path="mspVendor.userName" maxlength="32" />
                            </tags:nameValue2>

                            <tags:nameValue2 nameKey=".password">
                                <tags:password path="mspVendor.password" showPassword="true" includeShowHideButton="true"></tags:password>
                            </tags:nameValue2>
                        </tags:nameValueContainer2>

                        <h5>
                            <i:inline key=".outgoingAuthentication" />
                        </h5>

                        <tags:nameValueContainer2>
                            <tags:nameValue2 nameKey=".userName">
                                <tags:input path="mspVendor.outUserName" maxlength="32" />
                            </tags:nameValue2>

                            <tags:nameValue2 nameKey=".password">
                                <tags:password path="mspVendor.outPassword" showPassword="true" includeShowHideButton="true"></tags:password>
                            </tags:nameValue2>

                            <tags:nameValue2 nameKey=".validateCertificate">
                                <cti:displayForPageEditModes modes="EDIT,CREATE">
                                    <tags:switch path="mspVendor.validateCertificate" classes="toggle-sm"/>
                                    <input type="hidden" class="js-create-or-edit-mode">
                                </cti:displayForPageEditModes>
                                <cti:displayForPageEditModes modes="VIEW">
                                    <tags:switch path="mspVendor.validateCertificate" classes="toggle-sm" disabled="true"/>
                                    <input type="hidden" class="js-view-mode">
                                </cti:displayForPageEditModes>
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                    </div>
                </div>
            </c:if>
        </tags:sectionContainer2>
        <c:choose>
            <c:when test="${!noVendorsExist || createMode}">
                <tags:sectionContainer2 nameKey="mspInterfaces">
                    <div class="column-16-8 clearfix">
                        <div class="column one">
                            <!-- Interfaces -->
                            <table class="compact-results-table no-stripes full-width">
                                <thead>
                                    <tr>
                                        <th><i:inline key=".interface" /></th>
                                        <th><i:inline key=".url" /></th>
                                        <th><i:inline key=".version" /></th>
                                        <th><i:inline key=".actions" /></th>
                                    </tr>
                                </thead>
                                <tbody>
                                   <c:forEach var="mspPossibleInterfacev3"
                                      items="${multispeak.mspInterfaceList}" varStatus="status"
                                        begin="0">
                                        <c:if
                                            test="${status.index % 3 == 0 || (mspPossibleInterfacev3.mspInterface=='NOT_Server' && status.index % 3 == 1)}">
                                            <c:choose>
                                                <c:when
                                                    test="${mspPossibleInterfacev3.mspInterface=='CB_CD'}">
                                                    <c:set var="currentInterface"
                                                        value="${mspPossibleInterfacev3}" />
                                                    <c:set var="indexValue" value="${status.index}" />
                                                </c:when>
                                                <c:when
                                                    test="${mspPossibleInterfacev3.mspInterface=='NOT_Server' }">
                                                    <c:set var="currentInterface"
                                                        value="${mspPossibleInterfacev3}" />
                                                    <c:set var="indexValue" value="${status.index}" />
                                                    <c:set var="mspPossibleInterfacev5"
                                                        value="${multispeak.mspInterfaceList[status.index+1]}" />
                                                    <c:set var="enabledV5"
                                                        value="${mspPossibleInterfacev5.interfaceEnabled}" />
                                                    <c:if test="${enabledV5}">
                                                        <c:set var="currentInterface"
                                                            value="${mspPossibleInterfacev5}" />
                                                       <c:set var="indexValue" value="${status.index + 1}" />
                                                   </c:if>
                                                 </c:when>
                                                <c:otherwise>
                                                    <c:set var="mspPossibleInterfacev4"
                                                        value="${multispeak.mspInterfaceList[status.index+1]}" />
                                                    <c:set var="mspPossibleInterfacev5"
                                                        value="${multispeak.mspInterfaceList[status.index+2]}" />
                                                    <c:set var="enabledV3"
                                                        value="${mspPossibleInterfacev3.interfaceEnabled}" />
                                                    <c:set var="enabledV4"
                                                        value="${mspPossibleInterfacev4.interfaceEnabled}" />
                                                    <c:set var="enabledV5"
                                                        value="${mspPossibleInterfacev5.interfaceEnabled}" />
                                                    <c:set var="currentInterface"
                                                        value="${mspPossibleInterfacev3}" />
                                                    <c:set var="indexValue" value="${status.index}" />
                                                    <c:if test="${enabledV4}">
                                                        <c:set var="currentInterface"
                                                             value="${mspPossibleInterfacev4}" />
                                                        <c:set var="indexValue" value="${status.index + 1}" />
                                                   </c:if>
                                                   <c:if test="${enabledV5}">
                                                       <c:set var="currentInterface"
                                                           value="${mspPossibleInterfacev5}" />
                                                       <c:set var="indexValue" value="${status.index + 2}" />
                                                 </c:if>
                                               </c:otherwise>
                                            </c:choose>
                                            <tr data-id="${indexValue}">
                                                <td class="wsnw">
                                                    <tags:checkbox
                                                        path="mspInterfaceList[${indexValue}].interfaceEnabled"
                                                        id="${currentInterface.mspInterface}"
                                                        onclick="yukon.admin.multispeak.enableEndpointValue(this.checked, '${currentInterface.version}', this.id)" />
                                                    <span class="checkBoxLabel">
                                                        <cti:msg2 key=".vendor.endpoint.${currentInterface.mspInterface}"/>
                                                    </span>
                                                    <form:hidden path="mspInterfaceList[${indexValue}].vendorID" />
                                                    <form:hidden path="mspInterfaceList[${indexValue}].mspInterface" />
                                                </td>
                                                <td class="wbba" style="max-width: 320px;">
                                                    <tags:input
                                                        id="endpointURL_${currentInterface.version}_${currentInterface.mspInterface}"
                                                        path="mspInterfaceList[${indexValue}].mspEndpoint" size="35"
                                                        disabled="${!currentInterface.interfaceEnabled}" />
                                                </td>
                                                <td>
                                                    <c:set var="versionOptions" value="${mspVersionList}" />
                                                    <c:if test="${currentInterface.mspInterface == 'NOT_Server_DR'}">
                                                        <c:set var="versionOptions" value="${mspVersion5}" />
                                                    </c:if>
                                                    <c:if test="${currentInterface.mspInterface=='CB_CD'}">
                                                        <c:set var="versionOptions" value="${mspVersion3}" />
                                                    </c:if>
                                                    <c:if test="${currentInterface.mspInterface == 'NOT_Server'}">
                                                        <c:set var="versionOptions" value="${notMspVersionList}" />
                                                    </c:if>
                                                    <tags:selectWithItems id="select${currentInterface.mspInterface}"
                                                        path="mspInterfaceList[${indexValue}].version"
                                                        disabled="${!currentInterface.interfaceEnabled}"
                                                        items="${versionOptions}" inputClass="with-option-hiding" />
                                                </td>
                                                <td style="width: 120px;">
                                                    <div class="button-group fr wsnw oh">
                                                        <cti:button icon="icon-ping"
                                                            id="ping${currentInterface.mspInterface}" name="pingURL"
                                                            renderMode="buttonImage" title="${pingTitle}"
                                                            disabled="${!currentInterface.interfaceEnabled}"
                                                            onclick="yukon.admin.multispeak.executeRequest('${currentInterface.mspInterface}',this.name,'${currentInterface.version}');" />
                                                        <cti:button icon="icon-application-view-columns"
                                                            id="getMethods${currentInterface.mspInterface}"
                                                            name="getMethods" renderMode="buttonImage"
                                                            title="${getMethods}"
                                                            disabled="${!currentInterface.interfaceEnabled}"
                                                            onclick="yukon.admin.multispeak.executeRequest('${currentInterface.mspInterface}',this.name,'${currentInterface.version}');" />
                                                        <tags:hidden path="mspInterfaceList[${indexValue}].useVendorAuth" id="js-msp-uservendorauth-${indexValue}"/>
                                                        <tags:hidden path="mspInterfaceList[${indexValue}].inUserName" id="js-msp-inusername-${indexValue}"/>
                                                        <tags:hidden path="mspInterfaceList[${indexValue}].inPassword" id="js-msp-inpassword-${indexValue}"/>
                                                        <tags:hidden path="mspInterfaceList[${indexValue}].outUserName" id="js-msp-outusername-${indexValue}"/>
                                                        <tags:hidden path="mspInterfaceList[${indexValue}].outPassword" id="js-msp-outpassword-${indexValue}"/>
                                                        <tags:hidden path="mspInterfaceList[${indexValue}].validateCertificate" id="js-msp-validatecertificate-${indexValue}"/>
                                                        <input type="hidden" id="js-page-mode" value="${mode}"/>
                                                        <cti:msg2 var="interfaceAuthTitle" key=".interfaceAuthPopupTitle" />
                                                        <cti:button icon="icon-lock" 
                                                                    renderMode="buttonImage" 
                                                                    classes="js-endpoint-auth-btn"
                                                                    name="interfaceAuth" 
                                                                    data-title="${interfaceAuthTitle}"
                                                                    disabled="${!currentInterface.interfaceEnabled}"
                                                                    id="interfaceAuth${currentInterface.mspInterface}"/>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:if>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                        <div class="column two nogutter">
                            <table>
                                <thead>
                                    <tr>
                                        <th><i:inline key=".results" /></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <c:set var="interfaceListLength"
                                            value="${fn:length(multispeak.mspInterfaceList)/3}" />
                                        <td rowspan='${interfaceListLength+3}'><textarea cols="40"
                                                rows="${interfaceListLength*3+1}" name="Results" id="results" readonly
                                                wrap="VIRTUAL" style='color:<c:out value="${resultColor}"/>'>${MSP_RESULT_MSG}</textarea>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </tags:sectionContainer2>
            </c:when>
            <c:otherwise>
                <i:inline key=".noVendorsExist" />
            </c:otherwise>
        </c:choose>
        <div class="page-action-area">
            <cti:displayForPageEditModes modes="VIEW">
                <c:if test="${!noVendorsExist}">
                    <cti:button nameKey="edit" icon="icon-pencil" id="vendor-edit" />
                </c:if>
                <cti:url var="createUrl" value="/multispeak/setup/create" />
                <cti:button nameKey="create" busy="true" href="${createUrl}" icon="icon-plus-green" />
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="EDIT,CREATE">
                <cti:button type="submit" nameKey="save" classes="primary action" busy="true" />
                <cti:url var="deleteUrl" value="/multispeak/setup/delete" />
                <cti:button nameKey="delete" href="${deleteUrl}" classes="delete js-delete"
                    data-ok-event="yukon:multispeak:vendor:delete" />
                <d:confirm on=".js-delete" nameKey="confirmDelete" argument="${mspVendor.companyName}"/>
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="EDIT,CREATE">
            <cti:url var="vendorHomeUrl" value="/multispeak/setup/vendorHome"/>
                <cti:button nameKey="cancel" href="${vendorHomeUrl}"/>
            </cti:displayForPageEditModes>
        </div>
    </form:form>
    <cti:url var="url" value="/multispeak/setup/vendorHome/${mspVendor.vendorID}" />
    <form:form id="delete-vendor" method="DELETE" action="${url}">
        <cti:csrfToken />
    </form:form>

</cti:standardPage>
