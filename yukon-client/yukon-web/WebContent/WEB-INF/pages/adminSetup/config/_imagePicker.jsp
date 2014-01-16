<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<cti:msgScope paths="yukon.common.imagePicker">
<div class="separated-sections image-picker" data-category="${category}">
    <div class="section clearfix">
        <form id="file-upload" action="/common/images" method="post">
            <input id="uploader" class="b-upload fr" type="file" name="file" multiple accept="image/*" />
            <div class="upload-progress"></div><div class="upload-percent"></div>
        </form>
    </div>
    <c:forEach var="image" items="${images}">
        <div class="section">
            
            <div class="column-12-12 clearfix image<c:if test="${not empty selected and image.imageID == selected}"> selected</c:if>" data-image-id="${image.imageID}">
                <div class="column one">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".name" valueClass="f-name-value">${fn:escapeXml(image.imageName)}</tags:nameValue2>
                        <tags:nameValue2 nameKey=".category" valueClass="f-category-value">${fn:escapeXml(image.imageCategory)}</tags:nameValue2>
                        <tags:nameValue2 nameKey=".size" valueClass="f-size-value">
                            <i:inline key="yukon.common.prefixedByteValue.kibi" arguments="${fn:length(image.imageValue) * .001}"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </div>
                <div class="column two nogutter">
                    <div class="simple-input-image fr">
                        <a href="javascript:void(0);"><img alt="${fn:escapeXml(image.imageName)}" src="/common/images/${image.imageID}"></a>
                    </div>
                </div>
            </div>
        </div>
    </c:forEach>
    <script type="text/javascript">
    jQuery(function () {
        // init now that all the pieces are in place
        Yukon.Themes.initFileUpload();
    });
    </script>
</div>
</cti:msgScope>