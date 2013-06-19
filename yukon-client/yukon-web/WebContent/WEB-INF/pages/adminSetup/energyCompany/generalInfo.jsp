<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="generalInfo.${mode}">

<script type="text/javascript">
function addOperatorUserGroups() {
    document.getElementById('operatorUserGroupsForm').submit();
}

function addCustomerUserGroups() {
    document.getElementById('customerUserGroupsForm').submit();
}

jQuery(function() {
    operatorUserGroupPicker.excludeIds = ${cti:jsonString(operatorUserGroupIds)};
    customerUserGroupPicker.excludeIds = ${cti:jsonString(customerUserGroupIds)};
});
</script>

    <tags:setFormEditMode mode="${mode}"/>
    
        <div class="column_10_14">
        
            <%-- LEFT SIDE COLUMN --%>
            <div class="column one">
                <form:form commandName="generalInfo" method="post" action="update">
                    <form:hidden path="ecId"/>
                    
                    <tags:nameValueContainer2>
                        <tags:inputNameValue nameKey=".name" path="name" size="35" maxlength="60"/>
                        
                        <cti:displayForPageEditModes modes="EDIT">
                            <tags:inputNameValue nameKey=".streetAddress1" path="address.locationAddress1" size="35" maxlength="40"/>
                            <tags:inputNameValue nameKey=".streetAddress2" path="address.locationAddress2" size="35" maxlength="40"/>
                            <tags:inputNameValue nameKey=".city" path="address.cityName" size="32" maxlength="32"/>
                            <tags:inputNameValue nameKey=".stateCode" path="address.stateCode" size="2" maxlength="2"/>
                            <tags:inputNameValue nameKey=".zipCode" path="address.zipCode" size="12" maxlength="12"/>
                            <tags:inputNameValue nameKey=".county" path="address.county" size="30" maxlength="30"/>
                        </cti:displayForPageEditModes>
                        
                        <cti:displayForPageEditModes modes="VIEW">
                            <tags:nameValue2 nameKey=".address">
                                <tags:address address="${generalInfo.address}" inLine="true"/>
                            </tags:nameValue2>
                        </cti:displayForPageEditModes>
                        
                        <tags:inputPhone nameKey=".phone" path="phone"/>
                        <tags:inputPhone nameKey=".fax" path="fax"/>

                        <tags:inputNameValue nameKey=".email" path="email" size="35" maxlength="130"/>
                        <tags:selectNameValue items="${routes}" itemLabel="paoName" itemValue="yukonID" 
                            nameKey=".route" path="defaultRouteId" defaultItemLabel="${none}" defaultItemValue="-1"/>
                        <c:if test="${showParentLogin}">
                            <tags:selectNameValue items="${operatorLogins}" itemLabel="username" itemValue="userID" 
                                nameKey=".parentUser" path="parentLogin" defaultItemLabel="${none}" defaultItemValue=""/>
                        </c:if>
                    </tags:nameValueContainer2>
                    
                    <div class="pageActionArea">
                        <cti:displayForPageEditModes modes="VIEW">
                            <c:if test="${canEdit}">
                                <cti:button nameKey="edit" icon="icon-pencil" type="submit" name="edit"/>
                            </c:if>
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="EDIT">
                            <cti:button nameKey="save" type="submit" name="save"/>
                            <c:if test="${canDelete}">
                                <cti:url value="delete" var="deleteUrl" >
                                    <cti:param name="ecId" value="${ecId}"/>
                                </cti:url>
                                <cti:button nameKey="delete" name="deleteConfirmation" type="button" href="${deleteUrl}" />
                            </c:if>
                            <cti:button nameKey="cancel" type="submit" name="cancel"/>
                        </cti:displayForPageEditModes>
                    </div>
                
                </form:form>
            </div>
            
            <cti:displayForPageEditModes modes="VIEW">
                
                <%-- RIGHT SIDE COLUMN --%>
                <div class="column two nogutter">
            
                    <tags:boxContainer2 nameKey="membersContainer" styleClass="membersContainer">
                        <form action="manageMembers" method="post">
                            <input name="ecId" type="hidden" value="${ecId}">
                            
                            <c:choose>
                                <c:when test="${empty members}">
                                    <div><i:inline key=".noMembers"/></div>
                                </c:when>
                                <c:otherwise>
                                    <div class="membersContainer">
                                        <table class="compactResultsTable listTable">
                                            <thead>
                                                <tr>
                                                    <th><i:inline key=".companyName"/></th>
                                                    <c:if test="${canManageMembers}">
                                                        <th class="removeColumn"><i:inline key=".remove"/></th>
                                                    </c:if>
                                                </tr>
                                            </thead>
                                            <tfoot></tfoot>
                                            <tbody>
                                                <c:forEach var="company" items="${members}">
                                                    <cti:url value="/adminSetup/energyCompany/general/view" var="viewEcUrl">
                                                        <cti:param name="ecId" value="${company.energyCompanyId}"/>
                                                    </cti:url>
                                                    <tr>
                                                        <td><a href="${viewEcUrl}">${company.name}</a></td>
                                                        <c:if test="${canManageMembers}">
                                                            <td class="removeColumn">
                                                                <div class="dib">
                                                                    <cti:button nameKey="remove" type="submit" name="remove" value="${company.energyCompanyId}" renderMode="image" icon="icon-cross"/>
                                                                </div>
                                                            </td>
                                                        </c:if>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                            
                            <div class="actionArea" style="min-width: 380px;">
                                <c:if test="${canCreateMembers}">
                                    <cti:button nameKey="create" icon="icon-plus-green" type="submit" name="create" classes="fl"/>
                                </c:if>
                                <c:if test="${canManageMembers && !empty memberCandidates}">
                                    <cti:button nameKey="add" type="submit" name="add" classes="fr" icon="icon-add"/>
                                    <select name="newMemberId" class="fr" style="margin: 0 3px 0 0;">
                                        <c:forEach items="${memberCandidates}" var="member">
                                            <option value="${member.energyCompanyId}">${member.name}</option>
                                        </c:forEach>
                                    </select>
                                </c:if>
                            </div>
                        </form>
                    </tags:boxContainer2>
                    
                    <tags:boxContainer2 nameKey="userGroupsContainer">
                        <form action="updateOperatorGroups" id="operatorUserGroupsForm" method="post">
                            <input name="ecId" type="hidden" value="${ecId}">
                            <input type="hidden" name="operatorUserGroupIds" id="operatorUserGroupIds">
                            <div class="operatorGroupTable">
                                <table class="compactResultsTable listTable">
                                    <thead>
                                        <tr>
                                            <th><i:inline key=".operatorGroups"/></th>
                                            <c:if test="${fn:length(operatorUserGroups) > 1}"><th class="removeColumn"><i:inline key=".remove"/></th></c:if>
                                        </tr>
                                    </thead>
                                    <tfoot></tfoot>
                                    <tbody>
                                        <c:forEach var="userGroup" items="${operatorUserGroups}">
                                            <tr>
                                                <td><spring:escapeBody htmlEscape="true">${userGroup.userGroupName}</spring:escapeBody></td>
                                                <c:if test="${fn:length(operatorUserGroups) > 1}">
                                                    <td class="removeColumn">
                                                        <div class="dib">
                                                            <cti:button nameKey="remove" type="submit" name="removeOperatorUserGroup" value="${userGroup.userGroupId}" renderMode="image" icon="icon-cross"/>
                                                        </div>
                                                    </td>
                                                </c:if>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                            <div class="actionArea">
                                <tags:pickerDialog type="userGroupPicker" id="operatorUserGroupPicker" linkType="button" nameKey="add"
                                    destinationFieldId="operatorUserGroupIds" multiSelectMode="true" endAction="addOperatorUserGroups" icon="icon-add"/>
                            </div>
                        </form>
                        
                        <br>
                        
                        <form action="updateCustomerGroups" id="customerUserGroupsForm" method="post">
                            <input name="ecId" type="hidden" value="${ecId}">
                            <input type="hidden" name="customerUserGroupIds" id="customerUserGroupIds">
                            <div class="customerGroupTable">
                                <table class="compactResultsTable listTable">
                                    <thead>
                                        <tr>
                                            <th><i:inline key=".customerGroups"/></th>
                                            <c:if test="${not empty customerUserGroups}"><th class="removeColumn"><i:inline key=".remove"/></th></c:if>
                                        </tr>
                                    </thead>
                                    <tfoot></tfoot>
                                    <tbody>
                                        <c:choose>
                                            <c:when test="${not empty customerUserGroups}">
                                                <c:forEach var="userGroup" items="${customerUserGroups}">
                                                    <tr>
                                                        <td><spring:escapeBody htmlEscape="true">${userGroup.userGroupName}</spring:escapeBody></td>
                                                        <td class="removeColumn">
                                                            <div class="dib">
                                                                <cti:button nameKey="remove" type="submit" name="removeCustomerUserGroup" value="${userGroup.userGroupId}" renderMode="image" icon="icon-cross"/>
                                                            </div>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </c:when>
                                            <c:otherwise>
                                                <tr>
                                                    <td colspan="2"><i:inline key=".noCustomerGroups"/></td>
                                                </tr>
                                            </c:otherwise>
                                        </c:choose>
                                    </tbody>
                                </table>
                            </div>
                            <div class="actionArea">
                                <tags:pickerDialog type="userGroupPicker" id="customerUserGroupPicker" linkType="button" nameKey="add" icon="icon-add"
                                    destinationFieldId="customerUserGroupIds" multiSelectMode="true" endAction="addCustomerUserGroups"/>
                            </div>
                        </form>
                    </tags:boxContainer2>
                    
                </div>
                    
            </cti:displayForPageEditModes>
        
        </div>
        
</cti:standardPage>