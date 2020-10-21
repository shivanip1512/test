<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<%@ attribute name="id" required="true" description="The id of the tree element."%>
<%@ attribute name="maxHeight" type="java.lang.Integer"
    description="The max-height in pixels for the internal tree div. Example: maxHeight='300'. Default is 500."%>
<%@ attribute name="dataJson" type="java.lang.String" description="A dictionary starting with attributes of the root node."%>
<%@ attribute name="dataUrl" type="java.lang.String" description="A URL indicating how to get the data for the tree. Either dataJson or dataUrl is required."%>

<c:set var="maxHeight" value="${not empty maxHeight and maxHeight > 0  ? maxHeight : 500}" />

<div id="${id}" data-url="${dataUrl}" class="js-fancy-tree" style="width:100%;overflow:auto;max-height:${maxHeight}px;">
    <c:if test="${not empty dataJson}">
        <cti:toJson id="js-json-data" object="${dataJson}"/>
    </c:if>
</div>

<cti:includeScript link="/resources/js/common/yukon.ui.fancyTree.js"/>
<cti:includeScript link="/resources/js/lib/fancytree/jquery.fancytree.min.js" />
<cti:includeCss link="/resources/js/lib/fancytree/skins/skin-lion/ui.fancytree.css" /> 