<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="tools" page="configs">
    <%-- DEVICE CONFIGURATIONS --%>
    <div class="column_12_12">
        <div class="column one">
            <cti:msg2 key=".existingConfigs" var="configTitle"/>
            <tags:sectionContainer title="${configTitle}">
                <c:choose>
                    <c:when test="${empty configurations}">
                        <!-- This shouldn't ever happen, everyone should have a DNP configuration at least... -->
                        <div><i:inline key=".configDetails.table.noConfigs"/></div>
                    </c:when>
                    <c:otherwise>
                        <table id="configList" class="compactResultsTable rowHighlighting">
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
                                            <a href="${viewUrl}">${config.name}</a>
                                        </td>
                                        <td>
                                            <spring:escapeBody htmlEscape="true">${config.numDevices}</spring:escapeBody>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
                <cti:checkRolesAndProperties value="${editingRoleProperty}">
                    <div class="actionArea">
                        <cti:url var="setupUrl" value="config/selectTypes"/>
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
                        <table id="categoryList" class="compactResultsTable rowHighlighting">
                            <thead>
                                <tr>
                                    <th><i:inline key=".categoryDetails.table.categoryName"/></th>
                                    <th><i:inline key=".categoryDetails.table.categoryType"/></th>
                                    <th><i:inline key=".categoryDetails.table.numConfigs"/></th>
                                </tr>
                            </thead>
                            <tfoot></tfoot>
                            <tbody>
                                <c:forEach var="category" items="${categories}">
                                    <tr>
                                        <td>
                                            <spring:escapeBody htmlEscape="true">
                                                <cti:url value="category/view" var="viewUrl">
                                                    <cti:param name="categoryId" value="${category.categoryId}"/>
                                                </cti:url>
                                                <a href="${viewUrl}">${category.categoryName}</a>
                                            </spring:escapeBody>
                                        </td>
                                        <td>
                                            <spring:escapeBody htmlEscape="true">
                                                <i:inline key=".category.${category.categoryType}.title"/>
                                            </spring:escapeBody>
                                        </td>
                                        <td>
                                            <spring:escapeBody htmlEscape="true">${category.numConfigurations}</spring:escapeBody>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
                <cti:checkRolesAndProperties value="${editingRoleProperty}">
                    <div class="actionArea">
                        <form action="category/create">
                            <cti:button nameKey="create" type="submit" icon="icon-plus-green"/>
                            <select name="categoryType" id="categoryTypesSelect">
                                <c:forEach var="option" items="${categoryTypes}">
                                    <option value="${option.value}"><cti:msg2 key=".category.${option.value}.title"/></option>
                                </c:forEach>
                            </select>
                        </form>
                    </div>
                </cti:checkRolesAndProperties>
            </tags:sectionContainer>
        </div>
    </div>
</cti:standardPage>