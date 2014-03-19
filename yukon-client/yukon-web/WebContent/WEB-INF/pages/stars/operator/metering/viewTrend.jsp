<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ page import="com.cannontech.database.db.graph.GraphRenderers" %>
<%@ page import="com.cannontech.graph.GraphBean" %>
<%@ page import="com.cannontech.util.ServletUtil" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%
GraphBean graphBean = null;
graphBean = (GraphBean) session.getAttribute(ServletUtil.ATT_GRAPH_BEAN);
if (graphBean == null) {
    session.setAttribute(ServletUtil.ATT_GRAPH_BEAN, new GraphBean());
    graphBean = (GraphBean)session.getAttribute(ServletUtil.ATT_GRAPH_BEAN);
}

graphBean.setGdefid((request.getParameter("gdefid") == null ? -1 : Integer.parseInt(request.getParameter("gdefid")))); // default to gdefid -1 so no graph is displayed.
graphBean.setPage((request.getParameter("page") == null ? 1 : Integer.parseInt(request.getParameter("page"))));
    
final SimpleDateFormat datePart = new java.text.SimpleDateFormat("MM/dd/yyyy");
%>

<cti:standardPage module="operator" page="metering">

    <%@ include file="/include/trending_functions.jspf" %>
    <cti:includeCss link="/WebConfig/yukon/CannonStyle.css"/>
    
    <c:set var="gdefid" value="<%=graphBean.getGdefid()%>"/>
    <cti:url var="thisUrl" value="/stars/operator/metering/viewTrend">
        <cti:param name="accountId" value="${accountId}"/>
        <cti:param name="gdefid" value="${gdefid}"/>
    </cti:url>
    
    <script type="text/javascript">

        // hack our spring url into trending_options.jspf
        $(function() {
            $('#trendingOptionMFormRedirect').val('${thisUrl}');
            $('#trendingOptionMFormReferrer').val('${thisUrl}');
        });
        
    </script>
    
    <c:set var="hasTrends" value="${fn:length(customerGraphWrappers) > 0}"/>
    

    <table class="trendPageContent">
        <tr>
        
            <td>
            
                <%-- graph header --%>
                <c:set var="selectedTrendName" value=""/>
                <c:forEach var="customerGraphWrapper" items="${customerGraphWrappers}">
                    <c:if test="${gdefid == customerGraphWrapper.customerGraph.graphDefinitionId}">
                        <c:set var="selectedTrendName" value="${customerGraphWrapper.name}"/>
                    </c:if>
                </c:forEach>
                
                <c:choose> <%-- tag can't have scriptlet as body, so these empty containers are a hack to make it look very much like a header --%>
                    <c:when test="${not empty selectedTrendName}">
                        <tags:sectionContainer title="${selectedTrendName}"/>
                    </c:when>
                    <c:otherwise> <%-- there is some weirdness in the graphbean caching the selected trend so it is possible the graph shown after an available trends change wont match, this is a default header --%>
                        <tags:sectionContainer2 nameKey="trend"/> 
                    </c:otherwise>
                </c:choose>
                
                <c:choose>
                
                    <c:when test="${!hasTrends && createTrendAccess}">
                        <cti:msg2 key=".noTrends"/>
                    </c:when>
                    
                    
                    <c:otherwise>
                    
                        <div class="graphArea">
            
                            <%@include file="/include/trending_options.jspf"%>
                            <br><br>
                        
                            <% if(graphBean.getGdefid() <= 0) {%>
                                <i:inline key=".trends.noDataSelected"/>
                            <%} else if( graphBean.getViewType() == GraphRenderers.SUMMARY ) {
                                graphBean.updateCurrentPane();
                                out.println(graphBean.getHtmlString());
                            } else if( graphBean.getViewType() == GraphRenderers.TABULAR) {%>
                                <%@ include file="/include/trending_tabular.jspf" %>                    
                                
                            <%}    else { // "graph" is default %>
                                <cti:url var="graphGeneratorUrl" value="/servlet/GraphGenerator">
                                    <cti:param name="action" value="EncodeGraph"/>
                                </cti:url>
                                <img id="theGraph" src="${graphGeneratorUrl}"> 
                            <%}
                            %>
                            
                            <c:if test="${not empty disclaimer}">
                                <br>
                                <font size="-1">
                                    <spring:escapeBody htmlEscape="true">${disclaimer}</spring:escapeBody>
                                </font>
                            </c:if>
                        </div>
                    
                    </c:otherwise>
                
                </c:choose>
                
            </td>
        
            <c:if test="${hasTrends}">
            <td class="trendsListingTd">
            
                <tags:sectionContainer2 nameKey="trends">
                
                    <table class="trendList">
                    <c:forEach var="customerGraphWrapper" items="${customerGraphWrappers}" varStatus="status">
                        <tr>
                            <td class="nameCol">
                                <cti:url var="trendUrl" value="/stars/operator/metering/viewTrend">
                                    <cti:param name="accountId" value="${accountId}"/>
                                    <cti:param name="gdefid" value="${customerGraphWrapper.customerGraph.graphDefinitionId}"/>
                                </cti:url>
                                <a href="${trendUrl}">${customerGraphWrapper.name}</a>
                            </td>
                        
                            <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_METERING_CREATE">
                            <td class="editCol">
                            <c:choose>
                                <c:when test="${status.count == 1}">
                                    <cti:url var="selectTrendsUrl" value="/stars/operator/metering/selectTrends">
                                        <cti:param name="accountId" value="${accountId}"/>
                                    </cti:url>
                                    <a href="${selectTrendsUrl}"><i:inline key=".trends.selectTrends"/></a>
                                </c:when>
                                <c:otherwise>
                                    &nbsp;
                                </c:otherwise>
                            </c:choose>
                            </td>
                            </cti:checkRolesAndProperties>
                        </tr>
                    </c:forEach>
                    </table>
                
                </tags:sectionContainer2>
            
            </td>
            </c:if>
            
        </tr>
    </table>
    
</cti:standardPage>