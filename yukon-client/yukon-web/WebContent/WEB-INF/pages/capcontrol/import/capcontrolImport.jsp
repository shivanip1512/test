<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="capcontrol" page="import">
    <script>
    $(document).ready(function(){
        updateImportTypeSelection();
        $('#importTypeSelector').change(function(){
            updateImportTypeSelection();
            //get the import type value, remove spaces, lowercase
            var importType = $('#importTypeSelector').val().replace(/_/g, '');
            importType = importType.toLowerCase();
            $('#importForm').attr('action', yukon.url('/capcontrol/import/') + importType + 'File');
        });
    });

    function updateImportTypeSelection() {
        var itemSelected = $('#importTypeSelector').val();
        //hide all rows
        $("[class^='importType_']").hide();
        //show rows for the selected interface
        $('.importType_' + itemSelected).show();
    }
    </script>

    <div class="column-10-14">
        <div class="column one">
            <tags:sectionContainer2 nameKey="importContainer" styleClass="stacked">
                <cti:url var="cbcFileUrl" value="/capcontrol/import/cbcFile"/>
                <form id="importForm" method="post" action="${cbcFileUrl}" enctype="multipart/form-data">
                    <cti:csrfToken/>
                    <cti:msg2 key=".importTypeSelect" var="importTypeName" htmlEscape="true"/>
                    <cti:msg2 key="yukon.common.file" var="fileName" htmlEscape="true"/>
                    <tags:nameValueContainer>
                        <tags:nameValue name="${importTypeName}">
                            <select id="importTypeSelector">
                                <c:forEach var="importType" items="${importTypes}">
                                    <option value="${importType}">
                                        <i:inline key="${importType}" />
                                    </option>
                                </c:forEach>
                            </select>
                        </tags:nameValue>
                        <tags:nameValue name="${fileName}">
                            <input type="file" name="dataFile">
                        </tags:nameValue>
                    </tags:nameValueContainer>
                    
                    <div class="action-area">
                        <cti:button type="submit" nameKey="importSubmitButton" id="importSubmitButton" classes="js-blocker primary action" />
                    </div>
                </form>
            </tags:sectionContainer2>

        </div>

        <div class="column two nogutter">
            <c:forEach var="importType" items="${importTypes}">
                <div class="importType_${importType}" style="display: none;">
                    <table class="results-table detail ">
                        <tr>
                            <td colspan="2" style="background-color: #CDCDCD;">
                                <cti:msg2 var="displayImportType" key="${importType}" />
                                <span class="fwb"> ${displayImportType} File Format</span>
                                <a href="<cti:url value="/WebConfig/custom/sample_capcontrol_files/Sample ${displayImportType} Import.csv"/>" class="fr">
                                    <cti:icon icon="icon-page-white-excel"/>
                                    <i:inline key=".sampleFile" />
                                </a>
                            </td>
                        </tr>
    
                        <c:forEach var="columnType" items="${importType.columnTypes}">
                            <c:if test="${not empty columnType.value}">
                                <tr>
                                    <th><i:inline key=".header.${columnType.key}" /></th>
                                    <th><i:inline key=".header.description" /></th>
                                </tr>
                                <c:forEach var="column" items="${columnType.value}">
                                    <tr>
                                        <td class="fwb wsnw">${column}</td>
                                        <td><i:inline key=".description.${importType}.${column}" /></td>
                                    </tr>
                                </c:forEach>
                            </c:if>
                        </c:forEach>
                    </table>
                </div>
            </c:forEach>
        </div>
    </div>
    <c:if test="${not empty results}">
        <tags:boxContainer2 nameKey="resultContainer">
            <ol style="list-style-type:decimal; padding-left: 35px">
                <c:forEach var="result" items="${results}">
                    <c:choose>
                        <c:when test="${result.success}">
                            <c:set var="fontColor" value="success"/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="fontColor" value="error"/>
                        </c:otherwise>
                    </c:choose>
                    <li>
                         <span class="${fontColor}"><i:inline key="${result.importResultMessage}"/></span>
                    </li>
                </c:forEach>
            </ol>
        </tags:boxContainer2>
    </c:if>
</cti:standardPage>