<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="userGroupEditor.${mode}">

    <tags:setFormEditMode mode="${mode}"/>

    <div class="column-12-12">
        <div class="column one">
            <form:form commandName="userGroup" action="edit" method="post">
                <cti:csrfToken/>
	            <form:hidden path="userGroupId"/>

                <tags:nameValueContainer2>
                    <tags:inputNameValue nameKey=".userGroupName" path="userGroupName"/>
                    <tags:inputNameValue nameKey=".userGroupDescription" path="userGroupDescription"/>
                </tags:nameValueContainer2>
                
                <div class="page-action-area">
                    <cti:displayForPageEditModes modes="EDIT,CREATE">
                        <cti:button nameKey="save" name="update" type="submit" classes="primary action"/>
                        <c:if test="${isUserGroupDeletable}">
                             <%-- TODO Uncomment this when we implement the role group and user delete functionality.
                             <cti:button nameKey="delete" id="deleteButton" name="delete" name="delete" type="submit"/>
                             <d:confirm on="#deleteButton" nameKey="confirmDelete" argument="${userGroupName}"/>
                            --%>
                        </c:if>
                        <cti:url var="cancelUrl" value="view">
                            <cti:param name="userGroupId" value="${userGroupId}"/>
                        </cti:url>
                        <cti:button nameKey="cancel" href="${cancelUrl}"/>
                    </cti:displayForPageEditModes>
                    <cti:displayForPageEditModes modes="VIEW">
                        <cti:button nameKey="edit" icon="icon-pencil" name="edit" type="submit"/>
                    </cti:displayForPageEditModes>
                </div>
            </form:form>
        </div>
        
        <div class="column two nogutter">
            <cti:displayForPageEditModes modes="VIEW">
                <tags:sectionContainer2 nameKey="rolesContainer">

                    <c:choose>
                        <c:when test="${empty roles}">
                            <i:inline key=".noRoles"/>
                        </c:when>
                        <c:otherwise>
                            <div>
                                <c:forEach var="category" items="${roles}">
                                    <c:if test="${not empty category.key}">
                                        <ul class="grouped-list">
                                            <li><span class="group"><cti:formatObject value="${category.key}"/></span>
                                                <ul class="groupedItem">
                                                    <c:forEach var="roleGroupPair" items="${category.value}">
                                                        <li>
                                                            <cti:url value="/adminSetup/roleGroup/view" var="roleGroupUrl">
                                                                <cti:param name="roleGroupId" value="${roleGroupPair.group.groupID}"/>
                                                            </cti:url>
                                                            <cti:formatObject value="${roleGroupPair.role}"/>
                                                            &nbsp;<a href="${roleGroupUrl}" class="detail wsnw">(${fn:escapeXml(roleGroupPair.group)})</a>
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
</cti:standardPage>