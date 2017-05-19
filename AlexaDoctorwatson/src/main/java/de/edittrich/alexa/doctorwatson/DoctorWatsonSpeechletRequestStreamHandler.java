package de.edittrich.alexa.doctorwatson;

import java.util.HashSet;
import java.util.Set;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

public class DoctorWatsonSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {
    private static final Set<String> supportedApplicationIds = new HashSet<String>();
	
    static {
        supportedApplicationIds.add("amzn1.ask.skill.b5750a84-9305-44e3-8d8a-a0bbbb0db3bd");
    }

    public DoctorWatsonSpeechletRequestStreamHandler() {
        super(new DoctorWatsonSpeechlet(), supportedApplicationIds);
    }	
}