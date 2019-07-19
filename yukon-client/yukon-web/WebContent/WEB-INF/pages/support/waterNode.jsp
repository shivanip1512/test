<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="support" page="waterNode">
    <form action="generateReport" method="get">
        <tags:sectionContainer2 nameKey="support.waterNode">
            <div class="content clearfix"><i:inline key="yukon.web.modules.support.waterNode.description" /></div>
            <cti:button nameKey="generateReport" type="submit" icon="icon-page-white-excel"/>
        </tags:sectionContainer2>
    </form>
</cti:standardPage>