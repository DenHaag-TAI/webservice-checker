<%@ page language="java" contentType="text/html;  charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib  prefix="tw" uri="http://www.denhaag.nl/tw" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="css/default.css" rel="stylesheet" type="text/css" />
<title>${initParam.title} ${initParam.version}</title>
</head>
<body>

	<div  id="wrapper" >
	<div id="form" class="block">
		<h2>Check je webservice:</h2>
		<form action="check" method="POST" enctype="multipart/form-data">
			<table>
				<tr>
					<th>Upload zip, wsdl of xsd bestand:</th>
					<td><input name="uploadFile" type="file" size="50"></td>
				</tr>
				<tr>
					<th>Negeer namespaces die beginnen met:</th>
					<td><tw:excludeNamespaces/>				
					</td>
				</tr>
				<tr>
					<td colspan="2"><input type="submit" value="Uploaden"></td>
				</tr>
			</table>



		</form>	
	</div>
	<div id="info" class="block">
		<h2>Info:</h2>
		<div class="blockdiv">${initParam.title}: ${initParam.version}</div>
		<div class="blockdiv">Voorschriften versie: ${initParam.voorschriftenVersion}</div>
		<div id="description" class="blockdiv">${initParam.description}</div>
		
		<div id="created">
			Gemaakt door: <br/> <a href="${initParam.organizationUrl}">${initParam.organization}</a>
		</div>
		<div id="created">
			Zie <a href="changes.html">Wijzigingen</a>
		</div>
	</div>
	</div>


</body>
</html>