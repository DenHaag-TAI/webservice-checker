<%@ page language="java" contentType="text/html;  charset=utf-8" pageEncoding="utf-8"%>
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
		<h2>ERROR: ${errorMessage}</h2>
		<a href="${pageContext.request.contextPath}">Probeer op nieuw </a>
	</div>
	</div>


</body>
</html>