<%@ page import="com.cannontech.database.data.point.PointBase" %>

<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x" %>


<f:subview id="fdr" rendered="#{ptEditorForm.visibleTabs['FDR']}" >

	<x:htmlTag value="fieldset" styleClass="fieldSet">
        <x:htmlTag value="legend"><x:outputText value="Data Routing"/></x:htmlTag>
	   	<h:commandButton id="addAction" value="New Routing" styleClass="submenuLink"
	   		action="#{ptEditorForm.pointFDREntry.addTranslation}"
	   		disabled="#{ptEditorForm.pointFDREntry.FDRTransSize >= 5}"
	   		rendered="#{capControlForm.editingAuthorized}"
	   		title="Adds a new FDR entry to this point" />
		<x:outputText id="addWarn" styleClass="alert"
			rendered="#{ptEditorForm.pointFDREntry.FDRTransSize >= 5}" value="  (You have reached the maximum allowed FDR translation entries)"/>
	
	    <x:htmlTag value="br"/>
	    <x:dataTable id="fdrData" var="fdrTranslation" rowIndexVar="rowNum"
	            styleClass="fullTable" headerClass="scrollerTableHeader"
	            footerClass="scrollerTableHeader"
	            rowClasses="tableRow,altTableRow" 
	    		value="#{ptEditorForm.pointBase.pointFDRList}"
				columnClasses="gridCellSmall,gridCellSmall,gridCellLarge,gridCellSmall" >
	
			<h:column>
				<f:facet name="header">
					<x:outputText value="Interface" title="Foreign system this data is interfacing to" />
				</f:facet>
				<x:selectOneMenu id="Interface" onchange="submit();"
                        disabled="#{!capControlForm.editingAuthorized}"
						valueChangeListener="#{ptEditorForm.pointFDREntry.interfaceChange}"
						value="#{fdrTranslation.interfaceType}" >
					<f:selectItems value="#{ptEditorForm.pointFDREntry.FDRInterfaces}"/>
					<f:param name="rowNumber" value="#{rowNum}" />
				</x:selectOneMenu>
			</h:column>
	
			<h:column>
				<f:facet name="header">
					<x:outputText value="Direction" title="Controls whether the data is sent or received from Yukon" />
				</f:facet>
				<x:selectOneMenu id="Direction" value="#{fdrTranslation.directionType}" >
					<f:selectItems value="#{ptEditorForm.pointFDREntry.FDRDirectionsMap[fdrTranslation.interfaceType]}"/>
				</x:selectOneMenu>
			</h:column>
	
			<h:column>
				<f:facet name="header">
					<x:outputText value="Translation" title="The Yukon specific string translation generated from the selected options" />
				</f:facet>
	        	<h:outputText value="#{fdrTranslation.translation}"
	        			rendered="#{ptEditorForm.pointFDREntry.selectedRowNum != rowNum}" />
	        	<h:outputText value="#{fdrTranslation.translation}" styleClass="cellEditing"
	        			rendered="#{ptEditorForm.pointFDREntry.selectedRowNum == rowNum}" />
			</h:column>
	
			<h:column>
				<x:commandLink id="Translation_Edit" styleClass="stdButton"
                            disabled="#{!capControlForm.editingAuthorized}"
							title="Edit this FDR translation entry"
							action="#{ptEditorForm.pointFDREntry.showTranslation}" >
	              	<x:graphicImage value="/WebConfig/yukon/Icons/pencil.png" height="15" width="15" border="0" />
					<f:param name="fdrInterface" value="#{fdrTranslation.interfaceType}" />
					<f:param name="rowNumber" value="#{rowNum}" />
				</x:commandLink>
				<x:commandLink id="Translation_Delete" styleClass="stdButton"
                            disabled="#{!capControlForm.editingAuthorized}"
							title="Delete this FDR translation entry"
							action="#{ptEditorForm.pointFDREntry.deleteTranslation}" >
	              	<x:graphicImage value="/WebConfig/yukon/Icons/delete.png" height="15" width="15" border="0" />
					<f:param name="rowNumber" value="#{rowNum}" />
				</x:commandLink>
			</h:column>	
	
	    </x:dataTable>
	
		<x:htmlTag value="br"/>
		<f:subview id="Translation_Editor" rendered="#{ptEditorForm.pointFDREntry.selectedRowNum >= 0 && not empty ptEditorForm.pointFDREntry.fdrTranslationEntry}" >
			<f:subview id="view_0" rendered="#{not empty ptEditorForm.pointFDREntry.fdrTranslationEntry.optionDataMap['Option_0']}" >
				<x:outputLabel for="Option_0txt"
						rendered="#{empty ptEditorForm.pointFDREntry.fdrTranslationEntry.combo0Items}"
						value="#{ptEditorForm.pointFDREntry.fdrTranslationEntry.label0}: " />
				<x:inputText id="Option_0txt" required="true" maxlength="64"
						value="#{ptEditorForm.pointFDREntry.fdrTranslationEntry.value0}"
						rendered="#{empty ptEditorForm.pointFDREntry.fdrTranslationEntry.combo0Items}" />
				<x:outputLabel for="Option_0comb"
						rendered="#{not empty ptEditorForm.pointFDREntry.fdrTranslationEntry.combo0Items}"
						value="#{ptEditorForm.pointFDREntry.fdrTranslationEntry.label0}: " />
				<x:selectOneMenu id="Option_0comb"
						value="#{ptEditorForm.pointFDREntry.fdrTranslationEntry.value0}"
						rendered="#{not empty ptEditorForm.pointFDREntry.fdrTranslationEntry.combo0Items}" >
					<f:selectItems value="#{ptEditorForm.pointFDREntry.fdrTranslationEntry.combo0Items}" />
				</x:selectOneMenu>
			</f:subview>
	
			<f:subview id="view_1" rendered="#{not empty ptEditorForm.pointFDREntry.fdrTranslationEntry.optionDataMap['Option_1']}" >
				<x:htmlTag value="br"/>
				<x:outputLabel for="Option_1txt"
						rendered="#{empty ptEditorForm.pointFDREntry.fdrTranslationEntry.combo1Items}"
						value="#{ptEditorForm.pointFDREntry.fdrTranslationEntry.label1}: " />
				<x:inputText id="Option_1txt" required="true" maxlength="64"
						value="#{ptEditorForm.pointFDREntry.fdrTranslationEntry.value1}"
						rendered="#{empty ptEditorForm.pointFDREntry.fdrTranslationEntry.combo1Items}" />
				<x:outputLabel for="Option_1comb"
						rendered="#{not empty ptEditorForm.pointFDREntry.fdrTranslationEntry.combo1Items}"
						value="#{ptEditorForm.pointFDREntry.fdrTranslationEntry.label1}: " />
				<x:selectOneMenu id="Option_1comb"
						value="#{ptEditorForm.pointFDREntry.fdrTranslationEntry.value1}"
						rendered="#{not empty ptEditorForm.pointFDREntry.fdrTranslationEntry.combo1Items}" >
					<f:selectItems value="#{ptEditorForm.pointFDREntry.fdrTranslationEntry.combo1Items}" />
				</x:selectOneMenu>
			</f:subview>
	
			<f:subview id="view_2" rendered="#{not empty ptEditorForm.pointFDREntry.fdrTranslationEntry.optionDataMap['Option_2']}" >
				<x:htmlTag value="br"/>
				<x:outputLabel for="Option_2txt"
						rendered="#{empty ptEditorForm.pointFDREntry.fdrTranslationEntry.combo2Items}"
						value="#{ptEditorForm.pointFDREntry.fdrTranslationEntry.label2}: " />
				<x:inputText id="Option_2txt" required="true" maxlength="64"
						value="#{ptEditorForm.pointFDREntry.fdrTranslationEntry.value2}"
						rendered="#{empty ptEditorForm.pointFDREntry.fdrTranslationEntry.combo2Items}" />
				<x:outputLabel for="Option_2comb"
						rendered="#{not empty ptEditorForm.pointFDREntry.fdrTranslationEntry.combo2Items}"
						value="#{ptEditorForm.pointFDREntry.fdrTranslationEntry.label2}: " />
				<x:selectOneMenu id="Option_2comb"
						value="#{ptEditorForm.pointFDREntry.fdrTranslationEntry.value2}"
						rendered="#{not empty ptEditorForm.pointFDREntry.fdrTranslationEntry.combo2Items}" >
					<f:selectItems value="#{ptEditorForm.pointFDREntry.fdrTranslationEntry.combo2Items}" />
				</x:selectOneMenu>
			</f:subview>
	
	
			<f:subview id="view_3" rendered="#{not empty ptEditorForm.pointFDREntry.fdrTranslationEntry.optionDataMap['Option_3']}" >
				<x:htmlTag value="br"/>
				<x:outputLabel for="Option_3txt"
						rendered="#{empty ptEditorForm.pointFDREntry.fdrTranslationEntry.combo3Items}"
						value="#{ptEditorForm.pointFDREntry.fdrTranslationEntry.label3}: " />
				<x:inputText id="Option_3txt" required="true" maxlength="64"
						value="#{ptEditorForm.pointFDREntry.fdrTranslationEntry.value3}"
						rendered="#{empty ptEditorForm.pointFDREntry.fdrTranslationEntry.combo3Items}" />
				<x:outputLabel for="Option_3comb"
						rendered="#{not empty ptEditorForm.pointFDREntry.fdrTranslationEntry.combo3Items}"
						value="#{ptEditorForm.pointFDREntry.fdrTranslationEntry.label3}: " />
				<x:selectOneMenu id="Option_3comb"
						value="#{ptEditorForm.pointFDREntry.fdrTranslationEntry.value3}"
						rendered="#{not empty ptEditorForm.pointFDREntry.fdrTranslationEntry.combo3Items}" >
					<f:selectItems value="#{ptEditorForm.pointFDREntry.fdrTranslationEntry.combo3Items}" />
				</x:selectOneMenu>
			</f:subview>
	
			<f:subview id="view_4" rendered="#{not empty ptEditorForm.pointFDREntry.fdrTranslationEntry.optionDataMap['Option_4']}" >
				<x:htmlTag value="br"/>
				<x:outputLabel for="Option_4txt"
						rendered="#{empty ptEditorForm.pointFDREntry.fdrTranslationEntry.combo0Items}"
						value="#{ptEditorForm.pointFDREntry.fdrTranslationEntry.label4}: " />
				<x:inputText id="Option_4txt" required="true" maxlength="64"
						value="#{ptEditorForm.pointFDREntry.fdrTranslationEntry.value4}"
						rendered="#{empty ptEditorForm.pointFDREntry.fdrTranslationEntry.combo4Items}" />
				<x:outputLabel for="Option_4comb"
						rendered="#{not empty ptEditorForm.pointFDREntry.fdrTranslationEntry.combo4Items}"
						value="#{ptEditorForm.pointFDREntry.fdrTranslationEntry.label4}: " />
				<x:selectOneMenu id="Option_4comb"
						value="#{ptEditorForm.pointFDREntry.fdrTranslationEntry.value4}"
						rendered="#{not empty ptEditorForm.pointFDREntry.fdrTranslationEntry.combo4Items}" >
					<f:selectItems value="#{ptEditorForm.pointFDREntry.fdrTranslationEntry.combo4Items}" />
				</x:selectOneMenu>
			</f:subview>
	
		</f:subview>

	</x:htmlTag>
</f:subview>