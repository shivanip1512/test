<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="roleGroups">

    <script type="text/javascript">
        function addRoleGroups() {
        	jQuery('#addGroupsForm').submit();
        }
    </script>
    
    <tags:boxContainer2 nameKey="groupsContainer" styleClass="groupsContainer">
        <c:choose>
            <c:when test="${!empty groups}">
                <form action="/adminSetup/userGroup/removeRoleGroup" method="post">
                    <input type="hidden" name="userGroupId" value="${userGroupId}">
                    <table class="compactResultsTable rowHighlighting">
                        <tr>
                            <th><i:inline key=".groupName"/></th>
                            <th><i:inline key=".description"/></th>
                            <th class="removeColumn"><i:inline key=".remove"/></th>
                        </tr>
                        <c:forEach items="${groups}" var="group">
                            <cti:url value="/adminSetup/roleGroup/view" var="editGroupUrl">
                                <cti:param name="roleGroupId" value="${group.groupID}"/>
                            </cti:url>
                            <tr class="<tags:alternateRow odd="" even="altTableCell"/>">
                                <td><a href="${editGroupUrl}">${fn:escapeXml(group.groupName)}</a></td>
                                <td>${fn:escapeXml(group.groupDescription)}</td>
                                <td class="removeColumn">
                                    <div class="dib">
                                        <input type="submit" name="remove" value="${group.groupID}" class="pointer icon icon_remove">
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
            <form id="addGroupsForm" action="/adminSetup/userGroup/addRoleGroups" method="post">
                <input type="hidden" name="roleGroupIds" id="roleGroupIds">
                <input type="hidden" name="userGroupId" value="${userGroupId}">
                <tags:pickerDialog type="loginGroupPicker" id="loginGroupPicker" destinationFieldId="roleGroupIds" excludeIds="${alreadyAssignedRoleGroupIds}" 
                        linkType="button" nameKey="addGroups" multiSelectMode="true" endAction="addRoleGroups"/>
            </form>
        </div>
    </tags:boxContainer2>
    
</cti:standardPage>