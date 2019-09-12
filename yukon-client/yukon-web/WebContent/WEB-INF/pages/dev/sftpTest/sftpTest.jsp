<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="dev" page="sftp.sftpTest">
    <div class="column-14-10 clearfix">
        <div class="column one">
            <cti:msg2 key="modules.dev.sftp.sftpTest.helpText" var="helpText"/>
            <tags:sectionContainer2 nameKey="sftp.sftpTest">
                <cti:url var="action" value="/dev/sftpTest/execute"/>
                <form action="${action}" method="post" modelAttribute="sftp">
                    <cti:csrfToken/>
                    <tags:nameValueContainer2 naturalWidth="true" tableClass="with-form-controls">
                        <tags:inputNameValue nameKey=".domain" path="sftp.domain" size="30"/>
                        <tags:inputNameValue nameKey=".port" path="sftp.port" size="30"/>
                        <tags:inputNameValue nameKey=".sftpPath" path="sftp.sftpPath" size="30"/>
                        <tags:inputNameValue nameKey=".username" path="sftp.username" size="30"/>
                        <tags:inputNameValue nameKey=".password" path="sftp.password" size="30"/>
                        <tags:inputNameValue nameKey=".filename" path="sftp.filename" size="30"/>
                        <tags:inputNameValue nameKey=".privateKey" path="sftp.privateKey" size="30"/>
                    </tags:nameValueContainer2>
                    <div class="page-action-area">
                        <cti:button nameKey="submit" name="submit" type="submit" classes="primary action fr"/>
                    </div>
                </form>
            </tags:sectionContainer2>
        </div>
    </div>
</cti:standardPage>