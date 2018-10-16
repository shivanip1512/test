<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="switches">
<tags:styleguide page="switches">

<style>
.style-guide-example .one { line-height: 26px; }
.description { line-height: 22px; }
</style>

<p class="description">
    Switches are special components built by two tags 
    <span class="label label-attr">&lt;tags:switch&gt;</span> and
    <span class="label label-attr">&lt;tags:switchButton&gt;</span> that function as a toggle switch.  They have two states, 
    on or off.  They drive an html checkbox input internally and therefore require no javascript to function. They both support 
    <a target="_blank" href="http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/view.html">spring binding</a>.
</p>

<h1>Switch Tag</h1>
<h2>Attributes</h2>
<tags:nameValueContainer>
    <tags:nameValue name="name">
        The name of the input. <span class="label label-attr">name</span> should not be used if also using 
        <span class="label label-attr">path</span>.
    </tags:nameValue>
    <tags:nameValue name="path">
        The 'path' value passed to the <span class="label label-attr">&lt;form:checkbox&gt;</span> tag which builds an input
        with a special name.  <span class="label label-attr">path</span> should not be used if also using 
        <span class="label label-attr">name</span>.
    </tags:nameValue>
    <tags:nameValue name="checked">
        If <strong>true</strong>, the checkbox will be checked initially.  Only valid when using 
        <span class="label label-attr">name</span>.
    </tags:nameValue>
    <tags:nameValue name="id">The id attribute of the checkbox input.</tags:nameValue>
    <tags:nameValue name="disable">
        If <strong>true</strong> the component and the underlying checkbox input will be disabled.
    </tags:nameValue>
    <tags:nameValue name="key">
        I18n key used for accessability text.
    </tags:nameValue>
    <tags:nameValue name="classes">
        CSS class names applied to <span class="label label-attr">&lt;label&gt;</span> element wrapping the component.
    </tags:nameValue>
</tags:nameValueContainer>

<h1 class="dib">Examples</h1>
<h2 id="simple-toggle-example">Simple Toggle Switches</h2>

<p class="description">
    A switch that use the <span class="label label-attr">name</span>.<br>
    A switch defaulting to checked.<br>
    A switch defaulting to checked and disabled.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <tags:switch name="foo"/>
        <tags:switch name="foo" checked="true"/>
        <tags:switch name="foo" checked="true" disabled="true"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;tags:switch name=&quot;foo&quot;/&gt;
&lt;tags:switch name=&quot;foo&quot; checked=&quot;true&quot; disabled=&quot;true&quot;/&gt;
</pre>

<h2 id="simple-toggle-example">Toggle Switch with Spring Binding</h2>

<p class="description">
    Switches that use <span class="label label-attr">path</span> instead of <span class="label label-attr">name</span> 
    to utilize spring binding.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <form:form modelAttribute="thing" action="/some/cool/url">
            <tags:switch path="enabled"/>
            <tags:switch path="enabled" disabled="true"/>
        </form:form>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;form:form modelAttribute=&quot;thing&quot; action=&quot;/some/cool/url&quot;&gt;
    &lt;tags:switch path=&quot;enabled&quot;/&gt;
    &lt;tags:switch path=&quot;enabled&quot; disabled=&quot;true&quot;/&gt;
&lt;/form:form&gt;
</pre>

<h1>SwitchButton Tag</h1>
<h2>Attributes</h2>
<tags:nameValueContainer>
    <tags:nameValue name="name">
        The name of the input. <span class="label label-attr">name</span> should not be used if also using 
        <span class="label label-attr">path</span>.
    </tags:nameValue>
    <tags:nameValue name="path">
        The 'path' value passed to the <span class="label label-attr">&lt;form:checkbox&gt;</span> tag which builds an input
        with a special name.  <span class="label label-attr">path</span> should not be used if also using 
        <span class="label label-attr">name</span>.
    </tags:nameValue>
    <tags:nameValue name="checked">
        If <strong>true</strong>, the checkbox will be checked initially.  Only valid when using 
        <span class="label label-attr">name</span>.
    </tags:nameValue>
    <tags:nameValue name="inverse">
        If <strong>true</strong>, when the checkbox is checked, the red 'off state' button will be toggled on instead 
        of the green 'on state' button.
    </tags:nameValue>
    <tags:nameValue name="disable">
        If <strong>true</strong> the component and the underlying checkbox input will be disabled.
    </tags:nameValue>
    <tags:nameValue name="id">The id attribute of the checkbox input.</tags:nameValue>
    <tags:nameValue name="classes">
        CSS class names applied to <span class="label label-attr">&lt;label&gt;</span> element wrapping the component.
    </tags:nameValue>
    <tags:nameValue name="onClasses">
        CSS class names applied to <span class="label label-attr">&lt;label&gt;</span> element for the on button.
    </tags:nameValue>
    <tags:nameValue name="offClasses">
        CSS class names applied to <span class="label label-attr">&lt;label&gt;</span> element for the off button.
    </tags:nameValue>
    <tags:nameValue name="toggleGroup">
        Used to setup a toggle group driven by a checkbox.
    </tags:nameValue>
    <tags:nameValue name="onNameKey">
        I18n key used for the 'on' button label. '.label' is appended to the end of the key provided.
    </tags:nameValue>
    <tags:nameValue name="offNameKey">
        I18n key used for the 'off' button label. '.label' is appended to the end of the key provided.
    </tags:nameValue>
</tags:nameValueContainer>

<h1 class="dib">Examples</h1>
<h2 id="simple-toggle-example">Simple Toggle Switches</h2>

<p class="description">
    A switch that use the <span class="label label-attr">name</span>.<br>
    A switch using alternate button text.<br>
    A switch defaulting to checked.<br>
    A switch defaulting to checked and disabled.<br>
    A switch defaulting to checked, uses alternate button text, and inverts the state.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <tags:switchButton name="foo"/>
        <tags:switchButton name="foo" offNameKey="yukon.common.no" onNameKey="yukon.common.yes"/>
        <tags:switchButton name="foo" checked="true"/>
        <tags:switchButton name="foo" checked="true" disabled="true"/>
        <tags:switchButton name="foo" checked="true" offNameKey=".disable.label" onNameKey=".enable.label" inverse="true"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;tags:switchButton name=&quot;foo&quot;/&gt;
&lt;tags:switchButton name=&quot;foo&quot; offNameKey=&quot;no&quot; onNameKey=&quot;yes&quot;/&gt;
&lt;tags:switchButton name=&quot;foo&quot; checked=&quot;true&quot;/&gt;
&lt;tags:switchButton name=&quot;foo&quot; checked=&quot;true&quot; disabled=&quot;true&quot;/&gt;
&lt;tags:switchButton name=&quot;foo&quot; checked=&quot;true&quot; offNameKey=&quot;disable&quot; onNameKey=&quot;enable&quot; inverse=&quot;true&quot;/&gt;
</pre>

<h2 id="simple-toggle-example">Toggle Switch with Spring Binding</h2>

<p class="description">
    Switches that use <span class="label label-attr">path</span> instead of <span class="label label-attr">name</span> 
    to utilize spring binding.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <form:form modelAttribute="thing" action="/some/cool/url">
            <tags:switchButton path="enabled"/>
            <tags:switchButton path="enabled" disabled="true"/>
            <tags:switchButton path="enabled" inverse="true"/>
        </form:form>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;form:form modelAttribute=&quot;thing&quot; action=&quot;/some/cool/url&quot;&gt;
    &lt;tags:switchButton path=&quot;enabled&quot;/&gt;
    &lt;tags:switchButton path=&quot;enabled&quot; disabled=&quot;true&quot;/&gt;
    &lt;tags:switchButton path=&quot;enabled&quot; inverse=&quot;true&quot;/&gt;
&lt;/form:form&gt;
</pre>

<h2 id="toggle-group-example">Changing State with Switches</h2>

<p class="description">
    Switches can change the enabled/disabled state of elements using the 
    <span class="label label-attr">toggleGroup</span> attribute. This is handled using special data attributes
    explained <a href="fun-with-inputs#toggle-with-checkbox-example">here</a>.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <tags:switchButton name="sendEmail" toggleGroup="email-address"/>
        <input type="text" name="emailAddress" value="bob@vila.com" size="40" disabled data-toggle-group="email-address">
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;tags:switchButton name=&quot;sendEmail&quot; toggleGroup=&quot;email-address&quot;/&gt;
&lt;input type=&quot;text&quot; name=&quot;emailAddress&quot; value=&quot;bob@vila.com&quot; size=&quot;40&quot; disabled 
    data-toggle-group=&quot;email-address&quot;&gt;
</pre>

</tags:styleguide>
</cti:standardPage>