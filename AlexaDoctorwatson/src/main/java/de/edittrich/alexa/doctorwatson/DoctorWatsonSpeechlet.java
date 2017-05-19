package de.edittrich.alexa.doctorwatson;

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
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DoctorWatsonSpeechlet implements Speechlet {
    private static final Logger log = LoggerFactory.getLogger(DoctorWatsonSpeechlet.class);
    
    private static final String WORDA_SLOT = "WordA";
    private static final String WORDB_SLOT = "WordB";
    private static final String WORDC_SLOT = "WordC";
    private static final String WORDD_SLOT = "WordD";
    private static final String WORDE_SLOT = "WordE";
    private static final String WORDF_SLOT = "WordF";
    private static final String WORDG_SLOT = "WordG";
    private static final String WORDH_SLOT = "WordH";
    private static final String WORDI_SLOT = "WordI";
    private static final String WORDJ_SLOT = "WordJ";    
    
    private static final String watsonUrl = "https://gateway-fra.watsonplatform.net/conversation/api/v1/workspaces/428c08b3-2b4d-4946-b620-6339085219f4/message?version=2017-02-03";
    private static final String watsonAuth = "8707a68c-050c-4639-9891-3803c4d985ef:0pN8GSlncmC2";
	
	//
	//
	//
	
    @Override
    public void onSessionStarted(final SessionStartedRequest request, final Session session) throws SpeechletException {
        log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());
        
    }

    @Override
    public SpeechletResponse onLaunch(final LaunchRequest request, final Session session) throws SpeechletException {
        log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());
        
		return getWelcomeResponse();
    }

    @Override
    public SpeechletResponse onIntent(final IntentRequest request, final Session session) throws SpeechletException {
        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;
        
        log.info("onIntent requestId={}, sessionId={} intentName={}", request.getRequestId(), session.getSessionId(), intentName);
   
		if ("WordAIntent".equals(intentName)) {
			return getWordIntent(intent, session);
		} else if ("WordBIntent".equals(intentName)) {
			return getWordIntent(intent, session);
		} else if ("WordCIntent".equals(intentName)) {
			return getWordIntent(intent, session);
		} else if ("WordDIntent".equals(intentName)) {
			return getWordIntent(intent, session);
		} else if ("WordEIntent".equals(intentName)) {
			return getWordIntent(intent, session);			
		} else if ("WordFIntent".equals(intentName)) {
			return getWordIntent(intent, session);
		} else if ("WordGIntent".equals(intentName)) {
			return getWordIntent(intent, session);
		} else if ("WordHIntent".equals(intentName)) {
			return getWordIntent(intent, session);
		} else if ("WordIIntent".equals(intentName)) {
			return getWordIntent(intent, session);
		} else if ("WordJIntent".equals(intentName)) {
			return getWordIntent(intent, session);			
		} else {
			throw new SpeechletException("Invalid Intent");
		}
    
    }

    @Override
    public void onSessionEnded(final SessionEndedRequest request, final Session session) throws SpeechletException {
        log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());
 
    }
	
	//
	//
    //

    private SpeechletResponse getWelcomeResponse() {
        String speechText = "Willkommen bei Doctor Watson! Was willst Du wissen?";
        String repromptText = "Was willst Du wissen?";
		
        return getSpeechletResponse(speechText, repromptText, true);
    }

	//
    
	private SpeechletResponse getWordIntent(final Intent intent, final Session session) {
		log.info("getWordIntent intent={}, sessionId={}", intent.toString(), session.getSessionId());
		
		Map<String, Slot> slots = intent.getSlots();
		String requestText = new String("");
		if (slots.containsKey(WORDA_SLOT)) {
			requestText = requestText + slots.get(WORDA_SLOT).getValue();
		}
		if (slots.containsKey(WORDB_SLOT)) {
			requestText = requestText + slots.get(WORDB_SLOT).getValue();
		}
		if (slots.containsKey(WORDC_SLOT)) {
			requestText = requestText + slots.get(WORDC_SLOT).getValue();
		}
		if (slots.containsKey(WORDD_SLOT)) {
			requestText = requestText + slots.get(WORDD_SLOT).getValue();
		}
		if (slots.containsKey(WORDE_SLOT)) {
			requestText = requestText + slots.get(WORDE_SLOT).getValue();
		}
		if (slots.containsKey(WORDF_SLOT)) {
			requestText = requestText + slots.get(WORDF_SLOT).getValue();
		}
		if (slots.containsKey(WORDG_SLOT)) {
			requestText = requestText + slots.get(WORDG_SLOT).getValue();
		}
		if (slots.containsKey(WORDH_SLOT)) {
			requestText = requestText + slots.get(WORDH_SLOT).getValue();
		}
		if (slots.containsKey(WORDI_SLOT)) {
			requestText = requestText + slots.get(WORDI_SLOT).getValue();
		}
		if (slots.containsKey(WORDJ_SLOT)) {
			requestText = requestText + slots.get(WORDJ_SLOT).getValue();
		}

		String speechText = getWatsonResponse(requestText);
		String repromptText = "Wie kann ich Dir helfen?";

        return getSpeechletResponse(speechText, repromptText, true);
    }
		
	//
	//
	//

    private SpeechletResponse getSpeechletResponse(String speechText, String repromptText, boolean isAskResponse) {
        SimpleCard card = new SimpleCard();
        card.setTitle("Doctor Watson");
        card.setContent(speechText);

        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        if (isAskResponse) {
            PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
            repromptSpeech.setText(repromptText);
            Reprompt reprompt = new Reprompt();
            reprompt.setOutputSpeech(repromptSpeech);

            return SpeechletResponse.newAskResponse(speech, reprompt, card);
        } else {
            return SpeechletResponse.newTellResponse(speech, card);
        }
    }
    
    //
    //
    //
    
    private String getWatsonResponse(String input) {
    	
    	String output = "";
    
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost postRequest = new HttpPost(watsonUrl);
			postRequest.setHeader("accept", "application/json");
			postRequest.setHeader("authorization", "Basic " + Base64.getEncoder().encodeToString(watsonAuth.getBytes()));
			
			StringEntity requestEntity = new StringEntity("{\"input\": {\"text\": \"" + input + "\"}}", ContentType.APPLICATION_JSON);
			postRequest.setEntity(requestEntity);
			
			HttpResponse response = httpClient.execute(postRequest);
			log.info("Response from Url={} Response={}", watsonUrl, response.getStatusLine().getStatusCode());
			
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
			String responseContent = br.readLine();

			JSONObject responseContentJson = new JSONObject(responseContent);
			JSONObject outputJson = responseContentJson.getJSONObject("output");
			JSONArray textJson = outputJson.getJSONArray("text");
			output = textJson.getString(0);
			
			log.info("Content from Url={} Output={}", watsonUrl, output);			
			
			httpClient.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return output;
    }
    
}