<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:flashScopeMessages/>

<cti:msgScope paths="modules.adminSetup.config.attributes,yukon.common">

    <c:if test="${not empty assignment}">
    
        <cti:url var="saveUrl" value="/admin/config/attributeAssignments/save"/>
        <form:form id="assignment-form" action="${saveUrl}" method="POST" modelAttribute="assignment">
            <cti:csrfToken/>
            <tags:hidden path="attributeAssignmentId"/>
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".attributeName">
                    <tags:selectWithItems id="attributeId" path="attributeId" items="${attributes}" itemValue="customAttributeId" itemLabel="name"/>
                    <input id="attributeName" type="hidden" name="attributeName"/>
                </tags:nameValue2>
                
                <c:choose>
                    <c:when test="${isEditMode}">
                        <tags:nameValue2 nameKey=".deviceType">
                            <tags:selectWithItems path="paoType" items="${deviceTypes}"/>
                        </tags:nameValue2>
                    </c:when>
                    <c:otherwise>
                        <!-- Allow multiple device type selection when adding an assignment -->
                        <tags:nameValue2 nameKey=".deviceTypes">           
                            <cti:msg2 var="allDeviceTypes" key=".allDeviceTypes"/>
                            <select name="deviceTypes" class="js-device-types" multiple="multiple" data-placeholder="${allDeviceTypes}">
                                <c:forEach var="type" items="${deviceTypes}">
                                    <c:set var="selectedText" value=""/>
                                    <c:forEach var="selectedType" items="${selectedDeviceTypes}">
                                        <c:if test="${selectedType == type}">
                                            <c:set var="selectedText" value="selected=selected"/>
                                        </c:if>
                                    </c:forEach>
                                    <option value="${type}" ${selectedText}><i:inline key="${type.formatKey}"/></option>
                                </c:forEach>
                            </select>
                        </tags:nameValue2>
                    </c:otherwise>
                </c:choose>
    
                <tags:nameValue2 nameKey=".pointType">
                    <tags:selectWithItems inputClass="js-point-type MR0" path="pointType" items="${pointTypes}"/>
                    <tags:pickerDialog id="displayPointPicker" type="pointPicker" linkType="button" icon="icon-magnifier" 
                        buttonRenderMode="image" buttonStyleClass="fn vam" endEvent="yukon:assignment:pointSelected" immediateSelectMode="true"/>
                </tags:nameValue2>
                
                <tags:nameValue2 nameKey=".pointOffset">
                    <tags:input path="offset" inputClass="js-point-offset" size="6"/>
                </tags:nameValue2>
            
            </tags:nameValueContainer2>
        
        </form:form>
    
    </c:if>
    
    <cti:includeScript link="/resources/js/pages/yukon.adminSetup.attributes.js" />

</cti:msgScope>