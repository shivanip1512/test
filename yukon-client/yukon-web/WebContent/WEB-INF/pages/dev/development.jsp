<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="home">

    <div class="column-8-8-8">
        
        <div class="column one">
            <tags:sectionContainer title="User Interface Development">
                <ul class="simple-list">
                    <li><a href="<cti:url value="/dev/styleguide"/>">Style Guide and Component Library</a></li>
                    <li><a href="<cti:url value="/dev/js-api"/>">Yukon JavaScript Library</a></li>
                    <li><a href="<cti:url value="/dev/pages"/>">Pages</a></li>
                    <li><a href="<cti:url value="/dev/i18n"/>">Localization</a></li>
                    <li><a href="<cti:url value="/dev/i18n/scopes"/>">Localization Scoping</a></li>
                </ul>
            </tags:sectionContainer>
        </div>
        
        <div class="column two">
            <tags:sectionContainer title="Utilities">
                <ul class="simple-list">
                    <li><a href="<cti:url value="/dev/pointInjection/main"/>">Point Injection</a></li>
                    <li><a href="<cti:url value="/dev/bulkPointInjection/home"/>">Bulk Point Injection</a></li>
                    <li><a href="<cti:url value="/dev/setupDatabase/main"/>">Setup Development Database</a></li>
                    <li><a href="<cti:url value="/dev/miscellaneousMethod/main"/>">Miscellaneous Methods</a></li>
                    <li><a href="<cti:url value="/dev/db-change"/>">DbChange Messages</a></li>
                    <li><a href="<cti:url value="/dev/create-import-files"/>">Create Import Files</a></li>
                    <li><a href="<cti:url value="/dev/uber-log"/>">Uber Log</a></li>
                    <li><a href="<cti:url value="/dev/bulkPointInjection/rphSimulator"/>">High Speed Bulk Point Injection</a></li>
                    <li><a href="<cti:url value="/dev/bulkImportFileGenerator/home"/>">Bulk Import File Generator</a></li>
                    <li><a href="<cti:url value="/dev/viewDeviceGroupSimulator"/>">Device Group Generator</a></li>
                    <li><a href="<cti:url value="/dev/getEcobeePGPKeyPair"/>">Ecobee PGP Key Pair</a></li>
                    <li><a href="<cti:url value="/dev/jmsApiDirectory"/>">JMS API Directory</a></li>
                    <li><a href="<cti:url value="/dev/getItronKeyPair"/>">Itron Key Pair</a></li>
                </ul>
            </tags:sectionContainer>
        </div>
        
        <div class="column three nogutter">
            <tags:sectionContainer title="Testing">
                <ul class="simple-list">
                    <li><a href="<cti:url value="/dev/eimTest/main"/>">Web Service XML Test</a></li>
                    <li><a href="<cti:url value="/dev/sftpTest/sftpTest"/>">SFTP Test</a></li>
                    <li><a href="<cti:url value="/debug/loadControlService/inputs/home"/>">Load Control Services Test</a></li>
                    <li><a href="<cti:url value="/debug/accountService/inputs/home"/>">Account Service Test</a></li>
                    <li><a href="<cti:url value="/dev/rfn/viewBase"/>">RFN Test</a></li>
                    <li><a href="<cti:url value="/dev/ivvc/ivvcSimulator"/>">IVVC Test</a></li>
                    <li><a href="<cti:url value="/dev/js-testing"/>">JS Testing</a></li>
                    <li><a href="<cti:url value="/dev/ecobee/viewBase"/>">Ecobee Test</a></li>
                    <li><a href="<cti:url value="/dev/systemHealthMetricSimulator/home"/>">Fake System Health Metrics</a></li>
                    <li><a href="<cti:url value="/dev/paoDefinition"/>">Pao Definition Testing</a></li>
                    <li><a href="<cti:url value="/dev/smartNotificationsSimulator"/>">Smart Notifications Testing</a></li>
                    <li><a href="<cti:url value="/dev/collectionActionsTesting"/>">Collection Actions Testing</a></li>
                    <li><a href="<cti:url value="/dev/nest/home"/>">Nest Test</a></li>
                    <li><a href="<cti:url value="/dev/itron/itronSimulator"/>">Itron Simulator</a></li>
                    
                </ul>
            </tags:sectionContainer>
        </div>
    </div>
    
</cti:standardPage>