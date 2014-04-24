<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="support" page="supportBundle">
<script type="text/javascript">
$(function() {
    checkUpdate();

    $("#ftpUploadBtn").click(function() {
        var chosenBundle = $("input[name=fileName]", "#uploadForm").val();
        openFtpPopup(chosenBundle);
    });
});

function openFtpPopup(fileName){
    $.ajax({
        url: yukon.url("/support/infoOnBundle"),
        data: {fileName: fileName}
    }).done(function(results) {
        var form = $("#ftpPopupForm");
        $("#uploadFileName").text(results.fileName);
        $("#uploadFileSize").text(results.fileSize);
        $("#uploadFileDate").text(results.fileDate);
        $("input[name=fileName]", "#ftpPopupForm").val(fileName);
        $("#ftpUploadPopup").dialog({
            buttons: {
                cancel: {
                    text: "<cti:msg2 key=".cancel" />",
                    click: function() {
                        $(this).dialog("close");
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
    $("#mainDiv").load(yukon.url("/support/getBundleProgress"));
}

function checkUpdate(){
    $.getJSON(yukon.url("/support/bundleInProgress")).done(function(json) {
        refreshContent();
        if(json.inProgress) {
            setTimeout(checkUpdate,1000);
        } else{
            $("input[name=fileName]", "#uploadForm").val(json.fileName);
            $("#uploadForm :button").removeAttr("disabled");
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
