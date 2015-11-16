<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="modules.support.supportBundle">
    <div class="stacked">
        <tags:nameValueContainer2>
            <c:forEach var="writer" items="${writerList}">
                <c:if test="${not empty thingsDoneMap[writer.name]}"> 
                    <tags:nameValue2 nameKey=".writerName.${writer.name}">
                        <c:if test="${thingsDoneMap[writer.name]}">
                            <span class="success"><i:inline key='.bundleCreationStatus.doneMsg'/></span>
                        </c:if>
                        <c:if test="${not thingsDoneMap[writer.name]}">
                            <span class="error"><i:inline key='.bundleCreationStatus.pendingMsg'/></span>
                        </c:if>
                    </tags:nameValue2>
                </c:if>
            </c:forEach>
        </tags:nameValueContainer2>
    </div>

    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".bundleCreationStatus.label">
            <c:if test="${inProgress}">
                <span class="error">
                    <i:inline key='.bundleCreationStatus.inProgressMsg'/>
                </span>               
            </c:if>
            <c:if test="${not inProgress}">
                <span class="success">
                    <i:inline key='.bundleCreationStatus.finishedMsg'/>
                </span>
            </c:if>
        </tags:nameValue2>
    </tags:nameValueContainer2>
</cti:msgScope>