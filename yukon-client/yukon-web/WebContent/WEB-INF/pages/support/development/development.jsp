<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="debug" tagdir="/WEB-INF/tags/debug"%>

<cti:standardPage module="support" page="development">
    <div class="column-8-8-8">
        <div class="column one">
            <tags:sectionContainer title="UI Demos">
                <ul class="simple-list">
                    <li><a href="/support/development/uiDemos/main">UI Demos</a></li>
                    <li><a href="/support/development/uiToolkitDemo/main">UI Toolkit</a></li>
                </ul>
            </tags:sectionContainer>   
        </div>
        <div class="column two">
            <tags:sectionContainer title="Development Setup">
                <ul class="simple-list">
                    <li><a href="/support/development/pointInjection/main">Point Injection</a></li>
                    <li><a href="/support/development/bulkPointInjection/main">Bulk Point Injection</a></li>
                    <li><a href="/support/development/setupDatabase/main">Setup Development Database</a></li>
                    <li><a href="/support/development/miscellaneousMethod/main">Miscellaneous Methods</a></li>
                    <li><a href="/support/development/rfn/viewBase">RFN Test</a></li>
                </ul>
            </tags:sectionContainer>   
        </div>
        <div class="column three nogutter">
            <tags:sectionContainer title="Web Services Tests">
                <ul class="simple-list">
                    <li><a href="/support/development/eimTest/main">Web Service XML Test</a></li>
                    <li><a href="/debug/loadControlService/inputs/home">Load Control Services Test</a></li>
                    <li><a href="/debug/accountService/inputs/home">Account Service Test</a></li>
                </ul>
            </tags:sectionContainer>
        </div>
     </div>
</cti:standardPage>