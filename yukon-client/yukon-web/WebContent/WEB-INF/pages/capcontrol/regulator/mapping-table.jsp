<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.capcontrol.regulator.${mode}">
<tags:setFormEditMode mode="${mode}"/>

<table class="compact-results-table js-mappings-table">
    <thead>
        <tr>
            <th><i:inline key="yukon.common.attribute"/></th>
            <th><i:inline key="yukon.common.device"/></th>
            <th><i:inline key="yukon.common.point"/></th>
            <cti:displayForPageEditModes modes="VIEW">
                <th></th>
                <th><i:inline key="yukon.common.value"/></th>
            </cti:displayForPageEditModes>
        </tr>
    </thead>
    <tfoot></tfoot>
    <tbody>
        <c:forEach var="mapping" items="${regulator.mappings}" varStatus="status">
            
            <tr data-mapping="${mapping.key}">
                
                <%-- Attribute --%>
                <td><i:inline key="${mapping.key}"/></td>
                
                <%-- Device Name --%>
                <td id="paoName${status.index}"></td>
                
                <%-- Point Name --%>
                <td>
                    <tags:pickerDialog type="filterablePointPicker"
                        id="picker${status.index}"
                        extraArgs="${mapping.key.filterType}"
                        extraDestinationFields="deviceName:paoName${status.index};pointId:pointId${status.index};"
                        allowEmptySelection="true"
                        selectionProperty="pointName"
                        initialId="${mapping.value != 0 ? mapping.value : ''}"
                        linkType="selection"
                        buttonStyleClass="fn"
                        viewOnlyMode="${mode == 'VIEW'}"/>
                    <cti:displayForPageEditModes modes="EDIT">
                        <form:errors path="mappings[${mapping.key}]" element="div" cssClass="error"/>
                    </cti:displayForPageEditModes>
                    <input id="pointId${status.index}" type="hidden" name="mappings[${mapping.key}]" 
                        value="${mapping.value}"/>
                </td>
                
                <cti:displayForPageEditModes modes="VIEW">
                    <%-- Point Value --%>
                    <c:choose>
                        <c:when test="${mapping.key == regulatorControlModeMapping && mapping.value != null}">
                            <td class="state-indicator">
                                <cti:pointStatus pointId="${mapping.value}" format="${regulatorControlModeColorFormat}"/>
                            </td>
                            <td>
                                <cti:pointValue pointId="${mapping.value}" format="${regulatorControlModeFormat}"/>
                            </td>
                        </c:when>
                        <c:otherwise>
                            <td class="state-indicator">
                                <cti:pointStatus pointId="${mapping.value}" statusPointOnly="true"/>
                            </td>
                            <td>
                                <c:if test="${not empty mapping.value && mapping.value != 0}">
                                    <cti:pointValue pointId="${mapping.value}" format="VALUE"/>
                                    <cti:pointValue pointId="${mapping.value}" format="UNIT" unavailableValue=""/>
                                    <cti:pointValue pointId="${mapping.value}" format="SHORT_QUALITY" unavailableValue=""/>
                                </c:if>
                            </td>
                        </c:otherwise>
                    </c:choose>

                </cti:displayForPageEditModes>
            </tr>
        </c:forEach>
    </tbody>
</table>

</cti:msgScope>