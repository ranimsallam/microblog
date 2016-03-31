package Messages;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConvertDate {
	
	public final static int Sixty = 60;
	public final static int TwentyFour = 24;
	public final static long Second = 1000;
	
	public final static long Minute = Sixty*Second;
	
	public final static long Hour = Sixty*Minute;
	
	public final static long Day = TwentyFour*Hour;
	
	public ConvertDate(){}
	
	/**
	 * 
	 * @param PDate the date of the publish day of the message.
	 * @return the time that had been passed of the posted message.
	 * @throws ParseException
	 */
	public static String GetDuration(String PDate) throws ParseException{
		
		Date d = new Date();
		SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		
		Date PublishDate = date.parse(PDate);
		
		
		
		StringBuffer returnString = new StringBuffer();
		long check;
		
		String MsgDay = PDate.substring(0,3);
		String todayDay = date.format(d).substring(0, 3);
		
		Date now = date.parse(date.format(d));
		long dur = now.getTime() - PublishDate.getTime() +Second;
		
		if (dur >= Second) {
			check = dur / Day;
	          if (check > 0) {
	            dur -= check * Day;
	            returnString.append(PDate);
	            return returnString.toString();
	          }

	          check = dur / Hour;
	          if (check > 0) {
	            dur -= check * Hour;
	           
	            if(MsgDay.equals(todayDay)){
	            	returnString.append("today, ").append(check).append(" hour");
	            	
	            }else{
	            	returnString.append(check).append(" hour");
	            }
	            if(check > 1)
	            	returnString.append("s ago");
	            else{
	            	returnString.append(" ago");
	            }
	            
	            return returnString.toString();
	          }

	          check = dur / Minute;
	          if (check > 0) {
	            dur -= check * Minute;
	            returnString.append(check).append(" minute");
	            if(check > 1)
	            	returnString.append("s ago");
	            else{
	            	returnString.append(" ago");
	            }
	            
	            return returnString.toString();
	          }


	          check = dur / Second;
	          if (check > 0) {
	        	  returnString.append(check).append(" second");
	        	  if(check > 1)
	        		  returnString.append("s ago");
	        	  else{
	        		  returnString.append(" ago");
	        	  }
	        	
	          }
	          return returnString.toString();
	        }
		else{
			return "0 second ";
		}
	}
}
