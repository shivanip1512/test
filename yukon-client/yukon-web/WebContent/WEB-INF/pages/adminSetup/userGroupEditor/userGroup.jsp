<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="adminSetup" page="auth.user.group.${mode}">
<cti:includeScript link="/resources/js/pages/yukon.admin.user.group.js"/>

<tags:setFormEditMode mode="${mode}"/>

<div class="column-12-12 clearfix stacked-lg">
    <div class="column one">
        <tags:sectionContainer2 nameKey="infoContainer">
            <cti:url var="url" value="/admin/user-groups/${userGroupId}"/>
            <form:form modelAttribute="userGroup" action="${url}" method="post">
                <cti:csrfToken/>
                <form:hidden id="user-group-id" path="userGroupId"/>
                
                <tags:nameValueContainer2>
                    <tags:inputNameValue nameKey=".userGroupName" path="userGroupName"/>
                    <tags:inputNameValue nameKey=".userGroupDescription" path="userGroupDescription"/>
                </tags:nameValueContainer2>
                
                <cti:displayForPageEditModes modes="VIEW">
                    <c:if test="${showPermissions}">
                        <div class="action-area">
                            <cti:url var="url" value="/admin/user-groups/${userGroupId}/permissions"/>
                            <a href="${url}"><i:inline key=".permissions.view"/></a>
                        </div>
                    </c:if>
                </cti:displayForPageEditModes>
                
                <div class="page-action-area">
                    <cti:displayForPageEditModes modes="EDIT,CREATE">
                        <cti:button nameKey="save" name="update" type="submit" classes="primary action"/>
                        <c:if test="${isUserGroupDeletable}">
                             <cti:button nameKey="delete" id="delete-btn" name="delete" type="submit"
                                 classes="delete"/>
                             <d:confirm on="#delete-btn" nameKey="confirmDelete" argument="${userGroupName}"/>
                        </c:if>
                        <cti:url var="url" value="/admin/user-groups/${userGroupId}"/>
                        <cti:button nameKey="cancel" href="${url}"/>
                    </cti:displayForPageEditModes>
                    <cti:displayForPageEditModes modes="VIEW">
                        <cti:url var="url" value="/admin/user-groups/${userGroupId}/edit"/>
                        <cti:button nameKey="edit" icon="icon-pencil" href="${url}"/>
                    </cti:displayForPageEditModes>
                </div>
            </form:form>
            
        </tags:sectionContainer2>
    </div>
    
    <div class="column two nogutter">
        <cti:displayForPageEditModes modes="VIEW">
            <tags:sectionContainer2 nameKey="rolesContainer">
                
                <c:choose>
                    <c:when test="${empty roles}">
                        <i:inline key=".noRoles"/>
                    </c:when>
                    <c:otherwise>
                        <div class="scroll-md">
                            <c:forEach var="category" items="${roles}">
                                <c:if test="${not empty category.key}">
                                    <ul class="grouped-list">
                                        <li><span class="group"><cti:formatObject value="${category.key}"/></span>
                                            <ul class="groupedItem">
                                                <c:forEach var="roleGroupPair" items="${category.value}">
                                                    <li>
                                                        <cti:url var="url" value="/admin/role-groups/${roleGroupPair.group.groupID}"/>
                                                        <i:inline key="${roleGroupPair.role}"/>
                                                        &nbsp;<a href="${url}" class="detail wsnw">(${fn:escapeXml(roleGroupPair.group)})</a>
                                                    </li>
                                                </c:forEach>
                                            </ul>
                                        </li>
                                    </ul>
                                </c:if>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
                
            </tags:sectionContainer2>
        </cti:displayForPageEditModes>
        
    </div>
</div>

<%-- USERS --%>
<cti:displayForPageEditModes modes="VIEW">
<tags:sectionContainer2 nameKey="users" styleClass="stacked-lg">
    <c:choose>
        <c:when test="${fn:length(users.resultList) > 0}">
            <cti:url var="url" value="/admin/user-groups/${userGroupId}/remove-user"/>
            <form action="${url}" method="post">
                <cti:csrfToken/>
                <input type="hidden" name="userGroupId" value="${userGroupId}">
                <cti:url var="url" value="/admin/user-groups/${userGroupId}/users"/>
                <div data-url="${url}">
                    <%@ include file="users.jsp" %>
                </div>
            </form>
        </c:when>
        <c:otherwise>
            <div class="empty-list"><i:inline key=".noUsers"/></div>
        </c:otherwise>
    </c:choose>
    <div class="action-area">
            <tags:pickerDialog type="userPicker" id="userPicker" excludeIds="${alreadyAssignedUserIds}" 
                linkType="button" nameKey="add" multiSelectMode="true" icon="icon-add"
                endEvent="yukon:admin:user:group:add:user"/>
    </div>
</tags:sectionContainer2>

<%-- ROLE GROUPS --%>
<tags:sectionContainer2 nameKey="role.groups" styleClass="stacked">
    <c:choose>
        <c:when test="${!empty groups}">
            <cti:url var="url" value="/admin/user-groups/${userGroupId}/remove-role-group"/>
            <form action="${url}" method="post">
                <cti:csrfToken/>
                <input type="hidden" name="userGroupId" value="${userGroupId}">
                <table class="full-width striped dashed with-form-controls">
                    <thead>
                        <tr>
                            <th><i:inline key=".groupName"/></th>
                            <th><i:inline key=".description"/></th>
                            <th></th>
                        </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:forEach var="group" items="${groups}">
                            <cti:url var="url" value="/admin/role-groups/${group.groupID}"/>
                            <tr>
                                <td><a href="${url}">${fn:escapeXml(group.groupName)}</a></td>
                                <td>${fn:escapeXml(group.groupDescription)}</td>
                                <td>
                                    <cti:button id="remove_${user.userID}" 
                                        nameKey="remove" name="remove" classes="fr show-on-hover" 
                                        value="${group.groupID}" type="submit" 
                                        renderMode="buttonImage" icon="icon-cross"/>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </form>
        </c:when>
        <c:otherwise>
            <div class="empty-list"><i:inline key=".noGroups"/></div>
        </c:otherwise>
    </c:choose>
    <div class="action-area">
        <tags:pickerDialog type="loginGroupPicker" id="loginGroupPicker" excludeIds="${alreadyAssignedRoleGroupIds}" 
                linkType="button" nameKey="add" multiSelectMode="true" icon="icon-add"
                endEvent="yukon:admin:user:group:add:role:group"/>
    </div>
</tags:sectionContainer2>
</cti:displayForPageEditModes>
    
</cti:standardPage>