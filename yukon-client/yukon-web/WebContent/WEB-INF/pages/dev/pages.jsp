<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>

<cti:standardPage module="dev" page="yukon-pages">
<cti:includeScript link="/resources/js/lib/google-code-prettify/prettify.js"/>
<cti:includeCss link="/resources/js/lib/google-code-prettify/prettify.css"/>

<style>
.description { line-height: 22px; font-size: 16px; }
h2 { margin-top: 40px; }
dd { margin-bottom: 20px; line-height: 24px; }
dt, dd { font-size: 16px; }
</style>

<p class="description">
    This section describes how pages and page templating works in Yukon.
</p>


<h2>Modules</h2>
<p class="description">
    Yukon separates pages into modules.  They are defined in the 
    <span class="label label-attr">module_config.xml</span> file.  Note: These are not JavaScript modules. 
    Modules define a set of pages with hierarchy used to build bread crumbs.<br>
    A module can contain the following:<br>
    <dl>
        <dt><span class="label label-attr">skin</span></dt>
        <dd>
            A template jsp file.  It defines common parts of the page look like, such as headers, footers,
            and menus.  There are 3 skins: STANDARD, LEFT_SIDE_MENU, and PURPLE.
            STANDARD is the one you will most likely use.
        </dd>
        <dt><span class="label label-attr">css</span></dt>
        <dd>
            A path to a css file to include for all pages in the module.
        </dd>
        <dt><span class="label label-attr">script</span></dt>
        <dd>
            A path to a JavaScript file to include for all pages in the module.
        </dd>
        <dt><span class="label label-attr">pages</span></dt>
        <dd>
            A list of pages in that module for that module.
        </dd>
        <dt><span class="label label-attr">search</span></dt>
        <dd class="error">depricated</dd>
        <dt><span class="label label-attr">menu</span></dt>
        <dd class="error">depricated</dd>
    </dl>
</p>

<h2>Pages</h2>
<p class="description">
    Pages are the most interesting element; they define all the characteristics of a page.
    <h4>Required attributes:</h4>
    <dl>
        <dt><span class="label label-attr">name</span></dt>
        <dd>Must be unique within the module. Used as part of i18n keys.</dd>
        <dt><span class="label label-attr">Type</span></dt>
        <dd>
            Used to determine title, header, and crumb text. Values are: BASIC, DETAIL, DETAIL_CHILD, LANDING, and BLANK.
            When in doubt, just use BASIC. See standard.xml for how the type is used.
        </dd>
    </dl>
    <h4>Optional attributes:</h4>
    <dl>
        <dt><span class="label label-attr">contributeToMenu</span></dt>
        <dd>If 'true' this page will be an option in a side bar menu.</dd>
        <dt><span class="label label-attr">navigationMenuRoot</span></dt>
        <dd>If 'true' this page will be the first page of a side bar menu.</dd>
        <dt><span class="label label-attr">hideFavorite</span></dt>
        <dd>If 'true' this page will not be favoritable.</dd>
        <dt><span class="label label-attr">actions</span></dt>
        <dd class="error">depricated</dd>
        <dt><span class="label label-attr">hideSearch</span></dt>
        <dd class="error">depricated</dd>
    </dl>
    <h4>Pages can have the following elements inside them:</h4>
    <dl>
        <dt><span class="label label-attr">requireRoleProperty</span></dt>
        <dd>Used to require a boolean role property to access the page.</dd>
        <dt><span class="label label-attr">requireCanEditEnergyCompany</span></dt>
        <dd>User must have the ability to edit energy companies to access the page.</dd>
        <dt><span class="label label-attr">link</span></dt>
        <dd>Used as the link on the breadcrumb, only needed if the page has a child page.</dd>
        <dt><span class="label label-attr">labelArgument</span></dt>
        <dd>A string parsed as EL. Used to pass dynamic data to the i18n code i.e. the name of a device.</dd>
        <dt><span class="label label-attr">infoInclude</span></dt>
        <dd>A path to a jsp file included in a special location on the page.</dd>
        <dt><span class="label label-attr">pages</span></dt>
        <dd>Another pages section to define child pages.</dd>
    </dl>
</p>
<h4 class="subtle">Example: The actual dev module from module_config.xml.</h4>
<pre class="code prettyprint">
&lt;modules&gt;

...

    &lt;module name=&quot;dev&quot;&gt;
        &lt;skin name=&quot;STANDARD&quot;/&gt;
        &lt;pages&gt;
            &lt;page name=&quot;home&quot; type=&quot;BASIC&quot;&gt;
                &lt;requireRoleProperty value=&quot;DEVELOPMENT_MODE&quot;/&gt;
                &lt;link&gt;/dev&lt;/link&gt;
                &lt;pages&gt;
                    &lt;page name=&quot;webServices.eimTest&quot; type=&quot;BASIC&quot;/&gt;
                    &lt;page name=&quot;webServices.loadControl&quot; type=&quot;BASIC&quot;/&gt;
                    &lt;page name=&quot;webServices.account&quot; type=&quot;BASIC&quot;/&gt;
                    &lt;page name=&quot;pointInjection&quot; type=&quot;BASIC&quot;/&gt;
                    &lt;page name=&quot;bulkPointInjection&quot; type=&quot;BASIC&quot;/&gt;
                    &lt;page name=&quot;setupDatabase&quot; type=&quot;BASIC&quot;/&gt;
                    &lt;page name=&quot;miscellaneousMethod&quot; type=&quot;BASIC&quot;/&gt;
                    &lt;page name=&quot;ivvc.ivvcSimulator&quot; type=&quot;BASIC&quot;/&gt;
                    &lt;page name=&quot;rfnTest&quot; type=&quot;BASIC&quot;&gt;
                        &lt;link&gt;/dev/rfn/viewBase&lt;/link&gt;
                        &lt;pages&gt;
                            &lt;page name=&quot;rfnTest.viewMeterReadArchive&quot; type=&quot;DETAIL&quot;/&gt;
                            &lt;page name=&quot;rfnTest.viewEventArchive&quot; type=&quot;DETAIL&quot;/&gt;
                            &lt;page name=&quot;rfnTest.viewAlarmArchive&quot; type=&quot;DETAIL&quot;/&gt;
                            &lt;page name=&quot;rfnTest.viewDataSimulator&quot; type=&quot;BASIC&quot;/&gt;
                        &lt;/pages&gt;
                    &lt;/page&gt;
                    &lt;page name=&quot;ecobee.mockTest&quot; type=&quot;BASIC&quot;/&gt;
                    &lt;page name=&quot;i18nDemo&quot; type=&quot;BASIC&quot;/&gt;
                    &lt;page name=&quot;i18nDemo.scopes&quot; type=&quot;BASIC&quot;/&gt;
                    &lt;page name=&quot;jsTesting&quot; type=&quot;BASIC&quot;/&gt;
                    &lt;page name=&quot;yukon-js-lib&quot; type=&quot;BASIC&quot;/&gt;
                    &lt;page name=&quot;yukon-pages&quot; type=&quot;BASIC&quot;/&gt;&lt;!-- The page you are on right now! --&gt;
                    &lt;page name=&quot;styleguide&quot; type=&quot;BASIC&quot;&gt;
                        &lt;link&gt;/dev/styleguide&lt;/link&gt;
                        &lt;pages&gt;
                            &lt;page name=&quot;grids&quot; type=&quot;BASIC&quot;/&gt;
                            &lt;page name=&quot;tables&quot; type=&quot;BASIC&quot;/&gt;
                            &lt;page name=&quot;containers&quot; type=&quot;BASIC&quot;/&gt;
                            &lt;page name=&quot;icons&quot; type=&quot;BASIC&quot;/&gt;
                            &lt;page name=&quot;labels.badges&quot; type=&quot;BASIC&quot;/&gt;
                            &lt;page name=&quot;progressbars&quot; type=&quot;BASIC&quot;/&gt;
                            &lt;page name=&quot;buttons&quot; type=&quot;BASIC&quot;/&gt;
                            &lt;page name=&quot;switches&quot; type=&quot;BASIC&quot;/&gt;
                            &lt;page name=&quot;inputs&quot; type=&quot;BASIC&quot;/&gt;
                            &lt;page name=&quot;blocking&quot; type=&quot;BASIC&quot;/&gt;
                            &lt;page name=&quot;pickers&quot; type=&quot;BASIC&quot;/&gt;
                            &lt;page name=&quot;dialogs&quot; type=&quot;BASIC&quot;/&gt;
                            &lt;page name=&quot;datePickers&quot; type=&quot;BASIC&quot;/&gt;
                            &lt;page name=&quot;groupPickers&quot; type=&quot;BASIC&quot;/&gt;
                            &lt;page name=&quot;deviceCollections&quot; type=&quot;BASIC&quot;/&gt;
                        &lt;/pages&gt;
                    &lt;/page&gt;
                &lt;/pages&gt;
            &lt;/page&gt;
        &lt;/pages&gt;
    &lt;/module&gt;

...

&lt;/modules&gt;
</pre>

<h2>Page Parts: Title, Header, and Crumb</h2>
<p class="description">
    The page parts are derived from the combination of the page type and the module name, page name, page description.
    It's pretty complicated, see standard.xml.  The easiest way to define exactly what you want is to use the following 
    keys, where 'mypage' is your page name from module_config.xml.
    <span class="label label-attr">yukon.web.modules.dev.mypage.pageTitle</span>,
    <span class="label label-attr">yukon.web.modules.dev.mypage.pageHeading</span>, and
    <span class="label label-attr">yukon.web.modules.dev.mypage.crumbTitle</span>.
</p>

<h2>Page Actions and Page Buttons</h2>
<p class="description">
    Page actions are dropdown menu options added to an a 'actions' dropdown automatically at the top-right of the page
    when dropdown menu options are found inside an element with the id 'page-actions'.
</p>
<h4 class="subtle">Example: Page Actions</h4>
<pre class="code prettyprint">
&lt;div id=&quot;page-actions&quot; class=&quot;dn&quot;&gt;
    &lt;cm:dropdownOption label=&quot;Do A Thing&quot;/&gt;
    &lt;cm:dropdownOption label=&quot;Do A Another Thing&quot;/&gt;
&lt;/div&gt;
</pre>
<p class="description">
    Page buttons are buttons added at the top-right of the page when they are found inside an element with the id 
    'page-buttons'.
</p>
<h4 class="subtle">Example: Page Buttons</h4>
<pre class="code prettyprint">
&lt;div id=&quot;page-buttons&quot; class=&quot;dn&quot;&gt;
    &lt;cti:button icon=&quot;icon-brick&quot; label=&quot;Play Legos&quot;/&gt; 
&lt;/div&gt;
</pre>

<script>$(function () { prettyPrint(); });</script>
</cti:standardPage>