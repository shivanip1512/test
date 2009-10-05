<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>

<cti:url var="routeUrl" value="/spring/multispeak/setup/routemapping/route" />
<cti:url var="subUrl" value="/spring/multispeak/setup/routemapping/substation" />
<cti:url var="removeAllUrl" value="/spring/multispeak/setup/routemapping/substation">
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
    	<td style="vertical-align:top;">
			<select id="subSelectList" name="selection_list" size="10" style="width:200px" onclick='javascript:SubstationToRouteMappings_SelectListener("${routeUrl}")'>
		        <c:forEach var="substation" items="${list}">
		            <option value="${substation.id}">
		                ${fn:escapeXml(substation.name)}
		            </option>
		        </c:forEach>
		    </select>
    	</td>
    	<td style="vertical-align:bottom;">
    		<input type="submit" value="Delete" size="5" name="remove" />
		    <br><br>
		    <input type="button" value="Delete All" size="5" onclick="removeAllCheck();" />
    	</td>
    </tr>
        
    <tr><td><div style="height:20px;"></div></td></tr>
        
    <tr>
    	<td>
    		Substation Name<br>
    		<input type="text" value="" name="name" size="20" style="width:200px;"/>
    	</td>
    	<td style="vertical-align:bottom;">
    		<input id="addButton" type="submit" value="Add" size="5" name="add" />
    		
    		<c:if test="${hasVendorId}"> 
		    	
		    	<cti:url var="mspAddUrl" value="/spring/multispeak/setup/routemapping/mspSubstations/choose"/>
		    	<input id="mspAddButton" type="button" value="MSP" size="5" onclick="SubstationToRouteMappings_disableInputs(true);openSimpleDialog('mspAddDialog', '${mspAddUrl}', 'Choose Substations');">
		    </c:if>
    
    	</td>
    </tr>
    
    </table>
        
</form>

<tags:simpleDialog id="mspAddDialog" onClose="$('mspAddDialog').hide();SubstationToRouteMappings_disableInputs(false);"/>



