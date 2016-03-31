/**
 * angular application to get the username followers 
 */

var FollowersApp = angular.module('myFollowers' , []);



FollowersApp.controller('FollowerCrtl' , ['$scope','$sce','$http', '$interval' ,
	         function($scope,$sce,$http,$interval){
	      	
		$scope.TrustMsgContent = function(content){
			return $sce.trustAsHtml(content);
		};
		
		//$interval(function(){
			$scope.UpdateFollowers = function(userToVisit){
			$http({
				url: "/New/GetFollowerUserServlet/username/" + userToVisit,
				method: "post"
				
			}).success(function(response){ 
											
					$scope.followers=response;  
					var i=0; 
					while(i < $scope.followers.length){
						
						Thisnickname = $scope.followers[i].Nickname
						$scope.followers[i].Nickname = $scope.followers[i].Nickname.replace( /[A-Za-z0-9-_]+/, function(u)
						{				
									//link to Thisnickname
									return u.link("/New/Profile.html"+"#"+$scope.followers[i].Username);
								
						}); 
						
						i++;
					}

				
				});
			}
		//},1000);
	
	 }]);
angular.bootstrap(document.getElementById("myFollowers"),['myFollowers']);
 