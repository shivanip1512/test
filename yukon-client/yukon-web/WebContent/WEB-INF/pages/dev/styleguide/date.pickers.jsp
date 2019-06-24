<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="datePickers">
<tags:styleguide page="date-pickers">

<style>
.description { line-height: 22px; }
</style>

<p class="description">
Dates, date ranges and times are handled with special components built by the  
<span class="label label-attr">&lt;dt:date&gt;</span>, <span class="label label-attr">&lt;dt:dateRange&gt;</span>,
<span class="label label-attr">&lt;dt:time&gt;</span>, and <span class="label label-attr">&lt;dt:dateTime&gt;</span> tags. 
They all support <a target="_blank" href="http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/view.html">spring binding</a> 
as well as page edit modes (VIEW, EDIT, CREATE).
</p>

<h1>Date Tag</h1>
<h2>Attributes</h2>
<tags:nameValueContainer>
    <tags:nameValue name="name">
        The name of the input. <span class="label label-attr">name</span> should not be used if also using 
        <span class="label label-attr">path</span>.
    </tags:nameValue>
    <tags:nameValue name="path">
        The 'path' value passed to the <span class="label label-attr">&lt;form:input&gt;</span> tag which builds an input
        with a special name.  <span class="label label-attr">path</span> should not be used if also using 
        <span class="label label-attr">name</span>.
    </tags:nameValue>
    <tags:nameValue name="value">
        Specifies an initial value for the picker.  Should be an object consumable by the 
        <span class="label label-attr">DateFormatingService#format</span> method:
        <span class="label label-attr">Date</span>, <span class="label label-attr">ReadablePartial</span>,
        <span class="label label-attr">ReadableInstant</span>, or <span class="label label-attr">Long</span>.
    </tags:nameValue>
    <tags:nameValue name="id">The id attribute of the input.</tags:nameValue>
    <tags:nameValue name="disable">
        If <strong>true</strong> the picker component and the underlying input will be disabled.
    </tags:nameValue>
    <tags:nameValue name="cssClass">
        CSS class names applied to the outer container of the picker component.
    </tags:nameValue>
    <tags:nameValue name="cssDialogClass">
        CSS class names applied to the popup container.
    </tags:nameValue>
    <tags:nameValue name="maxDate">
        Set a maximum selectable date/time via an object consumable by the 
        <span class="label label-attr">DateFormatingService#format</span> method:
        <span class="label label-attr">Date</span>, <span class="label label-attr">ReadablePartial</span>,
        <span class="label label-attr">ReadableInstant</span>, or <span class="label label-attr">Long</span>.
    </tags:nameValue>
    <tags:nameValue name="minDate">
        Set a minimum selectable date/time via an object consumable by the 
        <span class="label label-attr">DateFormatingService#format</span> method:
        <span class="label label-attr">Date</span>, <span class="label label-attr">ReadablePartial</span>,
        <span class="label label-attr">ReadableInstant</span>, or <span class="label label-attr">Long</span>.
    </tags:nameValue>
    <tags:nameValue name="hideErrors">
        If <strong>true</strong>, will not display validation error messages. Default: <strong>false</strong>.
    </tags:nameValue>
</tags:nameValueContainer>

<h1 class="dib">Examples</h1>
<h2 id="basic-date-example">Basic Date Picker</h2>

<p class="description">
    A simple date picker with an initial value of right now.
</p>
<div class="column-4-20 clearfix picker-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <dt:date name="p1" value="${now}"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;dt:date name=&quot;p1&quot; value=&quot;&#36;{now}&quot;/&gt;
</pre>

<h2 id="basic-date-example">Disabled</h2>

<p class="description">
    A disabled date picker.
</p>
<div class="column-4-20 clearfix picker-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <dt:date name="p1" value="${now}" disabled="true"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;dt:date name=&quot;p1&quot; value=&quot;&#36;{now}&quot; disabled=&quot;true&quot;/&gt;
</pre>

<h2 id="basic-date-example">Maximum/Minimum Date</h2>

<p class="description">
    Picker with maximum date one week from now and picker with minimum date one week ago and a picker with both. 
</p>
<div class="column-4-20 clearfix picker-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <dt:date value="${now}" maxDate="${weekFromNow}"/>
        <dt:date value="${now}" minDate="${weekAgo}"/>
        <dt:date value="${now}" minDate="${weekAgo}" maxDate="${weekFromNow}"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;dt:date value=&quot;&#36;{now}&quot; maxDate=&quot;&#36;{weekFromNow}&quot;/&gt;
&lt;dt:date value=&quot;&#36;{now}&quot; minDate=&quot;&#36;{weekAgo}&quot;/&gt;
&lt;dt:date value=&quot;&#36;{now}&quot; minDate=&quot;&#36;{weekAgo}&quot; maxDate=&quot;&#36;{weekFromNow}&quot;/&gt;
</pre>

<br><br>

<h1>DateRange Tag</h1>
<h2>Attributes</h2>
<tags:nameValueContainer>
    <tags:nameValue name="startName">
        The name of the start input. <span class="label label-attr">startName</span> should not be used if also using 
        <span class="label label-attr">startPath</span>.
    </tags:nameValue>
    <tags:nameValue name="startPath">
        The 'path' value passed to the <span class="label label-attr">&lt;form:input&gt;</span> tag which builds an input
        with a special name.  <span class="label label-attr">startPath</span> should not be used if also using 
        <span class="label label-attr">startName</span>.
    </tags:nameValue>
    <tags:nameValue name="startValue">
        Specifies an initial start value of the range.  Should be an object consumable by the 
        <span class="label label-attr">DateFormatingService#format</span> method:
        <span class="label label-attr">Date</span>, <span class="label label-attr">ReadablePartial</span>,
        <span class="label label-attr">ReadableInstant</span>, or <span class="label label-attr">Long</span>.
    </tags:nameValue>
    <tags:nameValue name="endName">
        The name of the end input. <span class="label label-attr">endName</span> should not be used if also using 
        <span class="label label-attr">endPath</span>.
    </tags:nameValue>
    <tags:nameValue name="endPath">
        The 'path' value passed to the <span class="label label-attr">&lt;form:input&gt;</span> tag which builds an input
        with a special name.  <span class="label label-attr">endPath</span> should not be used if also using 
        <span class="label label-attr">endName</span>.
    </tags:nameValue>
    <tags:nameValue name="startValue">
        Specifies an initial end value of the range.  Should be an object consumable by the 
        <span class="label label-attr">DateFormatingService#format</span> method:
        <span class="label label-attr">Date</span>, <span class="label label-attr">ReadablePartial</span>,
        <span class="label label-attr">ReadableInstant</span>, or <span class="label label-attr">Long</span>.
    </tags:nameValue>
    <tags:nameValue name="disable">
        If <strong>true</strong> the picker components and the underlying inputs will be disabled.
    </tags:nameValue>
    <tags:nameValue name="cssClass">
        CSS class names applied to both date pickers in the range. Note the 
        <span class="label label-attr">.js-dateStart</span> and <span class="label label-attr">.js-dateEnd</span>
        classes are already included on the start and end pickers in the range.
    </tags:nameValue>
    <tags:nameValue name="cssDialogClass">
        CSS class names applied to both date pickers in the range.
    </tags:nameValue>
    <tags:nameValue name="wrapperClasses">
        CSS class names applied to the outer container around both picker components.
    </tags:nameValue>
    <tags:nameValue name="maxDate">
        Set a maximum selectable date for the end date in the range via an object consumable by the 
        <span class="label label-attr">DateFormatingService#format</span> method:
        <span class="label label-attr">Date</span>, <span class="label label-attr">ReadablePartial</span>,
        <span class="label label-attr">ReadableInstant</span>, or <span class="label label-attr">Long</span>.
    </tags:nameValue>
</tags:nameValueContainer>

<h1 class="dib">Examples</h1>
<h2 id="basic-date-example">Simple Date Range</h2>

<p class="description">
    A date range picker with initial start and end values.
</p>
<div class="column-4-20 clearfix picker-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <dt:dateRange startValue="${now}" endValue="${weekFromNow}"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;dt:dateRange startValue=&quot;&#36;{now}&quot; endValue=&quot;&#36;{weekFromNow}&quot;/&gt;
</pre>

<br><br>

<h1>Time Tag</h1>
<h2>Attributes</h2>
<tags:nameValueContainer>
    <tags:nameValue name="name">
        The name of the input. <span class="label label-attr">name</span> should not be used if also using 
        <span class="label label-attr">path</span>.
    </tags:nameValue>
    <tags:nameValue name="path">
        The 'path' value passed to the <span class="label label-attr">&lt;form:input&gt;</span> tag which builds an input
        with a special name.  <span class="label label-attr">path</span> should not be used if also using 
        <span class="label label-attr">name</span>.
    </tags:nameValue>
    <tags:nameValue name="value">
        Specifies an initial value for the picker.  Should be an object consumable by the 
        <span class="label label-attr">DateFormatingService#format</span> method:
        <span class="label label-attr">Date</span>, <span class="label label-attr">ReadablePartial</span>,
        <span class="label label-attr">ReadableInstant</span>, or <span class="label label-attr">Long</span>.
    </tags:nameValue>
    <tags:nameValue name="id">The id attribute of the input.</tags:nameValue>
    <tags:nameValue name="disable">
        If <strong>true</strong> the picker component and the underlying input will be disabled.
    </tags:nameValue>
    <tags:nameValue name="cssClass">
        CSS class names applied to the outer container of the picker component.
    </tags:nameValue>
    <tags:nameValue name="cssDialogClass">
        CSS class names applied to the popup container.
    </tags:nameValue>
    <tags:nameValue name="maxDate">
        Set a maximum selectable date/time via an object consumable by the 
        <span class="label label-attr">DateFormatingService#format</span> method:
        <span class="label label-attr">Date</span>, <span class="label label-attr">ReadablePartial</span>,
        <span class="label label-attr">ReadableInstant</span>, or <span class="label label-attr">Long</span>.
    </tags:nameValue>
    <tags:nameValue name="minDate">
        Set a minimum selectable date/time via an object consumable by the 
        <span class="label label-attr">DateFormatingService#format</span> method:
        <span class="label label-attr">Date</span>, <span class="label label-attr">ReadablePartial</span>,
        <span class="label label-attr">ReadableInstant</span>, or <span class="label label-attr">Long</span>.
    </tags:nameValue>
    <tags:nameValue name="stepHour">
        Step size when incrementing/decrementing hours.
    </tags:nameValue>
    <tags:nameValue name="stepMinute">
        Step size when incrementing/decrementing minutes.
    </tags:nameValue>
</tags:nameValueContainer>

<h1 class="dib">Examples</h1>
<h2 id="basic-time-example">Basic Time Picker</h2>

<p class="description">
    A simple time picker with an initial value of right now.
</p>
<div class="column-4-20 clearfix picker-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <dt:time name="time" value="${now}"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;dt:time name=&quot;time&quot; value=&quot;&#36;{now}&quot;/&gt;
</pre>

<h2 id="basic-time-example">Step Hour</h2>

<p class="description">
    A time picker with step hour of 3 hours, a time picker with step minute of 7 minutes and a time picker with both.
</p>
<div class="column-4-20 clearfix picker-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <dt:time name="time" value="${now}" stepHour="3"/>
        <dt:time name="time" value="${now}" stepMinute="7"/>
        <dt:time name="time" value="${now}" stepHour="3" stepMinute="7"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;dt:time name=&quot;time&quot; value=&quot;&#36;{now}&quot; stepHour=&quot;3&quot;/&gt;
&lt;dt:time name=&quot;time&quot; value=&quot;&#36;{now}&quot; stepMinute=&quot;7&quot;/&gt;
&lt;dt:time name=&quot;time&quot; value=&quot;&#36;{now}&quot; stepHour=&quot;3&quot; stepMinute=&quot;7&quot;/&gt;
</pre>

<br><br>

<h1>Time Offset Tag</h1>
<p class="description">
    A picker that is used to select the hours/minutes to offset an event
</p>
<h2>Attributes</h2>
<tags:nameValueContainer>
    <tags:nameValue name="name">
        The name of the input. <span class="label label-attr">name</span> should not be used if also using 
        <span class="label label-attr">path</span>.
    </tags:nameValue>
    <tags:nameValue name="path">
        The 'path' value passed to the <span class="label label-attr">&lt;form:input&gt;</span> tag which builds an input
        with a special name.  <span class="label label-attr">path</span> should not be used if also using 
        <span class="label label-attr">name</span>.
    </tags:nameValue>
    <tags:nameValue name="value">
        Specifies an initial value for the picker in number of minutes.  Default value is 0.
    </tags:nameValue>
    <tags:nameValue name="id">The id attribute of the input.</tags:nameValue>
    <tags:nameValue name="disable">
        If <strong>true</strong> the picker component and the underlying input will be disabled.
    </tags:nameValue>
    <tags:nameValue name="cssClass">
        CSS class names applied to the outer container of the picker component.
    </tags:nameValue>
    <tags:nameValue name="cssDialogClass">
        CSS class names applied to the popup container.
    </tags:nameValue>
    <tags:nameValue name="stepHour">
        Step size when incrementing/decrementing hours.
    </tags:nameValue>
    <tags:nameValue name="stepMinute">
        Step size when incrementing/decrementing minutes.
    </tags:nameValue>
</tags:nameValueContainer>

<h1 class="dib">Examples</h1>
<h2 id="basic-timeoffset-example">Basic Time Offset Picker</h2>

<p class="description">
    A simple time offset picker.
</p>
<div class="column-4-20 clearfix picker-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <dt:timeOffset name="offset"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;dt:timeOffset name=&quot;offset&quot; /&gt;
</pre>

<h2 id="default-value-timeoffset-example">Default Value and Step Minute</h2>

<p class="description">
    A time offset picker with a default value and Step Minute value.
</p>
<div class="column-4-20 clearfix picker-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <dt:timeOffset name="offset2" value="600" stepMinute="15"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;dt:timeOffset name=&quot;offset2&quot; value=&quot;600&quot; stepMinute=&quot;15&quot; /&gt;
</pre>

<h2 id="min-max-timeoffset-example">Min and Max Value</h2>

<p class="description">
    A time offset picker with a minimum and maximum value.
</p>
<div class="column-4-20 clearfix picker-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <dt:timeOffset name="offset3" minValue="100" maxValue="800"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;dt:timeOffset name=&quot;offset3&quot; minValue=&quot;100&quot; maxValue=&quot;800&quot; /&gt;
</pre>

<br><br>

<h1>DateTime Tag</h1>
<h2>Attributes</h2>
<tags:nameValueContainer>
    <tags:nameValue name="name">
        The name of the input. <span class="label label-attr">name</span> should not be used if also using 
        <span class="label label-attr">path</span>.
    </tags:nameValue>
    <tags:nameValue name="path">
        The 'path' value passed to the <span class="label label-attr">&lt;form:input&gt;</span> tag which builds an input
        with a special name.  <span class="label label-attr">path</span> should not be used if also using 
        <span class="label label-attr">name</span>.
    </tags:nameValue>
    <tags:nameValue name="value">
        Specifies an initial value for the picker.  Should be an object consumable by the 
        <span class="label label-attr">DateFormatingService#format</span> method:
        <span class="label label-attr">Date</span>, <span class="label label-attr">ReadablePartial</span>,
        <span class="label label-attr">ReadableInstant</span>, or <span class="label label-attr">Long</span>.
    </tags:nameValue>
    <tags:nameValue name="id">The id attribute of the input.</tags:nameValue>
    <tags:nameValue name="disable">
        If <strong>true</strong> the picker component and the underlying input will be disabled.
    </tags:nameValue>
    <tags:nameValue name="cssClass">
        CSS class names applied to the outer container of the picker component.
    </tags:nameValue>
    <tags:nameValue name="cssDialogClass">
        CSS class names applied to the popup container.
    </tags:nameValue>
    <tags:nameValue name="cssErrorClass">
        CSS class names applied to the error element. Note: The <span class="label label-attr">.error</span> class is 
        already included.
    </tags:nameValue>
    <tags:nameValue name="maxDate">
        Set a maximum selectable date/time via an object consumable by the 
        <span class="label label-attr">DateFormatingService#format</span> method:
        <span class="label label-attr">Date</span>, <span class="label label-attr">ReadablePartial</span>,
        <span class="label label-attr">ReadableInstant</span>, or <span class="label label-attr">Long</span>.
    </tags:nameValue>
    <tags:nameValue name="minDate">
        Set a minimum selectable date/time via an object consumable by the 
        <span class="label label-attr">DateFormatingService#format</span> method:
        <span class="label label-attr">Date</span>, <span class="label label-attr">ReadablePartial</span>,
        <span class="label label-attr">ReadableInstant</span>, or <span class="label label-attr">Long</span>.
    </tags:nameValue>
    <tags:nameValue name="stepHour">
        Step size when incrementing/decrementing hours. If <span class="label label-attr">stepHour</span> is greater 
        than 1, <span class="label label-attr">stepMinute</span> will be ignored.
    </tags:nameValue>
    <tags:nameValue name="stepMinute">
        Step size when incrementing/decrementing minutes.
    </tags:nameValue>
</tags:nameValueContainer>

<h1 class="dib">Examples</h1>
<h2 id="basic-time-example">Basic Date & Time Picker</h2>

<p class="description">
    A simple date & time picker with an initial value of right now.
</p>
<div class="column-4-20 clearfix picker-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <dt:dateTime name="dateTime" value="${now}"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;dt:dateTime name=&quot;dateTime&quot; value=&quot;&#36;{now}&quot;/&gt;
</pre>

<h2 id="basic-time-example">Maximum Date</h2>

<p class="description">
    A date & time picker with a maximum date one week from now.
</p>
<div class="column-4-20 clearfix picker-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <dt:dateTime name="dateTime" value="${now}" maxDate="${weekFromNow}"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;dt:dateTime name=&quot;dateTime&quot; value=&quot;&#36;{now}&quot; maxDate=&quot;&#36;{weekFromNow}&quot;/&gt;
</pre>

<h2 id="basic-time-example">Minimum Date</h2>

<p class="description">
    A date & time picker with a minimum date one week ago.
</p>
<div class="column-4-20 clearfix picker-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <dt:dateTime name="dateTime" value="${now}" minDate="${weekAgo}"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;dt:dateTime name=&quot;dateTime&quot; value=&quot;&#36;{now}&quot; minDate=&quot;&#36;{weekAgo}&quot;/&gt;
</pre>

<h2 id="basic-time-example">Limited Range (Minimum and Maximum) Date</h2>

<p class="description">
    A date & time picker with a minimum date one week ago and a maximum date of one week from now.
</p>
<div class="column-4-20 clearfix picker-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <dt:dateTime name="dateTime" value="${now}" minDate="${weekAgo}" maxDate="${weekFromNow}"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;dt:dateTime name=&quot;dateTime&quot; value=&quot;&#36;{now}&quot; minDate=&quot;&#36;{weekAgo}&quot; maxDate=&quot;&#36;{weekFromNow}&quot;/&gt;
</pre>

</tags:styleguide>
</cti:standardPage>