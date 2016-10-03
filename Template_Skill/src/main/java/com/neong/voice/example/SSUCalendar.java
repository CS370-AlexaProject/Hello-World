package com.neong.voice.example;

import com.amazon.speech.slu.Intent;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.neong.voice.model.base.Conversation;

import java.util.*;

/**
 * This is an example implementation of a Conversation subclass. It is
 * important to register your intents by adding them to the supportedIntentNames
 * array in the constructor. Your conversation must internally track the current
 * state of the conversation and all state transitions so that it feels natural.
 * The state machine below is the simplest of examples so feel free to create a
 * more robust state-machine object for your more complex needs.
 * 
 * @author Jeffrey Neong
 * @version 1.0
 * 
 */

public class SSUCalendar extends Conversation { 
    //Intent names
    private final static String INTENT_SSUCalendar = "SSUCalendarIntent"; // The general asking for MySSUCalendar
    private final static String INTENT_SSUAcademicC = "SSUAcademicCalendarIntent"; // Going straight to Academic Calendar
    private final static String INTENT_SSUSportsC = "SSUSportingEventsCalendarIntent"; // Going straight to Sporting Events Calendar

    //Slots
    private final static String SLOT_RELATIVE_TIME = "timeOfDay"; // Default to today?

    //State
    private final static Integer STATE_WAITING_WhatCalendar = 100000; // After MySSUCalendar-> list 3 events-> Ask if wanted info from specific calendar
    private final static Integer STATE_WAITING_WhatAcademicType = 100001; // In Academic Calendar-> list 3 upcoming events? -> Ask if wanted info about specific academic area?
    private final static Integer STATE_WAITING_WhatSportType = 100001; // In Sports Calendar -> list 3 upcoming events? -> Ask if wanted specific sport?
    private final static Integer STATE_WAITING_Continue = 100001; // In any calendar-> Alexa keep reading events.

    //Calendars
	private final static int ACADEMIC_CALENDAR = 1;
	private final static int SPORTS_CALENDAR = 2;
	private final static int FAIL_CALENDAR = -1;
	
	//Sports
	private final static String SOCCER = "soccer";
	private final static String VOLLEYBALL = "volleyball";
	private final static String CROSS_COUNTRY = "cross country";
	private final static String GOLF = "golf";
	private final static String TENNIS = "tennis";
	private final static String BASKETBALL = "basketball";
	
	
	//Static Responses (For Demo)
	private final static String STATIC_ACADEMIC_RESPONSE_1 = "October 30th is the last day for full-term widthdrawal and receipt of pro-rated cancellation of fees.";
	private final static String STATIC_ACADEMIC_RESPONSE_2 = "October 31st is the last day of continuation of petition to widthdraw from a class with fee.";
	private final static String STATIC_ACADEMIC_RESPONSE_3 = "November 1st is the ERD deadline of new spring 2017 students.";
	private final static String STATIC_SPORTS_RESPONSE_1   = "There is a men's soccer game at Fresno Pacific on October 5th at 7:30 PM";
	private final static String STATIC_SPORTS_RESPONSE_2   = "There is a women's cross country state invitational in San Francisco on October 7th at 4:15 PM.";
	private final static String STATIC_SPORTS_RESPONSE_3   = "There is a women's volleyball in San Marcos on October 7th at 7:00 PM.";
	
	
	//Static tables (for demo)
	private final static String[][] STATIC_ACADEMIC_TABLE = {
		{"10-30-2016", "Last day for full-term withdrawal and receipt of pro-rated cancellation of fees."},
		{"10-31-2016", "Last day of continuation of petition to withdraw from a class with fee."},
		{"11-01-2016", "ERD deadline of New Spring 2017 students."}
	};
		
	private final static String[][] STATIC_SPORTS_SOCCER = {
		{"10-05-2016", "Sports Event 1"},
                {"10-05-2016", "Sports Event 2"},
                {"10-06-2016", "Sports Event 3"}
	};
        
	private final static String[][] STATIC_SOCCER_TABLE = {
		{"10-05-2016", "Soccer 1"},
                {"10-06-2016", "Soccer 2"},
                {"10-07-2016", "Soccer 3"}
	};
        
        private final static String[][] STATIC_VOLLEYBALL_TABLE = {
		{"10-05-2016", "VOLLEYBALL 1"},
                {"10-06-2016", "VOLLEYBALL 2"},
                {"10-07-2016", "VOLLEYBALL 3"}
	};
        
        private final static String[][] STATIC_CROSS_COUNTRY_TABLE = {
		{"10-05-2016", "CROSS COUNTRY 1"},
                {"10-06-2016", "CROSS COUNTRY 2"},
                {"10-07-2016", "CROSS COUNTRY 3"}
	};
        
        private final static String[][] STATIC_GOLF_TABLE = {
		{"10-05-2016", "GOLF 1"},
                {"10-06-2016", "GOLF 2"},
                {"10-07-2016", "GOLF 3"}
	};
        
        private final static String[][] STATIC_TENNIS_TABLE = {
		{"10-05-2016", "TENNIS 1"},
                {"10-06-2016", "TENNIS 2"},
                {"10-07-2016", "TENNIS 3"}
	};
        
        private final static String[][] STATIC_BASKETBALL_TABLE = {
		{"10-05-2016", "BASKETBALL 1"},
                {"10-06-2016", "BASKETBALL 2"},
                {"10-07-2016", "BASKETBALL 3"}
	};
	//Session state storage key
    //private final static String SESSION_KNOCK_STATE = "knockState";

    public SSUCalendar() {
	super();

	//Add custom intent names for dispatcher use
	supportedIntentNames.add(INTENT_SSUCalendar);
	supportedIntentNames.add(INTENT_SSUAcademicC);
	supportedIntentNames.add(INTENT_SSUSportsC);	
    }


    @Override
    public SpeechletResponse respondToIntentRequest(IntentRequest intentReq, Session session) {
		Intent intent = intentReq.getIntent();
		String intentName = (intent != null) ? intent.getName() : null;
		Map<String, Slot> slots = intent.getSlots();  // get slots
		
		SpeechletResponse response = null;
			
		if (INTENT_SSUCalendar.equals(intentName)) {
			Slot dateSlot = slots.get("Date");
			String dateRequest = dateSlot.getValue();
			response = listThreeEventsIntent(dateRequest,ACADEMIC_CALENDAR);//include time slot, and calendar_type as a parameter) // List the 3 events in the specific time slot given by user.
			// calendar_type = null
	
			// Alexa Ask: "Would you like to continue or refer to a specific caledar?"
			// STATE_Continue?
			// If continue: continueListingIntent(     ) // parameters: time slot, calendar_type = null,
			// Else if academic: go to-> INTENT_SSUAcademicC // come back to this function but with the Academic Calendar being called
			// Else if sports: go to-> INTENT_SSUSportsC // come back to this function but with the Sporting Calendar being called
			// Else if no: End convo
			// Else: "didnt understand"
	
		}
		else if(INTENT_SSUAcademicC.equals(intentName)){
			Slot dateSlot = slots.get("Date");
			String dateRequest = dateSlot.getValue();
			response = listThreeEventsIntent(dateRequest,ACADEMIC_CALENDAR);//include time slot, and calendar_type as parameters// List the 3 academic events in the specific time slot given by user.
			// calendar_type = academic
			// Alexa Ask: "Would you like to continue or find out about the next school holiday, academic date, or financial deadline?"
			// If continue: continueListing Intent(      ) // parameters: time slot, calendar_type = academic
			// Else if school holiday, academic...: listNextSpecificEventIntent(   ); // include time slot, calendar_type, sub_calendar_type)
			// sub_calendar_type: school holiday, academic date, financial deadline
			// Else if no: End convo.
			// Else: "didn't understand".
	
			//response = newTellResponse("You said Potat", false); // from Hello World attempt
		}
		else if(INTENT_SSUSportsC.equals(intentName)) {
			Slot dateSlot = slots.get("Date");
			String dateRequest = dateSlot.getValue();
			response = listThreeEventsIntent(dateRequest,SPORTS_CALENDAR); // include time slot, and calendar_type as parameters // List the 3 sporting events in the specific time slot given by user.
			// Calendar type = sports
			// Alexa Ask: Continue or did you have a specific sport in mind?
			// If continue: continueListingIntent(     ) // parameters: time slot, calendar_type = sports
			// else if: sport type-volleybal, basketball, ect.: listNextSpecificEventIntent(   ) // parameters: time slot, calendar_type, sub_calendar_type)
			// sub_calendar_type: volleyball, basketball, etc.
			// Else: Alexa: "We dont offer that sport at SSU"
		}
		else{
			response = listThreeEventsIntent("",FAIL_CALENDAR);
		}
	
		return response; // figure out what needs to be returned
	}
	
	private SpeechletResponse listThreeEventsIntent(String dateRequest, int calendar_type){ // need perameters: time slot, calendar_type**************
		if (calendar_type == ACADEMIC_CALENDAR){
			return newTellResponse(STATIC_ACADEMIC_RESPONSE_1 + " " + STATIC_ACADEMIC_RESPONSE_2 + " " + STATIC_ACADEMIC_RESPONSE_3, false);
		}
		if (calendar_type == SPORTS_CALENDAR){
			return newTellResponse(STATIC_SPORTS_RESPONSE_1 + " " + STATIC_SPORTS_RESPONSE_2 + " " + STATIC_SPORTS_RESPONSE_3, false);
		}
		
		return newTellResponse("I don't have any information about that.", false);
	}
	
	private void getEvents(String date){
		
				
	}
	
	private String[][] getFromDatabase(String date, int calendar, String sport){
		// Will eventually return all events in a category starting at date and going on for some period of time
		
		if(calendar == ACADEMIC_CALENDAR){
			return STATIC_ACADEMIC_TABLE;
		}
		if(calendar == SPORTS_CALENDAR){
			if(sport.equals("")){
				return null;
			}
			if(sport.equals(SOCCER)){
				return STATIC_SOCCER_TABLE;
			}
			if(sport.equals(VOLLEYBALL)){
				return STATIC_VOLLEYBALL_TABLE;
			}
			if(sport.equals(CROSS_COUNTRY)){
				return STATIC_CROSS_COUNTRY_TABLE;
			}
			if(sport.equals(GOLF)){
				return STATIC_GOLF_TABLE;
			}
			if(sport.equals(TENNIS) ){
				return STATIC_TENNIS_TABLE;
			}
			if(sport.equals(BASKETBALL)){
				return STATIC_BASKETBALL_TABLE;
			}
		}
                return null;
	}
	
	/*
	private SpeechletResponse listNextSpecificEventIntent(    ){ // need parameters: time slot, calendar_type, sub_calendar_type*************
	if (calendar_type == academic){
		if (sub_calendar_type == school_holiday){
		// list the upcoming school holiday based on time slot.
		}
		else if (sub_calendar_type == academic_date){
		// list the upcoming academic date based on time slot;
		}
		else if (sub_calendar_type == financial_deadline){
		// list the upcoming financial deadline.
		}
		// Alexa asks: "Should I continue?"
		// If yes: listNextSpecificEventIntent( time slot +1, academic, sub_calendar_type    )
		// If no: return
	}
	else if (calendar_type == sports){
		if (sub_calendar_type == volleyball){
		// list the upcoming volleyball game.
		}
		else if (sub_calendar_type == basketball){
		// list the upcoming basketball game.
		}
		// either continue else if statements for every sport or figure out more efficient way to handle sport database.
		// Alexa asks: "Should I continue?"
		// If yes: recall this function: ListNextSpecificEventIntent(slot+1, sports, sub_calendar_type)
		// If no: end
	}

	}
    private SpeechletResponse continueListingEventsIntent(      ){ // need parameters: time slot, calendar_type**************************
	// list the next three events.
	// Alexa asks: "Would you like to continue?"
	// if no: return
	// if yes: continueListingEventsIntent(slots+3, calendar_type)
	// if no more events: Alexa says "No more events scheduled" -> return
    }
	*/

    /*    private SpeechletResponse handleGreetingsIntent(IntentRequest intentReq, Session session) {
	SpeechletResponse response = newTellResponse("Hello World", false);
	return response;
	}*/
}


