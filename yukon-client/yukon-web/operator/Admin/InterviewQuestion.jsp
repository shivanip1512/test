<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%
	ArrayList questions = new ArrayList();
	String qType = request.getParameter("Type");
	if (qType.equalsIgnoreCase("Exit")) {
		for (int i = 0; i < exitQuestions.getStarsExitInterviewQuestionCount(); i++)
			questions.add( exitQuestions.getStarsExitInterviewQuestion(i) );
	}
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<script language="JavaScript">
var questions = new Array();
var answerTypes = new Array();
<%
	for (int i = 0; i < questions.size(); i++) {
		StarsQuestionAnswer question = (StarsQuestionAnswer) questions.get(i);
%>
	questions[<%= i %>] = "<%= question.getQuestion().replaceAll("\"", "&quot;") %>".replace(/&quot;/g, "\"");
	answerTypes[<%= i %>] = <%= question.getAnswerType().getEntryID() %>;
<%	} %>

function getQuestionString(idx) {
	if (questions[idx] == null) return "";
	return (questions[idx].length <= 50) ? questions[idx] : questions[idx].substr(0,47).concat("...");
}

var curIdx = questions.length;

function showQuestion(form) {
	var qList = form.InterviewQuestions;
	if (qList.selectedIndex < 0 || qList.value == -1) {
		curIdx = questions.length;
		form.Question.value = "";
		form.AnswerType.selectedIndex = 0;
		form.Save.value = "Add";
	}
	else {
		curIdx = qList.selectedIndex;
		form.Question.value = questions[curIdx];
		form.AnswerType.value = answerTypes[curIdx];
		form.Save.value = "Save";
	}
}

function moveUp(form) {
	var qList = form.InterviewQuestions;
	var idx = qList.selectedIndex;
	if (idx >= 0 && qList.value != -1) {
		if (idx > 0) {
			var oOption = qList.options[idx];
			qList.options.remove(idx);
			qList.options.add(oOption, idx-1);
			var value = questions[idx];
			questions[idx] = questions[idx-1];
			questions[idx-1] = value;
			value = answerTypes[idx];
			answerTypes[idx] = answerTypes[idx-1];
			answerTypes[idx-1] = value;
			showQuestion(form);
		}
	}
}

function moveDown(form) {
	var qList = form.InterviewQuestions;
	var idx = qList.selectedIndex;
	if (idx >= 0 && qList.value != -1) {
		if (idx < qList.options.length - 2) {
			var oOption = qList.options[idx];
			qList.options.remove(idx);
			qList.options.add(oOption, idx+1);
			var value = questions[idx];
			questions[idx] = questions[idx+1];
			questions[idx+1] = value;
			value = answerTypes[idx];
			answerTypes[idx] = answerTypes[idx+1];
			answerTypes[idx+1] = value;
			showQuestion(form);
		}
	}
}

function deleteQuestion(form) {
	var qList = form.InterviewQuestions;
	var idx = qList.selectedIndex;
	if (idx >= 0 && qList.value != -1) {
		if (!confirm("Are you sure you want to delete this item?")) return;
		qList.options.remove(idx);
		questions.splice(idx, 1);
		answerTypes.splice(idx, 1);
		qList.selectedIndex = qList.options.length - 1;
		showQuestion(form);
	}
}

function deleteAllQuestions(form) {
	var qList = form.InterviewQuestions;
	if (qList.options[0].value >= 0) {
		if (!confirm("Are you sure you want to delete all items?")) return;
		for (idx = qList.options.length - 2; idx >= 0; idx--)
			qList.options.remove(idx);
		questions.splice(0, questions.length);
		answerTypes.splice(0, answerTypes.length);
		qList.selectedIndex = qList.options.length - 1;
		showQuestion(form);
	}
}

function saveQuestion(form) {
	questions[curIdx] = form.Question.value;
	answerTypes[curIdx] = form.AnswerType.value;
	var qList = form.InterviewQuestions;
	if (qList.options[curIdx].value == -1) {
		var oOption = document.createElement("OPTION");
		qList.options.add(oOption, curIdx);
		qList.selectedIndex = curIdx;
	}
	qList.options[curIdx].innerText = getQuestionString(curIdx);
	showQuestion(form);
}

function prepareSubmit(form) {
	for (i = 0; i < questions.length; i++) {
		var html = '<input type="hidden" name="Questions" value="' + questions[i].replace(/"/g, "&quot;") + '">';
		form.insertAdjacentHTML("beforeEnd", html);
		html = '<input type="hidden" name="AnswerTypes" value="' + answerTypes[i] + '">';
		form.insertAdjacentHTML("beforeEnd", html);
	}
}
</script>
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
          <td  valign="top" width="101">&nbsp;</td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" height="400" valign="top" bgcolor="#FFFFFF">
              
            <div align="center"> <br>
              <span class="TitleHeader">ADMINISTRATION - INTERVIEW QUESTIONS</span><br>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
            </div>
			
			<form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/StarsAdmin" onsubmit="prepareSubmit(this)">
              <table width="600" border="1" cellspacing="0" cellpadding="0" align="center">
                <tr> 
                  <td class="HeaderCell">Edit Interview Questions</td>
                </tr>
                <tr> 
                  <td height="67"> 
                    <table width="100%" border="0" cellspacing="0" cellpadding="5">
                      <input type="hidden" name="action" value="UpdateInterviewQuestions">
                      <input type="hidden" name="type" value="<%= qType %>">
                      <tr> 
                        <td width="15%" align="right" class="TableCell">Interview 
                          Type:</td>
                        <td width="85%" class="TableCell" colspan="2"><%= qType %> Interview</td>
                      </tr>
                      <tr> 
                        <td width="15%" align="right" class="TableCell">Questions:</td>
                        <td width="85%" class="TableCell" colspan="2"> 
                          <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                            <tr> 
                              <td width="75%" colspan="2"> 
                                <select name="InterviewQuestions" size="5" style="width:350" onchange="showQuestion(this.form)">
<%
	for (int i = 0; i < questions.size(); i++) {
		StarsQuestionAnswer question = (StarsQuestionAnswer) questions.get(i);
		String qStr = (question.getQuestion().length() <= 50) ? question.getQuestion() : question.getQuestion().substring(0,47).concat("...");
%>
                                  <option><%= qStr %></option>
<%	} %>
                                  <option value="-1">&lt;New Interview Question&gt;</option>
                                </select>
                              </td>
                              <td width="25%"> 
                                <input type="button" name="MoveUp" value="Move Up" onclick="moveUp(this.form)">
                                <br>
                                <input type="button" name="MoveDown" value="Move Down" onclick="moveDown(this.form)">
                                <br>
                                <input type="button" name="Delete" value="Delete" onclick="deleteQuestion(this.form)">
                                <br>
                                <input type="button" name="DeleteAll" value="Delete All" onClick="deleteAllQuestions(this.form)">
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
                        <td colspan="3" class="TableCell"> 
                          <table width="100%" border="0" cellspacing="0" cellpadding="0">
                            <tr> 
                              <td class="TableCell" width="80%"> 
                                <table width="100%" border="0" cellspacing="0" cellpadding="3" class="TableCell">
                                  <tr> 
                                    <td width="20%" align="right">Question:</td>
                                    <td width="80%"> 
                                      <input type="text" name="Question" size="55">
                                    </td>
                                  </tr>
                                  <tr> 
                                    <td width="20%" align="right">Answer Type:</td>
                                    <td width="80%">
                                      <select name="AnswerType">
<%
	StarsCustSelectionList answerTypeList = (StarsCustSelectionList) selectionListTable.get(YukonSelectionListDefs.YUK_LIST_NAME_ANSWER_TYPE);
	for (int i = 0; i < answerTypeList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = answerTypeList.getStarsSelectionListEntry(i);
		if (entry.getYukonDefID() != YukonListEntryTypes.YUK_DEF_ID_ANS_TYPE_FREE_FORM) continue;
%>
                                        <option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
<%	} %>
                                      </select>
                                    </td>
                                  </tr>
                                  <tr> 
                                    <td width="20%" align="right">Default Answer:</td>
                                    <td width="80%"> 
                                      <select name="DefaultAnswer">
                                        <option value="0">(none)</option>
                                      </select>
                                    </td>
                                  </tr>
                                </table>
                              </td>
                              <td class="TableCell" width="20%"> 
                                <input type="button" name="Save" value="Add" onClick="saveQuestion(this.form)">
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
                  <td width="290" align="right"> 
                    <input type="submit" name="Submit" value="Submit">
                  </td>
                  <td width="205"> 
                    <input type="reset" name="Reset" value="Reset">
                  </td>
                  <td width="75" align="right"> 
                    <input type="button" name="Done" value="Done" onclick="location.href='AdminTest.jsp'">
                  </td>
                </tr>
              </table>
            </form>
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
