<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="steppers">
<tags:styleguide page="steppers">

<style>
.description { line-height: 22px; }
</style>

<p class="description">
    Steppers components are components that can step through a set of options. They provide a better mobile 
    experience than a select and are more compact than a radio button group. There are two types of steppers.
    One based on a text field and one based on a select.
</p>

<h2>Simple Select Stepper</h2>

<p class="description">
    A stepper component that replaces a typical html select with pet options.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <tags:stepper name="pet">
            <option value="CAT">Cat</option>
            <option value="DOG">Dog</option>
            <option value="FISH">Fish</option>
            <option value="HAMPSTER">Hampster</option>
        </tags:stepper>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;tags:stepper name=&quot;pet&quot;&gt;
    &lt;option value=&quot;CAT&quot;&gt;Cat&lt;/option&gt;
    &lt;option value=&quot;DOG&quot;&gt;Dog&lt;/option&gt;
    &lt;option value=&quot;FISH&quot;&gt;Fish&lt;/option&gt;
    &lt;option value=&quot;HAMPSTER&quot;&gt;Hampster&lt;/option&gt;
&lt;/tags:stepper&gt;
</pre>

</tags:styleguide>
</cti:standardPage>