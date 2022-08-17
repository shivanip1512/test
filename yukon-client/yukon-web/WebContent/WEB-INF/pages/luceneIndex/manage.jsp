<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="support" page="indexes">

<cti:includeScript link="/resources/js/pages/yukon.index.manager.js"/>

<table id="index-table" class="with-form-controls full-width striped">
    <thead>
        <tr>
            <th><i:inline key=".name"/></th>
            <th><i:inline key=".created"/></th>
            <th></th>
        </tr>
    </thead>
    <tfoot></tfoot>
    <tbody>
        <c:forEach var="index" items="${indexes}">
            <tr data-index="${index.indexName}" data-building="${index.building}">
                <td>${names[index.indexName]}</td>
                <td><span class="js-date">${index.dateCreated}</span></td>
                <td>
                    <div class="progress progress-striped active dn push-down-4">
                        <div class="progress-bar" role="progressbar" aria-valuemin="0" aria-valuemax="100" style="width: 0%;"></div>
                    </div>
                    <cti:button classes="MR0 fr js-build-btn" nameKey="build" icon="icon-database-refresh"/>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>

</cti:standardPage>