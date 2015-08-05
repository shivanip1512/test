<%@ tag body-content="empty" 
        description="Creates a dropdown button that creates a point of the chosen type on the specified pao." %>

<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<%@ attribute name="paoId" required="true" description="The id of the pao that will have the point attached." %>
<%@ attribute name="buttonClasses" description="The id of the pao that will have the point attached." %>

<cti:default var="buttonClasses" value="fr"/>

<cm:dropdown key="components.button.create.label" icon="icon-plus-green" type="button"
    triggerClasses="${buttonClasses}" menuClasses="no-icons">

    <cti:url var="createAnalog" value="/capcontrol/points/Analog/create">
        <cti:param name="parentId" value="${paoId}"></cti:param>
    </cti:url>
    <cm:dropdownOption key="yukon.common.point.pointType.Analog.long" href="${createAnalog}" />

    <cti:url var="createPulseAccumulator" value="/capcontrol/points/PulseAccumulator/create">
        <cti:param name="parentId" value="${paoId}"></cti:param>
    </cti:url>
    <cm:dropdownOption key="yukon.common.point.pointType.PulseAccumulator.long" href="${createPulseAccumulator}" />

    <cti:url var="createCalcAnalog" value="/capcontrol/points/CalcAnalog/create">
        <cti:param name="parentId" value="${paoId}"></cti:param>
    </cti:url>
    <cm:dropdownOption key="yukon.common.point.pointType.CalcAnalog.long" href="${createCalcAnalog}" />

    <cti:url var="createStatus" value="/capcontrol/points/Status/create">
        <cti:param name="parentId" value="${paoId}"></cti:param>
    </cti:url>
    <cm:dropdownOption key="yukon.common.point.pointType.Status.long" href="${createStatus}" />

    <cti:url var="createCalcStatus" value="/capcontrol/points/CalcStatus/create">
        <cti:param name="parentId" value="${paoId}"></cti:param>
    </cti:url>
    <cm:dropdownOption key="yukon.common.point.pointType.CalcStatus.long" href="${createCalcStatus}" />
</cm:dropdown>