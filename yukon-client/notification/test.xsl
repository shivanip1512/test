<?xml version="1.0"?>
<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/">

<html>
<body>
Should be three: <xsl:value-of select="//three"/>
</body>
</html>

</xsl:template>

</xsl:transform>