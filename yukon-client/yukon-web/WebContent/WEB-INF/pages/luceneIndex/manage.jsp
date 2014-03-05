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
                            <div id="progressBorder" style="background-color:#cccccc;border:1px solid black;height:15px;width:75px;padding:0px;" align="left">
                                <div id="<c:out value="${index.indexName}" />progressInner" style="position:relative;top:0px;left:0px;background-color:#88bb88;height:13px;width:0px;padding-top:2px;padding:0px;">
                                    <div id="<c:out value="${index.indexName}" />progressText" style="position:relative;top:0px;left:1px;right:1px;color:#fff;text-align:center;font-weight:bold;padding:0px;">
                                    </div>
                                </div>
                            </div> </span> <span id="<c:out value="${index.indexName}" />buildIndex"><a href="javascript:indexManager_buildIndex('<c:out value="${index.indexName}"/>')">Build Index</a></span>
                    </td>
                    <c:if test="${index.building}">
                        <script type="text/javascript">Event.observe (window, 'load', function  () { indexManager_getProgress('<c:out value="${index.indexName}" />');});</script>
                    </c:if>
                </tr>
            </c:forEach>
        </tbody>
    </table>

</cti:standardPage>