<%@ page errorPage="/internalError.jsp" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>

<cti:outputDoctype levels="${info.htmlLevel}, strict"/>
<html>
    <head>
    	<meta http-equiv="X-UA-Compatible" content="IE=EDGE" />
        <title><c:out value="${title}"/></title>           

        <!-- Layout CSS files -->
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/normalize.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/yukon.default.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/icons.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/buttons.default.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/CannonStyle.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/consumer/LeftMenuStyles.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/JavaScript/lib/jQueryUI/jquery-ui-1.10.4.custom.min.css"/>" >
        
        <%-- Include overrides.css last so that, you know, they actually override.  cascade! --%>
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/overrides.css"/>" >
        
        <!-- Module CSS files from module_config.xml -->
        <c:forEach items="${moduleConfigCss}" var="file"><link rel="stylesheet" type="text/css" href="<cti:url value="${file}"/>" >
        </c:forEach>
        
        <!-- Individual files from includeCss tag on the request page -->
        <c:forEach items="${innerContentCss}" var="file"><link rel="stylesheet" type="text/css" href="<cti:url value="${file}"/>" >
        </c:forEach>
        
        <!-- Login Group specific style sheets (YukonRoleProperty.STD_PAGE_STYLE_SHEET)-->
        <c:forEach items="${loginGroupCss}" var="file"><link rel="stylesheet" type="text/css" href="<cti:url value="${file}"/>" >
        </c:forEach>
        
        <!-- Consolidated Script Files -->
        <c:forEach items="${javaScriptFiles}" var="file"><script type="text/javascript" src="<cti:url value="${file}"/>"></script>
        </c:forEach>

        <!-- Some prerequisite globals -->
        <script type="text/javascript">
        var YG = {
                PHONE: {
                    FORMATS: <cti:msg2 key="yukon.common.phoneNumberFormatting.formats"/>
                },
                APP_NAME: '<cti:url value="/"/>'
        };
        </script>
    </head>
    
	<body class="<c:out value="${module.moduleName}"/>_module">
    <div id="modal-glass" style="display:none;">
        <div class="tint"></div>
        <div class="loading">
            <div class="box load-message">
                <cti:msg key="yukon.web.components.pageloading"/>
            </div>
        </div>
    </div>
    
		<div id="Header">
		    <div class="stdhdr_left"><div id="TopLeftLogo"><cti:logo key="yukon.web.layout.standard.upperleftlogo"/></div></div>
		    <div class="stdhdr_middle"><div id="TopMiddleLogo"><cti:logo key="yukon.web.layout.standard.uppermiddlelogo"/></div></div>
		    <div class="stdhdr_right"><div id="TopRightLogo"><cti:logo key="yukon.web.layout.standard.upperrightlogo"/></div></div>
		    <div class="stdhdr_clear"></div>
		</div>

        <table style="width:100%">
            <tr>
                <td colspan="2" class="leftMenuHeader">
                    <cti:checkRolesAndProperties  value="RESIDENTIAL_SIGN_OUT_ENABLED">
                        <a href="/servlet/LoginController?ACTION=LOGOUT"><cti:msg key="yukon.web.menu.logout" /></a>
                    </cti:checkRolesAndProperties>
                    &nbsp;
                </td>
            </tr>
            <tr>
                <td class="leftMenu">
                    <cti:outputContent writable="${menuRenderer}"/>
                </td>
                <td id="Content" style="vertical-align: top;width:97%">
                    <table class="contentTable">
                        <tr>
                            <td class="leftColumn">
                                    <cti:outputContent writable="${bodyContent}"/>
                            </td>
                            <td class="rightColumn">
                                <cti:customerAccountInfoTag account="${customerAccount}" />
                                <br>
                                <cti:logo key="yukon.dr.${module.moduleName}.${info.pageName}.rightColumnLogo"/>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr>
                <td colspan="2">
            		<div id="CopyRight">
            			<cti:msg key="yukon.web.layout.standard.yukonVersion" arguments="${yukonVersion}"/> |
            			<cti:msg key="yukon.web.layout.standard.copyright"/> |
            			Generated at <cti:formatDate type="FULL" value="${currentTime}"/>
            		</div>
                </td>
            </tr>
        </table>

        <ct:analyticsTrackPage/>
	</body>
</html>