<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/xml" prefix="x"  %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:standardPage title="Overview of All Saved Format" module="amr">
<cti:standardMenu menuSelection="billing|setup"/>

<script type="text/Javascript" >

function createButton(){
	$("begin").action = "create";
	document.begin.submit();
}

function editButton(){
	$("begin").action = "edit";
	document.begin.submit();
}

function deleteButton(){
	$("begin").action = "delete";
	document.begin.submit();
}

function cloneButton(){
	$("begin").action = "clone";
	document.begin.submit();
}

function unfreeze(){ //used to enable or disable buttons
	if($('availableFormat').selectedIndex >= 0 ) {
		$("clone").disabled = false; //able to clone any format
		if ($($("availableFormat").options[$("availableFormat").selectedIndex]).readAttribute('isSystem') == "true"){
			$("edit").disabled = true; //disable edit and delete if it is system format
			$("delete").disabled = true;
		}
		else{
			$("edit").disabled = false; //enable
			$("delete").disabled = false;
		}
	}
	else{
		$("edit").disabled = true; //disable
		$("clone").disabled = true;
		$("delete").disabled = true;
	}
}


</script>

<form method="get" id="begin" name="begin" action="" onclick="unfreeze();">

	<h2>Available Formats</h2> <br>
	<table width="400" border="0" cellspacing="0" cellpadding="0">
		<tr>
		   <td width="300" align="center">

		   	<select style="width:275px;" size="10" id="availableFormat" 
		     name="availableFormat" onchange="unfreeze();"  >
		
				<c:forEach var="elem" items="${allRows}">
					<option value="${elem.formatId}" isSystem="${elem.isSystem}"> 
						<c:out value="${elem.name}" /> 
					</option>
				</c:forEach>
		
		     </select>
		  </td>
		  
		  <td width="100px" valign="top">
			<input type="button" id="create" name="create" value="Create" onclick="createButton();"  > <br/>
			<input type="button" id="edit" name="edit" value="Edit" onclick="editButton();" disabled="disabled" > <br />
			<input type="button" id="clone" name="clone" value="Clone" onclick="cloneButton();" disabled="disabled"> <br />
			<input type="button" id="delete" name="delete" value="Delete" onclick="deleteButton();" disabled="disabled" > <br />
		  </td>

		</tr>
		
	</table>
	<br />
</form>

</cti:standardPage>