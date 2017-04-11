<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

 <tags:nameValueContainer2>
    <tags:nameValue2 nameKey="${param.parameterKey}">

        <form:hidden id="${param.widgetId}-${param.parameterName}"
            path="${param.path}.parameters['${param.parameterName}']" />
        <tags:pickerDialog
            id="meterPicker${param.widgetId}"
            type="meterPicker"
            linkType="selectionLabel"                                         
            selectionProperty="paoName"
            destinationFieldId="${param.widgetId}-${param.parameterName}" />
    
    </tags:nameValue2>
</tags:nameValueContainer2>