<html>
<%@ page language="java" %>
<%@ page import="com.cannontech.roles.application.WebClientRole"%>
<%@ page import="com.cannontech.database.db.graph.GraphRenderers" %>
<%@ taglib uri="/WEB-INF/cti.tld" prefix="cti" %>

<cti:checklogin/>
<head>
<title>Trending Options Popup</title>
<link id="StyleSheet" rel="stylesheet" href="WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
<META NAME="robots" CONTENT="noindex, nofollow">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<SCRIPT LANGUAGE="JavaScript">
//variable options mapped to com.cannontech.database.db.graph.GraphRenderers options.
var MULT = parseInt(<%=GraphRenderers.GRAPH_MULTIPLIER_MASK%>); 
var MIN_MAX = parseInt(<%=GraphRenderers.PLOT_MIN_MAX_MASK%>);
var LEGEND_MIN_MAX = parseInt(<%=GraphRenderers.LEGEND_MIN_MAX_MASK%>);
var LEGEND_LOAD_FACTOR = parseInt(<%=GraphRenderers.LEGEND_LOAD_FACTOR_MASK%>);

function submitMForm()
{
	window.opener.submitMForm();
	window.close();
}

function initOptions()
{
	var currentMask = parseInt(window.opener.document.MForm.option.value);

	if  ((currentMask & MIN_MAX) != 0) 
		document.otherOptions.min_max.checked = true;

	if ((currentMask & MULT) != 0) 
		document.otherOptions.mult.checked = true;

	if ((currentMask & LEGEND_MIN_MAX) != 0 )
		document.otherOptions.legend_min_max.checked = true;

	if ((currentMask & LEGEND_LOAD_FACTOR) != 0)
		document.otherOptions.legend_load_factor.checked = true;
}

function showMinMax()
{
	window.opener.setMask(LEGEND_MIN_MAX, document.otherOptions.legend_min_max.checked);
}

function showLoadFactor()
{
	window.opener.setMask(LEGEND_LOAD_FACTOR, document.otherOptions.legend_load_factor.checked);
}

function changeMinMax()
{
	window.opener.setMask(MIN_MAX, document.otherOptions.min_max.checked);
}
function changeMult()
{
	window.opener.setMask(MULT, document.otherOptions.mult.checked);
}
</script>

<body onLoad="initOptions()">
<center>
	<table width="300" border="0" align="center" cellpadding="0" cellspacing="0">
    	<tr>
        	<td align="center">
            <form name = "otherOptions">
            <div align = "left">
            <table width="300" border="0" cellspacing="0" cellpadding="3">
            <tr>
				<td>
                <table width="100%" border="0" cellspacing="0" cellpadding="0" align = "center">
               	<tr> 
                   	<td class = "TableCell" width="100%" valign = "top" align="left"> 
					<table width="100%" border="0" cellspacing="0" cellpadding="0" class = "TableCell" height="19">
                    <tr> 
                    	<td width="10%" >
                        	<input id = "min_max" type="checkbox" name="min_max" value="checkbox" onClick = "changeMinMax()">
						</td>
						<td width="90%">Plot Min/Max</td>
					</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td class = "TableCell" width="100%" valign = "top"> 
					<table width="100%" border="0" cellspacing="0" cellpadding="0" class = "TableCell" height="19">
					<tr> 
						<td width="10%" >
							<input type="checkbox" name="mult" value="checkbox" onClick = "changeMult()">
						</td>
						<td width="90%">Use Graph Scaling</td>
					</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td class = "TableCell"  width="11%" valign = "top">
					<table width="100%" border="0" cellspacing="0" cellpadding="0" class = "tableCell">
					<tr> 
						<td width="10%"> 
							<input type="checkbox" name="legend_min_max" value="checkbox" onclick = "showMinMax()" > 
						</td>
						<td width="90%">Show Legend Min/Max</td>
					</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td class = "TableCell"  width="100%" valign = "top">
					<table width="100%" border="0" cellspacing="0" cellpadding="0" class = "tableCell">
					<tr> 
						<td width="10%"> 
							<input type="checkbox" name="legend_load_factor" value="checkbox" onclick = "showLoadFactor()"> 
						</td>
						<td width="90%">Show Legend Load Factor</td>
					</tr>
					</table>
					</td>
				</tr>
				</table>
				</td>
			</tr>
			<tr>
			<td>
				<input type="button" name="Submit" value="Update" align="center" onClick = "submitMForm()">
			</td>
			</tr>
			</table>
			</div>
			</form>
			</td>
		</tr>
	</table 
</center>
</body>