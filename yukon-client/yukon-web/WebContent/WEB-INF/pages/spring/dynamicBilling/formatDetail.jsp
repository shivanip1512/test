<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jstl/xml" %>

<cti:standardPage title="Create Bill Format -Format creation and edit" module="amr">

<cti:includeScript link="/JavaScript/yukonGeneral.js"/>
<cti:includeScript link="/JavaScript/dynamicBillingFileGenerator.js"/>
<cti:includeScript link="/JavaScript/scriptaculous/effects.js"/>

<cti:standardMenu/>

<h2>${title}</h2> <br> 
<br />
	<form id="begin" name="begin" action="" method="post" autocomplete="off">
	<label id="nameWordsDiv" style="color:black;" >Name of format: </label><input type="text" name="formatType" style="width:300px" id="formatType" value="<c:out value="${format.formatType}"/>" >
	<input type="button" value = "Save" onclick = "saveButton();" /> 
	<input type="button" value = "Cancel" onclick = "cancelButton();" />
	<input type="button" value = "Delete" onclick = "deleteButton();" />
	<br /> 
	<font size="1">Remember to save the changes before leaving or refreshing the page</font>
	<br />
	<div id="errorMsg" style="color:red;">&nbsp;</div>
	
	<strong>Field Format Settings </strong><br/> <br/>
		Delimiter: 
		
		<c:if test="${format.delim == ',' || format.delim == ';' || format.delim ==':'}">
			<select  name="delimiterChoice" id="delimiterChoice" onchange="unfreezeDelim();">
			<c:choose>
				<c:when test="${format.delim == ','}">
					<option value="," selected="selected"> Comma </option>
					<option value=";"> Semicolon </option>
					<option value=":"> Colon </option>
					<option value="Custom"> Custom </option>
				</c:when>
				<c:when test="${format.delim == ';'}">
					<option value=","> Comma </option>
					<option value=";" selected="selected"> Semicolon </option>
					<option value=":"> Colon </option>
					<option value="Custom"> Custom </option>
				</c:when>
				<c:when test="${format.delim == ':'}">
					<option value=","> Comma </option>
					<option value=";"> Semicolon </option>
					<option value=":" selected="selected"> Colon </option>
					<option value="Custom"> Custom </option>
				</c:when>
			</c:choose>
			</select> or create your own: 
			<input type="text" style="width:50px;" name="delimiterMade" id="delimiterMade" value= "${format.delim}"
			disabled="disabled" onkeyup="updatePreviewDiv($('preview'));"/>
		</c:if>
		
		<c:if test="${format.delim != ',' && format.delim != ';' && format.delim !=':'}">
			<select name="delimiterChoice" id="delimiterChoice" onchange="unfreezeDelim();">
				<option value=","> Comma </option>
				<option value=";"> Semicolon </option>
				<option value=":"> Colon </option>
				<option value="Custom" selected="selected"> Custom </option>
			</select> or create your own: 
			<input type="text" style="width:50px;" name="delimiterMade" id="delimiterMade" maxlength="20" value="<c:out value="${format.delim}"/>" onkeyup="updatePreviewDiv($('preview'));" /> 
		</c:if>
		
		<br/>
		<br />
		<input type="hidden" id="delimiter" name="delimiter" value="" />
		
		Header: <input style="width:500px" type="text" name="header" id="headerField" maxlength="255" 
			value="<c:out value="${format.header}" />" onchange="updatePreviewDiv($('preview'));" /> <br/> <br />
		Footer: <input style="width:500px" type="text" name="footer" id="footerField" maxlength="255"
			value="<c:out value="${format.footer}" />" onchange="updatePreviewDiv($('preview'));" /> <br/> <br/>
			
		<input type="hidden" id="availableFormat" name="availableFormat" value="${initiallySelected}" >
		<input type="hidden" id="fieldArray" name="fieldArray" value="">
		<table width="870" height="223" border="0" cellpadding="0" cellspacing="0">
			<tr>
	    		<td width="260"><div align="center"><strong>Available Fields </strong><br />
	      			<select id="Available" name="available" style="width:260px; height:200px;" 
	      			multiple="multiple" onchange="unfreeze();" >
						
						<c:forEach var="field" items="${availableFields}">
							<option> ${field} </option>
						</c:forEach>
						
	      			</select> 
	      			</div>
	    		</td>
	    		<td width="75px" align="center" valign="middle">
		   			<input type="button" id="right" onclick="moveLeftRight($('Available'),$('Selected') );unfreeze();updatePreviewDiv($('preview'));" value="&rarr;" disabled="disabled"/><br />
		    		<input type="button" id="left" onclick="moveLeftRight($('Selected'), $('Available'));unfreeze();updatePreviewDiv($('preview'));" value="&larr;" disabled="disabled"/>
	    		<td width="260"><div id="selectedWordsDiv" style="color:black" align="center"><strong>Selected Fields </strong>
	        		<select id="Selected" name="selected" style="width:260px; height:200px;"
	        		 multiple="multiple" onchange="unfreeze();" >
	        		 
	        		 	<c:set var="i" value="0"/>
						<c:forEach var="field" items="${selectedFields}">
							<option format="<c:out value="${field.format}" />" > ${field.name} </option>
							<c:set var="i" value="${i+1}"/>
						</c:forEach>
						
					</select> </div>
	    		</td>
	    		<td width="75px" align="center" valign="middle">
	    			<input type="button" id="up" onclick="yukonGeneral_moveOptionPositionInSelect(Selected, -1);unfreeze();updatePreviewDiv($('preview'));" value="&uarr;" disabled="disabled"/><br/>
	    			<input type="button" id="down" onclick="yukonGeneral_moveOptionPositionInSelect(Selected, 1);unfreeze();updatePreviewDiv($('preview'));" value="&darr;" disabled="disabled"/>
	    		</td>
	    		<td align="center" valign="top" width="200px">
	    			<div id="valueDiv" style="display:none"> 
	    				<div id="valueWords"> </div>
	    				<select id="valueReccomended" onchange="defaultFormatInitiater($('valueReccomended'), $('valueFormat'));">
	    					<option selected="selected">No Format</option>
	    					<option>Custom</option>
	    					<option>###.###</option>
	    					<option>####.##</option>
	    				</select> <br /> 
	    				reading pattern: <br />
	    				<input type="text" id="valueFormat" maxlength="30" value="" onkeyup="fieldFormatSaver($('valueFormat'));updatePreviewDiv($('preview'));" /> <br/>
	    				<a href="javascript:displayHelper($('valueHelper'));">Help with pattern</a>  <br />
	    			</div> 
	    			<div id="timestampDiv" style="display:none"> 
	    				<div id="timestampWords"> </div>
	    				<select id="timestampReccomended" onchange="defaultFormatInitiater($(timestampReccomended), $('timestampFormat'));">
	    					<option selected="selected">No Format</option>
	    					<option>Custom</option>
	    					<option>dd/MM/yyyy</option>
	    					<option>MM/dd/yyyy</option>
	    					<option>hh:mm:ss a</option>
	    					<option>HH:mm:ss</option>
	    				</select> <br /> 
	    				timestamp pattern: <br/>
	    				<input type="text" id="timestampFormat" maxlength="30" value="" onkeyup="fieldFormatSaver($('timestampFormat'));updatePreviewDiv($('preview'));"/> <br/> 
	    				<a href="javascript:displayHelper($('timestampHelper'));" >Help with pattern</a> <br/>
	    			</div>
	    			<div id="plainTextDiv" style="display:none">
	    				Plain Text Input:<br />
	    				<input type="text" id="plainTextFormat" maxlength="30" value="" onkeyup="fieldFormatSaver($('plainTextFormat'));updatePreviewDiv($('preview'));">
	    			</div>
	    		</td>
	  		</tr>
		</table>
		
		<div id="timestampHelper" class="popUpDiv" style="display:none">
			<!--  fix for IE6 bug (see itemPicker.css for more info) -->
			<!--[if lte IE 6.5]><iframe></iframe><![endif]-->
			<div style="border: 3px solid #888; padding: 5px 5px; font-size: 10px;" >
				<div align="right"> <a href="javascript:displayHelper($('timestampHelper'));" >close</a> </div>
				<br/>
				<div style="font-size: 12px;">				
					The timestamp pattern input box will accept a textual pattern that represents a date and time format.  The format will be applied
					to the selected field when the billing file is generated. Be careful as it is case sensitive and enter only the characters listed 
					in the examples below. <br />If no pattern is specified, the default pattern is: 'MM/dd/yyyy hh:mm:ss zZ'
				</div>
				<h3>Example Formats</h3>
				<table  border="1" cellpadding="5px" cellspacing="0px">
					<tr>
						<th>Input</th>
						<th>Result</th>
						<th>Description</th>
					</tr>
					<tr>
						<td>yy;yyyy</td>
						<td>07;2007</td>
						<td>year, the digits showed</td>
					</tr>
					<tr>
						<td>M;MM;MMM;MMM</td>
						<td>7;07;Jul;July</td>
						<td>Month in number and word</td>
					</tr>
					<tr>
						<td>dd</td>
						<td>15</td>
						<td>calendar day</td>
					</tr>
					<tr>
						<td>EEE;EEEE</td>
						<td>Wed;Wednesday</td>
						<td>Day name in the week</td>
					</tr>
					<tr>
						<td>HH</td>
						<td>14</td>
						<td>Hours in a day(0-24)</td>
					</tr>
					<tr>
						<td>h</td>
						<td>12</td>
						<td>Hour in AM/PM</td>
					</tr>
					<tr>
						<td>a</td>
						<td>AM</td>
						<td>AM/PM marker</td>
					</tr>
					<tr>
						<td>mm</td>
						<td>45</td>
						<td>Minute in an hour</td>
					</tr>
					<tr>
						<td>ss</td>
						<td>55</td>
						<td>Second in a minute</td>
					</tr>
					<tr>
						<td>SSS</td>
						<td>181</td>
						<td>Millisecond</td>
					</tr>
					<tr>
						<td>z; zzzz</td>
						<td>CDT; Central Daylight Time</td>
						<td>Timezone in general</td>
					</tr>
					<tr>
						<td>Z</td>
						<td>-0800</td>
						<td>hour difference timezone</td>
					</tr>
				</table>
			</div>
		</div>
		<div id="valueHelper" class="popUpDiv" style="display:none;">
			<!--  fix for IE6 bug (see itemPicker.css for more info) -->
			<!--[if lte IE 6.5]><iframe></iframe><![endif]-->
			<div align="left" style="border: 3px solid #888; padding: 5px 5px;" >
				<div align="right" > <a href="javascript:displayHelper($('valueHelper'));"  style="font-size: 10px" >close</a></div>
				<br />
				<div style="font-size: 12px;">
					The reading pattern input box will accept a textual pattern that represents a number format.  The format will be applied
					to the selected field when the billing file is generated. Acceptable symbols for the pattern are: ,(comma) .(decimal) 
					-(minus sign) $(currency symbols). Digits are represented by a 0(zero) or a #(pound sign). A zero will display a number if
					one exists for that position in the reading or a '0' filler will be displayed. A pound sign will display a number if one 
					exists for that position in the reading or nothing will be displayed in that position.<br/>
					If no pattern is specified, the default pattern is: '#####.00' 
					<h3>Example Formats</h3>
					<table  border="1" cellpadding="5px" cellspacing="0px">
						<tr>
							<th>Input</th>
							<th>Result</th>
							<th>Description</th>
						</tr>
						<tr>
							<td>####,###.##</td>
							<td>123,456 (for a reading of 123456)</td>
							<td>Display 7 digits and 2 decimals (if digits exist at each position)</td>
						</tr>
						<tr>
							<td>0000,000.00</td>
							<td>0123,456.00 (for a reading of 123456)</td>
							<td>Display 7 digits and 2 decimals (filling in '0's for missing digits)</td>
						</tr>
						<tr>
							<td>####,000.0#</td>
							<td>123,456.0 (for a reading of 123456)</td>
							<td>Display 7 digits and 2 decimals (filling in '0's for missing digits)</td>
						</tr>
					</table>
				</div>
			</div>
		</div>
		<hr>
		<br />Preview Here:<br />
		<br />
		<div id="preview"><br/> <br/><br/></div>
		<br />
		<script type="text/javascript"> 
			updatePreviewDiv($('preview'));
		</script>
		
	</form>

</cti:standardPage>