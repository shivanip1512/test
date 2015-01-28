<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="alerts">
<tags:styleguide page="alerts">

<style>
.description { line-height: 22px; }
</style>

<p class="description">
    Alerts are message to the user.  They can be <span class="success">success</span>, <span class="light-blue">info</span>, 
    <span class="light-blue">pending</span>, <span class="warning">warning</span> or <span class="error">error</span> alerts. 
    They can be rendered through the <span class="label label-attr">&lt;tags:alertBox&gt;</span> tag or programtically 
    through the javascript api.
</p>

<h1>AlertBox Tag</h1>

<h2>Tag Attributes</h2>
<tags:nameValueContainer tableClass="description">
    <tags:nameValue name="type">
        The type of alert: <span class="success">success</span>, <span class="info">info</span>, 
        <span class="warning">warning</span> or <span class="error">error</span>.  Defaults to
        <span class="error">error</span>.
    </tags:nameValue>
    <tags:nameValue name="key">
        i18n key object to use for the alert message. Note: key can be an i18n key String, 
        <span class="label label-attr">MessageSourceResolvable</span>, <span class="label label-attr">Displayable</span>,
        <span class="label label-attr">DisplayableEnum</span>, or <span class="label label-attr">ResolvableTemplate</span>.
    </tags:nameValue>
    <tags:nameValue name="arguments">
        Arguments to use when resolving the i18n message. Only valid when <span class="label label-attr">key</span> is used.
    </tags:nameValue>
</tags:nameValueContainer>

<br><h1 class="dib buffered">Examples</h1><br>

<h2>Simple AlertBox Usage</h2>
<p class="description">
    Alert boxes of each type using the body content as the message.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <tags:alertBox type="success">Things went well.</tags:alertBox>
        <tags:alertBox type="info">You should know something.</tags:alertBox>
        <tags:alertBox type="pending">I'm still working on that...</tags:alertBox>
        <tags:alertBox type="warning">Be careful.</tags:alertBox>
        <tags:alertBox>You did a bad thing, and you should feel bad.</tags:alertBox>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;tags:alertBox type=&quot;success&quot;&gt;Things went well.&lt;/tags:alertBox&gt;
&lt;tags:alertBox type=&quot;info&quot;&gt;You should know something.&lt;/tags:alertBox&gt;
&lt;tags:alertBox type=&quot;pending&quot;&gt;I'm still working on that...&lt;/tags:alertBox&gt;
&lt;tags:alertBox type=&quot;warning&quot;&gt;Be careful.&lt;/tags:alertBox&gt;
&lt;tags:alertBox&gt;You did a bad thing, and you should feel bad.&lt;/tags:alertBox&gt;
</pre>

<h2>AlertBox with Key</h2>
<p class="description">
    Alert box that uses a key instead of the tag body as the message.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <tags:alertBox key="yukon.common.invalidCron"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;tags:alertBox key=&quot;yukon.common.invalidCron&quot;/&gt;
</pre>

<br>
<h1>Javascript API</h1>
<p class="description">Yukon's Javascript API has helper functions to add and remove alerts messages.</p>

<h1 class="dib buffered">Examples</h1>

<h2>Adding Alerts to the Page</h2>
<p class="description">
    Clicking the buttons below will add or remove alert messages at the top of this page.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <div class="page-action-area">
            <cti:button label="Show a Success" classes="js-show-success"/>
            <cti:button label="Show an Error" classes="js-show-error"/>
            <cti:button label="Clear Alerts" classes="js-clear-alerts"/>
        </div>
        <script>
        $('.js-show-success').click(function (ev) {
            yukon.ui.alertSuccess('A winner is you!');
        });
        $('.js-show-error').click(function (ev) {
            yukon.ui.alertError('I know what you did last summer.');
        });
        $('.js-clear-alerts').click(function (ev) {
            yukon.ui.removeAlerts();
        });
        </script>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;page-action-area&quot;&gt;
    &lt;cti:button label=&quot;Show a Success&quot; classes=&quot;js-show-success&quot;/&gt;
    &lt;cti:button label=&quot;Show an Error&quot; classes=&quot;js-show-error&quot;/&gt;
    &lt;cti:button label=&quot;Clear Alerts&quot; classes=&quot;js-clear-alerts&quot;/&gt;
&lt;/div&gt;
&lt;script&gt;
$('.js-show-success').click(function (ev) {
    yukon.ui.alertSuccess('A winner is you!');
});
$('.js-show-error').click(function (ev) {
    yukon.ui.alertError('I know what you did last summer.');
});
$('.js-clear-alerts').click(function (ev) {
    yukon.ui.removeAlerts();
});
&lt;/script&gt;
</pre>

<h2>Adding Alerts to a Container</h2>
<p class="description">
    Clicking the buttons below will add or remove alert messages in the 
    <span class="label label-attr">#my-container</span> div.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <div id="my-alerts" style="border:1px solid #ccc;padding:3px;">
            <div>The quick brown fox jumps over the lazy dog.</div>
        </div>
        <div class="page-action-area">
            <cti:button label="Show a Success" classes="js-show-my-success"/>
            <cti:button label="Show an Error" classes="js-show-my-error"/>
            <cti:button label="Clear Alerts" classes="js-clear-my-alerts"/>
        </div>
        <script>
        $('.js-show-my-success').click(function (ev) {
            $('#my-alerts').addMessage({message: 'A winner is you!', messageClass: 'success'});
        });
        $('.js-show-my-error').click(function (ev) {
            $('#my-alerts').addMessage({message: 'I know what you did last summer.', messageClass: 'error'});
        });
        $('.js-clear-my-alerts').click(function (ev) {
            $('#my-alerts').removeMessages();
        });
        </script>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div id=&quot;my-alerts&quot; style=&quot;border:1px solid #ccc;padding:3px;&quot;&gt;
    &lt;div&gt;The quick brown fox jumps over the lazy dog.&lt;/div&gt;
&lt;/div&gt;
&lt;div class=&quot;page-action-area&quot;&gt;
    &lt;cti:button label=&quot;Show a Success&quot; classes=&quot;js-show-my-success&quot;/&gt;
    &lt;cti:button label=&quot;Show an Error&quot; classes=&quot;js-show-my-error&quot;/&gt;
    &lt;cti:button label=&quot;Clear Alerts&quot; classes=&quot;js-clear-my-alerts&quot;/&gt;
&lt;/div&gt;
&lt;script&gt;
$('.js-show-my-success').click(function (ev) {
    $('#my-alerts').addMessage({message: 'A winner is you!', messageClass: 'success'});
});
$('.js-show-my-error').click(function (ev) {
    $('#my-alerts').addMessage({message: 'I know what you did last summer.', messageClass: 'error'});
});
$('.js-clear-my-alerts').click(function (ev) {
    $('#my-alerts').removeMessages();
});
&lt;/script&gt;
</pre>

</tags:styleguide>
</cti:standardPage>