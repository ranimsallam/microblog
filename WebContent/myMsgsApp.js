/**
 * angular app - to get the last 10 messages of the user 
 */


var MyMsgsApp = angular.module('myMsgs' , []);
		
		MyMsgsApp.controller('myMsgsrCtrl' , ['$scope','$sce','$http','$timeout','$interval' , 
		                                      function($scope,$sce,$http,$timeout,$interval) {
		
			//return the content as html - call this function to make the @nickname or #topic in message as link
			$scope.TrustMsgContent = function(content){
				
				return $sce.trustAsHtml(content);
				};
				
				//return the username as html to link it with his page
				$scope.retNickname = function(author){
					return author.link("/New/Profile.html"+"#"+author);
				}
			
				//get last 10 messages that nickname posted
				//check if the messages include legal #topics or @tag 
				$interval(function(){
				$http({
					url: "/New/GetUserMsgsServlet/nickname/" + nickname,
					method: "post"
					
				}).success(function(response){ 
											
						$scope.msgs=response;  
												
						var c = 0;
						while( c < $scope.msgs.length){									
							
							$scope.msgs[c].MsgText = $scope.msgs[c].MsgText.replace(/[#]+[A-Za-z0-9-_]+/g, function(t) {
								
								
								var tag = t.replace("#","");
								return t.link("/New/Topic.html"+"#topic="+tag);
						
								
				
							});
							
							$scope.msgs[c].MsgText = $scope.msgs[c].MsgText.replace( /[@]+[A-Za-z0-9-_]+/g , function(u)
								{
									var i = 0;
									var j = 0;
									var isLegal = false;
							 		var username2 = u.replace("@","");
												
							 		while( i < allUsersIn.length-1){
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
			
			//execute this function when the user asks to republish a message
			// and check if I am visiting another user page don't show it else show it on the top of the list
			$scope.RepublishFunc = function(MsgID){
				
				$.ajax({
			    	type: "POST",
			        url: "/New/RepublishServlet/msgid/" + MsgID,
			        data: MsgID,
			        success: function(res, status, xhr) {
			        	var obj = JSON.parse(res);
			        	
			        	if(userToVisit == LogedNickname){
			        		nickname = obj[0].Author;
			        	}
				        	
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
		
		angular.bootstrap(document.getElementById("msgsID"),['myMsgs']);