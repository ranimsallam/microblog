/**
 * 
 */

/* this function called from the onload page to initialize the information about the loged in user  */
function LogedinPage(obj){
	username = obj[0].Username;
	VisitName = obj[0].Username;
		nickname = obj[0].Nickname;
		desc = obj[0].Description;
		following = obj[0].NumberOfFollowing;
		followers = obj[0].NumberOfFollowers;
		//updateFollowers = obj[0].NumberOfFollowers;
		photo = obj[0].Photo ;
		$('#imageprofile').attr('src',photo);
		$('#helloUser').text(" " + nickname);
		$('#aboutme').text(desc);
		$('#followers').text("Followers " + followers);
		//$('#badgeFollowers').text(followers);
		$('#following').text("Following " + following);
		$('#followMeBtn').hide();
		$('#newmsg').show();
	
}

/* this function called from the onload page to initialize the information about the user that we visit his page.*/
function UsersPage(obj){
	username = obj[0].Username;
	VisitName = username;
		nickname = obj[0].Nickname;
		VisitNickname = obj[0].Nickname;
		desc = obj[0].Description;
		following = obj[0].NumberOfFollowing;
		followers = obj[0].NumberOfFollowers;
		updateFollowers = obj[0].NumberOfFollowers;
		photo = obj[0].Photo ;
		$('#imageprofile').attr('src',photo);
		$('#helloUser').text(" " + nickname);
		$('#aboutme').text(desc);
		$('#followers').text("Followers " + followers);
		//$('#badgeFollowers').text(followers);
		$('#following').text("Following " + following);
		$('#newmsg').hide();
		$('#followMeBtn').show();
}