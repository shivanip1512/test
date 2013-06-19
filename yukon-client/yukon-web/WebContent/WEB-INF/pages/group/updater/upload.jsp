<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage page="deviceGroupUpload" module="amr">

<cti:linkTabbedContainer mode="section">
    <cti:msg var="name_home" key="yukon.web.deviceGroups.editor.tab.title" />
    <c:url var="url_home" value="/group/editor/home" />
    <cti:linkTab selectorName="${name_home}" tabHref="${url_home}" />

    <cti:msg var="name_command" key="yukon.web.deviceGroups.commander.tab.title" />
    <c:url var="url_command" value="/group/commander/groupProcessing" />
    <cti:linkTab selectorName="${name_command}" tabHref="${url_command}" />

    <cti:msg var="name_gattread" key="yukon.common.device.groupMeterRead.home.tab.title"/>
    <c:url var="url_gattread" value="/group/groupMeterRead/homeGroup" />
    <cti:linkTab selectorName="${name_gattread}" tabHref="${url_gattread}" />

    <cti:msg var="name_upload" key="yukon.web.modules.amr.deviceGroupUpload.tab.title" />
    <c:url var="url_upload" value="/group/updater/upload" />
    <cti:linkTab selectorName="${name_upload}" tabHref="${url_upload}" initiallySelected="true" />
</cti:linkTabbedContainer>

<c:choose>
<c:when test="${not empty error}">
    <div class="error">${error}</div>
</c:when>
<c:when test="${success}">
    <div class="success"><i:inline key="success" arguments="${deviceCount}"/></div>
</c:when>
</c:choose>

<cti:msg2 key=".createGroups" var="createGroups"/>
<tags:simplePopup id="createGroupsOptionInfoPopup" title="${createGroups}" on="#help_icon" options="{width: 600}">
    <i:inline key=".infoPopup"/>
</tags:simplePopup>

<div class="column_10_14">
    <div class="column one">
        <tags:sectionContainer2 nameKey="options">
            <form id="uploadForm" method="post" action="/group/updater/parseUpload" enctype="multipart/form-data">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey="yukon.common.file">
                        <input type="file" name="dataFile" size="30px">
                    </tags:nameValue2>
                    <tags:nameValue2 excludeColon="true" nameKey="yukon.web.defaults.blank">
                        <label><input type="checkbox" name="createGroups" class="fl"><span class="fl"><i:inline key=".autoCreate"/></span><cti:icon icon="icon-help" id="help_icon"/></label>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
                <div class="actionArea">
                    <cti:button nameKey="upload" type="submit" classes="primary action"/>
                </div>
            </form>
            <%-- sample files --%>
            <div>
               <h4 class="dib"><cti:msg key="yukon.common.device.bulk.importUpload.sampleFilesLabel"/>:</h4>
               <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/Sample_DeviceGroup_Update_File1.csv"/>"><i:inline key=".file1"/></a>, 
               <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/Sample_DeviceGroup_Update_File2.csv"/>"><i:inline key=".file2"/></a>, 
               <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/Sample_DeviceGroup_Update_File3.csv"/>"><i:inline key=".file3"/></a>
           </div>
        </tags:sectionContainer2>
        
    </div>
    <div class="column two nogutter">
        <tags:sectionContainer2 nameKey="instructions">
            <ul class="detail disc">
                <cti:msg2 key=".rules"/>
            </ul>
       </tags:sectionContainer2>
    </div>
</div>

       <cti:url var="check" value="/WebConfig/yukon/Icons/check.gif"/>
       <table class="resultsTable">
           <thead>
               <tr>
                   <th style="width:20%;"><i:inline key=".colHeader"/></th>
                   <th><i:inline key=".description"/></th>
                   <th><i:inline key=".identifier"/></th>
               </tr>
           </thead>
           <tfoot></tfoot>
           <tbody>
               <tr>
                   <td class="fwb"><i:inline key=".address"/></td>
                   <td><i:inline key=".address.description"/></td>
                   <td class="tac"><cti:icon icon="icon-check" classes="fn"/></td>
               </tr>
               <tr>
                   <td class="fwb"><i:inline key=".meterNumber"/></td>
                   <td><i:inline key=".meterNumber.description"/></td>
                   <td style="text-align:center;"><cti:icon icon="icon-check" classes="fn"/></td>
               </tr>
               <tr>
                   <td class="fwb"><i:inline key=".name"/></td>
                   <td><i:inline key=".name.description"/></td>
                   <td style="text-align:center;"><cti:icon icon="icon-check" classes="fn"/></td>
               </tr>
               <tr>
                   <td class="fwb"><i:inline key=".deviceId"/></td>
                   <td><i:inline key=".deviceId.description"/></td>
                   <td style="text-align:center;"><cti:icon icon="icon-check" classes="fn"/></td>
               </tr>
               
               <tr>
                   <td class="fwb"><i:inline key=".deviceGroupPrefix"/></td>
                   <td><i:inline key=".deviceGroupPrefix.description"/></td>
                   <td></td>
               </tr>
               
               <tr>
                   <td class="fwb"><i:inline key=".deviceGroupSet"/></td>
                   <td><i:inline key=".deviceGroupSet.description"/></td>
                   <td></td>
               </tr>
           </tbody>
       </table>
</cti:standardPage>