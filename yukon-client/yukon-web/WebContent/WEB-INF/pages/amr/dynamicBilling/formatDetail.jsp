<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jstl/xml" %>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="modules.amr.billing.DETAIL">
<cti:msg2 key=".pageName" var="pageName"/>


<script type="text/javascript">
    jQuery(function() {selectedFieldsChanged();});
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

<script>
BILLING_ERRORS = {
        fieldsNotEmpty:"<cti:msg2 key=".errors.fieldsNotEmpty"/>",
        nameNotEmpty:"<cti:msg2 key=".errors.nameNotEmpty"/>",
        nameMaxLength:"<cti:msg2 key=".errors.nameMaxLength"/>",
        invalidPattern:"<cti:msg2 key=".errors.invalidPattern"/>",
        invalidFormat:"<cti:msg2 key=".errors.invalidFormat"/>",
        errorSaving:"<cti:msg2 key=".errors.errorSaving"/>"
};
</script>

    <h2 >
        ${title }
    </h2>

    
    <div align="center">
        <div id="errorMsg" class="error">&nbsp;</div>
    </div>

    <form id="billingFormatForm" name="billingFormatForm" action="" method="post" autocomplete="off">
        <cti:csrfToken/>
    
        <%--  FORMAT SETUP --%>
        <cti:msg2 key=".formatSetup.title" var="formatSetup"/>
        <ct:sectionContainer title="${formatSetup}" id="dbgFormatSetup">
            <ct:nameValueContainer2>
                <ct:nameValue2 nameKey=".nameOfFormat" >
                    <input type="text" onkeyup="updateFormatName()" name="formatName" style="width:300px" id="formatName" value="<c:out value="${format.name}"/>" ><span class="error">*</span>
                </ct:nameValue2>
                <ct:nameValue2 nameKey=".delimiter">
                    <select  name="delimiterChoice" id="delimiterChoice" onchange="updateDelimiter();">
                        <option value="," <c:if test="${format.delim == ','}">selected="selected" </c:if>><cti:msg2 key=".comma"/></option>
                        <option value=";" <c:if test="${format.delim == ';'}">selected="selected" </c:if>><cti:msg2 key=".semicolon"/></option>
                        <option value=":" <c:if test="${format.delim == ':'}">selected="selected" </c:if>><cti:msg2 key=".colon"/></option>
                        <option value="Custom" <c:if test="${format.delim != ',' && format.delim != ';' && format.delim !=':'}">selected="selected" </c:if>><cti:msg2 key=".custom"/></option>
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
    
                </ct:nameValue2>
                <ct:nameValue2 nameKey=".header">
                    <input style="width:500px" type="text" name="header" id="headerField" maxlength="255" value="<c:out value="${format.header}" />" onKeyUp="updatePreview();" />
                </ct:nameValue2>
                <ct:nameValue2 nameKey=".footer">
                    <input style="width:500px" type="text" name="footer" id="footerField" maxlength="255" value="<c:out value="${format.footer}" />" onKeyUp="updatePreview();" />
                </ct:nameValue2>
            
            </ct:nameValueContainer2>
        <input type="hidden" id="formatId" name="formatId" value="${initiallySelected}" >
        <input type="hidden" id="fieldArray" name="fieldArray" value="">
        </ct:sectionContainer>
        <br>
        
        <!--  start Field pair columns -->
        <div class="column-12-12">
                <div class="column one">
            <%--  FIELD SETUP --%>
                    <ct:sectionContainer2 nameKey=".fieldSetup" id="dbgFieldSetup">
                    <div><h4 style="display: inline;"><cti:msg2 key=".selectedFields"/></h4><span class="error">*</span></div>
                    <div>
                        <div>
                            <select id="selectedFields" name="selectedFields" style="width:260px;" size="7" onchange="selectedFieldsChanged()" >
                                <c:forEach var="field" items="${selectedFields}" varStatus="status">
                                    <c:set var="selected" value=""/>
                                    <c:if test="${status.count == 1}">
                                        <c:set var="selected" value="selected"/>
                                    </c:if>
                                    <option ${selected} format="<c:out value="${field.format}" />" maxLength="<c:out value="${field.maxLength}" />" padChar="<c:out value="${field.padChar}" />" padSide="<c:out value="${field.padSide}" />" readingType="<c:out value="${field.readingType}" />" readingChannel="<c:out value="${field.channel}" />" roundingMode="<c:out value="${field.roundingMode}" />" >${field.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="page-action-area">
                            <cti:msg2 key=".moveUp" var="moveUp"/>
                            <cti:msg2 key=".moveDown" var="moveDown"/>
                            <cti:msg2 key=".remove" var="remove"/>
                            <cti:msg2 key=".addFields" var="addFields"/>
                            <cti:button id="upArrowButton" onclick="yukon.ui.aux.yukonGeneral_moveOptionPositionInSelect(selectedFields, -1);selectedFieldsChanged();" label="${moveUp}" disabled="true"/>
                            <cti:button id="downArrowButton" onclick="yukon.ui.aux.yukonGeneral_moveOptionPositionInSelect(selectedFields, 1);selectedFieldsChanged();" label="${moveDown}" disabled="true"/>
                            <cti:button id="removeButton" onclick="removeFromSelected();" label="${remove}" disabled="true"/>
                            <cti:button id="addButton" onclick="addFieldButton();" label="${addFields}"/>
                            
                        </div>
                        
                        <div id="addFieldsDropDown" style="display:none;">
                            <br>
                            <h4 style="display: inline;"><cti:msg2 key=".availableFields"/></h4><br>
                            <select id="availableFields" name="availableFields" style="width:260px;" >
                                <c:forEach var="field" items="${availableFields}">
                                    <option>${field}</option>
                                </c:forEach>
                            </select>&nbsp;
                            <cti:msg2 key=".add" var="add" />
                            <cti:msg2 key=".done" var="done" />
                            <cti:button onclick="addToSelected('${defaultRoundingMode}');" label="${add}"/>
                            <cti:button onclick="addFieldButton();" label="${done}"/>
                        </div>
                    </div>
                    </ct:sectionContainer2>
                </div>
            
                <%--  FORMAT SELECTED FIELD --%>
                <div class="column two nogutter">
                    <ct:sectionContainer2 nameKey="formatSelectedField" id="dbgFormatSelectedField">
                    <font size="1"><cti:msg2 key=".formatSelectedFieldHelp"/></font>        
                    <div>
                        <div id="valueFormatDiv" style="display:none"> 
                            <div id="valueWords"> </div>
                            <ct:nameValueContainer2>
                                <ct:nameValue2 nameKey=".readingType">
                                    <select id="readingReadingType" onchange="updateFormat('reading', 'readingType');">
                                        <c:forEach var="readingTypeValue" items="${readingTypes}">
                                            <option value="${readingTypeValue}">${readingTypeValue}</option>
                                        </c:forEach>
                                    </select>
                                </ct:nameValue2>
                                <ct:nameValue2 nameKey=".readingChannel">
                                    <select id="readingReadingChannel" onchange="updateFormat('reading', 'readingChannel');">
                                        <c:forEach var="readingChannelValue" items="${readingChannels}">
                                            <option value="${readingChannelValue}">${readingChannelValue}</option>
                                        </c:forEach>
                                    </select>
                                </ct:nameValue2>
                                <ct:nameValue2 nameKey=".roundingMode">
                                    <select id="readingRoundingMode" onchange="updateFormat('reading', 'roundingMode');">
                                        <c:forEach var="roundingModeValue" items="${roundingModes}">
                                            <option value="${roundingModeValue}">${roundingModeValue}</option>
                                        </c:forEach>
                                    </select>
                                    <a href="javascript:void(0);" onclick="toggleHelperPopup('roundingHelper');"><cti:msg2 key=".roundingHelp"/></a>
                                </ct:nameValue2>
                                <ct:nameValue2 nameKey=".readingPattern">
                                    <select id="readingFormatSelect" onchange="updateFormat('reading', 'formatWithSelect');">
                                        <option value="No Format" selected="selected"><cti:msg2 key=".noFormat"/></option>
                                        <option value="Custom"><cti:msg2 key=".custom"/></option>
                                        <option value="###.###"><cti:msg2 key=".threePlaces"/></option>
                                        <option value="####.##"><cti:msg2 key=".twoPlaces"/></option>
                                    </select>  
                                    <input type="text" id="readingFormat" maxlength="30" value="" onkeyup="updateFormat('reading', 'formatWithSelectText');" />
                                    
                                    <a href="javascript:void(0);" onclick="toggleHelperPopup('valueHelper');"><cti:msg2 key=".formatHelp"/></a>
                                    
                                </ct:nameValue2>
                                <ct:nameValue2 nameKey=".fieldSize">
                                    <input type="text" id="readingMaxLength" size="5" maxlength="5" value="" onkeyup="updateFormat('reading', 'maxLength');" /> <cti:msg2 key=".fieldSizeHelp"/>
                                </ct:nameValue2>
                                <ct:nameValue2 nameKey=".padding">
                                    <select id="readingPadSide" onchange="updateFormat('reading', 'padSide');">
                                        <option value="none"><cti:msg2 key=".none"/></option>
                                        <option value="left"><cti:msg2 key=".left"/></option>
                                        <option value="right"><cti:msg2 key=".right"/></option> 
                                    </select>
                                    <cti:msg2 key=".character"/>
                                    <select id="readingPadCharSelect" onchange="updateFormat('reading', 'padCharSelect');">
                                        <option value="Space" selected="selected"><cti:msg2 key=".space"/></option>
                                        <option value="Zero"><cti:msg2 key=".zero"/></option>
                                        <option value="Custom"><cti:msg2 key=".custom"/></option> 
                                    </select>
                                    <input type="text" id="readingPadChar" size="1" maxLength="1" value="" onkeyup="updateFormat('reading', 'padChar');" />
                                </ct:nameValue2>
                            </ct:nameValueContainer2>
                        </div> 
                        
                        <div id="timestampFormatDiv" style="display:none"> 
                            <div id="timestampWords"> </div>
                            <ct:nameValueContainer2>
                                <ct:nameValue2 nameKey=".readingType">
                                    <select id="timestampReadingType" onchange="updateFormat('timestamp', 'readingType');">
                                        <c:forEach var="readingTypeValue" items="${readingTypes}">
                                            <option value="${readingTypeValue}">${readingTypeValue}</option>
                                        </c:forEach>
                                    </select>
                                </ct:nameValue2>
                                <ct:nameValue2 nameKey=".readingChannel">
                                    <select id="timestampReadingChannel" onchange="updateFormat('timestamp', 'readingChannel');">
                                        <c:forEach var="readingChannelValue" items="${readingChannels}">
                                            <option value="${readingChannelValue}">${readingChannelValue}</option>
                                        </c:forEach>
                                    </select>
                                </ct:nameValue2>
                                <ct:nameValue2 nameKey=".timestampPattern">
                                    <select id="timestampFormatSelect" onchange="updateFormat('timestamp', 'formatWithSelect');">
                                        <option value="No Format" selected="selected"><cti:msg2 key=".noFormat"/></option>
                                        <option value="Custom"><cti:msg2 key=".custom"/></option>
                                        <option value="dd/MM/yyyy"><cti:msg2 key=".dayMonthYear"/></option>
                                        <option value="MM/dd/yyyy"><cti:msg2 key=".monthDayYear"/></option>
                                        <option value="hh:mm:ss a"><cti:msg2 key=".12hour"/></option>
                                        <option value="HH:mm:ss"><cti:msg2 key=".24hour"/></option>
                                    </select>
                                    <input type="text" id="timestampFormat" maxlength="30" value="" onkeyup="updateFormat('timestamp', 'formatWithSelectText');"/>
                                    
                                    <a href="javascript:void(0);" onclick="toggleHelperPopup('timestampHelper');"><cti:msg2 key=".formatHelp"/></a>
                                    
                                </ct:nameValue2>
                                <ct:nameValue2 nameKey=".fieldSize">
                                    <input type="text" id="timestampMaxLength" size="5" maxlength="5" value="" onkeyup="updateFormat('timestamp', 'maxLength');" /> <cti:msg2 key=".fieldSizeHelp"/>
                                </ct:nameValue2>
                                <ct:nameValue2 nameKey=".padding">
                                    <select id="timestampPadSide" onchange="updateFormat('timestamp', 'padSide');">
                                        <option value="none"><cti:msg2 key=".none"/></option>
                                        <option value="left"><cti:msg2 key=".left"/></option>
                                        <option value="right"><cti:msg2 key=".right"/></option> 
                                    </select>
                                    <cti:msg2 key=".character"/>
                                    <select id="timestampPadCharSelect" onchange="updateFormat('timestamp', 'padCharSelect');">
                                        <option value="Space"><cti:msg2 key=".space"/></option>
                                        <option value="Zero"><cti:msg2 key=".zero"/></option>
                                        <option value="Custom"><cti:msg2 key=".custom"/></option> 
                                    </select2>
                        
                                    <input type="text" id="timestampPadChar" size="1" maxLength="1" value="" onkeyup="updateFormat('timestamp', 'padChar');" />
                                </ct:nameValue2>
                            </ct:nameValueContainer2>
                        </div>
                        
                        <div id="plainTextDiv" style="display:none">
                        <ct:nameValueContainer2>
                            <ct:nameValue2 nameKey=".plainTextInput">
                                <input type="text" id="plainFormat" maxlength="30" value="" onkeyup="updateFormat('plain', 'formatWithoutSelect');">
                            </ct:nameValue2>
                        </ct:nameValueContainer2>
                        </div>
                        
                        <div id="genericFormatDiv" style="display:none">
                        <input type="hidden" id="genericReadingType" value="DEVICE_DATA"/>
                        <ct:nameValueContainer2>
                            <ct:nameValue2 nameKey=".fieldSize">
                                <input type="text" id="genericMaxLength" size="5" maxlength="5" value="" onkeyup="updateFormat('generic', 'maxLength');" /> <cti:msg2 key=".fieldSizeHelp"/>
                            </ct:nameValue2>
                            <ct:nameValue2 nameKey=".padding">
                                <select id="genericPadSide" onchange="updateFormat('generic', 'padSide');">
                                    <option value="none"><cti:msg2 key=".none"/></option>
                                    <option value="left"><cti:msg2 key=".left"/></option>
                                    <option value="right"><cti:msg2 key=".right"/></option> 
                                </select>
                                <cti:msg2 key=".character"/>
                                <select id="genericPadCharSelect" onchange="updateFormat('generic', 'padCharSelect');">
                                    <option value="Space"><cti:msg2 key=".space"/></option>
                                    <option value="Zero"><cti:msg2 key=".zero"/></option>
                                    <option value="Custom"><cti:msg2 key=".custom"/></option> 
                                </select>
                                <input type="text" id="genericPadChar" size="2" maxLength="1" value="" onkeyup="updateFormat('generic', 'padChar');" />
                            </ct:nameValue2>
                        </ct:nameValueContainer2>
                        </div>
                    </div>
                    </ct:sectionContainer2>
                    <br>
            </div>
        </div>

        <br>
        
        <%--  FORMAT PREVIEW --%>        
        <ct:sectionContainer2 nameKey=".previewAndSave" id="dbgFormatPreview" styleClass="clear">
            <pre id="preview"></pre>
            <script type="text/javascript"> 
                updatePreview();
                updateFormatName();
            </script>
            
        </ct:sectionContainer2>
        <div class="page-action-area stacked">
            <cti:button nameKey="save" classes="primary action" onclick="saveButton();"/>
            <cti:button nameKey="cancel" id="btnCancelSetup" />
            
        </div>
    </form>

</cti:msgScope>