<html>
<head>
<title>Graph Settings</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script langauge = "Javascript" src = "updateGraph.js"></script>
</head>

<body bgcolor="#000000" text="#000000"><form name = "MForm">
  <table width="100%" border="0" cellspacing="0" cellpadding="9" height="331">
    <tr>
    <td>
      <table width="100%" border="2" cellspacing="0" cellpadding="3" height="273" bgcolor = "#FFFFFF">
        <tr> 
          <td height="2" width="50%" valign = "top" bgcolor = "#CCCCCC"><b><font size="2" face="Arial, Helvetica, sans-serif">Graph 
            Settings</font></b></td>
        </tr>
        <tr> 
          <td height="103" width="50%" valign = "top"> 
            <table width="100%" border="0" cellspacing="3" cellpadding="0">
              <tr> 
                <td width="23%" align = "Right"><font size="2" face="Arial, Helvetica, sans-serif">Graph</font></td>
                <td width="77%"><font face="Arial, Helvetica, sans-serif" size="2">Type:</font></td>
              </tr>
              <tr> 
                <td width="23%" align = "right"> 
                  <input type="radio" name="view" value="0" checked>
                </td>
                <td width="77%"><font face="Arial, Helvetica, sans-serif" size="2" >Line</font></td>
              </tr>
              <tr> 
                <td width="23%" align = "Right"> 
                  <input type="radio" name="view" value="3">
                </td>
                <td width="77%"><font face="Arial, Helvetica, sans-serif" size="2">Step 
                  Line</font></td>
              </tr>
              <tr> 
                <td width="23%" align = "right"> 
                  <input type="radio" name="view" value="1">
                </td>
                <td width="77%"><font face="Arial, Helvetica, sans-serif" size="2">Bar</font></td>
              </tr>
              <tr> 
                <td width="23%" align = "right" height="2"> 
                  <input type="radio" name="view" value="2">
                </td>
                <td width="77%" height="2"><font face="Arial, Helvetica, sans-serif" size="2">3DBar</font></td>
              </tr>
            </table>
          </td>
        </tr>
        <tr> 
          <td height="103" width="50%" valign = "top"> 
            <table width="100%" border="0" cellspacing="3" cellpadding="0">
              <tr> 
                <td width="23%" align = "Right"><font face="Arial, Helvetica, sans-serif" size="2">Display</font></td>
                <td width="77%"><font face="Arial, Helvetica, sans-serif" size="2">Range:</font></td>
              </tr>
              <tr> 
                <td width="23%" align = "right"> 
                  <input type="radio" name="period" value="Today" >
                </td>
                <td width="77%"><font face="Arial, Helvetica, sans-serif" size="2" >Today</font></td>
              </tr>
              <tr> 
                <td width="23%" align = "Right"> 
                  <input type="radio" name="period" value="Yesterday">
                </td>
                <td width="77%"><font face="Arial, Helvetica, sans-serif" size="2">Yesterday</font></td>
              </tr>
              <tr> 
                <td width="23%" align = "right"> 
                  <input type="radio" name="period" value="Prev 2 Days" checked>
                </td>
                <td width="77%"><font face="Arial, Helvetica, sans-serif" size="2">Previous 
                  2 Days</font></td>
              </tr>
              <tr> 
                <td width="23%" align = "right" height="2"> 
                  <input type="radio" name="period" value="Prev 3 Days">
                </td>
                <td width="77%" height="2"><font face="Arial, Helvetica, sans-serif" size="2">Previous 
                  3 Days</font></td>
              </tr>
              <tr> 
                <td width="23%" align = "right" height="2"> 
                  <input type="radio" name="period" value="Prev 7 Days">
                </td>
                <td width="77%" height="2"><font face="Arial, Helvetica, sans-serif" size="2">Previous 
                  7 Days</font></td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td align = "center" valign = "bottom">
      <input type="submit" name="Submit2" value="Update Graph" onclick = "update()">
      <input type="submit" name="Submit" value="Cancel" onclick = "Javascript:window.close()">
    </td>
  </tr>
</table></form>
</body>
</html>
