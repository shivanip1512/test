<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="adminSetup" page="generalInfo.${mode}">

<tags:setFormEditMode mode="${mode}"/>

<cti:displayForPageEditModes modes="VIEW">
<script type="text/javascript">
function addOperatorUserGroups() {
    document.getElementById('operatorUserGroupsForm').submit();
}

function addCustomerUserGroups() {
    document.getElementById('customerUserGroupsForm').submit();
}

$(function() {
    operatorUserGroupPicker.excludeIds = ${cti:jsonString(operatorUserGroupIds)};
    customerUserGroupPicker.excludeIds = ${cti:jsonString(customerUserGroupIds)};
});
</script>
</cti:displayForPageEditModes>
    
<div class="column-12-12 clearfix stacked-md">
    <div class="column one">
        
        <tags:sectionContainer2 nameKey="infoContainer">
            <form:form modelAttribute="generalInfo" method="post" action="update">
                <cti:csrfToken/>
                <form:hidden path="ecId"/>
                
                <tags:nameValueContainer2>
                    <tags:inputNameValue nameKey=".name" path="name" size="35" maxlength="60"/>
                    
                    <cti:displayForPageEditModes modes="EDIT">
                        <tags:inputNameValue nameKey=".streetAddress1" path="address.locationAddress1" size="35" 
                            maxlength="100"/>
                        <tags:inputNameValue nameKey=".streetAddress2" path="address.locationAddress2" size="35" 
                            maxlength="100"/>
                        <tags:inputNameValue nameKey=".city" path="address.cityName" size="32" maxlength="32"/>
                        <tags:inputNameValue nameKey=".stateCode" path="address.stateCode" size="3" maxlength="2"/>
                        <tags:inputNameValue nameKey=".zipCode" path="address.zipCode" size="12" maxlength="12"/>
                        <tags:inputNameValue nameKey=".county" path="address.county" size="30" maxlength="30"/>
                    </cti:displayForPageEditModes>
                    
                    <cti:displayForPageEditModes modes="VIEW">
                        <tags:nameValue2 nameKey=".address">
                            <tags:address address="${generalInfo.address}"/>
                        </tags:nameValue2>
                    </cti:displayForPageEditModes>
                    
                    <tags:inputPhone nameKey=".phone" path="phone"/>
                    <tags:inputPhone nameKey=".fax" path="fax"/>
                    
                    <tags:inputNameValue nameKey=".email" path="email" size="35" maxlength="130" valueClass="wbba"/>
                    <tags:selectNameValue items="${routes}" itemLabel="paoName" itemValue="yukonID" 
                        nameKey=".route" path="defaultRouteId" defaultItemLabel="${none}" defaultItemValue="-1"/>
                    <c:if test="${showParentLogin}">
                        <tags:selectNameValue items="${operatorLogins}" itemLabel="username" itemValue="userID" 
                            nameKey=".parentUser" path="parentLogin" defaultItemLabel="${none}" defaultItemValue=""/>
                    </c:if>
                </tags:nameValueContainer2>
                
                <div class="page-action-area">
                    <cti:displayForPageEditModes modes="VIEW">
                        <c:if test="${canEdit}">
                            <cti:button nameKey="edit" icon="icon-pencil" type="submit" name="edit"/>
                        </c:if>
                    </cti:displayForPageEditModes>
                    <cti:displayForPageEditModes modes="EDIT">
                        <cti:button nameKey="save" type="submit" name="save" classes="primary action"/>
                        <c:if test="${canDelete}">
                            <cti:url value="delete" var="deleteUrl" >
                                <cti:param name="ecId" value="${ecId}"/>
                            </cti:url>
                            <cti:button nameKey="delete" name="deleteConfirmation" href="${deleteUrl}" classes="delete"/>
                        </c:if>
                        <cti:button nameKey="cancel" type="submit" name="cancel"/>
                    </cti:displayForPageEditModes>
                </div>
                
            </form:form>
        </tags:sectionContainer2>
    </div>
    
    <cti:displayForPageEditModes modes="VIEW">
        
        <div class="column two nogutter">
            
            <tags:sectionContainer2 nameKey="membersContainer" styleClass="membersContainer">
            
                <form action="manageMembers" method="post">
                    <cti:csrfToken/>
                    <input name="ecId" type="hidden" value="${ecId}">
                    
                    <c:choose>
                        <c:when test="${empty members}">
                            <div><i:inline key=".noMembers"/></div>
                        </c:when>
                        <c:otherwise>
                            <div class="membersContainer">
                                <table class="with-form-controls full-width">
                                    <thead></thead>
                                    <tfoot></tfoot>
                                    <tbody>
                                        <c:forEach var="company" items="${members}">
                                            <cti:url value="/admin/energyCompany/general/view" var="viewEcUrl">
                                                <cti:param name="ecId" value="${company.energyCompanyId}"/>
                                            </cti:url>
                                            <tr>
                                                <c:choose>
                                                    <c:when test="${canManageMembers}">
                                                        <td><a href="${viewEcUrl}">${fn:escapeXml(company.name)}</a></td>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <td>${fn:escapeXml(company.name)}</td>
                                                    </c:otherwise>
                                                </c:choose>
                                                <c:if test="${canCreateDeleteMembers}">
                                                    <td>
                                                        <cti:button nameKey="remove" type="submit" name="remove"
                                                            value="${company.energyCompanyId}" renderMode="buttonImage" 
                                                            classes="fr MR0" icon="icon-cross"/>
                                                    </td>
                                                </c:if>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:otherwise>
                    </c:choose>
                    
                    <div class="action-area">
                        <c:if test="${canCreateDeleteMembers}">
                            <cti:button nameKey="create" icon="icon-plus-green" type="submit" name="create"/>
                        </c:if>
                        <c:if test="${canManageMembers && !empty memberCandidates}">
                            <cti:button nameKey="add" type="submit" name="add" classes="right" icon="icon-add"/>
                            <select name="newMemberId" class="left" style="max-width: 215px;">
                                <c:forEach items="${memberCandidates}" var="member">
                                    <option value="${member.energyCompanyId}">${fn:escapeXml(member.name)}</option>
                                </c:forEach>
                            </select>
                        </c:if>
                    </div>
                </form>
            </tags:sectionContainer2>
            
        </div>
            
    </cti:displayForPageEditModes>
    
</div>

<cti:displayForPageEditModes modes="VIEW">
    <div class="column-12-12">
        <div class="column one">
            <tags:sectionContainer2 nameKey="operatorGroups">
                <form action="updateOperatorGroups" id="operatorUserGroupsForm" method="post" class="stacked-md">
                    <cti:csrfToken/>
                    <input name="ecId" type="hidden" value="${ecId}">
                    <input type="hidden" name="operatorUserGroupIds" id="operatorUserGroupIds">
                    <div class="scroll-md">
                        <table class="full-width with-form-controls striped">
                            <thead></thead>
                            <tfoot></tfoot>
                            <tbody>
                                <c:forEach var="userGroup" items="${operatorUserGroups}">
                                    <tr>
                                        <td>${fn:escapeXml(userGroup.userGroupName)}</td>
                                        <c:if test="${fn:length(operatorUserGroups) > 1}">
                                            <td>
                                                <cti:button nameKey="remove" type="submit" name="removeOperatorUserGroup" 
                                                    value="${userGroup.userGroupId}" classes="fr MR0"
                                                    renderMode="buttonImage" icon="icon-cross"/>
                                            </td>
                                        </c:if>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    <div class="action-area">
                        <tags:pickerDialog type="userGroupPicker" id="operatorUserGroupPicker" linkType="button" 
                            nameKey="add" destinationFieldId="operatorUserGroupIds" multiSelectMode="true" 
                            endAction="addOperatorUserGroups" icon="icon-add"/>
                    </div>
                </form>
            </tags:sectionContainer2>
        </div>
        <div class="column two nogutter">
            
            <tags:sectionContainer2 nameKey="customerGroups">
                <form action="updateCustomerGroups" id="customerUserGroupsForm" method="post">
                    <cti:csrfToken/>
                    <input name="ecId" type="hidden" value="${ecId}">
                    <input type="hidden" name="customerUserGroupIds" id="customerUserGroupIds">
                    <div class="scroll-md">
                        <table class="full-width with-form-controls striped">
                            <thead></thead>
                            <tfoot></tfoot>
                            <tbody>
                                <c:choose>
                                    <c:when test="${not empty customerUserGroups}">
                                        <c:forEach var="userGroup" items="${customerUserGroups}">
                                            <tr>
                                                <td>${fn:escapeXml(userGroup.userGroupName)}</td>
                                                <td>
                                                    <cti:button nameKey="remove" type="submit" 
                                                        name="removeCustomerUserGroup" value="${userGroup.userGroupId}" 
                                                        renderMode="buttonImage" icon="icon-cross" classes="fr MR0"/>
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
                    <div class="action-area">
                        <tags:pickerDialog type="userGroupPicker" id="customerUserGroupPicker" 
                            linkType="button" nameKey="add" icon="icon-add" destinationFieldId="customerUserGroupIds" 
                            multiSelectMode="true" endAction="addCustomerUserGroups"/>
                    </div>
                </form>
            </tags:sectionContainer2>
        </div>
    </div>
</cti:displayForPageEditModes>

</cti:standardPage>