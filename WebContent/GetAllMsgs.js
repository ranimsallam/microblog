/**
 * 
 */
//save in allMsgs the messages 
var allMsgs;
setInterval(function(){
				$.ajax({
			    	type: "POST",
			        url: "/New/GetAllMsgsServlet",
			        data: $('#helloUser').serialize(),
			        success: function(res, status, xhr) {
			        	     	
			        	var obj = JSON.parse(res);
			        	allMsgs = obj;
			        	
		
			        },
			        error: function() {
			        	
			       }
			    });	
			
		},1000);	