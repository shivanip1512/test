<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="tools" page="configs.category.${mode}">

<tags:setFormEditMode mode="${mode}"/>

    <form:form modelAttribute="categoryEditBean" id="categoryForm" action="save">
        <cti:csrfToken/>
        <form:hidden path="categoryType"/>
        <form:hidden path="categoryId"/>
        
        <tags:nameValueContainer2 tableClass="stacked">
            <tags:inputNameValue nameKey=".categoryName" path="categoryName" size="50" maxlength="60" nameClass="vat"/>
            <tags:nameValue2 nameKey=".type"><i:inline key=".${categoryTemplate.categoryType}.title"/></tags:nameValue2>
            <tags:nameValue2 nameKey=".definition"><i:inline key=".${categoryTemplate.categoryType}.definition"/></tags:nameValue2>
            <tags:textareaNameValue nameKey=".description" rows="2" cols="50" path="description" nameClass="vat"/>
        </tags:nameValueContainer2>
        
        <cti:displayForPageEditModes modes="VIEW,EDIT">
            <tags:sectionContainer2 nameKey="warning"><i:inline key=".changeWarning"/></tags:sectionContainer2>
        </cti:displayForPageEditModes>
        
        <div class="column-6-18 clearfix">
            <div class="column one">
                <tags:sectionContainer2 nameKey="assignments">
                    <c:choose>
                        <c:when test="${empty categoryEditBean.assignments}">
                            <div class="empty-list"><i:inline key=".noAssignments"/></div>
                        </c:when>
                        <c:otherwise>
                            <ul class="simple-list">
                                <c:forEach var="assignment" items="${categoryEditBean.assignments}">
                                    <li>${fn:escapeXml(assignment)}</li>
                                </c:forEach>
                            </ul>
                        </c:otherwise>
                    </c:choose>
                </tags:sectionContainer2>
            </div>
            <div class="column two nogutter">
                <tags:sectionContainer2 nameKey="categoryFields">
                    <%@ include file="category.jspf" %>
                </tags:sectionContainer2>
            </div>
        </div>
        
        <cti:displayForPageEditModes modes="VIEW">
            <div class="page-action-area clear">
                <cti:checkRolesAndProperties value="${editingRoleProperty}">
                    <cti:url var="editUrl" value="edit">
                        <cti:param name="categoryId" value="${categoryEditBean.categoryId}"/>
                    </cti:url>
                    <cti:button nameKey="edit" href="${editUrl}" icon="icon-pencil"/>
                </cti:checkRolesAndProperties>
                <cti:url var="backUrl" value="/deviceConfiguration/home"/>
                <cti:button nameKey="back" href="${backUrl}"/>
            </div>
        </cti:displayForPageEditModes>
        <cti:displayForPageEditModes modes="EDIT">
            <cti:checkRolesAndProperties value="${editingRoleProperty}">
                <div class="page-action-area clear">
                    <cti:url var="viewUrl" value="view">
                        <cti:param name="categoryId" value="${categoryEditBean.categoryId}"/>
                    </cti:url>
                    <cti:button nameKey="save" id="save" type="submit" classes="primary action"/>
                    <c:if test="${isDeletable}">
                        <d:confirm on="#remove" nameKey="confirmRemove"/>
                        <cti:url var="deleteUrl" value="delete">
                            <cti:param name="categoryId" value="${categoryEditBean.categoryId}"/>
                        </cti:url>
                        <cti:button nameKey="delete" id="remove" href="${deleteUrl}" disabled="${disabled}" classes="delete"/>
                    </c:if>
                    <cti:button nameKey="cancel" href="${viewUrl}"/>
                </div>
            </cti:checkRolesAndProperties>
        </cti:displayForPageEditModes>
        <cti:displayForPageEditModes modes="CREATE">
            <cti:checkRolesAndProperties value="${editingRoleProperty}">
                <div class="page-action-area clear">
                    <cti:button nameKey="create" type="submit" classes="primary action"/>
                    <cti:url var="homeUrl" value="/deviceConfiguration/home" />
                    <cti:button nameKey="cancel" href="${homeUrl}"/>
                </div>
            </cti:checkRolesAndProperties>
        </cti:displayForPageEditModes>
    </form:form>
</cti:standardPage>