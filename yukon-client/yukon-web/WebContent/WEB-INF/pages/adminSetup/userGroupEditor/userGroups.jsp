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
        $('addUserGroupsForm').submit();
    }
</script>
    
    <tags:boxContainer2 nameKey="groupsContainer" styleClass="groupsContainer">
        <c:choose>
            <c:when test="${!empty userGroups}">
                <form action="/spring/adminSetup/roleGroup/removeUserGroups" method="post">
                    <input type="hidden" name="roleGroupId" value="${roleGroupId}">
                    <table class="compactResultsTable rowHighlighting">
                        <tr>
                            <th><i:inline key=".groupName"/></th>
                            <th><i:inline key=".description"/></th>
                            <th class="removeColumn"><i:inline key=".remove"/></th>
                        </tr>
                        <c:forEach items="${userGroups}" var="userGroup">
                            <cti:url value="/spring/adminSetup/userGroup/view" var="editGroupUrl">
                                <cti:param name="userGroupId" value="${userGroup.userGroupId}"/>
                            </cti:url>
                            <tr class="<tags:alternateRow odd="" even="altTableCell"/>">
                                <td><a href="${editGroupUrl}">${userGroup.userGroupName}</a></td>
                                <td>${userGroup.userGroupDescription}</td>
                                <td class="removeColumn">
                                    <div class="dib">
                                        <input type="submit" name="remove" value="${userGroup.userGroupId}" class="pointer icon icon_remove">
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                </form>
            </c:when>
            <c:otherwise>
                <i:inline key=".noGroups"/>
            </c:otherwise>
        </c:choose>
        <div class="actionArea">
            <form id="addUserGroupsForm" action="/spring/adminSetup/roleGroup/addUserGroups" method="post">
                <input type="hidden" name="userGroupIds" id="userGroupIds">
                <input type="hidden" name="roleGroupId" value="${roleGroupId}">
                <tags:pickerDialog type="userGroupPicker" id="userGroupPicker" alreadyAssignedIds="${alreadyAssignedUserGroupIds}" destinationFieldId="userGroupIds" 
                        linkType="button" nameKey="addUserGroups" multiSelectMode="true" endAction="addUserGroups"/>
            </form>
        </div>
    </tags:boxContainer2>
    
</cti:standardPage>