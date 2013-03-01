<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/xml" prefix="x"  %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:msgScope paths="modules.amr.billing.HOME">
<cti:msg2 key=".pageName" var="pageName"/>

<cti:standardPage title="${pageName}" module="amr">
	<cti:standardMenu menuSelection="billing|setup"/>
	<cti:breadCrumbs>
		<cti:msg key="yukon.web.components.button.home.label" var="homeLabel"/>
	    <cti:crumbLink url="/operator/Operations.jsp" title="${homeLabel}" />
	    &gt; <cti:msg2 key=".contextualPageName"/>
	</cti:breadCrumbs>

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

function copyButton(){
	$("begin").action = "copy";
	document.begin.submit();
}

function unfreeze(){ //used to enable or disable buttons
	if($('availableFormat').selectedIndex >= 0 ) {
		$("copy").disabled = false; //able to copy any format
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
		$("copy").disabled = true;
		$("delete").disabled = true;
	}
}


</script>

<form method="get" id="begin" name="begin" action="" onclick="unfreeze();">

	<h2><cti:msg2 key=".availableFormats"/></h2>
    <div class="leftChildren">
        <div style="width: 300px;">
            <select style="width:275px;" size="10" id="availableFormat" 
             name="availableFormat" onchange="unfreeze();"  >
        
                <c:forEach var="elem" items="${allRows}">
                    <option value="${elem.formatId}" isSystem="${elem.isSystem}"> 
                        <c:out value="${elem.name}" /> 
                    </option>
                </c:forEach>
        
             </select>
        </div>
        <div class="equalWidthButtons">
            <input type="button" id="create" name="create" value="<cti:msg2 key=".create"/>" onclick="createButton();">
            <input type="button" id="edit" name="edit" value="<cti:msg2 key=".edit"/>" onclick="editButton();" disabled="disabled">
            <input type="button" id="copy" name="copy" value="<cti:msg2 key=".copy"/>" onclick="copyButton();" disabled="disabled">
            <input type="button" id="delete" name="delete" value="<cti:msg2 key=".delete"/>" onclick="deleteButton();" disabled="disabled">
        </div>
    </div>
</form>

</cti:standardPage>
</cti:msgScope>