<html>
<head>
<%@ include file="../include/user_header.jsp" %>
<!-- Find all the versacom serial groups associated with this operator -->
<!-- a serial address of 0 indicates that we should NOT display the serial number text field -->

<%
  boolean showSerialNumberTextField = false;
  int textFieldGroupID = -1;

  String sql = "SELECT GM.CHILDID FROM CUSTOMERLOGINSERIALGROUP CLSG,GENERICMACRO GM WHERE GM.OWNERID=CLSG.LMGROUPID AND CLSG.LOGINID=" + liteYukonUserID  + " ORDER BY GM.CHILDORDER";

  Object[][] serialGroupIDs = com.cannontech.util.ServletUtil.executeSQL( dbAlias, sql, new Class[] { Integer.class } );
  Object[][] nameSerial = null;

  if( serialGroupIDs != null ) {
    sql = "SELECT PAO.PAONAME,LMGV.SERIALADDRESS,LMGV.DEVICEID FROM YUKONPAOBJECT PAO,LMGROUPVERSACOM LMGV WHERE PAO.PAOBJECTID=LMGV.DEVICEID ";

    for( int i = 0; i < serialGroupIDs.length; i++ )
    {
      if( i == 0 ) {
        sql += "AND (LMGV.DEVICEID=" + serialGroupIDs[i][0] + " ";
      }
      else {
        sql += " OR LMGV.DEVICEID=" + serialGroupIDs[i][0] + " ";
      }
    }
    if( serialGroupIDs.length > 0)
      sql += " )";

    nameSerial = com.cannontech.util.ServletUtil.executeSQL( dbAlias, sql, new Class[] { String.class, Integer.class, Integer.class } );

    if( nameSerial != null )
    {
      for( int i = 0; i < nameSerial.length; i++ ) {
        if( ((Integer) nameSerial[i][1]).intValue() == 0 ) {
          showSerialNumberTextField = true;
          textFieldGroupID = ((Integer) nameSerial[i][2]).intValue();
        }
      }
    }
  }

  String prevSerialNumber = request.getParameter("sn");
%>

<SCRIPT LANGUAGE="JAVASCRIPT" TYPE="TEXT/JAVASCRIPT">
<!-- Hide the script from older browsers
function validForm(form)
{
  form.serialNumber.value = 0;
  form.groupid.value = 0;

  // did the user select a serial num or type it in?
  for( i = 0; i < availablesn.length; i++ ) {
    if( form.serialNumberField.value == availablesn[i] ) {
      form.groupid.value=availablegp[i];
      break;
    }
  }

  if( form.groupid.value == 0 ) {
    form.serialNumber.value = form.serialNumberField.value;
  }
  
  return true;
}
//End hiding script -->
</SCRIPT>

<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="150" height="102" background="../../WebConfig/yukon/MomWide.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="609" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>" defaultvalue="yukon/DemoHeader.gif"/>">&nbsp;</td>
              </tr>
              <tr> 
                <td width="265" height="28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Switch Commands </td>
                <td width="253" valign="middle">&nbsp;</td>
                <td width="58" valign="middle">&nbsp;</td>
                <td width="57" valign="middle"> 
                  <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log Off</a>&nbsp;</span></div>
                </td>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td>
    <%
    if( nameSerial != null ) {
    %>  
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center" bordercolor="0">
        <tr> 
          <td width="150" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
          <td width="609" bgcolor="#000000" height="1"></td>
		  <td width="1" bgcolor="#000000" height="1"></td>
        </tr>
        <tr> 
          <td  valign="top" width="150"> 
            <table width="150" border="0" cellspacing="0" cellpadding="0" height="200">
              <tr> 
                <td valign="top"> 
                  <% String pageName = "switch_commands.jsp"; %>
          		  <%@ include file="include/nav.jsp" %>
                </td>
              </tr>
            </table>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <FORM name="switchform" METHOD="POST" ACTION="<%=request.getContextPath()%>/servlet/SwitchCommand" onSubmit="return validForm(this)">
          <td width="609" valign="top" bgcolor="#FFFFFF"> 
            <p><br>
            <center>
              <p align="center" class="TitleHeader">INDIVIDUAL SWITCH COMMANDS</p>
            </center>
            <table width="345" border="0" cellspacing="0" cellpadding="0" align="center">
              <tr>
                <td width="100%">
                  <table border="0" width="100%" cellspacing="0" cellpadding="0">
                    <tr>
                      <td width="100%" align="center">
                        <p class="MainText" align="center">Enter or select a switch serial number:<br>
                        <input name="serialNumberField" size="9" maxlength="9" type="text" 
                          <% if( prevSerialNumber != null ) { %><%= "value=\"" + prevSerialNumber + "\""%><% } %>>
                        &nbsp;&nbsp;
                        <select name="selectbox" onChange="changecontent(this)"><option> </option>
                        <%
                        if( nameSerial != null ) {
                          for( int i = 0; i < nameSerial.length; i++ ) {
                            if( ((Integer) nameSerial[i][1]).intValue() != 0 ) {
                          %>
                          <option><%= nameSerial[i][1] %></option>
                          <%}
                          }
                        }
                        %>
                        </select>
                        <br>
                        </p>
                      </td>
                    </tr>
                    <tr></tr>
                  </table>
                </td>
              </tr>
            </table>
            
<script language="JavaScript">
/*
Drop down messages script
By JavaScript Kit (http://javascriptkit.com)
Over 400+ free scripts here!
*/

//change contents of message box, where the first one corresponds with the first drop down box, second with second box etc
var availablesn=new Array()
var availablegp=new Array()

availablesn[0]=''

<%
if( nameSerial != null ) {
  for( int i = 0; i < nameSerial.length; i++ ) {
    if( ((Integer) nameSerial[i][1]).intValue() != 0 ) {
      out.println("availablesn[" + (i+1) + "]='" + nameSerial[i][1] + "'");
      out.println("availablegp[" + (i+1) + "]='" + nameSerial[i][2] + "'");
    }
  }
}
%>

//don't edit pass this line

function changecontent(which){
document.switchform.serialNumberField.value=availablesn[which.selectedIndex]
}

document.switchform.serialNumberField.value=availablesn[document.switchform.selectbox.selectedIndex]
</script>
            <p>
            <div align="center"> 
            <span class="MainText">Select one of the following functions:</span>
            <table width="345" border="1" cellspacing="0" cellpadding="0">
              <tr>
                <td width="100%">
                  <table width="100%" border="1" cellspacing="0" cellpadding="3">
                    <tr>
                      <td width="12%" valign="TOP" class="TableCell">&nbsp;
                        <input type="radio" value="SHED Time" name="function" >
                      </td>
                      <td width="31%" valign="TOP" class="TableCell">&nbsp;SHED Time:</td>
                      <td width="57%" valign="TOP" class="TableCell">
                        <table width="182" border="0" cellspacing="0" cellpadding="2">
                          <tr>
                            <td width="87" class="TableCell">
                              <p align=RIGHT>&nbsp;Time:
                            </td>
                            <td width="87" class="TableCell">
                              <select name="time">
                              <option SELECTED>1 min
                              <option>1 sec
                              <option>5 min
                              <option>10 min
                              <option>20 min
                              <option>30 min
                              <option>1 hr
                              <option>4 hr
                              <option>8 hr
                              </select>
                            </td>
                          </tr>
                          <tr>
                            <td width="87" class="TableCell">
                              <p align=RIGHT>&nbsp;Relay #:
                            </td>
                            <td width="87" class="TableCell">
                              <select name="shedRelayNumber">
                              <option SELECTED>1
                              <option>2
                              <option>3
                              </select>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                    <tr>
                      <td width="12%" valign="TOP" class="TableCell">&nbsp;
                        <input type="radio" value="Restore" name="function">
                      </td>
                      <td width="31%" valign="TOP" class="TableCell"> &nbsp;Restore:</td>
                      <td width="57%" valign="TOP" class="TableCell">
                        <table width="182" border="0" cellspacing="0" cellpadding="2">
                          <tr>
                            <td width="87" class="TableCell">
                              <p align=RIGHT>&nbsp;Relay #:
                            </td>
                            <td width="87" class="TableCell">
                              <select name="restoreRelayNumber">
                              <option SELECTED>1
                              <option>2
                              <option>3
                              </select>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                    <tr>
                      <td width="12%" valign="TOP" class="TableCell">&nbsp;
                        <input type="radio" value="Cycle Rate" name="function">
                      </td>
                      <td width="31%" valign="TOP" class="TableCell"> &nbsp;Cycle Rate:</td>
                      <td width="57%" valign="TOP" class="TableCell">
                        <table width="182" border="0" cellspacing="0" cellpadding="2">
                          <tr>
                            <td width="87" class="TableCell">
                              <p align=RIGHT>&nbsp;Percent:
                            </td>
                            <td width="87" class="TableCell">
                              <select name="percent">
                              <%
                              for (int i = 1; i < 21; i++) {
                                if (i == 10)
                                  out.println("<option selected>" + (i*5));
                                else
                                  out.println("<option>" + (i*5));
                              }
                              %>
                              </select>
                            </td>
                          </tr>
                          <tr> 
                            <td width="87" class="TableCell">
                              <p align=RIGHT>&nbsp;Period (min):
                            </td>
                            <td width="87" class="TableCell">
                              <select name="period">
                              <%
                              for (int i = 1; i < 13; i++) {
                                if (i == 6)
                                  out.println("<option selected>" + (i*5));
                                else
                                  out.println("<option>" + (i*5));
                              }
                              %>
                              </select>
                            </td>
                          </tr>
                          <tr>
                            <td width="87" class="TableCell">
                              <p align=RIGHT>&nbsp;# of Periods:
                            </td>
                            <td width="87" class="TableCell">
                              <select name="periodCount">
                              <%
                              for (int i = 1; i < 64; i++) {
                                if( i == 8)
                                  out.println("<option selected>" + i);
                                else
                                  out.println("<option>" + i);
                              }
                              %>
                              </select>
                            </td>
                          </tr>
                          <tr>
                            <td width="87" class="TableCell">
                              <p align=RIGHT>&nbsp;Relay #:
                             </td>
                            <td width="87" class="TableCell">
                              <select name="startRelayNumber">
                                <option SELECTED>1
                                <option>2
                                <option>3
                              </select>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                    <tr>
                      <td width="12%" valign="TOP" class="TableCell">&nbsp;
                        <input type="radio" value="Stop Cycle" name="function">
                      </td>
                      <td width="31%" valign="TOP" class="TableCell">&nbsp;Stop Cycle:</td>
                      <td width="57%" valign="TOP" class="TableCell">
                        <table width="182" border="0" cellspacing="0" cellpadding="2">
                          <tr>
                            <td width="87" class="TableCell">
                              <p align=RIGHT>&nbsp;Relay #:
                            </td>
                            <td width="87" class="TableCell">
                              <select name="stopRelayNumber">
                              <option SELECTED>1
                              <option>2
                              <option>3
                              </select>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                  <table width="100%" border="1" cellspacing="0" cellpadding="3">
                    <tr>
                      <td width="12%" class="TableCell">&nbsp;
                        <input type="radio" value="Service Enable" name="function">
                      </td>
                      <td width="88%" valign="TOP" class="TableCell"> &nbsp;Service Enable</td>
                    </tr>
                    <tr>
                      <td width="12%" class="TableCell">&nbsp;
                        <input type="radio" value="Service Disable" name="function">
                      </td>
                      <td width="88%" valign="TOP" class="TableCell"> &nbsp;Service Disable</td>
                    </tr>
                    <tr>
                      <td width="12%" class="TableCell">&nbsp;
                        <input type="radio" value="Cap Control Open" name="function">
                      </td>
                      <td width="88%" valign="TOP" class="TableCell"> &nbsp;Cap Control Open</td>
                    </tr>
                    <tr>
                      <td width="12%" class="TableCell">&nbsp;
                        <input type="radio" value="Cap Control Closed" name="function" >
                      </td>
                      <td width="88%" valign="TOP" class="TableCell"> &nbsp;Cap Control Closed</td>
                    </tr>
                  </table>
                </td>
              </tr>
            </table>
            <br>
            <input type="submit" name="sendButton" value="Send">
            </div>
            <p align="center">&nbsp;</p>\
          </td>

          <input type="hidden" name="serialNumber" value="0">
          <input type="hidden" name="groupid" value="0">
          <input type="hidden" name="REDIRECT" value="<%=request.getContextPath()%>/user/CILC/switch_commands.jsp">
          
          </form>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    <%
    } // end if name serial != null
    else {
    %>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center" bordercolor="0">
        <tr>
          <td width="150" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
          <td width="609" bgcolor="#000000" height="1"></td>
		  <td width="1" bgcolor="#000000" height="1"></td>
        </tr>
      </table>
    <%
    }%>
    </td>
  </tr>
</table>
<br>
</body>
</html>