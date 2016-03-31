package Users;

public class User {
	private String Username;
	private String Password;
	private String Nickname;
	private String Description;
	private String Photo;
	private double Popularity;
	private int NumberOfFollowers;
	private int NumberOfFollowing;
	
	public User(String username, String password, String nickname, String description,
				String photo){
		
		this.Username = username;
		this.Password = password;
		this.Nickname = nickname;
		this.Description = description;
		this.Photo = photo;
		this.Popularity = Math.log10(2);
		this.NumberOfFollowers = 0;
		this.NumberOfFollowing = 0;
		
	}
	
	public User(String username, String password, String nickname, String description,
			String photo,double pop,int following, int followers){
	
	this.Username = username;
	this.Password = password;
	this.Nickname = nickname;
	this.Description = description;
	this.Photo = photo;
	this.Popularity = pop;
	this.NumberOfFollowers = followers;
	this.NumberOfFollowing = following;
	
}
	
	public String getUsername(){
		return this.Username;
	}
	
	public String getPassword(){
		return this.Password;
	}
	
	public String getNickname(){
		return this.Nickname;
	}

	public String getDescription(){
		return this.Description;
	}
	
	public String getPhoto(){
		return this.Photo;
	}
	
	public double getPopularity(){
		return this.Popularity;
	}
	
	public int getNumberOfFollowers(){
		return this.NumberOfFollowers;
	}
	
	public int getNumberOfFollowing(){
		return this.NumberOfFollowing;
	}
	
	public void CalculatePopularity(){
		this.Popularity = Math.log10(2 + this.NumberOfFollowers);
	}
	

}

