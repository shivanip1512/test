<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<cti:standardPage module="support" page="supportBundle">
    <script type="text/javascript">
    Event.observe(window, 'load', function() {
        
        Event.observe('startBtn', 'click', function() {
            Yukon.ui.blockPage();
            $("uploadBundle").submit();
        });

        Event.observe('cancelBtn', 'click', function() {
            window.location = "view";
        });
        
        Event.observe('okBtn', 'click', function() {
            window.location = "view";
        });
        
    });
</script>
    <c:if test="${ftpStatus == 'SEND_ERROR'}">
        <c:set var="msgStyle" value="errorMessage" />
    </c:if>
    <c:if test="${ftpStatus == 'SUCCESS'}">
        <c:set var="msgStyle" value="successMessage" />
    </c:if>
    <cti:msg2 var="boxTitle" key=".send.fileHeading"/>
    <tags:boxContainer title="${boxTitle}" styleClass="mediumContainer" hideEnabled="false">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".send.filenameLbl">
                    ${fn:escapeXml(filename)}
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".send.fileDateCreatedLbl">
                    <c:out value="${fileDate}"></c:out> 
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".send.fileSizeLbl">
                    <c:out value="${fileSize}"></c:out>
                </tags:nameValue2>
                
                <c:if test="${doUpload}">
                    <tags:nameValue2 nameKey=".ftp.status">
                       <span class="${msgStyle}">
                            <i:inline key="${ftpStatus}"/>
                       </span>
                    </tags:nameValue2>
                </c:if>
        </tags:nameValueContainer2>
    </tags:boxContainer>
    <br>
    <form:form id="uploadBundle" action="upload" method="POST">
        <input type="hidden" name="fileNum" id="fileNum" value="${fileNum}" />
        <input type="hidden" name="ftpStatus" value="${ftpStatus}" /> 
        <tags:nameValueContainer2>               
            <c:if test="${ftpStatus == 'SEND_ERROR'}">
            <i:inline key=".send.badDefaultSettings"/><br>
                <tags:nameValue2 nameKey=".ftp.username">
                   <input name="username" type="text" />
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".ftp.password">
                   <input name="password" type="password" />
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".ftp.host">
                   <input name="host" type="text" />
                </tags:nameValue2>
            </c:if>
        </tags:nameValueContainer2><br>
        <c:if test="${ftpStatus != 'SUCCESS'}">
            <cti:button nameKey="startUploadImg" id="startBtn" type="submit"/>
            <cti:button nameKey="cancelImg" id="cancelBtn"/>
            <input type="hidden" id="okBtn"/>
        </c:if>
        <c:if test="${ftpStatus == 'SUCCESS'}">
            <cti:button nameKey="okImg" id="okBtn"/>
            <input type="hidden" id="cancelBtn"/>
            <input type="hidden" id="startBtn"/>
        </c:if>
    </form:form>
</cti:standardPage>
