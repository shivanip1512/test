<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:wp="http://webworks.com/publisher/xml/schema"
                version="1.0">

<!-- Page Rules -->

  <xsl:template match="/">
    <HTML>
      <HEAD>
        <xsl:apply-templates select="/wp:Document/wp:Title" />
      </HEAD>

      <STYLE>
        a:active  { color: #0000CC}
        a:hover  { color: #CC0033}
        a:link    { color: #0066cc}
        a:visited { color: #0066cc}

          { font-family: Verdana,Arial, Helvetica, sans-serif; }
        .Body
          { color: #000000;
            font-family: Verdana,Arial, Helvetica, sans-serif;
            font-size: 11px; font-style: normal; font-weight: normal;
            margin-left: 0em; margin-top: 0.5em; margin-bottom: 0.5em;
            text-align: left; text-indent: 0em; text-decoration: none;
            white-space: normal }
        .Caption
          { color: #000000;
            font-family: Verdana,Arial, Helvetica, sans-serif;
            font-size: 11px; font-style: italic; font-weight: bold;
            margin-left: 0em; margin-top: 0.5em; margin-bottom: 0.5em;
            text-align: center; text-indent: 0em; text-decoration: none;
            white-space: normal }
        .CellBody
          { color: #000000;
            font-family: Verdana,Arial, Helvetica, sans-serif;
            font-size: 11px; font-style: normal; font-weight: normal;
            margin-left: 0em; margin-top: 0em; margin-bottom: 0em;
            text-align: left; text-indent: 0em; text-decoration: none;
            white-space: normal }
        .CellHeading
          { color: #000000;
            font-family: Verdana,Arial, Helvetica, sans-serif;
            font-size: 11px; font-style: normal; font-weight: bold;
            margin-left: 0em; margin-top: 0em; margin-bottom: 0em;
            text-align: center; text-indent: 0em; text-decoration: none;
            white-space: normal }
        .Heading1
          { color: #0066cc;
            font-family: Verdana,Arial, Helvetica, sans-serif;
            font-size: 18px; font-style: normal; font-weight: bold;
            margin-left: 0em; margin-top: 0.5em; margin-bottom: 0.5em;
            text-align: left; text-indent: 0em; text-decoration: none;
            white-space: normal }
        .Heading2
          { color: #0066cc;
            font-family: Verdana,Arial, Helvetica, sans-serif;
            font-size: 16px; font-style: normal; font-weight: bold;
            margin-left: 0em; margin-top: 0.75em; margin-bottom: 0.5em;
            text-align: left; text-indent: 0em; text-decoration: none;
            white-space: normal }
        .Heading3
          { color: #0066cc;
            font-family: Verdana,Arial, Helvetica, sans-serif;
            font-size: 14px; font-style: normal; font-weight: bold;
            margin-left: 0em; margin-top: 0.75em; margin-bottom: 0.5em;
            text-align: left; text-indent: 0em; text-decoration: none;
            white-space: normal }
        .Heading4
          { color: #0066cc;
            font-family: Verdana,Arial, Helvetica, sans-serif;
            font-size: 12px; font-style: italic; font-weight: bold;
            margin-left: 0em; margin-top: 0.75em; margin-bottom: 0.5em;
            text-align: left; text-indent: 0em; text-decoration: none;
            white-space: normal }
        .Heading5
          { color: #000000;
            font-family: Verdana,Arial, Helvetica, sans-serif;
            font-size: 11px; font-style: italic; font-weight: bold;
            margin-left: 0em; margin-top: 0.75em; margin-bottom: 0.5em;
            text-align: left; text-indent: 0em; text-decoration: none;
            white-space: normal }
        .Indented1
          { color: #000000;
            font-family: Verdana,Arial, Helvetica, sans-serif;
            font-size: 11px; font-style: normal; font-weight: normal;
            margin-left: 2.25em; margin-top: 0.5em; margin-bottom: 0.5em;
            text-align: left; text-indent: 0em; text-decoration: none;
            white-space: normal }
        .Indented2
          { color: #000000;
            font-family: Verdana,Arial, Helvetica, sans-serif;
            font-size: 11px; font-style: normal; font-weight: normal;
            margin-left: 5em; margin-top: 0.5em; margin-bottom: 0.5em;
            text-align: left; text-indent: 0em; text-decoration: none;
            white-space: normal }
        .Indented3
          { color: #000000;
            font-family: Verdana,Arial, Helvetica, sans-serif;
            font-size: 11px; font-style: normal; font-weight: normal;
            margin-left: 7.5em; margin-top: 0.5em; margin-bottom: 0.5em;
            text-align: left; text-indent: 0em; text-decoration: none;
            white-space: normal }
        .Indented4
          { color: #000000;
            font-family: Verdana,Arial, Helvetica, sans-serif;
            font-size: 11px; font-style: normal; font-weight: normal;
            margin-left: 10em; margin-top: 0.5em; margin-bottom: 0.5em;
            text-align: left; text-indent: 0em; text-decoration: none;
            white-space: normal }
        .Indented5
          { color: #000000;
            font-family: Verdana,Arial, Helvetica, sans-serif;
            font-size: 11px; font-style: normal; font-weight: normal;
            margin-left: 12.5em; margin-top: 0.5em; margin-bottom: 0.5em;
            text-align: left; text-indent: 0em; text-decoration: none;
            white-space: normal }
        .IXGroupTitle
          { color: #0066cc;
            font-family: Verdana,Arial, Helvetica, sans-serif;
            font-size: 16px; font-style: normal; font-weight: bold;
            margin-left: 0em; margin-top: 0.5em; margin-bottom: 0.5em;
            text-align: left; text-indent: 0em; text-decoration: none;
            white-space: normal }
        .IXHeadingLink
          { color: #000000;
            font-family: Verdana,Arial, Helvetica, sans-serif;
            font-size: 11px; font-style: normal; font-weight: normal;
            margin-left: 2.5em; margin-top: 0em; margin-bottom: 0em;
            text-align: left; text-indent: 0em; text-decoration: none;
            white-space: normal }
        .IXLevel1
          { color: #000000;
            font-family: Verdana,Arial, Helvetica, sans-serif;
            font-size: 11px; font-style: normal; font-weight: normal;
            margin-left: 2.5em; margin-top: 0em; margin-bottom: 0em;
            text-align: left; text-indent: 0em; text-decoration: none;
            white-space: normal }
        .IXLevel2
          { color: #000000;
            font-family: Verdana,Arial, Helvetica, sans-serif;
            font-size: 11px; font-style: normal; font-weight: normal;
            margin-left: 5em; margin-top: 0em; margin-bottom: 0em;
            text-align: left; text-indent: 0em; text-decoration: none;
            white-space: normal }
        .IXLevel3
          { color: #000000;
            font-family: Verdana,Arial, Helvetica, sans-serif;
            font-size: 11px; font-style: normal; font-weight: normal;
            margin-left: 7.5em; margin-top: 0em; margin-bottom: 0em;
            text-align: left; text-indent: 0em; text-decoration: none;
            white-space: normal }
        .IXLevel4
          { color: #000000;
            font-family: Verdana,Arial, Helvetica, sans-serif;
            font-size: 11px; font-style: normal; font-weight: normal;
            margin-left: 10em; margin-top: 0em; margin-bottom: 0em;
            text-align: left; text-indent: 0em; text-decoration: none;
            white-space: normal }
        .IXLevel5
          { color: #000000;
            font-family: Verdana,Arial, Helvetica, sans-serif;
            font-size: 11px; font-style: normal; font-weight: normal;
            margin-left: 12.5em; margin-top: 0em; margin-bottom: 0em;
            text-align: left; text-indent: 0em; text-decoration: none;
            white-space: normal }
        .IXTitle
          { color: #0066cc;
            font-family: Verdana,Arial, Helvetica, sans-serif;
            font-size: 18px; font-style: normal; font-weight: bold;
            margin-left: 0em; margin-top: 0.5em; margin-bottom: 0.5em;
            text-align: center; text-indent: 0em; text-decoration: none;
            white-space: normal }
        .Preformatted
          { color: #000000;
            font-family: monospace;
            font-size: 11px; font-style: normal; font-weight: normal;
            margin-left: 2.0em; margin-top: 0em; margin-bottom: 0em;
            text-align: left; text-indent: 0em; text-decoration: none;
            white-space: pre }
        .SmartList1
          { color: #000000;
            font-family: Verdana,Arial, Helvetica, sans-serif;
            font-size: 11px; font-style: normal; font-weight: normal;
            margin-left: 0em; margin-top: 0.25em; margin-bottom: 0.25em;
            text-align: left; text-indent: 0em; text-decoration: none;
            white-space: normal }
        .SmartList2
          { color: #000000;
            font-family: Verdana,Arial, Helvetica, sans-serif;
            font-size: 11px; font-style: normal; font-weight: normal;
            margin-left: 1em; margin-top: 0.25em; margin-bottom: 0.25em;
            text-align: left; text-indent: 0em; text-decoration: none;
            white-space: normal }
        .SmartList3
          { color: #000000;
            font-family: Verdana,Arial, Helvetica, sans-serif;
            font-size: 11px; font-style: normal; font-weight: normal;
            margin-left: 5em; margin-top: 0.25em; margin-bottom: 0.25em;
            text-align: left; text-indent: 0em; text-decoration: none;
            white-space: normal }
        .SmartList4
          { color: #000000;
            font-family: Verdana,Arial, Helvetica, sans-serif;
            font-size: 11px; font-style: normal; font-weight: normal;
            margin-left: 7.5em; margin-top: 0.25em; margin-bottom: 0.25em;
            text-align: left; text-indent: 0em; text-decoration: none;
            white-space: normal }
        .SmartList5
          { color: #000000;
           font-family: Verdana,Arial, Helvetica, sans-serif;
            font-size: 11px; font-style: normal; font-weight: normal;
            margin-left: 10em; margin-top: 0.25em; margin-bottom: 0.25em;
            text-align: left; text-indent: 0em; text-decoration: none;
            white-space: normal }
        .TableTitle
          { color: #000000;
            font-family: Verdana,Arial, Helvetica, sans-serif;
            font-size: 11px; font-style: italic; font-weight: bold;
            margin-left: 0em; margin-top: 0.5em; margin-bottom: 0.5em;
            text-align: center; text-indent: 0em; text-decoration: none;
            white-space: normal }
        .Title
          { color: #0066cc;
            font-family: Verdana,Arial, Helvetica, sans-serif;
            font-size: 20px; font-style: normal; font-weight: bold;
            margin-left: 0em; margin-top: 0.5em; margin-bottom: 0.5em;
            text-align: center; text-indent: 0em; text-decoration: none;
            white-space: normal }
        .TOC1
          { color: #000000;
            font-family: Verdana,Arial, Helvetica, sans-serif;
            font-size: 14px; font-style: normal; font-weight: bold;
            margin-left: 0em; margin-top: 0.25em; margin-bottom: 0.25em;
            text-align: left; text-indent: 0em; text-decoration: none;
            white-space: normal }
        .TOC2
          { color: #000000;
            font-family: Verdana,Arial, Helvetica, sans-serif;
            font-size: 13px; font-style: normal; font-weight: normal;
            margin-left: 2.5em; margin-top: 0em; margin-bottom: 0em;
            text-align: left; text-indent: 0em; text-decoration: none;
            white-space: normal }
        .TOC3
          { color: #000000;
            font-family: Verdana,Arial, Helvetica, sans-serif;
            font-size: 12px; font-style: normal; font-weight: normal;
            margin-left: 5em; margin-top: 0em; margin-bottom: 0em;
            text-align: left; text-indent: 0em; text-decoration: none;
            white-space: normal }
        .TOC4
          { color: #000000;
            font-family: Verdana,Arial, Helvetica, sans-serif;
            font-size: 11px; font-style: normal; font-weight: normal;
            margin-left: 7.5em; margin-top: 0em; margin-bottom: 0em;
            text-align: left; text-indent: 0em; text-decoration: none;
            white-space: normal }
        .TOC5
          { color: #000000;
            font-family: Verdana,Arial, Helvetica, sans-serif;
            font-size: 11px; font-style: normal; font-weight: normal;
            margin-left: 10em; margin-top: 0em; margin-bottom: 0em;
            text-align: left; text-indent: 0em; text-decoration: none;
            white-space: normal }
        .TOCTitle
          { color: #0066cc;
            font-family: Verdana,Arial, Helvetica, sans-serif;
            font-size: 18px; font-style: normal; font-weight: bold;
            margin-left: 0em; margin-top: 0.5em; margin-bottom: 0.5em;
            text-align: center; text-indent: 0em; text-decoration: none;
            white-space: normal }
      </STYLE>

      <BODY background="images/backgrnd.gif">
        <TABLE align="right" cellpadding="0" cellspacing="0">
          <xsl:apply-templates select="/wp:Document/wp:Navbar" />
        </TABLE>

        <xsl:apply-templates select="/wp:Document/wp:Logo" />
	<HR align="left" />

        <xsl:apply-templates select="/wp:Document/wp:InlineTitle" />

        <xsl:apply-templates select="/wp:Document/wp:Letters" />

	<BLOCKQUOTE>
          <xsl:apply-templates select="/wp:Document/wp:Content" />
	</BLOCKQUOTE>

        <HR />
        <TABLE align="right" border="0" cellpadding="0" cellspacing="0">
          <xsl:apply-templates select="/wp:Document/wp:ContactInfo" />
        </TABLE>
        
        <TABLE cellpadding="0" cellspacing="0">
          <xsl:apply-templates select="/wp:Document/wp:Navbar" />
        </TABLE>
      </BODY>
    </HTML>
  </xsl:template>

<!-- XML to HTML Rules -->
  <!-- Default Element Rule -->
  <xsl:template match="*">
    <xsl:apply-templates />
  </xsl:template>

  <!-- Rubi Rules -->
  <xsl:template match="RubiGroup">
    <RUBY>
      <xsl:apply-templates />
    </RUBY>
  </xsl:template>

  <xsl:template match="Rubi">
    <RT>
      <xsl:apply-templates />
    </RT>
  </xsl:template>

  <!-- Footnote Element Rule -->
  <xsl:template match="*[@wp:type='footnote_embedded']">
  </xsl:template>

  <!-- Element Rule -->
  <xsl:template match="*[@wp:type='container']">
    <xsl:choose>
      <xsl:when test="@wp:style">
        <SPAN>
          <xsl:attribute name="style"><xsl:value-of select="@wp:style"/></xsl:attribute>
          <xsl:apply-templates />
        </SPAN>
      </xsl:when>
      <xsl:otherwise>
          <xsl:apply-templates />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="*[@wp:type='preformatted']">
    <PRE><xsl:if test="@wp:class">
        <xsl:attribute name="class"><xsl:value-of select="@wp:class"/></xsl:attribute>
      </xsl:if>

      <xsl:choose>
        <xsl:when test="@wp:bullet">
          <DIV style="margin-left: 1em">
            <LI/> <xsl:apply-templates />
          </DIV>
        </xsl:when>

        <xsl:when test="@wp:autonum">
          <TABLE>
            <TR>
              <TD nowrap="1" style="vertical-align: top; padding-right: 0.2em">
                <SPAN>
                  <xsl:attribute name="class"><xsl:value-of select="@wp:class"/></xsl:attribute>
                  <xsl:if test="@wp:style">
                    <xsl:attribute name="style"><xsl:value-of select="@wp:style"/></xsl:attribute>
                  </xsl:if>
                  <xsl:value-of select="@wp:autonum"/>
                </SPAN>
              </TD>
              <TD>
                <SPAN>
                  <xsl:attribute name="class"><xsl:value-of select="@wp:class"/></xsl:attribute>
                  <xsl:if test="@wp:style">
                    <xsl:attribute name="style"><xsl:value-of select="@wp:style"/></xsl:attribute>
                  </xsl:if>
                  <xsl:apply-templates />
                </SPAN>
              </TD>
            </TR>
          </TABLE>
        </xsl:when>

        <xsl:otherwise>
          <xsl:if test="@wp:style">
            <xsl:attribute name="style"><xsl:value-of select="@wp:style"/></xsl:attribute>
          </xsl:if>
          <xsl:apply-templates />
        </xsl:otherwise>

      </xsl:choose>
    </PRE><A><xsl:attribute name="name"><xsl:value-of select="@wp:id"/></xsl:attribute></A>
  </xsl:template>

  <xsl:template match="*[@wp:type='para'] | *[@wp:type='tablecell_heading'] | *[@wp:type='tablecell_body'] | *[@wp:type='tablecell_footing'] | *[@wp:type='tabletitle'] | *[@wp:type='footnote']">
    <A>
      <xsl:attribute name="name"><xsl:value-of select="@wp:id"/></xsl:attribute>
    </A>
    <DIV>
      <xsl:if test="@wp:class">
        <xsl:attribute name="class"><xsl:value-of select="@wp:class"/></xsl:attribute>
      </xsl:if>

      <xsl:choose>
        <xsl:when test="@wp:bullet">
          <DIV style="margin-left: 1.5em">
            <LI/> <xsl:apply-templates />
          </DIV>
        </xsl:when>

        <xsl:when test="@wp:autonum">
          <TABLE>
            <TR>
              <TD nowrap="1" style="vertical-align: top; padding-right: 0.2em">
                <SPAN>
                  <xsl:attribute name="class"><xsl:value-of select="@wp:class"/></xsl:attribute>
                  <xsl:if test="@wp:style">
                    <xsl:attribute name="style"><xsl:value-of select="@wp:style"/></xsl:attribute>
                  </xsl:if>
                  <xsl:value-of select="@wp:autonum"/>
                </SPAN>
              </TD>
              <TD>
                <SPAN>
                  <xsl:attribute name="class"><xsl:value-of select="@wp:class"/></xsl:attribute>
                  <xsl:if test="@wp:style">
                    <xsl:attribute name="style"><xsl:value-of select="@wp:style"/></xsl:attribute>
                  </xsl:if>
                  <xsl:apply-templates />
                </SPAN>
              </TD>
            </TR>
          </TABLE>
        </xsl:when>

        <xsl:otherwise>
          <xsl:if test="@wp:style">
            <xsl:attribute name="style"><xsl:value-of select="@wp:style"/></xsl:attribute>
          </xsl:if>
          <xsl:apply-templates />
        </xsl:otherwise>

      </xsl:choose>
    </DIV>
  </xsl:template>

<!-- Misc. Rules -->
  <!-- Title Rule -->
  <xsl:template match="/wp:Document/wp:Title">
    <TITLE>
      <xsl:value-of select="." />
    </TITLE>
  </xsl:template>

  <!-- Logo Rule -->
  <xsl:template match="/wp:Document/wp:Logo">
    <P>
      <xsl:value-of select="." />
    </P>
    <xsl:apply-templates />
  </xsl:template>

  <!-- Inline Title Rule -->
  <xsl:template match="/wp:Document/wp:InlineTitle">
    <xsl:apply-templates />
  </xsl:template>

  <!-- Navigation Bar Rule -->
  <xsl:template match="/wp:Document/wp:Navbar">
    <TR>
      <TD> <xsl:apply-templates select="wp:TOC" /> </TD>
      <TD> <xsl:apply-templates select="wp:Prev" /> </TD>
      <TD> <xsl:apply-templates select="wp:Next" /> </TD>
      <TD> <xsl:apply-templates select="wp:Index" /> </TD>
    </TR>
  </xsl:template>

  <!-- Index Letter Navigation Rule -->
  <xsl:template match="/wp:Document/wp:Letters">
    <BLOCKQUOTE>
      <SPAN style="color: #999999; font-size: 8pt; font-weight: heavy">
        <xsl:apply-templates select="wp:Letter" />
      </SPAN>
    </BLOCKQUOTE>
  </xsl:template>

  <!-- Index Single Letter Rule -->
  <xsl:template match="/wp:Document/wp:Letters/wp:Letter">
    <xsl:choose>
      <xsl:when test="@wp:first">
      </xsl:when>
      <xsl:otherwise>
        -
      </xsl:otherwise>
    </xsl:choose>
    <A>
      <xsl:attribute name="href">#<xsl:value-of select="@wp:href"/></xsl:attribute>
      <xsl:attribute name="target"><xsl:value-of select="@wp:target"/></xsl:attribute>
      <xsl:value-of select="." />
    </A>
  </xsl:template>

  <!-- Contact Information Rule -->
  <xsl:template match="/wp:Document/wp:ContactInfo">
    <xsl:for-each select="wp:Item">
      <TR><TD align="right"><SPAN style="font-size: 8pt">
	<xsl:apply-templates />
      </SPAN></TD></TR>
    </xsl:for-each>
  </xsl:template>

  <!-- XRef and Hypertext Rule -->
  <xsl:template match="wp:GotoLink">
    <A>
      <xsl:attribute name="href"><xsl:value-of select="@wp:href"/></xsl:attribute>
      <xsl:attribute name="target"><xsl:value-of select="@wp:target"/></xsl:attribute>
      <SPAN>
      <xsl:attribute name="style"><xsl:value-of select="@wp:style"/></xsl:attribute>
      <xsl:apply-templates />
      </SPAN>
    </A>
  </xsl:template>

  <!-- Image Handling Rules -->
  <xsl:template match="wp:Image | *[@wp:type='image']">
    <xsl:if test="@wp:display">
      <BR>
        <xsl:attribute name="clear">all</xsl:attribute>
      </BR>
    </xsl:if>
    <IMG>
      <xsl:attribute name="src"><xsl:value-of select="@wp:href"/></xsl:attribute>

      <xsl:if test="@wp:usemap">
        <xsl:attribute name="usemap"><xsl:value-of select="@wp:usemap"/></xsl:attribute>
      </xsl:if>
      <xsl:if test="@wp:height">
        <xsl:attribute name="height"><xsl:value-of select="@wp:height"/></xsl:attribute>
      </xsl:if>
      <xsl:if test="@wp:width">
        <xsl:attribute name="width"><xsl:value-of select="@wp:width"/></xsl:attribute>
      </xsl:if>
      <xsl:if test="@wp:alt">
        <xsl:attribute name="alt"><xsl:value-of select="@wp:alt"/></xsl:attribute>
      </xsl:if>
      <xsl:if test="@wp:align">
        <xsl:attribute name="align"><xsl:value-of select="@wp:align"/></xsl:attribute>
      </xsl:if>
      <xsl:attribute name="border"><xsl:value-of select="@wp:border"/></xsl:attribute>
      <xsl:attribute name="hspace"><xsl:value-of select="@wp:hspace"/></xsl:attribute>
      <xsl:attribute name="vspace"><xsl:value-of select="@wp:vspace"/></xsl:attribute>
    </IMG>
    <xsl:if test="@wp:display">
      <BR>
        <xsl:attribute name="clear">all</xsl:attribute>
      </BR>
    </xsl:if>
      <xsl:apply-templates />
  </xsl:template>

  <xsl:template match="*[@wp:type='svg']">
    <xsl:if test="@wp:display">
      <BR>
        <xsl:attribute name="clear">all</xsl:attribute>
      </BR>
    </xsl:if>
      <EMBED>
        <xsl:attribute name="src"><xsl:value-of select="@wp:href"/></xsl:attribute>
        <xsl:attribute name="type">image/svg+xml</xsl:attribute>
        <xsl:attribute name="pluginspage">http://www.adobe.com/svg/viewer/install/</xsl:attribute>

        <xsl:if test="@wp:usemap">
          <xsl:attribute name="usemap"><xsl:value-of select="@wp:usemap"/></xsl:attribute>
        </xsl:if>
        <xsl:if test="@wp:width">
          <xsl:attribute name="width"><xsl:value-of select="@wp:width"/></xsl:attribute>
        </xsl:if>
        <xsl:if test="@wp:height">
          <xsl:attribute name="height"><xsl:value-of select="@wp:height"/></xsl:attribute>
        </xsl:if>
        <xsl:if test="@wp:alt">
          <xsl:attribute name="alt"><xsl:value-of select="@wp:alt"/></xsl:attribute>
        </xsl:if>
        <xsl:if test="@wp:align">
          <xsl:attribute name="align"><xsl:value-of select="@wp:align"/></xsl:attribute>
        </xsl:if>
        <xsl:attribute name="border"><xsl:value-of select="@wp:border"/></xsl:attribute>
        <xsl:attribute name="hspace"><xsl:value-of select="@wp:hspace"/></xsl:attribute>
        <xsl:attribute name="vspace"><xsl:value-of select="@wp:vspace"/></xsl:attribute>
      </EMBED>
    <xsl:if test="@wp:display">
      <BR>
        <xsl:attribute name="clear">all</xsl:attribute>
      </BR>
    </xsl:if>
      <xsl:apply-templates />
  </xsl:template>

  <xsl:template match="wp:Map">
    <MAP>
      <xsl:value-of select="." />
    </MAP>
  </xsl:template>

  <xsl:template match="wp:Area">
    <AREA>
      <xsl:attribute name="href"><xsl:value-of select="@wp:href"/></xsl:attribute>
      <xsl:attribute name="shape"><xsl:value-of select="@wp:shape"/></xsl:attribute>
      <xsl:attribute name="coords"><xsl:value-of select="@wp:coords"/></xsl:attribute>
      <xsl:attribute name="alt"><xsl:value-of select="@wp:alt"/></xsl:attribute>
      <xsl:attribute name="target"><xsl:value-of select="@wp:target"/></xsl:attribute>
    </AREA>
  </xsl:template>

  <xsl:template match="wp:br">
    <BR/>
  </xsl:template>

  <!-- Default Text Rule -->
  <xsl:template match="text()">
     <xsl:value-of select="." />
  </xsl:template>

<!-- Table Rules -->

  <!-- Single Table -->
  <xsl:template match="*[@wp:type='table']">
    <TABLE>
      <xsl:attribute name="border">1</xsl:attribute>
      <xsl:attribute name="cellpadding">5</xsl:attribute>
      <xsl:attribute name="cellspacing">0</xsl:attribute>

      <CAPTION>
        <xsl:apply-templates select="*[@wp:type='tabletitle']" />
      </CAPTION>

      <xsl:apply-templates select="*[@wp:type='tableheading']" />
      <xsl:apply-templates select="*[@wp:type='tablebody']" />
      <xsl:apply-templates select="*[@wp:type='tablefooting']" />
    </TABLE>
    <TABLE>
      <TR><TD>
        <xsl:apply-templates select="TableNotes//*[@wp:type='footnote']" />
      </TD></TR>
    </TABLE>
  </xsl:template>

  <!-- Table Heading Rows -->
  <xsl:template match="*[@wp:type='tablerow_heading']">
    <TR>
      <xsl:attribute name="bgcolor">#CCCCCC</xsl:attribute>
      <xsl:apply-templates />
    </TR>
  </xsl:template>

  <!-- Table Body and Footing Rows -->
  <xsl:template match="*[@wp:type='tablerow_body'] | *[@wp:type='tablerow_footing']">
    <TR>
      <xsl:apply-templates/>
    </TR>
  </xsl:template>

  <!-- Table Body and Footing Cells -->
  <xsl:template match="*[@wp:type='tablecell_body'] | *[@wp:type='tablecell_footing']">
    <TD>
      <xsl:attribute name="colspan"><xsl:value-of select="@wp:colspan"/></xsl:attribute>
      <xsl:attribute name="rowspan"><xsl:value-of select="@wp:rowspan"/></xsl:attribute>
      <xsl:choose>
        <xsl:when test="count(./*[1]) = 1">
          <A>
            <xsl:attribute name="name"><xsl:value-of select="@wp:id"/></xsl:attribute>
          </A>
          <DIV>
            <xsl:if test="@wp:class">
              <xsl:attribute name="class"><xsl:value-of select="@wp:class"/></xsl:attribute>
            </xsl:if>
            <xsl:if test="@wp:style">
              <xsl:attribute name="style"><xsl:value-of select="@wp:style"/></xsl:attribute>
            </xsl:if>
            <xsl:apply-templates/>
          </DIV>
        </xsl:when>

        <xsl:otherwise>
          <BR/>
        </xsl:otherwise>
      </xsl:choose>
    </TD>
  </xsl:template>

  <!-- Table Heading Cells -->
  <xsl:template match="*[@wp:type='tablecell_heading']">
    <TH>
      <xsl:attribute name="colspan"><xsl:value-of select="@wp:colspan"/></xsl:attribute>
      <xsl:attribute name="rowspan"><xsl:value-of select="@wp:rowspan"/></xsl:attribute>
      <xsl:choose>
        <xsl:when test="count(./*[1]) = 1">
          <A>
            <xsl:attribute name="name"><xsl:value-of select="@wp:id"/></xsl:attribute>
          </A>
          <DIV>
            <xsl:if test="@wp:class">
              <xsl:attribute name="class"><xsl:value-of select="@wp:class"/></xsl:attribute>
            </xsl:if>
            <xsl:if test="@wp:style">
              <xsl:attribute name="style"><xsl:value-of select="@wp:style"/></xsl:attribute>
            </xsl:if>
            <xsl:apply-templates/>
          </DIV>
        </xsl:when>

        <xsl:otherwise>
          <BR/>
        </xsl:otherwise>
      </xsl:choose>
    </TH>
  </xsl:template>
</xsl:stylesheet>
