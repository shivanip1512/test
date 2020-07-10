<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:flashScopeMessages/>

<cti:msgScope paths="modules.adminSetup.config.attributes,yukon.common">

    <c:if test="${not empty assignment}">
    
        <cti:url var="saveUrl" value="/admin/config/attributeAssignments/save"/>
        <form:form id="assignment-form" action="${saveUrl}" method="POST" modelAttribute="assignment">
            <cti:csrfToken/>
    
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".attributeName">
                    <tags:selectWithItems path="attribute.id" items="${attributes}" itemValue="id" itemLabel="name"/>
                </tags:nameValue2>
                
                <c:choose>
                    <c:when test="${not empty assignment.attribute.id}">
                        <tags:nameValue2 nameKey=".deviceType">
                            <tags:selectWithItems path="deviceType" items="${deviceTypes}"/>
                        </tags:nameValue2>
                    </c:when>
                    <c:otherwise>
                        <!-- Allow multiple device type selection when adding an assignment -->
                        <tags:nameValue2 nameKey=".deviceTypes">
                            <tags:selectWithItems path="deviceType" items="${deviceTypes}"/>
                        </tags:nameValue2>
                    </c:otherwise>
                </c:choose>
    
                <tags:nameValue2 nameKey=".pointType">
                    <tags:selectWithItems inputClass="js-point-type MR0" path="pointType" items="${pointTypes}"/>
                    <tags:pickerDialog id="displayPointPicker" type="pointPicker" linkType="button" icon="icon-magnifier" 
                        buttonRenderMode="image" buttonStyleClass="fn vam" endEvent="yukon:assignment:pointSelected" immediateSelectMode="true"/>
                </tags:nameValue2>
                
                <tags:nameValue2 nameKey=".pointOffset">
                    <tags:input path="pointOffset" inputClass="js-point-offset" size="6"/>
                </tags:nameValue2>
            
            </tags:nameValueContainer2>
        
        </form:form>
    
    </c:if>


</cti:msgScope>