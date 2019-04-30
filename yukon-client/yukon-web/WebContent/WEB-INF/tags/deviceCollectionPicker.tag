<%@ tag dynamic-attributes="extraInputs" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags/jsTree" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="type" 
              description="The type of trigger component. Values: 'link', 'button', 'button-selection'.
                           Default: 'link'." %>
<%@ attribute name="classes" 
              description="CSS class names applied to the trigger component." %>

<%@ attribute name="key" 
              description="I18n object to use for the alert message. Note: key can be an i18n key String, 
                           MessageSourceResolvable, Displayable, DisplayableEnum, or ResolvableTemplate." %>
                           
<%@ attribute name="title" 
              description="I18n object to use for the popup title. Note: title can be an i18n key String, 
                           MessageSourceResolvable, Displayable, DisplayableEnum, or ResolvableTemplate.
                           Default: 'yukon.common.device.selection.title' - 'Select By:'" %>
                           
<%@ attribute name="event" 
              description="Name of event to fire after successfully selecting devices." %>
              
<%@ attribute name="submit" 
              type="java.lang.Boolean" 
              description="If 'true', the closest parent form is submitted after successfully selecting devices. 
                           Default: 'false'." %>

<%--Device group attributes --%>
<%@ attribute name="multi"
              type="java.lang.Boolean" 
              description="If true, selection mode for device group will be multiselection. Default: 'true'." %>
              
<%@ attribute name="predicates" 
              description="Optional comma separated strings of DeviceGroupPredicateEnum entry names to filter the 
                           tree data by. Default: 'NON_HIDDEN'." %>
                           
<%@ attribute name="callbacks"
              type="java.util.Set" 
              description="Set of callbacks to be preformed on each node in the device group tree. Can be used to disable 
                           selecting certain nodes." %>
                           
<%@ attribute name="groupDataUrl" type="java.lang.String" description="A URL indicating how to get the data for the groups tree."%>
    
              
<cti:msgScope paths=", yukon.common.device.bulk.deviceSelection, yukon.common, yukon.common.device.selection">

<cti:default var="type" value="link"/>
<cti:default var="classes" value=""/>
<cti:default var="submit" value="${false}"/>
<cti:default var="multi" value="${true}"/>
<cti:default var="predicates" value="NON_HIDDEN"/>
<cti:default var="title" value=".select.title"/>

<cti:includeCss link="/resources/js/lib/dynatree/skin/ui.dynatree.css"/>
<cti:includeCss link="/resources/js/lib/dynatree/skin/device.group.css"/>

<cti:includeScript link="JQUERY_TREE"/>
<cti:includeScript link="JQUERY_TREE_HELPERS"/>
<cti:includeScript link="JQUERY_FILE_UPLOAD"/>
<!--[if lte IE 8]><cti:includeScript link="JQUERY_IFRAME_TRANSPORT" /><![endif]-->
<cti:includeScript link="/resources/js/pages/yukon.device.selection.js"/>

<c:if test="${empty groupDataUrl}">
    <cti:deviceGroupHierarchyJson var="groups" predicates="${predicates}" callbacks="${callbacks}"/>
</c:if>
<cti:uniqueIdentifier var="id" prefix="collection"/>

<cti:msgScope paths=",yukon.web.components">
<div class="collection-picker ${classes}" data-popup="#${id}" data-type="${type}"
    <c:if test="${submit}">data-submit</c:if>
    <c:if test="${not empty pageScope.event}">data-event="${event}"</c:if>>
    <c:choose>
        <c:when test="${type == 'link'}">
            <a href="javascript:void(0);">
                <c:choose>
                    <c:when test="${not empty pageScope.key}"><cti:msg2 key="${key}"/></c:when>
                    <c:otherwise><jsp:doBody/></c:otherwise>
                </c:choose>
            </a>
        </c:when>
        <c:when test="${type == 'button' or type == 'button-selection'}">
            <button class="button">
                <cti:icon icon="icon-folder-edit"/>
                <span class="b-label">
                    <c:choose>
                        <c:when test="${not empty pageScope.key}"><cti:msg2 key="${key}"/></c:when>
                        <c:otherwise><jsp:doBody/></c:otherwise>
                    </c:choose>
                </span>
            </button>
            <c:if test="${type == 'button-selection'}">
                <cti:msg2 var="devicesText" key="yukon.common.device.selection.device.selected"/>
                <cti:msg2 var="groupText" key="yukon.common.device.selection.group.selected"/>
                <cti:msg2 var="groupsText" key="yukon.common.device.selection.groups.selected"/>
                <cti:msg2 var="addressText" key="yukon.common.device.selection.address.selected"/>
                <cti:msg2 var="fileText" key="yukon.common.device.selection.file.selected"/>
                <span class="collection-picker-count badge">0</span>
                <span class="collection-picker-label"
                    data-devices="${devicesText}"
                    data-group="${groupText}"
                    data-groups="${groupsText}"
                    data-address="${addressText}"
                    data-file="${fileText}">${devicesText}</span>
            </c:if>
        </c:when>
    </c:choose>
</div>
</cti:msgScope>

<div data-dialog data-dialog-tabbed id="${id}" class="dn js-device-collection-picker-dialog" 
    data-width="700" data-height="675" data-title="<cti:msg2 key="${title}"/>"
    data-class="js-device-collection-picker"
    data-load-event="yukon:device:selection:load" 
    data-event="yukon:device:selection:made"
    data-id-picker="${id}IdPicker">
    
    <ul>
        <li><a href="#${id}-device-picker"><i:inline key=".device"/></a></li>
        <li><a href="#${id}-group" class="js-group-tab"><i:inline key=".group"/></a></li>
        <li><a href="#${id}-address"><i:inline key=".address"/></a></li>
        <li><a href="#${id}-file"><i:inline key=".file"/></a></li>
    </ul>
    
    <div id="${id}-device-picker" data-select-by="device">
        <input type="hidden" class="js-device-collection-inputs" name="collectionType" value="idList">
        <input type="hidden" class="js-device-collection-inputs" data-ids name="idList.ids">
        
        <div id="${id}-id-container"></div>
        <tags:pickerDialog id="${id}IdPicker" type="devicePicker" multiSelectMode="true" linkType="none"
             container="${id}-id-container"/>
    </div>
    
    <div id="${id}-group" class="inline-tree pr full-height" <c:if test="${multi}">data-multi</c:if>
        data-select-by="group" data-groups="${fn:escapeXml(groups)}" data-groups-url="${groupDataUrl}">
        
        <input type="hidden" class="js-device-collection-inputs" name="collectionType" value="groups" data-group-names>
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
        <input type="hidden" class="js-device-collection-inputs" name="collectionType" value="addressRange">
        
        <cti:msg2 var="startOfRange" key=".startOfRangeLabel"/>
        <cti:msg2 var="endOfRange" key=".endOfRangeLabel"/>
        <tags:nameValueContainer>
            <tags:nameValue name="${startOfRange}">
                <input type="number" class="js-device-collection-inputs" name="addressRange.start">
            </tags:nameValue>
            <tags:nameValue name="${endOfRange}">
                <input type="number" class="js-device-collection-inputs" name="addressRange.end">
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
            
            <tags:nameValueContainer2 tableClass="with-form-controls stacked natural-width">
                <tags:nameValue2 nameKey=".selectDataFileType">
                    <select name="fileUpload.uploadType">
                        <option value="ADDRESS"><cti:msg2 key=".dataFileAddress"/></option>
                        <option value="PAONAME"><cti:msg2 key=".dataFileName"/></option>
                        <option value="METERNUMBER"><cti:msg2 key=".dataFileMeterNumber"/></option>
                        <option value="DEVICEID"><cti:msg2 key=".dataFileDeviceId"/></option>
                        <option value="BULK"><cti:msg2 key=".dataFileBulk"/></option>
                    </select>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".selectDataFile">
                    <tags:file name="fileUpload.dataFile"/>
                    <div class="progress dib dn" style="width: 120px;">
                        <div class="progress-bar" style="width: 0%"></div>
                    </div>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".devices" nameClass="js-upload-results dn" valueClass="js-upload-results dn">
                    <span class="device-count"></span>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".errorCount" nameClass="js-upload-results dn" valueClass="js-upload-results dn">
                    <span class="js-error-count"></span>
                    <cti:button id="download" type="submit" classes="cp fn pull-icon-down js-error-devices-download" 
                            icon="icon-page-excel" renderMode="image" disabled="true" />
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </div>
        <div class="js-upload-results dn">
            <input type="hidden" class="js-device-collection-inputs" name="group.name">
            <input type="hidden" class="js-device-collection-inputs" name="collectionType" value="group">
            <input type="hidden" class="js-device-collection-inputs js-error-devices-input" name="deviceErrors">
            <input type="hidden" class="js-device-collection-inputs js-error-devices-input" name="uploadFileName">
        </div>
        <span class="error js-upload-errors"></span>
    </div>
    
</div>
</cti:msgScope>