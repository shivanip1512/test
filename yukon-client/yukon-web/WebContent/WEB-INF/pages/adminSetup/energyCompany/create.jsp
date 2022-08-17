<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="createEnergyCompany">

    <cti:url var="createUrl" value="/admin/energyCompany/create"/>
    <form:form modelAttribute="energyCompanyDto" action="${createUrl}">
        <cti:csrfToken/>
        <form:hidden path="operatorUserGroupIds" id="operatorUserGroupIds"/>
        <form:hidden path="residentialUserGroupIds" id="residentialUserGroupIds"/>
        <c:if test="${not empty parentId}">
            <input type="hidden" name="parentId" value="${parentId}">
        </c:if>
    
        <div class="column-12-12 clearfix">
        
            <div class="column one">
                    
                <tags:sectionContainer2 nameKey="generalInfo">
                    <tags:nameValueContainer2>
                        <tags:inputNameValue nameKey=".name" path="name" size="35" maxlength="60"/>
                        <tags:inputNameValue nameKey=".email" path="email" size="35" maxlength="130"/>
                        <tags:selectNameValue items="${routes}" itemLabel="paoName" itemValue="yukonID"
                            nameKey=".defaultRoute" path="defaultRouteId" defaultItemLabel="${none}" defaultItemValue="-1"/>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
                
            </div>
            
            <div class="column two nogutter">
                
                <tags:sectionContainer2 nameKey="defaultOperatorUser">
                    <div><span class="strong-label-small"><i:inline key=".note"/></span>&nbsp;<span class="notes"><i:inline key=".defaultOperatorUser.note"/></span></div>
                    <tags:nameValueContainer2>
                        <tags:inputNameValue nameKey=".username" path="adminUsername"/>
                        <tags:nameValue2 nameKey=".password">
                            <tags:password path="adminPassword1" autocomplete="off"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".confirmPassword">
                            <tags:password path="adminPassword2" autocomplete="off"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
                
            </div>
        </div>
        
        <div>
                        
            <tags:sectionContainer2 nameKey="groups">
                
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".primaryOperatorGroup" rowClass="pickerRow">
                        <tags:pickerDialog type="userGroupPicker" id="primaryOperatorUserGroupPicker" selectionProperty="userGroupName"
                            destinationFieldId="primaryOperatorUserGroupId" linkType="selection" immediateSelectMode="true"/>
                        <tags:hidden path="primaryOperatorUserGroupId" id="primaryOperatorUserGroupId"/>
                    </tags:nameValue2>
                    
                    <tags:nameValue2 nameKey=".additionalOperatorGroups" rowClass="pickerRow">
                        <tags:pickerDialog type="userGroupPicker" id="additionalOperatorUserGroupPicker" selectionProperty="userGroupName"
                            destinationFieldId="operatorUserGroupIds" linkType="selection" multiSelectMode="true" allowEmptySelection="true"/><i:inline key=".optional"/>
                    </tags:nameValue2>

                    <tags:nameValue2 nameKey=".residentialGroups" rowClass="pickerRow">
                        <tags:pickerDialog type="userGroupPicker" id="residentialUserGroupPicker" selectionProperty="userGroupName"
                            destinationFieldId="residentialUserGroupIds" linkType="selection" multiSelectMode="true" allowEmptySelection="true"/><i:inline key=".optional"/>
                    </tags:nameValue2>
                    
                </tags:nameValueContainer2>
                
            </tags:sectionContainer2>
        </div>
        
        <div class="page-action-area">
            <cti:button nameKey="save" type="submit" name="save" classes="primary action" busy="true"/>
            <cti:button nameKey="cancel" type="submit" name="cancel"/>
        </div>
    
    </form:form>
    
</cti:standardPage>