<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<cti:standardPage module="support" page="indexes">

    <cti:includeScript link="/JavaScript/yukon.index.manager.js" />

    <table class="compact-results-table">
        <thead>
            <tr>
                <th>Index Name</th>
                <th>Date Created</th>
                <th></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach var="index" items="${indexList}">
                <tr>
                    <td>
                        <c:out value="${index.indexName}" />
                    </td>
                    <td>
                        <span id="<c:out value="${index.indexName}" />dateCreated"><c:out value="${index.dateCreated}" /></span>
                    </td>
                    <td>
                        <span id="<c:out value="${index.indexName}" />percentComplete" style="display:none">
                            <div class="progress progress-striped active">
                              <div class="progress-bar progress-bar-success" role="progressbar" aria-valuemin="0" aria-valuemax="100" style="width: 0%;"></div>
                            </div>
                        </span> 
                            
                        <span id="<c:out value="${index.indexName}" />buildIndex"><a href="javascript:indexManager_buildIndex('<c:out value="${index.indexName}"/>')">Build Index</a></span>
                    </td>
                    <c:if test="${index.building}">
                        <script>$(function() {indexManager_getProgress('<c:out value="${index.indexName}" />');});</script>
                    </c:if>
                </tr>
            </c:forEach>
        </tbody>
    </table>

</cti:standardPage>