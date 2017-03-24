<%@ page import="com.cannontech.multispeak.client.*"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<cti:standardPage module="adminSetup" page="interfaces.${mode}">
    <tags:setFormEditMode mode="${mode}" />
    <cti:msg2 var="pingTitle" key=".ping" />
    <cti:msg2 var="getMethods" key=".getMethods" />
    <cti:includeScript link="/resources/js/pages/yukon.admin.multispeak.js" />
    <cti:linkTabbedContainer mode="section" id="page_header_tab_container">
        <cti:linkTab tabId="deviceTab" selectorKey="yukon.web.modules.adminSetup.interfaces.home.tab.title">
            <c:url value="/multispeak/setup/home" />
        </cti:linkTab>

        <cti:linkTab tabId="vendorTab" selectorKey="yukon.web.modules.adminSetup.interfaces.vendor.tab.title"
            initiallySelected="${true}">
            <c:set var="vendorId" value="0" />
            <c:url value="/multispeak/setup/vendorHome/${vendorId}" />
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
    <cti:url var="saveUrl" value="/multispeak/setup/save" />
    <form:form modelAttribute="multispeak" id="mspForm" name="mspForm" method="post" action="${saveUrl}"
        commandName="multispeak">
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
        <tags:sectionContainer2 nameKey="setup" styleClass="stacked-lg">
            <c:if test="${!noVendorsExist || createMode}">
                <div class="column-12-12 clearfix">
                    <div class="column one">
                        <tags:nameValueContainer2 tableClass="natural-width ${tableClass}">

                            <tags:nameValue2 nameKey=".companyName">
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
                            <i:inline key=".outcomingAuthentication" />
                        </h5>

                        <tags:nameValueContainer2>
                            <tags:nameValue2 nameKey=".userName">
                                <tags:input path="mspVendor.outUserName" maxlength="32" />
                            </tags:nameValue2>

                            <tags:nameValue2 nameKey=".password">
                                <tags:password path="mspVendor.outPassword" showPassword="true" includeShowHideButton="true"></tags:password>
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                    </div>
                </div>
            </c:if>
        </tags:sectionContainer2>
        <c:choose>
            <c:when test="${!noVendorsExist || createMode}">
                <tags:sectionContainer2 nameKey="mspInterfaces">
                    <!-- Interfaces -->
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
                            <c:forEach var="mspPossibleInterfacev3" items="${multispeak.mspInterfaceList}"
                                varStatus="status" step="2" begin="0" end="${fn:length(multispeak.mspInterfaceList)-3}">
                                <c:set var="mspPossibleInterfacev5"
                                    value="${multispeak.mspInterfaceList[status.index+1]}" />
                                <c:set var="enabledV3" value="${mspPossibleInterfacev3.interfaceEnabled}" />
                                <c:set var="enabledV5" value="${mspPossibleInterfacev5.interfaceEnabled}" />
                                <tr>
                                    <c:choose>
                                        <c:when test="${enabledV3}">
                                            <td><tags:checkbox
                                                    path="mspInterfaceList[${status.index}].interfaceEnabled"
                                                    id="${mspPossibleInterfacev3.mspInterface}"
                                                    onclick="yukon.admin.multispeak.enableEndpointValue(this.checked, this.id)" />
                                                <span class="checkBoxLabel"><spring:escapeBody htmlEscape="true">${mspPossibleInterfacev3.mspInterface}</spring:escapeBody></span>
                                                <form:hidden path="mspInterfaceList[${status.index}].vendorID" /> <form:hidden
                                                    path="mspInterfaceList[${status.index}].mspInterface" /></td>
                                            <td><tags:input
                                                    id="interfaceURL${mspPossibleInterfacev3.mspInterface}${mspPossibleInterfacev3.version}"
                                                    path="mspInterfaceList[${status.index}].mspEndpoint" size="40" /></td>
                                            <td><tags:selectWithItems
                                                    id="select${mspPossibleInterfacev3.mspInterface}"
                                                    path="mspInterfaceList[${status.index}].version"
                                                    items="${mspVersionList}" inputClass="with-option-hiding" /></td>
                                            <td>
                                                <div class="button-group fr wsnw oh">
                                                    <cti:button icon="icon-ping"
                                                        id="ping${mspPossibleInterfacev3.mspInterface}" name="pingURL"
                                                        renderMode="buttonImage" title="${pingTitle}"
                                                        onclick="yukon.admin.multispeak.executeRequest('${mspPossibleInterfacev3.mspInterface}',this.name,'${mspPossibleInterfacev3.version}');" />
                                                    <cti:button icon="icon-application-view-columns"
                                                        id="getMethods${mspPossibleInterfacev3.mspInterface}"
                                                        name="getMethods" renderMode="buttonImage" title="${getMethods}"
                                                        disabled="${disabled}"
                                                        onclick="yukon.admin.multispeak.executeRequest('${mspPossibleInterfacev3.mspInterface}',this.name,'${mspPossibleInterfacev3.version}');" />
                                                </div>
                                            </td>
                                        </c:when>
                                        <c:when test="${enabledV5}">
                                            <td><tags:checkbox
                                                    path="mspInterfaceList[${status.index+1}].interfaceEnabled"
                                                    id="${mspPossibleInterfacev5.mspInterface}"
                                                    onclick="yukon.admin.multispeak.enableEndpointValue(this.checked, this.id)" />
                                                <span class="checkBoxLabel"><spring:escapeBody htmlEscape="true">${mspPossibleInterfacev5.mspInterface}</spring:escapeBody></span>
                                                <form:hidden path="mspInterfaceList[${status.index+1}].vendorID" /> <form:hidden
                                                    path="mspInterfaceList[${status.index+1}].mspInterface" /></td>
                                            <td><tags:input
                                                    id="interfaceURL${mspPossibleInterfacev5.mspInterface}${mspPossibleInterfacev5.version}"
                                                    path="mspInterfaceList[${status.index+1}].mspEndpoint" size="40" />
                                            </td>
                                            <td><tags:selectWithItems
                                                    id="select${mspPossibleInterfacev5.mspInterface}"
                                                    path="mspInterfaceList[${status.index+1}].version"
                                                    items="${mspVersionList}" inputClass="with-option-hiding" /></td>
                                            <td>
                                                <div class="button-group fr wsnw oh">
                                                    <cti:button icon="icon-ping"
                                                        id="ping${mspPossibleInterfacev5.mspInterface}" name="pingURL"
                                                        renderMode="buttonImage" title="${pingTitle}"
                                                        onclick="yukon.admin.multispeak.executeRequest('${mspPossibleInterfacev3.mspInterface}',this.name,'${mspPossibleInterfacev5.version}');" />
                                                    <cti:button icon="icon-application-view-columns"
                                                        id="getMethods${mspPossibleInterfacev5.mspInterface}"
                                                        name="getMethods" renderMode="buttonImage" title="${getMethods}"
                                                        disabled="${disabled}"
                                                        onclick="yukon.admin.multispeak.executeRequest('${mspPossibleInterfacev3.mspInterface}',this.name,'${mspPossibleInterfacev5.version}');" />
                                                </div>
                                            </td>
                                        </c:when>
                                        <c:otherwise>
                                            <td><tags:checkbox
                                                    path="mspInterfaceList[${status.index}].interfaceEnabled"
                                                    id="${mspPossibleInterfacev3.mspInterface}"
                                                    onclick="yukon.admin.multispeak.enableEndpointValue(this.checked, this.id)" />
                                                <span class="checkBoxLabel"><spring:escapeBody htmlEscape="true">${mspPossibleInterfacev3.mspInterface}</spring:escapeBody></span>
                                                <form:hidden path="mspInterfaceList[${status.index}].vendorID" /> <form:hidden
                                                    path="mspInterfaceList[${status.index}].mspInterface" /></td>
                                            <td><tags:input
                                                    id="interfaceURL${mspPossibleInterfacev3.mspInterface}${mspPossibleInterfacev3.version}"
                                                    disabled="${!mspPossibleInterfacev3.interfaceEnabled}"
                                                    path="mspInterfaceList[${status.index}].mspEndpoint" size="40" /></td>
                                            <td><tags:selectWithItems
                                                    id="select${mspPossibleInterfacev3.mspInterface}"
                                                    path="mspInterfaceList[${status.index}].version" disabled="true"
                                                    items="${mspVersionList}" inputClass="with-option-hiding" /></td>
                                            <td>
                                                <div class="button-group fr wsnw oh">
                                                    <cti:button icon="icon-ping"
                                                        id="ping${mspPossibleInterfacev3.mspInterface}" name="pingURL"
                                                        renderMode="buttonImage" title="${pingTitle}"
                                                        disabled="${!mspPossibleInterfacev3.interfaceEnabled}"
                                                        onclick="yukon.admin.multispeak.executeRequest('${mspPossibleInterfacev3.mspInterface}',this.name,'${mspPossibleInterfacev3.version}');" />
                                                    <cti:button icon="icon-application-view-columns"
                                                        id="getMethods${mspPossibleInterfacev3.mspInterface}"
                                                        name="getMethods" renderMode="buttonImage" title="${getMethods}"
                                                        disabled="${!mspPossibleInterfacev3.interfaceEnabled}"
                                                        onclick="yukon.admin.multispeak.executeRequest('${mspPossibleInterfacev3.mspInterface}',this.name,'${mspPossibleInterfacev3.version}');" />
                                                </div>
                                            </td>
                                        </c:otherwise>
                                    </c:choose>
                                    <c:if test="${status.index == 0}">
                                        <c:set var="interfaceListLength"
                                            value="${fn:length(multispeak.mspInterfaceList)/2}" />
                                        <td rowspan='${interfaceListLength+3}'><textarea cols="49"
                                                rows="${interfaceListLength*2+3}" name="Results" id="results" readonly
                                                wrap="VIRTUAL" style='color:<c:out value="${resultColor}"/>'>${MSP_RESULT_MSG}</textarea>
                                        </td>
                                    </c:if>
                                </tr>
                            </c:forEach>
                            <c:forEach var="mspSingleInterface" items="${multispeak.mspInterfaceList}" varStatus="stat"
                                begin="${fn:length(multispeak.mspInterfaceList)-2}">
                                <tr>
                                    <td><tags:checkbox path="mspInterfaceList[${stat.index}].interfaceEnabled"
                                            id="${mspSingleInterface.mspInterface}"
                                            onclick="yukon.admin.multispeak.enableEndpointValue(this.checked, this.id)" />
                                        <span class="checkBoxLabel"><spring:escapeBody htmlEscape="true">${mspSingleInterface.mspInterface}</spring:escapeBody></span>
                                        <form:hidden path="mspInterfaceList[${stat.index}].vendorID" /> <form:hidden
                                            path="mspInterfaceList[${stat.index}].mspInterface" /></td>
                                    <td><tags:input
                                            id="interfaceURL${mspSingleInterface.mspInterface}${mspSingleInterface.version}"
                                            disabled="${!mspSingleInterface.interfaceEnabled}"
                                            path="mspInterfaceList[${stat.index}].mspEndpoint" size="40" /></td>
                                    <td><c:choose>
                                            <c:when test="${mspSingleInterface.mspInterface=='NOT_Server'}">
                                                <tags:selectWithItems id="select${mspSingleInterface.mspInterface}"
                                                    path="mspInterfaceList[${stat.index}].version"
                                                    disabled="${!mspSingleInterface.interfaceEnabled}"
                                                    items="${mspVersion5}" inputClass="with-option-hiding" />
                                            </c:when>
                                            <c:otherwise>
                                                <tags:selectWithItems id="select${mspSingleInterface.mspInterface}"
                                                    path="mspInterfaceList[${stat.index}].version"
                                                    disabled="${!mspSingleInterface.interfaceEnabled}"
                                                    items="${mspVersion3}" inputClass="with-option-hiding" />
                                            </c:otherwise>
                                        </c:choose></td>
                                    <td>
                                        <div class="button-group fr wsnw oh">
                                            <cti:button icon="icon-ping" id="ping${mspSingleInterface.mspInterface}"
                                                name="pingURL" renderMode="buttonImage" title="${pingTitle}"
                                                disabled="${!mspSingleInterface.interfaceEnabled}"
                                                onclick="yukon.admin.multispeak.executeRequest('${mspPossibleInterfacev3.mspInterface}',this.name,'${mspInterfacev5.version}');" />
                                            <cti:button icon="icon-application-view-columns"
                                                id="getMethods${mspSingleInterface.mspInterface}" name="getMethods"
                                                renderMode="buttonImage" title="${getMethods}"
                                                disabled="${!mspSingleInterface.interfaceEnabled}"
                                                onclick="yukon.admin.multispeak.executeRequest('${mspPossibleInterfacev3.mspInterface}',this.name,'${mspInterfacev5.version}');" />
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </tags:sectionContainer2>
            </c:when>
            <c:otherwise>
                <i:inline key=".noVendorsExist" />
            </c:otherwise>
        </c:choose>
        <div class="page-action-area">
            <cti:displayForPageEditModes modes="VIEW">
                <cti:button nameKey="edit" icon="icon-pencil" id="vendor-edit" />
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
            <cti:displayForPageEditModes modes="EDIT">
                <cti:button nameKey="cancel" href="javascript:window.history.back()"/>
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="CREATE">
                <cti:button type="reset" name="Cancel" value="Cancel" nameKey="cancel" />
            </cti:displayForPageEditModes>
        </div>
    </form:form>
    <cti:url var="url" value="/multispeak/setup/vendorHome/${mspVendor.vendorID}" />
    <form:form id="delete-vendor" method="DELETE" action="${url}">
        <cti:csrfToken />
    </form:form>

</cti:standardPage>