<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="icons">
<tags:styleguide page="icons">

<style>
.description { line-height: 22px; }
.card { margin-bottom: 20px; }
</style>
<div class="description">
    Yukon uses 18 and 32 pixel icons based on the <a href="https://brightlayer-ui.github.io/style/iconography" target="_blank">Brightlayer UI Icon Standards</a>.
    To use new icons within Yukon, download the SVG for the icon from the website above. Add this new SVG to our IconSvg.java class and reference the new class name to use the icon. 
    Yukon has a <span class="label label-attr">&lt;cti:icon&gt;</span> tag to create icons. You only need to set the icon attribute 
    to the icon class name desired.  For 32px icons add a class of <span class="label label-attr">.icon-32</span>.  Most 
    components that would use icons already have them built in. See <a href="buttons#icon-example">buttons</a>, and 
    <a href="buttons#drop-down-example">drop-downs</a>.
<h4 class="subtle">Example:</h4>
<div class="buffered clearfix">
    <cti:icon icon="icon-chart-bar"/>
    <cti:icon icon="icon-app-32-lightbulb" classes="icon-app"/>
    <cti:icon icon="icon-32-cloud" classes="icon-32"/>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;buffered clearfix&quot;&gt;
    &lt;cti:icon icon=&quot;icon-chart-bar&quot;/&gt;
    &lt;cti:icon icon=&quot;icon-app-32-lightbulb&quot; classes=&quot;icon-app&quot;/&gt;
    &lt;cti:icon icon=&quot;icon-32-cloud&quot; classes=&quot;icon-32&quot;/&gt;
&lt;/div&gt;
</pre>
</div>

<h2>Icon Class Names</h2>
<h3 class="subtle">18px Icons</h3>
<c:forEach items="${sprites16Array}" var="spriteProperty" varStatus="status">
    <c:choose>
        <c:when test="${status.index % 4 == 0}">
            <div class="column-6-6-6-6 clearfix">
            <c:set var="classes" value="one"/>
        </c:when>
        <c:when test="${status.index % 4 == 1}">
            <c:set var="classes" value="two"/>
        </c:when>
        <c:when test="${status.index % 4 == 2}">
            <c:set var="classes" value="three"/>
        </c:when>
        <c:when test="${status.index % 4 == 3}">
            <c:set var="classes" value="four nogutter"/>
        </c:when>
    </c:choose>
    
        <div class="card bg-light column ${classes}">
            <div class="card-body">
                <div class="clearfix" style="text-align:center;">
                    <cti:icon icon="${spriteProperty}" classes="fn"/>
                </div>
                <div style="text-align:center">
                    <span ><c:out value="${spriteProperty}" /></span>
                </div>
            </div>
        </div>
        
    <c:if test="${status.index % 4 == 3 or status.last}"></div></c:if>
</c:forEach>

<h3 class="subtle">New 32px Icons</h3>
<c:forEach items="${spritesNew32Array}" var="sprite32Property" varStatus="status">
    <c:choose>
        <c:when test="${status.index % 4 == 0}">
            <div class="column-6-6-6-6 clearfix">
            <c:set var="classes" value="one"/>
        </c:when>
        <c:when test="${status.index % 4 == 1}">
            <c:set var="classes" value="two"/>
        </c:when>
        <c:when test="${status.index % 4 == 2}">
            <c:set var="classes" value="three"/>
        </c:when>
        <c:when test="${status.index % 4 == 3}">
            <c:set var="classes" value="four nogutter"/>
        </c:when>
    </c:choose>
    
        <div class="card bg-light column ${classes}">
            <div class="card-body">                
                <div class="clearfix" style="text-align:center;">
                    <cti:icon icon="${sprite32Property}" classes="icon-app fn"/>
                </div>
                <div style="text-align:center">
                    <span ><c:out value="${sprite32Property}" /></span>
                </div>
            </div>
        </div>
        
    <c:if test="${status.index % 4 == 3 or status.last}"></div></c:if>
</c:forEach>

<h3 class="subtle">Older 32px Icons</h3>
<c:forEach items="${sprites32Array}" var="sprite32Property" varStatus="status">
    <c:choose>
        <c:when test="${status.index % 4 == 0}">
            <div class="column-6-6-6-6 clearfix">
            <c:set var="classes" value="one"/>
        </c:when>
        <c:when test="${status.index % 4 == 1}">
            <c:set var="classes" value="two"/>
        </c:when>
        <c:when test="${status.index % 4 == 2}">
            <c:set var="classes" value="three"/>
        </c:when>
        <c:when test="${status.index % 4 == 3}">
            <c:set var="classes" value="four nogutter"/>
        </c:when>
    </c:choose>
    
        <div class="card bg-light column ${classes}">
            <div class="card-body">                
                <div class="clearfix" style="text-align:center;">
                    <cti:icon icon="${sprite32Property}" classes="icon-32 fn"/>
                </div>
                <div style="text-align:center">
                    <span ><c:out value="${sprite32Property}" /></span>
                </div>
            </div>
        </div>
        
    <c:if test="${status.index % 4 == 3 or status.last}"></div></c:if>
</c:forEach>

</tags:styleguide>
</cti:standardPage>