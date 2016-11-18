<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
	xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/" xmlns:soap12="http://schemas.xmlsoap.org/wsdl/soap12/" xmlns:fn="http://www.w3.org/2005/xpath-functions">
	<xsl:include href="common-messages.xsl"/>
	<xsl:param name="basedir"></xsl:param>
	<xsl:param name="filename"></xsl:param>
	<xsl:param name="voorschriftenVersion">${voorschriften.version}</xsl:param>
	<xsl:param name="toolVersion">${project.version}</xsl:param>
	<xsl:param name="generationDate"></xsl:param>
	<xsl:param name="wsiCompliance"></xsl:param>
	<xsl:param name="wsiComplianceText"></xsl:param>
	<xsl:param name="otherErrorsText"></xsl:param>			
	<xsl:output indent="yes" method="html" />
	<xsl:template match="/">
	<xsl:variable name="up" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'"/>
	<xsl:variable name="lo" select="'abcdefghijklmnopqrstuvwxyz'"/>
		<html>
			<head>
				<link href="{$basedir}css/default.css" rel="stylesheet" type="text/css" />
			</head>
			<body>
				<xsl:for-each select="/wsdl:definitions">
					<h1>
						Voorschriften rapport van de WSDL : <xsl:value-of select="$filename" />
					</h1>
					<div class="version">Voorschriften versie: <xsl:value-of select="$voorschriftenVersion" />, Generator <xsl:value-of select="$toolVersion" />, Generatie datum: <xsl:value-of select="$generationDate" /></div>
					<div class="content">
					<div class="content">
					<table>
						<tr>
							<th>Voorschrift</th>
							<th>OK?</th>
							<th>Inhoud</th>
							<th>Extra toelichting</th>
						</tr>
                                            <xsl:if test="./wsdl:import">
                                                <tr>
                                                    <td>Voor elke webservice is één WSDL contract</td>
                                                    <td><xsl:call-template name="warning"/></td>
                                                    <td>Importeert wsdl: <xsl:value-of select="./wsdl:import/@location" /> met namespace:  <xsl:value-of select="./wsdl:import/@namespace" /></td>
                                                    <td>Voor elke webservice is één WSDL contract. Alleen wanneer de interface in de WSDL (wsdl:portType) wordt geïmplementeerd door meerdere webservices, dan zijn meerdere WSDL bestanden per webservice toegestaan.</td>
                                                </tr>
                                            </xsl:if>
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
								<xsl:when test="fn:matches($filename,'[a-zA-Z]-v\d+\.\d+\.wsdl')">
									<td class="center"><xsl:call-template name="passed"/></td>
								</xsl:when>
								<xsl:otherwise>
									<td class="center"><xsl:call-template name="highpriority"/></td>
								</xsl:otherwise>
							</xsl:choose>
							<td><xsl:value-of select="$filename" /></td>
							<td>Syntax: naam-v{majorversie}.{minorversie}.wsdl</td>
						</tr>
						<tr>
							<td>Voldoet aan WS-I Basic Profile 1.1</td>
							<xsl:choose>
								<xsl:when test="$wsiCompliance">
									<td class="center"><xsl:call-template name="passed"/></td>
								</xsl:when>
								<xsl:otherwise>
									<td class="center"><xsl:call-template name="highpriority"/></td>
								</xsl:otherwise>
							</xsl:choose>							
							<td></td>
							<td></td>
						</tr>			
						<tr>
							<td>Namespace en versionering:</td>
							<xsl:choose>
								<xsl:when test="fn:matches(./@targetNamespace,'http://contracts.denhaag.nl/([a-z0-9-]+/){2,}?v\d+')">
									<td class="center"><xsl:call-template name="passed"/></td>
								</xsl:when>
								<xsl:otherwise>
									<td class="center"><xsl:call-template name="highpriority"/></td>
								</xsl:otherwise>
							</xsl:choose>
							<td>
								<xsl:value-of select="./@targetNamespace" />
							</td>
							<td>Syntax: http://contracts.denhaag.nl/{domein}/{applicatie}/v{majorversie}</td>
						</tr>

						<tr>
							<td>De major en minor versie moet opgenomen worden in de documentatie:</td>
							<xsl:choose>
								<xsl:when test="./wsdl:documentation and fn:matches(./wsdl:documentation,'Contract version \d+\.\d+')">
									<td class="center"><xsl:call-template name="passed"/></td>
									<td>
										<xsl:value-of select="./wsdl:documentation" />
									</td>
								</xsl:when>
								<xsl:otherwise>
									<td class="center"><xsl:call-template name="highpriority"/></td>
									<td></td>
								</xsl:otherwise>
							</xsl:choose>
							<td>Contract version {majorversie}.{minorversie}</td>
						</tr>
						<tr>
							<td>Geen inline XSD types in WSDL:</td>
							<xsl:choose>
								<xsl:when test="./wsdl:types/xsd:schema and not(./wsdl:types/xsd:schema/xsd:import/@schemaLocation)">
									<td class="center"><xsl:call-template name="lowpriority"/></td>
									<td>Zie details verder in rapport</td>
								</xsl:when>
								<xsl:otherwise>
									<td class="center"><xsl:call-template name="passed"/></td>
									<td></td>
								</xsl:otherwise>
							</xsl:choose>
							<td>In de WSDL mogen geen types worden gedefinieerd</td>
						</tr>
						<xsl:if test="./wsdl:types/xsd:schema">
							<tr>
								<td>XSD namespaces:</td>
								<td  class="center">
									<xsl:for-each select="./wsdl:types/xsd:schema">
																
									<xsl:choose>
										<xsl:when test="not (./@targetNamespace) or fn:matches(./@targetNamespace,'http://contracts.denhaag.nl/([a-z0-9]+/){2,}?v\d+')">
											<xsl:call-template name="passed"/>
										</xsl:when>
										<xsl:otherwise>
											<xsl:call-template name="highpriority"/>
										</xsl:otherwise>
									</xsl:choose><br/>
									</xsl:for-each>
								</td>
								<td>
								<xsl:for-each select="./wsdl:types/xsd:schema">
									<xsl:value-of select="./@targetNamespace"/><br/>
								</xsl:for-each>
								</td>
								<td>Syntax: http://contracts.denhaag.nl/{domein}/{applicatie}/v{majorversie}</td>
							</tr>
						</xsl:if>						
						<xsl:if test="not(./wsdl:import/@location)">
						<tr>
							<td>Imports naar XSD's aanwezig:</td>
							<xsl:choose>
								<xsl:when test="./wsdl:types/xsd:schema/xsd:import/@schemaLocation">
									<td  class="center"><xsl:call-template name="passed"/></td>
									<td>
										<xsl:for-each select="./wsdl:types/xsd:schema/xsd:import">
											<xsl:value-of select="./@schemaLocation" />
											<xsl:text> - </xsl:text>
											<xsl:value-of select="./@namespace" />
											<br />
										</xsl:for-each>
									</td>
								</xsl:when>
								<xsl:otherwise>
									<td class="center"><xsl:call-template name="lowpriority"/></td>
									<td>Er worden geen externe XSD's geïmporteerd</td>
								</xsl:otherwise>
							</xsl:choose>
						</tr>

						<xsl:if test="./wsdl:types/xsd:schema/xsd:import/@schemaLocation">
							<xsl:variable name="targetNamespace" select="./@targetNamespace"/>
							<xsl:choose>
								<xsl:when test="./wsdl:types/xsd:schema/xsd:import[@namespace=$targetNamespace]">
									<xsl:variable name="messageXsdFilename" select="./wsdl:types/xsd:schema/xsd:import[@namespace=$targetNamespace]/@schemaLocation"/>
									<tr>
										<td>Berichten XSD import aanwezig:</td>
										<td class="center"><xsl:call-template name="passed"/></td>
										<td><xsl:value-of select="$messageXsdFilename" /></td>
										<td></td>
									</tr>	
									<tr>
										<td>Berichten XSD bestandsnaam voldoet aan conventie:</td>
										<xsl:choose>
											<xsl:when test="fn:matches($messageXsdFilename,'(.[/\\])*[a-z0-9-]-messages-v\d+\.\d+\.xsd')">
												<td  class="center"><xsl:call-template name="passed"/></td>
												<td></td>
											</xsl:when>
											<xsl:otherwise>
												<td  class="center"><xsl:call-template name="highpriority"/></td>
												<td><xsl:value-of select="$messageXsdFilename" /></td>
											</xsl:otherwise>
										</xsl:choose>					
										<td>Syntax: naam-messages-v{majorversie}.{minorversie}.xsd</td>
									</tr>
									<tr>
										<td>Alleen berichten XSD aanwezig:</td>	
										<td class="center">
										<xsl:for-each select="./wsdl:types/xsd:schema/xsd:import[@namespace!=$targetNamespace]"> 
											<xsl:choose>
												<xsl:when  test="fn:starts-with(./@namespace, 'http://messages.denhaag.nl')">
													<xsl:call-template name="passed"/><br/>
												</xsl:when>
												<xsl:otherwise>
													<xsl:call-template name="lowpriority"/>
												</xsl:otherwise>
											</xsl:choose>
										</xsl:for-each>
										</td>	
										<td>
											<xsl:for-each select="./wsdl:types/xsd:schema/xsd:import[@namespace!=$targetNamespace]"> 
												<xsl:value-of select="./@schemaLocation" />
												<xsl:text> - </xsl:text>
												<xsl:value-of select="./@namespace" />
												<br />
											</xsl:for-each>
										</td>
										<td>Er mogen alleen berichten XSD geïmporteerd worden.</td>						
									</tr>																		
								</xsl:when>
								<xsl:otherwise>
									<tr>
										<td>Berichten XSD import aanwezig:</td>								
										<td class="center"><xsl:call-template name="lowpriority"/></td>
										<td>Er wordt geen berichten XSD geïmporteerd. </td>
										<td></td>
									</tr>
									<tr>
										<td>Alleen berichten XSD aanwezig:</td>		
										<td  class="center"><xsl:call-template name="lowpriority"/></td>
										<td></td>
										<td>Er mogen alleen berichten XSD geïmporteerd worden.</td>						
									</tr>									
								</xsl:otherwise>
							</xsl:choose>
						</xsl:if>	
						</xsl:if>			
						<tr>
							<td>De style van de WSDL moet 'document' zijn.</td>
							<xsl:choose>
								<xsl:when test="./wsdl:binding/soap:binding[@style='document']">
									<td class="center"><xsl:call-template name="passed"/></td>
								</xsl:when>
								<xsl:otherwise>
									<xsl:variable name="notDocumentStyle">
										<xsl:for-each select="./wsdl:binding/wsdl:operation/soap:operation">
											<xsl:if test="not(./@style) or not (./@style = 'document')">true</xsl:if>
									
										</xsl:for-each>
									</xsl:variable>
									<xsl:choose>
										<xsl:when test="fn:starts-with($notDocumentStyle,'true')">
											<td  class="center"><xsl:call-template name="highpriority"/></td>
										</xsl:when>
										<xsl:otherwise><td  class="center"><xsl:call-template name="passed"/></td></xsl:otherwise>
									</xsl:choose>
									
								</xsl:otherwise>
							</xsl:choose>
							<td></td>
							<td></td>
						</tr>

					</table>
					<xsl:if test="not($wsiCompliance)">
						<h3>Niet WS-I Compliance</h3>
						<table><xsl:value-of select="$wsiComplianceText" disable-output-escaping="yes"/></table>
					</xsl:if>
					<xsl:if test="fn:string-length($otherErrorsText) > 0">
						<h3>Andere kritieke fouten</h3>
						<table><xsl:value-of select="$otherErrorsText" disable-output-escaping="yes"/></table>					
					</xsl:if>
					<xsl:for-each select="./wsdl:portType">
						<h3>Operaties van portType: <xsl:value-of select="./@name" /></h3>
						<table>
							<tr>
								<th>Operatie</th>
								<th>Naamgeving(1)</th>
								<th>Input/Output/Fault</th>
								<th>wsdl:part (2/3)</th>
                                <th>Beschrijving (4)</th>
							</tr>
							
							<xsl:for-each select="./wsdl:operation">
								<xsl:variable name="operationName" select="./@name" />
								<xsl:variable name="lowerCaseOperationName" select="fn:concat(fn:lower-case(fn:substring(./@name,1,1)), fn:substring(./@name,2))" />
								<tr>
									<td>
										<xsl:value-of select="./@name" />
									</td>
									<xsl:choose>
										<xsl:when test="$operationName = $lowerCaseOperationName">
											<td class="center"><xsl:call-template name="passedWithParams"><xsl:with-param name="specificMessage"><xsl:text>De operatie moet beginnen met een kleine letter en is lowerCamelCase.</xsl:text></xsl:with-param></xsl:call-template></td>
										</xsl:when>
										<xsl:otherwise>
											<!-- td class="center"><xsl:call-template name="lowpriorityWithParams"><xsl:with-param name="specificMessage"><xsl:text>De operatie moet beginnen met een kleine letter en is lowerCamelCase.</xsl:text></xsl:with-param></xsl:call-template></td-->
											<td class="center"><xsl:call-template name="warningWithParams"><xsl:with-param name="specificMessage"><xsl:text>De operatie moet beginnen met een kleine letter en is lowerCamelCase.</xsl:text></xsl:with-param></xsl:call-template></td> 
										</xsl:otherwise>
									</xsl:choose>	
									 <td>
										<xsl:value-of select="./wsdl:input/@message" />
										<xsl:if test="./wsdl:output"><br/>
										<xsl:value-of select="./wsdl:output/@message" /><br/>
										<xsl:value-of select="./wsdl:fault/@message" />
										</xsl:if>
									</td>
									<td class="center">	
										<xsl:apply-templates select="./wsdl:input"  mode="parameters"/>/<xsl:apply-templates select="./wsdl:input"  mode="name"/>
										<xsl:if test="./wsdl:output"><br/>
											<xsl:apply-templates select="./wsdl:output" mode="parameters"/>/<xsl:apply-templates select="./wsdl:output" mode="name"/><br/>
											<xsl:apply-templates select="./wsdl:fault" mode="parameters" /><xsl:if test="./wsdl:fault">/<xsl:call-template name="passed"/></xsl:if>
										</xsl:if>
									</td>
                                    <td>
                                        <xsl:choose>
										<xsl:when test="fn:string-length(./wsdl:documentation[1]/text()[1]) > 0">
											<xsl:call-template name="passedWithParams"><xsl:with-param name="specificMessage"><xsl:text>Is er wsdl:documentation aanwezig per operatie?</xsl:text></xsl:with-param></xsl:call-template>: <xsl:value-of select="./wsdl:documentation[1]" />
										</xsl:when>
										<xsl:otherwise>
											<xsl:call-template name="warningWithParams"><xsl:with-param name="specificMessage"><xsl:text>Is er wsdl:documentation aanwezig per operatie?</xsl:text></xsl:with-param></xsl:call-template>
										</xsl:otherwise>
									</xsl:choose></td>
								</tr>							
							</xsl:for-each>
						</table>
						<ol>
							<li>De operatie moet beginnen met een kleine letter en is lowerCamelCase.</li>
							<li>De 'name' van de 'wsdl:part' moet de waarde 'parameters' hebben.</li>
							<li>De naam van het requestbericht moet gelijk zijn aan de naam van de operatie. De naam van het responsebericht moet gelijkt zijn aan de name van de operatie plus 'Response'.</li>
                            <li>Is er wsdl:documentation aanwezig per operatie?</li>
						</ol>
					</xsl:for-each>
					<xsl:for-each select="./wsdl:binding">

						<h3>Operaties van binding: <xsl:value-of select="./@name" /></h3>
						<span class="relation">Heeft relatie met portType: <xsl:value-of select="substring-after(./@type,':')" /></span>
						<table>
							<tr>
								<th>Operatie</th>
								<th>Naamgeving(1)</th>
								<th>Literal</th>
								<th>Type</th>
								<th>SOAP fault(2)</th>
								<th>SOAP request header</th>
								<th>SOAP response header</th>
							</tr>
							<xsl:for-each select="./wsdl:operation">
								<xsl:variable name="operationName" select="./@name" />
								<xsl:variable name="lowerCaseOperationName" select="fn:concat(fn:lower-case(fn:substring(./@name,1,1)), fn:substring(./@name,2))" />
								<tr>
									<td>
										<xsl:value-of select="./@name" />
									</td>
									<xsl:choose>
										<xsl:when test="$operationName = $lowerCaseOperationName">
											<td class="center"><xsl:call-template name="passedWithParams"><xsl:with-param name="specificMessage"><xsl:text>De operatie moet beginnen met een kleine letter en is lowerCamelCase.</xsl:text></xsl:with-param></xsl:call-template></td>
										</xsl:when>
										<xsl:otherwise>
											<!--td class="center"><xsl:call-template name="lowpriorityWithParams"><xsl:with-param name="specificMessage"><xsl:text>De operatie moet beginnen met een kleine letter en is lowerCamelCase.</xsl:text></xsl:with-param></xsl:call-template></td-->
											<td class="center"><xsl:call-template name="warningWithParams"><xsl:with-param name="specificMessage"><xsl:text>De operatie moet beginnen met een kleine letter en is lowerCamelCase.</xsl:text></xsl:with-param></xsl:call-template></td> 
										</xsl:otherwise>
									</xsl:choose>										
									<xsl:choose>
										<xsl:when test="(./wsdl:input/soap:body[@use='literal'] or ./wsdl:input/soap12:body[@use='literal']) and  ( not(./wsdl:output) or ./wsdl:output/soap:body[@use='literal'] or ./wsdl:output/soap12:body[@use='literal']) and ( not(./wsdl:fault) or ./wsdl:fault/soap:fault[@use='literal']or ./wsdl:fault/soap12:fault[@use='literal'])">
											<td class="center"><xsl:call-template name="passed"/></td>
										</xsl:when>
										<xsl:otherwise>
											<td class="center"><xsl:call-template name="highpriority"/></td> 
										</xsl:otherwise>
									</xsl:choose>						
									<td>
										<xsl:if test="./wsdl:input">request</xsl:if><xsl:if test="./wsdl:output">/response</xsl:if>
									</td>
										<xsl:choose>
											<xsl:when test="./wsdl:input and ./wsdl:output">
												<xsl:choose>
													<xsl:when test="./wsdl:fault">
														<td class="center"><xsl:call-template name="passedWithParams"><xsl:with-param name="specificMessage"><xsl:text>Alle operaties (met request/response) moeten gebruik maken van een foutbericht.</xsl:text></xsl:with-param></xsl:call-template></td>
													</xsl:when>
													<xsl:otherwise>
														<td class="center"><xsl:call-template name="lowpriorityWithParams"><xsl:with-param name="specificMessage"><xsl:text>Alle operaties (met request/response) moeten gebruik maken van een foutbericht.</xsl:text></xsl:with-param></xsl:call-template></td>
													</xsl:otherwise>
												</xsl:choose>
											</xsl:when>
											<xsl:otherwise><td class="center"><xsl:call-template name="passed"/></td></xsl:otherwise>
										</xsl:choose>	
									<td>
										<xsl:if test="./wsdl:input/soap:header">
										 	<xsl:value-of select="./wsdl:input/soap:header/@message" />(<xsl:value-of select="./wsdl:input/soap:header/@part" />)
										</xsl:if>
									</td>	
									<td>
										<xsl:if test="./wsdl:output/soap:header">
										 	<xsl:value-of select="./wsdl:output/soap:header/@message" />(<xsl:value-of select="./wsdl:output/soap:header/@part" />)
										</xsl:if>
									</td>															
								</tr>
							</xsl:for-each>
	
						</table>
						<ol>
							<li>De operatie moet beginnen met een kleine letter en is lowerCamelCase.</li>
							<li>Alle operaties (met request/response) moeten gebruik maken van een foutbericht.</li>
						</ol>						
					</xsl:for-each>
					<xsl:for-each select="./wsdl:service">
						<h3>Endpoints of service: <xsl:value-of select="./@name" /></h3>
                                                WSDL documenatie voor de service: <xsl:choose>
										<xsl:when test="fn:string-length(./wsdl:documentation/text()) > 0">
											<xsl:call-template name="passedWithParams"><xsl:with-param name="specificMessage"><xsl:text>Is er wsdl:documentation aanwezig per service?</xsl:text></xsl:with-param></xsl:call-template>: <xsl:value-of select="./wsdl:documentation" />
										</xsl:when>
										<xsl:otherwise>
											<xsl:call-template name="warningWithParams"><xsl:with-param name="specificMessage"><xsl:text>Is er wsdl:documentation aanwezig per service?</xsl:text></xsl:with-param></xsl:call-template>
										</xsl:otherwise>
									</xsl:choose>
						<table>
							<tr>
								<th>Port naam</th>
								<th>Relatie met binding</th>
								<th>Endpoint</th>
								<th>Type</th>
							</tr>
							<xsl:for-each select="./wsdl:port">
							<tr>
								<td><xsl:value-of select="./@name" /></td>
								<td><xsl:value-of select="substring-after(./@binding,':')" /></td>
								<td><xsl:value-of select="./soap:address/@location" /><xsl:value-of select="./soap12:address/@location" /></td>
								<td><xsl:if test="./soap:address/@location">SOAP 1.1</xsl:if><xsl:if test="./soap12:address/@location">SOAP 1.2</xsl:if></td>
							</tr>
							</xsl:for-each>
						</table>
					</xsl:for-each>
					</div>
					<xsl:if test="./wsdl:types/xsd:schema and not(./wsdl:types/xsd:schema/xsd:import/@schemaLocation)">
					<h2>Inline XSD types</h2>
					<div class="content">
					<xsl:for-each select="./wsdl:types/xsd:schema">
						<h3>Namespace <xsl:value-of select="./@targetNamespace" /></h3>
						<div class="content">
							<xsl:call-template name="xsdSummary"/>
						</div>
					</xsl:for-each>
					</div>
					</xsl:if>
					</div>
				</xsl:for-each>
				
			</body>
		</html>
	</xsl:template>
	<xsl:template match="wsdl:fault" mode="parameters">
		<xsl:variable name="inputMessage" select="substring-after(./@message,':')" />								
		<xsl:for-each select="parent::node()/parent::node()/parent::node()/wsdl:message[@name=$inputMessage]">
			<xsl:choose>
				<xsl:when test="count(./wsdl:part) &gt; 1">
					<xsl:call-template name="lowpriorityWithParams"><xsl:with-param name="specificMessage"><xsl:text>Meer dan twee 'wsdl:part'</xsl:text></xsl:with-param></xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="passed"/>
				</xsl:otherwise>
			</xsl:choose>			

		</xsl:for-each>
	</xsl:template>	
	<xsl:template match="*" mode="parameters">
		<xsl:variable name="inputMessage" select="substring-after(./@message,':')" />								
		<xsl:for-each select="parent::node()/parent::node()/parent::node()/wsdl:message[@name=$inputMessage]">
			<xsl:choose>
				<xsl:when test="count(./wsdl:part) &gt; 1">
					<xsl:call-template name="lowpriorityWithParams"><xsl:with-param name="specificMessage"><xsl:text>Meer dan twee 'wsdl:part'</xsl:text></xsl:with-param></xsl:call-template>
				</xsl:when>
 				<xsl:when test="./wsdl:part[@name='parameters'] or ./wsdl:part[@name='header']">
					<xsl:call-template name="passedWithParams"><xsl:with-param name="specificMessage"><xsl:text>De 'name' van de 'wsdl:part' moet de waarde 'parameters' of 'header' hebben.</xsl:text></xsl:with-param></xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="lowpriorityWithParams"><xsl:with-param name="specificMessage"><xsl:text>De 'name' van de 'wsdl:part' moet de waarde 'parameters' of 'header' hebben.</xsl:text></xsl:with-param></xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>			

		</xsl:for-each>
	</xsl:template>
	<xsl:template match="wsdl:input" mode="name">
		<xsl:variable name="inputMessage" select="substring-after(./@message,':')" />	
		<xsl:variable name="operationName" select="parent::node()/@name" />	
		<xsl:for-each select="parent::node()/parent::node()/parent::node()/wsdl:message[@name=$inputMessage]">
			<xsl:choose>
				<xsl:when test="count(./wsdl:part) &gt; 1">		
						<xsl:call-template name="lowpriorityWithParams"><xsl:with-param name="specificMessage"><xsl:text>De naam van het requestbericht moet gelijk zijn aan de naam van de operatie.</xsl:text></xsl:with-param></xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:variable name="partElement" select="substring-after(./wsdl:part/@element,':')" />
							
							<xsl:choose>
								<xsl:when test="$partElement = $operationName">
									<xsl:call-template name="passedWithParams"><xsl:with-param name="specificMessage"><xsl:text>De naam van het requestbericht moet gelijk zijn aan de naam van de operatie.</xsl:text></xsl:with-param></xsl:call-template>
								</xsl:when>
								<xsl:otherwise>
									<xsl:call-template name="lowpriorityWithParams"><xsl:with-param name="specificMessage"><xsl:text>De naam van het requestbericht moet gelijk zijn aan de naam van de operatie.</xsl:text></xsl:with-param></xsl:call-template>
								</xsl:otherwise>
							</xsl:choose>	
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>	
	</xsl:template>	
	<xsl:template match="wsdl:output" mode="name">
		<xsl:variable name="inputMessage" select="substring-after(./@message,':')" />	
		<xsl:variable name="operationName" select="parent::node()/@name" />	
		<xsl:variable name="operationNameWithResponse" select="fn:concat($operationName,'Response')" />	
				
		<xsl:for-each select="parent::node()/parent::node()/parent::node()/wsdl:message[@name=$inputMessage]">
			<xsl:choose>
				<xsl:when test="count(./wsdl:part) &gt; 1">		
						<xsl:call-template name="lowpriorityWithParams"><xsl:with-param name="specificMessage"><xsl:text>De naam van het responsebericht moet gelijkt zijn aan de name van de operatie plus 'Response'.</xsl:text></xsl:with-param></xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:variable name="partElement" select="substring-after(./wsdl:part/@element,':')" />
			
					<xsl:choose>
						<xsl:when test="$partElement = $operationNameWithResponse">
							<xsl:call-template name="passedWithParams"><xsl:with-param name="specificMessage"><xsl:text>De naam van het responsebericht moet gelijkt zijn aan de name van de operatie plus 'Response'. </xsl:text></xsl:with-param></xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="lowpriorityWithParams"><xsl:with-param name="specificMessage"><xsl:text>De naam van het responsebericht moet gelijkt zijn aan de name van de operatie plus 'Response'. </xsl:text></xsl:with-param></xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>			
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>	
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
	<h3>Globale element definities</h3>
	<table class="element">
		<tr><th></th><th>Naam</th><th>Type</th></tr>
		<xsl:apply-templates select="./xsd:element" mode="global-messages"/>
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