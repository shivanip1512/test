<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="date" tagdir="/WEB-INF/tags/dateTime" %>

<cti:standardPage module="dev" page="nest.viewNestSync">
    <!-- Sync Yukon with Nest -->
    <cti:msg2 key=".sync.helpText" var="helpText"/>
    <tags:sectionContainer title="Sync Yukon and Nest" helpText="${helpText}">
    <cti:url var="syncYukonAndNest" value="syncYukonAndNest" />
    <form id="syncYukonAndNestForm" action="${syncYukonAndNest}" method="GET">
       <div class="column nogutter">
           <div class="action-area">
                <button id="sync" type="submit" classes="js-blocker">Sync</button>
            </div>
       </div>
    </form>
    <cti:url var="downloadExisting" value="downloadExisting" />
    <form id="downloadExistingForm" action="${downloadExisting}" method="GET">
       <div class="column nogutter">
           <div class="action-area">
                <button id="download" type="submit" classes="js-blocker">Test generated file download</button>
            </div>
       </div>
    </form>
    </tags:sectionContainer>
</cti:standardPage>