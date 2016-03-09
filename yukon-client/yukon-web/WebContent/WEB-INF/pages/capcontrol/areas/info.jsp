<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.capcontrol, modules.capcontrol.area">

<cti:url var="url" value="/capcontrol/areas/${area.id}/info"/>
<form:form commandName="area" action="${url}" method="post">
    
    <cti:csrfToken/>
    <form:hidden path="id"/>
    <form:hidden path="type"/>
    
    <tags:nameValueContainer2 tableClass="with-form-controls" naturalWidth="false">
        <%-- NAME --%>
        <tags:nameValue2 nameKey="yukon.common.name">
            <tags:input path="name" size="35" maxlength="60"/>
        </tags:nameValue2>
        <%-- TYPE --%>
        <tags:nameValue2 nameKey="yukon.common.type">
            <i:inline key="${area.type}"/>
        </tags:nameValue2>
        <%-- DESCRIPTION --%>
        <tags:nameValue2 nameKey=".geoName">
            <tags:input path="description" size="35" maxlength="60"/>
        </tags:nameValue2>
        <%-- STATE (ENABLED/DISABLED) --%>
        <tags:nameValue2 nameKey=".state">
            <tags:switchButton path="disabled" inverse="true" 
                offNameKey=".disabled" onNameKey=".enabled" offClasses="M0"/>
        </tags:nameValue2>
        <%-- VOLTAGE REDUCTION --%>
        <tags:nameValue2 nameKey=".voltReduction">
            <c:set var="active" value="${not empty area.voltReductionPoint}"/>
            <tags:switchButton name="vrActive" checked="${active}" offClasses="M0" classes="js-volt-reduct"/>
            <c:set var="initial" value="${not active ? '' : area.voltReductionPoint}"/>
            <tags:pickerDialog type="voltReductionPointPicker" id="voltReduction" allowEmptySelection="true"
                destinationFieldName="voltReductionPoint" initialId="${initial}" 
                buttonStyleClass="js-picker-btn ${not active ? 'dn' : ''}"
                linkType="selection" selectionProperty="pointName"/>
        </tags:nameValue2>
        
    </tags:nameValueContainer2>
    
</form:form>
<cti:url var="url" value="/capcontrol/areas/${area.id}"/>
<form:form method="DELETE" id="delete-area-form" action="${url}" >
    <cti:csrfToken/>
</form:form>

</cti:msgScope>