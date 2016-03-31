package First;

import java.lang.reflect.Type;
import java.util.Collection;

import com.google.gson.reflect.TypeToken;

import Topics.Topic;
import Users.User;
import Following.Follow;
import Messages.Message;
/**
 * A simple place to hold global application constants
 */
public interface AppConstants {
	
	public final String USERS = "users";
	public final String MESSAGES = "messages";
	public final String NAME = "name";
	public final String NICKNAME = "nickname";
	public final String MSGID = "msgid";
	public final String FOLLOW = "follow";
	public final String USERNAME = "username";
	public final String TOPIC = "topic"; 
	
	
	public final Type USER_COLLECTION = new TypeToken<Collection<User>>() {}.getType();
	public final Type MSGS_COLLECTION = new TypeToken<Collection<Message>>() {}.getType();
	public final Type FOLLOW_COLLECTION = new TypeToken<Collection<Follow>>() {}.getType();
	public final Type TOPIC_COLLECTION = new TypeToken<Collection<Topic>>() {}.getType();
	//derby constants
	public final String DB_NAME = "CustomerDB";
	public final String DB_DATASOURCE = "java:comp/env/jdbc/CustomerDatasource";
	public final String PROTOCOL = "jdbc:derby:";
	
	//sql statements
	public final String CREATE_USERS_TABLE = "CREATE TABLE USERS(Username varchar(10),"
			+ "Password varchar(8),"
			+ "Nickname varchar(20),"
			+ "Description varchar(50),"
			+ "Photo varchar(255),"
			+ "Popularity double,"
			+ "FollowingCounter integer,"
			+ "FollowersCounter integer)";
	
	public final String INSERT_USER_STMT = "INSERT INTO USERS VALUES(?,?,?,?,?,?,?,?)";
	public final String SELECT_ALL_USERS_STMT = "SELECT * FROM USERS";
	public final String SELECT_USERS_BY_NAME_STMT = "SELECT * FROM USERS "
			+ "WHERE Username = ?";
	public final String SELECT_USERS_BY_NICKNAME_STMT = "SELECT * FROM USERS "
			+ "WHERE Nickname = ?";
	public final String INC_FOLLOWERS_BY_ONE = "UPDATE USERS " + "SET  FollowersCounter=FollowersCounter+1 " + "WHERE Nickname = ?";
	public final String INC_FOLLOWING_BY_ONE = "UPDATE USERS " + "SET  FollowingCounter=FollowingCounter+1 " + "WHERE Nickname = ?";
	public final String DEC_FOLLOWERS_BY_ONE = "UPDATE USERS " + "SET  FollowersCounter=FollowersCounter-1 " + "WHERE Nickname = ?";
	public final String DEC_FOLLOWING_BY_ONE = "UPDATE USERS " + "SET  FollowingCounter=FollowingCounter-1 " + "WHERE Nickname = ?";
	public final String UPDATE_POPULARITY = "UPDATE USERS " + "SET  Popularity= ? " + "WHERE Nickname = ?";
	

	
	
	public final String CREATE_MESSAGES_TABLE = "CREATE TABLE MESSAGES(MsgID integer,"
			+ "MsgTxt varchar(140),"
			+ "Username varchar(10),"	
			+ "Date varchar(200),"
			+ "Republished integer)";
	
	public final String INSERT_MESSAGE_STMT = "INSERT INTO MESSAGES VALUES(?,?,?,?,?)";
	public final String SELECT_OTHERS_MSGS = "SELECT * FROM MESSAGES " + "WHERE Username <> ? ORDER BY MsgID DESC";
	public final String SELECT_ALL_MSGS = "SELECT * FROM MESSAGES";
	public final String SELECT_MSG_ORDER_BY_ID = "SELECT * FROM MESSAGES ORDER BY MsgID DESC";

	public final String SELECT_MSGS_BY_USER = "SELECT * FROM MESSAGES " + "WHERE Username = ? " + "ORDER BY MsgID DESC";
	public final String UPDATE_REPUBLISHED = "UPDATE MESSAGES " + "SET  Republished = Republished+1 " + "WHERE MsgID = ?";
	public final String GET_MSG_BY_ID = "SELECT * FROM MESSAGES WHERE MsgID = ?";
	
	

	
	
	
	
	public final String CREATE_FOLLOWING_TABLE = "CREATE TABLE FOLLOWING(Nickname varchar(10),"
			+"NicknameToFollow varchar(10))";
	
	public final String INSER_FOLLOWING_STMT = "INSERT INTO FOLLOWING VALUES(?,?)";
	public final String SELECT_ALL_FOLLOWING = "SELECT * FROM FOLLOWING";
	public final String SELECT_FOLLOW_PAIR = "SELECT * FROM FOLLOWING " + "WHERE Nickname = ?" + "AND NicknameToFollow = ?";
	public final String DELETE_ROW_FROM_FOLLOWING = "DELETE FROM FOLLOWING " + "WHERE Nickname = ?" + "AND NicknameToFollow = ?";
	public final String GET_FOLLOWERS = "SELECT * FROM USERS AS P1 INNER JOIN FOLLOWING AS P2 ON "
			+ "P1.Nickname=P2.Nickname WHERE NicknameToFollow = ?" + "ORDER BY Popularity DESC"; 
	public final String GET_FOLLOWING = "SELECT * FROM USERS AS P1 INNER JOIN FOLLOWING AS P2 ON "
			+ "P1.Nickname=P2.NicknameToFollow WHERE P2.Nickname = ?" + "ORDER BY Popularity DESC";
	
	
	public final String CREATE_TOPICS_TABLE = "CREATE TABLE TOPICS(Topic varchar(10),"
			+"MsgID integer)";
	
	public final String INSERT_INTO_TOPICS = "INSERT INTO TOPICS VALUES(?,?)";
	public final String GET_MSGS_FROM_TOPICS = "SELECT * FROM MESSAGES AS P1 INNER JOIN TOPICS AS P2 ON "
			+ "P1.MsgID=P2.MsgID WHERE P2.Topic = ?" + "ORDER BY P2.MsgID DESC";
	public final String SELECT_ALL_TOPICS = "SELECT * FROM TOPICS";

}

