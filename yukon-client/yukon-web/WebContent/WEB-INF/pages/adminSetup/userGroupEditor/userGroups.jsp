<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="userGroups">

<script type="text/javascript">
    function addUserGroups() {
    	$('#addUserGroupsForm').submit();
    }
</script>
    
    <tags:boxContainer2 nameKey="groupsContainer" styleClass="groupsContainer">
        <c:choose>
            <c:when test="${!empty userGroups}">
                <form action="/adminSetup/roleGroup/removeUserGroups" method="post">
                    <cti:csrfToken/>
                    <input type="hidden" name="roleGroupId" value="${roleGroupId}">
                    <table class="compact-results-table row-highlighting">
                        <thead>
                            <tr>
                                <th><i:inline key=".groupName"/></th>
                                <th><i:inline key=".description"/></th>
                                <th class="remove-column"><i:inline key=".remove"/></th>
                            </tr>
                        </thead>
                        <tfoot></tfoot>
                        <tbody>
                            <c:forEach items="${userGroups}" var="userGroup">
                                <cti:url value="/adminSetup/userGroup/view" var="editGroupUrl">
                                    <cti:param name="userGroupId" value="${userGroup.userGroupId}"/>
                                </cti:url>
                                <tr>
                                    <td><a href="${editGroupUrl}">${fn:escapeXml(userGroup.userGroupName)}</a></td>
                                    <td>${fn:escapeXml(userGroup.userGroupDescription)}</td>
                                    <td class="remove-column">
                                        <div class="dib">
                                            <cti:button nameKey="remove" name="remove" value="${userGroup.userGroupId}" type="submit" renderMode="image" icon="icon-cross"/>
                                        </div>
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
            <form id="addUserGroupsForm" action="/adminSetup/roleGroup/addUserGroups" method="post">
                <cti:csrfToken/>
                <input type="hidden" name="userGroupIds" id="userGroupIds">
                <input type="hidden" name="roleGroupId" value="${roleGroupId}">
                <tags:pickerDialog type="userGroupPicker" id="userGroupPicker" excludeIds="${alreadyAssignedUserGroupIds}" destinationFieldId="userGroupIds" 
                        linkType="button" nameKey="addUserGroups" multiSelectMode="true" endAction="addUserGroups"/>
            </form>
        </div>
    </tags:boxContainer2>
    
</cti:standardPage>