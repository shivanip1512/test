<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jstl/xml" %>

<cti:standardPage title="Create Billing Format - Format creation and edit" module="amr">
<cti:standardMenu menuSelection="billing|setup"/>
<cti:breadCrumbs>
    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
    <cti:crumbLink url="/spring/dynamicBilling/overview" title="Billing Setup" />
    &gt; ${title}
</cti:breadCrumbs>

<cti:includeScript link="/JavaScript/yukonGeneral.js"/>
<cti:includeScript link="/JavaScript/dynamicBillingFileGenerator.js"/>
<cti:includeScript link="/JavaScript/scriptaculous/effects.js"/>

<h2>${title}</h2> <br> 
<br />
	<form id="begin" name="begin" action="" method="post" autocomplete="off">
		Name of format:
		<input type="text" name="formatName" style="width:300px" id="formatName" value="<c:out value="${format.name}"/>" >
		<input type="button" value="Save" onclick="saveButton();" /> 
		<input type="button" value="Cancel" onclick="cancelButton();" />
		<br /> 
		<font size="1">* The Save button must be clicked or changes will be lost when page is refreshed.</font>
		<br />
		<div id="errorMsg" style="color:red;">&nbsp;</div>
	
		<h3>Field Format Settings</h3>
		Delimiter: 
		<select  name="delimiterChoice" id="delimiterChoice" onchange="updateDelimiter();">
			<option value="," <c:if test="${format.delim == ','}">selected="selected" </c:if>>Comma</option>
			<option value=";" <c:if test="${format.delim == ';'}">selected="selected" </c:if>> Semicolon </option>
			<option value=":" <c:if test="${format.delim == ':'}">selected="selected" </c:if>> Colon </option>
			<option value="Custom" <c:if test="${format.delim != ',' && format.delim != ';' && format.delim !=':'}">selected="selected" </c:if>> Custom </option>
		</select>
		<input 
			type="text" 
			id="delimiter" 
			name="delimiter"
            size="1" 
            maxLength="1"
			style="width:50px;" 
			value="<c:out value="${format.delim}"/>" 
			<c:if test="${format.delim == ',' || format.delim == ';' || format.delim ==':'}">readOnly="readonly" </c:if>
			onkeyup="updatePreview();" /> 
		
		<br/><br/>
		
		Header:
		<input style="width:500px" type="text" name="header" id="headerField" maxlength="255" value="<c:out value="${format.header}" />" onKeyUp="updatePreview();" />
		
		<br/><br/>
		
		Footer:
		<input style="width:500px" type="text" name="footer" id="footerField" maxlength="255" value="<c:out value="${format.footer}" />" onKeyUp="updatePreview();" />
		
		<br/><br/>
			
		<input type="hidden" id="formatId" name="formatId" value="${initiallySelected}" >
		<input type="hidden" id="fieldArray" name="fieldArray" value="">
		<table width="1200" height="223" border="0" cellpadding="0" cellspacing="0">
			<tr>
	    		<td width="260" align="center">
    				<h4 style="display: inline;">Available Fields</h4>
	      			<select id="availableFields" name="availableFields" style="width:260px; height:200px;" multiple="multiple" onchange="availableFieldsChanged();" >
						<c:forEach var="field" items="${availableFields}">
							<option>${field}</option>
						</c:forEach>
	      			</select> 
	    		</td>
	    		<td width="75px" align="center" valign="middle">
		   			<input type="button" id="rightArrowButton" onclick="addToSelected();" value="&rarr;" disabled="disabled"/><br />
		    		<input type="button" id="leftArrowButton" onclick="removeFromSelected();" value="&larr;" disabled="disabled"/>
		    	</td>
	    		<td width="260" align="center">
    				<h4 style="display: inline;">Selected Fields</h4>
	        		<select id="selectedFields" name="selectedFields" style="width:260px; height:200px;" multiple="multiple" onchange="selectedFieldsChanged()" >
						<c:forEach var="field" items="${selectedFields}">
							<option format="<c:out value="${field.format}" />" maxLength="<c:out value="${field.maxLength}" />" padChar="<c:out value="${field.padChar}" />" padSide="<c:out value="${field.padSide}" />" readingType="<c:out value="${field.readingType}" />" >${field.name}</option>
						</c:forEach>
					</select>
	    		</td>
	    		<td width="75px" align="center" valign="middle">
	    			<input type="button" id="upArrowButton" onclick="yukonGeneral_moveOptionPositionInSelect(selectedFields, -1);selectedFieldsChanged();" value="&uarr;" disabled="disabled"/><br/>
	    			<input type="button" id="downArrowButton" onclick="yukonGeneral_moveOptionPositionInSelect(selectedFields, 1);selectedFieldsChanged();" value="&darr;" disabled="disabled"/>
	    		</td>
	    		
	    		<td id="fieldFormats" valign="middle" width="600px" >
                    <div id="valueFormatDiv" style="display:none"> 
	    				<div id="valueWords"> </div>
                        <table><tr>
                                <td>Reading Type:</td>
                                <td>
                                    <select id="readingReadingType" onchange="updateFormat('reading', 'readingType');">
                                        <c:forEach var="readingTypeValue" items="${readingTypes}">
                                            <option value="${readingTypeValue}">${readingTypeValue}</option>
                                        </c:forEach>
                                    </select>
                                </td>
                            </tr><tr>
                                <td>Reading Pattern:</td>
                                <td>
	    				           <select id="readingFormatSelect" onchange="updateFormat('reading', 'formatWithSelect');">
	    					          <option value="No Format" selected="selected">No Format</option>
	    					          <option value="Custom">Custom</option>
	    					          <option value="###.###">###.###</option>
	    					          <option value="####.##">####.##</option>
	    				           </select>  
	    				           <input type="text" id="readingFormat" maxlength="30" value="" onkeyup="updateFormat('reading', 'formatWithSelectText');" />
                                </td>
                            </tr><tr>
                                <td>Field Size:</td>
                                <td>
                                    <input type="text" id="readingMaxLength" size="5" maxlength="5" value="" onkeyup="updateFormat('reading', 'maxLength');" /> (0 for no max)
                                </td>
	    				    </tr><tr>
                                <td>Padding:</td>
                                <td>
                                    <select id="readingPadSide" onchange="updateFormat('reading', 'padSide');">
                                        <option value="none">None</option>
                                        <option value="left">Left</option>
                                        <option value="right">Right</option> 
                                    </select>
                                    Character
                                    <select id="readingPadCharSelect" onchange="updateFormat('reading', 'padCharSelect');">
                                        <option value="Space" selected="selected">Space</option>
                                        <option value="Zero">Zero</option>
                                        <option value="Custom">Custom</option> 
                                    </select>
                        
                                    <input type="text" id="readingPadChar" size="1" maxLength="1" value="" onkeyup="updateFormat('reading', 'padChar');" />
                                </td>
                            </tr><tr align="center">
                                <td colspan="2">
                                    <a href="javascript:displayHelper($('valueHelper'));">Help with Format</a>
                                </td>
                            </tr>
                        </table>
	    			</div> 
	    			
                    <div id="timestampFormatDiv" style="display:none"> 
                        <div id="timestampWords"> </div>
                        <table><tr>
                                <td>Reading Type:</td>
                                <td>
                                    <select id="timestampReadingType" onchange="updateFormat('timestamp', 'readingType');">
                                        <c:forEach var="readingTypeValue" items="${readingTypes}">
                                            <option value="${readingTypeValue}">${readingTypeValue}</option>
                                        </c:forEach>
                                    </select>
                                </td>
                            </tr><tr>
                                <td>Timestamp Pattern: </td>
                                <td>
                                   <select id="timestampFormatSelect" onchange="updateFormat('timestamp', 'formatWithSelect');">
                                        <option value="No Format" selected="selected">No Format</option>
                                        <option value="Custom">Custom</option>
                                        <option value="dd/MM/yyyy">dd/MM/yyyy</option>
                                        <option value="MM/dd/yyyy">MM/dd/yyyy</option>
                                        <option value="hh:mm:ss a">hh:mm:ss a</option>
                                        <option value="HH:mm:ss">HH:mm:ss</option>
                                    </select>
                                    <input type="text" id="timestampFormat" maxlength="30" value="" onkeyup="updateFormat('timestamp', 'formatWithSelectText');"/> 
                                </td>
                            </tr><tr>
                                <td>Field Size:</td>
                                <td>
                                    <input type="text" id="timestampMaxLength" size="5" maxlength="5" value="" onkeyup="updateFormat('timestamp', 'maxLength');" /> (0 for no max)
                                </td>
                            </tr><tr>
                                <td>Padding:</td>
                                <td>
                                    <select id="timestampPadSide" onchange="updateFormat('timestamp', 'padSide');">
                                        <option value="none">None</option>
                                        <option value="left">Left</option>
                                        <option value="right">Right</option> 
                                    </select>
                                    Character
                                    <select id="timestampPadCharSelect" onchange="updateFormat('timestamp', 'padCharSelect');">
                                        <option value="Space">Space</option>
                                        <option value="Zero">Zero</option>
                                        <option value="Custom">Custom</option> 
                                    </select>
                        
                                    <input type="text" id="timestampPadChar" size="1" maxLength="1" value="" onkeyup="updateFormat('timestamp', 'padChar');" />
                                </td>
                            </tr><tr align="center">
                                <td colspan="2">
                                    <a href="javascript:displayHelper($('timestampHelper'));" >Help with Format</a>
                                </td>
                            </tr>
                        </table>
                    </div>
                    
                    <div id="plainTextDiv" style="display:none">
                        <table><tr>
                                <td>Plain Text Input:</td>
                                <td>
                                    <input type="text" id="plainFormat" maxlength="30" value="" onkeyup="updateFormat('plain', 'formatWithoutSelect');">
                                </td>
                            </tr>
                        </table>
	    			</div>
                    
                    <div id="genericFormatDiv" style="display:none">
                        <table><tr>
                                <td>Field Size:</td>
                                <td>
                                    <input type="text" id="genericMaxLength" size="5" maxlength="5" value="" onkeyup="updateFormat('generic', 'maxLength');" /> (0 for no max)
                                </td>
                            </tr><tr>
                                <td>Padding:</td>
                                <td>
                                    <select id="genericPadSide" onchange="updateFormat('generic', 'padSide');">
                                        <option value="none">None</option>
                                        <option value="left">Left</option>
                                        <option value="right">Right</option> 
                                    </select>
                                    Character
                                    <select id="genericPadCharSelect" onchange="updateFormat('generic', 'padCharSelect');">
                                        <option value="Space">Space</option>
                                        <option value="Zero">Zero</option>
                                        <option value="Custom">Custom</option> 
                                    </select>
                        
                                    <input type="text" id="genericPadChar" size="1" maxLength="1" value="" onkeyup="updateFormat('generic', 'padChar');" />
                                </td>
                            </tr>
                        </table>
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
		<div id="valueHelper" class="popUpDiv" style="display:none">
			<!--  fix for IE6 bug (see itemPicker.css for more info) -->
			<!--[if lte IE 6.5]><iframe></iframe><![endif]-->
			<div style="border: 3px solid #888; padding: 5px 5px; font-size: 10px;" >
				<div align="right"> <a href="javascript:displayHelper($('valueHelper'));" >close</a> </div>
				<br/>
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
		<h3>Format Preview:</h3>
		<div id="preview"></div>
		<script type="text/javascript"> 
			updatePreview();
		</script>
		
	</form>

</cti:standardPage>