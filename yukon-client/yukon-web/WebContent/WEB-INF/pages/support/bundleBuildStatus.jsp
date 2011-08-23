<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<cti:msgScope paths="modules.support.supportBundle">
    <table border='0'>
        <c:forEach var="source" items="${sourceList}">
            <tr>
                <!-- Optional sources which are not being included will not show up in this list-->
                <c:if test="${not empty thingsDoneMap[source.sourceName]}"> 
                    <td>
                        <i:inline key=".sourceName.${source.sourceName}" />
                    </td>
                    <td>
                        <c:if test="${thingsDoneMap[source.sourceName]}">
                            <span class="successMessage">&nbsp&nbsp <i:inline key='.bundleCreationStatus.doneMsg'/></span>
                        </c:if>
                        <c:if test="${not thingsDoneMap[source.sourceName]}">
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
        <form id="sendForm" method="POST" action="send">
            <input type=hidden name="fileNum" id="fileNum" value="0"/>
            <cti:button nameKey="downloadBtn" href="download?fileNum=0"/>
            <cti:button nameKey="ftpUploadBtn" href="send?fileNum=0"/>
            <span class="successMessage">&nbsp&nbsp <i:inline key='.bundleCreationStatus.finishedMsg'/></span>
        </form>
    </c:if>
</cti:msgScope>