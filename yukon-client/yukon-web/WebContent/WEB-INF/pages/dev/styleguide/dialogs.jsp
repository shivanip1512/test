<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="dialogs">
<tags:styleguide page="dialogs">

<style>
.dialog-example .one { line-height: 26px; }
.description { line-height: 22px; }
</style>

<p class="description">
    Popups and dialogs are made with html elements and data attributes.  (Dialogs are just popups with buttons.)  Use the 
    methods described here for popups and dialogs instead of the legacy popup/dialog tags which are being phased out. All 
    popups in Yukon utilize the JQuery dialog widget.
    <br><br>
    All data about a popup/dialog is stored in data attributes on the popup/dialog html element.<br>
    <h2>Popup/Dialog Options</h2>
    <tags:nameValueContainer>
        <tags:nameValue name="data-title">The text used for the title of the popup.</tags:nameValue>
        <tags:nameValue name="data-height">The initial height of the popup.</tags:nameValue>
        <tags:nameValue name="data-width">The initial width of the popup.</tags:nameValue>
        <tags:nameValue name="data-url">
            If present, the contents of the popup element will be replaced with the response of an ajax request to the url 
            before the popup is shown.
        </tags:nameValue>
        <tags:nameValue name="data-load-event">
            If present, this event will be fired right before the popup is shown. If 
            <span class="label label-attr">data-url</span> is used, the event will be fired after the dialog is loaded with 
            the response body.
        </tags:nameValue>
        <tags:nameValue name="data-popup-toggle">
            If present, the trigger element can be clicked to close the popup as well.
        </tags:nameValue>
        <tags:nameValue name="data-dialog">
            If present the popup will have 'ok' and 'cancel' buttons.
        </tags:nameValue>
        <tags:nameValue name="data-event">
            If present and <span class="label label-attr">data-dialog</span> is present, the value of 
            <span class="label label-attr">data-event</span> will be the name of the event to fire when clicking the 'ok' 
            button.
        </tags:nameValue>
        <tags:nameValue name="data-target">
            If present and <span class="label label-attr">data-event</span> is present, the value of 
            <span class="label label-attr">data-target</span> is the css selector to the element that will be the target of 
            the event specified in <span class="label label-attr">data-event</span>.
        </tags:nameValue>
        <tags:nameValue name="data-form">
            If present, a form will be submitted when the 'ok' button is clicked.  If the value is not present, the first 
            form found inside the popup will be submitted.  Otherwise the value is a css selector to the form to submit.
        </tags:nameValue>
    </tags:nameValueContainer>
    <br><br>
    <h2>Popup/Dialog Trigger Options</h2>
    Popups/Dialogs can be opened by clicking elements with the <span class="label label-attr">data-popup</span> attribute. 
    Popup/Dialog trigger element options are as follows:
    <tags:nameValueContainer>
        <tags:nameValue name="data-popup">A css selector to the popup element to open.</tags:nameValue>
        <tags:nameValue name="data-popup-toggle">
            If present, the trigger element will also close the popup when clicked.
        </tags:nameValue>
    </tags:nameValueContainer>
</p>

<h2 id="simple-popup-example">Simple Popup</h2>

<p class="description">
    To make a simple popup just start with a hidden <span class="label label-attr">&lt;div&gt;</span> and a trigger.
</p>
<div class="column-4-20 clearfix dialog-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <div class="dn" id="popup-1" data-title="Popup Example">This is a simple popup.</div>
        <cti:button label="Open Popup" data-popup="#popup-1"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;dn&quot; id=&quot;popup-1&quot; data-title=&quot;My First Popup&quot;&gt;This is a simple popup.&lt;/div&gt;
&lt;cti:button label=&quot;Open Popup&quot; data-popup=&quot;#popup-1&quot;/&gt;
</pre>

<h2 id="button-text-example">Width and Height</h2>

<p class="description">
    A popup with initial width of 500 pixels and height of 300 pixels.
</p>
<div class="column-4-20 clearfix dialog-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <div class="dn" id="popup-2" 
                data-title="Popup Example"
                data-width="500"
                data-height="300">This is a popup with initial width of 500 pixels and height of 300 pixels.</div>
        <cti:button label="Open Popup" data-popup="#popup-2"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;dn&quot; id=&quot;popup-2&quot; 
        data-title=&quot;Popup Example&quot;
        data-width=&quot;500&quot;
        data-height=&quot;300&quot;&gt;This is a popup with initial width of 500 pixels and height of 300 pixels.&lt;/div&gt;
&lt;cti:button label=&quot;Open Popup&quot; data-popup=&quot;#popup-2&quot;/&gt;
</pre>
    
</tags:styleguide>
</cti:standardPage>