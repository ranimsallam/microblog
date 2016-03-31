/**
 * 
 */
//save in allUsersIn all the users to check if the @nickname are legal before display messages

var allUsersIn;

$(window).load(function(){
	$.ajax({
    	type: "POST",
        url: "/New/GetAllUsersServlet",
        data: $('#helloUser').serialize(),
        success: function(res, status, xhr) {
        	     	
        	var obj = JSON.parse(res);
        	allUsersIn = obj;
        },
        error: function() {
        	
       }
    }).done(function(data){
    	var obj = JSON.parse(data);
    	allUsersIn = obj;
    })
	
});
