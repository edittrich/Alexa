package de.edittrich.alexa.pokewelt;

import java.util.HashSet;
import java.util.Set;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

public class PokeweltSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {
    private static final Set<String> supportedApplicationIds = new HashSet<String>();
	
    static {
        supportedApplicationIds.add("amzn1.ask.skill.1f0ec167-c189-4cbd-8df1-50bfb7d98e20");
    }

    public PokeweltSpeechletRequestStreamHandler() {
        super(new PokeweltSpeechlet(), supportedApplicationIds);
    }	
}