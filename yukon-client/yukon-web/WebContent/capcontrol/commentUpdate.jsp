<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@ page import="com.cannontech.spring.YukonSpringHook" %>
<%@ page import="com.cannontech.cbc.dao.*"%>
<%@ page import="com.cannontech.cbc.model.*"%>
<%@ page import="com.cannontech.util.*"%>
<%@ page import="com.cannontech.common.constants.LoginController" %>
<%@include file="cbc_inc.jspf"%>

<%
	LiteYukonUser user = (LiteYukonUser) session.getAttribute(LoginController.YUKON_USER);
	int commentId = ParamUtil.getInteger(request,"commentID",-1);
	int paoId = ParamUtil.getInteger(request,"paoID",-1);
	
	CapControlCommentDao dao = YukonSpringHook.getBean("capbankCommentDao", CapControlCommentDao.class);

	//set time and comment right before executing.
%>

<%if( commentId == -1 ){ %>
	<cti:titledContainer title="Add Comment" >
		<input type="text" name="commentTextBox" id="commentTextBox" />
		<input type="submit" value="Add" onclick="setComment()">
	</cti:titledContainer>
<%}else{ 
	CapControlComment comment = dao.getById(commentId);
%>
	<cti:titledContainer title="Change Comment" >
		<input type="text" name="commentTextBox" id="commentTextBox" value="<%=comment.getComment() %>"/>
		<input type="submit" value="Change" onclick="setComment();setDelete(-1)">
	</cti:titledContainer>
<%} %>