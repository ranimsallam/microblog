/**
 * execute when the words after # in the url changes
 * check if it changes to username get his information and go to his profile page
 * if it topic name get the relevant information and go to topic page of the requested topic name 
 */

$(window).on('hashchange', function(){


var topicCase = window.location.hash.substring(1,7); 
var topicname = window.location.hash.substring(7);
		
	var userToVisit = window.location.hash.substring(1);
	$('#myModal').modal('hide');
	$('#myModal2').modal('hide');
	
		$.ajax({ 
	    	type: "POST",
	        url: "/New/GetSessionServlet",
	        success: function(res, status, xhr) {
	        	
	        	var obj = JSON.parse(res);
	        	logedinUser = obj[0].Username;
	        	
	        	
	        },
	        error: function() {
	       }
	    }).done(function(){
	    	
	    	//if this is true - load information about the requested topic and load the page
	    	if(topicCase == "topic="){
	    		
	    		var newUrl = "/New/Topic.html#topic=" + topicname;
	    		window.location.href = newUrl ;
	    	}
	    	//if true load my information
	    	else if(userToVisit == logedinUser){
	    		
	    			$.ajax({ 
	    		    	type: "POST",
	    		        url: "/New/GetSessionServlet",
	    		        success: function(res, status, xhr) {
	    		        	
	    		        	var obj = JSON.parse(res);
	    		        	var thisUser = obj[0].Username;
	    		        	LogedinPage(obj);
	    			     		
	    			     		angular.element(document.getElementById('myFollowing')).scope().Updatefollow(VisitName);
	    			     		angular.element(document.getElementById('myFollowers')).scope().UpdateFollowers(VisitName);
	    		        },
	    		        error: function() {
	    		        	$('#helloUser').text("Failed");
	    		       }
	    		    });
	    		
	    	}
	    	//load the requested user information to visit his page
	    	else{
	    		
	    		
	    		$.ajax({ 
	    	    	type: "POST",
	    	        url: "/New/VisitUserInfoServlet/username/" + userToVisit,
	    	        success: function(res, status, xhr) {
	    	        	
	    	        	var obj = JSON.parse(res);
	    	        	var thisUser = obj[0].Username;
	    	        	
	    	        	UsersPage(obj);
	    		     		
	    		     		angular.element(document.getElementById('myFollowing')).scope().Updatefollow(VisitName);
	    		     		angular.element(document.getElementById('myFollowers')).scope().UpdateFollowers(VisitName);
	    		     		
	    	        },
	    	        error: function() {
	    	       }
	    	    });
	    	}
	    	
	   //done 	
	    });
});