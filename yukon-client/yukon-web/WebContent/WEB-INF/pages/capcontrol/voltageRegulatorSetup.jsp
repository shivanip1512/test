<%@ page import="com.cannontech.web.editor.CapControlForm" %>
<%@ page import="com.cannontech.web.util.JSFParamUtil" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<f:subview id="regulatorView" rendered="#{capControlForm.visibleTabs['Regulator']}">
    <x:htmlTag value="fieldSet" styleClass="fieldSet">
        <x:htmlTag value="legend">
            <x:outputText value="Regulator Info" />
        </x:htmlTag>

        <x:panelGrid columns="1" styleClass="gridLayout" columnClasses="gridCell, gridCell">
            <x:panelGroup>
                <x:outputLabel for="paoDescription" value="Description: "
                    title="Description of the Voltage Regulator" />
                <x:inputText id="paoDescription"
                    value="#{capControlForm.PAOBase.PAODescription}" required="false"
                    maxlength="60" styleClass="char32Label" />

                <x:htmlTag value="br"/>
                <x:htmlTag value="br"/>

                <x:outputLabel for="voltChangePerTap" value="Volt Change Per Tap: "
                    title="Voltage Change Per Tap for the Voltage Regulator" />
                <x:inputText id="voltChangePerTap"
                    value="#{capControlForm.regulatorBase.voltChangePerTap}" required="false"
                    maxlength="10" styleClass="char4Label">
                    <f:validateDoubleRange minimum="0" />
                </x:inputText>

                <x:htmlTag value="br" rendered="#{capControlForm.regulatorBase.displayTimer}" />
                <x:htmlTag value="br" rendered="#{capControlForm.regulatorBase.displayTimer}" />

                <x:outputLabel for="keepAliveTimer" value="Keep Alive Timer: "
                    title="Keep Alive Timer (in minutes) for the Voltage Regulator"
                    rendered="#{capControlForm.regulatorBase.displayTimer}" />
                <x:inputText id="keepAliveTimer"
                    value="#{capControlForm.regulatorBase.keepAliveTimer}" required="false"
                    maxlength="10" styleClass="char4Label"
                    rendered="#{capControlForm.regulatorBase.displayTimer}">
                    <f:validateLongRange minimum="0" />
                </x:inputText>

                <x:htmlTag value="br" rendered="#{capControlForm.regulatorBase.displayConfig}" />
                <x:htmlTag value="br" rendered="#{capControlForm.regulatorBase.displayConfig}" />

                <x:outputLabel for="keepAliveConfig" value="Keep Alive Config: "
                    title="Keep Alive Config for the Voltage Regulator"
                    rendered="#{capControlForm.regulatorBase.displayConfig}" />
                <x:inputText id="keepAliveConfig"
                    value="#{capControlForm.regulatorBase.keepAliveConfig}" required="false"
                    maxlength="10" styleClass="char4Label"
                    rendered="#{capControlForm.regulatorBase.displayConfig}">
                    <f:validateLongRange minimum="0" />
                </x:inputText>
            </x:panelGroup>
        </x:panelGrid>
    </x:htmlTag>
    <x:htmlTag value="br" />

    <x:htmlTag value="fieldset" styleClass="fieldSet">
        <x:htmlTag value="legend"><x:outputText value="Regulator Points"/></x:htmlTag>
        <x:div styleClass="regulatorPointDiv">
        <f:verbatim>
            <script type="text/javascript">
                var rowNumber = 0;
                $(function () {
                    // delegated event listeners for Select Point and No Point links
                    $('.regulatorPointTable').on('click', '[id^="pickerLink"]', function (event) {
                        var rowNum = $(event.currentTarget).data('rowNum');
                        window['pointPicker_' + rowNum].show.call(window['pointPicker_' + rowNum]);
                    });
                    $('.regulatorPointTable').on('click', '[id^="clearPoint"]', function (event) {
                        var rowNum = $(event.currentTarget).data('rowNum');
                        // workaround for jQuery, which can't use [, ], : and other characters as selectors
                        $($('[id^="paoName"]')[rowNum]).html('(none)');
                        $($('[id^="pointName"]')[rowNum]).html('(none)');
                        $($('[id^="pointId"]')[rowNum]).val(-1);
                    });
                });
            </script>
        </f:verbatim>
        <x:dataTable rowIndexVar="rowIndex" var="mapping" value="#{capControlForm.regulatorBase.pointMappings}"
            styleClass="regulatorPointTable" 
            rowClasses="pointPickerRows"
            headerClass="regulatorColumnHeader regulatorPointCell" 
            columnClasses="regulatorPointCell"
            >
                
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="Attribute"/>
                    </f:facet>
                    <h:outputText value="#{mapping.regulatorPointMapping.description}" />
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
                            var filterType;
                            window['pointPicker_' + rowNumber] = new Picker('OK', 'Cancel', '(none selected)', 'filterablePointPicker', 
                                    '', 'pointPicker_' + rowNumber, 'pointId:'+ 'pointId['+ rowNumber + ']'+ 
                                    ';pointName:'+ 'pointName[' + rowNumber + ']' + ';deviceName:'+ 'paoName[' + rowNumber + ']');
                            window['pointPicker_' + rowNumber].destinationFieldId = 'pointId[' + rowNumber + ']';
                            <cti:pickerProperties var="outputColumns" property="OUTPUT_COLUMNS" type="filterablePointPicker"/>
                            window['pointPicker_' + rowNumber].outputColumns = ${outputColumns};
                            filterType = document.getElementById('filterType[' + rowNumber + ']').value;
                            window['pointPicker_' + rowNumber].extraArgs = filterType;
                            window['pointPicker_' + rowNumber].immediateSelectMode = true;
                            window['pointPicker_' + rowNumber].init.call(window['pointPicker_' + rowNumber]);
                            // stash rowNumber for delegate event handler
                            $($('[id^="pickerLink"]')[rowNumber]).data('rowNum', rowNumber);
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
                            $($('[id^="clearPoint"]')[rowNumber]).data('rowNum', rowNumber);
                            rowNumber += 1;
                        </script>
                    </f:verbatim>
                    
                </h:column>
                
            </x:dataTable>
            
        </x:div>
        
    </x:htmlTag>
</f:subview>