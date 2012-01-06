<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<c:if test="${!inProgress}">
    <script type="text/javascript">
        window.supportBundleProgressUpdater.stop();
    </script>
</c:if>

<cti:msgScope paths="modules.support.supportBundle">
    <table>
        <c:forEach var="writer" items="${writerList}">
            <tr>
                <!-- Optional writers which are not being included will not show up in this list-->
                <c:if test="${not empty thingsDoneMap[writer.name]}"> 
                    <td>
                        <i:inline key=".writerName.${writer.name}" />&nbsp;
                    </td>
                    <td>
                        <c:if test="${thingsDoneMap[writer.name]}">
                            <span class="successMessage"><i:inline key='.bundleCreationStatus.doneMsg'/></span>
                        </c:if>
                        <c:if test="${not thingsDoneMap[writer.name]}">
                            <span class="errorMessage"><i:inline key='.bundleCreationStatus.pendingMsg'/></span>
                        </c:if>
                    </td>
                </c:if>
            </tr>
        </c:forEach>
        <tr>
            <td><br></td>
        </tr>
        <tr>
            <td>
                <i:inline key='.bundleCreationStatus.label'/>
            </td>
            <td>
                <c:if test="${inProgress}">
                    <div class="errorMessage">
                        <i:inline key='.bundleCreationStatus.inProgressMsg'/>
                    </div>               
                </c:if>
                <c:if test="${not inProgress}">
                    <div class="successMessage">
                        <i:inline key='.bundleCreationStatus.finishedMsg'/>
                    </div>
                </c:if>
            </td>
        </tr>
    </table>

    <div class="pageActionArea">
        <c:if test="${inProgress}">
            <cti:button nameKey="downloadBtn" disabled="true"/>
            <cti:button nameKey="ftpUploadBtn" disabled="true"/>
        </c:if>
        <c:if test="${not inProgress}">
            <cti:url var="transferUrl" value="transfer"/>
            <form method="POST" action="${transferUrl}">
                <input type="hidden" name="fileNum" value="0">
                <cti:button nameKey="downloadBtn" name="download" type="submit"/>
                <cti:button nameKey="ftpUploadBtn" name="upload" type="submit" styleClass="f_blocker"/>
            </form>
        </c:if>
    </div>
</cti:msgScope>
