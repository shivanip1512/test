<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="support" page="supportBundle">

    <script type="text/javascript">
    Event.observe(window, 'load', function() {

        if (${inProgress}) {
            $('supportBundleForm').disable();
            $('inProgressMessage').show();
            $('viewProgressBtn').enable();
        }
        
        Event.observe('downloadBtn', 'click', function() {
            var selectedFile = Form.getInputs('previousBundlesForm', 'radio',
            'previousBundles').find(function(radio) {return radio.checked;}).value;
            
            window.location = "download?fileNum=" + selectedFile;
        });

        Event.observe('ftpUploadBtn', 'click', function() {
            Yukon.ui.blockPage();
            var selectedFile = Form.getInputs('previousBundlesForm', 'radio',
                    'previousBundles').find(function(radio) {return radio.checked;}).value;
            window.location = "send?fileNum=" + selectedFile;
        });

        Event.observe('viewProgressBtn', 'click', function() {
            window.location = "viewProgress";
        });

        Event.observe('createBundleBtn', 'click', function() {
            Yukon.ui.blockPage();
            $("supportBundleForm").submit();
        });
    });
</script>
    <cti:dataGrid cols="2" tableClasses="collectionActionAlignment collectionActionCellPadding">
        <cti:dataGridCell>
            <form:form id="supportBundleForm" commandName="supportBundle" action="createBundle"
                method="POST">
                <h1><i:inline key=".createNewHeading" /></h1><br>
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".custNameLbl">
                        <tags:input path="customerName" />
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".dateRangeSelect">
                        <form:select path="bundleRangeSelection">
                            <c:forEach var="rangeSel" items="${bundleRangeSelection}">
                                <form:option value="${rangeSel}">
                                    <i:inline key=".bundleRangeSelection.${rangeSel}" />
                                </form:option>
                            </c:forEach>
                        </form:select>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".includeLbl">
                        <c:forEach var="source" items="${sourceList}">
                            <c:if test="${source.optional}">
                                <form:checkbox path="optionalSourcesToInclude" value="${source.sourceName}" /> 
                                <i:inline key=".sourceName.${source.sourceName}" /><br>
                            </c:if>
                        </c:forEach>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".notesLbl">
                        <form:textarea rows="6" cols="40" path="comments"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2><br>
                <cti:button key="createBundleBtn" id="createBundleBtn"/>
                <span id="inProgressMessage" style="display:none"> 
                    <br><br><i:inline key='.bundleInProgressMsg' />
                    <cti:button key="viewProgressBtn" id="viewProgressBtn"/>
                </span>
            </form:form>
        </cti:dataGridCell>
        <cti:dataGridCell>
            <form:form id="previousBundlesForm" method="POST" action="send">
                <input type="hidden" name="fileToSend" id="fileToSend" value="" />
                <input type="hidden" name="uploadAttempt" id="uploadAttempted" value="false" />
                <table>
                    <tr>
                        <td>
                        <h1><i:inline key=".previousHeading" /></h1>
                        </td>
                    </tr>
                    <tr><td>&nbsp;</td></tr>
                    <c:if test="${not empty bundleList}">
                        <c:forEach var="bundle" varStatus="status" items="${bundleList}">
                            <tr>
                            <c:set var="checkedAtt" value=""/>
                            <c:if test="${status.first}">
                                <c:set var="checkedAtt" value=" checked=\"checked\""/>
                            </c:if>
                                <td><input type="radio" name="previousBundles"${checkedAtt}
                                    value="${status.index}" /> ${bundle.name}</td>
                            </tr>
                        </c:forEach>
                    </c:if>
                    <c:if test="${empty bundleList}">
                        <tr>
                            <td>
                                <i:inline key=".noPreviousBundlesLbl"/>
                            </td>
                        </tr>
                    </c:if>
                    <tr><td>&nbsp;</td></tr>
                    <tr>
                        <td>
                            <cti:button key="downloadImg" id="downloadBtn" disabled="${empty bundleList}" />
                            <cti:button key="uploadImg" id="ftpUploadBtn" disabled="${empty bundleList}" />
                        </td>
                    </tr>
                </table>
            </form:form>
        </cti:dataGridCell>
    </cti:dataGrid>
</cti:standardPage>
