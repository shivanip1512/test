<%@ tag dynamic-attributes="extraInputs" trimDirectiveWhitespaces="true" %>

<%@ attribute name="action" required="true" %>
<%@ attribute name="blockOnSubmit" type="java.lang.Boolean" %>
<%@ attribute name="groupDataJson" required="true" %>
<%@ attribute name="pickerType" required="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.common.device.bulk.deviceSelection">

    <div class="column-6-6-6-6 ML0" style="width:80%;">
        <div class="column one">
        
            <input type="hidden" id="blockOnSubmit" value="${blockOnSubmit}"/>
            <cti:msg2 var="noGroupSelectedAlertText" key=".selectDevicesByGroupTree.noGroupSelectedAlertText"/>
            <input type="hidden" id="noGroupSelectedText" value="${noGroupSelectedAlertText}"/>
        
            <%-- DEVICES --%>
            <div class="page-action-area">
                <form id="selectDevicesForm" method="post" action="${action}" class="dn">
                    <cti:csrfToken/>
                    <input type="hidden" id="deviceIds" name="idList.ids"/>
                    <input type="hidden" name="collectionType" value="idList"/>
                    <tags:mapToHiddenInputs values="${pageScope.extraInputs}"/>
                </form>
                <tags:pickerDialog id="selectDevicesPicker" 
                    type="${pageScope.pickerType}"
                    destinationFieldId="deviceIds" 
                    multiSelectMode="true" 
                    linkType="none"
                    endEvent="yukon:ui:bulk:device:selection:selectDevices"/>
                <cti:button renderMode="appButton" icon="icon-app icon-app-32-brick-add" classes="js-device"/>
                <br/><br/><a href="#" class="js-device"><i:inline key=".selectByDevice"/></a>
            </div>
    
        </div>
        
        <div class="column two nogutter">
                
            <%-- GROUP --%>
            <div class="page-action-area">
                <cti:msg2 var="addDeviceTitle" key=".selectDevicesByGroupTitle"/>
                <cti:msg2 var="submitButtonText" key=".selectDevicesByGroupTree.submitButtonText"/>
                <cti:msg2 var="cancelButtonText" key=".selectDevicesByGroupTree.cancelButtonText"/>
                
                <form id="selectDevicesByGroupForm" method="post" action="${action}">
                    <cti:csrfToken/>
                    <input type="hidden" name="collectionType" value="group"/>                
                    <tags:mapToHiddenInputs values="${pageScope.extraInputs}"/>
                    <jsTree:nodeValueSelectingPopupTree fieldId="group-name"
                                                        fieldName="group.name"
                                                        nodeValueName="groupName"
                                                        submitButtonText="${submitButtonText}"
                                                        cancelButtonText="${cancelButtonText}"
                                                        submitCallback="yukon.ui.bulk.device.selection.submitSelectDevicesByGroupForm();"
                                                        id="bulkDeviceSelectionByGroupTree"
                                                        triggerElement="selectByGroupButton, #selectByGroupLink"
                                                        dataJson="${groupDataJson}"
                                                        title="${addDeviceTitle}"
                                                        includeControlBar="true"/>
                </form>
                <cti:button renderMode="appButton" id="selectByGroupButton" icon="icon-app icon-app-32-folder"/>
                <br/><br/><a href="#" id="selectByGroupLink"><i:inline key=".selectDevicesByGroup"/></a>
            </div>
            
        </div>
            
        <div class="column three nogutter">
            
            <%-- ADDRESS RANGE --%>
            <div class="page-action-area">
            
                <cti:msg2 var="selectAddressPopupTitle" key=".selectAddressPopupTitle"/>
    
                <tags:simplePopup id="byAddrPopup" title="${selectAddressPopupTitle}" styleClass="deviceSelectionPopup">
                    <form id="addByAddressForm" method="post" action="${action}">
                        <cti:csrfToken/>
                        <ul style="color: red">
                            <li class="dn rangeMsg lessThanZero"><i:inline key=".errLessThanZero"/></li>
                            <li class="dn rangeMsg outOfRange"><i:inline key=".errOutOfRange"/></li>
                            <li class="dn rangeMsg undefinedStartAddress"><i:inline key=".errNoStart"/></li>
                            <li class="dn rangeMsg undefinedEndAddress"><i:inline key=".errNoEnd"/></li>
                            <li class="dn rangeMsg startTooHigh"><i:inline key=".errBigStart"/></li>
                            <li class="dn rangeMsg endTooHigh"><i:inline key=".errBigEnd"/></li>
                        </ul>
                        
                        <input type="hidden" name="collectionType" value="addressRange"/>
                        
                        <tags:nameValueContainer2>
                            <tags:nameValue2 nameKey=".startOfRangeLabel">
                                <input type="text" id="byAddrPopup_startRange" name="addressRange.start" class="undefinedStartAddress outOfRange lessThanZero startTooHigh"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".endOfRangeLabel">
                                <input type="text" id="byAddrPopup_endRange" name="addressRange.end" class="undefinedEndAddress outOfRange endTooHigh"/>
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                        <div class="action-area">
                            <cti:button nameKey="select" classes="primary action" busy="true"/>
                        </div>
                        <tags:mapToHiddenInputs values="${pageScope.extraInputs}"/>
                    </form>
                </tags:simplePopup>
                <cti:button renderMode="appButton" icon="icon-app icon-app-32-address" classes="js-address-range"/>
                <br/><br/><a href="#" class="js-address-range" style="margin-left:-25px;"><i:inline key=".selectDevicesByAddress"/></a>
            </div>
            
        </div>
            
        <div class="column four nogutter">
            
            <%-- FILE UPLOAD --%>
            <div class="page-action-area">
                <cti:msg2 var="selectDataFilePopupTitle" key=".selectDataFilePopupTitle"/>
                
                <tags:simplePopup id="byFileUpload" title="${selectDataFilePopupTitle}" styleClass="deviceSelectionPopup">
                    <form id="addByFileUploadForm" method="post" action="${action}" enctype="multipart/form-data">
                        <cti:csrfToken/>
                        <input type="hidden" name="collectionType" value="fileUpload"/>
                        <input type="hidden" name="isFileUpload" value="true"/>
                        
                        <tags:nameValueContainer2>
                        
                            <tags:nameValue2 nameKey=".selectDataFileType">
                                <select id="byFileUpload_uploadType" name="fileUpload.uploadType">
                                    <option value="ADDRESS"><cti:msg2 key=".dataFileAddress"/></option>
                                    <option value="PAONAME"><cti:msg2 key=".dataFileName"/></option>
                                    <option value="METERNUMBER"><cti:msg2 key=".dataFileMeterNumber"/></option>
                                    <option value="DEVICEID"><cti:msg2 key=".dataFileDeviceId"/></option>
                                    <option value="BULK"><cti:msg2 key=".dataFileBulk"/></option>
                                </select>
                            </tags:nameValue2>
                            
                            <tags:nameValue2 nameKey=".selectDataFile">
                                <tags:file id="fileUpload.dataFile" name="fileUpload.dataFile"/>
                                <br>
                                <span style="font-size: .7em; color: blue;">
                                    <span class="js-note-address dn"><i:inline key=".dataFileNote.address"/></span>
                                    <span class="js-note-pao dn"><i:inline key=".dataFileNote.paoName"/></span>
                                    <span class="js-note-device dn"><i:inline key=".dataFileNote.deviceId"/></span>
                                    <span class="js-note-meter dn"><i:inline key=".dataFileNote.meterNumber"/></span>
                                    <span class="js-note-bulk dn"><i:inline key=".dataFileNote.bulk"/></span>
                                </span>
                            </tags:nameValue2>
                            
                        </tags:nameValueContainer2>
                        
                        <div class="action-area">
                            <cti:button type="submit" busy="true" nameKey="selectDevices"/>
                            <tags:mapToHiddenInputs values="${pageScope.extraInputs}"/>
                        </div>
                    </form>
                </tags:simplePopup>
    
                <cti:button renderMode="appButton" type="submit" icon="icon-app icon-app-32-page-upload" classes="js-file-upload"/>
                <br/><br/><a href="#" class="js-file-upload" style="margin-left:-10px;"><i:inline key=".selectDevicesByFile"/></a>
            </div>
        
        </div>
    </div>
    
    <cti:includeScript link="/resources/js/common/yukon.ui.bulk.device.selection.js"/>

</cti:msgScope>