<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="sliders">
<tags:styleguide page="sliders">
<cti:includeScript link="YUKON_TIME_FORMATTER"/>

<style>
.description { line-height: 22px; }
</style>

<p class="description">
    Sliders allow users to select a single value or a range within a determined set of possible values.  They are most often used for time selection.
    Sliders are built as a jquery widget using jquery's <a href="https://jqueryui.com/widget/">widget factory</a>.  We have create a timeSlider tag to easily create a new Time Slider.
</p>

<h2>Simple Slider allowing selection of a Single Value</h2>

<p class="description">
    Create a slider using the timeSlider tag.  You can set options such as the min/max values, default value and step value (how much to increment per step).
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <tags:timeSlider startName="startTime" startValue="360" displayTimeToLeft="true"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
    &lt;tags:timeSlider startName="startTime" startValue="360" displayTimeToLeft="true"/&gt;
</pre>

<h2>Simple Slider allowing selection of a Range</h2>

<p class="description">
    Create a slider using the timeSlider tag to allow the user to select a range.  Example: between 8:00AM - 5:00PM.
    You can set options such as the min/max values, default value and step value (how much to increment per step).  Setting rangeEnabled=true allows the user to select a range.
    There is also an option to show labels.  If set to true, it will display a timeline below the slider.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <tags:timeSlider startName="startTime" startValue="480" rangeEnabled="true" endName="endTime" endValue="1020" displayTimeToLeft="true" showLabels="true"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
    &lt;tags:timeSlider startName="startTime" startValue="480" rangeEnabled="true" endName="endTime" endValue="1020" displayTimeToLeft="true" showLabels="true"/&gt;
</pre>

</tags:styleguide>
</cti:standardPage>