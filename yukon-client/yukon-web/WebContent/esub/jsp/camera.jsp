<html>
<BODY BGCOLOR="#000000" LINK="#000000" ALINK="#000000" VLINK="#000000">
<script type="text/javascript" src="camera.js"></script>
<script type="text/javascript" src="xmlhttp.js"></script>
<table>
    <tr><td height=5>&nbsp;</td></tr> 
      <tr><td align=center> 
		<script type="text/javascript">
		var presets = new Array('Door', 'AirVent');
		setPresets(presets);
		setAutoPanDwell(30);
		setAutoPanTimeout(240);
		startAutoPan();
      <!--
      if ((navigator.appName == "Microsoft Internet Explorer")&&(navigator.platform != "MacPPC"))
      {
        document.write("<OBJECT ID=\"VAMCtrl\" WIDTH=600 HEIGHT=507");
        document.write(" CLASSID=CLSID:A93B47FD-9BF6-4DA8-97FC-9270B9D64A6C");
        document.write(" CODEBASE=\"/plugin/h263ctrl.cab#version=1,8,0,5\">");
        document.write("<PARAM NAME=\"DigitalZoomEdit\" VALUE=\"true\">");
        document.write("<PARAM NAME=\"EnableVolumeControl\" VALUE=\"false\">");
        document.write("<PARAM NAME=\"Deblocking\" VALUE=\"true\">");
        document.write("<PARAM NAME=\"Url\" VALUE=\"http://66.172.242.31:80/cgi-bin/video.vam\">");
        document.write("<PARAM NAME=\"VSize\" VALUE=\"SIF\">");
       document.write("<PARAM NAME=\"Language\" VALUE=\"EN\">");
        document.write("</OBJECT>");
      }
      //-->
      </script> 
      </td></tr> 
</table>
<form name="presets">
	<script type="text/javascript">
		for ( i = 0; i < presets.length; i++) {
			document.write('<input type=\"button\" value=\"' + presets[i] + '\" onclick=\"manualPanToPreset(\'' + presets[i] + '\')\">\n');
		}
	</script>
</form>
</html>