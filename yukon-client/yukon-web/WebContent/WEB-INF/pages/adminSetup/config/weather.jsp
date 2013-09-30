<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>

<cti:standardPage module="adminSetup" page="config.weather">

<div class="box clear dashboard">

        <div class="clearfix box">
            <div class="category fl">
                <a href="weather" class="icon icon-32 fl icon-32-cloud2"></a>
                <div class="box fl meta">
                    <div><a class="title" href="/adminSetup/config/weather"><i:inline key="yukon.common.setting.subcategory.${category}"/></a></div>
                    <div class="detail"><i:inline key="yukon.common.setting.subcategory.${category}.description"/></div>
                </div>
            </div>
            
        </div>
        
        <div><!-- dans weather stuffs --></div>
        
</div>

</cti:standardPage>