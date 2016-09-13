package com.neong.voice.example;

import com.amazon.speech.slu.Intent;
	
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.neong.voice.model.base.Conversation;

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

public class HelloWorldConversation extends Conversation {
	//Intent names
	private final static String INTENT_START = "GreetingIntent";


	//Slots
	//private final static String SLOT_RELATIVE_TIME = "timeOfDay";

	//State
	//private final static Integer STATE_WAITING_WHO_DER = 100000;
	//private final static Integer STATE_WAITING_DR_WHO = 100001;

	//Session state storage key
	private final static String SESSION_KNOCK_STATE = "knockState";

	public HelloWorldConversation() {
		super();
		
		//Add custom intent names for dispatcher use
		supportedIntentNames.add(INTENT_START);

	}


	@Override
	public SpeechletResponse respondToIntentRequest(IntentRequest intentReq, Session session) {
		Intent intent = intentReq.getIntent();
		String intentName = (intent != null) ? intent.getName() : null;
		SpeechletResponse response = null;
		
		if (INTENT_START.equals(intentName)) {
			response = handleGreetingsIntent(intentReq, session);
        }
		else {
			response = newTellResponse("No", false);
		}
		
		return response;
	}

	
	private SpeechletResponse handleGreetingsIntent(IntentRequest intentReq, Session session) {
		SpeechletResponse response = newTellResponse("Hello World", false);
		return response;	
	}
}

	
	