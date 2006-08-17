

<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<f:verbatim>
    <script type="text/javascript">
        addSmartScrolling('VARscrollOffsetTop', 'VARscrollable_div', null, null);
        addSmartScrolling('WATTscrollOffsetTop', 'WATTscrollable_div', null, null);
        addSmartScrolling('VOLTscrollOffsetTop', 'VOLTscrollable_div', null, null);
    </script>
</f:verbatim>

<f:subview id="subSetup" rendered="#{capControlForm.visibleTabs['CBCSubstation']}">

	<f:subview id="paoSubBus" rendered="#{capControlForm.visibleTabs['CBCSubstation']}">
		<h:panelGrid id="subBody" columns="2" styleClass="gridLayout" columnClasses="gridColumn">

			<h:column>
				<f:verbatim>
					<br />
					<br />
					<fieldset>
						<legend>
							SubBus Info
						</legend>
				</f:verbatim>
				<x:outputLabel for="subAreaName" value="#{capControlForm.PAODescLabel}: " title="Physical location of the Substation Bus" rendered="#{!empty capControlForm.PAODescLabel}" />
				<x:inputText id="subAreaName" value="#{capControlForm.PAOBase.geoAreaName}" required="true" maxlength="60" styleClass="char32Label" rendered="#{!empty capControlForm.PAODescLabel}" />

				<f:verbatim>
					<br />
				</f:verbatim>
				<x:outputLabel for="subMapLocID" value="Map Location ID: " title="Mapping code/string used for third-party systems" />
				<x:inputText id="subMapLocID" value="#{capControlForm.PAOBase.capControlSubstationBus.mapLocationID}" required="true" maxlength="64" styleClass="char32Label" />

				<f:verbatim>
					</fieldset>
				</f:verbatim>


				<f:verbatim>
					<br />
					<br />
					<fieldset>
						<legend>
							VAR Point Setup
						</legend>
				</f:verbatim>
				<x:outputLabel for="subVarPoint" value="Selected Point: " title="Data point used for the current kVAR value" styleClass="medStaticLabel" />
				<x:outputText id="subVarPoint" rendered="#{capControlForm.PAOBase.capControlSubstationBus.currentVarLoadPointID != 0}"
					value="#{capControlForm.paoNameMap[capControlForm.PAOBase.capControlSubstationBus.currentVarLoadPointID]} / #{capControlForm.pointNameMap[capControlForm.PAOBase.capControlSubstationBus.currentVarLoadPointID]}"
					styleClass="medLabel" />
				<x:outputText id="subVarPoint_none" rendered="#{capControlForm.PAOBase.capControlSubstationBus.currentVarLoadPointID == 0}" value="(none)" styleClass="medLabel" />



				<x:div forceId="true" id="VARscrollable_div" styleClass="scrollSmall">

				
                <x:inputText id="var_point" forceId="true" value="0"/>               
                
                <f:verbatim>
                    <br/>
                </f:verbatim>
                <h:outputLink value="javascript:pointPicker_showPicker('var_point','com.cannontech.common.search.criteria.CCVarCriteria')" >
                    <h:outputText value="Select point..."/>
                </h:outputLink>

			</x:div>


				<x:commandLink id="subVarPoint_setNone" title="Do not use a point for the VAR value" value="No VAR Point" actionListener="#{capControlForm.varPtTeeClick}" styleClass="medStaticLabel">
					<f:param name="ptID" value="0" />
				</x:commandLink>
				<f:verbatim>
					</fieldset>
				</f:verbatim>
			</h:column>



			<h:column>
				<f:verbatim>
					<br />
					<br />
					<fieldset>
						<legend>
							Watt Point Setup
						</legend>
				</f:verbatim>
				<x:outputLabel for="subWattPoint" value="Selected Point: " title="Data point used for the current WATT value" styleClass="medStaticLabel" />
				<x:outputText id="subWattPoint" rendered="#{capControlForm.PAOBase.capControlSubstationBus.currentWattLoadPointID != 0}" styleClass="medStaticLabel"
					value="#{capControlForm.paoNameMap[capControlForm.PAOBase.capControlSubstationBus.currentWattLoadPointID ]}/ #{capControlForm.pointNameMap[capControlForm.PAOBase.capControlSubstationBus.currentWattLoadPointID ]}"
					/>
				<x:outputText id="subWattPoint_none" rendered="#{capControlForm.PAOBase.capControlSubstationBus.currentWattLoadPointID == 0}" value="(none)" styleClass="medLabel" />

				<x:div forceId="true" id="WATTscrollable_div" styleClass="scrollSmall">
                <x:inputText id="watt_point" forceId="true" value="0"/>               
                
                <f:verbatim>
                    <br/>
                </f:verbatim>
				<h:outputLink value="javascript:pointPicker_showPicker('watt_point','com.cannontech.common.search.criteria.CCWattCriteria')" >
                    <h:outputText value="Select point..."/>
                </h:outputLink>
                </x:div>
               
                <x:inputHidden forceId="true" id="VARscrollOffsetTop" value="#{capControlForm.offsetMap['VARscrollOffsetTop']}" />
				<x:inputHidden forceId="true" id="WATTscrollOffsetTop" value="#{capControlForm.offsetMap['WATTscrollOffsetTop']}" />
				<x:inputHidden forceId="true" id="VOLTscrollOffsetTop" value="#{capControlForm.offsetMap['VOLTscrollOffsetTop']}" />

				
				
				<h:inputHidden id="varTreeExp" value="0" />
				<x:commandLink id="subWattPoint_setNone" title="Do not use a point for the Watt value" styleClass="medStaticLabel" value="No Watt Point" actionListener="#{capControlForm.wattPtTeeClick}">
					<f:param name="ptID" value="0" />
				</x:commandLink>

				<f:verbatim>
					</fieldset>
				</f:verbatim>


				<f:verbatim>
					<br />
					<br />
					<fieldset>
						<legend>
							Volt Point Setup
						</legend>
				</f:verbatim>
				<x:outputLabel for="subVoltPoint" value="Selected Point: " title="Data point used for the current Volt value" styleClass="medStaticLabel" />
				<x:outputText id="subVoltPoint" rendered="#{capControlForm.PAOBase.capControlSubstationBus.currentVoltLoadPointID != 0}"
					value="#{capControlForm.paoNameMap[capControlForm.PAOBase.capControlSubstationBus.currentVoltLoadPointID]}/ #{capControlForm.pointNameMap[capControlForm.PAOBase.capControlSubstationBus.currentVoltLoadPointID]}"
					styleClass="medLabel" />
				<x:outputText id="subVoltPoint_none" rendered="#{capControlForm.PAOBase.capControlSubstationBus.currentVoltLoadPointID == 0}" value="(none)" styleClass="medLabel" />

				<x:div forceId="true" id="VOLTscrollable_div" styleClass="scrollSmall">

                <x:inputText id="volt_point" forceId="true" value="0"/>               
                
                <f:verbatim>
                    <br/>
                </f:verbatim>
                <h:outputLink value="javascript:pointPicker_showPicker('volt_point','com.cannontech.common.search.criteria.CCVoltCriteria')" >
                    <h:outputText value="Select point..."/>
                </h:outputLink>

				</x:div>
				<x:commandLink id="subVoltPoint_setNone" title="Do not use a point for the Volt value" styleClass="medStaticLabel" value="No Volt Point" actionListener="#{capControlForm.voltPtTeeClick}">
					<f:param name="ptID" value="0" />
				</x:commandLink>

				<f:verbatim>
					</fieldset>
				</f:verbatim>

			</h:column>

		</h:panelGrid>

	</f:subview>

</f:subview>




