<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="groupEditor.${mode}">
<tags:setFormEditMode mode="${mode}"/>

<style>
table.groupEditorLayout {
    width: 100%;
}

table.groupEditorLayout td.first,
table.groupEditorLayout td.last {
    vertical-align: top;
    width: 50%;
}

table.groupEditorLayout td.last {
    padding-left: 20px;
}

div.membersContainer {
    max-height: 300px;
    overflow: auto;
    overflow-x: hidden;
}

div.rolesContainer {
    font-size: 11px;
}

div.category {
    padding-bottom: 3px;
    padding-top: 5px;
}

div.category span.controls:hover span.triangleContainer_title{
    text-decoration: underline;
}

span.role {
    vertical-align: middle;
}

div.rolesContainer ul {
    padding-left: 5px;
}

div.rolesContainer input[type='checkbox'] {
    vertical-align: middle;
}

div.rolesContainer img.logoImage {
    padding-left: 5px;
    vertical-align: top;
}

</style>

<script type="text/javascript">

</script>

    <cti:dataGrid cols="2" rowStyle="vertical-align:top;" cellStyle="padding-bottom:10px;" tableClasses="groupEditorLayout">

        <%-- LEFT SIDE COLUMN --%>
        <cti:dataGridCell>

            <tags:formElementContainer nameKey="groupSettings">
                
                <form:form commandName="group" action="/spring/adminSetup/groupEditor/edit" method="post">
                    <form:hidden path="groupID"/>
                    <input type="hidden" value="${group.groupID}" name="groupId">
                    
                    <tags:nameValueContainer2>
    
                        <tags:inputNameValue nameKey=".name" path="groupName"/>
                        <tags:textareaNameValue nameKey=".description" rows="3" cols="35" path="groupDescription"/>
                    
                    </tags:nameValueContainer2>
                    
                    <div class="pageActionArea">
                        <cti:displayForPageEditModes modes="EDIT">
                            <cti:button key="save" name="update" type="submit"/>
                            <cti:button key="delete" name="delete" type="submit"/>
                            <cti:button key="cancel" name="cancel" type="submit"/>
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            <cti:button key="edit" name="edit" type="submit"/>
                        </cti:displayForPageEditModes>
                    </div>
                </form:form>
                
            </tags:formElementContainer>
            
            <br>
            
            <cti:displayForPageEditModes modes="VIEW">
                <tags:boxContainer2 nameKey="membersContainer">
                    <c:choose>
                        <c:when test="${!empty members}">
                            <div class="membersContainer">
                                <table class="compactResultsTable">
                                    <tr>
                                        <th><i:inline key=".username"/></th>
                                        <th><i:inline key=".authtype"/></th>
                                        <th><i:inline key=".loginStatus"/></th>
                                    </tr>
                                    <c:forEach items="${members}" var="member">
                                        <c:choose>
                                            <c:when test="${member.loginStatus == 'ENABLED'}">
                                                <c:set  var="styleClass" value="successMessage"/>
                                            </c:when>
                                            <c:otherwise>
                                                <c:set  var="styleClass" value="errorMessage"/>
                                            </c:otherwise>
                                        </c:choose>
                                        <tr>
                                            <td>${member.username}</td>
                                            <td>${member.authType}</td>
                                            <td><span class="${styleClass}">${member.loginStatus}</span></td>
                                        </tr>
                                    </c:forEach>
                                </table>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <i:inline key=".noMembers"/>
                        </c:otherwise>
                    </c:choose>
                </tags:boxContainer2>
            </cti:displayForPageEditModes>
            
        </cti:dataGridCell>
        
        <!-- RIGHT SIDE COLUMN -->
        <cti:dataGridCell>
            
            <cti:displayForPageEditModes modes="VIEW">
                
                <tags:boxContainer2 nameKey="otherActions">
                    <cti:url value="/spring/adminSetup/groupEditor/permissions" var="permissionsUrl">
                        <cti:param name="groupId" value="${group.groupID}"/>
                    </cti:url>
                    <cti:labeledImg key="editPermissions" href="/spring/editor/group/editGroup?groupId=${group.groupID}"/>
                </tags:boxContainer2>
            
                <br>
                
                <tags:boxContainer2 nameKey="rolesContainer" styleClass="rolesContainer">
                    <c:forEach var="category" items="${categories}">
                        <cti:msg2 var="roleCategory" key=".roleCategory" arguments="${category.name},${category.selectedCount},${fn:length(category.roles)}" argumentSeparator=","/>
                        <tags:hideReveal title="${roleCategory}" showInitially="false" slide="true" styleClass="category">
                            <c:forEach items="${category.roles}" var="role">
                                <ul>
                                    <li>
                                        <cti:url value="/spring/adminSetup/groupEditor/role" var="roleUrl">
                                            <cti:param name="roleId" value="${role.role.roleId}"/>
                                        </cti:url>
                                        <input type="checkbox" disabled="disabled" <c:if test="${role.enabled}">checked="checked"</c:if>>
                                        <span class="role">${role.name} <cti:labeledImg key="edit" href="/spring/support/setup/view"/></span>
                                    </li>
                                </ul>
                            </c:forEach>
                        </tags:hideReveal>
                    </c:forEach>
                </tags:boxContainer2>
                
            </cti:displayForPageEditModes>
        </cti:dataGridCell>
        
    </cti:dataGrid>
    
</cti:standardPage>