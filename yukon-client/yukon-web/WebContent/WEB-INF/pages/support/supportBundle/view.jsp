<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:standardPage module="support" page="supportBundle">
    <cti:dataGrid cols="2" tableClasses="collectionActionAlignment collectionActionCellPadding">
        <cti:dataGridCell>
            <cti:url var="createUrl" value="create"/>
            <form:form commandName="supportBundle" action="${createUrl}" method="POST">
                <h1><i:inline key=".createNewHeading"/></h1><br>
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".custNameLbl">
                        <tags:input path="customerName"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".dateRangeSelect">
                        <form:select path="bundleRangeSelection">
                            <c:forEach var="rangeSel" items="${bundleRangeSelection}">
                                <form:option value="${rangeSel}">
                                    <cti:msg2 key=".bundleRangeSelection.${rangeSel}"/>
                                </form:option>
                            </c:forEach>
                        </form:select>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".includeLbl">
                        <c:forEach var="writer" items="${writerList}">
                            <c:if test="${writer.optional}">
                                <form:checkbox path="optionalWritersToInclude" value="${writer.name}"/> 
                                <i:inline key=".writerName.${writer.name}"/><br>
                            </c:if>
                        </c:forEach>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".notesLbl">
                        <form:textarea rows="6" cols="40" path="comments"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
                <div class="pageActionArea"> 
                    <c:if test="${!inProgress}">
                        <cti:button nameKey="createBundleBtn" type="submit" styleClass="f_blocker"/>
                    </c:if>
                    <c:if test="${inProgress}">
                        <i:inline key=".bundleInProgressMsg"/>
                        <cti:url var="viewProgressUrl" value="viewProgress"/>
                        <cti:button nameKey="viewProgressBtn" id="viewProgressBtn" href="${viewProgressUrl}"/>
                    </c:if>
                </div>
            </form:form>
        </cti:dataGridCell>
        <cti:dataGridCell>
            <cti:url var="transferUrl" value="transfer"/>
            <form method="POST" action="${transferUrl}">
                <h1><i:inline key=".previousHeading"/></h1><br>
                <c:if test="${empty bundleList}">
                    <i:inline key=".noPreviousBundlesLbl"/>
                </c:if>
                <c:if test="${not empty bundleList}">
                    <table>
                        <c:forEach var="bundle" varStatus="status" items="${bundleList}">
                            <tr>
                                <td><input type="radio" name="fileNum"
                                    <c:if test="${status.first}">checked="checked"</c:if>
                                    value="${status.index}"/>${fn:escapeXml(bundle.name)}</td>
                            </tr>
                        </c:forEach>
                    </table>
                </c:if>
                <div class="pageActionArea">
                    <cti:button nameKey="downloadBtn" name="download" type="submit"
                        disabled="${empty bundleList}"/>
                    <cti:button nameKey="ftpUploadBtn" name="upload" type="submit"
                        disabled="${empty bundleList}" styleClass="f_blocker"/>
                </div>
            </form>
        </cti:dataGridCell>
    </cti:dataGrid>
</cti:standardPage>
