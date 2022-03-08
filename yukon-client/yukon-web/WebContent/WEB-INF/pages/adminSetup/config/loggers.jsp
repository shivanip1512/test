<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="adminSetup" page="config.loggers">
    <dt:pickerIncludes/>
    <cti:msgScope paths="yukon.common,modules.adminSetup.config.loggers">

        <div class="box clear dashboard">
            <div class="clearfix box">
                <div class="category fl">
                    <cti:button renderMode="appButton" icon="icon-app icon-app-32-loggers" href="allLoggers"/>
                    <div class="box fl meta">
                        <div><a class="title" href="<cti:url value="/admin/config/loggers/allLoggers"/>">
                            <i:inline key="yukon.common.setting.subcategory.YUKON_LOGGERS"/>
                        </a></div>
                        <div class="detail"><i:inline key="yukon.common.setting.subcategory.YUKON_LOGGERS.description"/></div>
                    </div>
                </div>
            </div>
        </div>

        <tags:sectionContainer2 nameKey="userLoggers">
            <div class="filter-section stacked-md">
                <cti:url var="filterUrl" value="/admin/config/loggers/filter"/>
                <form:form id="filter-form" method="get" action="${filterUrl}" >
                    <span class="fl">
                        <span class="vat"><i:inline key="yukon.common.filterBy"/></span>
                        <cti:msg2 var="loggerNamePlaceholder" key=".loggerName"/>&nbsp;
                        <input type="text" name="loggerName" placeholder="${loggerNamePlaceholder}" class="vat MR5" size="20"/>
 
                        <cti:msg2 var="allLoggerLevels" key=".allLoggerLevels"/>&nbsp;
                        <select name="loggerLevels" class="js-selected-levels" multiple="multiple" size="1" 
                            data-placeholder="${allLoggerLevels}" size="width:350px;">
                            <c:forEach var="level" items="${loggerLevels}">
                                <option value="${level}"><i:inline key="${level.formatKey}"/></option>
                            </c:forEach>
                        </select>
                    </span>
                    <cti:button nameKey="filter" classes="js-filter-loggers action primary fn ML15"/>
                </form:form>
            </div>
            <hr/>

            <div id="logger-container" data-url="${filterUrl}">
                <%@ include file="userLoggersTable.jsp" %>
            </div>

            <cti:msg2 var="addLoggerTitle" key=".addLoggerTitle"/>
            <cti:button nameKey="add" classes="fr js-logger-popup" icon="icon-add" data-title="${addLoggerTitle}"/>

        </tags:sectionContainer2>

        <tags:sectionContainer2 nameKey="systemLoggers">
        <cti:url var="getUrl" value="/admin/config/loggers/getSystemLoggers"/>
        <form:form id="systemLoggerForm" method="get" action="${getUrl}" >
        </form:form>
            <div id="system-logger-container" data-url="${getUrl}">
                <%@ include file="systemLoggersTable.jsp" %>
            </div>
        </tags:sectionContainer2>
        
    </cti:msgScope>

    <cti:includeScript link="/resources/js/pages/yukon.adminSetup.yukonLoggers.js" />

</cti:standardPage>