<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="ext" tagdir="/WEB-INF/tags/ext"%>

<cti:msg var="pageTitle" key="yukon.web.deviceGroups.composedGroup.pageTitle"/>
<cti:msg var="nameLabelText" key="yukon.web.deviceGroups.composedGroup.nameLabel"/>
<cti:msg var="headerRulesText" key="yukon.web.deviceGroups.composedGroup.header.rules"/>
<cti:msg var="headerRemoveText" key="yukon.web.deviceGroups.composedGroup.header.remove"/>
<cti:msg var="saveButtonText" key="yukon.web.deviceGroups.composedGroup.saveButton"/>
<cti:msg var="instructions" key="yukon.web.deviceGroups.composedGroup.instructions"/>
<cti:msg var="instructionsHeader" key="yukon.web.deviceGroups.composedGroup.instructions.header"/>
<cti:msg var="matchSentencePrefix" key="yukon.web.deviceGroups.composedGroup.matchSentence.prefix"/>
<cti:msg var="matchSentenceSuffix" key="yukon.web.deviceGroups.composedGroup.matchSentence.suffix"/>
<cti:msg var="ruleSentenceDeviceGroupPrefix" key="yukon.web.deviceGroups.composedGroup.ruleSentence.deviceGroup.prefix"/>
<cti:msg var="ruleSentenceDeviceGroupSuffix" key="yukon.web.deviceGroups.composedGroup.ruleSentence.deviceGroup.suffix"/>

<cti:url var="add" value="/WebConfig/yukon/Icons/add.gif"/>
<cti:url var="addOver" value="/WebConfig/yukon/Icons/add_over.gif"/>
<cti:url var="delete" value="/WebConfig/yukon/Icons/delete.gif"/>
<cti:url var="deleteOver" value="/WebConfig/yukon/Icons/delete_over.gif"/>

<cti:standardPage title="${pageTitle}" module="amr">
    <cti:msgScope paths="yukon.web.deviceGroups.composedGroup">
    <cti:standardMenu menuSelection="" />
    
    <cti:breadCrumbs>
    
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        <cti:crumbLink url="/spring/meter/start" title="Metering" />
        <cti:url var="groupHomeUrl" value="/spring/group/editor/home">
        	<cti:param name="groupName" value="${groupName}"/>
        </cti:url>
        <cti:crumbLink url="${groupHomeUrl}" title="Device Groups" />
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <script type="text/javascript">

		function setSelectedGroupName(spanElName, valueElName) {

			$(spanElName).innerHTML = $(valueElName).value;
		}
    
	</script>

    <h2>${pageTitle}</h2>
    <br>
    
    <c:if test="${not empty errorMsg}">
		<div class="errorRed">${errorMsg}</div>
		<br>
	</c:if>
    
    <form id="buildForm" action="/spring/group/composedGroup/build" method="post">
    
    <input type="hidden" name="groupName" value="${fn:escapeXml(groupName)}">
    <input type="hidden" name="firstLoad" value="false">
    
    <%-- NAME --%>
	<tags:nameValueContainer>
		<tags:nameValue name="${nameLabelText}" nameColumnWidth="60px">
			${fn:escapeXml(groupName)}
		</tags:nameValue>
	</tags:nameValueContainer>
	<br>
			
				
    <%-- INSTRUCTIONS --%>
    <div style="width:95%;">
    <tags:boxContainer title="${instructionsHeader}" hideEnabled="false">
		${instructions}
    </tags:boxContainer>
    </div>
    <br><br>
    	
    
    <%-- MATCH --%>
    <b>
    ${matchSentencePrefix} 
	<select name="compositionType">
		<c:forEach var="compositionType" items="${availableCompositionTypes}">
			<c:set var="selected" value=""/>
			<c:if test="${compositionType == selectedCompositionType}">
				<c:set var="selected" value="selected"/>
			</c:if>
			<option value="${compositionType}" ${selected}><cti:msg key="${compositionType.formatKey}"/></option>
		</c:forEach>
 	</select>
 	${matchSentenceSuffix}
 	</b>
 	<br><br>
	 
	
	<%-- RULES TABLE --%>
	<table id="groupsTable" class="resultsTable">
	
		<tr>
			<th>${headerRulesText}</th>
			<th style="text-align:center;width:100px;">${headerRemoveText}</th>
		</tr>
		
		<c:forEach var="group" items="${groups}">
		
			<tr>
				<td>
				
					${ruleSentenceDeviceGroupPrefix}
					<select name="notSelect_${group.order}">
						<option value="false">contained in</option>
						<option value="true" <c:if test="${group.negate}">selected</c:if>>not contained in</option>
					</select>
					${ruleSentenceDeviceGroupSuffix}
				
					<tags:deviceGroupNameSelector fieldName="deviceGroupNameField_${group.order}" 
												  fieldValue="${group.groupFullName}" 
												  dataJson="${chooseGroupTreeJson}"/>
					
				</td>
				
				<td style="text-align:center;">
                    <div class="dib">
    					<input type="submit"  class="icon icon_remove" name="removeRow${group.order}">
                    </div>
				</td>
			</tr>
		
		</c:forEach>
		
		<%-- ADD RULE --%>
        <tfoot>
    		<tr>
    			<td colspan="3">
                    <cti:button nameKey="addAnotherDeviceGroup" type="submit" name="addRow"/>
    			</td>
    		</tr>
	   </tfoot>
	</table>
	<br>
	
	<%-- SAVE --%>
	<tags:slowInput myFormId="buildForm" labelBusy="${saveButtonText}" label="${saveButtonText}"/>
	
	</form>
    </cti:msgScope>
</cti:standardPage>