<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="userGroupEditor.${mode}">

    <tags:setFormEditMode mode="${mode}"/>

    <cti:dataGrid cols="2" tableClasses="twoColumnLayout">
        <cti:dataGridCell>

            <form:form commandName="userGroup" action="edit" method="post">
                <form:hidden path="liteUserGroup.userGroupId"/>
                <input type="hidden" value="${userGroupId}" name="userGroupId">

                <tags:nameValueContainer2>
                    <tags:inputNameValue nameKey=".userGroupName" path="liteUserGroup.userGroupName"/>
                    <tags:inputNameValue nameKey=".userGroupDescription" path="liteUserGroup.userGroupDescription"/>
                
                </tags:nameValueContainer2>
                
                <div class="pageActionArea">
                    <cti:displayForPageEditModes modes="EDIT,CREATE">
                        <cti:button nameKey="save" name="update" type="submit"/>
                        <c:if test="${isUserGroupDeletable}">
                            <cti:button nameKey="delete" name="delete" type="submit"/>
                        </c:if>
                        <cti:button nameKey="cancel" name="cancel" type="submit"/>
                    </cti:displayForPageEditModes>
                    <cti:displayForPageEditModes modes="VIEW">
                        <cti:button nameKey="edit" name="edit" type="submit"/>
                    </cti:displayForPageEditModes>
                </div>
            </form:form>

        </cti:dataGridCell>
        
        
        
        <cti:dataGridCell>
            <cti:displayForPageEditModes modes="VIEW">
                <tags:boxContainer2 nameKey="rolesContainer">

                    <c:choose>
                        <c:when test="${empty roles}">
                            <i:inline key=".noRoles"/>
                        </c:when>
                        <c:otherwise>
                            <div class="rolesContainer">
                                <c:forEach var="category" items="${roles}">
                                    <ul class="category">
                                        <li><span class="categoryLabel">${category.key}</span>
                                            <ul class="role">
                                                <c:forEach var="roleGroupPair" items="${category.value}">
                                                    <li>
                                                        <cti:url value="/spring/adminSetup/roleGroup/view" var="roleGroupUrl">
                                                            <cti:param name="roleGroupId" value="${roleGroupPair.second.groupID}"/>
                                                        </cti:url>
                                                        <cti:formatObject value="${roleGroupPair.first}"/>
                                                        &nbsp;<a href="${roleGroupUrl}"><spring:escapeBody htmlEscape="true">(${roleGroupPair.second})</spring:escapeBody></a>
                                                    </li>
                                                </c:forEach>
                                            </ul>
                                        </li>
                                    </ul>
                                </c:forEach>
                            </div>
                        </c:otherwise>
                    </c:choose>

                </tags:boxContainer2>
            </cti:displayForPageEditModes>
            
        </cti:dataGridCell>
    </cti:dataGrid>
</cti:standardPage>