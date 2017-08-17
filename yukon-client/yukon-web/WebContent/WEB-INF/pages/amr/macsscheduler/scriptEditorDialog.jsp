<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="error" id="script-error"></div>
<cti:displayForPageEditModes modes="VIEW">
    <c:set var="disableArea" value="disabled" />
</cti:displayForPageEditModes>
<textarea id="script" cols="100" rows="20" ${disableArea}>${script}</textarea>