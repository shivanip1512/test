<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jstl/xml" %>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>


<cti:standardPage title="Billing Format Editor" module="amr">
<cti:standardMenu menuSelection="billing|setup"/>
<cti:breadCrumbs>
    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
    <cti:crumbLink url="/spring/dynamicBilling/overview" title="Billing Setup" />
    &gt; ${title}
</cti:breadCrumbs>

<script type="text/javascript">
    Event.observe(window,'load', function() {selectedFieldsChanged();});
</script>

<ct:simplePopup id="roundingHelper" title="Rounding Mode Help" onClose="toggleHelperPopup('roundingHelper');">
    <cti:url var="roundingHelperUrl" value="/WEB-INF/pages/amr/dynamicBilling/roundingHelper.jsp"/>
    <jsp:include page="${roundingHelperUrl}"/>
</ct:simplePopup>

<ct:simplePopup id="valueHelper" title="Format Help" onClose="toggleHelperPopup('valueHelper');">
    <cti:url var="valueHelperUrl" value="/WEB-INF/pages/amr/dynamicBilling/valueHelper.jsp"/>
    <jsp:include page="${valueHelperUrl}"/>
</ct:simplePopup>

<ct:simplePopup id="timestampHelper" title="Format Help" onClose="toggleHelperPopup('timestampHelper');">
    <cti:url var="timestampHelperUrl" value="/WEB-INF/pages/amr/dynamicBilling/timestampHelper.jsp"/>
    <jsp:include page="${timestampHelperUrl}"/>
</ct:simplePopup>
        
<cti:includeScript link="/JavaScript/yukonGeneral.js"/>
<cti:includeScript link="/JavaScript/dynamicBillingFileGenerator.js"/>
<cti:includeScript link="SCRIPTACULOUS_EFFECTS"/>

    <table class="widgetColumns">
        <tr>
            <td>
                <h2 style="display: inline;">
                    ${title }
                </h2>
            </td>
        </tr>
    </table>
    <br>
    
    <div align="center">
        <div id="errorMsg" style="color:#CC0000;">&nbsp;</div>
    </div>

    <form id="begin" name="begin" action="" method="post" autocomplete="off">
    
        <%--  FORMAT SETUP --%>
        <ct:sectionContainer title="Format Setup" id="dbgFormatSetup">
            <ct:nameValueContainer>
                <ct:nameValue name="Name of Format">
                    <input type="text" onkeyup="updateFormatName()" name="formatName" style="width:300px" id="formatName" value="<c:out value="${format.name}"/>" ><span style="color:#CC0000;">*</span>
                </ct:nameValue>
                <ct:nameValue name="Delimiter">
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
    
                </ct:nameValue>
                <ct:nameValue name="Header">
                    <input style="width:500px" type="text" name="header" id="headerField" maxlength="255" value="<c:out value="${format.header}" />" onKeyUp="updatePreview();" />
                </ct:nameValue>
                <ct:nameValue name="Footer">
                    <input style="width:500px" type="text" name="footer" id="footerField" maxlength="255" value="<c:out value="${format.footer}" />" onKeyUp="updatePreview();" />
                </ct:nameValue>
            
            </ct:nameValueContainer>
        <input type="hidden" id="formatId" name="formatId" value="${initiallySelected}" >
        <input type="hidden" id="fieldArray" name="fieldArray" value="">
        </ct:sectionContainer>
        <br>
        
        <table>
            <tr>
            
                <%--  FIELD SETUP --%>
                <td width="35%" valign="top">
                    
                    <ct:sectionContainer title="Field Setup" id="dbgFieldSetup">
                    <table width="335" border="0" cellpadding="0" cellspacing="0">
                        <tr>
                            <td colspan="2">
                                <h4 style="display: inline;">Selected Fields</h4><span style="color:#CC0000;">*</span>
                            </td>
                        </tr>
                        <tr>
                            <td width="260">
                                <select id="selectedFields" name="selectedFields" style="width:260px;" size="7" onchange="selectedFieldsChanged()" >
                                    <c:forEach var="field" items="${selectedFields}" varStatus="status">
                                        <c:set var="selected" value=""/>
                                        <c:if test="${status.count == 1}">
                                            <c:set var="selected" value="selected"/>
                                        </c:if>
                                        <option ${selected} format="<c:out value="${field.format}" />" maxLength="<c:out value="${field.maxLength}" />" padChar="<c:out value="${field.padChar}" />" padSide="<c:out value="${field.padSide}" />" readingType="<c:out value="${field.readingType}" />" readingChannel="<c:out value="${field.channel}" />" roundingMode="<c:out value="${field.roundingMode}" />" >${field.name}</option>
                                    </c:forEach>
                                </select>
                            </td>
                            <td width="75px" valign="top">
                                <div style="padding-left:10px;">
                                    <input type="button" id="upArrowButton" onclick="yukonGeneral_moveOptionPositionInSelect(selectedFields, -1);selectedFieldsChanged();" value="Move Up" disabled="disabled" style="width:120px;margin-bottom:10px;"><br>
                                    <input type="button" id="downArrowButton" onclick="yukonGeneral_moveOptionPositionInSelect(selectedFields, 1);selectedFieldsChanged();" value="Move Down" disabled="disabled" style="width:120px;margin-bottom:10px;"><br>
                                    <input type="button" id="removeButton" onclick="removeFromSelected();" value="Remove" disabled="disabled" style="width:120px;margin-bottom:10px;">
                                    <input type="button" id="addButton" onclick="addFieldButton();" value="Add Fields" style="width:120px;">
                                </div>
                            </td>
                            
                        </tr>
                        
                        <tr>
                            <td colspan="2">
                            
                                <div id="addFieldsDropDown" style="display:none;">
                                    <br>
                                    <h4 style="display: inline;">Available Fields</h4><br>
                                    <select id="availableFields" name="availableFields" style="width:260px;" >
                                        <c:forEach var="field" items="${availableFields}">
                                            <option>${field}</option>
                                        </c:forEach>
                                    </select>&nbsp;
                                    <input type="button" onclick="addToSelected('<cti:getProperty property="yukon.BillingRole.DEFAULT_ROUNDING_MODE"/>');" value="Add" style="width:55px;">
                                    <input type="button" onclick="addFieldButton();" value="Done" style="width:59px;">
                                </div>
                            
                            </td>
                        
                        </tr>
                        
                    </table>
                    </ct:sectionContainer>
                </td>            
            
                <%-- SPACER --%>
                <td><div style="width:30px;"></div></td>
            
                <%--  FORMAT SELECTED FIELD --%>
                <td width="65%" valign="top">
                    
                    <ct:sectionContainer title="Format Selected Field" id="dbgFormatSelectedField">
                    <font size="1">* Select a field from 'Selected Fields' list to customize field formatting.</font>        
                    <table>
                        <tr>
                            <td id="fieldFormats" valign="middle" width="600px" >
                                <div id="valueFormatDiv" style="display:none"> 
                                    <div id="valueWords"> </div>
                                    <ct:nameValueContainer>
                                        <ct:nameValue name="Reading Type">
                                            <select id="readingReadingType" onchange="updateFormat('reading', 'readingType');">
                                                <c:forEach var="readingTypeValue" items="${readingTypes}">
                                                    <option value="${readingTypeValue}">${readingTypeValue}</option>
                                                </c:forEach>
                                            </select>
                                        </ct:nameValue>
                                        <ct:nameValue name="Reading Channel">
                                            <select id="readingReadingChannel" onchange="updateFormat('reading', 'readingChannel');">
                                                <c:forEach var="readingChannelValue" items="${readingChannels}">
                                                    <option value="${readingChannelValue}">${readingChannelValue}</option>
                                                </c:forEach>
                                            </select>
                                        </ct:nameValue>
                                        <ct:nameValue name="Rounding Mode">
                                            <select id="readingRoundingMode" onchange="updateFormat('reading', 'roundingMode');">
                                                <c:forEach var="roundingModeValue" items="${roundingModes}">
                                                    <option value="${roundingModeValue}">${roundingModeValue}</option>
                                                </c:forEach>
                                            </select>
                                            <a href="javascript:void(0);" onclick="toggleHelperPopup('roundingHelper');">Help with Rounding</a>
                                        </ct:nameValue>
                                        <ct:nameValue name="Reading Pattern">
                                            <select id="readingFormatSelect" onchange="updateFormat('reading', 'formatWithSelect');">
                                                <option value="No Format" selected="selected">No Format</option>
                                                <option value="Custom">Custom</option>
                                                <option value="###.###">###.###</option>
                                                <option value="####.##">####.##</option>
                                            </select>  
                                            <input type="text" id="readingFormat" maxlength="30" value="" onkeyup="updateFormat('reading', 'formatWithSelectText');" />
                                            
                                            <a href="javascript:void(0);" onclick="toggleHelperPopup('valueHelper');">Help with Format</a>
                                            
                                        </ct:nameValue>
                                        <ct:nameValue name="Field Size">
                                            <input type="text" id="readingMaxLength" size="5" maxlength="5" value="" onkeyup="updateFormat('reading', 'maxLength');" /> (0 for no max)
                                        </ct:nameValue>
                                        <ct:nameValue name="Padding">
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
                                        </ct:nameValue>
                                    </ct:nameValueContainer>
                                </div> 
                                
                                <div id="timestampFormatDiv" style="display:none"> 
                                    <div id="timestampWords"> </div>
                                    <ct:nameValueContainer>
                                        <ct:nameValue name="Reading Type">
                                            <select id="timestampReadingType" onchange="updateFormat('timestamp', 'readingType');">
                                                <c:forEach var="readingTypeValue" items="${readingTypes}">
                                                    <option value="${readingTypeValue}">${readingTypeValue}</option>
                                                </c:forEach>
                                            </select>
                                        </ct:nameValue>
                                        <ct:nameValue name="Reading Channel">
                                            <select id="timestampReadingChannel" onchange="updateFormat('timestamp', 'readingChannel');">
                                                <c:forEach var="readingChannelValue" items="${readingChannels}">
                                                    <option value="${readingChannelValue}">${readingChannelValue}</option>
                                                </c:forEach>
                                            </select>
                                        </ct:nameValue>
                                        <ct:nameValue name="Timestamp Pattern">
                                            <select id="timestampFormatSelect" onchange="updateFormat('timestamp', 'formatWithSelect');">
                                                <option value="No Format" selected="selected">No Format</option>
                                                <option value="Custom">Custom</option>
                                                <option value="dd/MM/yyyy">dd/MM/yyyy</option>
                                                <option value="MM/dd/yyyy">MM/dd/yyyy</option>
                                                <option value="hh:mm:ss a">hh:mm:ss a</option>
                                                <option value="HH:mm:ss">HH:mm:ss</option>
                                            </select>
                                            <input type="text" id="timestampFormat" maxlength="30" value="" onkeyup="updateFormat('timestamp', 'formatWithSelectText');"/>
                                            
                                            <a href="javascript:void(0);" onclick="toggleHelperPopup('timestampHelper');">Help with Format</a>
                                            
                                        </ct:nameValue>
                                        <ct:nameValue name="Field Size">
                                            <input type="text" id="timestampMaxLength" size="5" maxlength="5" value="" onkeyup="updateFormat('timestamp', 'maxLength');" /> (0 for no max)
                                        </ct:nameValue>
                                        <ct:nameValue name="Padding">
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
                                        </ct:nameValue>
                                    </ct:nameValueContainer>
                                </div>
                                
                                <div id="plainTextDiv" style="display:none">
                                <ct:nameValueContainer>
                                    <ct:nameValue name="Plain Text Input">
                                        <input type="text" id="plainFormat" maxlength="30" value="" onkeyup="updateFormat('plain', 'formatWithoutSelect');">
                                    </ct:nameValue>
                                </ct:nameValueContainer>
                                </div>
                                
                                <div id="genericFormatDiv" style="display:none">
                                <input type="hidden" id="genericReadingType" value="DEVICE_DATA"/>
                                <ct:nameValueContainer>
                                    <ct:nameValue name="Field Size">
                                        <input type="text" id="genericMaxLength" size="5" maxlength="5" value="" onkeyup="updateFormat('generic', 'maxLength');" /> (0 for no max)
                                    </ct:nameValue>
                                    <ct:nameValue name="Padding">
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
                                    </ct:nameValue>
                                </ct:nameValueContainer>
                                </div>
                            </td>
                        </tr>
                    </table>
                    </ct:sectionContainer>
                    <br>
                
                </td>
                
            </tr>
        </table>
        <br>
        
        
        
        
        <%--  FORMAT PREVIEW --%>        
        <ct:sectionContainer title="Preview & Save Format" id="dbgFormatPreview">
            <div id="preview"></div>
            <script type="text/javascript"> 
                updatePreview();
                updateFormatName();
            </script>
            
            <br>
            <input type="button" onclick="saveButton();" value="Save" style="width:55px;">
            <input type="button" onclick="cancelButton();" value="Cancel" style="width:59px;">
            
        </ct:sectionContainer>
        
    </form>
    
</cti:standardPage>