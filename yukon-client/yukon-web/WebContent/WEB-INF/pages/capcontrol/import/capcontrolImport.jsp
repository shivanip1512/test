<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="capcontrol" page="import">    
    <script>
    jQuery(document).ready(function(){
        updateImportTypeSelection();
        jQuery("#importTypeSelector").change(function(){
            updateImportTypeSelection();
            //get the import type value, remove spaces, lowercase
            var importType = jQuery('#importTypeSelector').val().replace(/_/g, '');
            importType = importType.toLowerCase();
            jQuery("#importForm").attr("action", "/capcontrol/import/" + importType + "File");
        });
    });

    function updateImportTypeSelection() {
        var itemSelected = jQuery("#importTypeSelector").val();
        //hide all rows
        jQuery("[class^='importType_']").hide();
        //show rows for the selected interface
        jQuery(".importType_" + itemSelected).show();
    }
    </script>

    <div style="width:50%;">
    <tags:boxContainer2 nameKey="importContainer">
        <div>
            <i:inline key=".importTypeSelect" />
            <select id="importTypeSelector">
                <c:forEach var="importType" items="${importTypes}">
                    <option value="${importType}">
                        <i:inline key="${importType}" />
                    </option>
                </c:forEach>
            </select>
        </div>
        <div>
            <form id="importForm" method="post" action="/capcontrol/import/cbcFile" enctype="multipart/form-data">
                <div>
                    <img src="/WebConfig/yukon/Icons/folder_edit.gif">
                    <input type="file" name="dataFile">
                </div>
                <div>
                    <cti:button type="submit" nameKey="importSubmitButton" id="importSubmitButton" styleClass="f_blocker" />
                </div>
            </form>
        </div>
        </tags:boxContainer2>
    </div>

    <c:if test="${!empty results}">
    <tags:boxContainer2 nameKey="resultContainer">
        <ol style="list-style-type:decimal; padding-left: 35px">
            <c:forEach var="result" items="${results}">
                <c:choose>
                    <c:when test="${result.success}">
                        <c:set var="fontColor" value="successMessage"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="fontColor" value="errorMessage"/>
                    </c:otherwise>
                </c:choose>
                <li>
                     <span class="${fontColor}"><i:inline key="${result.importResultMessage}"/></span>
                </li>
            </c:forEach>
        </ol>
    </tags:boxContainer2>
    </c:if>

    <div style="width: 50%">
        <c:forEach var="importType" items="${importTypes}">
            <div class="importType_${importType}" style="display: none;">
                <table class="resultsTable detail">
                    <tr>
                        <td colspan="2" style="background-color: #CDCDCD;">
                            <cti:msg2 var="displayImportType" key="${importType}" />
                            <div class="fwb"> ${displayImportType} File Format</div>
                            <a href="<cti:url value="/WebConfig/custom/sample_capcontrol_files/Sample ${displayImportType} Import.csv"/>">
                                <img src="/WebConfig/yukon/Icons/download_file.gif">
                                <i:inline key="yukon.web.modules.capcontrol.import.sampleFile" />
                            </a>
                        </td>
                    </tr>

                    <c:forEach var="columnType" items="${importType.columnTypes}">
                        <c:if test="${not empty columnType.value}">
                            <tr>
                                <th align="left">
                                    <i:inline key="yukon.web.modules.capcontrol.import.header.${columnType.key}" />
                                </th>
                                <th>
                                    <i:inline key="yukon.web.modules.capcontrol.import.header.description" />
                                </th>
                            </tr>
                            <c:forEach var="column" items="${columnType.value}">
                                <tr>
                                    <td class="smallBoldLabel">${column}</td>
                                    <td>
                                        <i:inline key="yukon.web.modules.capcontrol.import.description.${importType}.${column}" />
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:if>
                    </c:forEach>
                </table>
            </td>
            </div>
        </c:forEach>
    </div>
</cti:standardPage>