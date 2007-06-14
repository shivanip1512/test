var jwsCancelCheck = 0;
var jwsCurrentUrl = "";

function jwsLaunch(url) {
  var javaCookieValue = readCookie("javainstalled");
  if (javaCookieValue == 'true') {
    window.location.replace(url);
    return;
  }
  
  jwsCurrentUrl = url;
  jwsCancelCheck += 1;
  $('javaWebStartPopup').show();
  
  // add applet to page
  // <APPLET CODE="JavaDetector.class"  codebase="/JavaScript/" NAME="myApplet" HEIGHT=0 WIDTH=0></APPLET>
  var applet = document.createElement("applet");
  applet.setAttribute("code", "JavaDetector.class");
  applet.setAttribute("codebase", "/JavaScript/");
  applet.setAttribute("name", "myApplet");
  applet.setAttribute("mayscript", "");
  applet.setAttribute("height", "0");
  applet.setAttribute("width", "0");
  document.body.appendChild(applet);

  var cancelCheckAtStart = jwsCancelCheck;

  setTimeout(function() {
    if (cancelCheckAtStart != jwsCancelCheck) return;
    var thisApplet = document.getElementsByTagName("applet")[0];
      if (thisApplet.getJavaVersion) {
        var javaVersion = thisApplet.getJavaVersion();
        if (javaVersion.indexOf('.') != -1) {
          createCookie("javainstalled", "true");
          $('javaWebStartPopup').hide();
          window.location.replace(url);
          return;
        }
      }
    $('javaWebStartWaiting').hide();
    $('javaWebStartNoJava').show();
  },5000);
}

function jwsClosePopup() {
  jwsCancelCheck += 1;
  $('javaWebStartPopup').hide();
}

function jwsRelaunchCurrent() {
  jwsCancelCheck += 1;
  $('javaWebStartWaiting').hide();
  window.location.replace(jwsCurrentUrl);
}

