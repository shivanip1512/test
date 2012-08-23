<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="users">

<script type="text/javascript">
function addUsers() {
    $('addUsersForm').submit();
}
</script>
    
    <cti:url value="/spring/adminSetup/userGroup/users" var="usersUrl"/>
    <cti:msg2 var="usersContainerTitle" key=".usersContainer"/>
    
    <tags:pagedBox title="${usersContainerTitle}" searchResult="${searchResult}" isFiltered="false" baseUrl="${usersUrl}" styleClass="usersContainer">
        <c:choose>
            <c:when test="${!empty users}">
                <form action="/spring/adminSetup/userGroup/removeUser" method="post">
                    <input type="hidden" name="userGroupId" value="${userGroupId}">
                    <div class="usersContainer">
                        <table class="compactResultsTable rowHighlighting">
                            <tr>
                                <th><i:inline key=".username"/></th>
                                <th><i:inline key=".authtype"/></th>
                                <th><i:inline key=".loginStatus"/></th>
                                <th class="removeColumn"><i:inline key=".remove"/></th>
                            </tr>
                            <c:forEach items="${users}" var="user">
                                <cti:url value="/spring/adminSetup/user/view" var="editUserUrl">
                                    <cti:param name="userId" value="${user.userID}"/>
                                </cti:url>
                                <c:choose>
                                    <c:when test="${user.loginStatus == 'ENABLED'}">
                                        <c:set  var="styleClass" value="successMessage"/>
                                    </c:when>
                                    <c:otherwise>
                                        <c:set  var="styleClass" value="errorMessage"/>
                                    </c:otherwise>
                                </c:choose>
                                <tr class="<tags:alternateRow odd="" even="altTableCell"/>">
                                    <td><a href="${editUserUrl}">${user.username}</a></td>
                                    <td><cti:formatObject value="${user.authType}"/></td>
                                    <td><span class="${styleClass}"><cti:formatObject value="${user.loginStatus}"/></span></td>
                                    <td class="removeColumn">
                                        <div class="dib">
                                            <input type="submit" name="remove" value="${user.userID}" class="pointer icon icon_remove">
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </table>
                    </div>
                </form>
            </c:when>
            <c:otherwise>
                <i:inline key=".noUsers"/>
            </c:otherwise>
        </c:choose>
        <div class="actionArea">
            <form id="addUsersForm" action="/spring/adminSetup/userGroup/addUsers" method="post">
                <input type="hidden" name="userIds" id="userIds">
                <input type="hidden" name="userGroupId" value="${userGroupId}">
                <tags:pickerDialog type="userPicker" id="userPicker" destinationFieldId="userIds" alreadyAssignedIds="${alreadyAssignedUserIds}" linkType="button" 
                        nameKey="addUsers" multiSelectMode="true" endAction="addUsers"/>

            </form>
        </div>
    </tags:pagedBox>
    
</cti:standardPage>