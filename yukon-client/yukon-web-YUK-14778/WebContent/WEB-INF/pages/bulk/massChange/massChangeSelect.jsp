<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="tools" page="bulk.massChangeSelect">

<script type="text/javascript">
function submitForm(selectMassChangeField) {
  document.getElementById('massChangeBulkFieldName').value = selectMassChangeField;
  document.getElementById('massChangeSelectForm').submit();
}
</script>

  <tags:bulkActionContainer key="yukon.common.device.bulk.massChangeSelect" deviceCollection="${deviceCollection}">

    <cti:url var="massChange" value="/bulk/massChangeOptions" />
    <form id="massChangeSelectForm" method="get" action="${massChange}">

      <%-- DEVICE COLLECTION --%>
      <cti:deviceCollection deviceCollection="${deviceCollection}" />

      <%-- CHANGES BUTTONS --%>
      <cti:checkRolesAndProperties value="DEVICE_ACTIONS">
        <cti:checkRolesAndProperties value="MASS_CHANGE">
          <input type="hidden" id="massChangeBulkFieldName" name="massChangeBulkFieldName" value="">
          <c:forEach var="bulkField" items="${massChangableBulkFields}">
            <div class="page-action-area">
              <button id="massChangeTypeButton" onclick="submitForm('${bulkField.inputSource.field}');">
                <span class="b-label"><cti:msg key="${bulkField.displayKey}"/></span>
              </button>
              <span><i:inline key="${bulkField.displayKey}.description"/></span>
            </div>
          </c:forEach>
        </cti:checkRolesAndProperties>
      </cti:checkRolesAndProperties>
    </form>

  </tags:bulkActionContainer>

</cti:standardPage>