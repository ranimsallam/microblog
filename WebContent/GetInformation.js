/**
 * execute when loading the page
 * check if the page is discover -> load the relevant information to display the discover page
 * if the page is topic -> load the information to display topic page by the requested topic name
 * if the page is profile check if it is the loged-in user and load its information
 * if its another user profile page load his information to display his page
 */
	var username ;
	var nickname;
	var desc;
	var following;
	var followers;
	var photo ;

	
	var VisitName;
	var VisitNickname;
	var logedinUser;
	var LogedNickname;
	var updateFollowers;
	var ShowTopic=false;
	var TopicName;
	var IsTopicPage;
	var url;
	var userToVisit;
	
$(window).load(function(){
	
	userToVisit = window.location.hash.substring(1);	

		$.ajax({ 
	    	type: "POST",
	        url: "/New/GetSessionServlet",
	        success: function(res, status, xhr) {
	        	
	        	var obj = JSON.parse(res);
	        	logedinUser = obj[0].Username;
	        	LogedNickname = obj[0].Nickname;
	       
	        },
	        error: function() {
	       }
	    }).done(function(){
	    	//if this condition is true load discover data
	    	if(userToVisit == "")
	    	{
	    		$('#republishedMsg').hide();
	    		
	    		$.ajax({ 
	    	    	type: "POST",
	    	        url: "/New/GetSessionServlet",
	    	        success: function(res, status, xhr) {
	    	        	
	    	        	var obj = JSON.parse(res);
	    	        	var thisUser = obj[0].Username;
	    	        	
	    	        		username = obj[0].Username;
	    	        		VisitName = username;
	    		     		nickname = obj[0].Nickname;
	    		     		desc = obj[0].Description;
	    		     		following = obj[0].NumberOfFollowing;
	    		     		followers = obj[0].NumberOfFollowers;
	    		     		updateFollowers = obj[0].NumberOfFollowers;
	    		     		photo = obj[0].Photo ;
	    		     		$('#imageprofile').attr('src',photo);
	    		     		$('#helloUser').text(" " + nickname);
	    		     		$('#aboutme').text(desc);
	    		     		$('#followers').text("Followers " + followers);
	    		     		$('#following').text("Following " + following);	
	    		     		
	    		     		
		
	    	        },
	    	        error: function() {
	    	        	$('#helloUser').text("Failed");
	    	       }
	    	    });
	    	}
	    	//topic case
	    	else if(userToVisit.substring(0,6) == "topic="){
	    		$.ajax({ 
	    	    	type: "POST",
	    	        url: "/New/GetSessionServlet",
	    	        success: function(res, status, xhr) {
	    	        	
	    	        	var obj = JSON.parse(res);
	    	        	var thisUser = obj[0].Username;
	    	        
	    	        		username = obj[0].Username;
	    	        		VisitName = username;
	    		     		nickname = obj[0].Nickname;
	    		     		desc = obj[0].Description;
	    		     		following = obj[0].NumberOfFollowing;
	    		     		followers = obj[0].NumberOfFollowers;
	    		     		updateFollowers = obj[0].NumberOfFollowers;
	    		     		photo = obj[0].Photo ;
	    		     		$('#imageprofile').attr('src',photo);
	    		     		$('#helloUser').text(" " + nickname);
	    		     		$('#aboutme').text(desc);
	    		     		$('#followers').text("Followers " + followers);
	    		     		$('#following').text("Following " + following);	
	    		     		
		
	    	        },
	    	        error: function() {
	    	        	$('#helloUser').text("Failed");
	    	       }
	    	    });
	    	}
	    	//if its my page
	    	else if(userToVisit == logedinUser){
	    			$.ajax({ 
	    		    	type: "POST",
	    		        url: "/New/GetSessionServlet",
	    		        //data: $('#helloUser').serialize(),
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
	    	else{
	    		
	    		
	    		$.ajax({ 
	    	    	type: "POST",
	    	        url: "/New/VisitUserInfoServlet/username/" + userToVisit,
	    	        success: function(res, status, xhr) {
	    	        	
	    	        	var obj = JSON.parse(res);
	    	        	
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

//go to profile page 
function GoToProfile(){
	
	window.location.href='/New/Profile.html' +'#'+logedinUser;
}
//go to discover page
function GoToDiscover(){
	
	window.location.href='/New/Discover.html' ;
}



// execute when I am visiting another user profile and ask to follow him
function FollowMe(){
	
	$.ajax({
    	type: "POST",
        url: "/New/FollowServlet/nickname/" + VisitNickname,
        success: function(res, status, xhr) {	
        	var response = JSON.parse(res);
        	
        	if(response == "FOLLOW"){
        		updateFollowers = updateFollowers +1;
        		$('#followers').text("Followers " + updateFollowers );
        		$('#followMeBtn').text("UnFollow");
        	}
        	else if(response == "UNFOLLOW"){
        		updateFollowers = updateFollowers -1;
        		$('#followers').text("Followers " + updateFollowers );
        		$('#followMeBtn').text("Follow Me");
        	}
        	
        },
        error: function() {
        	
       }
    });
};
