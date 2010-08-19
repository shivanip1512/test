<%@ page import="com.cannontech.web.editor.CapControlForm"%>
<%@ page import="com.cannontech.web.util.JSFParamUtil"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:includeScript link="/JavaScript/picker.js"/>
<cti:includeScript link="/JavaScript/simpleDialog.js"/>
<cti:includeScript link="/JavaScript/tableCreation.js"/>

<f:subview id="ltcView" rendered="#{capControlForm.visibleTabs['LTC']}">
    <x:htmlTag value="fieldset" styleClass="fieldSet">
        <x:htmlTag value="legend"><x:outputText value="LTC Points"/></x:htmlTag>
        <x:div styleClass="ltcPointDiv">
		<f:verbatim>
			<script type="text/javascript">
				var picker = [];
				var rowNumber = 0;
			</script>
		</f:verbatim>
        <x:dataTable rowIndexVar="rowIndex" var="mapping" value="#{capControlForm.ltcBase.ltcPointMappings}"
            styleClass="ltcPointTable" 
            rowClasses="pointPickerRows"
            headerClass="ltcColumnHeader ltcPointCell" 
            columnClasses="ltcPointCell"
            >
                
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="Attribute"/>
                    </f:facet>
                    <h:outputText value="#{mapping.attribute.description}" />
                </h:column>
                
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="Device Name"/>
                    </f:facet>
                    <x:outputText id="paoName" forceId="true" value="#{mapping.paoName}" />
                </h:column>
                
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="Point Name"/>
                    </f:facet>
                    <x:outputText id="pointName" forceId="true" value="#{mapping.pointName}" />
                </h:column>
                
                <h:column>
                    <f:facet name="header">
                        <h:outputText value=""/>
                    </f:facet>
			
                    <x:inputHidden id="pointId" forceId="true" value="#{mapping.pointId}"/>
                    <x:inputHidden id="filterType" forceId="true" value="#{mapping.filterName}"/>
                    <x:commandLink id="pickerLink" forceId="true" onclick="javascript:return;" value="Select Point" rendered="#{capControlForm.editingAuthorized}"/>

                    <f:verbatim>
                        <script type="text/javascript">
			                picker[rowNumber] = new Picker('filterablePointPicker', '', 'picker[' + rowNumber + ']', 'pointName:'+ 'pointName[' + rowNumber + ']' + ';deviceName:'+ 'paoName[' + rowNumber + ']');
			                picker[rowNumber].destinationFieldId = 'pointId[' + rowNumber + ']';
							var filterType = document.getElementById('filterType[' + rowNumber + ']').value;
			                picker[rowNumber].extraArgs = filterType;

			                function createPickerShower(rowNumberIn) {
				                return function() {
	                            	picker[rowNumberIn].show();
				                }
			                }
			                Event.observe('pickerLink[' + rowNumber + ']','click', createPickerShower(rowNumber));


    					</script>
                    </f:verbatim>
                </h:column>
                
                <h:column>
                    <f:facet name="header">
                        <h:outputText value=""/>
                    </f:facet>
                    
                    <x:commandLink id="clearPoint" forceId="true" onclick="javascript:return;" value="No Point" rendered="#{capControlForm.editingAuthorized}"/>
                    <f:verbatim>
                        <script type="text/javascript">
			                function createClearPoint(rowNumberIn) {
				                return function() {
				                    var paoName = document.getElementById('paoName[' + rowNumberIn + ']');
				                    var pointName = document.getElementById('pointName[' + rowNumberIn + ']');
				                    var point = document.getElementById('pointId[' + rowNumberIn + ']');

				                    paoName.innerHTML = '(none)';
				                    pointName.innerHTML = '(none)';
				                    point.value = -1;
				                }
			                }     
                        	Event.observe('clearPoint[' + rowNumber + ']','click', createClearPoint(rowNumber));
                           	rowNumber++;
    					</script>
                    </f:verbatim>
                                            	
                </h:column>
                
            </x:dataTable>
            
        </x:div>
        
    </x:htmlTag>            
</f:subview>