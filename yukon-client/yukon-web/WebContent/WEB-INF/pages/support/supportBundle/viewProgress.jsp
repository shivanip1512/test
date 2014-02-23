<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="support" page="supportBundle">
<script type="text/javascript">
jQuery(function() {
    checkUpdate();

    jQuery("#ftpUploadBtn").click(function() {
        var chosenBundle = jQuery("input[name=fileName]", "#uploadForm").val();
        openFtpPopup(chosenBundle);
    });
});

function openFtpPopup(fileName){
    jQuery.ajax({
        url: "/support/infoOnBundle",
        data: {fileName: fileName}
    }).done(function(results) {
        var form = jQuery("#ftpPopupForm");
        jQuery("#uploadFileName").text(results.fileName);
        jQuery("#uploadFileSize").text(results.fileSize);
        jQuery("#uploadFileDate").text(results.fileDate);
        jQuery("input[name=fileName]", "#ftpPopupForm").val(fileName);
        jQuery("#ftpUploadPopup").dialog({
            buttons: {
                cancel: {
                    text: "<cti:msg2 key=".cancel" />",
                    click: function() {
                        jQuery(this).dialog("close");
                    }
                },
                upload: {
                    text: "<cti:msg2 key=".supportBundle.startUpload.label" />",
                    'class': "primary",
                    click: function() {
                        form.submit();
                    }
                }
            }
        });
    });
}

function refreshContent(){
    jQuery("#mainDiv").load("/support/getBundleProgress");
}

function checkUpdate(){
    jQuery.getJSON("/support/bundleInProgress").done(function(json) {
        refreshContent();
        if(json.inProgress) {
            setTimeout(checkUpdate,1000);
        } else{
            jQuery("input[name=fileName]", "#uploadForm").val(json.fileName);
            jQuery("#uploadForm :button").removeAttr("disabled");
        }
    });
}
</script>
<div id="mainDiv"></div>
<div class="page-action-area">
    <form id="uploadForm" method="POST" action="downloadBundle">
        <cti:csrfToken/>
        <input type="hidden" name="fileName">
        <cti:button nameKey="downloadBtn" disabled="true" name="download" type="submit" icon="icon-download"/>
        <cti:button nameKey="ftpUploadBtn" disabled="true" id="ftpUploadBtn" href="javascript:void(0);" icon="icon-upload"/>
    </form>
</div>

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
    <form id="ftpPopupForm" action="uploadBundle" method="POST">
        <cti:csrfToken/>
        <input type="hidden" name="fileName" />
    </form>
</div>

</cti:standardPage>
