package de.edittrich.alexa.pokewelt;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazon.speech.ui.SsmlOutputSpeech;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import de.edittrich.alexa.pokewelt.Pokemon;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PokeweltSpeechlet implements Speechlet {	
    private static final Logger log = LoggerFactory.getLogger(PokeweltSpeechlet.class);
    
    private static final String REQUESTPOKEMON_SLOT = "requestPokemon";
    private static final String REQUESTPOKEMONTYPE_SLOT = "requestPokemonType";
    
    private AmazonDynamoDB dynamoDB;
	
	//
	//
	//
    
    private void initializeComponents() {
        if (dynamoDB == null) {
		    dynamoDB = AmazonDynamoDBClientBuilder.standard()
		        	.withRegion(Regions.US_EAST_1)
		        	.withCredentials(new DefaultAWSCredentialsProviderChain())
		        	.build();		    
        }
    }
    
    //
    //
    //
	
    @Override
    public void onSessionStarted(final SessionStartedRequest request, final Session session) throws SpeechletException {
        log.info("onSessionStarted version from=11:27 requestId=[{}], sessionId=[{}]", request.getRequestId(), session.getSessionId());
        
        initializeComponents();		
    }

    @Override
    public SpeechletResponse onLaunch(final LaunchRequest request, final Session session) throws SpeechletException {
        log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());
        
		return getWelcomeResponse();
    }

    @Override
    public SpeechletResponse onIntent(final IntentRequest request, final Session session) throws SpeechletException {
    	initializeComponents();
    	
    	Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;
        
        log.info("onIntent requestId=[{}], sessionId=[{}] intentName=[{}]", request.getRequestId(), session.getSessionId(), intentName);

		if ("WhatsPokemonIntent".equals(intentName)) {
			return getPokemon(intent, session);
		} else if ("WhatsPokemonTypeIntent".equals(intentName)) {
			return getPokemonType(intent, session);			
		} else if ("WhatsPokemonGoIntent".equals(intentName)) {
			return getPokemonGo(intent, session);
		} else if ("WhatsFavPokemonIntent".equals(intentName)) {
			return getFavouritePokemon(intent, session);
		} else if ("AMAZON.StopIntent".equals(intentName)) {
			return amazonStop();			
		} else {
			throw new SpeechletException("Invalid Intent");
		}
      
    }

    @Override
    public void onSessionEnded(final SessionEndedRequest request, final Session session) throws SpeechletException {
        log.info("onSessionEnded requestId=[{}], sessionId=[{}]", request.getRequestId(), session.getSessionId());
        
    }
    
    //
    //
    //

    private SpeechletResponse getWelcomeResponse() {
        String speechText = "Willkommen in der welt der pokemons.";
        String repromptText = "Was möchtest Du über die Welt der Pokemons wissen?";
		
        return getSpeechletResponse(speechText, false, repromptText, false, true);
    }
    
    //
    
    private SpeechletResponse amazonStop() {
        String speechText = "";
        String repromptText = "Was willst Du sonst noch über die Welt der Pokemons wissen?";
		
        return getSpeechletResponse(speechText, false, repromptText, false, true);
    }
    
	//
	//
	//
    
	private SpeechletResponse getPokemon(final Intent intent, final Session session) {
		
		Map<String, Slot> slots = intent.getSlots();
        Slot requestPokemonSlot = slots.get(REQUESTPOKEMON_SLOT);
        String requestPokemon = "";
        if ((requestPokemonSlot != null) && (requestPokemonSlot.getValue() != null))  {
        	requestPokemon = requestPokemonSlot.getValue();
        }
        
        log.info("getPokemon Pokemon=[{}]", requestPokemon);

        String speechText = "<speak>";
        
        if (requestPokemon.length() == 0) {
	        speechText = speechText + "Leider habe ich kein Pokemon erkannt.";
        } else {
	        requestPokemon = requestPokemon.substring(0, 1).toUpperCase()
	        		+ requestPokemon.substring(1, requestPokemon.length()).toLowerCase();
	        
	        HashMap<String, String> expressionAttribute
	    			= new HashMap<String, String>();
	        expressionAttribute.put("#n", "Name");
	        
	        Map<String, AttributeValue> dynamoDBFilterExpression = new HashMap<String, AttributeValue>();
	        dynamoDBFilterExpression.put(":val1", new AttributeValue().withS(requestPokemon));
	
	        DynamoDBScanExpression dynamoDBScanExpression = new DynamoDBScanExpression()
	                .withFilterExpression("#n = :val1")
	            	.withExpressionAttributeNames(expressionAttribute)
	                .withExpressionAttributeValues(dynamoDBFilterExpression);
	        
	        DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(dynamoDB);
	        List<Pokemon> dynamoDScanResultMapper = dynamoDBMapper.scan(Pokemon.class, dynamoDBScanExpression);
	        
	        if (dynamoDScanResultMapper.size() == 0) {
	        	speechText = speechText + "Es gibt kein Pokemon mit Namen " + requestPokemon + ".";
	        } else {
				Pokemon pokemon = dynamoDScanResultMapper.get(0);

				speechText = speechText + "Das " + requestPokemon + " ist ein tolles Pokemon.";

				if (pokemon.getTypes() != null) {
	        		speechText = speechText + " Es ist vom Typ ";
	        		
	                int count = 0;
	                Iterator<String> it = pokemon.getTypes().iterator();
	                while (it.hasNext())
	                {
	                	count++;
	                	it.next();
	                }
	                
	                int i = 0;
	                it = pokemon.getTypes().iterator();
	                while (it.hasNext())
	                {
	                	i++;
	                	speechText = speechText + (String) it.next();
	                	if (i == count-1) {
	                		speechText = speechText + " und ";            		
	                	} else if (i < count-1) {
	                		speechText = speechText + ", ";            		
	                	}
	                }
	                
	        		speechText = speechText + ".";
	        	}
				
				if (pokemon.getEvaluations() != null) {
					speechText = speechText + " Es hat die Weiterentwicklungen ";
					
	                int count = 0;
	                Iterator<Evaluation> it = pokemon.getEvaluations().iterator();
	                while (it.hasNext())
	                {
	                	count++;
	                	it.next();
	                }
	                
	                int i = 0;
	                it = pokemon.getEvaluations().iterator();
	                while (it.hasNext())
	                {
	                	i++;
	                	speechText = speechText + (String) it.next().getName();
	                	if (i == count-1) {
	                		speechText = speechText + " und ";            		
	                	} else if (i < count-1) {
	                		speechText = speechText + ", ";            		
	                	}
	                }
	                
	                speechText = speechText + ".";
				}
	        	
				if (pokemon.getWP() > 0) {
	        		speechText = speechText + " Es kann einen maximalen WP von " + pokemon.getWP() + " erreichen.";
	        	}
	        	
				if (pokemon.getKP() > 0) {
	        		speechText = speechText + " Der KP beträgt <say-as interpret-as=\"cardinal\">"
	        				+ pokemon.getKP() + "</say-as>.";
	        	}
	        	
				if (pokemon.getAngriff() > 0) {
	        		speechText = speechText + " Es hat einen Angriff von <say-as interpret-as=\"cardinal\">"
	        				+ pokemon.getAngriff() + "</say-as>.";
	        	}
	        	
				if (pokemon.getVerteidigung() > 0) {
	        		speechText = speechText + " Die Verteidigung beträgt <say-as interpret-as=\"cardinal\">"
	        				+ pokemon.getVerteidigung() + "</say-as>.";
	        	}
	        }
        }

        speechText = speechText + "</speak>";
        String repromptText = "Was willst Du sonst noch über die Welt der Pokemons wissen?";
        return getSpeechletResponse(speechText, true, repromptText, false, true);
    }
	
	//
	
	private SpeechletResponse getPokemonType(final Intent intent, final Session session) {
		
		Map<String, Slot> slots = intent.getSlots();
        Slot requestPokemonSlot = slots.get(REQUESTPOKEMONTYPE_SLOT);
        String requestPokemonType = "";
        if ((requestPokemonSlot != null) && (requestPokemonSlot.getValue() != null))  {
        	requestPokemonType = requestPokemonSlot.getValue();
        }
        
        log.info("getPokemonType PokemonType=[{}]", requestPokemonType);
        
        if (requestPokemonType.equals("electro")) requestPokemonType = "elektro";
        
        String speechText;
        
        if (requestPokemonType.length() == 0) {
	        speechText = "Leider habe ich keinen Pokemontyp erkannt.";
        } else {
	        requestPokemonType = requestPokemonType.substring(0, 1).toUpperCase()
	        		+ requestPokemonType.substring(1, requestPokemonType.length()).toLowerCase();
	        
	        Map<String, AttributeValue> dynamoDBFilterExpression = new HashMap<String, AttributeValue>();
	        dynamoDBFilterExpression.put(":val1", new AttributeValue().withS(requestPokemonType));
	
	        DynamoDBScanExpression dynamoDBScanExpression = new DynamoDBScanExpression()
		            .withFilterExpression("contains(Types, :val1)")
		            .withExpressionAttributeValues(dynamoDBFilterExpression);
	        
	        DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(dynamoDB);
	        List<Pokemon> dynamoDScanResultMapper = dynamoDBMapper.scan(Pokemon.class, dynamoDBScanExpression);
	        
	        speechText = "";
	        
	        if (dynamoDScanResultMapper.size() == 0) {
	        	speechText = "Es gibt keine Pokemon vom Typ " + requestPokemonType + ".";
	        } else {
	        	if (dynamoDScanResultMapper.size() == 1) {
	        		speechText = "Es gibt ein Pokemon vom Typ " + requestPokemonType + ": ";
	        	} else {
	        		speechText = "Es gibt " + dynamoDScanResultMapper.size() + " Pokemon vom Typ " + requestPokemonType + ": ";
	        	}    		 
	    		
	    		int i = 0;
	            for (Pokemon pokemon : dynamoDScanResultMapper) {
	            	log.info("getPokemonType Pokemon=[{}]", pokemon);
	             	speechText = speechText + pokemon.getName();
	             	i++;
	             	if (i == dynamoDScanResultMapper.size()-1) {
	             		speechText = speechText +  " und ";
	             	} else if (i < dynamoDScanResultMapper.size()-1) {
	             		speechText = speechText +  ", ";
	             	}
	            }
	            
	            speechText = speechText +  ".";
	        }
        }
        
        String repromptText = "Was willst Du sonst noch über die Welt der Pokemons wissen?";
        return getSpeechletResponse(speechText, false, repromptText, false, true);
    }

	
	//

	private SpeechletResponse getPokemonGo (final Intent intent, final Session session) {
		
		String speechText = "Pokemon Go ist eine App, welche 2016 für Smartphones erschienen ist. In der App müssen die Spieler Pokemons in der realen Welt finden. ";
		String repromptText = "Was willst Du sonst noch über die Welt der Pokemons wissen?";

        return getSpeechletResponse(speechText, false, repromptText, false, true);
    }
	
	//
	
	private SpeechletResponse getFavouritePokemon(final Intent intent, final Session session) {
		
		String speechText = "Mein Lieblingspokemon ist Evoli.";
		String repromptText = "Was willst Du sonst noch über die Welt der Pokemons wissen?";

        return getSpeechletResponse(speechText, false, repromptText, false, true);
    }
	
	//
	//
	//

    private SpeechletResponse getSpeechletResponse(String speechText, boolean isOutputSsml, 
    		String repromptText, boolean isRepromptSsml,
    		boolean isAskResponse) {
    	SimpleCard card = new SimpleCard();
        card.setTitle("Die Welt der Pokemons!");
        card.setContent(speechText);
        
        OutputSpeech outputSpeech, repromptOutputSpeech;
        
    	if (isOutputSsml) {
            outputSpeech = new SsmlOutputSpeech();
            ((SsmlOutputSpeech) outputSpeech).setSsml(speechText);
        } else {
            outputSpeech = new PlainTextOutputSpeech();
            ((PlainTextOutputSpeech) outputSpeech).setText(speechText);
        }
    	
        if (isRepromptSsml) {
            repromptOutputSpeech = new SsmlOutputSpeech();
            ((SsmlOutputSpeech) repromptOutputSpeech).setSsml(repromptText);
        } else {
            repromptOutputSpeech = new PlainTextOutputSpeech();
            ((PlainTextOutputSpeech) repromptOutputSpeech).setText(repromptText);
        }

        if (isAskResponse) {
            Reprompt reprompt = new Reprompt();
            reprompt.setOutputSpeech(repromptOutputSpeech);
            return SpeechletResponse.newAskResponse(outputSpeech, reprompt, card);
        } else {     	
            return SpeechletResponse.newTellResponse(outputSpeech, card);
        }
    }
    
}