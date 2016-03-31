package Messages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;




public class Message implements Comparable<Message>{
	public static int MsgCounter = 0;
	public static boolean ShowMsgsIFollow = false;
	private int MsgID;
	private String MsgText;
	private String Author;
	private double Popularity;
	private int Republished;
	private String date;
	
	
	public Message(int msgid, String msgtext, String author,String d, int republished,double pop){
		this.MsgID = msgid;
		this.MsgText = msgtext;
		this.Author = author;
		this.Popularity = pop;
		this.Republished = republished;
		this.date = d;
		
	}
	
	public Message(int msgid, String msgtext, String author,String d, int republished){
		this.MsgID = msgid;
		this.MsgText = msgtext;
		this.Author = author;
		this.Republished = republished;
		this.date = d;
		this.Popularity = Math.log(2);
	}
	
	public int getMsgID(){
		return this.MsgID;
	}
	
	public String getMsgText(){
		return this.MsgText;
	}
	
	public String getAuthor(){
		return this.Author;
	}
	
	public int getRepublished(){
		return this.Republished;
	}
	
	public double getPopularity(){
		return this.Popularity;
	}
	
	public String getDate(){
		return this.date;
	}
	
	/*public void CalculatePopularity(){
		int followers = this.Author.getNumberOfFollowers();
		double x = Math.log10(2+followers);
		this.Popularity = (x * Math.log(2 + this.Republished) );
		
	}*/
	
	
	@Override
    public int compareTo(Message comparemsg) {
        double comparePopularity=((Message)comparemsg).getPopularity();
        /* For Ascending order*/
        double result = comparePopularity-this.Popularity; // Descending order
        
        if(result == 0)
        	return 0;
        else if(result > 0)
        	return 1;
        else return -1;
   
    }
	
	public static void ReturnSorted(ArrayList<Message> msgs){
		Collections.sort(msgs);
	}
    
	public static Comparator<Message> COMPAREBYDATE = new Comparator<Message>() {
        public int compare(Message msg1, Message msg2) {
        	int res = (msg2.MsgID) - (msg1.MsgID);
            return res;
        }
    };
	
	

}