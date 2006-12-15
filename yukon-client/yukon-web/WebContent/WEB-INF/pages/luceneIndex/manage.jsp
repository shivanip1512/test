<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<cti:standardPage title="Lucene Index Manager" module="blank">
    <cti:includeScript link="/JavaScript/indexManager.js" />

    <h2>
        Manage Indexes
    </h2>
    <hr>
    <table cellpadding="5">
        <th>
            Index Name
        </th>
        <th>
            Date Created
        </th>
        <c:forEach var="index" items="${indexList}">
            <tr>
                <td>
                    <c:out value="${index.indexName}" />
                </td>
                <td>
                    <span id="<c:out value="${index.indexName}" />dateCreated"><c:out value="${index.dateCreated}" /></span>
                </td>
                <td>
                    <span id="<c:out value="${index.indexName}" />percentComplete" style="display:none"> Building...
                        <div id="progressBorder" style="background-color:#cccccc;border:1px solid black;height:20px;width:200px;padding:0px;" align="left">
                            <div id="<c:out value="${index.indexName}" />progressInner" style="position:relative;top:0px;left:0px;background-color:#333333;height:20px;width:0px;padding-top:2px;padding:0px;">
                                <div id="<c:out value="${index.indexName}" />progressText" style="position:relative;top:0px;left:1px;right:1px;color:#f0ffff;height:20px;text-align:center;font:bold;padding:0px;padding-top:2px;">
                                </div>
                            </div>
                        </div> </span> <span id="<c:out value="${index.indexName}" />buildIndex"><a href="javascript:indexManager_buildIndex('<c:out value="${index.indexName}"/>')">Build Index</a></span>
                </td>
                <c:if test="${index.building}">
                    <script type="text/javascript">Event.observe (window, 'load', function  () { indexManager_getProgress('<c:out value="${index.indexName}" />');});</script>
                </c:if>
            </tr>
        </c:forEach>

    </table>
    <hr>

</cti:standardPage>
