<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="amr" page="pointImport">
    <cti:includeScript link="/JavaScript/pointImport.js"/>
    
    <tags:boxContainer2 nameKey="mainBox">
        <div style="width: 50%; float: left;">
            <div class="bottomPadded">
            <h3><i:inline key=".importHeader"/></h3>
            <i:inline key=".importDescription"/>
            </div>
            <form id="importForm" method="post" action="/bulk/pointImport/submitImport" enctype="multipart/form-data">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".importTypeSelectLabel">
                        <select name="importType" id="importTypeSelect">
                            <c:forEach var="importType" items="${importTypes}">
                                <option value="${importType}">
                                    <cti:msg2 key="${importType}"/>
                                </option>
                            </c:forEach>
                        </select>
                    </tags:nameValue2>
                    <cti:url var="folderImg" value="/WebConfig/yukon/Icons/folder_edit.gif"/>
                    <tags:nameValue2 nameKey=".calculationUploadLabel" rowId="calculationFile">
                        <img src="${folderImg}">
                        <input type="file" name="calculationFile">
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".pointUploadLabel">
                        <img src="${folderImg}">
                        <input type="file" name="dataFile">
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".empty" excludeColon="true">
                        <input type="checkbox" name="ignoreInvalidColumns" style="padding-left:0; margin-left:0;">
                        <i:inline key=".ignoreInvalidText"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
                <br><cti:button type="submit" nameKey="importSubmitButton"/>
            </form>
            <br><br>
            <div id="calculationInstructions" style="display:none;">
                <div class="bottomPadded">
                    <h3><i:inline key=".calculationImportInstructionsHeader"/></h3>
                    <i:inline key=".importInstructions"/>
                </div>
                <div>
                    <tags:importFormatDisplay format="${calculationFormat}" formatName="calculationFormat"/>
                </div>
            </div>
        </div>
        
        
        <div style="width: 50%; float: left;">
            <div class="bottomPadded">
                <h3><i:inline key=".importInstructionsHeader"/></h3>
                <i:inline key=".importInstructions"/>
            </div>
            <div id="ANALOG_instructions" class=" instructions">
                <tags:importFormatDisplay format="${analogPointFormat}" formatName="analogFormat"/>
            </div>
            <div id="STATUS_instructions" class=" instructions" style="display:none;">
                <tags:importFormatDisplay format="${statusPointFormat}" formatName="statusFormat"/>
            </div>
            <div id="ACCUMULATOR_instructions" class=" instructions" style="display:none;">
                <tags:importFormatDisplay format="${accumulatorPointFormat}" formatName="accumulatorFormat"/>
            </div>
            <div id="CALC_ANALOG_instructions" class=" instructions" style="display:none;">
                <tags:importFormatDisplay format="${calcAnalogPointFormat}" formatName="calcAnalogFormat"/>
            </div>
            <div id="CALC_STATUS_instructions" class=" instructions" style="display:none;">
                <tags:importFormatDisplay format="${calcStatusPointFormat}" formatName="calcStatusFormat"/>
            </div>
        </div>
    </tags:boxContainer2>
</cti:standardPage>