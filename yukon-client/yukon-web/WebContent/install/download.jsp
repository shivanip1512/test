<%
 StringBuffer codebaseBuffer = new StringBuffer();
 codebaseBuffer.append(!request.isSecure() ? "http://" : "https://");
 codebaseBuffer.append(request.getServerName());
 if (request.getServerPort() != (!request.isSecure() ? 80 : 443))
 {
   codebaseBuffer.append(':');
   codebaseBuffer.append(request.getServerPort());
 }
 codebaseBuffer.append('/');
%>

<html>
<BODY>


<OBJECT CODEBASE="<%=codebaseBuffer.toString()%>install/include/jinstall-1_4_2_05-windows-i586.cab" 
CLASSID="clsid:5852F5ED-8BF4-11D4-A245-0080C6F74284" HEIGHT=0 WIDTH=0>
<PARAM NAME="app" VALUE= "<%=codebaseBuffer.toString()%>install/dbeditor.jnlp">
<PARAM NAME="back" VALUE="true">
<!-- Alternate HTML for browsers which cannot instantiate the object -->
<A HREF="<%=codebaseBuffer.toString()%>install/include/j2re-1_4_2_05-windows-i586-p.exe">Download</A>
and install the Java Runtime Environment (JRE) manually
</OBJECT>
</BODY>
</html>