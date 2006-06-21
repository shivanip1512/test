<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<cti:standardPage title="Yukon WebStart" module="capcontrol">

<jsp:setProperty name="CtiNavObject" property="moduleExitPage" value="<%=request.getRequestURL().toString()%>"/>

<!-- necessary DIV element for the OverLIB popup library -->
<div id="overDiv" style="position:absolute; visibility:hidden; z-index:1000;"></div>

<cti:standardMenu/>

<cti:breadCrumbs>
    <cti:crumbLink url="webstart.jsp" title="Yukon WebStart Applications"/>
</cti:breadCrumbs>

    <cti:titledContainer title="Yukon WebStart Applications">
 		
<!---- This initializes the navigator.family object ---->
<SCRIPT LANGUAGE="JavaScript"
  SRC="js/xbDetectBrowser.js">
</SCRIPT>


<SCRIPT LANGUAGE="JavaScript">
var javawsInstalled = 0; 
isIE = "false"; 

if (navigator.mimeTypes && navigator.mimeTypes.length) {
	x = navigator.mimeTypes['application/x-java-jnlp-file']; 
	if (x) javawsInstalled = 1;
} else {
	isIE = "true"; 
} 
</SCRIPT>
		
<SCRIPT LANGUAGE="VBScript">
on error resume next
If isIE = "true" Then
  If Not(IsObject(CreateObject("JavaWebStart.isInstalled"))) Then
	 javawsInstalled = 0
  Else
	 javawsInstalled = 1
  End If
  If Not(IsObject(CreateObject("JavaWebStart.isInstalled.2"))) Then
	 javaws12Installed = 0
  Else
	 javaws12Installed = 1
  End If
  If Not(IsObject(CreateObject("JavaWebStart.isInstalled.1.4.2.0"))) Then
	 javaws142Installed = 0
  Else
	 javaws142Installed = 1
  End If  
End If
</SCRIPT>

<SCRIPT LANGUAGE="JavaScript">
/* Note that the logic below always launches the JNLP application */
/* if the browser is Gecko based. This is because it is not possible */
/* to detect MIME type application/x-java-jnlp-file on Gecko-based browsers. */
if (javawsInstalled || javaws12Installed || javaws142Installed || (navigator.userAgent.indexOf("Gecko") !=-1)) {
	document.write("<Table>");
	document.write("<TR height = 50>");
	document.write("<TD colspan = 2>");
	document.write("<h2><font color = navy>Cannon Technologies WebStart Clients</font></h2)");
	document.write("</td>");
	document.write("</TR>");
	
	document.write("<TR height = 50>");
	document.write("<TD>");
	document.write("<a href=esub-editor.jnlp><IMG src = \"images/esubEditorIcon.gif\" height = 40 width = 40 border = \"0\"></a>");
	document.write("</td>");
	document.write("<td>");
	document.write("<a href=esub-editor.jnlp>Esub Editor</a> Yukon client application to manipulate Esubstation drawings<br>");
	document.write("</td>");
	document.write("</tr>");


	document.write("<TR height = 50>");
	document.write("<TD>");
	document.write("<a href=trending.jnlp><IMG src = \"images/GraphIcon.gif\" height = 40 width = 40 border = \"0\"></a>");
	document.write("</td>");
	document.write("<td>");
	document.write("<a href=trending.jnlp>Trending</a> Yukon client application to create and view trending data<br>");
	document.write("</td>");
	document.write("</tr>");

	document.write("<TR height = 50>");
	document.write("<TD>");
	document.write("<a href=dbeditor.jnlp><IMG src = \"images/dbEditorIcon.gif\" height = 40 width = 40 border = \"0\"></a>");
	document.write("</td>");
	document.write("<td>");
	document.write("<a href=dbeditor.jnlp>DBEditor</a> Yukon client application to edit the Yukon database <br>");
	document.write("</td>");
	document.write("</tr>");
	
	document.write("<TR height = 50>");
	document.write("<TD>");
	document.write("<a href=tdc.jnlp><IMG src = \"images/tdcIcon.gif\" height = 40 width = 40 border = \"0\"></a>");
	document.write("</td>");
	document.write("<td>");
	document.write("<a href=tdc.jnlp>TDC</a> Yukon client application to view tabular data<br>");
	document.write("</td>");
	document.write("</tr>");

	document.write("</table>");
} else {
	document.write("Click ");
	document.write("<a href=download.jsp>here</a> ");
	document.write("to download and install the Java Runtime Environment (JRE).<br>");
}
</SCRIPT>

<script type="text/javascript">
Event.observe(window, 'load', function() { new CtiNonScrollTable('areaTable','areaHeaderTable');});
</script>

	</cti:titledContainer>
</cti:standardPage>
