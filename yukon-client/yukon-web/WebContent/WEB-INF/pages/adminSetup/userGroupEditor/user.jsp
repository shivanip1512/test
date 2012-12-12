<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="userEditor.${mode}">

<script type="text/javascript">
var supportsPasswordSet = ${cti:jsonString(supportsPasswordSet)};
var originalAuthCategory = '${user.authCategory}';

YEvent.observeSelectorClick('#cancelChangePassword', function(event) {
    $('changePasswordPopup').hide();
});

jQuery(function() {
    jQuery('#authCategory').change(function() {
        var newAuthCategory = jQuery('#authCategory').val();
        if (newAuthCategory !== originalAuthCategory && supportsPasswordSet[newAuthCategory]) {
            jQuery('#changeAuthPassword, #changeAuthConfirmPassword').show();
        } else {
            jQuery('#changeAuthPassword, #changeAuthConfirmPassword').hide();
        }
    });
    
    <c:if test="${not empty hasPasswordError and hasPasswordError}">
         jQuery('#changeAuthPassword, #changeAuthConfirmPassword').show();
    </c:if>
});
</script>
    
    <i:simplePopup titleKey=".changePasswordPopup" arguments="${user.username}" id="changePasswordPopup" 
            on="#changePasswordButton" styleClass="smallSimplePopup" showImmediately="${showChangePwPopup}">
        <tags:setFormEditMode mode="${pwChangeFormMode}"/>
        <c:if test="${showChangePasswordErrors}">
            <cti:flashScopeMessages/>
        </c:if>
        <form:form commandName="password" action="changePassword" method="post">
            <input type="hidden" value="${userId}" name="userId">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".password">
                    <tags:password path="password"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".confirmPassword">
                    <tags:password path="confirmPassword"/>
                </tags:nameValue2>
            </tags:nameValueContainer2>
            <div class="actionArea">
                <cti:button nameKey="save" type="submit"/>
                <cti:button nameKey="cancel" type="button" id="cancelChangePassword"/>
            </div>
        </form:form>
    </i:simplePopup>
    
    <tags:setFormEditMode mode="${mode}"/>
    <cti:msg2 var="none" key="yukon.web.defaults.none"/>

    <cti:dataGrid cols="2" tableClasses="twoColumnLayout">

        <cti:dataGridCell>

            <form:form commandName="user" action="edit" method="post">
                <form:hidden path="userId"/>

                <tags:nameValueContainer2>
                    
                    <c:choose>
                        <c:when test="${editNameAndStatus}">
                            <tags:inputNameValue nameKey=".username" path="username"/>
                        </c:when>
                        <c:otherwise>
                            <tags:hidden path="username"/>
                            <tags:nameValue2 nameKey=".username">
                                <spring:escapeBody htmlEscape="true">${user.username}</spring:escapeBody>
                            </tags:nameValue2>
                        </c:otherwise>
                    </c:choose>

                    <tags:selectNameValue nameKey=".authentication" items="${authenticationCategories}" path="authCategory"/>

                    <cti:displayForPageEditModes modes="EDIT,CREATE">
                        <tags:nameValue2 nameKey=".password" rowId="changeAuthPassword" rowClass="dn">
                            <tags:password path="password.password"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".confirmPassword" rowId="changeAuthConfirmPassword" rowClass="dn">
                            <tags:password path="password.confirmPassword"/>
                        </tags:nameValue2>
                    </cti:displayForPageEditModes>

                    <c:choose>
                        <c:when test="${editNameAndStatus && currentUserId != user.userId}">
                            <tags:selectNameValue nameKey=".userStatus" items="${loginStatusTypes}" path="loginStatus"/>
                        </c:when>
                        <c:otherwise>
                            <tags:hidden path="loginStatus"/>
                            <tags:nameValue2 nameKey=".userStatus">
                                <spring:escapeBody htmlEscape="true"><i:inline key="${user.loginStatus}"/></spring:escapeBody>
                            </tags:nameValue2>
                        </c:otherwise>
                    </c:choose>
                    
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <cti:msg2 var="none" key="defaults.none"/>
                            <tags:selectNameValue nameKey=".userGroup" items="${userGroups}" itemValue="userGroupId" itemLabel="userGroupName" 
                                path="userGroupId" defaultItemLabel="${none}" defaultItemValue="" />
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            <cti:url value="/spring/adminSetup/userGroup/view" var="userGroupUrl">
                                <cti:param name="userGroupId" value="${user.userGroupId}" />
                            </cti:url>
                            <tags:nameValue2 nameKey=".userGroup">
                                <a href="${userGroupUrl}"><spring:escapeBody htmlEscape="true">${userGroupName}</spring:escapeBody></a>
                            </tags:nameValue2>
                        </cti:displayForPageEditModes>
                    
                </tags:nameValueContainer2>
                
                <div class="pageActionArea">
                    <cti:displayForPageEditModes modes="EDIT,CREATE">
                        <cti:button nameKey="save" name="update" type="submit"/>
                        <%-- TODO implement this later <cti:button nameKey="delete" name="delete" type="submit"/> --%>
                        <cti:url var="cancelUrl" value="view">
                            <cti:param name="userId" value="${user.userId}"/>
                        </cti:url>
                        <cti:button nameKey="cancel" href="${cancelUrl}"/>
                    </cti:displayForPageEditModes>
                    <cti:displayForPageEditModes modes="VIEW">
                        <cti:button nameKey="edit" name="edit" type="submit"/>
                        <c:if test="${supportsPasswordSet[user.authCategory]}">
                            <cti:button nameKey="changePassword" id="changePasswordButton" type="button"/>
                        </c:if>
                    </cti:displayForPageEditModes>
                    <cti:button nameKey="unlockUser" name="unlockUser" id="unlockUser"  type="submit"/>
                </div>
            </form:form>
                
        </cti:dataGridCell>
        
        <cti:dataGridCell>
            
            <cti:displayForPageEditModes modes="VIEW">
                
                <tags:boxContainer2 nameKey="rolesContainer">
                    <c:choose>
                        <c:when test="${empty roles}">
                            <i:inline key=".noRoles"/>
                        </c:when>
                        <c:otherwise>
                            <div class="rolesContainer">
                                <c:forEach var="category" items="${roles}">
                                    <ul class="category">
                                        <li><span class="categoryLabel"><cti:formatObject value="${category.key}"/></span>
                                            <ul class="role">
                                                <c:forEach var="roleGroupPair" items="${category.value}">
                                                    <li>
                                                        <cti:formatObject value="${roleGroupPair.first}"/>
                                                        &nbsp;<span class="subtleGray"><spring:escapeBody htmlEscape="true">(${roleGroupPair.second})</spring:escapeBody></span>
                                                    </li>
                                                </c:forEach>
                                            </ul>
                                        </li>
                                    </ul>
                                </c:forEach>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </tags:boxContainer2>
                
            </cti:displayForPageEditModes>
            
        </cti:dataGridCell>
        
    </cti:dataGrid>
    
</cti:standardPage>