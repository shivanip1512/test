<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<cti:includeCss link="/resources/js/lib/dynatree/skin/ui.dynatree.css" force="true"/>
<cti:includeCss link="/resources/js/lib/dynatree/skin/device.group.css" force="true"/>

<cti:includeScript link="JQUERY_TREE" force="true"/>
<cti:includeScript link="JQUERY_TREE_HELPERS" force="true"/>

<tags:nameValueContainer2>
    <tags:nameValue2 nameKey="${param.parameterKey}">
        <cti:list var="group"><cti:item value="${param.parameterValue}"/></cti:list>
        <tags:deviceGroupPicker inputName="${param.path}.parameters['${param.parameterName}']" inputValue="${group}"/>
    </tags:nameValue2>
</tags:nameValueContainer2>