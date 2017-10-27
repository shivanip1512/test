<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<cti:uniqueIdentifier var="id" />

 <tags:nameValueContainer2>
    <tags:nameValue2 nameKey="${param.parameterKey}">

        <form:hidden id="param_${id}"
            path="${param.path}.parameters['${param.parameterName}']" />

        <cti:globalSetting var="maxNumPorts" globalSettingType="PORTER_QUEUE_COUNTS_TREND_MAX_NUM_PORTS"/>
        <tags:pickerDialog
            id="portPicker${id}"
            type="portPicker"
            linkType="selection"
            multiSelectMode="true"
            selectionProperty="paoName"
            destinationFieldId="param_${id}"
            allowEmptySelection="false"
            maxNumSelections="${maxNumPorts}"/>
            
    </tags:nameValue2>
</tags:nameValueContainer2>