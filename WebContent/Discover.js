/**
 * 
 */
	var ClickCounter = 0;

	
	//function that returns the follow information to intialize the follow button to follow/unfollow
	setInterval(function(){
					$.ajax({
				    	type: "POST",
				        url: "/New/ShowFollowTable",
				        
				        success: function(res, status, xhr) {
				        	     	
				        	var obj = JSON.parse(res);
				        	allFollow = obj;
				        },
				        error: function() {
				        	
				       }
				    });	
				
			},1000);	
	
	//call change display servlet to change the display from in the discover -> show messages I follow / show public messages
	function ChangeDisplay(){
		
		$.ajax({
	    	type: "POST",
	        url: "/New/ChangeDisplayServlet",
	      
	        success: function(res, status, xhr) {
	        	ClickCounter++;
	        	if( (ClickCounter%2) == 0){
	        		$('#btnP').text("Show Messages I Follow");	
	        	}else{
	        		$('#btnP').text("Show Public Messages");
	        	}
	
	        },
	        error: function() {
	        	$('#btnP').text("Failed");
	       }
	    });	
	}
	
	//executes when the user wants to post a message from the discover page.
	function PostMsg(){
		$.ajax({
	    	type: "POST",
	        url: "/New/PostMsgServlet",
	        data: $('#PostMsgFromDisc').serialize(),
	        success: function(res, status, xhr) {
	        	$('#msgTxtFromDisc').val("");  
	        	$('#msgTxtFromDisc').attr("placeholder", "What's on your mind ?");  
	        	$('#postNote').show(1000).delay(3000).hide(1000);
	        	
	        },
	        error: function() {
	       }
	    });
	}
	
	//function to execute when the user wants to reply to a message - call post servlet with the reply message
	function Reply(){
		$.ajax({
	    	type: "POST",
	        url: "/New/PostMsgServlet",
	        data: $('#ReplyForm').serialize(),
	        success: function(res, status, xhr) {
	        	//var obj = JSON.parse(res);
	        	$('#myModal4').modal('hide');
	        	$('#postNote').show(1000).delay(2000).hide(1000);	
	        },
	        error: function() {
	       }
	    });
	}
	
	//angular discover application
	var DiscoverApp = angular.module('discover' , []);
	
	DiscoverApp.controller('DiscoverCtrl' , ['$scope','$sce','$http','$timeout','$interval', function($scope,$sce,$http,$timeout,$interval) {
		
		$scope.myNickname = nickname;
		$scope.myPhoto = photo;
		$scope.time = "";
		$scope.ReMsg = "";
	
		
		//return the #topics and @tags in the message as html
		$scope.TrustMsgContent = function(content){
			return $sce.trustAsHtml(content);
			};
			
			//return the username as html to link it with his page
			$scope.retNickname = function(author){
				
				var i=0;
				var userRet;
				while(i < allUsersIn.length){
					
					if( allUsersIn[i].Nickname == author){
						userRet = allUsersIn[i].Username;
						
					}
					i++;
				}
				return "Profile.html#"+userRet;
				
			}

		//get all the meesages that must be displayed in the discover page 
		//$interval - to update in real time
		$interval(function(){

				$http({
				url: "/New/DiscoverServlet",
				method: "post"
				
			}).success(function(response){ 
			
				$scope.msgs=response;  

				var c = 0;
				while( c < $scope.msgs.length){
								
					
					$scope.msgs[c].MsgText = $scope.msgs[c].MsgText.replace(/[#]+[A-Za-z0-9-_]+/g, function(t) {
						
						
						var tag = t.replace("#","");
						return t.link("/New/Topic.html"+"#topic="+tag);

					});
					
											
					$scope.msgs[c].MsgText = $scope.msgs[c].MsgText.replace( /[@]+[A-Za-z0-9-_]+/g, function(u)
						{
							var i = 0;
							var j = 0;
							var isLegal = false;
					 		var username2 = u.replace("@","");
										
					 		while( i < allUsersIn.length){
									if(allUsersIn[i].Nickname == username2){
										isLegal = true;
										j=i;
									}
									i++;
							}
					 		if(isLegal == true){
									
									return u.link("/New/Profile.html"+"#"+allUsersIn[j].Username);
							}else{
									return u;
								}		
						});
					
					
					c++;					
				}
			});

		},2000);
		
		
		//add @nickname to the reply message
		$scope.ReplyName = function(author){
			$('#replymsg').val("@" + author + " ");	
			};
		
		//execute when the user asks to follow Nickname
		$scope.Follow = function(Nickname){
			
			$.ajax({
		    	type: "POST",
		        url: "/New/FollowServlet/nickname/" + Nickname,
		        success: function(res, status, xhr) {	
			        	
		        },
		        error: function() {
		        	
		       }
		    });
		};
		
		//check if the user is following the author of the message to update the the follow button ->follow/unfollow
		$scope.isFollow = function(author){
		
			var isfollow = false;
			var followbtn = "Follow";
			var i = 0;
			while( i < allFollow.length){
				if(author == allFollow[i].NicknameToFollow && allFollow[i].Nickname == nickname){
					isfollow = true;
					followbtn = "UnFollow";
				}
				i++;
			}
			if(isfollow == true){
			
				return followbtn;
			}else{
				
				return followbtn;
			}

			};
			
			
			//execute this function when the user asks to republish a message
			// and check if I am visiting another user page don't show it else show it on the top of the list
			$scope.RepublishFunc = function(MsgID){
				
				$.ajax({
			    	type: "POST",
			        url: "/New/RepublishServlet/msgid/" + MsgID,
			        data: MsgID,
			        success: function(res, status, xhr) {
				        	var obj = JSON.parse(res);
				        	$('#republishNote').show(1000).delay(2000).hide(1000);

			        },
			        error: function() {
			       }
			    });
				};
				
				//get the url photo to the user with nickname author
				$scope.GetPhoto = function(author){
					var i=0;
					var photoUrl;
					while(i < allUsersIn.length){
						if(author == allUsersIn[i].Nickname ){
							photoUrl = allUsersIn[i].Photo;
							
						}
						i++;
					}
					return photoUrl;
				}
				
				

	}]);