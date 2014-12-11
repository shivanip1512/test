<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="adminSetup" page="userEditor.${mode}">

<cti:includeScript link="/JavaScript/yukon.admin.user.js"/>

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
                <form:form id="change-password-form" commandName="password" action="change-password" method="post">
                    <cti:csrfToken/>
                    <input type="hidden" name="userId" value="${userId}">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".password">
                            <tags:password path="password" cssClass="js-new-password"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".confirmPassword">
                            <tags:password path="confirmPassword" cssClass="js-confirm-password"/>
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
    <cti:msg2 var="none" key="yukon.web.defaults.none"/>
    
    <div class="column-12-12">
    
        <div class="column one">
            
            <form:form commandName="user" action="edit" method="post">
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
                    
                    <c:choose>
                        <c:when test="${editNameAndStatus && currentUserId != user.userId}">
                            <tags:selectNameValue nameKey=".userStatus" items="${loginStatusTypes}" path="loginStatus"/>
                        </c:when>
                        <c:otherwise>
                            <tags:hidden path="loginStatus"/>
                            <tags:nameValue2 nameKey=".userStatus">
                                <i:inline key="${user.loginStatus}" htmlEscape="true"/>
                            </tags:nameValue2>
                        </c:otherwise>
                    </c:choose>
                    
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <cti:msg2 var="none" key="defaults.none"/>
                            <tags:selectNameValue nameKey=".userGroup" items="${userGroups}" itemValue="userGroupId" 
                                itemLabel="userGroupName" path="userGroupId" 
                                defaultItemLabel="${none}" defaultItemValue=""/>
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            <cti:url value="/adminSetup/userGroup/view" var="userGroupUrl">
                                <cti:param name="userGroupId" value="${user.userGroupId}" />
                            </cti:url>
                            <tags:nameValue2 nameKey=".userGroup">
                                <a href="${userGroupUrl}">${fn:escapeXml(userGroupName)}</a>
                            </tags:nameValue2>
                        </cti:displayForPageEditModes>
                    
                </tags:nameValueContainer2>
                
                <div class="page-action-area">
                    <cti:displayForPageEditModes modes="EDIT,CREATE">
                        <cti:button nameKey="save" name="update" type="submit" classes="primary action"/>
                        <%-- TODO implement this later <cti:button nameKey="delete" name="delete" type="submit"/> --%>
                        <cti:url var="cancelUrl" value="view">
                            <cti:param name="userId" value="${user.userId}"/>
                        </cti:url>
                        <cti:button nameKey="cancel" href="${cancelUrl}"/>
                    </cti:displayForPageEditModes>
                    <cti:displayForPageEditModes modes="VIEW">
                        <cti:button nameKey="edit" icon="icon-pencil" name="edit" type="submit"/>
                        <c:if test="${supportsPasswordSet[user.authCategory]}">
                            <cti:button nameKey="changePassword" data-popup="#change-password-popup" icon="icon-key"/>
                        </c:if>
                        <cti:button nameKey="unlockUser" name="unlockUser" id="unlockUser"  type="submit"/>
                    </cti:displayForPageEditModes>
                </div>
            </form:form>
                
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