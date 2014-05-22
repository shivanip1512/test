<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="home">
    <div class="column-8-8-8">
        <div class="column one">
            <tags:sectionContainer title="User Interface Development">
                <ul class="simple-list">
                    <li><a href="<cti:url value="/dev/styleguide/main"/>">Style Guide</a></li>
                    <li><a href="<cti:url value="/dev/uiToolkitDemo/main"/>">UI Toolkit</a></li>
                </ul>
            </tags:sectionContainer>
        </div>
        <div class="column two">
            <tags:sectionContainer title="Development Setup">
                <ul class="simple-list">
                    <li><a href="<cti:url value="/dev/pointInjection/main"/>">Point Injection</a></li>
                    <li><a href="<cti:url value="/dev/bulkPointInjection/main"/>">Bulk Point Injection</a></li>
                    <li><a href="<cti:url value="/dev/setupDatabase/main"/>">Setup Development Database</a></li>
                    <li><a href="<cti:url value="/dev/miscellaneousMethod/main"/>">Miscellaneous Methods</a></li>
                    <li><a href="<cti:url value="/dev/rfn/viewBase"/>">RFN Test</a></li>
                </ul>
            </tags:sectionContainer>
        </div>
        <div class="column three nogutter">
            <tags:sectionContainer title="Web Services Tests">
                <ul class="simple-list">
                    <li><a href="<cti:url value="/dev/eimTest/main"/>">Web Service XML Test</a></li>
                    <li><a href="<cti:url value="/debug/loadControlService/inputs/home"/>">Load Control Services Test</a></li>
                    <li><a href="<cti:url value="/debug/accountService/inputs/home"/>">Account Service Test</a></li>
                </ul>
            </tags:sectionContainer>
        </div>
    </div>
</cti:standardPage>