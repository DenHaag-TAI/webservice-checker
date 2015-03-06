<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
	xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/" xmlns:fn="http://www.w3.org/2005/xpath-functions">
	<xsl:include href="common.xsl"/>
	<xsl:param name="basedir"></xsl:param>
	<xsl:param name="filename"></xsl:param>
	<xsl:param name="voorschriftenVersion">${voorschriften.version}</xsl:param>
	<xsl:param name="toolVersion">${project.version}</xsl:param>
	<xsl:param name="generationDate"></xsl:param>	
	<xsl:output indent="yes" method="html" />
	<xsl:template match="/">
	<xsl:variable name="up" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'"/>
	<xsl:variable name="lo" select="'abcdefghijklmnopqrstuvwxyz'"/>
		<html>
			<head>
				<link href="{$basedir}css/default.css" rel="stylesheet" type="text/css" />
			</head>
			<body>
				<xsl:for-each select="/xsd:schema">
					<h1>
						Voorschriften rapport van het type XSD : <xsl:value-of select="$filename" />
					</h1>
					<div class="version">Voorschriften versie: <xsl:value-of select="$voorschriftenVersion" />, Generator <xsl:value-of select="$toolVersion" />, Generatie datum: <xsl:value-of select="$generationDate" /></div>
					<div class="content">					
					<table>
						<tr>
							<th>Voorschrift</th>
							<th>OK?</th>
							<th>Inhoud</th>
							<th>Extra toelichting</th>
						</tr>
						<tr>
							<td>De bestandsnaam mag geen spaties bevatten.</td>
							<xsl:choose>
								<xsl:when test="contains($filename,' ')">
									<td class="center"><xsl:call-template name="highpriority"/></td>
								</xsl:when>
								<xsl:otherwise>
									<td class="center"><xsl:call-template name="passed"/></td>
								</xsl:otherwise>
							</xsl:choose>
							<td><xsl:value-of select="$filename" /></td>
							<td></td>
						</tr>		
						<tr>
							<td>De bestandsnaam moet lowercase zijn.</td>
							<xsl:variable name="lowercaseFilename" select="fn:lower-case($filename)"/>
							<xsl:choose>
								<xsl:when test="$lowercaseFilename = $filename">
									<td class="center"><xsl:call-template name="passed"/></td>
								</xsl:when>
								<xsl:otherwise>
									<td class="center"><xsl:call-template name="lowpriority"/></td>
								</xsl:otherwise>
							</xsl:choose>
							<td><xsl:value-of select="$filename" /></td>
							<td></td>
						</tr>											
						<tr>
							<td>De bestandsnaam eindigt met het versienummer</td>
							<xsl:choose>
								<xsl:when test="fn:matches($filename,'[a-zA-Z0-9]+-types-v\d+\.\d+\.xsd')">
									<td class="center"><xsl:call-template name="passed"/></td>
								</xsl:when>
								<xsl:otherwise>
									<td class="center"><xsl:call-template name="highpriority"/></td>
								</xsl:otherwise>
							</xsl:choose>
							<td><xsl:value-of select="$filename" /></td>
							<td>Syntax: naam-types-v{majorversie}.{minorversie}.xsd</td>
						</tr>
						<tr>
							<td>Namespace en versionering:</td>
							<xsl:choose>
								<xsl:when test="fn:matches(./@targetNamespace,'http://schemas.denhaag.nl/([a-z0-9-]+/){1,}?v\d+')">
									<td class="center"><xsl:call-template name="passed"/></td>
								</xsl:when>
								<xsl:otherwise>
									<td class="center"><xsl:call-template name="highpriority"/></td>
								</xsl:otherwise>
							</xsl:choose>
							<td>
								<xsl:value-of select="./@targetNamespace" />
							</td>
							<td>http://schemas.denhaag.nl/{domein}/{applicatie}/v{majorversie}<br/>http://schemas.denhaag.nl/{domein}/v{majorversie}</td>
						</tr>
						<tr>
							<td>De major en minor versie moet opgenomen worden xsd</td>
							<xsl:choose>
								<xsl:when test="./@version and fn:matches(./@version,'\d+\.\d+')">
									<td class="center"><xsl:call-template name="passed"/></td>
									<td>
										<xsl:value-of select="./@version" />
									</td>
								</xsl:when>
								<xsl:otherwise>
									<td class="center"><xsl:call-template name="highpriority"/></td>
									<td><xsl:value-of select="./@version" /></td>
								</xsl:otherwise>
							</xsl:choose>
							<td> &lt;schema version="{majorversie}.{minorversie}"&gt;</td>
						</tr>
					</table>
					<xsl:call-template name="xsdSummary"/>
					</div>
					
				</xsl:for-each>
				
			</body>
		</html>
	</xsl:template>
	<xsl:template name="xsdSummary" >
	<xsl:if test="./xsd:import">
	<h3>XML Schema imports</h3>
	<ul>
		<xsl:apply-templates select="./xsd:import" mode="global"/>
	</ul>
	</xsl:if>
	<xsl:if test="./xsd:include">
	<h3>XML Schema includes</h3>
	<ul>
		<xsl:apply-templates select="./xsd:include" mode="global"/>
	</ul>
	</xsl:if>
	<xsl:if test="./xsd:redefine">
	<h3>XML Schema redefines<xsl:call-template name="badsupport"/></h3>
	<ul>
		<xsl:apply-templates select="./xsd:redefine" mode="global"/>
	</ul>
	</xsl:if>
	<xsl:if test="./xsd:complexType">
	<h3>Complexe types</h3>
	<ul>
		<xsl:apply-templates select="./xsd:complexType" mode="global"/>
	</ul>
	</xsl:if>
	<xsl:if test="./xsd:simpleType">
	<h3>Simple types</h3>
	<table class="simpleType">
		<xsl:apply-templates select="./xsd:simpleType" mode="global"/>
	</table>
	</xsl:if>
	<xsl:if test="./xsd:element">
	<h3>Globale element definities<xsl:call-template name="lowpriorityWithParams"><xsl:with-param name="specificMessage"><xsl:text>Maak geen gebruik van globale element.</xsl:text></xsl:with-param></xsl:call-template></h3>
	<table class="element">
		<tr><th></th><th>Naam</th><th>Type</th></tr>
		<xsl:apply-templates select="./xsd:element" mode="global"/>
	</table>
	</xsl:if>
	<xsl:if test="./xsd:attribute or ./xsd:attributeGroup or ./xsd:group">
	<h3>Overige definities<xsl:call-template name="badsupport"/></h3>
	<ul>
		<xsl:apply-templates select="child::node()" mode="notallowedglobal"/>
	</ul>
	</xsl:if>
	</xsl:template>








</xsl:stylesheet>