<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:url var="parseUploadURL" value="/group/updater/parseUpload"/>

<cti:standardPage module="tools" page="deviceGroupUpload">

<cti:linkTabbedContainer mode="section">
    <cti:linkTab selectorKey="yukon.web.deviceGroups.editor.tab.title">
        <c:url value="/group/editor/home" />
    </cti:linkTab>

    <cti:linkTab selectorKey="yukon.web.deviceGroups.commander.tab.title">
        <c:url value="/group/commander/groupProcessing" />
    </cti:linkTab>

    <cti:linkTab selectorKey="yukon.common.device.groupMeterRead.home.tab.title">
        <c:url value="/group/groupMeterRead/homeGroup" />
    </cti:linkTab>

    <cti:linkTab selectorKey="yukon.web.modules.amr.deviceGroupUpload.tab.title" initiallySelected="true">
        <c:url value="/group/updater/upload" />
    </cti:linkTab>
</cti:linkTabbedContainer>

<c:choose>
<c:when test="${not empty error}">
    <div class="error">${error}</div>
</c:when>
<c:when test="${success}">
    <div class="success"><i:inline key=".success" arguments="${deviceCount}"/></div>
</c:when>
</c:choose>

<cti:msg2 key=".createGroups" var="createGroups"/>
<tags:simplePopup id="createGroupsOptionInfoPopup" title="${createGroups}" on="#help_icon" options="{width: 600}">
    <i:inline key=".infoPopup"/>
</tags:simplePopup>

<div class="column-10-14">
    <div class="column one">
        <tags:sectionContainer2 nameKey="options">
            <form id="uploadForm" method="post" action="${parseUploadURL}" enctype="multipart/form-data">
                <cti:csrfToken/>
                <tags:nameValueContainer2 tableClass="name-collapse">
                    <tags:nameValue2 nameKey="yukon.common.file"><tags:file/></tags:nameValue2>
                    <tags:nameValue2 excludeColon="true" nameKey="yukon.web.defaults.blank">
                        <label><input type="checkbox" name="createGroups" class="fl"><span class="fl"><i:inline key=".autoCreate"/></span></label><cti:icon icon="icon-help" id="help_icon" classes="cp"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
                <div class="action-area">
                    <cti:button nameKey="upload" type="submit" classes="primary action"/>
                </div>
            </form>
            <%-- sample files --%>
            <div>
               <h4 class="dib"><cti:msg key="yukon.common.device.bulk.importUpload.sampleFilesLabel"/>:</h4>
               <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/Sample_DeviceGroup_Update_File1.csv"/>"><i:inline key="yukon.common.file1"/></a>, 
               <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/Sample_DeviceGroup_Update_File2.csv"/>"><i:inline key="yukon.common.file2"/></a>, 
               <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/Sample_DeviceGroup_Update_File3.csv"/>"><i:inline key="yukon.common.file3"/></a>
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

       <table class="results-table">
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
                   <td class="fwb">ADDRESS</td>
                   <td><i:inline key=".address.description"/></td>
                   <td class="tac"><cti:icon icon="icon-tick" classes="fn"/></td>
               </tr>
               <tr>
                   <td class="fwb">METER_NUMBER</td>
                   <td><i:inline key=".meterNumber.description"/></td>
                   <td style="text-align:center;"><cti:icon icon="icon-tick" classes="fn"/></td>
               </tr>
               <tr>
                   <td class="fwb">NAME</td>
                   <td><i:inline key=".name.description"/></td>
                   <td style="text-align:center;"><cti:icon icon="icon-tick" classes="fn"/></td>
               </tr>
               <tr>
                   <td class="fwb">DEVICE_ID</td>
                   <td><i:inline key=".deviceId.description"/></td>
                   <td style="text-align:center;"><cti:icon icon="icon-tick" classes="fn"/></td>
               </tr>
               
               <tr>
                   <td class="fwb">DEVICE_GROUP_PREFIX</td>
                   <td><i:inline key=".deviceGroupPrefix.description"/></td>
                   <td></td>
               </tr>
               
               <tr>
                   <td class="fwb">DEVICE_GROUP_SET</td>
                   <td><i:inline key=".deviceGroupSet.description"/></td>
                   <td></td>
               </tr>
           </tbody>
       </table>
</cti:standardPage>