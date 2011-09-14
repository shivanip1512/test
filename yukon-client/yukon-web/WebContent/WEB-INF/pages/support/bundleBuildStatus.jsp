<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<cti:msgScope paths="modules.support.supportBundle">
    <table border='0'>
        <c:forEach var="writer" items="${writerList}">
            <tr>
                <!-- Optional writers which are not being included will not show up in this list-->
                <c:if test="${not empty thingsDoneMap[writer.name]}"> 
                    <td>
                        <i:inline key=".writerName.${writer.name}" />
                    </td>
                    <td>
                        <c:if test="${thingsDoneMap[writer.name]}">
                            <span class="successMessage">&nbsp&nbsp <i:inline key='.bundleCreationStatus.doneMsg'/></span>
                        </c:if>
                        <c:if test="${not thingsDoneMap[writer.name]}">
                            <span class="errorMessage">&nbsp&nbsp <i:inline key='.bundleCreationStatus.pendingMsg'/></span>
                        </c:if>
                    </td>
                </c:if>
            </tr>
        </c:forEach>
    </table>
    <br>
    <c:if test="${inProgress}">
        <cti:button nameKey="downloadBtn" disabled="true"/>
        <cti:button nameKey="ftpUploadBtn" disabled="true"/>
        <span class="errorMessage">&nbsp&nbsp <i:inline key='.bundleCreationStatus.inProgressMsg'/> </span>
    </c:if>
    <c:if test="${not inProgress}">
        <cti:url var="downloadUrl" value="download">
            <cti:param name="fileNum" value="0"/>
        </cti:url>
        <cti:button nameKey="downloadBtn" href="${downloadUrl}"/>
        <cti:url var="uploadUrl" value="send">
            <cti:param name="fileNum" value="0"/>
        </cti:url>
        <cti:button nameKey="ftpUploadBtn" href="${uploadUrl}"/>
        <span class="successMessage">&nbsp&nbsp <i:inline key='.bundleCreationStatus.finishedMsg'/></span>
    </c:if>
</cti:msgScope>