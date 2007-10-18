<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>


<cti:titledContainer>
	<jsp:attribute name="title">
	
		<table width="100%" class="titleTable">
			<tr>
				<td>
					Assigned LM Objects
				</td>
				<td align="right">
					<span class="pickerLink">
						<cti:paoPicker pickerId="${pickerId}" paoIdField="newPaoId" constraint="com.cannontech.common.search.criteria.LMDeviceCriteria" finalTriggerAction="addPao">Add</cti:paoPicker>
						<input id="newPaoId" type="hidden" />
					</span>
				</td>
       		</tr>
       	</table>
	        
	</jsp:attribute>
	<jsp:body>

		<table width="100%" border="0" cellspacing="0" cellpadding="3">
			<tr class="<tags:alternateRow odd="" even="altRow"/>">
				<th>Name</th>
				<th>Type</th>
				<th>&nbsp;</th>
			</tr>
			<c:forEach var="pao" items="${paoList}">
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td>
						<c:out value="${pao.paoName}" />
					</td>
					<td>
						<c:out value="${pao.type}" />
					</td>
					<td align="center">
						<img title="Remove" class="cssicon" src="/WebConfig/yukon/Icons/clearbits/close.gif" onclick="javascript:removePao(${pao.paoId})">
					</td>
				</tr>
				
			</c:forEach>
		</table>
	
	</jsp:body>
</cti:titledContainer>
<br/>
<cti:titledContainer>
	<jsp:attribute name="title">
	
		<table width="100%" class="titleTable">
			<tr>
				<td>
					Restricted CBC Objects
				</td>
				<td align="right">
					<span class="pickerLink">
						<cti:paoPicker pickerId="${cbcPickerId}" paoIdField="newCbcPaoId" constraint="com.cannontech.common.search.criteria.CBCDeviceCriteria" finalTriggerAction="addPao">Add</cti:paoPicker>
						<input id="newCbcPaoId" type="hidden" />
					</span>
				</td>
       		</tr>
       	</table>
	        
	</jsp:attribute>
	<jsp:body>

		<table width="100%" border="0" cellspacing="0" cellpadding="3">
			<tr class="<tags:alternateRow odd="" even="altRow"/>">
				<th>Name</th>
				<th>Type</th>
				<th>&nbsp;</th>
			</tr>
			<c:forEach var="pao" items="${cbcPaoList}">
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td>
						<c:out value="${pao.paoName}" />
					</td>
					<td>
						<c:out value="${pao.type}" />
					</td>
					<td align="center">
						<img title="Remove" class="cssicon" src="/WebConfig/yukon/Icons/clearbits/close.gif" onclick="javascript:removePao(${pao.paoId})">
					</td>
				</tr>
				
			</c:forEach>
		</table>
	
	</jsp:body>
</cti:titledContainer>
<br/>
<input type="button" value="Save" onclick="javascript:save()" />
<input id="paoIdList" type="hidden" value="${paoIds}"></input>
<input id="cbcPaoIdList" type="hidden" value="${cbcPaoIds}"></input>
