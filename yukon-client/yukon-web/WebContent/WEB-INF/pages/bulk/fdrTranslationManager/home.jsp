<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="tools" page="bulk.fdrTranslationManagement">
    
    <script>
    $(document).ready(function(){
        updateInterfaceSelection();
        
        $('#interfaceColumnSelector').change(function(){
            updateInterfaceSelection();
        });
        
        $('#importForm').submit(function() {
            $(this).ajaxSubmit({
                dataType:   'json',
                success:    function(data) {
                    if(data.error) {
                        yukon.ui.alertError(data.error);
                    } else {
                        var url = yukon.url('/bulk/fdrTranslationManager/importResults?');
                        url += 'resultId=' + data.resultId;
                        url += '&fileName=' + data.fileName;
                        url += '&ignoreInvalidColumns=' + data.ignoreInvalidColumns;
                        window.location = url;
                    }
                }
            });
            return false;
        });
    });
    
    function updateInterfaceSelection() {
        var itemSelected = $('#interfaceColumnSelector').val();
        //hide all rows
        $("[class^='interface_']").hide();
        //show rows for the selected interface
        $('.interface_' + itemSelected).show();
        //update the sample files link
        var filePath = yukon.url('/WebConfig/custom/sample_bulk_files/fdr_translations/') + itemSelected + '_sample.csv';
        $('#sampleFileLink').attr('href', filePath);
    }
    </script>
    
    <div class="column-12-12">
        <div class="column one">
            <div class="stacked clearfix">
                <h3><i:inline key=".addRemoveTitle"/></h3>
                <i:inline key=".addRemoveText"/>
            </div>
            <div class="stacked clearfix">
                <%-- This form submitted through ajax --%>
                <cti:url var="submitImportUrl" value="/bulk/fdrTranslationManager/submitImport"/>
                <form id="importForm" method="post" action="${submitImportUrl}" enctype="multipart/form-data">
                    <cti:csrfToken/>
                    <cti:msg2 key="yukon.common.file" var="fileName"/>
                    <tags:nameValueContainer>
                        <tags:nameValue name="${fileName}">
                            <input type="file" name="importFile">
                        </tags:nameValue>
                        <tags:nameValue name="">
                            <label><input type="checkbox" name="ignoreInvalidColumns">&nbsp;<i:inline key=".ignoreInvalidText"/></label>
                        </tags:nameValue>
                    </tags:nameValueContainer>
                    <div class="action-area">
                        <cti:button type="submit" nameKey="import" classes="primary action"/>
                    </div>
                </form>
            </div>
            <div class="stacked clearfix">
                <h3><i:inline key=".reportTitle"/></h3>
                <i:inline key=".reportText"/>
            </div>
            <cti:url var="reportUrl" value="/bulk/fdrTranslationManager/report"/>
            <form id="downloadReportForm" method="post" action="${reportUrl}" class="clearfix">
                <cti:csrfToken/>
                <div class="action-area">
                  <cti:button type="submit" nameKey="download" icon="icon-download"/>
                  <label>
                    <select name="reportInterface">
                        <option value="AllInterfaces"><i:inline key=".allInterfaces"/></option>
                        <c:forEach var="interfaceType" items="${interfaceTypes}">
                            <option value="${interfaceType}"><i:inline key="${interfaceType}"/></option>
                        </c:forEach>
                    </select>
                    <span class="fr" style="margin-right:10px;"><i:inline key=".interfaceFilterLabel"/></span>
                  </label>
                </div>
            </form>
        </div>
        <div class="column two nogutter">
            <div class="stacked">
                <h3><i:inline key=".importTitle"/></h3>
                <i:inline key=".importText"/>
            </div>
            <i:inline key=".defaultColumnsText"/><br>
            
            <table class="results-table detail stacked">
                <thead>
                    <tr>
                        <th><i:inline key=".defaultColumnsHeader"/></th>
                        <th><i:inline key=".defaultColumnsDescriptionHeader"/></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <tr>
                        <td class="strong-label-small">
                            ACTION
                        </td>
                        <td>
                            ADD or REMOVE
                        </td>
                    </tr>
                    <tr>
                        <td class="strong-label-small">
                            DEVICE_NAME
                        </td>
                        <td>
                            <i:inline key=".defaultColumns.deviceNameDescription"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="strong-label-small">
                            DEVICE_TYPE
                        </td>
                        <td>
                            <i:inline key=".defaultColumns.deviceTypeDescription"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="strong-label-small">
                            POINT_NAME
                        </td>
                        <td>
                            <i:inline key=".defaultColumns.pointNameDescription"/>
                        </td>
                    </tr>
                    <c:forEach var="interfaceType" items="${interfaceTypes}">
                        <tr class="interface_${interfaceType}" style="display:none;">
                            <td class="strong-label-small">
                                DIRECTION
                            </td>
                            <td>
                                <c:forEach var="direction" varStatus="status" items="${interfaceType.supportedDirections}">"${direction.value}"<c:if test="${!status.last}">,&nbsp;</c:if></c:forEach>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <div class="stacked">
                <i:inline key=".interfaceColumnsText"/>
            </div>
            <div class="with-form-controls stacked">
                <i:inline key=".interfaceColumnsLabel"/>
                <select id="interfaceColumnSelector">
                    <c:forEach var="interfaceType" items="${interfaceTypes}">
                        <option value="${interfaceType}"><i:inline key="${interfaceType}"/></option>
                    </c:forEach>
                </select>
            </div>
            <div class="stacked">
            <table class="results-table detail">
                <c:forEach var="displayableInterface" items="${displayableInterfaces}">
                    <thead>
                        <tr class="interface_${displayableInterface.name}" style="display: none;">
                            <th>
                                ${displayableInterface.name}&nbsp;<i:inline key=".interfaceColumnsHeader"/>
                            </th>
                            <th>
                                <i:inline key=".defaultColumnsDescriptionHeader"/>
                            </th>
                        </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:forEach var="colEntry" items="${displayableInterface.columnsAndDescriptions}">
                            <tr class="interface_${displayableInterface.name}" style="display:none;">
                                <td class="strong-label-small">
                                    ${colEntry.key}
                                </td>
                                <td>
                                    ${colEntry.value}
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </c:forEach>
            </table>
            </div>

            <a id="sampleFileLink" href="">
                <i:inline key=".sampleFileLink"/>
            </a>
        </div>
    </div>
</cti:standardPage>