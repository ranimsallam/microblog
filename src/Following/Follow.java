package Following;

public class Follow {
	
	String Nickname;
	String NicknameToFollow;
	
	public Follow(String n1, String n2){
		this.Nickname = n1;
		this.NicknameToFollow = n2;
	}
	
	public String getNickname(){
		return this.Nickname;
	}
	
	public String getNicknameToFollow(){
		return this.NicknameToFollow;
	}

}
