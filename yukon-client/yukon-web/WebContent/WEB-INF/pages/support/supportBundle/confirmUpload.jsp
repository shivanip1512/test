<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<cti:standardPage module="support" page="supportBundle">
    <tags:boxContainer2 nameKey=".send.fileHeading" styleClass="mediumContainer" hideEnabled="false">
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".send.filenameLbl">
                ${fn:escapeXml(filename)}
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".send.fileDateCreatedLbl">
                <cti:formatDate value="${fileDate}" type="DATE"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".send.fileSizeLbl">
                <cti:msg2 key="${fileSize}"/>
            </tags:nameValue2>
        </tags:nameValueContainer2>
    </tags:boxContainer2>

    <div class="pageActionArea">
        <cti:url var="uploadUrl" value="upload"/>
        <form action="${uploadUrl}" method="POST">
            <input type="hidden" name="fileNum" value="${fileNum}"/>
            <cti:button nameKey="startUpload" type="submit" styleClass="f_blocker"/>
            <cti:url var="viewUrl" value="view"/>
            <cti:button nameKey="cancel" href="${viewUrl}"/>
        </form>
    </div>
</cti:standardPage>
