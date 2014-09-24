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
        <tags:nameValue name="data-position-my">
            When positioning a dialog, the part of the dialog to be used.<br>
            Values: 'left|center|right', followed by a space, followed by  'top|center|bottom'. Order matters.<br>
            Each of these can have +X or -X attached (X is an integer) to move them X pixels away.<br>
            Sample Value: left+2 bottom-2<br>
            Default Value: center center<br>
            See <a href="#positioning-example">Example below</a> for usage
        </tags:nameValue>
        <tags:nameValue name="data-position-at">
            When positioning a dialog, the part of the reference object to be used. 
            If <span class="label label-attr">data-position-of</span> is present, then that element. Otherwise, the window is used.<br>
            Values: 'left|center|right', followed by a space, followed by  'top|center|bottom'. Order matters.<br>
            Each of these can have +X or -X attached (X is an integer) to move them X pixels away.<br>
            Sample Value: left+2 bottom-2<br>
            Default Value: center center<br>
            See <a href="#positioning-example">Example below</a> for usage
        </tags:nameValue>
        <tags:nameValue name="data-position-of">
            When positioning a dialog, the reference object for position.<br>
            Values: css selector<br>
            Sample Value: '#button1'<br>
            Default Value: window object when not present<br>
            See <a href="#positioning-example">Example below</a> for usage
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


<h2 id="positioning-example">Positioning</h2>

<p class="description">
    A popup that will be immediately above the button
</p>
<div class="column-4-20 clearfix dialog-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <div class="dn" id="positioned-popup-1" 
            data-title="Popup Example"
            data-position-my="right bottom"
            data-position-at="right top-4"
            data-position-of="#positioned-link-1"
            data-width="300">
                This popup is 4 pixels above the button, with their right edges aligned.
        </div>
        <cti:button id="positioned-link-1" label="Positioned Popup" data-popup="#positioned-popup-1" data-popup-toggle=""/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;dn&quot; id=&quot;positioned-popup-1&quot;
    data-title=&quot;Popup Example&quot;
    data-position-my=&quot;right bottom&quot;
    data-position-at=&quot;right top-4&quot;
    data-position-of=&quot;#positioned-link-1&quot;&gt;
        This popup is 4 pixels above the button, with their right edges aligned.
&lt;/div&gt;
&lt;cti:button id=&quot;positioned-link-1&quot; label=&quot;Positioned Popup&quot; data-popup=&quot;#positioned-popup-1&quot; data-popup-toggle=""/&gt;
</pre>
</tags:styleguide>
</cti:standardPage>