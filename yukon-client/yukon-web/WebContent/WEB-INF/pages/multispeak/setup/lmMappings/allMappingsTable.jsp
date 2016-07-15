<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="modules.adminSetup.lmMappings">

    <div data-dialog
    id="edit-mapping"
    data-event="yukon.substation.mappings.updateMap"
    data-width="545"
    data-title="<cti:msg2 key=".allMappings.editMap"/>"
    class="dn">
        <tags:nameValueContainer2 tableClass="with-form-controls">
            <tags:nameValue2 nameKey=".strategy">
                <input type="text" class="js-edit-mapping-popup js-strategy-popup">
                <span class="errors dn js-edit-mapping-popup-errors js-strategy-popup"><i:inline key=".error.noStrategy"/></span>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".substation">
                <input type="text" class="js-edit-mapping-popup js-substation-popup" >
                <span class="errors dn js-edit-mapping-popup-errors js-substation-popup"><i:inline key=".error.noSubstation"/></span>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".pao" valueClass="pao-values">
                <cti:msg2  var="notFound-edit" key=".error.notFound"/>
                <span class="mapped-pao-name-popup" data-empty-text="${notFound-edit}"></span>

                <cti:msg2 var="message-popup" key=".changeMapping.message"/>
                <cti:msg2 var="title-popup" key=".changeMapping.title"/>
                <cti:msg2 var="editText-popup" key="components.button.edit.label"/>
                <cti:button nameKey="edit" busy="true" classes="js-edit-btn fn vat"
                    data-message="${message-popup}" data-title="${title-popup}" data-edit-text="${editText-popup}"/>

                <%-- These inputs are used by the picker --%>
                <input type="hidden" id="mappedNameId-popup" >
                <input id="mappedName-popup" class="dn" >
                <tags:pickerDialog id="paoPickerEditPopup"
                    type="lmProgramOrScenarioPicker"
                    destinationFieldId="mappedNameId-popup"
                    extraDestinationFields="paoName:mappedName-popup;"
                    endAction="yukon.substation.mappings.setMappedNameIdOnEdit"
                    linkType="none"/>
            </tags:nameValue2>
        </tags:nameValueContainer2>
    </div>
    <div class="dn js-map-edit-warning error">
        <cti:msg2 key="yukon.web.modules.adminSetup.lmMappings.allMappings.FAILED"/>:
        <cti:msg2 key="yukon.web.modules.adminSetup.lmMappings.allMappings.invalidInput"/>
    </div>
    <table class="compact-results-table dashed with-form-controls">
        <thead>
            <tr>
                <c:forEach var="column" items="${columns}">
                    <tags:sort column="${column}"/>
                </c:forEach>
                <th>&nbsp;</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="mapping" items="${allMappings}">
                <cti:deviceName var="paoName" deviceId="${mapping.paobjectId}"/>
                <tr>
                    <td id='strategy${mapping.mspLMInterfaceMappingId}'>${fn:escapeXml(mapping.strategyName)}</td>
                    <td id='substation${mapping.mspLMInterfaceMappingId}'>${fn:escapeXml(mapping.substationName)}</td>
                    <td id='tpao-name${mapping.mspLMInterfaceMappingId}'>${fn:escapeXml(paoName)}</td>
                    <td>
                        <div class="button-group fr">
                            <cti:button icon="icon-pencil" renderMode="buttonImage" classes="js-edit-mapping"
                                data-mapping-id="${mapping.mspLMInterfaceMappingId}"
                                data-popup="#edit-mapping"/>
                            <cti:button icon="icon-cross" classes="js-remove-mapping" renderMode="buttonImage"
                                data-ok-event="yukon.substation.mappings.delete"
                                data-mapping-id="${mapping.mspLMInterfaceMappingId}" />
                            <cti:list var="arguments">
                                <cti:item value="${fn:escapeXml(mapping.strategyName)}"/>
                                <cti:item value="${fn:escapeXml(mapping.substationName)}"/>
                            </cti:list>
                            <cti:msg2 var="argument" key=".mapKey" arguments="${arguments}"/>
                            <d:confirm on='[data-mapping-id="${mapping.mspLMInterfaceMappingId}"].js-remove-mapping' nameKey="confirmDelete"
                                argument="${argument}"/>
                         </div>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>    
</cti:msgScope>