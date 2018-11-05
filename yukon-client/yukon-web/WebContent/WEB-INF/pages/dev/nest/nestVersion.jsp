<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<cti:standardPage module="dev" page="nest.viewNestVersion">
    <!-- Set Nest Version to use -->
    <tags:sectionContainer title="Set Nest Version">
    <cti:url var="saveVersion" value="saveNestVersion" />
    <form id="saveVersionForm" action="${saveVersion}" method="POST">
    <cti:csrfToken/>
       <div class="column-8-8-8">
           <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".nestVersion.version">
                          <select name="version">
                              <c:forEach var="availableVersion" items="${allAvailableVersions}">
                                  <option value="${availableVersion}" "<c:if test="${availableVersion == savedNestVersion}"> selected="selected" </c:if>>
                                      ${availableVersion}
                                  </option>
                              </c:forEach>
                          </select>
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </div>
        <div class="column nogutter">
            <div class="action-area">
                <button id="save" type="submit" classes="js-blocker">Save</button>
            </div>
        </div>
    </form>
    </tags:sectionContainer>
</cti:standardPage>
