<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="groups">

<script type="text/javascript">

function addGroups() {
    $('addGroupsForm').submit();
}

</script>
    
    <tags:boxContainer2 nameKey="groupsContainer" styleClass="groupsContainer">
        <c:choose>
            <c:when test="${!empty groups}">
                <form action="/spring/adminSetup/userEditor/removeGroup" method="post">
                    <input type="hidden" name="userId" value="${userId}">
                    <table class="compactResultsTable rowHighlighting">
                        <tr>
                            <th><i:inline key=".groupName"/></th>
                            <th><i:inline key=".description"/></th>
                            <th class="removeColumn"><i:inline key=".remove"/></th>
                        </tr>
                        <c:forEach items="${groups}" var="group">
                            <cti:url value="/spring/adminSetup/groupEditor/view" var="editGroupUrl">
                                <cti:param name="groupId" value="${group.groupID}"/>
                            </cti:url>
                            <tr class="<tags:alternateRow odd="" even="altTableCell"/>">
                                <td><a href="${editGroupUrl}">${group.groupName}</a></td>
                                <td>${group.groupDescription}</td>
                                <td class="removeColumn">
                                    <input type="image" name="remove" src="/WebConfig/yukon/Icons/delete.png" value="${group.groupID}" 
                                        class="pointer hoverableImage">
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                </form>
            </c:when>
            <c:otherwise>
                <i:inline key=".noMembers"/>
            </c:otherwise>
        </c:choose>
        <div class="actionArea">
            <form id="addGroupsForm" action="/spring/adminSetup/userEditor/addGroups" method="post">
                <input type="hidden" name="groupIds" id="groupIds">
                <input type="hidden" name="userId" value="${userId}">
                <tags:pickerDialog type="loginGroupPicker" id="loginGroupPicker" destinationFieldId="groupIds" linkType="button" 
                        nameKey="addGroups" multiSelectMode="true" endAction="addGroups"/>
            </form>
        </div>
    </tags:boxContainer2>
    
</cti:standardPage>