<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<cti:standardPage module="support" page="support">

<script type="text/javascript">
function openFtpPopup(fileName){
    jQuery.ajax({
        url: "/support/infoOnBundle",
        data: {fileName: fileName}
    }).done(function(json) {
        var form = jQuery("#ftpPopupForm");
        jQuery("#uploadFileName").text(json.fileName);
        jQuery("#uploadFileSize").text(json.fileSize);
        jQuery("#uploadFileDate").text(json.fileDate);
        jQuery("input[name=fileName]", "#ftpPopupForm").val(fileName);
        jQuery("#ftpUploadPopup").dialog({
            buttons: {
                upload: {
                    text: "<cti:msg2 key='.supportBundle.startUpload.label' />",
                    class: "primary",
                    click: function() {
                        form.submit();
                    }
                },
                cancel: {
                    text: '<cti:msg2 key=".cancel"/>',
                    click: function() {
                        jQuery(this).dialog("close");
                    }
                }
            }
        });
    });
}

jQuery(function() {
    jQuery("#ftpUploadBtn").click(function() {
        var chosenBundle = jQuery("input[name=fileName]:checked", "#previousBundlesForm").val();
        openFtpPopup(chosenBundle);
    });
});
</script>

<div class="column_8_8_8 clearfix">
    <div class="column one">
        <tags:sectionContainer2 nameKey="pageList">
            <ul class="simple-list">
                <c:forEach var="wrapper" items="${supportPages}">
                    <li>
                    <c:if test="${wrapper.enabled}">
                        <a href="${wrapper.page.link}"><i:inline key="${wrapper.page.formatKey}"/></a>
                    </c:if>
                    <c:if test="${not wrapper.enabled}">
                        <cti:msg2 key=".noPermissions" argument="${wrapper.requiredPermissions}" var="perms"/>
                        <span title="${perms}" class="disabled"><i:inline key="${wrapper.page.formatKey}"/></span>
                    </c:if>
                    </li>
                </c:forEach>            
            </ul>
        </tags:sectionContainer2>
    </div>
    <div class="column two">
        <tags:sectionContainer2 nameKey="manuals">
            <ul class="simple-list stacked">
                <li><a href="<cti:msg2 key=".manuals.amiAdmin.link"/>"><i:inline key=".manuals.amiAdmin.title"/></a></li>
                <li><a href="<cti:msg2 key=".manuals.amiUser.link"/>"><i:inline key=".manuals.amiUser.title"/></a></li>
                <li><a href="<cti:msg2 key=".manuals.csr.link"/>"><i:inline key=".manuals.csr.title"/></a></li>
                <li><a href="<cti:msg2 key=".manuals.drAdmin.link"/>"><i:inline key=".manuals.drAdmin.title"/></a></li>
                <li><a href="<cti:msg2 key=".manuals.drUser.link"/>"><i:inline key=".manuals.drUser.title"/></a></li>
                <li><a href="<cti:msg2 key=".manuals.daAdmin.link"/>"><i:inline key=".manuals.daAdmin.title"/></a></li>
                <li><a href="<cti:msg2 key=".manuals.daUser.link"/>"><i:inline key=".manuals.daUser.title"/></a></li>
            </ul>
            <a href="<cti:msg2 key=".manuals.additional.link"/>"><i:inline key=".manuals.additional.title"/></a>
        </tags:sectionContainer2>
    </div>
    <div class="column three nogutter">
        <tags:sectionContainer2 nameKey="contact">
            <div class="stacked"><label><strong><i:inline key=".email.header"/></strong>&nbsp;<a href="mailto:<cti:msg2 key=".email.value"/>"><cti:msg2 key=".email.value"/></a></label></div>
            <div><label><strong><i:inline key=".phone.header"/></strong>&nbsp;<i:inline key=".phone.value"/></label></div>
            <div><label><strong><i:inline key=".hours.header"/></strong>&nbsp;<i:inline key=".hours.value"/></label></div>
            <div class="stacked"></div>
            <div><a href="<cti:msg2 key=".rma.link"/>" target="_blank"><i:inline key=".rma"/></a></div>
        </tags:sectionContainer2>
    </div>
</div>

<div class="column_12_12 clearfix">
    <div class="column one">
        <tags:sectionContainer2 nameKey="bundle">
            <cti:tabbedContentSelector mode="section">
                <cti:msg2 key=".supportBundle.createNewHeading" var="createNewHeading"/>
                <cti:tabbedContentSelectorContent selectorName="${createNewHeading}" >
                    <form:form id="createBundleForm" commandName="supportBundle" action="/support/createBundle" method="POST">
                        <tags:nameValueContainer2>
                            <tags:nameValue2 nameKey=".supportBundle.custNameLbl">
                                <tags:input path="customerName"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".supportBundle.dateRangeSelect">
                                <form:select path="bundleRangeSelection">
                                    <c:forEach var="rangeSel" items="${bundleRangeSelectionOptions}">
                                        <form:option value="${rangeSel}">
                                            <cti:msg2 key="${rangeSel.formatKey}"/>
                                        </form:option>
                                    </c:forEach>
                                </form:select>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".supportBundle.includeLbl">
                                <c:forEach var="writer" items="${writerList}">
                                    <c:if test="${writer.optional}">
                                        <form:checkbox path="optionalWritersToInclude" value="${writer.name}"/> 
                                        <i:inline key=".supportBundle.writerName.${writer.name}"/><br>
                                    </c:if>
                                </c:forEach>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".supportBundle.notesLbl">
                                <form:textarea rows="6" cols="40" path="comments"/>
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                        <div class="pageActionArea"> 
                            <c:if test="${!inProgress}">
                                <cti:button nameKey="supportBundle.createBundleBtn" type="submit"/>
                            </c:if>
                            <c:if test="${inProgress}">
                                <i:inline key=".supportBundle.bundleInProgressMsg"/>
                                <cti:button id="viewProgress" nameKey="supportBundle.viewProgressBtn" href="/support/viewBundleProgress" />
                            </c:if>
                        </div>
                    </form:form>
                </cti:tabbedContentSelectorContent>
                <cti:msg2 key='.supportBundle.previousHeading' var="previousHeading"/>
                <cti:tabbedContentSelectorContent selectorName="${previousHeading}" >
                    <form id="previousBundlesForm" action="/support/downloadBundle" method="POST">
                        <c:if test="${empty bundleList}">
                            <span class="empty-list"><i:inline key=".supportBundle.noPreviousBundlesLbl"/></span>
                        </c:if>
                        <c:if test="${not empty bundleList}">
                            <ul class="simple-list">
                                <c:forEach var="bundle" varStatus="status" items="${bundleList}">
                                    <li>
                                        <c:if test="${status.first}"><c:set var="checked">checked="checked"</c:set></c:if>
                                        <c:if test="${!status.first}"><c:set var="checked"></c:set></c:if>
                                        <label><input type="radio" name="fileName"value="${bundle.fileName}" ${checked}>${fn:escapeXml(bundle.fileName)}</label>
                                    </li>
                                </c:forEach>
                            </ul>
                        </c:if>
                        <div class="pageActionArea">
                            <cti:button nameKey="supportBundle.downloadBtn" type="submit" disabled="${empty bundleList}" icon="icon-bullet-go-down"/>
                            <cti:button nameKey="supportBundle.ftpUploadBtn" id="ftpUploadBtn" href="javascript:void(0);" disabled="${empty bundleList}" icon="icon-bullet-go-up"/>
                        </div>
                    </form>

                    <div id="ftpUploadPopup" title="<cti:msg2 key=".supportBundle.send.fileHeading"/>" class="dn">
                        <tags:nameValueContainer2>
                            <tags:nameValue2 nameKey=".supportBundle.send.filenameLbl">
                                <span id="uploadFileName"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".supportBundle.send.fileDateCreatedLbl">
                                <span id="uploadFileDate"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".supportBundle.send.fileSizeLbl">
                                <span id="uploadFileSize"/>
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                        <form id="ftpPopupForm" action="/support/uploadBundle" method="POST">
                            <input type="hidden" name="fileName" />
                        </form>
                    </div>
                </cti:tabbedContentSelectorContent>
            </cti:tabbedContentSelector>
        </tags:sectionContainer2>
    </div>

    <div class="column two nogutter">
        <tags:sectionContainer2 nameKey="logs">
             <ul class="stacked simple-list">
                <c:forEach var="logFile" items="${todaysLogs}">
                    <cti:url value="/support/logging/tail" var="url"><cti:param name="file" value="/${logFile.fileName}"></cti:param></cti:url>
                    <li> <a href="${url}"> <i:inline key="${logFile.message}"/> </a></li>
                </c:forEach>
            </ul>
            <a href="/support/logging/menu?file=/&sortType=date"><i:inline key=".allLogs"/></a>
        </tags:sectionContainer2>

        <tags:sectionContainer2 nameKey="dbInfo">
                <div><strong><i:inline key=".databaseInfo.dbConnection.jdbcUrl"/></strong></div>
                <div class="stacked">${dbUrl}</div>
                <div><strong><i:inline key=".databaseInfo.dbConnection.jdbcUser"/></strong></div>
                <div class="stacked">${dbUser}</div>
                <a href="/support/database/validate/home"><i:inline key=".databaseValidate.pageName"/></a>
        </tags:sectionContainer2>
    </div>
</div>
</cti:standardPage>