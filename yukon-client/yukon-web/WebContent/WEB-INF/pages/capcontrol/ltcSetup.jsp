<%@ page import="com.cannontech.web.editor.CapControlForm"%>
<%@ page import="com.cannontech.web.util.JSFParamUtil"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>

<f:verbatim>
    <script type="text/javascript">
        var mappingPointPicker;
    
        function showDynamicPicker(index, pointType) {
            var rows = $$('tr.pointPickerRows');
            var row = rows[index];
            var children = row.childElements();
            var paoName = children[1].getElementsByTagName('span')[0];
            var pointName = children[2].getElementsByTagName('span')[0];
            var point = children[3].getElementsByTagName('input')[0];
            
            var paoNameId = dashId(paoName);
            var pointNameId = dashId(pointName);
            var pointId = dashId(point);
            
            function fixIds() {
                undashId(paoName);
                undashId(pointName);
                undashId(point);
            }            

            mappingPointPicker = new PointPicker(pointId
                    , 'com.cannontech.common.search.criteria.' + pointType + 'PointCriteria'
                    , 'pointName:'+ pointNameId + ';deviceName:'+ paoNameId
                    , 'mappingPointPicker'
                    , ''
                    , fixIds
                    , Prototype.emptyFunction);
            
            mappingPointPicker.showPicker();
        }

        function dashId(element) {
            var elementId = element.id;
            var newElementId = elementId.replace(/:/g, '-');
            element.id = newElementId;
            return element.id;
        }

        function undashId(element) {
            var elementId = element.id;
            var newElementId = elementId.replace(/-/g, ':');
            element.id = newElementId;
        }

        function setNone(index) {
        	var rows = $$('tr.pointPickerRows');
            var row = rows[index];
            var children = row.childElements();
            var paoName = children[1].getElementsByTagName('span')[0];
            var pointName = children[2].getElementsByTagName('span')[0];
            var point = children[3].getElementsByTagName('input')[0];

            paoName.innerHTML = '(none)';
            pointName.innerHTML = '(none)';
            point.value = -1;
        }
    </script>
</f:verbatim>

<f:subview id="ltcView" rendered="#{capControlForm.visibleTabs['LTC']}">
    <x:htmlTag value="fieldset" styleClass="fieldSet">
        <x:htmlTag value="legend"><x:outputText value="LTC Points"/></x:htmlTag>
        
        <x:div styleClass="ltcPointDiv">
        
            <h:dataTable var="mapping" value="#{capControlForm.ltcBase.ltcPointMappings}"
                styleClass="ltcPointTable" 
                rowClasses="pointPickerRows"
                headerClass="ltcColumnHeader ltcPointCell" 
                columnClasses="ltcPointCell">
                
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
                    <x:outputText id="paoName" value="#{mapping.paoName}" />
                </h:column>
                
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="Point Name"/>
                    </f:facet>
                    <x:outputText id="pointName" value="#{mapping.pointName}" />
                </h:column>
                
                <h:column>
                    <f:facet name="header">
                        <h:outputText value=""/>
                    </f:facet>
                    
                    <x:inputHidden id="pointId" value="#{mapping.pointId}"/>
                    <h:outputLink  value="javascript:showDynamicPicker(#{mapping.index}, '#{mapping.pointType.pointTypeString}')" rendered="#{capControlForm.editingAuthorized}">
                        <h:outputText value="Select Point"/>
                    </h:outputLink>
                </h:column>
                
                <h:column>
                    <f:facet name="header">
                        <h:outputText value=""/>
                    </f:facet>
                    
                    <h:outputLink  value="javascript:setNone(#{mapping.index})" rendered="#{capControlForm.editingAuthorized}">
                        <h:outputText value="No Point"/>
                    </h:outputLink>
                </h:column>
                
            </h:dataTable>
            
        </x:div>
        
    </x:htmlTag>            
</f:subview>