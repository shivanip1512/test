<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
<script language="JavaScript">

var subjectIDs = new Array();
<%
	for (int i = 0; i < customerFAQs.getStarsCustomerFAQGroupCount(); i++) {
		StarsCustomerFAQGroup faqGroup = customerFAQs.getStarsCustomerFAQGroup(i);
%>
	subjectIDs[<%= i %>] = <%= faqGroup.getSubjectID() %>;
<%	} %>

function setButtonStatus(form) {
	var subjects = form.FAQSubjects;
	var disabled = (subjects.selectedIndex < 0 || subjects.options[0].value < 0);
	form.Edit.disabled = disabled;
	form.Delete.disabled = disabled;
}

function init() {
	var form = document.form1;
	setButtonStatus(form);
	form.DeleteAll.disabled = form.FAQSubjects.options[0].value < 0;
}

function moveUp(form) {
	var subjects = form.FAQSubjects;
	var idx = subjects.selectedIndex;
	if (idx > 0) {
		var oOption = subjects[idx];
		subjects.options.remove(idx);
		subjects.options.add(oOption, idx-1);
	}
}

function moveDown(form) {
	var subjects = form.FAQSubjects;
	var idx = subjects.selectedIndex;
	if (idx >= 0 && idx < subjects.options.length - 1) {
		var oOption = subjects[idx];
		subjects.options.remove(idx);
		subjects.options.add(oOption, idx+1);
	}
}

function submitFAQSubjects(form) {
	var subjects = form.FAQSubjects;
	if (subjects.options[0].value >= 0) {
		form.action.value = "UpdateFAQSubjects";
		for (i = 0; i < subjects.options.length; i++) {
			var html = '<input type="hidden" name="SubjectIDs" value="' + subjectIDs[subjects.options[i].value] + '">';
			form.insertAdjacentHTML("beforeEnd", html);
		}
		form.submit();
	}
}

function editFAQSubject(form) {
	var subjects = form.FAQSubjects;
	if (subjects.selectedIndex >= 0)
		location.href = "FAQSubject.jsp?Subject=" + subjects.value;
}

function deleteFAQSubject(form) {
	var subjects = form.FAQSubjects;
	if (subjects.selectedIndex >= 0) {
		if (!confirm("Are you sure you want to delete the FAQ subject?")) return;
		form.action.value = "DeleteFAQSubject";
		form.SubjectID.value = subjectIDs[subjects.value];
		form.submit();
	}
}

function deleteAllFAQSubjects(form) {
	var subjects = form.FAQSubjects;
	if (subjects.options[0].value >= 0) {
		if (!confirm("Are you sure you want to delete all FAQ subjects?")) return;
		form.action.value = "DeleteFAQSubject";
		form.SubjectID.value = -1;
		form.submit();
	}
}

function newFAQSubject(form) {
	var subjects = form.FAQSubjects;
	var idx = (subjects.options[0].value < 0)? 0 : subjects.options.length;
	location.href = "FAQSubject.jsp?Subject=" + idx;
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0" onload="init()">
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
          <td  valign="top" width="101">&nbsp;</td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" height="400" valign="top" bgcolor="#FFFFFF">
            <div align="center"> <br>
              <span class="TitleHeader">ADMINISTRATION - CUSTOMER FAQS</span><br>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
              
              <form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/StarsAdmin">
                <table width="600" border="1" cellspacing="0" cellpadding="0">
                  <tr>
                    <td>
                      <table width="100%" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                          <td>
                            <table width="100%" border="0" cellspacing="0" cellpadding="3" align="center" class="TableCell">
                              <input type="hidden" name="action" value="UpdateFAQLink">
<%
	String faqLink = ServerUtils.forceNotNone(AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_LINK_FAQ));
	boolean customizedFAQ = faqLink.length() > 0;
	String checkedStr = (customizedFAQ) ? "checked" : "";
	String disabledStr = (customizedFAQ) ? "" : "disabled";
%>
                              <tr> 
                                <td width="75%"> 
                                  <input type="checkbox" name="CustomizedFAQ" value="true" onClick="this.form.FAQLink.disabled = !this.checked" <%= checkedStr %>>
                                  Use a link to your company's website: 
                                  <input type="text" name="FAQLink" size="30" value="<%= faqLink %>" <%= disabledStr %>>
                                </td>
                                <td width="25%"> 
                                  <input type="submit" name="SubmitFAQLink" value="Submit">
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                        <tr>
                          <td>
                            <table width="100%" border="0" cellspacing="0" cellpadding="3">
							  <input type="hidden" name="SubjectID" value="0">
                              <tr>
                                <td class="HeaderCell">Edit FAQ Subjects</td>
                              </tr>
                              <tr>
                                <td>
                                  <table width="100%" border="0" cellspacing="0" cellpadding="3">
                                    <tr valign="middle"> 
                                      <td class="TableCell" width="10%">FAQ Subjects:</td>
                                      <td class="TableCell" width="65%"> 
                                        <select name="FAQSubjects" size="5" style="width:200" onchange="setButtonStatus(this.form)">
<%
	if (customerFAQs.getStarsCustomerFAQGroupCount() == 0) {
%>
                                          <option value="-1">&lt;no FAQ Subjects&gt;</option>
<%	}
	else {
		for (int i = 0; i < customerFAQs.getStarsCustomerFAQGroupCount(); i++) {
			StarsCustomerFAQGroup faqGroup = customerFAQs.getStarsCustomerFAQGroup(i);
%>
                                          <option value="<%= i %>"><%= faqGroup.getSubject() %></option>
<%		}
	}
%>
                                        </select>
                                      </td>
                                      <td class="TableCell" width="25%"> 
                                        <input type="button" name="MoveUp" value="Move Up" onClick="moveUp(this.form)">
                                        <br>
                                        <input type="button" name="MoveDown" value="Move Down" onClick="moveDown(this.form)">
                                        <br>
                                        <input type="button" name="Save" value="Save" onClick="submitFAQSubjects(this.form)">
                                      </td>
                                    </tr>
                                  </table>
                                </td>
                              </tr>
                              <tr>
                                <td>
                                  <table width="100%" border="0" cellspacing="0" cellpadding="3" align="center">
                                    <tr> 
                                      <td width="15%"> 
                                        <input type="button" name="Edit" value="Edit" onClick="editFAQSubject(this.form)">
                                      </td>
                                      <td width="15%"> 
                                        <input type="button" name="New" value="New" onClick="newFAQSubject(this.form)">
                                      </td>
                                      <td width="15%"> 
                                        <input type="button" name="Delete" value="Delete" onClick="deleteFAQSubject(this.form)">
                                      </td>
                                      <td width="55%"> 
                                        <input type="button" name="DeleteAll" value="Delete All" onClick="deleteAllFAQSubjects(this.form)">
                                      </td>
                                    </tr>
                                  </table>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                </table>
                <table width="600" border="0" cellspacing="0" cellpadding="5" align="center">
                  <tr> 
                    <td width="290" align="right">&nbsp; </td>
                    <td width="205">&nbsp; </td>
                    <td width="75" align="right"> 
                      <input type="button" name="Done" value="Done" onClick="location.href='AdminTest.jsp'">
                    </td>
                  </tr>
                </table>
              </form>
            </div>
          </td>
        <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
