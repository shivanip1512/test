<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="configs">

    <div id="page-actions" class="dn">        
        <cti:url var="url" value="/deviceConfiguration/summary/view"/>
        <cm:dropdownOption key=".summary" href="${url}"/>
    </div>

<%-- DEVICE CONFIGURATIONS --%>
<div class="column-12-12">
    <div class="column one">
        
        <tags:sectionContainer2 nameKey="configs">
            
            <c:choose>
                <c:when test="${empty configurations}">
                    <!-- This shouldn't ever happen, everyone should have a DNP configuration at least. -->
                    <div class="empty-list"><i:inline key=".configDetails.table.noConfigs"/></div>
                </c:when>
                <c:otherwise>
                    <div data-url="configTable">
                        <%@ include file="configTable.jsp" %>
                    </div>
                </c:otherwise>
            </c:choose>
            
            <cti:checkRolesAndProperties value="${editingRoleProperty}">
                <div class="action-area">
                    <cti:url var="url" value="config/create"/>
                    <cti:button nameKey="create" href="${url}" icon="icon-plus-green"/>
                </div>
            </cti:checkRolesAndProperties>
            
        </tags:sectionContainer2>
    </div>
    
    <div class="column two nogutter">
        <%-- CONFIGURATION CATEGORIES --%>
        <tags:sectionContainer2 nameKey="categories">
        
            <c:choose>
                <c:when test="${empty categories}">
                    <!-- This shouldn't ever happen either, everyone should have a DNP category at least. -->
                    <div><i:inline key=".categoryDetails.table.noCategories"/></div>
                </c:when>
                <c:otherwise>
                    <div data-url="categoryTable">
                        <%@ include file="categoryTable.jsp" %>
                    </div>
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
            
        </tags:sectionContainer2>
    </div>
</div>

</cti:standardPage>