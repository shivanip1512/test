<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="createEnergyCompany">

    <form:form commandName="energyCompanyDto" action="/spring/adminSetup/energyCompany/create">
        <form:hidden path="operatorGroupIds" id="operatorGroupIds"/>
        <form:hidden path="residentialGroupIds" id="residentialGroupIds"/>
        <c:if test="${not empty parentId}">
            <input type="hidden" name="parentId" value="${parentId}">
        </c:if>
    
        <cti:dataGrid cols="2" tableClasses="twoColumnLayout">
        
            <%-- LEFT SIDE COLUMN --%>
            <cti:dataGridCell>
                    
                <tags:formElementContainer nameKey="generalInfo">
                    <tags:nameValueContainer2>
                        <tags:inputNameValue nameKey=".name" path="name" maxlength="60"/>
                        <tags:inputNameValue nameKey=".email" path="email"/>
                        <tags:selectNameValue items="${routes}" itemLabel="paoName" itemValue="yukonID"
                            nameKey=".defaultRoute" path="defaultRouteId" defaultItemLabel="${none}" defaultItemValue="-1"/>
                    </tags:nameValueContainer2>
                </tags:formElementContainer>
                
                <tags:formElementContainer nameKey="groups">
                    
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".primaryOperatorGroup" rowClass="pickerRow">
                            <tags:pickerDialog type="loginGroupPicker" id="primaryOperatorGroupPicker" selectionProperty="groupName"
                                destinationFieldId="primaryOperatorGroupId" linkType="selection" immediateSelectMode="true"/>
                            <tags:hidden path="primaryOperatorGroupId" id="primaryOperatorGroupId"/>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".additionalOperatorGroups" rowClass="pickerRow">
                            <tags:pickerDialog type="loginGroupPicker" id="additionalOperatorGroupPicker" selectionProperty="groupName"
                                destinationFieldId="operatorGroupIds" linkType="selection" multiSelectMode="true" allowEmptySelection="true"/><i:inline key=".optional"/>
                        </tags:nameValue2>

                        <tags:nameValue2 nameKey=".residentialGroups" rowClass="pickerRow">
                            <tags:pickerDialog type="loginGroupPicker" id="residentialGroupPicker" selectionProperty="groupName"
                                destinationFieldId="residentialGroupIds" linkType="selection" multiSelectMode="true" allowEmptySelection="true"/><i:inline key=".optional"/>
                        </tags:nameValue2>
                        
                    </tags:nameValueContainer2>
                    
                </tags:formElementContainer>
                
            </cti:dataGridCell>
            
            <%-- RIGHT SIDE COLUMN --%>
            <cti:dataGridCell>
                
                <tags:formElementContainer nameKey="defaultOperatorLogin">
                    <div><span class="smallBoldLabel"><i:inline key=".note"/></span>&nbsp;<span class="notes"><i:inline key=".defaultOperatorLogin.note"/></span></div>
                    <tags:nameValueContainer2>
                        <tags:inputNameValue nameKey=".username" path="adminUsername"/>
                        <tags:nameValue2 nameKey=".password">
                            <tags:password path="adminPassword1"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".confirmPassword">
                            <tags:password path="adminPassword2"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:formElementContainer>
                
                <tags:formElementContainer nameKey="secondaryOperatorLogin">
                    <div><span class="smallBoldLabel"><i:inline key=".note"/></span>&nbsp;<span class="notes"><i:inline key=".secondaryOperatorLogin.note"/></span></div>
                    <tags:nameValueContainer2>
                        <tags:inputNameValue nameKey=".username" path="admin2Username"/>
                        <tags:nameValue2 nameKey=".password">
                            <tags:password path="admin2Password1"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".confirmPassword">
                            <tags:password path="admin2Password2"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:formElementContainer>
                
            </cti:dataGridCell>
        
        </cti:dataGrid>
        
        <div class="pageActionArea">
            <cti:button key="save" type="submit" name="save"/>
            <cti:button key="cancel" type="submit" name="cancel"/>
        </div>
    
    </form:form>
    
</cti:standardPage>