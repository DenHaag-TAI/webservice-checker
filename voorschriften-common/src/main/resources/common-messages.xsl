<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
	xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/" xmlns:fn="http://www.w3.org/2005/xpath-functions">
	<xsl:include href="common.xsl"/>
	<xsl:template match="xsd:element"  mode="global-messages">
		<tr><td>
			<xsl:variable name="lowerCamelCase" select="fn:concat(fn:lower-case(fn:substring(./@name,1,1)), fn:substring(./@name,2))" />
				<xsl:choose>
				<xsl:when test="$lowerCamelCase = ./@name">
					<xsl:call-template name="passedWithParams"><xsl:with-param name="specificMessage"><xsl:text>De naam een element moet beginnen met een kleine letter en is lowerCamelCase.</xsl:text></xsl:with-param></xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="lowpriorityWithParams"><xsl:with-param name="specificMessage"><xsl:text>De naam een element moet beginnen met een kleine letter en is lowerCamelCase.</xsl:text></xsl:with-param></xsl:call-template>
				</xsl:otherwise>
			</xsl:choose></td>
				<xsl:choose>
				<xsl:when test="./@type">
					<td><xsl:call-template name="passedWithParams"><xsl:with-param name="specificMessage"><xsl:text>Bij voorkeur geen gebruik van anonieme types.</xsl:text></xsl:with-param></xsl:call-template><xsl:value-of select="./@name" /></td><td><xsl:value-of select="./@type" /></td>
				</xsl:when>
				<xsl:when test="./@ref">
					<td><xsl:call-template name="warningWithParams"><xsl:with-param name="specificMessage"><xsl:text>Bij voorkeur geen gebruik van anonieme types.</xsl:text></xsl:with-param></xsl:call-template><xsl:value-of select="./@name" /></td><td><xsl:text> ref: </xsl:text><xsl:value-of select="./@ref" /></td>
				</xsl:when>
				<xsl:otherwise>
					<td><xsl:value-of select="./@name" /></td><td><xsl:apply-templates select="child::node()" mode="global-messages-child"/></td>			
				</xsl:otherwise>
				</xsl:choose>
				
	</tr>
	</xsl:template>
	<xsl:template match="*" mode="global-messages-child">
		<xsl:apply-templates select="." mode="local"/>
	</xsl:template>

	<xsl:template match="xsd:complexType" mode="global-messages-child">
		<table class="complexType">
			<tr><th colspan="4"><xsl:call-template name="passedWithParams"><xsl:with-param name="specificMessage"><xsl:text>Maak geen gebruik van lokale complexe types.</xsl:text></xsl:with-param></xsl:call-template>
			<xsl:if test="./@mixed='true'"><xsl:call-template name="highpriorityWithParams"><xsl:with-param name="specificMessage"><xsl:text>Het gebruik van mixed content is niet toegestaan.</xsl:text></xsl:with-param></xsl:call-template></xsl:if>
			Local Complex Type</th>
			</tr>
			<xsl:apply-templates select="child::node()" mode="insideComplexType"/>
			
		</table>
	</xsl:template> 
</xsl:stylesheet>