<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="inputs">
<tags:styleguide page="inputs">

<style>
.style-guide-example .one { line-height: 26px; }
.description { line-height: 22px; }
</style>

<p class="description">
    Inputs can have special behavior simply by using some css classes.
</p>

<h2 id="focus-example">Focus</h2>

<p class="description">
    Inputs can be focused automatically using the <span class="label label-attr">.js-focus</span> class.
    When the page loads the element with this class will have focus initially.  Use this wisely on your pages
    to make the user's experience more streamlined.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <input type="text" name="foo" placeholder="don't look at me">
        <input type="text" name="bar" placeholder="please focus on me" class="js-focus">
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;input type=&quot;text&quot; name=&quot;foo&quot; placeholder=&quot;don't look at me&quot;&gt;
&lt;input type=&quot;text&quot; name=&quot;bar&quot; placeholder=&quot;please focus on me&quot; class=&quot;js-focus&quot;&gt;
</pre>

<h2 id="format-phone-example">Formatting</h2>

<p class="description">
    Formatting a phone number is serious business but you can do it simply by using the 
    <span class="label label-attr">.js-format-phone</span> class.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <input class="js-format-phone" type="text" name="phone" value="15554443333">
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;input class=&quot;js-format-phone&quot; type=&quot;text&quot; name=&quot;phone&quot; value=&quot;15554443333&quot;&gt;
</pre>

<h2 id="numeric-example">Numeric</h2>

<p class="description">
    The numeric tag can be used for formatting numeric fields. You can specify min, max, and a step value for the field.
    This tag uses the jQuery UI Spinner widget. The value is validated against the min/max values supplied when the field loses focus.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <form:form modelAttribute="numericInput">
            <tags:numeric path="temperature" minValue="-10" maxValue="40" stepValue="5"/>
        </form:form>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;tags:numeric path=&quot;temperature&quot; minValue=&quot;-10&quot; maxValue=&quot;40&quot; stepValue=&quot;5&quot;&gt;
</pre>


<h2 id="toggle-with-checkbox-example">Toggle Groups</h2>

<p class="description">
    You can enable or disable groups of elements using the 
    <span class="label label-attr">[data-toggle]</span> and 
    <span class="label label-attr">[data-toggle-group]</span> attributes.
    This is also utilized by the <span class="label label-attr">&lt;tags:switch&gt;</span> and
    <span class="label label-attr">&lt;tags:switchButton&gt;</span> tags internally. 
    <a href="switches#toggle-group-example">See example</a>.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <label><input type="checkbox" name="enable" data-toggle="my-toggle-grp">Enable All The Things?</label>
        <div>
            <input type="text" name="name" placeholder="Name" data-toggle-group="my-toggle-grp" disabled>
            <select name="color" data-toggle-group="my-toggle-grp" disabled>
                <option>Red</option><option>Blue</option>
            </select>
            <cti:button nameKey="save" data-toggle-group="my-toggle-grp" disabled="true"/>
        </div>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;label&gt;&lt;input type=&quot;checkbox&quot; name=&quot;enable&quot; data-toggle=&quot;my-toggle-grp&quot;&gt;Enable All The Things?&lt;/label&gt;
&lt;div&gt;
    &lt;input type=&quot;text&quot; name=&quot;name&quot; placeholder=&quot;Name&quot; data-toggle-group=&quot;my-toggle-grp&quot; disabled&gt;
    &lt;select name=&quot;color&quot; data-toggle-group=&quot;my-toggle-grp&quot; disabled&gt;
        &lt;option&gt;Red&lt;/option&gt;&lt;option&gt;Blue&lt;/option&gt;
    &lt;/select&gt;
    &lt;cti:button nameKey=&quot;save&quot; data-toggle-group=&quot;my-toggle-grp&quot; disabled=&quot;true&quot;/&gt;
&lt;/div&gt;
</pre>

<h2 id="toggle-with-checkbox-example">File Uploads</h2>

<p class="description">
    The <span class="label label-attr">&lt;tags:file/&gt;</span> tag is used to create a file input that looks and feels
    the same across all browsers. Always use this tag instead of raw html 
    <span class="label label-attr">&lt;input type="file"&gt;</span>.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <tags:file/>
        <tags:file name="history.magnaCarta" id="my-input"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;tags:file/&gt;
&lt;tags:file name="history.magnaCarta" id="my-input"/&gt;
</pre>

</tags:styleguide>
</cti:standardPage>