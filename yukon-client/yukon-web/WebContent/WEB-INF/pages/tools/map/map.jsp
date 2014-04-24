<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="tools" page="map">

<style>
.map {
  height: 600px;
  width: 100%;
  border: 1px solid #bbb;
  box-shadow: 0px 0px 5px #ddd;
}
#filter-form .chosen-results {max-height: 100px;}
</style>
    
    <div id="page-buttons"><cti:button icon="icon-filter" nameKey="filter" popup="#map-popup"/></div>
    <div id="map-popup" dialog class="dn" data-title="<cti:msg2 key=".filter.title"/>" data-form data-width="500" data-height="250">
        <cti:url value="/tools/map/filter" var="filterUrl"/>
        <form:form commandName="filter" id="filter-form" action="${filterUrl}">
            <cti:deviceCollection deviceCollection="${deviceCollection}" />
            <tags:nameValueContainer2 tableClass="with-form-controls" naturalWidth="false">
                <cti:msg2 key=".chooseAttribute" var="chooseAttribute"/>
                <tags:selectNameValue nameKey=".attribute"
                                      path="attribute"
                                      items="${attributes}"
                                      itemLabel="message"
                                      itemValue="key"
                                      groupItems="true"
                                      id="attribute-select"
                                      defaultItemLabel="${chooseAttribute}"
                                      defaultItemValue="-1"/>
                <tags:nameValue2 nameKey=".states" valueClass="full-width">
                    <div id="waiting-for-states" class="dn"><cti:icon icon="icon-spinner" style="margin-top:5px;"/><i:inline key=".retrievingStates"/></div>
                    <div id="no-states-for-attribute" class="dn"><cti:icon icon="icon-error" style="margin-top:5px;"/><i:inline key=".noStatesForAttribute"/></div>
                    <div id="filter-states"></div>
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </form:form>
    </div>
    <div id="state-group-template" class="dn">
        <input type="hidden" name="groups[?].id">
        <select name="groups[?].state"></select>
    </div>
    
    <div class="stacked clearfix">
        <tags:selectedDevices deviceCollection="${deviceCollection}" id="selectedDevices"/>
    </div>

    <div id="map" class="clearfix map f-focus" tabindex="0"></div>
    <div id="mouse-position" class="detail"></div>
    
    <cti:url value="/tools/map/locations" var="locationsUrl">
        <c:forEach items="${deviceCollection.collectionParameters}" var="cp">
            <cti:param name="${cp.key}" value="${cp.value}"/>
        </c:forEach>
    </cti:url>
    <input id="locations" type="hidden" value="${fn:escapeXml(locationsUrl)}">
    
    <cti:url value="/tools/map/filter/state-groups" var="stateGroupBaseUrl">
        <c:forEach items="${deviceCollection.collectionParameters}" var="cp">
            <cti:param name="${cp.key}" value="${cp.value}"/>
        </c:forEach>
    </cti:url>
    <input id="state-group-base-url" type="hidden" value="${fn:escapeXml(stateGroupBaseUrl)}">

    <cti:includeScript link="OPEN_LAYERS"/>
    <cti:includeCss link="/resources/js/lib/open-layers/ol.css"/>
    <cti:includeScript link="/JavaScript/yukon.tools.map.js"/>
</cti:standardPage>