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

<c:set var="tree_id" value="bulkDeviceSelectionByGroupTree"/>
<c:set var="byAddrPopupId" value="byAddrPopup"/>

<cti:msg2 var="noGroupSelectedAlertText" key=".selectDevicesByGroupTree.noGroupSelectedAlertText"/>
<cti:msg2 var="dataFileNoteAddress" key=".dataFileNote.address"/>
<cti:msg2 var="dataFileNotePaoName" key=".dataFileNote.paoName"/>
<cti:msg2 var="dataFileNoteDeviceId" key=".dataFileNote.deviceId"/>
<cti:msg2 var="dataFileNoteMeterNumber" key=".dataFileNote.meterNumber"/>
<cti:msg2 var="dataFileNoteBulk" key=".dataFileNote.bulk"/>
<cti:msg2 key=".cancel" var="cancel"/>

<script type="text/javascript">
$(function(){
    $("#selectDevicesButton").click(function(e) {selectDevicesPicker.show.call(selectDevicesPicker);});

    $("#addByAddressForm button").click(function(e) {
        $(e.currentTarget).attr("disabled","disabled");
          
        var errors = validateAddressRange();
        if (errors.length === 0) {
            //submit the form
            $(e.currentTarget).closest("form")[0].submit();
            return true;
        } else {
            //show the error
            $(errors.join(",")).show().addClass("error");
            $(e.currentTarget).removeAttr("disabled");
            return false;
        }
    });

    $("#addByAddressForm input:text").keyup(function(e) {
        $(e.currentTarget).val($(e.currentTarget).val().replace(/\D/g, ''));
    });
});

function selectDevices(devices) {
    var ids = [];
    
    // Get the id out of each of the selected devices
    for (var i=0;i<devices.length;i++) {
        ids[i] = devices[i].paoId;
    }
    
    document.getElementById('deviceIds').value = ids;
    if ('${blockOnSubmit}') {
        yukon.ui.blockPage();
    }
    document.getElementById('selectDevicesForm').submit();
    return true;
}

function submitSelectDevicesByGroupForm() {

    if (document.getElementById('group-name').value == '') {
        alert('${noGroupSelectedAlertText}');
        return false;
    }
    if ('${blockOnSubmit}') {
        yukon.ui.blockPage();
    }
    document.getElementById('selectDevicesByGroupForm').submit();
}

function selectDevicesByAddress() {
    return true;
}

function updateFileNote(id){

    var selection = document.getElementById(id + '_uploadType').value;
    var fileNote = document.getElementById(id + '_fileNote');
    
    if (selection == 'ADDRESS') {
        fileNote.innerHTML = '${dataFileNoteAddress}';
    } else if (selection == 'PAONAME') {
        fileNote.innerHTML = '${dataFileNotePaoName}';
    } else if (selection == 'DEVICEID') {
        fileNote.innerHTML = '${dataFileNoteDeviceId}';
    } else if (selection == 'METERNUMBER') {
        fileNote.innerHTML = '${dataFileNoteMeterNumber}';
    } else if (selection == 'BULK') {
        fileNote.innerHTML = '${dataFileNoteBulk}';
    }
}

function toggleByAddrPopup(id) {
    clearErrors();
    clearAddrFields();
    togglePopup(id);
    if ($('#' + id).is(':visible')) { 
        $('#' + id + '_startRange').focus();
    }
}

function clearAddrFields(id) {
    clearErrors();
    $('#' + id + '_startRange').val('');
    $('#' + id + '_endRange').val('');
}

function toggleByFileUploadPopup(id) {
    togglePopup(id);
    if ($('#' + id).is(':visible')) {
        $('#' + id + '_uploadType')[0].options[0].selected = true;
        updateFileNote(id);
        $('#' + id + '_uploadType').focus();
    }
}

function togglePopup(id) {
    var popupElem = $('#' + id);
    if (popupElem.is(':visible')) {
        popupElem.dialog('close');
    } else {
        popupElem.dialog('open');
    }
}

function clearErrors() {
    $(".undefinedStartAddress, .undefinedEndAddress, .lessThanZero, .outOfRange, .startTooHigh, .endTooHigh").removeClass('error');
    $(".rangeMsg").hide();
}

function validateAddressRange() {
    clearErrors();
    var start = parseInt($("#${byAddrPopupId}_startRange").val());
    var end = parseInt($("#${byAddrPopupId}_endRange").val());
    var errors = [];
    var MAX_INT = 2147483648; //Maximum 32-bit integer, which is how the range values are eventually interpreted
    
    //separate errors
    if (isNaN(start)) {
        errors.push(".undefinedStartAddress");
    }
    
    if (isNaN(end)) {
        errors.push(".undefinedEndAddress");
    }
    
    if (start < 0) {
        errors.push(".lessThanZero");
    }
    
    if (start > MAX_INT) {
        errors.push(".startTooHigh");
    }
    
    if (end > MAX_INT) {
        errors.push(".endTooHigh");
    }
          
    if (start > end) {
        errors.push(".outOfRange");
    }
    
    return errors;
};
</script>

<div class="column-12-12">
    <div class="column one">
    
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
                endAction="selectDevices"/>
            <cti:button nameKey="selectByDevice" id="selectDevicesButton"/>
            <span><i:inline key=".selectDevicesTitle"/></span>
        </div>
        
        <%-- GROUP --%>
        <div class="page-action-area">
            <cti:msg2 var="addDeviceTitle" key=".selectDevicesByGroupTitle"/>
            <cti:msg2 var="rootLabel" key=".selectDevicesByGroupTree.rootLabel"/>
            <cti:msg2 var="submitButtonText" key=".selectDevicesByGroupTree.submitButtonText"/>
            <cti:msg2 var="cancelButtonText" key=".selectDevicesByGroupTree.cancelButtonText"/>
            
            <form id="selectDevicesByGroupForm" method="get" action="${action}">
                <input type="hidden" name="collectionType" value="group"/>
                <tags:mapToHiddenInputs values="${pageScope.extraInputs}"/>
                <jsTree:nodeValueSelectingPopupTree fieldId="group-name"
                                                    fieldName="group.name"
                                                    nodeValueName="groupName"
                                                    submitButtonText="${submitButtonText}"
                                                    cancelButtonText="${cancelButtonText}"
                                                    submitCallback="submitSelectDevicesByGroupForm();"
                                                    id="bulkDeviceSelectionByGroupTree"
                                                    triggerElement="selectByGroupButton"
                                                    dataJson="${groupDataJson}"
                                                    title="${addDeviceTitle}"
                                                    includeControlBar="true"/>
            </form>
            <cti:button nameKey="selectDevicesByGroup" id="selectByGroupButton"/>
            <span>${addDeviceTitle}</span>
        </div>
    </div>
    
    <div class="column two nogutter">
        
        <%-- ADDRESS RANGE --%>
        <div class="page-action-area">
        
            <cti:msg2 var="selectAddressPopupTitle" key=".selectAddressPopupTitle"/>

            <tags:simplePopup id="${byAddrPopupId}" title="${selectAddressPopupTitle}" styleClass="deviceSelectionPopup"  onClose="clearAddrFields('${byAddrPopupId}');">
                <form id="addByAddressForm" method="get" action="${action}">
                    <ul style="color: red">
                        <cti:msg2 key=".errLessThanZero" var="lessThanZero"/>
                        <cti:msg2 key=".errOutOfRange" var="outOfRange"/>
                        <cti:msg2 key=".errNoStart" var="noStart"/>
                        <cti:msg2 key=".errNoEnd" var="noEnd"/>
                        <cti:msg2 key=".errBigStart" var="startTooHigh"/>
                        <cti:msg2 key=".errBigEnd" var="endTooHigh"/>
                        
                        <li class="dn rangeMsg lessThanZero">${lessThanZero}</li>
                        
                        <li class="dn rangeMsg outOfRange">${outOfRange}</li>
                        
                        <li class="dn rangeMsg undefinedStartAddress">${noStart}</li>
                        
                        <li class="dn rangeMsg undefinedEndAddress">${noEnd}</li>
                        
                        <li class="dn rangeMsg startTooHigh">${startTooHigh}</li>
                        
                        <li class="dn rangeMsg endTooHigh">${endTooHigh}</li>
                    </ul>
                    
                    <input type="hidden" name="collectionType" value="addressRange"/>
                    
                    <cti:msg2 var="startOfRangeLabel" key=".startOfRangeLabel"/>
                    <cti:msg2 var="endOfRangeLabel" key=".endOfRangeLabel"/>
                    
                    <tags:nameValueContainer>
                        <tags:nameValue name="${startOfRangeLabel}">
                            <input type="text" id="${byAddrPopupId}_startRange" name="addressRange.start" class="undefinedStartAddress outOfRange lessThanZero startTooHigh"/>
                        </tags:nameValue>
                        <tags:nameValue name="${endOfRangeLabel}">
                            <input type="text" id="${byAddrPopupId}_endRange" name="addressRange.end" class="undefinedEndAddress outOfRange endTooHigh"/>
                        </tags:nameValue>
                    </tags:nameValueContainer>
                    <div class="action-area">
                        <cti:button nameKey="select" classes="primary action js-blocker"/>
                    </div>
                    <tags:mapToHiddenInputs values="${pageScope.extraInputs}"/>
                </form>
            </tags:simplePopup>
            <cti:button nameKey="selectDevicesByAddress" id="addressButton" onclick="toggleByAddrPopup('${byAddrPopupId}');"/>
            <span><i:inline key=".selectDevicesByAddressTitle"/></span>
        </div>
        
        <%-- FILE UPLOAD --%>
        <div class="page-action-area">
            <c:set var="byFileUploadId" value="byFileUpload"/>
            <cti:msg2 var="selectDataFilePopupTitle" key=".selectDataFilePopupTitle"/>
            
            <tags:simplePopup id="${byFileUploadId}" title="${selectDataFilePopupTitle}" styleClass="deviceSelectionPopup">
                <form id="addByFileUploadForm" method="post" action="${action}" enctype="multipart/form-data">
                    <cti:csrfToken/>
                    <input type="hidden" name="collectionType" value="fileUpload"/>
                    <input type="hidden" name="isFileUpload" value="true"/>
                    
                    <cti:msg2 var="typeLabel" key=".selectDataFileType"/>
                    <cti:msg2 var="dataFileLabel" key=".selectDataFile"/>
                    
                    <tags:nameValueContainer>
                    
                        <tags:nameValue name="${typeLabel}">
                            <select id="${byFileUploadId}_uploadType" name="fileUpload.uploadType" onchange="updateFileNote('${byFileUploadId}')">
                                <option value="ADDRESS">
                                    <cti:msg2 key=".dataFileAddress"/>
                                </option>
                                <option value="PAONAME">
                                    <cti:msg2 key=".dataFileName"/>
                                </option>
                                <option value="METERNUMBER">
                                    <cti:msg2 key=".dataFileMeterNumber"/>
                                </option>
                                <option value="DEVICEID">
                                    <cti:msg2 key=".dataFileDeviceId"/>
                                </option>
                                <option value="BULK">
                                    <cti:msg2 key=".dataFileBulk"/>
                                </option>
                            </select>
                        </tags:nameValue>
                        
                        <tags:nameValue name="${dataFileLabel}">
                            <tags:file id="fileUpload.dataFile" name="fileUpload.dataFile"/>
                            <br>
                            <span id="${byFileUploadId}_fileNote" style="font-size: .7em; color: blue;">${dateFileNoteAddress}</span>
                        </tags:nameValue>
                        
                    </tags:nameValueContainer>
                    
                    <div class="action-area">
                        <cti:msg2 var="selectDevicesPopupButtonText" key=".selectDevicesPopupButtonText"/>
                        <cti:button type="submit" busy="true" label="${selectDevicesPopupButtonText}"/>
                        <tags:mapToHiddenInputs values="${pageScope.extraInputs}"/>
                    </div>
                </form>
            </tags:simplePopup>
            
            <cti:button nameKey="selectDevicesByFile" id="fileButton" onclick="toggleByFileUploadPopup('${byFileUploadId}');"/>
            <span><i:inline key=".selectDevicesByFileTitle"/></span>
        </div>
    </div>
</div>

</cti:msgScope>