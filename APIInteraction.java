// This requires the JSON Object library
// org_json_json_20070829
// Look up how to add a Maven library using your compiler
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class APIInteraction {
    // These are for converting the Alexa date format into Java Date objects
    static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ssX";
    static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN);
    static final String ALEXA_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    static final SimpleDateFormat ALEXA_DATE_FORMAT = new SimpleDateFormat(ALEXA_DATE_PATTERN);
    
    public static void main(String args[]) throws Exception {  // Throws an exception when the API returns abnormally
        // This main function is just an example.
        
        
        // Alexa gives dates in an ISO format. You can change this string to any of the Alexa date outputs to test it.
        // In Alexa format, this will be the Date Slot output
        String alexaTestDate1 = "2016-FA";  // "What's going on this fall"
        //String alexaTestDate1 = "2017";  // "What's going on next year?"
        //String alexaTestDate1 = "2016-11";  // "What's going on next month?"
        //String alexaTestDate1 = "201X";  // "What's going on this decade?"
        
        ArrayList<JSONObject> events = getFromAPIByAlexaDate(alexaTestDate1);
        // Gets all the events in the requested range from the String input form Alexa's date slot
        // range is determined by input
        // EX: "2017" will return events between 01-01-2017 and 12-31-2017
        // Single date input like 'tomorrow' is accepted

        // Returns an ArrayList of JSONObjects that can be accessed like a Map using String Keys
        // You can pull any of the data individually, by using the .getString interface
        // These are all the categories in the API JSON event object. Some can return null.
        
            System.out.println("title        : " + events.get(0).getString("title"));
            System.out.println("end_date     : " + events.get(0).getString("end_date"));
            System.out.println("created      : " + events.get(0).getString("created"));
            System.out.println("image_url    : " + events.get(0).getString("image_url"));
            System.out.println("description  : " + events.get(0).getString("description"));
            System.out.println("url          : " + events.get(0).getString("url"));
            System.out.println("deleted      : " + events.get(0).getString("deleted"));
            System.out.println("event_id     : " + events.get(0).getString("event_id"));
            System.out.println("organization : " + events.get(0).getString("organization"));
            System.out.println("modified     : " + events.get(0).getString("modified"));
            System.out.println("location     : " + events.get(0).getString("location"));
            System.out.println("id           : " + events.get(0).getString("id"));
            System.out.println("category     : " + events.get(0).getString("category"));
            System.out.println("start_date   : " + events.get(0).getString("start_date"));
            System.out.println();
        
        // There's also a literal converter that can take a JSON object and produce a string
        // The string output can be edited at the bottom of this file
        for(JSONObject event : events) {
            System.out.println(JSONEventToAlexaLiteral(event));
        }
    }
    
    
    // Gets all events - there's currently no way to only request specific dates
    public static ArrayList<JSONObject> getFromAPI() throws Exception {
        ArrayList<JSONObject> mappedAPIResults = new ArrayList<>();
        URL api = new URL("http://moonlight.cs.sonoma.edu/api/v1/events/event/?format=json");
        URLConnection apiConnection = api.openConnection();
        apiConnection.connect();
        BufferedReader in = new BufferedReader(new InputStreamReader(apiConnection.getInputStream()));
        String inputLine, rawJSON = "";
        while ((inputLine = in.readLine()) != null)
            rawJSON += inputLine;
        in.close();

        // Clean the JSON string
        rawJSON = rawJSON.substring(1, rawJSON.length() - 1);  // remove [ and ]
        String[] cleanedJSON = rawJSON.split(",(?=\\{)");  // convert rawJSON into an array of objects
        
        for (String buff : cleanedJSON)  // for each element of the cleanedJSON array
            mappedAPIResults.add(new JSONObject(buff));  // convert the String into a new object in the array

        return mappedAPIResults;
    }
    
    // Returns all dates within a date range (startDate and endDate can be the same, will return events on that day)
    public static ArrayList<JSONObject> getFromAPIByDate(Date startDate, Date endDate) throws Exception {
        ArrayList<JSONObject> results = new ArrayList<>();
        ArrayList<JSONObject> mappedAPIResults = getFromAPI();
        Date JSONDate;
        for(JSONObject buff : mappedAPIResults){
            JSONDate = DATE_FORMAT.parse(buff.getString("start_date"));
            if((JSONDate.after(startDate) && JSONDate.before(endDate)) 
                    || JSONDate.equals(startDate)
                    || JSONDate.equals(endDate)){
                results.add(buff);
            }
        }
        return results;
    }
    
    // Returns a start and end date in an ArrayList that matches the date Alexa gives
    // Works for all Alexa datetypes (that are listed online)
    public static ArrayList<Date> parseAlexaDate(String alexaDate) throws ParseException {
        ArrayList<Date> results = new ArrayList<>();
        if(alexaDate.length() == 10){
            results.add(ALEXA_DATE_FORMAT.parse(alexaDate + "T00:00:00"));
            results.add(ALEXA_DATE_FORMAT.parse(alexaDate + "T23:59:59"));            
        }
        else if(alexaDate.length() == 4){
            if(alexaDate.contains("X")){
                String decadeDate = alexaDate.substring(0,3) + "0-01-01";
                results.add(ALEXA_DATE_FORMAT.parse(decadeDate + "T00:00:00"));
                decadeDate = alexaDate.substring(0,3) + "9-12-31";
                results.add(ALEXA_DATE_FORMAT.parse(decadeDate + "T23:59:59"));
            }
            else{
                String yearDate = alexaDate + "-01-01";
                results.add(ALEXA_DATE_FORMAT.parse(yearDate + "T00:00:00"));
                yearDate = alexaDate + "-12-31";
                results.add(ALEXA_DATE_FORMAT.parse(yearDate + "T00:00:00"));
            }
        }
        else if(alexaDate.contains("WI")){
            String winterDate = alexaDate.substring(0,4) + "-12-01";
            results.add(ALEXA_DATE_FORMAT.parse(winterDate + "T00:00:00"));
            int winterYear = Integer.parseInt(winterDate.substring(0,4));
            winterYear++;
            winterDate = String.valueOf(winterYear) + "-02-28";
            results.add(ALEXA_DATE_FORMAT.parse(winterDate + "T00:00:00"));
        }
        else if(alexaDate.contains("SP")){
            String springDate = alexaDate.substring(0,4) + "-03-01";
            results.add(ALEXA_DATE_FORMAT.parse(springDate + "T00:00:00"));
            springDate = springDate.substring(0,4) + "-05-31";
            results.add(ALEXA_DATE_FORMAT.parse(springDate + "T00:00:00"));
        }
        else if(alexaDate.contains("SU")){
            String summerDate = alexaDate.substring(0,4) + "-06-01";
            results.add(ALEXA_DATE_FORMAT.parse(summerDate + "T00:00:00"));
            summerDate = summerDate.substring(0,4) + "-08-31";
            results.add(ALEXA_DATE_FORMAT.parse(summerDate + "T00:00:00"));
        }
        else if(alexaDate.contains("FA")){
            String fallDate = alexaDate.substring(0,4) + "-09-01";
            results.add(ALEXA_DATE_FORMAT.parse(fallDate + "T00:00:00"));
            fallDate = fallDate.substring(0,4) + "-10-30";
            results.add(ALEXA_DATE_FORMAT.parse(fallDate + "T00:00:00"));
        }
        else if(alexaDate.contains("WE")){
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(alexaDate.substring(6,8)) + 1);
            cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            results.add(ALEXA_DATE_FORMAT.parse(ALEXA_DATE_FORMAT.format(cal.getTime()) + "T00:00:00"));
            cal.add(Calendar.DATE, 1);
            results.add(ALEXA_DATE_FORMAT.parse(ALEXA_DATE_FORMAT.format(cal.getTime()) + "T00:00:00"));
        }
        else if(alexaDate.contains("W") && alexaDate.length() == 8){
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(alexaDate.substring(6,8)) + 1);
            cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            results.add(ALEXA_DATE_FORMAT.parse(ALEXA_DATE_FORMAT.format(cal.getTime())));
            cal.add(Calendar.DATE, 7);
            results.add(ALEXA_DATE_FORMAT.parse(ALEXA_DATE_FORMAT.format(cal.getTime())));
        }
        else if(alexaDate.length() == 7){
            String monthYear = alexaDate.substring(0,4);
            String monthMonth = alexaDate.substring(5,7);
            
            results.add(ALEXA_DATE_FORMAT.parse(monthYear + "-" + monthMonth + "-01" + "T00:00:00"));
            
            int monthYearNum = Integer.parseInt(monthYear);
            int monthMonthNum = Integer.parseInt(monthMonth);
            
            monthMonthNum++;
            if(monthMonthNum == 13){
                monthYearNum++;
                monthMonthNum = 1;
            }
            
            monthYear = String.valueOf(monthYearNum);
            monthMonth = String.valueOf(monthMonthNum);
            
            if(monthMonth.length() == 1)
                monthMonth = "0" + monthMonth;

            results.add(ALEXA_DATE_FORMAT.parse(monthYear + "-" + monthMonth + "-01" + "T00:00:00"));
        }
            
        return results;
    }
    
    // Converts the ISO date format in the API into something Alexa will say correctly
    public static String JSONDateToAlexaLiteral(String inputDate) throws ParseException {
        return " <say-as interpret-as=\"date\">" + inputDate.substring(0,10) + "</say-as> ";
    }
    
    // Converts the JSON API input time to a time Alexa can say
    public static String JSONTimeToAlexaLiteral(String inputStartTime, String inputEndTime){
        String startHour = inputStartTime.substring(11,13);
        String startMinute = inputEndTime.substring(14,16);
        String endHour = inputEndTime.substring(11,13);
        String endMinute = inputEndTime.substring(14,16);
        
        
        if(startHour == "00" && startMinute == "00" && endHour == "00" && endMinute == "00")
            return "";
        
        // Converts the 24 hour time to 12 hour times
        // badly. this doesn't work. but the 24 time returns correctly
        /*
        int startHourInt = Integer.parseInt(startHour);
        if(startHourInt > 12) {
            startHourInt -= 12;
            startHour = String.valueOf(startHourInt);
            if(startHour.length() < 2)
                startHour = "0" + startHour;
        }
        
        int endHourInt = Integer.parseInt(endHour);
        if(endHourInt > 12) {
            endHourInt -= 12;
            endHour = String.valueOf(endHourInt);
            if(endHour.length() < 2)
                endHour = "0" + endHour;
        }
        */
        return "at " + startHour + ":" + startMinute + "-" + endHour + ":" + endMinute;
        
    }
    
    public static ArrayList<JSONObject> getFromAPIByAlexaDate(String alexaDate) throws Exception {
        ArrayList<Date> dates = parseAlexaDate(alexaDate);
        ArrayList<JSONObject> events = getFromAPIByDate(dates.get(0),dates.get(1));
        return events;
    }
    
    // Returns a string to pass to Alexa's request system from each single JSON event
    public static String JSONEventToAlexaLiteral(JSONObject event) throws JSONException, ParseException {
        return "On" + JSONDateToAlexaLiteral(event.getString("start_date")) + ", "  // date of event
                + "there is a " + event.getString("title") + " event "  // name of event
                + JSONTimeToAlexaLiteral(event.getString("start_date"),event.getString("end_date"));  // time of event
    }
}
