<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>

<cti:standardPage module="adminSetup" page="config.themes">

<div class="box clear dashboard">

    <form:form action="/adminSetup/config/update" id="settingsForm" method="post">
        
        <div class="clearfix box">
            <div class="category fl">
                <cti:url value="edit" var="category_url"><cti:param name="category" value="${category}"/></cti:url>
                <a href="${category_url}" class="icon icon-32 fl ${category_icon}"></a>
                <div class="box fl meta">
                    <div><a class="title" href="/adminSetup/config/theme"><i:inline key="yukon.common.setting.subcategory.${category}"/></a></div>
                    <div class="detail"><i:inline key="yukon.common.setting.subcategory.${category}.description"/></div>
                </div>
            </div>
            
        </div>
        
        <div class="column_6_18">
            
            <div class="column one">
                <h1>column1</h1>
            </div>
        
            <div class="column two nogutter">
                <h1>column2</h1>
            </div>
        
        </div>
        
    </form:form>
    
</div>

</cti:standardPage>