<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="generalInfo.${mode}">

    <tags:setFormEditMode mode="${mode}"/>
    
        <cti:dataGrid cols="2" tableClasses="energyCompanyHomeLayout">
        
            <%-- LEFT SIDE COLUMN --%>
            <cti:dataGridCell>
                <form:form commandName="generalInfo" method="post" action="/spring/adminSetup/energyCompany/general/update">
                    <form:hidden path="ecId"/>
                    
                    <tags:formElementContainer nameKey="infoContainer">
                    
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
                                    <tags:address address="${generalInfo.address}" inLine="true" />
                                </tags:nameValue2>
                            </cti:displayForPageEditModes>
                            
                            <cti:displayForPageEditModes modes="VIEW">
                                <tags:nameValue2 nameKey=".phone"><cti:formatPhoneNumber value="${generalInfo.phone}"/></tags:nameValue2>
                            </cti:displayForPageEditModes>
                            
                            <cti:displayForPageEditModes modes="EDIT">
                                <tags:inputNameValue nameKey=".phone" path="phone"/>
                            </cti:displayForPageEditModes>
                            
                            <cti:displayForPageEditModes modes="VIEW">
                                <tags:nameValue2 nameKey=".fax"><cti:formatPhoneNumber value="${generalInfo.fax}"/></tags:nameValue2>
                            </cti:displayForPageEditModes>
                            
                            <cti:displayForPageEditModes modes="EDIT">
                                <tags:inputNameValue nameKey=".fax" path="fax"/>
                            </cti:displayForPageEditModes>

                            <tags:inputNameValue nameKey=".email" path="email" size="35" maxlength="130"/>
                            <tags:selectNameValue items="${routes}" itemLabel="paoName" itemValue="yukonID" 
                                nameKey=".route" path="defaultRouteId" defaultItemLabel="${none}" defaultItemValue="-1"/>
                            
                        </tags:nameValueContainer2>
                    
                    </tags:formElementContainer>
                    
                    <div class="pageActionArea">
                        <cti:displayForPageEditModes modes="VIEW">
                            <c:if test="${canEdit}">
                                <cti:button key="edit" type="submit" name="edit"/>
                            </c:if>
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="EDIT">
                            <cti:button key="save" type="submit" name="save"/>
                            <%-- TODO: Replace old deleting code, then uncomment this
                            <c:if test="${canDelete}">
                                <cti:button key="delete" type="button" id="deleteButton"/>
                                <tags:confirmDialog nameKey=".confirmDelete" on="#deleteButton" arguments="${energyCompanyName}" submitName="delete"/>
                            </c:if> --%>
                            <cti:button key="cancel" type="submit" name="cancel"/>
                        </cti:displayForPageEditModes>
                    </div>
                
                </form:form>
            </cti:dataGridCell>
            
            <cti:displayForPageEditModes modes="VIEW">
                
                <%-- RIGHT SIDE COLUMN --%>
                <cti:dataGridCell>
            
                    <tags:boxContainer2 nameKey="membersContainer">
                        <form action="/spring/adminSetup/energyCompany/general/manageMembers" method="post">
                            <input name="ecId" type="hidden" value="${ecId}">
                            
                            <c:choose>
                                <c:when test="${empty members}">
                                    <div><i:inline key=".noMembers"/></div>
                                </c:when>
                                <c:otherwise>
                                    <div class="membersContainer">
                                        <table class="compactResultsTable">
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
                                                            <input type="image" src="/WebConfig/yukon/Icons/delete.png" class="pointer hoverableImage" name="remove" value="${company.energyCompanyId}">
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
                    
                </cti:dataGridCell>
                    
            </cti:displayForPageEditModes>
        
        </cti:dataGrid>
        
        
</cti:standardPage>