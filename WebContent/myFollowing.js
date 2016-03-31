/**
 * angulat app to get the following users of userToVisit
 */

var FollowersApp = angular.module('myFollowing' , []);


FollowersApp.controller('FollowingCrtl' , ['$scope','$sce','$http', '$interval' ,
	         function($scope,$sce,$http,$interval){
	      	
		$scope.TrustMsgContent = function(content){
			return $sce.trustAsHtml(content);
		};

		
		//$interval(function(){
		$scope.Updatefollow = function(userToVisit){
			$http({
				url: "/New/GetFollowUsersServlet/username/" + userToVisit ,
				method: "post"
				
			}).success(function(response){ 
			
					$scope.following=response;  
					var i=0; 
					while(i < $scope.following.length){
						
						Thisnickname = $scope.following[i].Nickname
						$scope.following[i].Nickname = $scope.following[i].Nickname.replace( /[A-Za-z0-9-_]+/, function(u)
						{				
									//link to Thisnickname
									return u.link("/New/Profile.html"+"#"+$scope.following[i].Username);
								
						}); 
						
						i++;
					}

				
				});
		}
		//},1000);
	
	 }]);
angular.bootstrap(document.getElementById("myFollowing"),['myFollowing']);