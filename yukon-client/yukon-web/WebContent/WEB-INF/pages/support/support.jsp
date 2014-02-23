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
                    'class': "primary",
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

<div class="column-8-8-8 clearfix">
    <div class="column one">
        <tags:sectionContainer2 nameKey="pageList">
            <ul class="simple-list">
                <c:forEach var="wrapper" items="${supportPages}">
                    <li>
                    <c:if test="${wrapper.enabled}">
                        <a href="${wrapper.page.link}"><i:inline key="${wrapper.page}"/></a>
                    </c:if>
                    <c:if test="${not wrapper.enabled}">
                        <cti:msg2 key=".siteMap.noPermissions" argument="${wrapper.requiredPermissions}" var="perms"/>
                        <span title="${perms}" class="disabled"><i:inline key="${wrapper.page}"/></span>
                    </c:if>
                    </li>
                </c:forEach>            
            </ul>
        </tags:sectionContainer2>
    </div>
    <div class="column two">
        <tags:sectionContainer2 nameKey="manuals">
            <ul class="simple-list stacked">
                <li class="stacked"><a href="<cti:msg2 key=".manuals.general.link"/>"><i:inline key=".manuals.general.title"/></a>&nbsp;<span class="notes"><i:inline key=".manuals.general.label"/></span></li>
                
                <c:forEach var="manual" items="${manuals}">
                    <cti:url var="manualLink" value="/support/manual">
                        <cti:param name="manualName" value="${manual}" />
                    </cti:url>
                    <li>
                        <a href="${manualLink}">${fn:escapeXml(manual)}</a>
                    </li>
                </c:forEach>
            </ul>
        </tags:sectionContainer2>
    </div>
    <div class="column three nogutter">
        <tags:sectionContainer2 nameKey="contact">
            <div class="stacked">
                <label><strong><i:inline key=".email.header"/></strong>&nbsp;<a href="mailto:<cti:msg2 key=".email.value"/>"><i:inline key=".email.value"/></a></label>
            </div>
            <div class="stacked">
                <div><label><strong><i:inline key=".phone.header"/></strong>&nbsp;<i:inline key=".phone.value"/></label></div>
                <div><label><strong><i:inline key=".hours.header"/></strong>&nbsp;<i:inline key=".hours.value"/></label></div>
            </div>
            <div>
                <label><a href="<cti:msg2 key=".supportSite.link"/>"><i:inline key=".supportSite.title"/></a>&nbsp;<span class="notes"><i:inline key=".supportSite.label"/></span></label>
            </div>
            <div>
                <label><a href="<cti:msg2 key=".powerCentral.link"/>"><i:inline key=".powerCentral.title"/></a>&nbsp;<span class="notes"><i:inline key=".powerCentral.label"/></span></label>
            </div>
            <div>
                <a href="<cti:msg2 key=".rma.link"/>" target="_blank"><i:inline key=".rma"/></a>
            </div>
        </tags:sectionContainer2>
    </div>
</div>

<cti:checkRolesAndProperties value="OPERATOR_ADMINISTRATOR">
<div class="column-12-12 clearfix">
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
                                        <label>
                                            <form:checkbox path="optionalWritersToInclude" value="${writer.name}"/> 
                                            <i:inline key=".supportBundle.writerName.${writer.name}"/>
                                        </label><br>
                                    </c:if>
                                </c:forEach>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".supportBundle.notesLbl">
                                <form:textarea rows="6" cols="40" path="comments"/>
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                        <div class="page-action-area"> 
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
                        <cti:csrfToken/>
                        <c:if test="${empty bundleList}">
                            <span class="empty-list"><i:inline key=".supportBundle.noPreviousBundlesLbl"/></span>
                        </c:if>
                        <c:if test="${not empty bundleList}">
                            <ul class="simple-list">
                                <c:forEach var="bundleName" varStatus="status" items="${bundleList}">
                                    <li>
                                        <c:if test="${status.first}"><c:set var="checked">checked="checked"</c:set></c:if>
                                        <c:if test="${!status.first}"><c:set var="checked"></c:set></c:if>
                                        <label><input type="radio" name="fileName" value="${bundleName}" ${checked}>${fn:escapeXml(bundleName)}</label>
                                    </li>
                                </c:forEach>
                            </ul>
                        </c:if>
                        <div class="page-action-area">
                            <cti:button nameKey="supportBundle.downloadBtn" type="submit" disabled="${empty bundleList}" icon="icon-download"/>
                            <cti:button nameKey="supportBundle.ftpUploadBtn" id="ftpUploadBtn" href="javascript:void(0);" disabled="${empty bundleList}" icon="icon-upload"/>
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
                            <cti:csrfToken/>
                            <input type="hidden" name="fileName" />
                        </form>
                    </div>
                </cti:tabbedContentSelectorContent>
            </cti:tabbedContentSelector>
        </tags:sectionContainer2>
    </div>

    <div class="column two nogutter">
        <cti:checkRolesAndProperties value="ADMIN_VIEW_LOGS">
        <tags:sectionContainer2 nameKey="logs">
             <ul class="stacked simple-list">
                <c:forEach var="logFile" items="${todaysLogs}">
                    <cti:url value="/support/logging/view" var="url"><cti:param name="file" value="/${logFile.name}"></cti:param></cti:url>
                    <li> <a href="${url}">${logFile.identifier}</a></li>
                </c:forEach>
            </ul>
            <a href="/support/logging/menu?file=/&sortType=date"><i:inline key=".allLogs"/></a>
        </tags:sectionContainer2>
        </cti:checkRolesAndProperties>

        <tags:sectionContainer2 nameKey="dbInfo">
                <div><strong><i:inline key=".databaseInfo.dbConnection.jdbcUrl"/></strong></div>
                <div class="stacked">${dbUrl}</div>
                <div><strong><i:inline key=".databaseInfo.dbConnection.jdbcUser"/></strong></div>
                <div class="stacked">${dbUser}</div>
                <a href="/support/database/validate/home"><i:inline key=".databaseValidate.pageName"/></a>
        </tags:sectionContainer2>
    </div>
</div>
</cti:checkRolesAndProperties>
</cti:standardPage>