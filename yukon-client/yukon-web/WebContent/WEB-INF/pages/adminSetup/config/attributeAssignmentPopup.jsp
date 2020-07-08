<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:flashScopeMessages/>

<cti:msgScope paths="modules.adminSetup.config.attributes,yukon.common">

    <cti:url var="saveUrl" value="/admin/config/attributeAssignments/save"/>
    <form:form id="assignment-form" action="${saveUrl}" method="POST" modelAttribute="assignment">
        <cti:csrfToken/>

        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".attributeName">
                <tags:selectWithItems path="attribute.id" items="${attributes}" itemValue="id" itemLabel="name"/>
            </tags:nameValue2>
            
            <tags:nameValue2 nameKey=".deviceType">
                <tags:selectWithItems path="deviceType" items="${deviceTypes}"/>
            </tags:nameValue2>
            
            <tags:nameValue2 nameKey=".pointType">
                <tags:selectWithItems path="pointType" items="${pointTypes}"/>
                <tags:pickerDialog id="displayPointPicker" type="pointPicker" linkType="button" icon="icon-magnifier" 
                    buttonRenderMode="image" buttonStyleClass="fn vam"/>
            </tags:nameValue2>
            
            <tags:nameValue2 nameKey=".pointOffset">
                <tags:input path="pointOffset"/>
            </tags:nameValue2>
        
        </tags:nameValueContainer2>
    
    </form:form>


</cti:msgScope>