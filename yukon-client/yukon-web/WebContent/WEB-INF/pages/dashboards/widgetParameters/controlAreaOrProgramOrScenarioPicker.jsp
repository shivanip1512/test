<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<cti:uniqueIdentifier var="id" />

 <tags:nameValueContainer2>
    <tags:nameValue2 nameKey="${param.parameterKey}">

        <form:hidden id="param_${id}"
            path="${param.path}.parameters['${param.parameterName}']" />
        <tags:pickerDialog
            id="controlAreaOrProgramOrScenarioPicker_${id}"
            type="controlAreaOrProgramOrScenarioPicker"
            linkType="selection"
            multiSelectMode="false"
            selectionProperty="paoName"
            destinationFieldId="param_${id}"
            allowEmptySelection="false"/>
            
    </tags:nameValue2>
</tags:nameValueContainer2>