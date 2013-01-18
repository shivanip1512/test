<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>

<cti:url var="routeUrl" value="/adminSetup/substations/routeMapping/viewRoute" />
<cti:url var="subUrl" value="/adminSetup/substations/routeMapping/edit" />
<cti:url var="removeAllUrl" value="/adminSetup/substations/routeMapping/removeAll">
	<cti:param name="removeAll" value=""/>
</cti:url>


<script type="text/javascript">
	function removeAllCheck() {

		var ok = confirm('Are you sure you want to delete all substation mappings?');
		if (ok) {
			window.location = '${removeAllUrl}';
		}
	}

</script>

<b>Substation Name</b>
<form name="subform" action="${subUrl}" method="post">
        
    <table>
    
    <tr>
    	<td class="vat">
			<select id="subSelectList" name="selection_list" size="10" style="width:200px" onclick='javascript:SubstationToRouteMappings_SelectListener("${routeUrl}")'>
		        <c:forEach var="substation" items="${list}">
		            <option value="${substation.id}">
		                ${fn:escapeXml(substation.name)}
		            </option>
		        </c:forEach>
		    </select>
    	</td>
    	<td class="vab">
    		<input type="submit" value="Delete" name="remove" class="formSubmit"/>
		    <br><br>
		    <input type="button" value="Delete All" onclick="removeAllCheck();" class="formSubmit"/>
    	</td>
    </tr>
        
    <tr><td><div style="height:20px;"></div></td></tr>
        
    <tr>
    	<td>
    		Substation Name<br>
    		<input type="text" value="" name="name" size="20" style="width:188px;"/>
    	</td>
    	<td class="vab">
    		<input id="addButton" type="submit" value="Add" name="add" class="formSubmit"/>
    		
    		<c:if test="${hasVendorId}"> 
		    	
		    	<cti:url var="mspAddUrl" value="/adminSetup/substations/routemapping/multispeak/choose"/>
		    	<input id="mspAddButton" type="button" value="MSP" onclick="SubstationToRouteMappings_disableInputs(true);openSimpleDialog('mspAddDialog', '${mspAddUrl}', 'Choose Substations');" class="formSubmit">
		    </c:if>
    
    	</td>
    </tr>
    
    </table>
        
</form>

<tags:simpleDialog id="mspAddDialog" onClose="$('mspAddDialog').hide();SubstationToRouteMappings_disableInputs(false);"/>



