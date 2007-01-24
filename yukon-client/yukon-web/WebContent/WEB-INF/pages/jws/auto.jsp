<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<cti:standardPage title="Client Launcher" module="javawebstart">
<cti:standardMenu/>

<!--  This will be ignored by Mozilla Browsers -->
<object codeBase="http://java.sun.com/update/1.5.0/jinstall-1_5_0_05-windows-i586.cab"
        classid="clsid:5852F5ED-8BF4-11D4-A245-0080C6F74284" height="0" width="0">
    <param name="app" value="${app}">
    <param name="back" value="true">
</object>

<p>If the application does not start automatically, please click on the following link to launch it manually.</p>

<p><a href="${app}">Launch Manually</a></p>

<script type="text/javascript">
if (!isIE()) {
    if (!webstartVersionCheck("1.5")) {
        window.open("http://www.java.com/getjava/");
            
        launchTID = setInterval(launchJNLP, 100);
    
        function launchJNLP() {
            if (webstartVersionCheck("1.5")) {
                clearInterval(launchTID);
                window.location = "${app}";
            }
        }
    } else {
        window.location = "${app}";
    }
}

function isIE() {
    var agent = navigator.userAgent.toLowerCase();
    return (agent.indexOf("msie") > 0) && (agent.indexOf("win") > 0);
}

function webstartVersionCheck(versionString) {
    // Mozilla may not recognize new plugins without this refresh
    navigator.plugins.refresh(true);
    // First, determine if Web Start is available
    if (navigator.mimeTypes['application/x-java-jnlp-file']) {
        // Next, check for appropriate version family
        for (var i = 0; i < navigator.mimeTypes.length; ++i) {
            pluginType = navigator.mimeTypes[i].type;
            if (pluginType == "application/x-java-applet;version=" + versionString) {
                return true;
            }
        }
    }
    return false;
}
</script>
	
</cti:standardPage>