<%@ tag dynamic-attributes="extraInputs" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags/jsTree" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="text" description="Link text and dialog title. Defaults to 'Select Devices'" %>
<%@ attribute name="submitOnCompletion" type="java.lang.Boolean" 
              description="When true, will submit the closest parent form after successfully selecting devices" %>

<%--Device group attributes --%>
<%@ attribute name="multi" type="java.lang.Boolean" 
              description="If true, selection mode for device group will be multiselection. Default: single selection." %>
<%@ attribute name="predicates" 
              description="Optional comma separated strings of DeviceGroupPredicateEnum entry names to filter the 
                           tree data by. Default: 'NON_HIDDEN'." %>
<%@ attribute name="callbacks" type="java.util.Set" 
              description="Set of callbacks to be preformed on each node in the device group tree. Can be used to disable 
                           selecting certain nodes." %>
              
<cti:default var="multi" value="${false}"/>
<cti:default var="predicates" value="NON_HIDDEN"/>

<cti:includeCss link="/resources/js/lib/dynatree/skin/ui.dynatree.css"/>
<cti:includeCss link="/resources/js/lib/dynatree/skin/device.group.css"/>
<cti:checkRolesAndProperties value="DEVELOPMENT_MODE">
    <cti:includeScript link="JQUERY_TREE"/>
</cti:checkRolesAndProperties>
<cti:checkRolesAndProperties value="!DEVELOPMENT_MODE">
    <cti:includeScript link="JQUERY_TREE_MIN"/>
</cti:checkRolesAndProperties>
<cti:includeScript link="JQUERY_TREE_HELPERS"/>

<cti:deviceGroupHierarchyJson var="groups" predicates="${predicates}" callbacks="${callbacks}"/>

<cti:msgScope paths="yukon.common.device.bulk.deviceSelection">
<cti:uniqueIdentifier var="id" prefix="collection"/>

<cti:msg2 var="defaultText" key=".selectDevices"/>
<c:set var="text" value="${(empty text) ? defaultText : text}" />

<c:set var="dataFormAttr" value="${submitOnCompletion ? 'data-submit-on-completion' : ''}" />

<span class="js-device-collection" ${dataFormAttr}>
    <a data-popup="#${id}" href="javascript:void(0);" data-device-collection>${text}</a>
</span>

<div id="${id}" class="dn js-device-collection-picker-dialog" data-dialog data-dialog-tabbed 
    data-width="700" data-height="675" data-title="${text}"
    data-class="js-device-collection-picker"
    data-load-event="yukon:device:selection:load" 
    data-event="yukon:device:selection:made"
    data-id-picker="${id}IdPicker">
    
    <ul>
        <li><a href="#${id}-device-picker">By Device</a></li>
        <li><a class="js-group-tab" href="#${id}-group">By Group</a></li>
        <li><a href="#${id}-address">By Address</a></li>
        <li><a href="#${id}-file">By File</a></li>
    </ul>
    
    <div id="${id}-device-picker" data-select-by="device">
        <input type="hidden" class="js-device-inputs" name="collectionType" value="idList">
        <input type="hidden" class="js-device-inputs" data-ids name="idList.ids">
        
        <div id="${id}-id-container"></div>
        <tags:pickerDialog id="${id}IdPicker" type="devicePicker" multiSelectMode="true" linkType="none"
             container="${id}-id-container"/>
    </div>
    
    <div id="${id}-group" class="inline-tree pr full-height" <c:if test="${multi}">data-multi</c:if>
        data-select-by="group" data-groups="${fn:escapeXml(groups)}">
        
        <input type="hidden" class="js-device-inputs" name="collectionType" value="groups" data-group-names>
        <div class="tree-controls clearfix">
            <cti:msg2 var="expand" key="yukon.common.expandAll"/>
            <cti:msg2 var="collapse" key="yukon.common.collapseAll"/>
            <cti:msg2 var="search" key="yukon.common.search.placeholder"/>
            <cti:msg2 var="tooltip" key="yukon.web.components.jstree.input.search.tooltip"/>
            <a href="javascript:void(0);" class="open-all fl" title="${expand}">${expand}</a>
            <a href="javascript:void(0);" class="close-all fl" title="${collapse}">${collapse}</a>
            <input type="text" class="tree-search fl" placeholder="${search}" title="${tooltip}">
        </div>
        <div class="clearfix tree-canvas oa" style="max-height: 400px"></div>
    </div>
    
    <div id="${id}-address" data-select-by="address">
        <input type="hidden" class="js-device-inputs" name="collectionType" value="addressRange">
        
        <cti:msg2 var="startOfRange" key=".startOfRangeLabel"/>
        <cti:msg2 var="endOfRange" key=".endOfRangeLabel"/>
        <tags:nameValueContainer>
            <tags:nameValue name="${startOfRange}">
                <input type="number" class="js-device-inputs" name="addressRange.start">
            </tags:nameValue>
            <tags:nameValue name="${endOfRange}">
                <input type="number" class="js-device-inputs" name="addressRange.end">
            </tags:nameValue>
        </tags:nameValueContainer>
        <ul class="error simple-list range-errors">
            <li class="dn js-undefined-start"><cti:msg2 key=".errNoStart"/></li>
            <li class="dn js-undefined-end"><cti:msg2 key=".errNoEnd"/></li>
            <li class="dn js-invalid-start"><cti:msg2 key=".errLessThanZero"/></li>
            <li class="dn js-invalid-end"><cti:msg2 key=".errBigEnd"/></li>
            <li class="dn js-invalid-range"><cti:msg2 key=".errOutOfRange"/></li>
        </ul>
    </div>
    
    <div id="${id}-file" data-select-by="file">
        <cti:csrfToken var="csrfToken"/>
        <div data-form data-csrf-token="${csrfToken}">
            <input type="hidden" name="collectionType" value="fileUpload">
            <input type="hidden" name="isFileUpload" value="true">
            
            <cti:msg2 var="typeLabel" key=".selectDataFileType"/>
            <cti:msg2 var="dataFileLabel" key=".selectDataFile"/>
            
            <tags:nameValueContainer tableClass="stacked natural-width">
                <tags:nameValue name="${typeLabel}">
                    <select name="fileUpload.uploadType">
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
                    <span class="file-upload">
                        <div class="button M0">
                            <cti:icon icon="icon-upload"/>
                            <span class="b-label">Upload</span>
                            <input type="file" name="fileUpload.dataFile">
                        </div>&nbsp;
                        <span class="file-name form-control">Choose File</span>
                    </span>
                    <div class="progress dib dn" style="width: 120px;">
                        <div class="progress-bar" style="width: 0%">
                        </div>
                    </div>
                </tags:nameValue>
                <tags:nameValue name="Devices" nameClass="js-upload-results dn" valueClass="js-upload-results dn">
                    <span class="device-count"></span>
                </tags:nameValue>
            </tags:nameValueContainer>
        </div>
        <div class="js-upload-results dn">
            <input type="hidden" class="js-device-inputs" name="group.name">
            <input type="hidden" class="js-device-inputs" name="collectionType" value="group">
        </div>
        <span class="error js-upload-errors"></span>
    </div>
    
</div>
</cti:msgScope>