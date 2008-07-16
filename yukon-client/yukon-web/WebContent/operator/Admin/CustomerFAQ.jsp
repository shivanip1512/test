<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@ include file="../Consumer/include/StarsHeader.jsp"%>

<%
    String faqLink = ServletUtils.getCustomerFAQLink(liteEC);
    boolean inherited = ServletUtils.isCustomerFAQInherited(liteEC);
    boolean customized = (faqLink == null) && !inherited;
%>

<c:set var="isInherited" value="<%=inherited%>"/>

<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css"
    type="text/css">
<link rel="stylesheet"
    href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>"
    type="text/css">

<script language="JavaScript">
</script>

</head>

<body class="Background" leftmargin="0" topmargin="0">

<table width="760" border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td><%@ include file="include/HeaderBar.jspf"%></td>
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
                    <td valign="top" width="101">&nbsp;</td>
                    <td width="1" bgcolor="#000000">
                        <img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
                    <td width="657" height="400" valign="top" bgcolor="#FFFFFF">
                        <div align="center">
                            <br>
                            <span class="TitleHeader">ADMINISTRATION - CUSTOMER FAQS</span>
                            <br>
                            <%
                                if (errorMsg != null)
                                    out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>");
                            %> <%
                                if (confirmMsg != null)
                                    out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>");
                            %>
                            <form method="post" action="<%=request.getContextPath()%>/servlet/StarsAdmin">
                            
                                <input type="hidden" name="action" value="UpdateFAQSource">
                            
                                <table width="600" border="1" cellspacing="0" cellpadding="0">
                                    <tr>
                                        <td>
                                            <table class="MainText" width="100%" border="0" cellspacing="0" cellpadding="3">
                                                <tr>
                                                    <td colspan="2" class="HeaderCell">Choose FAQ Source</td>
                                                </tr>
                                                
                                                <tr>
                                                    <td colspan="2">
                                                        <input type="radio" name="Source" value="Inherited" <% if (inherited) out.print("checked"); %>>Inherited<br>
                                                    </td>
                                                </tr>
                                                
                                                <tr>
                                                    <td>
                                                        <input type="radio" name="Source" value="Link" <% if (!inherited && faqLink != null) out.print("checked"); %>>Use a link to your company's website:
                                                    </td>
                                                    <td>
                                                        <input style="width: 25em;" type="text" name="FAQLink" size="30" value="<c:if test='${!isInherited}'><%=StarsUtils.forceNotNone(faqLink)%></c:if>">
                                                    </td>
                                                </tr>
                                                
                                                <tr>
                                                    <td colspan="2">
                                                        <input type="radio" name="Source" value="Customized" <% if (customized) out.print("checked"); %>>Define FAQ's in xml configuration (See Yukon documentation)  
                                                    </td>
                                                </tr>
                                                    
                                            </table>
                                        </td>
                                    </tr>
                                </table>
                            
                                <br>
                                
                                <div>
                                    <span style="margin-right: 0.5em;"><input type="submit" value="Submit"></input></span>
                                    <input type="button" name="Back" value="Back" onclick="if (warnUnsavedChanges()) location.href='ConfigEnergyCompany.jsp'">
                                </div>
                        
                            </form>
                        </div>
                    </td>
                    
                    <td width="1" bgcolor="#000000">
                        <img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1">
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>

<br>

</body>
</html>
