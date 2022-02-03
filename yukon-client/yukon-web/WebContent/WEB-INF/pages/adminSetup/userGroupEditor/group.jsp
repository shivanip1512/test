<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="adminSetup" page="auth.role.group.${mode}">
<cti:includeScript link="/resources/js/pages/yukon.admin.role.group.js"/>

<tags:setFormEditMode mode="${mode}"/>
<c:set var="groupName" value="${group.groupName}"/>
<c:set var="groupId" value="${group.groupID}"/>

<div class="column-14-10 clearfix">

    <div class="column one">
        <tags:sectionContainer2 nameKey="infoContainer">
            <cti:url var="url" value="/admin/role-groups/${groupId}"/>
            <form:form modelAttribute="group" action="${url}" method="post">
                <cti:csrfToken/>
                <form:hidden path="groupID"/>
                <input id="role-group-id" type="hidden" value="${groupId}" name="roleGroupId">
                
                <tags:nameValueContainer2>
                    
                    <c:choose>
                        <c:when test="${editName}">
                            <tags:inputNameValue nameKey=".name" path="groupName" size="40"/>
                        </c:when>
                        <c:otherwise>
                            <tags:hidden path="groupName"/>
                            <tags:nameValue2 nameKey=".name">${fn:escapeXml(groupName)}</tags:nameValue2>
                        </c:otherwise>
                    </c:choose>
                    <tags:textareaNameValue nameKey=".description" rows="3" cols="35" path="groupDescription"/>
                
                </tags:nameValueContainer2>
                
                <div class="page-action-area">
                    <cti:displayForPageEditModes modes="EDIT">
                        <cti:button nameKey="save" name="update" type="submit" classes="primary action"/>
                        <cti:button nameKey="delete" id="delete-btn" name="delete" type="submit" classes="delete"/>
                        <d:confirm on="#delete-btn" nameKey="confirmDelete" argument="${groupName}"/>
                        <cti:url var="url" value="/admin/role-groups/${groupId}"/>
                        <cti:button nameKey="cancel" href="${url}"/>
                    </cti:displayForPageEditModes>
                    <cti:displayForPageEditModes modes="VIEW">
                        <cti:url var="url" value="/admin/role-groups/${groupId}/edit"/>
                        <cti:button nameKey="edit" icon="icon-pencil" href="${url}"/>
                        <cti:url var="url" value="/admin/role-groups/${groupId}/expire-passwords"/>
                        <cti:button nameKey="expireAllPasswords" id="expire-passwords-btn" href="${url}"/>
                        <d:confirm on="#expire-passwords-btn" nameKey="confirmExpireAllPasswords" argument="${groupName}"/>
                    </cti:displayForPageEditModes>
                </div>
            </form:form>
        </tags:sectionContainer2>
    </div>
        
    <div class="column two nogutter">
        <cti:displayForPageEditModes modes="VIEW">
            
            <tags:sectionContainer2 nameKey="rolesContainer">
                <c:choose>
                    <c:when test="${empty categoryRoleMap}">
                        <div class="empty-list"><i:inline key=".noRoles"/></div>
                    </c:when>
                    <c:otherwise>
                        <div class="wsnw scroll-md">
                            <c:forEach var="category" items="${categoryRoleMap}">
                                <ul class="grouped-list">
                                    <li><span class="group"><cti:formatObject value="${category.key}"/></span>
                                        <ul class="groupedItem">
                                            <c:forEach var="role" items="${category.value}">
                                                <cti:url var="url" value="/admin/role-groups/${roleGroupId}/roles/${role.roleId}"/>
                                                <li><a href="${url}"><i:inline key="${role}"/></a></li>
                                            </c:forEach>
                                        </ul>
                                    </li>
                                </ul>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
                <div class="action-area">
                    <form action="<cti:url value="/admin/role-groups/${roleGroupId}/add-role"/>" method="post">
                        <cti:csrfToken/>
                        <cti:button nameKey="add" type="submit" icon="icon-add"/>
                        <select name="newRoleId">
                            <c:forEach var="availableCategory" items="${availableRolesMap}">
                                <optgroup label="<cti:msg2 key="${availableCategory.key}"/>">
                                    <c:forEach var="availableRole" items="${availableCategory.value}">
                                        <option value="${availableRole.roleId}">
                                            <i:inline key="${availableRole}"/>
                                        </option>
                                    </c:forEach>
                                </optgroup>
                            </c:forEach>
                        </select>
                    </form>
                </div>
            </tags:sectionContainer2>
            
        </cti:displayForPageEditModes>
    </div>
</div>

<cti:displayForPageEditModes modes="VIEW">
<tags:sectionContainer2 nameKey="groupsContainer">
    <c:choose>
        <c:when test="${!empty userGroups}">
            <cti:url var="url" value="/admin/role-groups/${roleGroupId}/remove-user-group"/>
            <form action="${url}" method="post">
                <cti:csrfToken/>
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
                        <c:forEach items="${userGroups}" var="userGroup">
                            <cti:url var="url" value="/admin/user-groups/${userGroup.userGroupId}"/>
                            <tr>
                                <td><a href="${url}">${fn:escapeXml(userGroup.userGroupName)}</a></td>
                                <td>${fn:escapeXml(userGroup.userGroupDescription)}</td>
                                <td>
                                    <cti:button nameKey="remove" name="remove" value="${userGroup.userGroupId}" 
                                        type="submit" classes="fr show-on-hover"
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
        <tags:pickerDialog type="userGroupPicker" id="userGroupPicker" excludeIds="${alreadyAssignedUserGroupIds}" 
            linkType="button" nameKey="add" multiSelectMode="true" icon="icon-add"
            endEvent="yukon:admin:role:group:add:user:groups"/>
    </div>
</tags:sectionContainer2>
</cti:displayForPageEditModes>

</cti:standardPage>