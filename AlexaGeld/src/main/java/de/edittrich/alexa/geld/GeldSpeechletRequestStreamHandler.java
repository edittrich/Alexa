package de.edittrich.alexa.geld;

import java.util.HashSet;
import java.util.Set;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

public class GeldSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {
    private static final Set<String> supportedApplicationIds = new HashSet<String>();
	
    static {
        supportedApplicationIds.add("amzn1.ask.skill.3256200f-27cb-4e8e-a77c-affaf60383c7");
    }

    public GeldSpeechletRequestStreamHandler() {
        super(new GeldSpeechlet(), supportedApplicationIds);
    }	
}