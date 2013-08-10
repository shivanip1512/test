<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="tools" page="bulk.fdrTranslationManagement">
    
    <script>
    jQuery(document).ready(function(){
        updateInterfaceSelection();
        
        jQuery("#interfaceColumnSelector").change(function(){
            updateInterfaceSelection();
        });
        
        jQuery('#importForm').submit(function() {
            jQuery(this).ajaxSubmit({
                dataType:   'json',
                success:    function(data) {
                    if(data.error) {
                        Yukon.ui.flashError(data.error);
                    } else {
                        var url = '/bulk/fdrTranslationManager/importResults?';
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
        var itemSelected = jQuery("#interfaceColumnSelector").val();
        //hide all rows
        jQuery("[class^='interface_']").hide();
        //show rows for the selected interface
        jQuery(".interface_" + itemSelected).show();
        //update the sample files link
        var filePath = "/WebConfig/custom/sample_bulk_files/fdr_translations/" + itemSelected + "_sample.csv";
        jQuery("#sampleFileLink").attr("href", filePath);
    }
    </script>
    
    <div class="column_12_12">
        <div class="column one">
            <div class="stacked clearfix">
                <h3><i:inline key="yukon.web.modules.tools.bulk.fdrTranslationManagement.addRemoveTitle"/></h3>
                <i:inline key="yukon.web.modules.tools.bulk.fdrTranslationManagement.addRemoveText"/>
            </div>
            <div class="stacked clearfix">
                <%-- This form submitted through ajax --%>
                <form id="importForm" method="post" action="/bulk/fdrTranslationManager/submitImport" enctype="multipart/form-data">
                    <cti:icon icon="icon-folder-edit"/>&nbsp;<input type="file" name="importFile"><br>
                    <input type="checkbox" name="ignoreInvalidColumns">&nbsp;<i:inline key="yukon.web.modules.tools.bulk.fdrTranslationManagement.ignoreInvalidText"/>
                    <div class="actionArea">
                        <cti:button type="submit" nameKey="import" classes="primary action"/>
                    </div>
                </form>
            </div>
            <div class="stacked clearfix">
                <h3><i:inline key="yukon.web.modules.tools.bulk.fdrTranslationManagement.reportTitle"/></h3>
                <i:inline key="yukon.web.modules.tools.bulk.fdrTranslationManagement.reportText"/>
            </div>
            <form id="downloadReportForm" method="post" action="/bulk/fdrTranslationManager/report" class="clearfix">
                <div class="actionArea">
                  <cti:button type="submit" nameKey="download" icon="icon-bullet-go-down"/>
                  <label>
                    <select name="reportInterface">
                        <option value="AllInterfaces"><i:inline key="yukon.web.modules.tools.bulk.fdrTranslationManagement.allInterfaces"/></option>
                        <c:forEach var="interfaceType" items="${interfaceTypes}">
                            <option value="${interfaceType}"><i:inline key="${interfaceType}"/></option>
                        </c:forEach>
                    </select>
                    <span class="fr" style="margin-right:10px;"><i:inline key="yukon.web.modules.tools.bulk.fdrTranslationManagement.interfaceFilterLabel"/></span>
                  </label>
                </div>
            </form>
        </div>
        <div class="column two nogutter">
            <div class="stacked">
                <h3><i:inline key="yukon.web.modules.tools.bulk.fdrTranslationManagement.importTitle"/></h3>
                <i:inline key="yukon.web.modules.tools.bulk.fdrTranslationManagement.importText"/>
            </div>
            <i:inline key="yukon.web.modules.tools.bulk.fdrTranslationManagement.defaultColumnsText"/><br>
            
            <div class="stacked">
                <table class="resultsTable detail">
                    <tr>
                        <th><i:inline key="yukon.web.modules.tools.bulk.fdrTranslationManagement.defaultColumnsHeader"/></th>
                        <th><i:inline key="yukon.web.modules.tools.bulk.fdrTranslationManagement.defaultColumnsDescriptionHeader"/></th>
                    </tr>
                    <tr>
                        <td class="smallBoldLabel">
                            ACTION
                        </td>
                        <td>
                            ADD or REMOVE
                        </td>
                    </tr>
                    <tr>
                        <td class="smallBoldLabel">
                            DEVICE_NAME
                        </td>
                        <td>
                            <i:inline key="yukon.web.modules.tools.bulk.fdrTranslationManagement.defaultColumns.deviceNameDescription"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="smallBoldLabel">
                            DEVICE_TYPE
                        </td>
                        <td>
                            <i:inline key="yukon.web.modules.tools.bulk.fdrTranslationManagement.defaultColumns.deviceTypeDescription"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="smallBoldLabel">
                            POINT_NAME
                        </td>
                        <td>
                            <i:inline key="yukon.web.modules.tools.bulk.fdrTranslationManagement.defaultColumns.pointNameDescription"/>
                        </td>
                    </tr>
                    <c:forEach var="interfaceType" items="${interfaceTypes}">
                        <tr class="interface_${interfaceType}" style="display:none;">
                            <td class="smallBoldLabel">
                                DIRECTION
                            </td>
                            <td>
                                <c:forEach var="direction" varStatus="status" items="${interfaceType.supportedDirections}">"${direction.value}"<c:if test="${!status.last}">,&nbsp;</c:if></c:forEach>
                            </td>
                        </tr>
                    </c:forEach>   
                </table>
            </div>
            <div class="stacked">
                <i:inline key="yukon.web.modules.tools.bulk.fdrTranslationManagement.interfaceColumnsText"/>
            </div>
            <i:inline key="yukon.web.modules.tools.bulk.fdrTranslationManagement.interfaceColumnsLabel"/>
            <select id="interfaceColumnSelector">
                <c:forEach var="interfaceType" items="${interfaceTypes}">
                    <option value="${interfaceType}"><i:inline key="${interfaceType}"/></option>
                </c:forEach>
            </select>
            <div class="stacked">
            <table class="resultsTable detail">
                <c:forEach var="displayableInterface" items="${displayableInterfaces}">
                    <tr class="interface_${displayableInterface.name}" style="display: none;">
                        <th>
                            ${displayableInterface.name}&nbsp;<i:inline key="yukon.web.modules.tools.bulk.fdrTranslationManagement.interfaceColumnsHeader"/>
                        </th>
                        <th>
                            <i:inline key="yukon.web.modules.tools.bulk.fdrTranslationManagement.defaultColumnsDescriptionHeader"/>
                        </th>
                    </tr>
                    <c:forEach var="colEntry" items="${displayableInterface.columnsAndDescriptions}">
                        <tr class="interface_${displayableInterface.name}" style="display:none;">
                            <td class="smallBoldLabel">
                                ${colEntry.key}
                            </td>
                            <td>
                                ${colEntry.value}
                            </td>
                        </tr>
                    </c:forEach>
                </c:forEach>
            </table>
            </div>

            <a id="sampleFileLink" href="">
                <i:inline key="yukon.web.modules.tools.bulk.fdrTranslationManagement.sampleFileLink"/>
            </a>
        </div>
    </div>
</cti:standardPage>