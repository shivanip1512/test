<%@ include file="include/StarsHeader.jsp" %>
<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <%@ include file="include/HeaderBar.jsp" %>
    </td>
  </tr>
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center" bordercolor="0">
        <tr> 
          <td width="101" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
          <td width="657" bgcolor="#000000" height="1"></td>
		  <td width="1" bgcolor="#000000" height="1"></td>
        </tr>
        <tr> 
          <td  valign="top" width="101">
		  <% String pageName = "FAQ.jsp"; %>
          <%@ include file="include/Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"><br>
              <% String header = "QUESTIONS - FAQ"; %>
              <%@ include file="include/InfoBar.jsp" %>
              <table width="600" border="0" cellpadding="0" cellspacing="0">
                <tr> 
                  <td> 
                    <hr>
                  </td>
                </tr>
              </table>
              <span class="MainText"><br></span>
              <table width="600" border="0" cellspacing="0" cellpadding="0">
                <tr> 
                  <form>
                    <td valign="top" class="MainText"> 
					  <a name="Top"></a>
<%
	for (int i = 0; i < customerFAQs.getStarsCustomerFAQGroupCount(); i++) {
		StarsCustomerFAQGroup group = customerFAQs.getStarsCustomerFAQGroup(i);
%>
                      <p class="TitleHeader" align="left"><%= group.getSubject() %></p>
                      <ul>
<%
		for (int j = 0; j < group.getStarsCustomerFAQCount(); j++) {
			StarsCustomerFAQ faq = group.getStarsCustomerFAQ(j);
%>
                        <li> 
                          <div align="left"><a href="#GROUP<%= i+1 %>_FAQ<%= j+1 %>"><%= faq.getQuestion() %></a></div>
                        </li>
<%
		}
%>
                      </ul>
<%
	}
%>
<%
	for (int i = 0; i < customerFAQs.getStarsCustomerFAQGroupCount(); i++) {
		StarsCustomerFAQGroup group = customerFAQs.getStarsCustomerFAQGroup(i);
%>
                        <hr>
                        <p class="TitleHeader" align="left"><%= group.getSubject() %></p>
<%
		for (int j = 0; j < group.getStarsCustomerFAQCount(); j++) {
			StarsCustomerFAQ faq = group.getStarsCustomerFAQ(j);
%>
                        <p class="TitleHeader" align="left"><i><a name="#GROUP<%= i+1 %>_FAQ<%= j+1 %>"></a>
                          <%= faq.getQuestion() %></i></p>
                        <p align="left"><%= faq.getAnswer() %><br>
                          <a href="#Top">[back to top]</a></p>
<%
		}
	}
%>
                    </td>
                  </form>
                </tr>
              </table>
              <br>
            </div>
            <p>&nbsp;</p>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<br>
</body>
</html>
