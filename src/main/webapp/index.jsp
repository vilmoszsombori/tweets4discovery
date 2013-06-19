<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="shortcut icon" type="image/x-icon" href="favicon.ico" />
<link href="tweets4discovery.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />
<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<!-- script src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.1/jquery.min.js"></script -->
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
<script type="text/javascript" src="tweets4discovery.js"></script>
<script>
	$(function() {
		$( "#since" ).datepicker({ minDate: -20, maxDate: 0, dateFormat: "yy-mm-dd"});
		$( "#until" ).datepicker({ minDate: -1, maxDate: 0, dateFormat: "yy-mm-dd"});

		$( '#since' ).change(function() { 
			$( "#until" ).datepicker('option', 'minDate', $( '#since' ).val());
		});		
			
		// initialize the date range
		var day = new Date();
		$( "#until" ).datepicker("setDate", day);
		day.setDate(day.getDate() - 1);
		$( "#since" ).datepicker("setDate", day);

		$( '#queryString' ).change(function() {
			if ( $( '#queryString' ).val() == null || $( '#queryString' ).val().trim() == "" ) {
				$( "#getJsonButton" ).attr('disabled','disabled');
				$( "#getDocxButton" ).attr('disabled','disabled');
			} else {
				$( "#getJsonButton" ).removeAttr('disabled');
				$( "#getDocxButton" ).removeAttr('disabled');
			}
		});
		
	});
</script>
<title>Twitter 4 Discovery</title>
</head>
<body>
	<div id="queryDiv">
		<!-- h2>Query</h2 -->
		<!-- <form name="form_sessions" action="json" method="GET"> -->
		<p><img alt="Discovery Channel" src="discovery.png"/></p>
		<p>
			Twitter query: 	<input type="text" name="query" id="queryString" value="@discovery"/>
			Since: 			<input type="text" id="since" size="12" />
			Until: 			<input type="text" id="until" size="12" />			
			<button id="getJsonButton" onclick="getJson();">HTML results</button>
			<button id="getDocxButton" onclick="getDocx();">DOCX results</button>
			<!-- <input type="submit" name="submit" id="submitButton" value="JSON results" accept="" /> -->
		</p>
		<hr size="1" />
	</div>
	<div id="resultsDiv">
		<h2>Results</h2>
		<table id="resultsTable"><tbody></tbody></table>
		<a id="docxLink"></a>
		<hr size="1" />
	</div>		
</html>