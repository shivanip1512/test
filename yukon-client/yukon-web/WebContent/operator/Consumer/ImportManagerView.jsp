<%@ include file="include/StarsHeader.jsp"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<cti:standardPage title="Energy Services Operations Center"
	module="stars">

	<%@page import="java.util.List"%>

	<jsp:useBean id="importManagerBean"
		class="com.cannontech.stars.web.bean.ImportManagerBean"
		scope="session" />
	<jsp:setProperty name="importManagerBean" property="page" param="page" />
	<jsp:setProperty name="importManagerBean" property="pageSize"
		param="pageSize" />

	<%
			Collection errorList = (Collection) session.getAttribute("errorList");
			importManagerBean.setErrorList(errorList);
	%>

<script language="JavaScript">
function init() {
}
</script>

	<table width="760" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td>
				<%@ include file="include/HeaderBar.jspf"%>
			</td>
		</tr>
		<tr>
			<td>
				<table width="760" border="0" cellspacing="0" cellpadding="0"
					align="center" bordercolor="0">
					<tr>
						<td width="101" bgcolor="#000000" height="1"></td>
						<td width="1" bgcolor="#000000" height="1"></td>
						<td width="657" bgcolor="#000000" height="1"></td>
						<td width="1" bgcolor="#000000" height="1"></td>
					</tr>
					<tr>
						<td valign="top" width="101">
							&nbsp;
						</td>
						<td width="1" bgcolor="#000000">
							<img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1">
						</td>
						<td width="657" height="400" valign="top" bgcolor="#FFFFFF">
							<div align="center">
								<%=importManagerBean.getHtml()%>
							</div>
						</td>
				</table>
			</td>
		</tr>

	</table>

</cti:standardPage>
