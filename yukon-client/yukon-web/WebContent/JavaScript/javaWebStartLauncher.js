var jwsCurrentUrl = "";

function jwsLaunch(url) {
  $('javaWebStartPopup').show();
  jwsCurrentUrl = url;
}

function jwsClosePopup() {
  $('javaWebStartPopup').hide();
}

function jwsRelaunchCurrent() {
  $('javaWebStartPopup').hide();
  window.location.replace(jwsCurrentUrl);
}

