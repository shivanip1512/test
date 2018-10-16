<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="alerts">
<tags:styleguide page="alerts">

<style>
.description { line-height: 22px; }
</style>

<p class="description">
    Alerts are message to the user.  They can be <span class="success">success</span>, <span class="light-blue">info</span>, 
    <span class="light-blue">pending</span>, <span class="warning">warning</span>, or <span class="error">error</span> alerts. 
    They can be rendered through the <span class="label label-attr">&lt;tags:alertBox&gt;</span> tag, programtically 
    through the javascript api, or through the <span class="label label-attr">FlashScope</span> session object.
</p>

<h1>AlertBox Tag</h1>

<h2>Tag Attributes</h2>
<tags:nameValueContainer tableClass="description">
    <tags:nameValue name="type">
        The type of alert: <span class="success">success</span>, <span class="light-blue">info</span>,
        <span class="light-blue">pending</span>, <span class="warning">warning</span>, or 
        <span class="error">error</span>.  Defaults to <span class="error">error</span>.
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
        <c:if test="${not empty errorMsg}"><tags:alertBox>${errorMsg}</tags:alertBox></c:if>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;tags:alertBox type=&quot;success&quot;&gt;Things went well.&lt;/tags:alertBox&gt;
&lt;tags:alertBox type=&quot;info&quot;&gt;You should know something.&lt;/tags:alertBox&gt;
&lt;tags:alertBox type=&quot;pending&quot;&gt;I'm still working on that...&lt;/tags:alertBox&gt;
&lt;tags:alertBox type=&quot;warning&quot;&gt;Be careful.&lt;/tags:alertBox&gt;
&lt;tags:alertBox&gt;You did a bad thing, and you should feel bad.&lt;/tags:alertBox&gt;
&lt;c:if test=&quot;&#36;{not empty errorMsg}&quot;&gt;&lt;tags:alertBox&gt;&#36;{errorMsg}&lt;/tags:alertBox&gt;&lt;/c:if&gt;

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

<h2>Flash Scope</h2>
<p class="description">
    Yukon has a <span class="label label-attr">FlashScope</span> object which is used to contain messages and is stored
    in the users session.  The <span class="label label-attr">&lt;cti:flashScopeMessages/&gt;</span> tag then removes
    the messages from the session and renders them. This tag is already included by default at the top of pages that use 
    the standard layout.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <cti:flashScopeMessages/>
        <cti:url var="url" value="/dev/styleguide/alerts/flash-scope-test"/>
        <form:form modelAttribute="person" action="${url}" method="post">
            <cti:csrfToken/>
            <tags:nameValueContainer2 tableClass="with-form-controls">
                <tags:inputNameValue nameKey=".name" path="name"/>
                <tags:inputNameValue nameKey=".email" path="email"/>
            </tags:nameValueContainer2>
            <div class="page-action-area">
                <cti:button type="submit" nameKey="save" classes="primary action"/>
            </div>
        </form:form>
    </div>
</div>
<h4 class="subtle">JSP Code:</h4>
<pre class="code prettyprint">
&lt;cti:flashScopeMessages/&gt;
&lt;cti:url var=&quot;url&quot; value=&quot;/dev/styleguide/alerts/flash-scope-test&quot;/&gt;
&lt;form:form modelAttribute=&quot;person&quot; action=&quot;${url}&quot; method=&quot;post&quot;&gt;
    &lt;cti:csrfToken/&gt;
    &lt;tags:nameValueContainer2&gt;
        &lt;tags:inputNameValue nameKey=&quot;.name&quot; path=&quot;name&quot;/&gt;
        &lt;tags:inputNameValue nameKey=&quot;.email&quot; path=&quot;email&quot;/&gt;
    &lt;/tags:nameValueContainer2&gt;
    &lt;div class=&quot;page-action-area&quot;&gt;
        &lt;cti:button type=&quot;submit&quot; nameKey=&quot;save&quot; classes=&quot;primary action&quot;/&gt;
    &lt;/div&gt;
&lt;/form:form&gt;
</pre>
<h4 class="subtle">Controller Code:</h4>
<pre class="code prettyprint">
@RequestMapping(&quot;/styleguide/alerts&quot;)
public String alerts(ModelMap model, YukonUserContext userContext) {
    model.addAttribute(&quot;person&quot;, new Person());
    return &quot;styleguide/alerts.jsp&quot;;
}

private static final String keyBase = &quot;yukon.web.modules.dev.alerts.&quot;;

@RequestMapping(value=&quot;/styleguide/alerts/flash-scope-test&quot;, method=RequestMethod.POST)
public String alerts(ModelMap model, YukonUserContext userContext, FlashScope flash,
        @ModelAttribute(&quot;person&quot;) Person person, BindingResult result) {
    
    if (StringUtils.isBlank(person.getName())) {
        flash.setError(new YukonMessageSourceResolvable(keyBase + &quot;error&quot;));
    } else if (person.getEmail().contains(&quot;@aol.com&quot;)) {
        flash.setWarning(new YukonMessageSourceResolvable(keyBase + &quot;warning&quot;));
    } else {
        flash.setConfirm(new YukonMessageSourceResolvable(keyBase + &quot;success&quot;));
    }
    
    return &quot;styleguide/alerts.jsp&quot;;
}
</pre>

</tags:styleguide>
</cti:standardPage>