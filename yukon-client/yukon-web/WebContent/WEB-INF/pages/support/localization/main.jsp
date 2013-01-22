<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="debug" tagdir="/WEB-INF/tags/debug"%>

<cti:standardPage module="support" page="localization"
    title="${pageTitle}">

    <cti:includeScript link="JQUERY_COOKIE" />

    <script type="text/javascript">
        jQuery(function() {
            jQuery('#tabs').tabs({
                'cookie' : {}
            });
        });
    </script>

    <form:form action="view" modelAttribute="localizationBackingBean">
        <cti:button type="submit" name="task" value="DUMP" nameKey="dumpKeys" />
    </form:form>

    <div id="tabs" class="tabbedContainer">
        <ul>
            <li><a href="#searchTab"><i:inline key=".search" /></a></li>
            <li><a href="#compareTab"><i:inline key=".compare" /></a></li>
        </ul>

        <div id="searchTab">
            <%@ include file="search.jspf"%>
        </div>

        <div id="compareTab">
            <%@ include file="compare.jspf"%>
        </div>
    </div>

    <c:if test="${not empty query}">
        <tags:boxContainer2 nameKey="searchResults" arguments="${query}">
            <div class="striped-list">
                <c:forEach items="${result}" var="entryResult">
                    <div>${entryResult}</div>
                </c:forEach>
                <c:if test="${empty result}">
                    <div class="list-none">
                        <i:inline key=".noResults" />
                    </div>
                </c:if>
            </div>
        </tags:boxContainer2>
    </c:if>

    <c:if test="${compareSets}">
        <c:if test="${empty compareResults}">
            <div class="list-none">
                <i:inline key=".noResults" />
            </div>
        </c:if>
        <c:if test="${!empty compareResults}">
            <table class="resultsTable">
                <thead>
                    <tr>
                        <th><i:inline key=".key" /></th>
                        <th><i:inline key=".modifiedValue" /></th>
                        <th><i:inline key=".baseValue" /></th>
                    </tr>
                </thead>
                <tfoot>
                </tfoot>
                <tbody>
                    <c:forEach items="${compareResults}" var="entryResult">
                        <tr>
                            <td>${entryResult.key}</td>
                            <td>${entryResult.value}</td>
                            <td>${entryResult.secondaryValue}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>
    </c:if>

</cti:standardPage>