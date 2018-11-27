<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="date" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<cti:standardPage module="dev" page="nest.viewControlEvents">

    <cti:msg2 key=".controlEvent.helpText" var="helpText"/>
    <div class="column-8-8-8">
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
                          <input id="groupName" name="groupName" type="text" maxlength="50" size="50">
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

                <div class="column nogutter">
                    <div class="page-action-area">
                        <cti:button id="send-button" label="Send Event" type="submit"/>
                    </div>
                </div>
                
            </form:form>
        </tags:sectionContainer>
        <tags:sectionContainer title="Response">
            <div class="code pr">
                <div id="responseDiv">${message}</div>
            </div>
            <c:if test="${!empty message}">
                <cti:url var="stopEventUrl" value="stopEvent"/>
                <form:form method ="POST" action="${stopEventUrl}" >
                    <cti:csrfToken/>
                    <span class="fl">Event: <input name="eventId" type="text" value="${eventKey}" size="40"></span>
                    <cti:button label="Stop Event" type="submit"/>
                    <cti:button id="cancel-button" label="Cancel Event"/>
                </form:form>
            </c:if>
        </tags:sectionContainer>
    </div>
    <cti:includeScript link="/resources/js/pages/yukon.dev.simulators.nestSimulator.js" />
</cti:standardPage>