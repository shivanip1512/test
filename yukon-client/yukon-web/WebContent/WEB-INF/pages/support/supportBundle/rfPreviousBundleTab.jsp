<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<cti:msgScope paths="modules.support">
       
       <cti:url value="/support/downloadBundle" var="downloadBundleURl" />
       <form id="previousRfBundlesForm" action="${downloadBundleURl}" method="POST">
            <cti:csrfToken />
            <input type="hidden" name="isRfBundle" value="true"></input>
            <c:if test="${empty rfBundleList}">
                <span class="empty-list"><i:inline key=".supportBundle.noPreviousBundlesLbl" /></span>
            </c:if>
            <c:if test="${not empty rfBundleList}">
                <ul class="simple-list">
                    <c:forEach var="rfBundleName" varStatus="status" items="${rfBundleList}">
                        <li>
                            <c:set var="checked" value="${status.first} ? 'checked=checked' : ''"/>
                            <label><input type="radio" name="fileName" value="${rfBundleName}" ${checked}>${fn:escapeXml(rfBundleName)}</label>
                        </li>
                    </c:forEach>
                </ul>
            </c:if>
            <div class="page-action-area">
                <cti:button nameKey="supportBundle.downloadBtn" type="submit" disabled="${empty rfBundleList}" icon="icon-download" />
            </div>
        </form>
        
 </cti:msgScope>