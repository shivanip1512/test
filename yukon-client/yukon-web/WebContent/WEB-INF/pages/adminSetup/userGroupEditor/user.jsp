<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="adminSetup" page="auth.user.${mode}">
<cti:includeScript link="/JavaScript/yukon.admin.user.js"/>

<cti:msg2 var="none" key="yukon.common.none.choice"/>

<%-- CHANGE PASSWORD POPUP --%>
<cti:msg2 var="passwordTitle" key=".changePasswordPopup" arguments="${user.username}"/>
<div class="dn" id="change-password-popup" data-dialog 
        data-title="${passwordTitle}"
        data-ok-class="js-save-pw-btn"
        data-event="yukon:admin:user:password:save"
        data-ok-disabled="true">
    <tags:setFormEditMode mode="${pwChangeFormMode}"/>
    <c:if test="${not empty passwordErrorMsg}">
        <tags:alertBox>${fn:escapeXml(passwordErrorMsg)}</tags:alertBox>
    </c:if>
    <div class="column-12-12">
        <div class="column one">
            <cti:url var="url" value="/admin/users/${userId}/change-password"/>
            <form:form id="change-password-form" commandName="password" action="${url}" method="post">
                <cti:csrfToken/>
                <input type="hidden" name="userId" value="${userId}">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".password">
                        <tags:password path="password" cssClass="js-new-password" autocomplete="off"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".confirmPassword">
                        <tags:password path="confirmPassword" cssClass="js-confirm-password" autocomplete="off"/>
                    </tags:nameValue2>
                    <tags:nameValue2 excludeColon="true">
                        <div class="js-password-mismatch error">
                            <i:inline key="yukon.web.modules.passwordPolicy.noMatch.description"/>
                        </div>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </form:form>
        </div>
        <div class="column two nogutter">
            <tags:passwordHelper passwordPolicy="${passwordPolicy}" userId="${userId}" saveButton=".js-save-pw-btn"/>
        </div>
    </div>
</div>

<tags:setFormEditMode mode="${mode}"/>

<div class="column-12-12">

    <div class="column one">
        
        <tags:sectionContainer2 nameKey="infoContainer" styleClass="stacked-lg">
            <cti:url var="url" value="/admin/users/${user.userId}"/>
            <form:form commandName="user" action="${url}" method="post">
                <cti:csrfToken/>
                <form:hidden path="userId"/>
                <tags:hidden path="password.password"/>
                <tags:hidden path="password.confirmPassword"/>
                
                <tags:nameValueContainer2>
                    
                    <c:choose>
                        <c:when test="${editNameAndStatus}">
                            <tags:inputNameValue nameKey=".username" path="username"/>
                        </c:when>
                        <c:otherwise>
                            <tags:hidden path="username"/>
                            <tags:nameValue2 nameKey=".username">${fn:escapeXml(user.username)}</tags:nameValue2>
                        </c:otherwise>
                    </c:choose>
                    
                    <tags:selectNameValue inputClass="js-auth-category" path="authCategory" nameKey=".authentication" 
                        items="${authenticationCategories}"/>
                    
                    <cti:displayForPageEditModes modes="EDIT,CREATE">
                        <tags:selectNameValue nameKey=".userGroup" items="${userGroups}" itemValue="userGroupId" 
                            itemLabel="userGroupName" path="userGroupId" 
                            defaultItemLabel="${none}" defaultItemValue=""/>
                    </cti:displayForPageEditModes>
                    <cti:displayForPageEditModes modes="VIEW">
                        <tags:nameValue2 nameKey=".userGroup">
                            <cti:url var="url" value="/admin/user-groups/${user.userGroupId}"/>
                            <a href="${url}">${fn:escapeXml(userGroupName)}</a>
                        </tags:nameValue2>
                    </cti:displayForPageEditModes>
                    
                    <c:choose>
                        <c:when test="${editNameAndStatus && currentUserId != user.userId}">
                            <c:choose>
                                <c:when test="${mode == 'VIEW'}">
                                    <tags:nameValue2 nameKey=".userStatus">
                                        <c:set var="clazz" value="${user.enabled ? 'success' : 'error'}"/>
                                        <span class="${clazz}"><i:inline key="${user.loginStatus}"/></span>
                                    </tags:nameValue2>
                                </c:when>
                                <c:otherwise>
                                    <tags:selectNameValue nameKey=".userStatus" items="${loginStatusTypes}" path="loginStatus"/>
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:otherwise>
                            <tags:nameValue2 nameKey=".userStatus">
                                <tags:hidden path="loginStatus"/>
                                <c:set var="clazz" value="${user.enabled ? 'success' : 'error'}"/>
                                <span class="${clazz}"><i:inline key="${user.loginStatus}"/></span>
                            </tags:nameValue2>
                        </c:otherwise>
                    </c:choose>
                    
                </tags:nameValueContainer2>
                
                <cti:displayForPageEditModes modes="VIEW">
                    <c:if test="${showPermissions}">
                        <div class="action-area">
                            <cti:url var="url" value="/admin/users/${user.userId}/permissions"/>
                            <a href="${url}"><i:inline key=".permissions.view"/></a>
                        </div>
                    </c:if>
                </cti:displayForPageEditModes>
                
                <div class="page-action-area">
                    <cti:displayForPageEditModes modes="EDIT,CREATE">
                        
                        <cti:button nameKey="save" name="update" type="submit" classes="primary action"/>
                        
                        <c:set var="disabled" value="${currentUserId == userId}"/>
                        <cti:button nameKey="delete" name="delete" type="submit" classes="delete js-delete-user"
                            disabled="${disabled}"/>
                        <d:confirm on=".js-delete-user" nameKey="delete.confirm"/>
                        
                        <cti:url var="url" value="/admin/users/${user.userId}"/>
                        <cti:button nameKey="cancel" href="${url}"/>
                        
                    </cti:displayForPageEditModes>
                    <cti:displayForPageEditModes modes="VIEW">
                        
                        <cti:url var="url" value="/admin/users/${user.userId}/edit"/>
                        <cti:button nameKey="edit" icon="icon-pencil" href="${url}"/>
                        
                        <c:if test="${supportsPasswordSet[user.authCategory]}">
                            <cti:button nameKey="changePassword" data-popup="#change-password-popup" icon="icon-key"/>
                        </c:if>
                        
                        <cti:url var="url" value="/admin/users/${user.userId}/unlock"/>
                        <cti:button href="${url}" nameKey="unlockUser" icon="icon-lock-open"/>
                        
                    </cti:displayForPageEditModes>
                </div>
            </form:form>
        </tags:sectionContainer2>
            
            <c:if test="${not empty throttling}">
                <tags:sectionContainer2 id="login-throttle" nameKey="login.throttle">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".lastFailed">
                            <cti:formatDate type="DATEHM" value="${throttling.lastFailedLoginTime}"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".numFailed">
                            ${throttling.retryCount}
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".waitDuration">
                            <c:choose>
                                <c:when test="${throttling.throttleDurationSeconds == 0}">
                                    <i:inline key="yukon.common.none.choice"/>
                                </c:when>
                                <c:otherwise>
                                    <cti:formatDuration type="DHMS_REDUCED" 
                                        value="${throttling.throttleDurationSeconds * 1000}"/>
                                </c:otherwise>
                            </c:choose>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".retryEndTime">
                            <c:if test="${not empty throttling.throttleEndtime}">
                                <cti:formatDate type="DATEHM" value="${throttling.throttleEndtime}"/>
                            </c:if>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                    <c:if test="${throttling.throttleDurationSeconds > 0}">
                        <div class="page-action-area">
                            <cti:button nameKey="remove.login.wait" icon="icon-lock-open" classes="js-remove-login-wait"
                                data-user-id="${user.userId}"/>
                        </div>
                    </c:if>
                </tags:sectionContainer2>
            </c:if>

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
                                <ul class="grouped-list">
                                    <li><span class="group"><cti:formatObject value="${category.key}"/></span>
                                        <ul class="groupedItem">
                                            <c:forEach var="roleGroupPair" items="${category.value}">
                                                <li>
                                                    <cti:formatObject value="${roleGroupPair.role}"/>
                                                    &nbsp;<span class="detail wsnw">(${fn:escapeXml(roleGroupPair.group)})</span>
                                                </li>
                                            </c:forEach>
                                        </ul>
                                    </li>
                                </ul>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </tags:sectionContainer2>
            
        </cti:displayForPageEditModes>
        
    </div>
    
</div>

</cti:standardPage>