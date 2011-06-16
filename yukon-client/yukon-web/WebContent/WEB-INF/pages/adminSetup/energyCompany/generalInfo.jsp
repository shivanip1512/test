<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="generalInfo.${mode}">

<script type="text/javascript">
function addOperatorGroups() {
    $('operatorGroupsForm').submit();
}

function addCustomerGroups() {
    $('customerGroupsForm').submit();
}

Event.observe(window, "load", function() {
    operatorGroupPicker.excludeIds = ${cti:jsonString(operatorGroupIds)};
    customerGroupPicker.excludeIds = ${cti:jsonString(customerGroupIds)};
});
</script>

    <tags:setFormEditMode mode="${mode}"/>
    
        <cti:dataGrid cols="2" tableClasses="twoColumnLayout">
        
            <%-- LEFT SIDE COLUMN --%>
            <cti:dataGridCell>
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
                                nameKey=".parentLogin" path="parentLogin" defaultItemLabel="${none}" defaultItemValue=""/>
                        </c:if>
                    </tags:nameValueContainer2>
                    
                    <div class="pageActionArea">
                        <cti:displayForPageEditModes modes="VIEW">
                            <c:if test="${canEdit}">
                                <cti:button key="edit" type="submit" name="edit"/>
                            </c:if>
                            <c:if test="${canEditRoles}">
                                <cti:url var="editRolesUrl" value="/spring/adminSetup/groupEditor/view">
                                    <cti:param name="groupId" value="${groupId}"/>
                                </cti:url>
                                <cti:button key="editRoles" type="button" href="${editRolesUrl}"/>
                            </c:if>
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="EDIT">
                            <cti:button key="save" type="submit" name="save"/>
                            <c:if test="${canDelete}">
                                <cti:url value="delete" var="deleteUrl" >
                                    <cti:param name="ecId" value="${ecId}"/>
                                </cti:url>
                                <cti:button key="delete" type="button" href="${deleteUrl}" />
                            </c:if>
                            <cti:button key="cancel" type="submit" name="cancel"/>
                        </cti:displayForPageEditModes>
                    </div>
                
                </form:form>
            </cti:dataGridCell>
            
            <cti:displayForPageEditModes modes="VIEW">
                
                <%-- RIGHT SIDE COLUMN --%>
                <cti:dataGridCell>
            
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
                                            <tr>
                                                <th><i:inline key=".companyName"/></th>
                                                <c:if test="${canManageMembers}">
                                                    <th class="removeColumn"><i:inline key=".remove"/></th>
                                                </c:if>
                                            </tr>
                                            
                                            <c:forEach var="company" items="${members}">
                                                <cti:url value="/spring/adminSetup/energyCompany/general/view" var="viewEcUrl">
                                                    <cti:param name="ecId" value="${company.energyCompanyId}"/>
                                                </cti:url>
                                                <tr>
                                                    <td><a href="${viewEcUrl}">${company.name}</a></td>
                                                    <c:if test="${canManageMembers}">
                                                        <td class="removeColumn">
                                                            <div class="dib">
                                                                <cti:button key="remove" type="submit" name="remove" value="${company.energyCompanyId}" renderMode="image"/>
                                                            </div>
                                                        </td>
                                                    </c:if>
                                                </tr>
                                            </c:forEach>
                                        </table>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                            
                            <div>
                                <c:if test="${canCreateMembers}">
                                    <span style="float: left;margin-top: 15px;"><cti:button key="create" type="submit" name="create"/></span>
                                </c:if>
                                <c:if test="${canManageMembers && !empty memberCandidates}">
                                    <span class="actionArea" style="float: right;">
                                        <select name="newMemberId">
                                            <c:forEach items="${memberCandidates}" var="member">
                                                <option value="${member.energyCompanyId}">${member.name}</option>
                                            </c:forEach>
                                        </select>
                                        <cti:button key="add" type="submit" name="add"/>
                                    </span>
                                </c:if>
                            </div>
                        </form>
                    </tags:boxContainer2>
                    
                    <br>
                    
                    <tags:boxContainer2 nameKey="loginGroupsContainer">
                        <form action="updateOperatorGroups" id="operatorGroupsForm" method="post">
                            <input name="ecId" type="hidden" value="${ecId}">
                            <input type="hidden" name="operatorGroupIds" id="operatorGroupIds">
                            <div class="operatorGroupTable">
                                <table class="compactResultsTable listTable">
                                    <tr>
                                        <th><i:inline key=".operatorGroups"/></th>
                                        <c:if test="${fn:length(operatorGroups) > 1}"><th class="removeColumn"><i:inline key=".remove"/></th></c:if>
                                    </tr>
                                    <c:forEach var="group" items="${operatorGroups}">
                                        <tr>
                                            <td><spring:escapeBody htmlEscape="true">${group.groupName}</spring:escapeBody></td>
                                            <c:if test="${fn:length(operatorGroups) > 1}">
                                                <td class="removeColumn">
                                                    <div class="dib">
                                                        <cti:button key="remove" type="submit" name="removeOperatorGroup" value="${group.groupID}" renderMode="image"/>
                                                    </div>
                                                </td>
                                            </c:if>
                                        </tr>
                                    </c:forEach>
                                </table>
                            </div>
                            <div class="actionArea">
                                <tags:pickerDialog type="loginGroupPicker" id="operatorGroupPicker" linkType="button" nameKey="add"
                                    destinationFieldId="operatorGroupIds" multiSelectMode="true" endAction="addOperatorGroups"/>
                            </div>
                        </form>
                        
                        <br>
                        
                        <form action="updateCustomerGroups" id="customerGroupsForm" method="post">
                            <input name="ecId" type="hidden" value="${ecId}">
                            <input type="hidden" name="customerGroupIds" id="customerGroupIds">
                            <div class="customerGroupTable">
                                <table class="compactResultsTable listTable">
                                    <tr>
                                        <th><i:inline key=".customerGroups"/></th>
                                        <c:if test="${not empty customerGroups}"><th class="removeColumn"><i:inline key=".remove"/></th></c:if>
                                    </tr>
                                    <c:choose>
                                        <c:when test="${not empty customerGroups}">
                                            <c:forEach var="group" items="${customerGroups}">
                                                <tr>
                                                    <td><spring:escapeBody htmlEscape="true">${group.groupName}</spring:escapeBody></td>
                                                    <td class="removeColumn">
                                                        <div class="dib">
                                                            <cti:button key="remove" type="submit" name="removeCustomerGroup" value="${group.groupID}" renderMode="image"/>
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
                                </table>
                            </div>
                            <div class="actionArea">
                                <tags:pickerDialog type="loginGroupPicker" id="customerGroupPicker" linkType="button" nameKey="add"
                                    destinationFieldId="customerGroupIds" multiSelectMode="true" endAction="addCustomerGroups"/>
                            </div>
                        </form>
                    </tags:boxContainer2>
                    
                </cti:dataGridCell>
                    
            </cti:displayForPageEditModes>
        
        </cti:dataGrid>
        
</cti:standardPage>