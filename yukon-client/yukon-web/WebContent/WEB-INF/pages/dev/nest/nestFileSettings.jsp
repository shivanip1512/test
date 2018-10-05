<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="date" tagdir="/WEB-INF/tags/dateTime" %>

<cti:standardPage module="dev" page="nest.viewNestFileSetting">
    <!-- For setting existing file to use for nest -->
    <cti:url var="useNestFileUrl" value="useAsNestFile" />
    <form id="nestForm" action="${useNestFileUrl}" method="POST">
    <cti:csrfToken/>
        <tags:sectionContainer title="Set Existing File For Nest" helpText="${helpTextForExistingFile}">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".useNestFile.fileName">
                    <input id="fileName" name="fileName" type="text" maxlength="100" size="50" value = "${defaultFileName}">
                </tags:nameValue2>
            </tags:nameValueContainer2>
        
            <div class="column nogutter">
                <div class="action-area">
                    <button id="setFile" type="submit" classes="js-blocker">Set Nest File</button>
                </div>
            </div>
        </tags:sectionContainer>
    </form> 
    
    <!-- For generating new file for nest -->
    <cti:url var="generateFileUrl" value="generateFile" />
    <form id="generateNestFileForm" action="${generateFileUrl}" method="POST" modelAttribute = "NestFileGenerationSetting">
    <cti:csrfToken/>
    <cti:msg2 key=".nestFileGenerator.helpText" var="helpText"/>
        <div class="column-8-8-8">
            <div>
                <tags:sectionContainer title="Nest File Generation" helpText="${helpText}">
                    <tags:nameValueContainer2>
                         <tags:nameValue2 nameKey=".nestFileGenerator.groupName">
                            <input id="groupName" name="groupName" type="text" maxlength="50" size="50">
                         </tags:nameValue2>
                         <tags:nameValue2 nameKey=".nestFileGenerator.noOfRows">
                            <input id="noOfRows" name="noOfRows" type="text" maxlength="3" size="3" value="1">
                         </tags:nameValue2>
                         <tags:nameValue2 nameKey=".nestFileGenerator.noOfThermostats">
                            <input id="noOfThermostats" name="noOfThermostats" type="text" maxlength="3" size="3" value="1">
                         </tags:nameValue2>
                          <tags:nameValue2 nameKey=".nestFileGenerator.winterProgram">
                                <input id="noOfThermostats" name="winterProgram" type="checkbox">
                         </tags:nameValue2>
                         <tags:nameValue2 nameKey=".nestFileGenerator.defaultFile">
                                <input id="defaultFile" name="defaultFile" type="checkbox">
                         </tags:nameValue2>
                    </tags:nameValueContainer2>
                    <div class="column nogutter">
                        <div class="action-area">
                            <button id="generateFile" type="submit" classes="js-blocker">Generate File</button>
                        </div>
                    </div>
                </tags:sectionContainer>
            </div>

        </div>
    </form>
</cti:standardPage>