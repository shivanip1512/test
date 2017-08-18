<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<cti:displayForPageEditModes modes="VIEW">
    <c:set var="disableArea" value="readonly" />
</cti:displayForPageEditModes>
<textarea id="script" cols="100" rows="20" ${disableArea} style="resize:none;">${script}</textarea>