<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="progressbars">
<tags:styleguide page="progressbars">

<style>
.description { line-height: 22px; }
</style>

<p class="description">
    Yukon uses <a target="_blank" href="http://getbootstrap.com/components/#progress">bootstrap's</a> progress bars and 
    the <span class="label label-attr">&lt;tags:updateableProgressBar&gt;</span> tag will create bars that are driven by
    updating background tasks.
</p>

<h2>Simple Progress Bars</h2>

<p class="description">
    Create progress bars with a <span class="label label-attr">.progress</span> wrapper around a 
    <span class="label label-attr">.progress-bar</span> element.  Color the inner bar with
    <span class="label label-attr">.progress-bar-success</span>, <span class="label label-attr">.progress-bar-info</span>,
    <span class="label label-attr">.progress-bar-warning</span>, or 
    <span class="label label-attr">.progress-bar-danger</span>. 
    Remove <span class="label label-attr">.sr-only</span> to show inner text. 
    Use <span class="label label-attr">.progress-bar-low</span> to keep text visible when the bar would normally be super 
    thin.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <p>
            <div class="progress">
                <div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" style="width: 40%">
                    <span class="sr-only">40% Complete (success)</span>
                </div>
            </div>
        </p>
        <p>
            <div class="progress">
                <div class="progress-bar progress-bar-info" role="progressbar" aria-valuenow="20" aria-valuemin="0" aria-valuemax="100" style="width: 20%">
                    <span class="sr-only">20% Complete</span>
                </div>
            </div>
        </p>
        <p>
            <div class="progress">
                <div class="progress-bar progress-bar-warning" role="progressbar" aria-valuenow="60" aria-valuemin="0" aria-valuemax="100" style="width: 60%">
                    <span class="sr-only">60% Complete (warning)</span>
                </div>
            </div>
        </p>
        <p>
            <div class="progress">
                <div class="progress-bar progress-bar-danger" role="progressbar" aria-valuenow="80" aria-valuemin="0" aria-valuemax="100" style="width: 80%">
                    <span class="sr-only">80% Complete (danger)</span>
                </div>
            </div>
        </p>
        <p>
            <div class="progress">
                <div class="progress-bar progress-bar-danger progress-bar-low" role="progressbar" aria-valuenow="1" aria-valuemin="0" aria-valuemax="100" style="width: 1%">
                    <span>1%</span>
                </div>
            </div>
        </p>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;p&gt;
    &lt;div class=&quot;progress&quot;&gt;
        &lt;div class=&quot;progress-bar progress-bar-success&quot; role=&quot;progressbar&quot; aria-valuenow=&quot;40&quot; aria-valuemin=&quot;0&quot; aria-valuemax=&quot;100&quot; style=&quot;width: 40%&quot;&gt;
            &lt;span class=&quot;sr-only&quot;&gt;40% Complete (success)&lt;/span&gt;
        &lt;/div&gt;
    &lt;/div&gt;
&lt;/p&gt;
&lt;p&gt;
    &lt;div class=&quot;progress&quot;&gt;
        &lt;div class=&quot;progress-bar progress-bar-info&quot; role=&quot;progressbar&quot; aria-valuenow=&quot;20&quot; aria-valuemin=&quot;0&quot; aria-valuemax=&quot;100&quot; style=&quot;width: 20%&quot;&gt;
            &lt;span class=&quot;sr-only&quot;&gt;20% Complete&lt;/span&gt;
        &lt;/div&gt;
    &lt;/div&gt;
&lt;/p&gt;
&lt;p&gt;
    &lt;div class=&quot;progress&quot;&gt;
        &lt;div class=&quot;progress-bar progress-bar-warning&quot; role=&quot;progressbar&quot; aria-valuenow=&quot;60&quot; aria-valuemin=&quot;0&quot; aria-valuemax=&quot;100&quot; style=&quot;width: 60%&quot;&gt;
            &lt;span class=&quot;sr-only&quot;&gt;60% Complete (warning)&lt;/span&gt;
        &lt;/div&gt;
    &lt;/div&gt;
&lt;/p&gt;
&lt;p&gt;
    &lt;div class=&quot;progress&quot;&gt;
        &lt;div class=&quot;progress-bar progress-bar-danger&quot; role=&quot;progressbar&quot; aria-valuenow=&quot;80&quot; aria-valuemin=&quot;0&quot; aria-valuemax=&quot;100&quot; style=&quot;width: 80%&quot;&gt;
            &lt;span class=&quot;sr-only&quot;&gt;80% Complete (danger)&lt;/span&gt;
        &lt;/div&gt;
    &lt;/div&gt;
&lt;/p&gt;
&lt;p&gt;
    &lt;div class=&quot;progress&quot;&gt;
        &lt;div class=&quot;progress-bar progress-bar-danger progress-bar-low&quot; role=&quot;progressbar&quot; aria-valuenow=&quot;1&quot; aria-valuemin=&quot;0&quot; aria-valuemax=&quot;100&quot; style=&quot;width: 1%&quot;&gt;
            &lt;span&gt;1%&lt;/span&gt;
        &lt;/div&gt;
    &lt;/div&gt;
&lt;/p&gt;
</pre>

<h2>Animated Progress Bars</h2>

<p class="description">
    Stripe bars with <span class="label label-attr">.progress-bar-striped</span> and animate them with 
    <span class="label label-attr">.active</span>.  As a bonus there are being updated with super sexy javascript.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <p>
            <div class="progress js-updating-bar" data-progress="40">
                <div class="progress-bar progress-bar-success progress-bar-striped active" role="progressbar" aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" style="width: 40%">
                    <span class="sr-only">40% Complete (success)</span>
                </div>
            </div>
        </p>
        <p>
            <div class="progress js-updating-bar" data-progress="20">
                <div class="progress-bar progress-bar-info progress-bar-striped active" role="progressbar" aria-valuenow="20" aria-valuemin="0" aria-valuemax="100" style="width: 20%">
                    <span class="sr-only">20% Complete</span>
                </div>
            </div>
        </p>
        <p>
            <div class="progress js-updating-bar" data-progress="60">
                <div class="progress-bar progress-bar-warning progress-bar-striped active" role="progressbar" aria-valuenow="60" aria-valuemin="0" aria-valuemax="100" style="width: 60%">
                    <span class="sr-only">60% Complete (warning)</span>
                </div>
            </div>
        </p>
        <p>
            <div class="progress js-updating-bar" data-progress="1">
                <div class="progress-bar progress-bar-danger progress-bar-low progress-bar-striped active" role="progressbar" aria-valuenow="1" aria-valuemin="0" aria-valuemax="100" style="width: 1%">
                    <span>1%</span>
                </div>
            </div>
        </p>
        <script>
            setInterval(function () {
                $('.js-updating-bar').each(function (idx, bar) {
                    bar = $(bar);
                    var width = bar.data('progress');
                    width = !width ?  1 : width + 5;
                    if (width > 100) width = 1;
                    var percentage = yukon.percent(width, 100, 2);
                    
                    bar.data('progress', width)
                    .find('.progress-bar').css({ width: percentage })
                    .find('span').text(percentage);
                });
            }, 600);
        </script>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;p&gt;
    &lt;div class=&quot;progress&quot;&gt;
        &lt;div class=&quot;progress-bar progress-bar-success progress-bar-striped active&quot; role=&quot;progressbar&quot; aria-valuenow=&quot;40&quot; aria-valuemin=&quot;0&quot; aria-valuemax=&quot;100&quot; style=&quot;width: 40%&quot;&gt;
            &lt;span class=&quot;sr-only&quot;&gt;40% Complete (success)&lt;/span&gt;
        &lt;/div&gt;
    &lt;/div&gt;
&lt;/p&gt;
&lt;p&gt;
    &lt;div class=&quot;progress&quot;&gt;
        &lt;div class=&quot;progress-bar progress-bar-info progress-bar-striped active&quot; role=&quot;progressbar&quot; aria-valuenow=&quot;20&quot; aria-valuemin=&quot;0&quot; aria-valuemax=&quot;100&quot; style=&quot;width: 20%&quot;&gt;
            &lt;span class=&quot;sr-only&quot;&gt;20% Complete&lt;/span&gt;
        &lt;/div&gt;
    &lt;/div&gt;
&lt;/p&gt;
&lt;p&gt;
    &lt;div class=&quot;progress&quot;&gt;
        &lt;div class=&quot;progress-bar progress-bar-warning progress-bar-striped active&quot; role=&quot;progressbar&quot; aria-valuenow=&quot;60&quot; aria-valuemin=&quot;0&quot; aria-valuemax=&quot;100&quot; style=&quot;width: 60%&quot;&gt;
            &lt;span class=&quot;sr-only&quot;&gt;60% Complete (warning)&lt;/span&gt;
        &lt;/div&gt;
    &lt;/div&gt;
&lt;/p&gt;
&lt;p&gt;
    &lt;div class=&quot;progress js-updating-bar&quot;&gt;
        &lt;div class=&quot;progress-bar progress-bar-danger progress-bar-low progress-bar-striped active&quot; role=&quot;progressbar&quot; aria-valuenow=&quot;1&quot; aria-valuemin=&quot;0&quot; aria-valuemax=&quot;100&quot; style=&quot;width: 1%&quot;&gt;
            &lt;span&gt;1%&lt;/span&gt;
        &lt;/div&gt;
    &lt;/div&gt;
&lt;/p&gt;
&lt;script&gt;
    setInterval(function () {
        var bar = $('.js-updating-bar'),
            width = bar.data('progress');
        
        width = !width ?  1 : width + 5;
        if (width &gt; 100) width = 1;
        var percentage = yukon.percent(width, 100, 2);
        
        bar.data('progress', width)
        .find('.progress-bar').css({ width: percentage })
        .find('span').text(percentage);
    }, 600);
&lt;/script&gt;
</pre>

<h2>Stacked Progress Bars</h2>

<p class="description">Place multiple bars into the same .progress to stack them.</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one">
        <h4 class="subtle">Example:</h4>
    </div>
    <div class="column two nogutter">
        <div class="progress">
            <div class="progress-bar progress-bar-success" style="width: 35%">
                <span class="sr-only">35% Complete (success)</span>
            </div>
            <div class="progress-bar progress-bar-warning" style="width: 20%">
                <span class="sr-only">20% Complete (warning)</span>
            </div>
            <div class="progress-bar progress-bar-danger" style="width: 10%">
                <span class="sr-only">10% Complete (danger)</span>
            </div>
        </div>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;progress&quot;&gt;
    &lt;div class=&quot;progress-bar progress-bar-success&quot; style=&quot;width: 35%&quot;&gt;
        &lt;span class=&quot;sr-only&quot;&gt;35% Complete (success)&lt;/span&gt;
    &lt;/div&gt;
    &lt;div class=&quot;progress-bar progress-bar-warning&quot; style=&quot;width: 20%&quot;&gt;
        &lt;span class=&quot;sr-only&quot;&gt;20% Complete (warning)&lt;/span&gt;
    &lt;/div&gt;
    &lt;div class=&quot;progress-bar progress-bar-danger&quot; style=&quot;width: 10%&quot;&gt;
        &lt;span class=&quot;sr-only&quot;&gt;10% Complete (danger)&lt;/span&gt;
    &lt;/div&gt;
&lt;/div&gt;
</pre>

</tags:styleguide>
</cti:standardPage>