<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>

<cti:standardPage module="adminSetup" page="roleGroupEditor.${mode}">
    
    <tags:setFormEditMode mode="${mode}"/>

    <div class="column_14_10">

        <div class="column one">
            <form:form commandName="group" action="/adminSetup/roleGroup/edit" method="post">
                <form:hidden path="groupID"/>
                <input type="hidden" value="${group.groupID}" name="roleGroupId">
                
                <tags:nameValueContainer2>
                    
                    <c:choose>
                        <c:when test="${editName}">
                            <tags:inputNameValue nameKey=".name" path="groupName" size="40"/>
                        </c:when>
                        <c:otherwise>
                            <tags:hidden path="groupName"/>
                            <tags:nameValue2 nameKey=".name">
                                <spring:escapeBody htmlEscape="true">${group.groupName}</spring:escapeBody>
                            </tags:nameValue2>
                        </c:otherwise>
                    </c:choose>
                    <tags:textareaNameValue nameKey=".description" rows="3" cols="35" path="groupDescription"/>
                
                </tags:nameValueContainer2>
                
                <div class="pageActionArea">
                    <cti:displayForPageEditModes modes="EDIT">
                        <cti:button nameKey="save" name="update" type="submit"/>
                        <%-- TODO implement this later 
                        <cti:button nameKey="delete" id="deleteButton" name="delete" type="submit"/>
                        <d:confirm on="#deleteButton" nameKey="confirmDelete" argument="${groupName}"/>
                        --%>
                        <cti:button nameKey="cancel" name="cancel" type="submit"/>
                    </cti:displayForPageEditModes>
                    <cti:displayForPageEditModes modes="VIEW">
                        <cti:button nameKey="edit" icon="icon-pencil" name="edit" type="submit"/>
                    </cti:displayForPageEditModes>
                    <cti:button nameKey="expireAllPasswords" id="expireAllPasswordsButton" name="expireAllPasswords" type="submit"/>
                    <d:confirm on="#expireAllPasswordsButton" nameKey="confirmExpireAllPasswords" argument="${group.groupName}"/>
                    
                </div>
            </form:form>
                
        </div>
            
        <div class="column two nogutter">
            <cti:displayForPageEditModes modes="VIEW">
                
                <tags:boxContainer2 nameKey="rolesContainer">
                    <c:choose>
                        <c:when test="${empty categoryRoleMap}">
                            <i:inline key=".noRoles"/>
                        </c:when>
                        <c:otherwise>
                            <div class="rolesContainer wsnw">
                                <c:forEach var="category" items="${categoryRoleMap}">
                                    <ul class="groupedList">
                                        <li><span class="group detail"><cti:formatObject value="${category.key}"/></span>
                                            <ul class="groupedItem">
                                                <c:forEach var="role" items="${category.value}">
                                                    <li class="detail">
                                                        <cti:url value="/adminSetup/roleEditor/view" var="roleUrl">
                                                            <cti:param name="roleId" value="${role.roleId}"/>
                                                            <cti:param name="roleGroupId" value="${roleGroupId}"/>
                                                        </cti:url>
                                                        <a href="${roleUrl}"><cti:formatObject value="${role}"/></a>
                                                    </li>
                                                </c:forEach>
                                            </ul>
                                        </li>
                                    </ul>
                                </c:forEach>
                            </div>
                        </c:otherwise>
                    </c:choose>
                    <div class="actionArea">
                        <form action="/adminSetup/roleGroup/addRole" method="post">
                            <input type="hidden" value="${roleGroupId}" name="roleGroupId">
                            <cti:button nameKey="add" type="submit" id="addButton" icon="icon-add"/>
                            <select name="newRoleId">
                                <c:forEach var="availableCategory" items="${availableRolesMap}">
                                    <optgroup label="<cti:formatObject value="${availableCategory.key}"/>">
                                        <c:forEach var="availableRole" items="${availableCategory.value}">
                                            <option value="${availableRole.roleId}"><cti:formatObject value="${availableRole}"/></option>
                                        </c:forEach>
                                    </optgroup>
                                </c:forEach>
                            </select>
                        </form>
                    </div>
                </tags:boxContainer2>
                
            </cti:displayForPageEditModes>
        </div>
    </div>
</cti:standardPage>