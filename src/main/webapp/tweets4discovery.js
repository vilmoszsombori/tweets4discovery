function doQuery() {
	$.ajax({
		url : 'json',
		data : {
			query : $("#queryString").val(),
			since : $("#since").val(),
			until : $("#until").val()
		},			
		type : "GET",
		async : false,
		success : function(response) {
			if ( typeof response.tweets !== 'undefined' ) {
				$("#resultsTable > tbody").html("");					
				for ( var i in response.tweets) {
					try {
						$("#resultsTable > tbody")
						.append(
								'<tr id="' + 		response.tweets[i].id + '">' + 
									'<td><b>@' +	response.tweets[i].user.screenName + '</b></td>' + 
									'<td>' +		response.tweets[i].user.name + '</td>' +
									'<td>' +		response.tweets[i].text + '</td>' + 
									'<td>' +		response.tweets[i].createdAt + '</td>' +
								'</tr>');
					} catch(err) {
						console.error(err.message);
					}
				}
			} else if ( typeof response.exception !== 'undefined' ) {
				console.error(response.exception);				
			}			
		},
		error: function() {
			console.error('AJAX error');
		}			
	});		
}