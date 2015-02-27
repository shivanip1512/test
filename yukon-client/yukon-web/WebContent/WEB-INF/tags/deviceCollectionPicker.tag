<%@ tag dynamic-attributes="extraInputs" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags/jsTree" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="key" description="I18n object to use for the alert message. Note: key can be an i18n key String, 
                                      MessageSourceResolvable, Displayable, DisplayableEnum, or ResolvableTemplate." %>
<%@ attribute name="title" description="I18n object to use for the popup title. Note: title can be an i18n key String, 
                                      MessageSourceResolvable, Displayable, DisplayableEnum, or ResolvableTemplate.
                                      Default: 'yukon.common.device.selection.title' - 'Select By:'" %>
                                      
<%@ attribute name="event" description="Name of event to fire after successfully selecting devices." %>
<%@ attribute name="submit" type="java.lang.Boolean" 
              description="If 'true', the closest parent form is submitted after successfully selecting devices. 
                           Default: 'false'." %>

<%--Device group attributes --%>
<%@ attribute name="multi" type="java.lang.Boolean" 
              description="If true, selection mode for device group will be multiselection. Default: 'true'." %>
<%@ attribute name="predicates" 
              description="Optional comma separated strings of DeviceGroupPredicateEnum entry names to filter the 
                           tree data by. Default: 'NON_HIDDEN'." %>
<%@ attribute name="callbacks" type="java.util.Set" 
              description="Set of callbacks to be preformed on each node in the device group tree. Can be used to disable 
                           selecting certain nodes." %>
              
<cti:msgScope paths=", yukon.common.device.bulk.deviceSelection, yukon.web.defaults, yukon.common.device.selection">

<cti:default var="submit" value="${false}"/>
<cti:default var="multi" value="${true}"/>
<cti:default var="predicates" value="NON_HIDDEN"/>
<cti:default var="title" value=".select.title"/>

<cti:includeCss link="/resources/js/lib/dynatree/skin/ui.dynatree.css"/>
<cti:includeCss link="/resources/js/lib/dynatree/skin/device.group.css"/>
<cti:checkRolesAndProperties value="DEVELOPMENT_MODE">
    <cti:includeScript link="JQUERY_TREE"/>
</cti:checkRolesAndProperties>
<cti:checkRolesAndProperties value="!DEVELOPMENT_MODE">
    <cti:includeScript link="JQUERY_TREE_MIN"/>
</cti:checkRolesAndProperties>
<cti:includeScript link="JQUERY_TREE_HELPERS"/>
<cti:includeScript link="JQUERY_FILE_UPLOAD"/>
<!--[if lte IE 8]><cti:includeScript link="JQUERY_IFRAME_TRANSPORT" /><![endif]-->
<cti:includeScript link="/JavaScript/yukon.device.selection.js"/>

<cti:deviceGroupHierarchyJson var="groups" predicates="${predicates}" callbacks="${callbacks}"/>

<cti:uniqueIdentifier var="id" prefix="collection"/>

<a data-popup="#${id}" href="javascript:void(0);" data-device-collection
    <c:if test="${submit}"> data-submit</c:if>
    <c:if test="${not empty pageScope.event}"> data-event="${event}"</c:if>>
    <c:choose>
        <c:when test="${not empty pageScope.key}"><cti:msg2 key="${key}"/></c:when>
        <c:otherwise><jsp:doBody/></c:otherwise>
    </c:choose>
</a>

<div id="${id}" class="dn js-device-collection-picker-dialog" data-dialog data-dialog-tabbed 
    data-width="700" data-height="675" data-title="<cti:msg2 key="${title}"/>"
    data-class="js-device-collection-picker"
    data-load-event="yukon:device:selection:load" 
    data-event="yukon:device:selection:made"
    data-id-picker="${id}IdPicker">
    
    <ul>
        <li><a href="#${id}-device-picker"><i:inline key=".device"/></a></li>
        <li><a class="js-group-tab" href="#${id}-group"><i:inline key=".group"/></a></li>
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
        data-select-by="group" data-groups="${fn:escapeXml(groups)}">
        
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
            
            <tags:nameValueContainer2 tableClass="stacked natural-width">
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
            </tags:nameValueContainer2>
        </div>
        <div class="js-upload-results dn">
            <input type="hidden" class="js-device-collection-inputs" name="group.name">
            <input type="hidden" class="js-device-collection-inputs" name="collectionType" value="group">
        </div>
        <span class="error js-upload-errors"></span>
    </div>
    
</div>
</cti:msgScope>