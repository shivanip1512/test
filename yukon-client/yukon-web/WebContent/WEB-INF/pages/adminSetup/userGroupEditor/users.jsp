<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="users">

<script type="text/javascript">
function addUsers() {
    jQuery('#addUsersForm').submit();
}
</script>
    
    <cti:url var="baseUrl" value="/adminSetup/userGroup/users" />
    <cti:msg2 var="usersContainerTitle" key=".usersContainer"/>

    <tags:pagedBox title="${usersContainerTitle}" searchResult="${searchResult}" isFiltered="false" baseUrl="${baseUrl}" styleClass="usersContainer">
        <c:choose>
            <c:when test="${!empty users}">
                <form action="/adminSetup/userGroup/removeUser" method="post">
                    <cti:csrfToken/>
                    <input type="hidden" name="userGroupId" value="${userGroupId}">
                    <div class="usersContainer">
                        <table class="compact-results-table row-highlighting">
                            <thead>
                                <tr>
                                    <th><i:inline key=".username"/></th>
                                    <th><i:inline key=".authentication"/></th>
                                    <th><i:inline key=".userStatus"/></th>
                                    <th class="remove-column"><i:inline key=".remove"/></th>
                                </tr>
                            </thead>
                            <tfoot></tfoot>
                            <tbody>
                                <c:forEach items="${users}" var="user">
                                    <cti:url value="/adminSetup/user/view" var="editUserUrl">
                                        <cti:param name="userId" value="${user.userID}"/>
                                    </cti:url>
                                    <c:choose>
                                        <c:when test="${user.loginStatus == 'ENABLED'}">
                                            <c:set  var="styleClass" value="success"/>
                                        </c:when>
                                        <c:otherwise>
                                            <c:set  var="styleClass" value="error"/>
                                        </c:otherwise>
                                    </c:choose>
                                    <tr>
                                        <td><a href="${editUserUrl}">${fn:escapeXml(user.username)}</a></td>
                                        <td><cti:msg2 key="${userAuthenticationInfo[user.userID].authenticationCategory}"/></td>
                                        <td><span class="${styleClass}"><cti:formatObject value="${user.loginStatus}"/></span></td>
    
                                        <dialog:confirm on="#remove_${user.userID}" nameKey="confirmRemove" argument="${fn:escapeXml(user.username)}" />
                                        <td class="remove-column">
                                            <div class="dib">
                                                <cti:button id="remove_${user.userID}" nameKey="remove" name="remove" value="${user.userID}" type="submit" renderMode="image" icon="icon-cross"/>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </form>
            </c:when>
            <c:otherwise>
                <i:inline key=".noUsers"/>
            </c:otherwise>
        </c:choose>
        <div class="action-area">
            <form id="addUsersForm" action="/adminSetup/userGroup/addUsers" method="post">
                <cti:csrfToken/>
                <input type="hidden" name="userIds" id="userIds">
                <input type="hidden" name="userGroupId" value="${userGroupId}">
                <tags:pickerDialog type="userPicker" id="userPicker" destinationFieldId="userIds" excludeIds="${alreadyAssignedUserIds}" linkType="button" 
                        nameKey="addUsers" multiSelectMode="true" endAction="addUsers"/>

            </form>
        </div>
    </tags:pagedBox>
    
</cti:standardPage>