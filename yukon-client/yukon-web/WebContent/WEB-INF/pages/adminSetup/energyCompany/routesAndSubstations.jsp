<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="routesAndSubstations.${mode}">

    <tags:setFormEditMode mode="${mode}"/>
    <cti:url value="edit" var="editUrl" />
    
    <c:choose>
	    <c:when test="${routeAccess}">
	        <c:set var="layout" value="column-12-12"/>
	        <c:set var="subCol" value="two nogutter"/>
	    </c:when>
	    <c:otherwise>
            <c:set var="layout" value="column-24"/>
            <c:set var="subCol" value="one nogutter"/>
	    </c:otherwise>
    </c:choose>

    <div class="${layout}">
        <c:if test="${routeAccess}">
            <div class="column one">
	            <tags:boxContainer2 nameKey="routes">
	                <form action="${editUrl}" method="post">
                        <cti:csrfToken/>
	                    <input type="hidden" name="ecId" value="${ecId}"/>
	                    <c:choose>
	                        <c:when test="${empty inheritedRoutes and empty ecRoutes}">
	                            <div><i:inline key=".noRoutes"/></div>
	                        </c:when>
	                        <c:otherwise>
	                            <div class="membersContainer scroll-lg">
	                                <table class="compact-results-table">
	                                    <thead>
	                                        <tr>
	                                            <th><i:inline key=".route"/></th>
	                                            <c:if test="${not isSingleEnergyCompany}">
	                                                <th class="remove-column"><i:inline key=".remove"/></th>
	                                            </c:if>
	                                        </tr>
	                                    </thead>
	                                    <tfoot></tfoot>
	                                    <tbody>
	                                        <c:forEach items="${ecRoutes}" var="route" >
	                                            <tr>
	                                                <td><spring:escapeBody htmlEscape="true">${route.key.paoName}</spring:escapeBody></td>
	                                                <c:if test="${not isSingleEnergyCompany}">
	                                                    <c:if test="${not route.value.deletable}">
	                                                        <td class="remove-column">
	                                                            <i:inline key="${route.value}" />
	                                                        </td>
	                                                    </c:if>
	                                                    <c:if test="${route.value.deletable}">
	                                                        <td class="remove-column">
	                                                            <div class="dib">
	                                                                <cti:button nameKey="remove" name="removeRoute" value="${route.key.yukonID}" type="submit" renderMode="image" icon="icon-remove"/>
	                                                            </div>
	                                                        </td>
	                                                    </c:if>
	                                                </c:if>
	                                            </tr>
	                                        </c:forEach>
	                                    </tbody>
	                                </table>
	                            </div>
	                        </c:otherwise>
	                    </c:choose>
	                    
	                    <div>
	                        <c:if test="${not empty availableRoutes}">
	                            <span class="action-area" style="float: right;">
	                                <select name="routeId">
	                                    <c:forEach items="${availableRoutes}" var="routeCandidate">
	                                        <option value="${routeCandidate.yukonID}"><spring:escapeBody htmlEscape="true">${routeCandidate.paoName}</spring:escapeBody></option>
	                                    </c:forEach>
	                                </select>
	                                <cti:button nameKey="add" type="submit" name="addRoute" icon="icon-add"/>
	                            </span>
	                        </c:if>
	                    </div>
	                </form>
	            </tags:boxContainer2>
            </div>
        </c:if>
        
        <div class="column ${subCol}">
        
	        <tags:boxContainer2 nameKey="substations">
	
	            <form action="${editUrl}" method="post">
                    <cti:csrfToken/>
	                <input type="hidden" name="ecId" value="${ecId}"/>
	                
	                <c:choose>
	                    <c:when test="${empty inheritedSubstations and empty ecSubstations}">
	                        <div><i:inline key=".noSubstations"/></div>
	                    </c:when>
	                    <c:otherwise>
	                        <div class="membersContainer">
	                            <table class="compact-results-table">
	                            	<thead>
    	                                <tr>
	                                        <th><i:inline key=".substation"/></th>
	                                        <th class="remove-column"><i:inline key=".remove"/></th>
	                                    </tr>
	                                </thead>
	                                <tfoot></tfoot>
	                                <tbody>
	                                <!-- Inherited Routes -->
	                                <c:forEach items="${inheritedSubstations}" var="substation" >
	                                    <tr>
	                                        <td><spring:escapeBody htmlEscape="true">${substation.substationName}</spring:escapeBody></td>
	                                        <td class="remove-column"><i:inline key=".inherited" /></td>
	                                    </tr>
	                                </c:forEach>
	                                
	                                <!-- EC Routes -->
	                                <c:forEach items="${ecSubstations}" var="substation" >
	                                    <tr>
	                                        <td><spring:escapeBody htmlEscape="true">${substation.substationName}</spring:escapeBody></td>
	                                        <td class="dib">
                                                <cti:button nameKey="remove" renderMode="image" type="submit" name="removeSubstation" value="${substation.substationID}" icon="icon-remove"/>
	                                        </td>
	                                    </tr>
	                                </c:forEach>
	                                </tbody>
	                            </table>
	                        </div>
	                    </c:otherwise>
	                </c:choose>
	                
	                <div class="action-area">
	                    <c:if test="${not empty availableSubstations}">
                            <cti:button nameKey="add" type="submit" name="addSubstation" icon="icon-add"/>
                            <select name="substationId" class="fr">
                                <c:forEach items="${availableSubstations}" var="substationCandidate">
                                    <option value="${substationCandidate.substationID}"><spring:escapeBody htmlEscape="true">${substationCandidate.substationName}</spring:escapeBody></option>
                                </c:forEach>
                            </select>
	                    </c:if>
	                    <a href="createSubstation" ><i:inline key=".substationToRouteMapping"/></a>
	                </div>
	            </form>
	            
	        </tags:boxContainer2>
        </div>
    </div>    
</cti:standardPage>