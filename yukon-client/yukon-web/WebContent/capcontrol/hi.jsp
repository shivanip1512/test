<%-- jsf:codeBehind language="java" location="/JavaSource/pagecode/capcontrol/Hi1.java" --%><%-- /jsf:codeBehind --%>
<%@taglib uri="http://www.ibm.com/jsf/BrowserFramework" prefix="odc"%>
<%@taglib uri="http://www.ibm.com/jsf/rte" prefix="r"%>
<%-- tpl:insert page="/capcontrol/cbctemplate.jtpl" --%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@taglib uri="http://www.ibm.com/jsf/html_extended" prefix="hx"%>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<HTML>
<HEAD>
<%@ taglib uri="/WEB-INF/cti.tld" prefix="cti" %>
<%@ page 
language="java"
contentType="text/html; charset=ISO-8859-1"
pageEncoding="ISO-8859-1"
%>
<META http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<META name="GENERATOR" content="IBM WebSphere Studio">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK rel="stylesheet" href="/WebConfig/yukon/CannonStyle.css"
	type="text/css">
<%-- tpl:put name="headarea" --%>
<META name="GENERATOR" content="IBM WebSphere Studio">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK href="../theme/Master.css" rel="stylesheet" type="text/css">
<TITLE>hi.jsp</TITLE>
		<LINK rel="stylesheet" type="text/css" href="../theme/tree.css"
			title="Style">
		<LINK rel="stylesheet" type="text/css" href="../theme/stylesheet.css"
			title="Style">
		<LINK rel="stylesheet" type="text/css" href="../theme/datagrid.css"
			title="Style">
		<LINK rel="stylesheet" type="text/css" href="../theme/tabpanel.css"
			title="Style">
		<LINK rel="stylesheet" type="text/css" href="../theme/rte_style.css"
			title="Style">
	<%-- /tpl:put --%>
<SCRIPT type="text/javascript">
function func_1(thisObj, thisEvent) {
//use 'thisObj' to refer directly to this component instead of keyword 'this'
//use 'thisEvent' to refer to the event generated instead of keyword 'event'
	formArea.submit();
}</SCRIPT>
</HEAD>

<cti:checklogin/>

<f:view>
	<BODY class="bodyStyle">
	<hx:scriptCollector id="scriptCollector1">
		<%@include file="js/cbc_funcs.js"%>

		<TABLE width="760" border="0" cellspacing="0" cellpadding="0" align="center" >
			<TBODY>
				<TR>
					<TD>
					<TABLE width="760" border="0" cellspacing="0" cellpadding="0" align="center">
						<TBODY>
							<TR>
								<TD width="102" height="102" background="images/LoadImage.gif">&nbsp;</TD>
								<TD width="1" bgcolor="#000000">
								<IMG src="images/VerticalRule.gif" width="1"></TD>
								<TD valign="bottom" height="102">
								<TABLE width="657" cellspacing="0" cellpadding="0" border="0">
									<TBODY>
										<TR>
											<TD colspan="4" height="74" background="images/Header.gif">&nbsp;</TD>
										</TR>
										<TR>
											<TD width="50%" height="28" class="Header">&nbsp;&nbsp;&nbsp;
											Capacitor Control <c:if
												test="${!applicationScope.CBCConnection.connected}">
												<FONT color="#FFFF00"> (Not connected to server) </FONT>
											</c:if> <c:if
												test="#{pc_Cbctemplate.cbcAnnex.refreshPending}">
												<FONT color="#FFFF00"> (Auto-refresh in <h:outputText
													styleClass="outputText" id="textPending"
													value="#{pc_Cbctemplate.cbcAnnex.pendingSecs}"></h:outputText>
												seconds)</FONT>
											</c:if></TD>
											<TD width="30%" valign="middle">&nbsp;</TD>

											<TD width="10%" valign="middle">
											<DIV align="center"><hx:outputLinkEx styleClass="Link3"
												value="/operator/Operations.jsp" id="linkExHome"
												style="text-align: center; vertical-align: bottom">
												<h:outputText id="textHome" styleClass="Link3" value="Home"
													style="text-align: center; vertical-align: bottom"></h:outputText>
											</hx:outputLinkEx></DIV>
											</TD>

											<TD width="10%" valign="middle">
											<DIV align="center"><hx:outputLinkEx styleClass="Link3"
												value="/servlet/LoginController?ACTION=LOGOUT"
												id="linkExLogOff"
												style="text-align: center; vertical-align: bottom">
												<h:outputText id="textLogOff" styleClass="Link3"
													value="Log Off"
													style="text-align: center; vertical-align: bottom"></h:outputText>
											</hx:outputLinkEx></DIV>
											</TD>

										</TR>
									</TBODY>
								</TABLE>
								</TD>
								<TD width="1" height="102" bgcolor="#000000"><IMG
									src="images/VerticalRule.gif" width="1"></TD>
							</TR>
						</TBODY>
					</TABLE>
					</TD>
				</TR>
			</TBODY>
		</TABLE>

		<CENTER>
		<TABLE width="760" bgcolor="#ffffff" border="1">
			<TBODY>
				<TR>
					<TD><h:form styleClass="form" id="formArea">
						<h:outputText styleClass="outputText" id="textSubs"
							value="Substation Areas:"></h:outputText>
						<h:selectOneMenu styleClass="selectOneMenu" id="menuArea"
							valueChangeListener="#{pc_Cbctemplate.handleMenuAreaValueChange}"
							onchange="return func_1(this, event);"
							value="#{pc_Cbctemplate.cbcAnnex.subTableModel.filter}">

							<f:selectItems
								value="#{selectitems.pc_Cbctemplate.cbcAnnex.subTableModel.areaNames.areaNames.toArray}" />
						</h:selectOneMenu>
						<BR>
					</h:form></TD>
				</TR>
				<TR>
					<TD><%-- tpl:put name="bodyarea" --%>
							<BR>
							<BR>
							<BR>
							<BR>
							<BR>
						<%-- /tpl:put --%>
					</TD>
				</TR>
			</TBODY>
		</TABLE>
		</CENTER>


	</hx:scriptCollector>
	</BODY>
</f:view>
</HTML><%-- /tpl:insert --%>