<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="tools" page="bulk.importResults">

  <cti:msg var="headerTitle" key="yukon.common.device.bulk.importResults.header" />
  <tags:sectionContainer title="${headerTitle}" id="importResultsContainer" hideEnabled="false">

    <tags:backgroundProcessResultHolder resultsTypeMsgKey="import" callbackResult="${callbackResult}" />
    <form id="importResultsForm" type="post" action="/bulk/import/upload">
      <div class="page-action-area">
        <button type="submit">
          <span class="b-label"><i:inline key="yukon.common.device.bulk.importResults.backToUpload" /></span>
        </button>
      </div>
      <input type="hidden" name="ignoreInvalidCols" value="${ignoreInvalidCols}">
    </form>
  </tags:sectionContainer>

</cti:standardPage>