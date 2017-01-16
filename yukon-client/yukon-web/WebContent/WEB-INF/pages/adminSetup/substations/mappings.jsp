<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<cti:url var="routeUrl" value="/admin/substations/routeMapping/viewRoute" />
<cti:standardPage page="substationToRouteMapping" module="adminSetup">
<cti:msg2 var="removeRouteTitle" key="yukon.web.modules.adminSetup.substationToRouteMapping.removeRoutes"/>
    <div id="saveStatusMessage"></div>
    <div id="route-template" class="dn">
        <table>
            <tr>
                <td><input type="hidden" value="${route.id}" class="routeId"></td>
                <td>
                    <div class="button-group fr wsnw oh">
                                    <cti:button icon="icon-cross" id="removeRouteBtn_${route.id}" name="removeRoute" value="${route.id}" type="submit" 
                                    title="${removeRouteTitle}" renderMode="buttonImage" disabled="true"/>
                                    <c:set var="disableUp" value="${row.first}" />
                                    <c:set var="disableDown" value="${row.last}" />
                                    <cti:button icon="icon-bullet-go-up" classes="js-up right M0"
                                        renderMode="buttonImage" disabled="${disableUp}" />
                                    <cti:button icon="icon-bullet-go-down" classes="js-down left M0"
                                        renderMode="buttonImage" disabled="${disableDown}" />
                    </div>
                </td>
            </tr>
        </table>
    </div>
    
    <cti:includeScript link="/resources/js/pages/yukon.substation.route.mapping.js" />

    <cti:url var="action" value="/admin/substations/routeMapping/save"/>
            <form:form id="substation-form" name="substationForm" commandName="substationRouteMapping" action="${action}">
                <cti:csrfToken/>
                <tags:simpleDialog id="mspAddDialog"/>
                <cti:url var="mspAddUrl" value="/admin/substations/routemapping/multispeak/choose"/>
                <tags:nameValueContainer2 tableClass="with-form-controls">
                
                
                        <tags:nameValue2 nameKey=".substation">
                        <c:choose>
                        <c:when test="${not empty list}">
                            <form:select id="substation" path="substationId" cssClass="fl" onchange="submitTypeSelect()">
                                <form:options items="${list}" itemValue="id" title="name" itemLabel="name"/>
                            </form:select>
                            </c:when>
                            <c:otherwise><span><i:inline key=".noSubstations"/></span>
                            </c:otherwise>
                                </c:choose>
                        </tags:nameValue2>
                       
                       <tr>
                         <tags:nameValue2 excludeColon = "true">
                           <form:input id="substationName" name="substationName" path="substationName" placeholder="Substation Name"/>
                           <td>
                               <cti:button nameKey="add" icon="icon-add" id="b-add" type="submit" name="addSubstation"/>
                               <c:if test="${hasVendorId}"> 
                                 <cti:button nameKey="mspSubstation" icon="icon-add" id="b-add" onclick="openSimpleDialog('mspAddDialog', '${mspAddUrl}', 'Choose Substations');"/>
                               </c:if>
                           </td>
                         </tags:nameValue2>
                       </tr>
                </tags:nameValueContainer2>
                <br>
               <d:confirm on="#removeSubstation" nameKey="confirmDelete" argument="Current Substation"/>
               <d:confirm on="#removeAllSubstations" nameKey="confirmDelete" argument="All Substations"/>
            <tags:sectionContainer2 nameKey="substationRouteMapping">
            
                <div class="column-10-4-10 clearfix">
                  <div class="column one">
                    <tags:boxContainer2 nameKey="assignedRoutes" styleClass="stacked"> 
                    <div class="scroll-lg" style="height:200px;">
                      <table id="routes-table" class="compact-results-table dashed with-form-controls">
                        <thead>
                          <tr>
                            <th><i:inline key=".route"/></th>
                            <th style="width: 155px;"></th>
                          </tr>
                       </thead>
                
                       <tbody>
                         <c:forEach var="route" items="${substationRouteMapping.routeList}" varStatus="row">
                         <div>
                             <d:confirm on="#removeRouteBtn_${route.id}" nameKey="confirmDelete" argument="${route.name}" />
                         </div>
                           <tr>
                             <td>
                                <input type="hidden" value="${route.id}" class="routeId">
                                <span>${route.name}</span>
                             </td>
                            <td>
                              <div class="button-group fr wsnw oh">
                                <cti:button icon="icon-cross" id="removeRouteBtn_${route.id}" name="removeRoute" value="${route.id}" type="submit" renderMode="buttonImage" />
                                    <c:set var="disableUp" value="${row.first}" />
                                    <c:set var="disableDown" value="${row.last}" />
                                    <cti:button icon="icon-bullet-go-up" classes="js-up right M0"
                                        renderMode="buttonImage" disabled="${disableUp}" />
                                    <cti:button icon="icon-bullet-go-down" classes="js-down left M0"
                                        renderMode="buttonImage" disabled="${disableDown}" />
                              </div>
                           </td>
                        </tr>
                      </c:forEach>
                  </tbody>
                </table>
                </div>
              </tags:boxContainer2>
            </div>
            <div class="column two">
              <div class="action-area" style="width:80%;padding-top:100px;">
                <c:set var="disabled" value="${empty list}"/>
                <cti:button id="b-add-route" nameKey="add" icon="icon-arrow-left" disabled="${disabled}"/>
              </div>
           </div>
           <div class="column three nogutter">
             <tags:boxContainer2 nameKey="availableRoutes"> 
               <c:choose>
                 <c:when test="${substationRouteMapping.avList!=null}">
                    <div style="height:200px;">
                     <form:select path="selectedRoutes" multiple="true" size="11" cssStyle="width:100%;">
                       <form:options class="selectedRoutes" id="routes" items="${substationRouteMapping.avList}" itemValue="id" title="name" itemLabel="name"/> 
                       <form:options items="${avList}" itemValue="id" title="name" itemLabel="name"/>
                     </form:select> 
                  </div>
                </c:when>
                <c:otherwise><span><i:inline key=".noRoutes"/></span></c:otherwise>
              </c:choose>
            </tags:boxContainer2> 
          </div>
          </div>
       </tags:sectionContainer2>
       <div class="page-action-area">
         <cti:button id="saveAllRoutes" nameKey="save" busy="true"/>
         <cti:button id="removeSubstation" nameKey="delete" classes="delete js-delete" name="removeSubstation" type="submit"/>
       </div>
      </form:form>
<div id="addRoute-popup"></div>
</cti:standardPage>