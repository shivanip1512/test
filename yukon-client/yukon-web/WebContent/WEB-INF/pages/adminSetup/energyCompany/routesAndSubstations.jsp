`<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:url var="delete" value="/WebConfig/yukon/Icons/delete.gif"/>
<cti:url var="deleteOver" value="/WebConfig/yukon/Icons/delete_over.gif"/>

<cti:standardPage module="adminSetup" page="routesAndSubstations.${mode}">

    <cti:includeCss link="/WebConfig/yukon/styles/admin/energyCompany.css"/>
    <tags:setFormEditMode mode="${mode}"/>
    
    
    <cti:dataGrid cols="2" tableClasses="routeAndSubstationLayout">
      <cti:dataGridCell>
      
        <tags:boxContainer2 nameKey="routes">
            <form action="/spring/adminSetup/energyCompany/routesAndSubstations/edit" method="post">
                <input name="ecId" type="hidden" value="${ecId}">
                
                <c:choose>
                    <c:when test="${empty inheritedRoutes and empty ecRoutes}">
                        <div><i:inline key=".noRoutes"/></div>
                    </c:when>
                    <c:otherwise>
                        <div class="membersContainer">
                            <table class="compactResultsTable">
                                <tr>
                                    <th><i:inline key=".route"/></th>
                                    <th class="removeColumn"><i:inline key=".remove"/></th>
                                </tr>

                                <!-- Inherited Routes -->
                                <c:forEach items="${inheritedRoutes}" var="route" >
                                    <tr>
                                        <td><spring:escapeBody htmlEscape="true">${route.paoName}</spring:escapeBody></td>
                                        <td class="removeColumn"><i:inline key=".inherited" /></td>
                                    </tr>
                                </c:forEach>
                                
                                <!-- EC Routes -->
                                <c:forEach items="${ecRoutes}" var="route" >
                                    <tr>
                                        <td><spring:escapeBody htmlEscape="true">${route.paoName}</spring:escapeBody></td>
                                        <td class="removeColumn">
                                            <input type="image" src="/WebConfig/yukon/Icons/delete.png" 
                                                   class="pointer hoverableImage" name="removeRoute" value="${route.yukonID}">
                                        </td>
                                    </tr>
                                </c:forEach>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
                
                <div>
                    <span class="actionArea" style="float: right;">
                        <select name="routeId">
                            <c:forEach items="${availableRoutes}" var="routeCandidate">
                                <option value="${routeCandidate.yukonID}"><spring:escapeBody htmlEscape="true">${routeCandidate.paoName}</spring:escapeBody></option>
                            </c:forEach>
                        </select>
                        <cti:button key="add" type="submit" name="addRoute"/>
                    </span>
                </div>
            </form>
        </tags:boxContainer2>
    
        <br>
      </cti:dataGridCell>
      <cti:dataGridCell>
        <tags:boxContainer2 nameKey="substations">
            <form action="/spring/adminSetup/energyCompany/routesAndSubstations/edit" method="post">
                <input name="ecId" type="hidden" value="${ecId}">
                
                <c:choose>
                    <c:when test="${empty inheritedSubstations and empty ecSubstations}">
                        <div><i:inline key=".noSubstations"/></div>
                    </c:when>
                    <c:otherwise>
                        <div class="membersContainer">
                            <table class="compactResultsTable">
                                <tr>
                                    <th><i:inline key=".substation"/></th>
                                    <th class="removeColumn"><i:inline key=".remove"/></th>
                                </tr>

                                <!-- Inherited Routes -->
                                <c:forEach items="${inheritedSubstations}" var="substation" >
                                    <tr>
                                        <td><spring:escapeBody htmlEscape="true">${substation.substationName}</spring:escapeBody></td>
                                        <td class="removeColumn"><i:inline key=".inherited" /></td>
                                    </tr>
                                </c:forEach>
                                
                                <!-- EC Routes -->
                                <c:forEach items="${ecSubstations}" var="substation" >
                                    <tr>
                                        <td><spring:escapeBody htmlEscape="true">${substation.substationName}</spring:escapeBody></td>
                                        <td class="removeColumn">
                                            <input type="image" src="/WebConfig/yukon/Icons/delete.png" 
                                                   class="pointer hoverableImage" name="removeSubstation" value="${substation.substationID}">
                                        </td>
                                    </tr>
                                </c:forEach>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
                
                <div>
                    <span class="actionArea" style="float: right;">
                        <select name="substationId">
                            <c:forEach items="${availableSubstations}" var="substationCandidate">
                                <option value="${substationCandidate.substationID}"><spring:escapeBody htmlEscape="true">${substationCandidate.substationName}</spring:escapeBody></option>
                            </c:forEach>
                        </select>
                        <cti:button key="add" type="submit" name="addSubstation"/>
                    </span>
                    
                    <br>
                    <a href="/spring/multispeak/setup/routemapping/mappings"><i:inline key=".substationToRouteMapping" /></a>
                </div>
            </form>
            
            
        </tags:boxContainer2>
      </cti:dataGridCell>
    </cti:dataGrid>
    
</cti:standardPage>