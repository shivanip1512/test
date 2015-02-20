<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="configs">

    <cti:msg var="popupTitle" key="yukon.common.device.bulk.selectedDevicesPopup.popupTitle" />

    <%-- DEVICE CONFIGURATIONS --%>
    <div class="column-12-12">
        <div class="column one">
            <cti:msg2 key=".existingConfigs" var="configTitle"/>
            <tags:sectionContainer title="${configTitle}">
                <c:choose>
                    <c:when test="${empty configurations}">
                        <!-- This shouldn't ever happen, everyone should have a DNP configuration at least... -->
                        <div><i:inline key=".configDetails.table.noConfigs"/></div>
                    </c:when>
                    <c:otherwise>
                        <table id="configList" class="compact-results-table row-highlighting has-actions">
                            <thead>
                                <tr>
                                    <th><i:inline key=".configDetails.table.configName"/></th>
                                    <th><i:inline key=".configDetails.table.numDevices"/></th>
                                </tr>
                            </thead>
                            <tfoot></tfoot>
                            <tbody>
                                <c:forEach var="config" items="${configurations}">
                                    <tr>
                                        <td>
                                            <cti:url value="config/view" var="viewUrl">
                                                <cti:param name="configId" value="${config.configurationId}"/>
                                            </cti:url>
                                            <a href="${viewUrl}">${fn:escapeXml(config.name)}</a>
                                        </td>
                                        <td>
                                            <span class="fl">${fn:escapeXml(config.numDevices)}</span>
                                            <c:if test="${config.numDevices > 0}">
                                                <c:url var="deviceGroupPopupUrl" value="/bulk/selectedDevicesTableForGroupName">
                                                    <c:set var="fullName" value="/System/Device Configs/${config.name}"/>
                                                    <c:param name="groupName" value="${fullName}"/>
                                                </c:url>
                                                <cti:uniqueIdentifier var="deviceGroupPopupId" prefix="device-group-popup-"/>
                                                <cti:icon icon="icon-magnifier" classes="cp show-on-hover" data-popup="#${deviceGroupPopupId}"/>
                                                <%-- Device Group popup --%> 
                                                <div id="${deviceGroupPopupId}" data-title="${popupTitle}" data-url="${deviceGroupPopupUrl}" data-width="450" data-height="300" class="dn"></div>
                                            </c:if>
                                            
                                            <cm:dropdown triggerClasses="fr">
                                                <cti:url var="groupUrl" value="/group/editor/home">
                                                    <cti:param name="groupName" value="/System/Device Configs/${config.name}"/>
                                                </cti:url>
                                                <cm:dropdownOption icon="icon-folder-magnify" href="${groupUrl}" key=".deviceGroup"/>
                                                <cti:url var="collectionsUrl" value="/bulk/collectionActions">
                                                    <cti:param name="collectionType" value="group"/>
                                                    <cti:param name="group.name" value="/System/Device Configs/${config.name}"/>
                                                </cti:url>
                                                <cm:dropdownOption icon="icon-cog-go" href="${collectionsUrl}" key=".collectionAction"/>
                                            </cm:dropdown>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
                <cti:checkRolesAndProperties value="${editingRoleProperty}">
                    <div class="action-area">
                        <cti:url var="setupUrl" value="config/create"/>
                        <cti:button nameKey="create" href="${setupUrl}" icon="icon-plus-green"/>
                    </div>
                </cti:checkRolesAndProperties>
            </tags:sectionContainer>
        </div>
            
        <div class="column two nogutter">
            <%-- CONFIGURATION CATEGORIES --%>
            <cti:msg2 key=".existingCategories" var="categoryTitle"/>
            <tags:sectionContainer title="${categoryTitle}">
                <c:choose>
                    <c:when test="${empty categories}">
                        <!-- This shouldn't ever happen either, everyone should have a DNP category at least... -->
                        <div><i:inline key=".categoryDetails.table.noCategories"/></div>
                    </c:when>
                    <c:otherwise>
                        <table id="categoryList" class="compact-results-table row-highlighting">
                            <thead>
                                <tr>
                                    <th><i:inline key=".categoryDetails.table.categoryName"/></th>
                                    <th><i:inline key=".categoryDetails.table.categoryType"/></th>
                                    <th><i:inline key=".categoryDetails.table.numAssignments"/></th>
                                </tr>
                            </thead>
                            <tfoot></tfoot>
                            <tbody>
                                <c:forEach var="category" items="${categories}">
                                    <tr>
                                        <td>
                                            <cti:url value="category/view" var="viewUrl">
                                                <cti:param name="categoryId" value="${category.categoryId}"/>
                                            </cti:url>
                                            <a href="${viewUrl}">${fn:escapeXml(category.categoryName)}</a>
                                        </td>
                                        <td>
                                            <cti:msg2 var="catType" key=".category.${category.categoryType}.title"/>
                                            ${fn:escapeXml(catType)}
                                        </td>
                                        <td>
                                            <div id="category-assignments-${category.categoryId}" class="dn">
                                                <c:forEach var="configName" items="${category.configNames}">
                                                    <div class="detail">${fn:escapeXml(configName)}</div>
                                                </c:forEach>
                                            </div>
                                            <div data-tooltip="#category-assignments-${category.categoryId}">
                                                ${fn:length(category.configNames)}
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
                <cti:checkRolesAndProperties value="${editingRoleProperty}">
                    <div class="action-area">
                        <form action="category/create">
                            <cti:button nameKey="create" type="submit" icon="icon-plus-green"/>
                            <select name="categoryType" class="js-init-chosen">
                                <c:forEach var="option" items="${categoryTypes}">
                                    <option value="${option.value}"><cti:msg2 key="${option.formatKey}"/></option>
                                </c:forEach>
                            </select>
                        </form>
                    </div>
                </cti:checkRolesAndProperties>
            </tags:sectionContainer>
        </div>
    </div>
</cti:standardPage>