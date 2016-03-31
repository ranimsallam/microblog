/**
 * angular app = get the last 10 topics that includes topic_name
 * and load the data of topic page
 * when changing the topic name int the url load the relevant information to displat the messages that include the new topic name 
 */



 $(window).on('hashchange', function(){

	 var TopicName = window.location.hash.substring(7);
	 $('#hashtagName').text("#" + TopicName);
	 angular.element(document.getElementById('Topics')).scope().GetMsgs(TopicName);
});	


//on load get the loged-in user information
	$(window).load(function(){
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
		     		

		     		
		     		var TopicName = window.location.hash.substring(7);
		     		$('#hashtagName').text("#" + TopicName);
		     		angular.element(document.getElementById('Topics')).scope().GetMsgs(TopicName);
		     		
	        },
	        error: function() {
	        	$('#helloUser').text("Failed");
	       }
	    });
		
		
		
	});	
	
	//execute when  the user wants to post a message
	function PostMsg(){
		$.ajax({
	    	type: "POST",
	        url: "/New/PostMsgServlet",
	        data: $('#PostMsgFromDisc').serialize(),
	        success: function(res, status, xhr) {
	        	$('#msgTxtFromDisc').val("");  
	        	$('#msgTxtFromDisc').attr("placeholder", "What's on your mind ?");   	
	        },
	        error: function() {
	       }
	    });
	}
	
	//execute when the user wants to reply to a message
	function Reply(){
		$.ajax({
	    	type: "POST",
	        url: "/New/PostMsgServlet",
	        data: $('#ReplyForm').serialize(),
	        success: function(res, status, xhr) {
	        	//var obj = JSON.parse(res);
	        	
	        	$('#myModal5').modal('hide');
	        	$('#postNote').show(1000).delay(2000).hide(1000);
	        },
	        error: function() {
	       }
	    });
	}
	
	//angular discover application
	var TopicApp  = angular.module('Topics' , []);
	
	TopicApp .controller('TopicCtrl' , ['$scope','$sce','$http','$timeout','$interval', function($scope,$sce,$http,$timeout,$interval) {
		
		$scope.url = window.location.hash.substring(1);
		$scope.TopicName = $scope.url.substring(6);
		$scope.OldMsgCounter=0;
		$scope.NewMsgCounter=0;
		$scope.newTopic;
		$scope.msgs;

		$scope.GetMsgs = function(xz){ 
			$scope.newTopic = xz;

		$http.get("/New/GetTopicMsgsServlet/topic/" + xz)
		.success(function(response) {
			
			$scope.msgs=response;
			$scope.OldMsgCounter = $scope.msgs.length;
			$scope.DisMsgs = [];
			
			var c = 0;
			while( c < 10){
				
				//check hashtag
				$scope.msgs[c].MsgText = $scope.msgs[c].MsgText.replace(/[#]+[A-Za-z0-9-_]+/g, function(t) {
	
					var tag = t.replace("#","");
						return t.link("/New/Topic.html"+"#topic="+tag);
				});
				//hashtag end
				
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
				
				var y = $scope.msgs[c];
				$scope.DisMsgs.push(y);
				c++;					
			}
				
		});	
	}	
			
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
		
		//check if there is new message with this topic and show the see new updates option
		$interval(function(){
			
		
					$http({
					url: "/New/GetTopicMsgsServlet/topic/" + $scope.newTopic,
					method: "post"
					
				}).success(function(response){ 
					$scope.topicMsgs=response;  
					
					if($scope.OldMsgCounter == $scope.topicMsgs.length  ){
						$("#notificationBtn").hide();
						
					}else if($scope.OldMsgCounter < $scope.topicMsgs.length){
						$scope.newMsgs =  $scope.topicMsgs.length - $scope.OldMsgCounter;
						$("#notificationBtn").show();
					}

				});
			},1000);
		
		
		
			$scope.TrustMsgContent = function(content){
				return $sce.trustAsHtml(content);
			};
				
		$scope.ReplyName = function(author){
			$('#replymsg').val("@" + author + " ");	
			};
		
		//execute when the user asks to follow Nickname
		$scope.Follow = function(Nickname){
			
			$.ajax({
		    	type: "POST",
		        url: "/New/FollowServlet/nickname/" + Nickname,
		        //data: MsgID,
		        success: function(res, status, xhr) {	
		        	if( $("#FollowBtn").html() == "UnFollow" ){
		        		$("#FollowBtn").html("Follow");
		        	}
		        	else{
		        		$("#FollowBtn").html("UnFollow");
		        	}
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
			$scope.FollowTable
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
			        	
			        	$('#republishNote').show(1000).delay(2000).hide(1000);	
			        },
			        error: function() {
			       }
			    });
				};
				
				//get the photo url of author
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