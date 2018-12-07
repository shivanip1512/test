<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="date" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<cti:standardPage module="dev" page="nest.viewControlEvents">

    <div class="column-12-12">
    
        <div class="column one">
    
        <cti:msg2 key=".controlEvent.helpText" var="helpText"/>
        <tags:sectionContainer title="Control Event Generation" helpText="${helpText}">
            <!-- Sending control events to Nest -->
            <cti:url var="sendEventUrl" value="sendEvent"/>
            <form:form id="controlEventNestForm" modelAttribute="nestParameters" method ="POST" action="${sendEventUrl}" >
                <cti:csrfToken/>
                <tags:nameValueContainer2>
                     <tags:nameValue2 nameKey=".controlEvent.controlMethod">
                         <form:select path="controlMethod" id= "controlMethod">
                             <c:forEach var="controlMethodType" items="${controlMethods}">
                                 <form:option value="${controlMethodType}">
                                     <i:inline key="${controlMethodType}" />
                                 </form:option>
                             </c:forEach>
                         </form:select>
                     </tags:nameValue2>
                     <tags:nameValue2 label=".controlEvent.startTime">
                        <date:dateTime path="startTime" value="${startTime}"/>
                     </tags:nameValue2>
                     <tags:nameValue2 nameKey=".controlEvent.stopTime">
                         <date:dateTime path="stopTime" value="${stopTime}"/>
                      </tags:nameValue2>
                      <tags:nameValue2 nameKey=".controlEvent.groupName">
                          <input id="groupName" name="groupName" type="text" maxlength="50" size="35">
                      </tags:nameValue2>
                      <tags:nameValue2 nameKey=".controlEvent.loadShapingPreparation">
                          <form:select path="loadShapingPreparation">
                              <c:forEach var="preparationType" items="${loadShapingPreparations}">
                                  <form:option value="${preparationType}">
                                      <i:inline key="${preparationType}" />
                                  </form:option>
                              </c:forEach>
                          </form:select>
                      </tags:nameValue2>
                      <tags:nameValue2 nameKey=".controlEvent.loadShapingPeak">
                          <form:select path="loadShapingPeak">
                              <c:forEach var="peakType" items="${loadShapingPeaks}">
                                  <form:option value="${peakType}">
                                      <i:inline key="${peakType}" />
                                  </form:option>
                              </c:forEach>
                          </form:select>
                      </tags:nameValue2>
                      <tags:nameValue2 nameKey=".controlEvent.loadShapingPost">
                          <form:select path="loadShapingPost">
                              <c:forEach var="postType" items="${loadShapingPosts}">
                                  <form:option value="${postType}">
                                      <i:inline key="${postType}" />
                                  </form:option>
                              </c:forEach>
                          </form:select>
                       </tags:nameValue2>
                </tags:nameValueContainer2>
                <div class="PT10">
                    <cti:button id="send-button" label="Send Event" type="submit"/>
                </div>
            </form:form>
        </tags:sectionContainer>
            
        <div class="PT10">
            <cti:url var="stopEventUrl" value="stopEvent"/>
            <form:form method ="POST" action="${stopEventUrl}">
                <cti:csrfToken/>
                <span class="fl">Event ID: <input name="eventId" type="text" value="${eventKey}" size="25"></span>
                <cti:button label="Stop Event" type="submit"/>
                <cti:button id="cancel-button" label="Cancel Event"/>
            </form:form>
        </div>
        <br/><br/>
        
        </div>
        <div class="column two nogutter">
            <tags:sectionContainer title="Error Generation">
                <cti:url var="setErrorsUrl" value="setErrors"/>
                <form:form method="POST" action="${setErrorsUrl}" modelAttribute="nestSimulatorConfiguration">
                    <cti:csrfToken/>
                    <tags:nameValueContainer2>
                       <tags:nameValue2 nameKey=".controlEvent.startError">
                         <form:select path="startError">
                            <option label="NONE"></option>
                              <c:forEach var="errorType" items="${errorTypes}">
                                <form:option value="${errorType}"></form:option>
<%--                                 <c:set var="selectedText" value="${errorType == selectedError ? 'selected=selected' : ''}"/>
                                  <option label="${errorType}" value="${errorType}" ${selectedText}></option> --%>
                              </c:forEach>
                          </form:select>
                       </tags:nameValue2>
                       <tags:nameValue2 nameKey=".controlEvent.stopError">
                            <tags:input path="stopError"/>
                       </tags:nameValue2>
                       <tags:nameValue2 nameKey=".controlEvent.cancelError">
                        <tags:input path="cancelError"/>
                       </tags:nameValue2>
                   </tags:nameValueContainer2>
                   <div class="PT10">
                        <cti:button label="Set Error" type="submit"/>
                    </div>
               </form:form>
            </tags:sectionContainer>
        </div>
    </div>
    <div class="column-24">
        <div class="column one">
            <tags:sectionContainer title="Response">
                <div class="code pr">
                    <div id="responseDiv">${message}</div>
                </div>
            </tags:sectionContainer>
        </div>
    </div>
    <cti:includeScript link="/resources/js/pages/yukon.dev.simulators.nestSimulator.js" />
</cti:standardPage>