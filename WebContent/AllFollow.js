/**
 * 
 */

// save in allFollow all the following pairs
var allFollow;

	setInterval(function(){

					$.ajax({
				    	type: "POST",
				        url: "/New/ShowFollowTable",
				        //data: $('#helloUser').serialize(),
				        success: function(res, status, xhr) {
				        	     
				        	var obj = JSON.parse(res);
				        	allFollow = obj;	//alert(allFollow.length);
				        },
				        error: function() {
				        	
				       }
				    });	
					
					
	
			},200);