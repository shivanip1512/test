<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="adminSetup" page="roleGroupEditor.${mode}">
    
    <tags:setFormEditMode mode="${mode}"/>

    <div class="column-14-10">

        <div class="column one">
            <tags:sectionContainer2 nameKey="infoContainer">
                <cti:url var="groupUrl" value="/adminSetup/roleGroup/edit"/>
                <form:form commandName="group" action="${groupUrl}" method="post">
                    <cti:csrfToken/>
                    <form:hidden path="groupID"/>
                    <input type="hidden" value="${group.groupID}" name="roleGroupId">
                    
                    <tags:nameValueContainer2>
                        
                        <c:choose>
                            <c:when test="${editName}">
                                <tags:inputNameValue nameKey=".name" path="groupName" size="40"/>
                            </c:when>
                            <c:otherwise>
                                <tags:hidden path="groupName"/>
                                <tags:nameValue2 nameKey=".name">${fn:escapeXml(group.groupName)}</tags:nameValue2>
                            </c:otherwise>
                        </c:choose>
                        <tags:textareaNameValue nameKey=".description" rows="3" cols="35" path="groupDescription"/>
                    
                    </tags:nameValueContainer2>
                    
                    <div class="page-action-area">
                        <cti:displayForPageEditModes modes="EDIT">
                            <cti:button nameKey="save" name="update" type="submit" classes="primary action"/>
                            <cti:button nameKey="delete" id="delete-btn" name="delete" type="submit" classes="delete"/>
                            <d:confirm on="#delete-btn" nameKey="confirmDelete" argument="${group.groupName}"/>
                            <cti:button nameKey="cancel" name="cancel" type="submit"/>
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            <cti:button nameKey="edit" icon="icon-pencil" name="edit" type="submit"/>
                            <cti:button nameKey="expireAllPasswords" id="expire-passwords-btn" 
                                    name="expireAllPasswords" type="submit"/>
                            <d:confirm on="#expire-passwords-btn" nameKey="confirmExpireAllPasswords" 
                                    argument="${group.groupName}"/>
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
                            <div class="wsnw">
                                <c:forEach var="category" items="${categoryRoleMap}">
                                    <ul class="grouped-list">
                                        <li><span class="group"><cti:formatObject value="${category.key}"/></span>
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
                    <div class="action-area">
                        <form action="<cti:url value="/adminSetup/roleGroup/addRole"/>" method="post">
                            <cti:csrfToken/>
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
                </tags:sectionContainer2>
                
            </cti:displayForPageEditModes>
        </div>
    </div>
</cti:standardPage>