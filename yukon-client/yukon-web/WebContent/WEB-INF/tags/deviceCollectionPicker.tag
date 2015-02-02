<%@ tag dynamic-attributes="extraInputs" trimDirectiveWhitespaces="true" %>

<%@ attribute name="text" description="Link text and dialog title. Defaults to 'Select Devices'" %>
<%@ attribute name="groupDataJson" description="A dictionary starting with attributes of the root node." %>
<%@ attribute name="submitOnCompletion" type="java.lang.Boolean" description="When true, will submit the closest parent form after successfully selecting devices" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.common.device.bulk.deviceSelection">
<cti:uniqueIdentifier var="id" prefix="collection"/>

<cti:msg2 var="defaultText" key=".selectDevices"/>
<c:set var="text" value="${(empty text) ? defaultText : text}" />

<c:set var="dataFormAttr" value="${submitOnCompletion ? 'data-submit-on-completion' : ''}" />

<span class="js-device-collection" ${dataFormAttr}>
    <a data-popup="#${id}" href="javascript:void(0);" data-device-collection>${text}</a>
</span>

<div id="${id}" class="dn" data-width="700" data-height="732" data-title="${text}"
    data-dialog data-load-event="yukon.device.selection.start" data-event="yukon.device.selection.end"
    data-id-picker="${id}IdPicker" >

    <cti:tabbedContentSelector mode="section">
        <cti:tabbedContentSelectorContent selectorName="By Device">
            <div data-select-by="device">
                <input type="hidden" class="js-device-inputs" name="collectionType" value="idList"/>
                <input type="hidden" class="js-device-inputs" data-ids name="idList.ids"/>
                <div id="${id}-id-container"></div>
                <tags:pickerDialog
                     id="${id}IdPicker"
                     type="devicePicker"
                     multiSelectMode="true"
                     linkType="none"
                     container="${id}-id-container"/>
            </div>
        </cti:tabbedContentSelectorContent>
        <cti:tabbedContentSelectorContent selectorName="By Group">
            <div data-select-by="group">
                <input type="hidden" class="js-device-inputs" name="collectionType" value="group"/>
                <input type="hidden" class="js-device-inputs" name="group.name" data-group-name/>
                <jsTree:inlineTree id="${id}Group"
                    treeCss="/resources/js/lib/dynatree/skin/device.group.css"
                    treeParameters="{onActivate:yukon.device.selection.selectGroup}"
                    dataJson="${groupDataJson}"/>
            </div>
        </cti:tabbedContentSelectorContent>
        <cti:tabbedContentSelectorContent selectorName="By Address">
            <div data-select-by="address">
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
        </cti:tabbedContentSelectorContent>
        <cti:tabbedContentSelectorContent selectorName="By File">
            <div data-select-by="file">
                <cti:csrfToken var="csrfToken"/>
                <div data-form data-csrf-token="${csrfToken}">
                    <input type="hidden" name="collectionType" value="fileUpload"/>
                    <input type="hidden" name="isFileUpload" value="true"/>

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
                    <input type="hidden" class="js-device-inputs" name="group.name"/>
                    <input type="hidden" class="js-device-inputs" name="collectionType" value="group"/>
                </div>
                <span class="error js-upload-errors"></span>
            </div>
        </cti:tabbedContentSelectorContent>
    </cti:tabbedContentSelector>
</div>
</cti:msgScope>