<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="amr" page="fdrTranslationManagement">
    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        <%-- bulk home --%>
        <cti:msg var="bulkOperationsPageTitle" key="yukon.common.device.bulk.bulkHome.pageTitle"/>
        <cti:crumbLink url="/spring/bulk/bulkHome" title="${bulkOperationsPageTitle}" />
        <%-- fdr translations --%>
        <cti:crumbLink><i:inline key="yukon.web.modules.amr.fdrTranslationManagement.pageName"/></cti:crumbLink>
    </cti:breadCrumbs>
    
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
                        var url = '/spring/bulk/fdrTranslationManager/importResults?';
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
        var filePath = "/WebConfig/custom/sample_bulk_files/fdr_translations/" + itemSelected + "_sample.csv"
        jQuery("#sampleFileLink").attr("href", filePath);
    }
    </script>
    
    <tags:boxContainer2 nameKey="mainBox">
        <div>
            <div style="width: 50%; float: left;">
                <div class="bottomPadded">
                    <h3><i:inline key="yukon.web.modules.amr.fdrTranslationManagement.addRemoveTitle"/></h3>
                    <i:inline key="yukon.web.modules.amr.fdrTranslationManagement.addRemoveText"/>
                </div>
                <div class="bottomPadded">
                    <%-- This form submitted through ajax --%>
                    <form id="importForm" method="post" action="/spring/bulk/fdrTranslationManager/submitImport" enctype="multipart/form-data">
                        <cti:url var="folderImg" value="/WebConfig/yukon/Icons/folder_edit.gif"/>
                        <img src="${folderImg}">&nbsp;<input type="file" name="importFile"><br>
                        <input type="checkbox" name="ignoreInvalidColumns">&nbsp;<i:inline key="yukon.web.modules.amr.fdrTranslationManagement.ignoreInvalidText"/>
                        <br>
                        <cti:button type="submit" nameKey="importSubmitButton"/>
                    </form>
                </div>
                <div class="bottomPadded">
                    <h3><i:inline key="yukon.web.modules.amr.fdrTranslationManagement.reportTitle"/></h3>
                    <i:inline key="yukon.web.modules.amr.fdrTranslationManagement.reportText"/>
                </div>
                <form id="downloadReportForm" method="post" action="/spring/bulk/fdrTranslationManager/report">
                    <i:inline key="yukon.web.modules.amr.fdrTranslationManagement.interfaceFilterLabel"/>
                    <select name="reportInterface">
                        <option value="AllInterfaces"><i:inline key="yukon.web.modules.amr.fdrTranslationManagement.allInterfaces"/></option>
                        <c:forEach var="interface" items="${interfaceTypes}">
                            <option value="${interface}"><i:inline key="${interface}"/></option>
                        </c:forEach>
                    </select>
                    <cti:button type="submit" nameKey="downloadTranslationsButton"/>
                </form>
            </div>
            <div style="width: 50%; float: left;">
                <div class="bottomPadded">
                    <h3><i:inline key="yukon.web.modules.amr.fdrTranslationManagement.importTitle"/></h3>
                    <i:inline key="yukon.web.modules.amr.fdrTranslationManagement.importText"/>
                </div>
                <i:inline key="yukon.web.modules.amr.fdrTranslationManagement.defaultColumnsText"/><br>
                
                <div class="bottomPadded">
                    <table class="miniResultsTable" style="font-size:11px;">
                        <tr>
                            <th><i:inline key="yukon.web.modules.amr.fdrTranslationManagement.defaultColumnsHeader"/></th>
                            <th><i:inline key="yukon.web.modules.amr.fdrTranslationManagement.defaultColumnsDescriptionHeader"/></th>
                        </tr>
                        <tr>
                            <td class="smallBoldLabel">
                                <i:inline key="yukon.web.modules.amr.fdrTranslationManagement.defaultColumns.action"/>
                            </td>
                            <td>
                                <i:inline key="yukon.web.modules.amr.fdrTranslationManagement.defaultColumns.actionDescription"/>
                            </td>
                        </tr>
                        <tr>
                            <td class="smallBoldLabel">
                                <i:inline key="yukon.web.modules.amr.fdrTranslationManagement.defaultColumns.deviceName"/>
                            </td>
                            <td>
                                <i:inline key="yukon.web.modules.amr.fdrTranslationManagement.defaultColumns.deviceNameDescription"/>
                            </td>
                        </tr>
                        <tr>
                            <td class="smallBoldLabel">
                                <i:inline key="yukon.web.modules.amr.fdrTranslationManagement.defaultColumns.deviceType"/>
                            </td>
                            <td>
                                <i:inline key="yukon.web.modules.amr.fdrTranslationManagement.defaultColumns.deviceTypeDescription"/>
                            </td>
                        </tr>
                        <tr>
                            <td class="smallBoldLabel">
                                <i:inline key="yukon.web.modules.amr.fdrTranslationManagement.defaultColumns.pointName"/>
                            </td>
                            <td>
                                <i:inline key="yukon.web.modules.amr.fdrTranslationManagement.defaultColumns.pointNameDescription"/>
                            </td>
                        </tr>
                        <c:forEach var="interface" items="${interfaceTypes}">
                            <tr class="interface_${interface}" style="display:none;">
                                <td class="smallBoldLabel">
                                    <i:inline key="yukon.web.modules.amr.fdrTranslationManagement.defaultColumns.direction"/>
                                </td>
                                <td>
                                    <c:forEach var="direction" varStatus="status" items="${interface.supportedDirections}">"${direction.value}"<c:if test="${!status.last}">,&nbsp;</c:if></c:forEach>
                                </td>
                            </tr>
                        </c:forEach>   
                    </table>
                </div>
                <div class="bottomPadded">
                    <i:inline key="yukon.web.modules.amr.fdrTranslationManagement.interfaceColumnsText"/>
                </div>
                <i:inline key="yukon.web.modules.amr.fdrTranslationManagement.interfaceColumnsLabel"/>
                <select id="interfaceColumnSelector">
                    <c:forEach var="interface" items="${interfaceTypes}">
                        <option value="${interface}"><i:inline key="${interface}"/></option>
                    </c:forEach>
                </select>
                <div class="bottomPadded">
                <table class="miniResultsTable" style="font-size:11px;">
                    <c:forEach var="displayableInterface" items="${displayableInterfaces}">
                        <tr class="interface_${displayableInterface.name}" style="display: none;">
                            <th>
                                ${displayableInterface.name}&nbsp;<i:inline key="yukon.web.modules.amr.fdrTranslationManagement.interfaceColumnsHeader"/>
                            </th>
                            <th>
                                <i:inline key="yukon.web.modules.amr.fdrTranslationManagement.defaultColumnsDescriptionHeader"/>
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
                    <i:inline key="yukon.web.modules.amr.fdrTranslationManagement.sampleFileLink"/>
                </a>
            </div>
        </div>
    </tags:boxContainer2>
</cti:standardPage>