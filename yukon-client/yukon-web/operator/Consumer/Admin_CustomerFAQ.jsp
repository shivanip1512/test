<%@ include file="StarsHeader.jsp" %>
<% if (!AuthFuncs.checkRoleProperty(lYukonUser, ConsumerInfoRole.SUPER_OPERATOR)) { response.sendRedirect("../Operations.jsp"); return; } %>
<%
	StarsCustomerFAQGroup group = null;
	int grpIdx = Integer.parseInt( request.getParameter("Subject") );
	if (grpIdx < customerFAQs.getStarsCustomerFAQGroupCount())
		group = customerFAQs.getStarsCustomerFAQGroup(grpIdx);
	else {
		group = new StarsCustomerFAQGroup();
		group.setSubjectID(-1);
		group.setSubject("");
	}
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
<script language="JavaScript">
var questions = new Array();
var answers = new Array();
<%
	for (int i = 0; i < group.getStarsCustomerFAQCount(); i++) {
		StarsCustomerFAQ faq = group.getStarsCustomerFAQ(i);
%>
	questions[<%= i %>] = "<%= faq.getQuestion().replaceAll("\"", "'") %>";
	answers[<%= i %>] = "<%= faq.getAnswer().replaceAll("\"", "'") %>";
<%	} %>

function getFAQString(idx) {
	if (questions[idx] == null) return "";
	var qStr = (questions[idx].length <= 25) ? questions[idx] : questions[idx].substr(0,22).concat("...");
	var aStr = (answers[idx].length <= 25) ? answers[idx] : answers[idx].substr(0,22).concat("...");
	return "Q: " + qStr + " A: " + aStr;
}

var curIdx = questions.length;

function showFAQ(form) {
	var faqs = form.CustomerFAQs;
	if (faqs.selectedIndex < 0 || faqs.value == -1) {
		curIdx = questions.length;
		form.Question.value = "";
		form.Answer.value = "";
	}
	else {
		curIdx = faqs.selectedIndex;
		form.Question.value = questions[curIdx];
		form.Answer.value = answers[curIdx];
	}
}

function moveUp(form) {
	var faqs = form.CustomerFAQs;
	var idx = faqs.selectedIndex;
	if (idx >= 0 && faqs.value != -1) {
		if (idx > 0) {
			var oOption = faqs.options[idx];
			faqs.options.remove(idx);
			faqs.options.add(oOption, idx-1);
			var value = questions[idx];
			questions[idx] = questions[idx-1];
			questions[idx-1] = value;
			value = answers[idx];
			answers[idx] = answers[idx-1];
			answers[idx-1] = value;
			showFAQ(form);
		}
	}
}

function moveDown(form) {
	var faqs = form.CustomerFAQs;
	var idx = faqs.selectedIndex;
	if (idx >= 0 && faqs.value != -1) {
		if (idx < faqs.options.length - 2) {
			var oOption = faqs.options[idx];
			faqs.options.remove(idx);
			faqs.options.add(oOption, idx+1);
			var value = questions[idx];
			questions[idx] = questions[idx+1];
			questions[idx+1] = value;
			value = answers[idx];
			answers[idx] = answers[idx+1];
			answers[idx+1] = value;
			showFAQ(form);
		}
	}
}

function deleteFAQ(form) {
	var faqs = form.CustomerFAQs;
	var idx = faqs.selectedIndex;
	if (idx >= 0 && faqs.value != -1) {
		faqs.options.remove(idx);
		questions.splice(idx, 1);
		answers.splice(idx, 1);
		showFAQ(form);
	}
}

function saveFAQ(form) {
	questions[curIdx] = form.Question.value;
	answers[curIdx] = form.Answer.value;
	var faqs = form.CustomerFAQs;
	if (curIdx == faqs.options.length - 1) {
		var oOption = document.createElement("OPTION");
		faqs.options.add(oOption, curIdx);
		faqs.selectedIndex = curIdx;
	}
	faqs.options[curIdx].innerText = getFAQString(curIdx);
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="ConsumerImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
                  <td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Customer 
                    Account Information&nbsp;&nbsp;</td>
                  
                <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="Main"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="Main"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log 
                      Off</a>&nbsp;</span></div>
                  </td>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          </tr>
      </table>
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
          <td  valign="top" width="101">&nbsp;</td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" height="400" valign="top" bgcolor="#FFFFFF">
              
            <div align="center">
              <% String header = "ADMINISTRATION - CUSTOMER FAQS"; %>
              <%@ include file="InfoSearchBar2.jsp" %>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
            </div>
			
			<form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/StarsAdmin">
              <table width="600" border="1" cellspacing="0" cellpadding="0" align="center">
                <tr> 
                  <td class="HeaderCell">Edit Customer FAQs</td>
                </tr>
                <tr> 
                  <td height="67"> 
                    <table width="100%" border="0" cellspacing="0" cellpadding="5">
                      <input type="hidden" name="action" value="UpdateCustomerFAQ">
                      <input type="hidden" name="SubjectID" value="<%= group.getSubjectID() %>">
                      <tr> 
                        <td width="15%" align="right" class="TableCell">Subject:</td>
                        <td width="85%" class="TableCell" colspan="2"> 
                          <input type="text" name="Subject" value="<%= group.getSubject() %>">
                        </td>
                      </tr>
                      <tr> 
                        <td width="15%" align="right" class="TableCell">FAQs:</td>
                        <td width="85%" class="TableCell" colspan="2"> 
                          <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                            <tr> 
                              <td width="75%" colspan="2"> 
                                <select name="CustomerFAQs" size="5" style="width:350" onchange="showFAQ(this.form)">
<%
	for (int i = 0; i < group.getStarsCustomerFAQCount(); i++) {
		StarsCustomerFAQ faq = group.getStarsCustomerFAQ(i);
		String qStr = (faq.getQuestion().length() <= 25) ? faq.getQuestion() : faq.getQuestion().substring(0,22).concat("...");
		String aStr = (faq.getAnswer().length() <= 25) ? faq.getAnswer() : faq.getAnswer().substring(0,22).concat("...");
		String faqStr = "Q: " + qStr + " A: " + aStr;
%>
                                  <option><%= faqStr %></option>
<%	} %>
                                  <option value="-1">&lt;New Customer FAQ&gt;</option>
                                </select>
                              </td>
                              <td width="25%"> 
                                <input type="button" name="MoveUp" value="Move Up" onclick="moveUp(this.form)">
                                <br>
                                <input type="button" name="MoveDown" value="Move Down" onclick="moveDown(this.form)">
                                <br>
                                <input type="button" name="Delete" value="Delete" onclick="deleteFAQ(this.form)">
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                      <tr> 
                        <td colspan="3" class="TableCell"> 
                          <hr>
                        </td>
                      </tr>
                      <tr> 
                        <td width="15%" class="TableCell" align="right">Question:</td>
                        <td width="65%" class="TableCell"> 
                          <input type="text" name="Question" size="55">
                        </td>
                        <td rowspan="2" class="TableCell" width="20%"> 
                          <input type="button" name="Save" value="Save" onClick="saveFAQ(this.form)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="15%" class="TableCell" align="right">Answer:</td>
                        <td width="65%" class="TableCell"> 
                          <input type="text" name="Answer" size="55">
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table>
              <table width="600" border="0" cellspacing="0" cellpadding="5" align="center">
                <tr>
                  <td width="290" align="right"> 
                    <input type="submit" name="Submit" value="Submit">
                  </td>
                  <td width="205"> 
                    <input type="reset" name="Cancel" value="Cancel">
                  </td>
                  <td width="75" align="right"> 
                    <input type="button" name="Done" value="Done" onclick="location.href='AdminTest.jsp'">
                  </td>
                </tr>
              </table>
            </form>
          </td>
        <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
