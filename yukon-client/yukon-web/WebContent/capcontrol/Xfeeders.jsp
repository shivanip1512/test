<%-- jsf:codeBehind language="java" location="/JavaSource/pagecode/capcontrol/Xfeeders.java" --%><%-- /jsf:codeBehind --%>
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
		<TITLE>cbctemplate.jtpl</TITLE>
		<LINK rel="stylesheet" type="text/css" href="../theme/stylesheet.css"
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
							<hx:scriptCollector id="scriptCollector2">
								<h:form styleClass="form" id="form1">
									<h:dataTable styleClass="dataTable" id="tableFeeders"
										var="fdrdata"
										value="#{pc_Xfeeders.cbcAnnex.feederTableModel.rows}"
										headerClass="HeaderCell" rowClasses="TableCell" width="750"
										border="1">
										<f:facet name="header">
										</f:facet>
										<h:column id="column1">
											<f:facet name="header">
												<h:outputText styleClass="outputText" value="Feeder Name"
													id="text1"></h:outputText>
											</f:facet>
											<h:commandLink styleClass="commandLink" id="linkFeederList"
												action="#{pc_Xfeeders.doLinkFeederListAction}">
												<h:outputText id="text13" styleClass="outputText"
													value="#{fdrdata.renderName}"></h:outputText>
												
											</h:commandLink>
										</h:column>
										<h:column id="column2">
											<f:facet name="header">
												<h:outputText styleClass="outputText" value="State"
													id="text2"></h:outputText>
											</f:facet>

											<h:commandLink styleClass="commandLink" id="linkSubCntrl"
												action="#{pc_Xfeeders.doLinkSubCntrl}">
												<h:outputText id="text12" styleClass="outputText"
													value="#{fdrdata.renderState}"></h:outputText>
												
												
											</h:commandLink>
										</h:column>
										<h:column id="column3">
											<f:facet name="header">
												<h:outputText styleClass="outputText" value="Target"
													id="text3"></h:outputText>
											</f:facet>

											<h:outputText styleClass="outputText" id="text11"
												value="#{fdrdata.renderTarget}"></h:outputText>
										</h:column>
										<h:column id="column4">
											<f:facet name="header">
												<h:outputText styleClass="outputText"
													value="Var Load / Est." id="text4"></h:outputText>
											</f:facet>
											<h:outputText styleClass="outputText" id="text14"
												value="#{fdrdata.renderVarLoad}"></h:outputText>
										</h:column>
										<h:column id="column5">
											<f:facet name="header">
												<h:outputText styleClass="outputText" value="Date/Time"
													id="text5"></h:outputText>
											</f:facet>
											<h:outputText styleClass="outputText" id="text15"
												value="#{fdrdata.renderTimeStamp}"></h:outputText>
										</h:column>
										<h:column id="column6">
											<f:facet name="header">
												<h:outputText styleClass="outputText" value="PFactor / Est."
													id="text6"></h:outputText>
											</f:facet>
											<h:outputText styleClass="outputText" id="text16"
												value="#{fdrdata.renderPF}"></h:outputText>
										</h:column>
										<h:column id="column7">
											<f:facet name="header">
												<h:outputText styleClass="outputText" value="Watts"
													id="text7"></h:outputText>
											</f:facet>
											<h:outputText styleClass="outputText" id="text17"
												value="#{fdrdata.renderWatts}"></h:outputText>
										</h:column>
										<h:column id="column8">
											<f:facet name="header">
												<h:outputText styleClass="outputText" value="Daily Ops"
													id="text8"></h:outputText>
											</f:facet>
											<h:outputText styleClass="outputText" id="text18"
												value="#{fdrdata.renderDailyOps}"></h:outputText>
										</h:column>
										<h:column id="column9">
											<f:facet name="header">
												<h:outputText styleClass="outputText" value="Graphs"
													id="text9"></h:outputText>
											</f:facet>
										</h:column>
										<h:column id="column10">
											<f:facet name="header">
												<h:outputText styleClass="outputText" value="Reports"
													id="text10"></h:outputText>
											</f:facet>
										</h:column>
									</h:dataTable>
								</h:form>
						</hx:scriptCollector>
							
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
