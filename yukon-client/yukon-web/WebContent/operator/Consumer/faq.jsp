<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ include file="include/StarsHeader.jsp" %>
<%@page import="com.cannontech.common.i18n.MessageSourceAccessor"%>
<%@page import="org.springframework.context.NoSuchMessageException"%>
<%@page import="com.cannontech.i18n.YukonUserContextMessageSourceResolver"%>

<% 
if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } 

YukonUserContextMessageSourceResolver messageSourceResolver = YukonSpringHook.getBean("yukonUserContextMessageSourceResolver", YukonUserContextMessageSourceResolver.class);
String keyPrefix = "yukon.dr.consumer.faq.question.";
String question = ".question";
String answer = ".answer";
String subject = ".subject";


YukonUserContext yukonUserContext = YukonUserContextUtils.getYukonUserContext(request);


MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(yukonUserContext);
final Map<String, Map<String, String>> faqQuestions = new LinkedHashMap<String, Map<String,String>>();

int index = 1;
boolean done = false;

while (!done) {
    try {
        int currentIndex = index++;
        
        String subjectCode = keyPrefix + currentIndex + subject;
        String subjectValue = messageSourceAccessor.getMessage(subjectCode);
        
        String questionCode = keyPrefix + currentIndex + question;
        String questionValue = messageSourceAccessor.getMessage(questionCode);
        
        String answerCode = keyPrefix + currentIndex + answer;
        String answerValue = messageSourceAccessor.getMessage(answerCode);
        
        Map<String, String> subjectMap = faqQuestions.get(subjectValue);
        if (subjectMap == null) {
            subjectMap = new LinkedHashMap<String, String>();
            faqQuestions.put(subjectValue, subjectMap);
        }
        
        subjectMap.put(questionValue, answerValue);
        
    } catch (NoSuchMessageException e) {
        done = true;
    }
}

%>

<c:set var="faqQuestions" value="<%=faqQuestions%>" />

<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <%@ include file="include/HeaderBar.jspf" %>
    </td>
  </tr>
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center" bordercolor="0">
        <tr> 
          <td width="101" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
          <td width="10" bgcolor="#000000"></td>
          <td width="657" bgcolor="#000000" height="1"></td>
          <td width="10" bgcolor="#000000"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
        </tr>
        <tr> 
          <td  valign="top" width="101">
          <% String pageName = "FAQ.jsp"; %>
          <%@ include file="include/Nav.jspf" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="10" valign="top" bgcolor="#FFFFFF"></td>
          <td width="637" valign="top" bgcolor="#FFFFFF">
            <!--  FAQ Key Render -->
            <div align="center"><% String header = "FAQ"; %>
            <%@ include file="include/InfoSearchBar.jspf" %>
            <p>&nbsp;</p>
            </div>
            
    <div align="left">
      <c:set var="index" value="0"/>
      <c:forEach var="subject" items="${faqQuestions}">
        <% System.out.println("in for"); %>
        <b>${subject.key}</b>
        <br>
        <ul>
            <c:forEach var="question" items="${subject.value}">
                <c:set var="index" value="${index + 1}"/>
                <c:set var="link" value="#faq${index}"/>
                <li><a href="${link}">${question.key}</a></li>
            </c:forEach>
        </ul>
      </c:forEach>
    
      <br/>
      <br/>
    
      <c:set var="index" value="0"/>
      <c:forEach var="subject" items="${faqQuestions}">
        <br/>
        <b>${subject.key}</b>
        <br/>
        <br/>
        
        <c:forEach var="question" items="${subject.value}">
            <c:set var="index" value="${index + 1}"/>
            <c:set var="name" value="faq${index}"/>
            
            <a name="${name}"></a>
            <b><i>${question.key}</i></b>
            <br/>
            <br/>
            ${question.value}
            <br/>
            <a href="#" name="top"><cti:msg key="yukon.dr.consumer.faq.backtotop"/></a>
            <br/>
            <br/>
        </c:forEach>
      </c:forEach>
    </div>          
          </td>
          <td width="10" bgcolor="#FFFFFF"></td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>

        </tr>
      </table>
      
    
    </td>
  </tr>
</table>
<br>
</body>
</html>
