<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%
	String faqLink = ServletUtils.getCustomerFAQLink(liteEC);
	boolean inherited = (faqLink == null) && (liteEC.getCustomerFAQs() == null);
	boolean customized = (faqLink == null) && (liteEC.getCustomerFAQs() != null);
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
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

function changeSource(form, source) {
	form.FAQLink.disabled = (source != "Link");
	var customized = <%= customized %> && (source == "Customized");
	form.FAQSubjects.disabled = !customized;
	form.MoveUp.disabled = !customized;
	form.MoveDown.disabled = !customized;
	form.Save.disabled = !customized;
	form.Edit.disabled = !customized;
	form.New.disabled = !customized;
	form.Delete.disabled = !customized;
	form.DeleteAll.disabled = !customized || form.FAQSubjects.options[0].value < 0;
	if (customized) setButtonStatus(form);
}

function init() {
<%
	String source = null;
	if (faqLink != null)
		source = "Link";
	else if (inherited)
		source = "Inherited";
	else
		source = "Customized";
%>
	changeSource(document.form1, "<%= source %>");
}

function moveUp(form) {
	var subjects = form.FAQSubjects;
	var idx = subjects.selectedIndex;
	if (idx > 0) {
		var oOption = subjects[idx];
		subjects.options.remove(idx);
		subjects.options.add(oOption, idx-1);
		setContentChanged(true);
	}
}

function moveDown(form) {
	var subjects = form.FAQSubjects;
	var idx = subjects.selectedIndex;
	if (idx >= 0 && idx < subjects.options.length - 1) {
		var oOption = subjects[idx];
		subjects.options.remove(idx);
		subjects.options.add(oOption, idx+1);
		setContentChanged(true);
	}
}

function submitFAQSubjects(form) {
	var subjects = form.FAQSubjects;
	if (subjects.options[0].value >= 0) {
		form.action.value = "UpdateFAQSubjectList";
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
                            <table width="100%" border="0" cellspacing="0" cellpadding="3" align="center" class="MainText">
                              <input type="hidden" name="action" value="UpdateFAQSource">
                              <tr> 
                                <td width="75%"> 
                                  <input type="radio" name="Source" value="Inherited" onclick="changeSource(this.form, this.value); setContentChanged(true);" <% if (inherited) out.print("checked"); %>>
                                  Inherited<br>
                                  <input type="radio" name="Source" value="Link" onclick="changeSource(this.form, this.value); setContentChanged(true);" <% if (faqLink != null) out.print("checked"); %>>
                                  Use a link to your company's website: 
                                  <input type="text" name="FAQLink" size="30" value="<%= StarsUtils.forceNotNone(faqLink) %>" onchange="setContentChanged(true)">
                                  <br>
                                  <input type="radio" name="Source" value="Customized" onclick="changeSource(this.form, this.value); setContentChanged(true);" <% if (customized) out.print("checked"); %>>
                                  Define the FAQ subjects below:</td>
                                <td width="25%"> 
                                  <input type="submit" name="Submit" value="Submit">
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
                                        <input type="button" name="MoveUp" value="Move Up" onclick="moveUp(this.form)">
                                        <br>
                                        <input type="button" name="MoveDown" value="Move Down" onclick="moveDown(this.form)">
                                        <br>
                                        <input type="button" name="Save" value="Save" onclick="submitFAQSubjects(this.form)">
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
                                        <input type="button" name="Edit" value="Edit" onclick="editFAQSubject(this.form)">
                                      </td>
                                      <td width="15%"> 
                                        <input type="button" name="New" value="New" onclick="newFAQSubject(this.form)">
                                      </td>
                                      <td width="15%"> 
                                        <input type="button" name="Delete" value="Delete" onclick="deleteFAQSubject(this.form)">
                                      </td>
                                      <td width="55%"> 
                                        <input type="button" name="DeleteAll" value="Delete All" onclick="deleteAllFAQSubjects(this.form)">
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
                      <input type="button" name="Back" value="Back" onclick="if (warnUnsavedChanges()) location.href='AdminTest.jsp'">
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
