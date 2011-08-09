<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="userEditor.${mode}">

<script type="text/javascript">
YEvent.observeSelectorClick('#cancelChangePassword', function(event) {
    $('changePasswordPopup').hide();
});
</script>
    
    <i:simplePopup titleKey=".changePasswordPopup" arguments="${user.username}" id="changePasswordPopup" 
            on="#changePasswordButton" styleClass="smallSimplePopup" showImmediately="${showChangePwPopup}">
        <tags:setFormEditMode mode="${pwChangeFormMode}"/>
        <c:if test="${showChangePasswordErrors}">
            <cti:flashScopeMessages/>
        </c:if>
        <form:form commandName="passwordChange" action="changePassword" method="post">
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
                <cti:button key="save" type="submit"/>
                <cti:button key="cancel" type="button" id="cancelChangePassword"/>
            </div>
        </form:form>
    </i:simplePopup>
    
    <tags:setFormEditMode mode="${mode}"/>
    <cti:msg2 var="none" key="yukon.web.defaults.none"/>

    <cti:dataGrid cols="2" tableClasses="twoColumnLayout">

        <cti:dataGridCell>

            <form:form commandName="user" action="edit" method="post">
                <form:hidden path="userID"/>
                <input type="hidden" value="${userId}" name="userId">
                
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
                        
                    <tags:selectNameValue nameKey=".authentication" items="${authTypes}" path="authType" />
                    
                    <c:choose>
                        <c:when test="${editNameAndStatus}">
                            <tags:selectNameValue nameKey=".loginStatus" items="${loginStatusTypes}" path="loginStatus"/>
                        </c:when>
                        <c:otherwise>
                            <tags:hidden path="loginStatus"/>
                            <tags:nameValue2 nameKey=".loginStatus">
                                <spring:escapeBody htmlEscape="true"><i:inline key="${user.loginStatus}"/></spring:escapeBody>
                            </tags:nameValue2>
                        </c:otherwise>
                    </c:choose>
                </tags:nameValueContainer2>
                
                <div class="pageActionArea">
                    <cti:displayForPageEditModes modes="EDIT,CREATE">
                        <cti:button key="save" name="update" type="submit"/>
                        <%-- TODO implement this later <cti:button key="delete" name="delete" type="submit"/> --%>
                        <cti:button key="cancel" name="cancel" type="submit"/>
                    </cti:displayForPageEditModes>
                    <cti:displayForPageEditModes modes="VIEW">
                        <cti:button key="edit" name="edit" type="submit"/>
                        <c:if test="${showChangePassword}">
                            <cti:button key="changePassword" id="changePasswordButton" type="button"/>
                        </c:if>
                    </cti:displayForPageEditModes>
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
                                        <li><span class="categoryLabel">${category.key}</span>
                                            <ul class="role">
                                                <c:forEach var="roleGroupPair" items="${category.value}">
                                                    <li>
                                                        <cti:url value="/spring/adminSetup/roleEditor/view" var="roleUrl">
                                                            <cti:param name="roleId" value="${roleGroupPair.first.roleId}"/>
                                                            <cti:param name="groupId" value="${roleGroupPair.second.groupID}"/>
                                                        </cti:url>
                                                        <a href="${roleUrl}"><cti:formatObject value="${roleGroupPair.first}"/></a>
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