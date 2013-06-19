var jwsCurrentUrl = "";

function jwsLaunch(url) {
    jQuery('#javaWebStartPopup').dialog("open");
    jwsCurrentUrl = url;
}

function jwsRelaunchCurrent() {
    jQuery('#javaWebStartPopup').dialog("close");
    window.location.replace(jwsCurrentUrl);
}