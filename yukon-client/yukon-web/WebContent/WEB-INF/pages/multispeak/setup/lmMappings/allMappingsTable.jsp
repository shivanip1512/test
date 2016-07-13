<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="modules.adminSetup.lmMappings">
	
	<div data-dialog
    id="edit-mapping"
	data-event="yukon.substation.mappings.updateMap"
    data-width="500"    
    data-title="<cti:msg2 key=".allMappings.editMap"/>"
    class="dn">       
        <tags:nameValueContainer2 tableClass="with-form-controls">
            <tags:nameValue2 nameKey=".strategy">
                <input type="text" class="js-mapping-input2 js-strategy2">
                <span class="errors dn js-mapping-errors2 js-strategy2"><i:inline key=".error.noStrategy"/></span>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".substation">
                <input type="text" class="js-mapping-input2 js-substation2" >
                <span class="errors dn js-mapping-errors2 js-substation2"><i:inline key=".error.noSubstation"/></span>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".pao" valueClass="pao-values">
                <cti:msg2  var="notFound2" key=".error.notFound"/>
                <span class="mapped-pao-name2" data-empty-text="${notFound2}"></span>

                <cti:msg2 var="message2" key=".changeMapping.message"/>
                <cti:msg2 var="title2" key=".changeMapping.title"/>
                <cti:msg2 var="editText2" key="components.button.edit.label"/>
                <cti:button nameKey="edit" busy="true" classes="js-add-btnam fn vat" data-ok-event="yukon_substation_mappings_add2"
                    data-message="${message2}" data-title="${title2}" data-edit-text="${editText2}"/>

                <%-- These inputs are used by the picker --%>
                <input type="hidden" id="mappedNameId2" >
                <input id="mappedName2" class="dn" >
                <tags:pickerDialog id="paoPicker2"
                    type="lmProgramOrScenarioPicker"
                    destinationFieldId="mappedNameId2"
                    extraDestinationFields="paoName:mappedName2;"
                    endAction="yukon.substation.mappings.setMappedNameId2"
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