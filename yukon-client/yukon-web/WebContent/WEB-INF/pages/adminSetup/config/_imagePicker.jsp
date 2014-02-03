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
        <form action="/common/images" method="post">
            <label for="uploader${cti:escapeJavaScript(category)}" class="uploadLabel">
                <i:inline key=".uploadNew"/>
            </label>
            <input id="uploader${cti:escapeJavaScript(category)}" class="b-upload fr" type="file" name="file" multiple accept="image/*" />
            <div class="upload-progress"></div><div class="upload-percent"></div>
        </form>
    </div>
    <c:forEach var="pickerImage" items="${pickerImages}">
        <div class="section has-show-on-hover">
            <div class="image well<c:if test="${not empty selected and pickerImage.image.imageID == selected}"> selected</c:if>" data-image-id="${pickerImage.image.imageID}">
                <div class="column-12-12 clearfix">
                    <div class="column one">
                        <c:if test="${not empty selected and pickerImage.image.imageID == selected}">
                            <div><strong><em><i:inline key=".currentlySelected"/></em></strong></div>
                        </c:if>
                        <tags:nameValueContainer2>
                            <tags:nameValue2 nameKey=".name" valueClass="f-name-value">${fn:escapeXml(pickerImage.image.imageName)}</tags:nameValue2>
                            <tags:nameValue2 nameKey=".category" valueClass="f-category-value">${fn:escapeXml(pickerImage.image.imageCategory)}</tags:nameValue2>
                            <tags:nameValue2 nameKey=".size" valueClass="f-size-value">
                                <i:inline key="yukon.common.prefixedByteValue.kibi" arguments="${fn:length(pickerImage.image.imageValue) * .001}"/>
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                    </div>
                    <div class="column two nogutter">
                        <div class="simple-input-image fr">
                            <a href="javascript:void(0);"><img alt="${fn:escapeXml(pickerImage.image.imageName)}" src="/common/images/${pickerImage.image.imageID}"></a>
                        </div>
                    </div>
                </div>
                <c:if test="${pickerImage.deletable and pickerImage.image.imageID != selected}">
                    <div class="page-action-area">
                        <cti:button icon="icon-cross" nameKey="delete" classes="show-on-hover delete-image"/>
                        <div class="dn dib fr f-delete-confirm">
                            <span class="fl f-confirm-message"><i:inline key=".delete"/></span>
                            <cti:button nameKey="cancel" classes="cancel"/>
                            <cti:button nameKey="ok" classes="action primary ok"/>
                        </div>
                    </div>
                </c:if>
            </div>
        </div>
    </c:forEach>
</div>
</cti:msgScope>