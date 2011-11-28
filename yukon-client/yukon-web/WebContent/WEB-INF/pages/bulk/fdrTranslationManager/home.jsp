<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
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
        var itemSelected = jQuery("#interfaceColumnSelector").val();
        //and show rows
        jQuery(".interface_" + itemSelected).show();
        
        jQuery("#interfaceColumnSelector").change(function(){
            itemSelected = jQuery("#interfaceColumnSelector").val();
            
            //hide rows
            jQuery("[class^='interface_']").hide();
            //and show rows
            jQuery(".interface_" + itemSelected).show();
        });
    });
    </script>
    
    <tags:boxContainer2 nameKey="mainBox">
        <div>
            <div style="width: 50%; float: left;">
                <h3><i:inline key="yukon.web.modules.amr.fdrTranslationManagement.addRemoveTitle"/></h3>
                <i:inline key="yukon.web.modules.amr.fdrTranslationManagement.addRemoveText"/>
                <br><br>
                <form id="importForm" method="post" action="/spring/bulk/fdrTranslationManager/submitImport" enctype="multipart/form-data">
                    <cti:url var="folderImg" value="/WebConfig/yukon/Icons/folder_edit.gif"/>
                    <img src="${folderImg}">&nbsp;<input type="file" name="importFile"><br>
                    <input type="checkbox" name="ignoreInvalidColumns">&nbsp;<i:inline key="yukon.web.modules.amr.fdrTranslationManagement.ignoreInvalidText"/>
                    <br>
                    <cti:button type="submit" nameKey="importSubmitButton"/>
                </form>
                <br><br>
                <h3><i:inline key="yukon.web.modules.amr.fdrTranslationManagement.reportTitle"/></h3>
                <i:inline key="yukon.web.modules.amr.fdrTranslationManagement.reportText"/>
                <br><br>
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
                <h3><i:inline key="yukon.web.modules.amr.fdrTranslationManagement.importTitle"/></h3>
                <i:inline key="yukon.web.modules.amr.fdrTranslationManagement.importText"/>
                <br><br>
                <i:inline key="yukon.web.modules.amr.fdrTranslationManagement.defaultColumnsText"/><br>
                
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
                    <tr>
                        <td class="smallBoldLabel">
                            <i:inline key="yukon.web.modules.amr.fdrTranslationManagement.defaultColumns.direction"/>
                        </td>
                        <td>
                            <i:inline key="yukon.web.modules.amr.fdrTranslationManagement.defaultColumns.directionDescription"/>
                        </td>
                    </tr>
                </table>
                <br><br>
                <i:inline key="yukon.web.modules.amr.fdrTranslationManagement.interfaceColumnsText"/>
                <br><br>
                <i:inline key="yukon.web.modules.amr.fdrTranslationManagement.interfaceColumnsLabel"/>
                <select id="interfaceColumnSelector">
                    <c:forEach var="interface" items="${interfaceTypes}">
                        <option value="${interface}"><i:inline key="${interface}"/></option>
                    </c:forEach>
                </select>
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
        </div>
    </tags:boxContainer2>
</cti:standardPage>