<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="blocking">
<tags:styleguide page="blocking">

<style>
.description { line-height: 22px; }
</style>

<script>
$(function() {
    // unblock page (and elements) on escape key
    $(document).keyup(function(e) {
        if (e.which == yg.keys.escape) {
            yukon.ui.unblockPage();
            
            $('.js-block-this').each(function() {
                yukon.ui.unblock($(this));
            });
        }
    });
});
</script>

<p class="description">
    Blocking is a way to disable user action for a container or the whole page.  It's useful when you need the keep
    the user from interacting while you are waiting for something to happen. Blocking things is done using yukon's js 
    blocking api, or by using special class names that have will trigger the yukon's js blocking api.
    <span class="warning">Blocking should only be used as a last resort since it is visually jarring to users.</span>
    <a href="buttons#busy-buttons-example">Busy buttons</a> should be used when needing to prevent double form submission.
</p>

<h2 id="block-element-example">Block An Element</h2>

<p class="description">
    Blocking the container with the <span class="label label-attr">.js-block-this</span> class when the button
    with the <span class="label label-attr">.js-blocker</span> class is clicked.  Note there is a global event handler
    that handles clicks on <span class="label label-attr">.js-blocker</span> elements for you.  Unblock the container
    when the 'esc' key is pressed.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <div class="js-block-this box" style="border: 1px solid #ccc; padding: 4px;">
            <span>In this example, clicking the block button will block only this box. Hit esc key to unblock.</span>
            <div class="action-area"><cti:button label="Block This" classes="js-blocker"/></div>
        </div>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;js-block-this js-my-container box&quot; style=&quot;border: 1px solid #ccc; padding: 4px;&quot;&gt;
    &lt;span&gt;In this example, clicking the block button will block only this box. Hit esc key to unblock.&lt;/span&gt;
    &lt;div class=&quot;action-area&quot;&gt;&lt;cti:button label=&quot;Block This&quot; classes=&quot;js-blocker&quot;/&gt;&lt;/div&gt;
&lt;/div&gt;
&lt;script&gt;
$(document).keyup(function(e) {
    if (e.which == yg.keys.escape) {
        yukon.ui.unblock($('.js-my-container'));
    }
});
&lt;/script&gt;
</pre>

<h2 id="block-element-example">Block The Whole Page</h2>

<p class="description">
    Blocking the whole page when the button with the <span class="label label-attr">.js-blocker</span> class is clicked.  
    Note there is a global event handler that handles clicks on <span class="label label-attr">.js-blocker</span> elements 
    for you.  Unblock the page after 3 seconds.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <cti:button label="Block The Page" classes="js-my-btn js-blocker"/>
        <script>
        $('.js-my-btn').click(function(e) {
            setTimeout(function() { yukon.ui.unblockPage(); }, 3000);
        });
        </script>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;cti:button label=&quot;Block The Page&quot; classes=&quot;js-my-btn js-blocker&quot;/&gt;
&lt;script&gt;
$('.js-my-btn').click(function(e) {
    setTimeout(function() { yukon.ui.unblockPage(); }, 3000);
});
&lt;/script&gt;
</pre>

</tags:styleguide>
</cti:standardPage>