<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.common,modules.adminSetup.auth.user.group">

<table class="full-width striped dashed with-form-controls">
    <thead>
        <tr>
            <th><i:inline key=".username"/></th>
            <th><i:inline key=".authentication"/></th>
            <th><i:inline key=".userStatus"/></th>
            <th></th>
        </tr>
    </thead>
    <tfoot></tfoot>
    <tbody>
        <c:forEach var="user" items="${users.resultList}">
            <cti:url var="url" value="/admin/users/${user.userID}"/>
            <tr>
                <td><a href="${url}">${fn:escapeXml(user.username)}</a></td>
                <td><cti:msg2 key="${authInfo[user.userID].authenticationCategory}"/></td>
                <td>
                    <c:set  var="clazz" value="${user.enabled ? 'success' : 'error'}"/>
                    <span class="${clazz}"><i:inline key="${user.loginStatus}"/></span>
                </td>
                
                <d:confirm on="#remove_${user.userID}" nameKey="remove.user.confirm" 
                    argument="${fn:escapeXml(user.username)}" />
                <td>
                    <cti:button id="remove_${user.userID}" 
                        nameKey="remove" name="remove" classes="fr show-on-hover" 
                        value="${user.userID}" type="submit" 
                        renderMode="buttonImage" icon="icon-remove"/>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>
<tags:pagingResultsControls result="${users}" adjustPageCount="true"/>

</cti:msgScope>