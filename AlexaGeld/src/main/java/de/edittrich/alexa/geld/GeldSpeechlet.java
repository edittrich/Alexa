package de.edittrich.alexa.geld;

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
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeldSpeechlet implements Speechlet {
    private static final Logger log = LoggerFactory.getLogger(GeldSpeechlet.class);

    private static final String REQUESTCUSTOMERID_SLOT = "requestCustomerId";
    private static final String REQUESTCONFIRMATIONCODE_SLOT = "requestConfirmationCode";
    private static final String REQUESTDATE_SLOT = "requestDate";
    
    private static final String urlCashAccounts = "https://simulator-api.db.com:443/gw/dbapi/v1/cashAccounts";
    private static final String urlCashAccountsHbci = "http://edittrich.de:38080/cashAccountBalance";
    private static final String urlCashAccountsFigo = "https://api.figo.me/rest/accounts/A1.1";
    private static final String urlConfirmationCode = "http://edittrich.de:38080/confirmationCode";
    private static final String urlTransactions = "https://simulator-api.db.com:443/gw/dbapi/v1/transactions";
    private static final String urlUserInfo = "https://simulator-api.db.com:443/gw/dbapi/v1/userInfo";
   
	private Boolean confirmationCodeVerified;
	private Boolean accessTokenVerified;
	private Intent openIntent;	
	private String accessToken;
	
	//
	//
	//
	
    @Override
    public void onSessionStarted(final SessionStartedRequest request, final Session session) throws SpeechletException {
        log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());

        if (verifyAccessToken(session)) {
        	confirmationCodeVerified = false;
        	openIntent = null;
        } else {
        	throw new SpeechletException("Missing Access Token");
        }
        
        
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
        
        if ("VerifyConfirmatationCodeIntent".equals(intentName)) {
            if  (verifyConfirmatationCode(intent, session)) {
            	if (openIntent != null) {
            		intent = openIntent;
            		intentName = (intent != null) ? intent.getName() : null;
                } else {
            		String speechText = "Vielen Dank für Deinen Bestätigungscode. Wie kann ich Dir helfen?";
            		String repromptText = "Wie kann ich Dir helfen?";
            		return getSpeechletResponse(speechText, repromptText, true);
            	}
            } else {
        		String speechText = "Dein Bestätigungscode ist leider nicht korrekt. Bitte noch einmal versuchen.";
        		String repromptText = "Ich benötige bitte Deinen Bestätigungscode.";
        		return getSpeechletResponse(speechText, repromptText, true);
            }
        }
        
        if (confirmationCodeVerified) {
	        if ("WhatsMyBalanceIntent".equals(intentName)) {
	            return getBalance(intent, session);
			} else if ("WhatsMyTransactionsIntent".equals(intentName)) {
	            return getTransactions(intent, session);
			} else if ("WhatsMyFigoBalanceIntent".equals(intentName)) {
	            return getFigoBalance(intent, session);
			} else if ("WhatsMyHbciBalanceIntent".equals(intentName)) {
	            return getHbciBalance(intent, session);
	        } else {
	            throw new SpeechletException("Invalid Intent");
	        }
	    } else {
	    	openIntent = request.getIntent();
    		String speechText = "Bitte nenne mir Deinen Bestätigungscode.";
    		String repromptText = "Ich benötige bitte Deinen Bestätigungscode.";
    		return getSpeechletResponse(speechText, repromptText, true);
        }
       
    }

    @Override
    public void onSessionEnded(final SessionEndedRequest request, final Session session) throws SpeechletException {
        log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());
        
        confirmationCodeVerified = false;
		openIntent = null;
    }

    private SpeechletResponse getWelcomeResponse() {
        String speechText = "Willkommen bei der Deutschen Bank! Ich mache Dein Leben leichter.";
        String repromptText = "Wie kann ich Dir helfen?";
		
        return getSpeechletResponse(speechText, repromptText, true);
    }
	
	//
	//
	//
    
    private boolean verifyAccessToken(final Session session) {
    	accessTokenVerified = false;
    	
		accessToken = session.getUser().getAccessToken();
	
		if (accessToken != null) {		
			try {
				ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<SecurityContext>();
				
				//JWKSource<SecurityContext> keySource = new RemoteJWKSet<SecurityContext>(new URL("https://simulator-api.db.com/gw/oidc/jwk"));
				JWKSet jwkSet = JWKSet.load(new File("dbapi.json"));
				JWKSource<SecurityContext> keySource = new ImmutableJWKSet<SecurityContext>(jwkSet);
			
				JWSAlgorithm expectedJWSAlg = JWSAlgorithm.RS256;
				JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<SecurityContext>(expectedJWSAlg, keySource);
				
				jwtProcessor.setJWSKeySelector(keySelector);
			
				SecurityContext ctx = null;
				JWTClaimsSet claimsSet = jwtProcessor.process(accessToken, ctx);
				
				accessTokenVerified = true;
				log.info("accessToken ClaimsSet={}", claimsSet.toJSONObject());
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
		}
		
		return accessTokenVerified;
    	
    }
    
	private boolean verifyConfirmatationCode(final Intent intent, final Session session) {
		confirmationCodeVerified = false;
		
		Map<String, Slot> slots = intent.getSlots();
        Slot requestConfirmationCodeSlot = slots.get(REQUESTCONFIRMATIONCODE_SLOT);
        
        if ((requestConfirmationCodeSlot != null) && (requestConfirmationCodeSlot.getValue() != null))  {
        	String requestConfirmationCode = requestConfirmationCodeSlot.getValue();
		         	
    		String output = getRestResponse(urlUserInfo);
    		
    		JSONObject userInfo = new JSONObject(output);
    		String customerId = userInfo.getString("firstName") + userInfo.getString("lastName");
        	    		          	
			try {
				CloseableHttpClient httpClient = HttpClients.createDefault();
				HttpGet getRequest = new HttpGet(urlConfirmationCode + "?customerId=" + customerId + "&confirmationCode=" + requestConfirmationCode);
				
				HttpResponse response = httpClient.execute(getRequest);				
				log.info("Response from Url={} Response={}", urlConfirmationCode, response.getStatusLine().getStatusCode());
				
				if (response.getStatusLine().getStatusCode() == 200) {
					confirmationCodeVerified = true;
				}
				
				httpClient.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		
        }
        
        return confirmationCodeVerified;

    }
    
    //
	//
	//

	private SpeechletResponse getBalance(final Intent intent, final Session session) {
		log.info("getBalance intent={}, sessionId={}", intent.toString(), session.getSessionId());
		
		String output = getRestResponse(urlCashAccounts);
	
		JSONArray cashAccountsArray = new JSONArray(output);
		JSONObject cashAccountObject = cashAccountsArray.getJSONObject(0);		
		Double cashAccountBalance = cashAccountObject.getDouble("balance");
		
		String speechText = "Dein Kontostand betr\u00E4gt " + cashAccountBalance + " Euro.";
		String repromptText = "Wie kann ich Dir helfen?";
		
		openIntent = null;

        return getSpeechletResponse(speechText, repromptText, true);
    }
	
	//
	
	private SpeechletResponse getTransactions(final Intent intent, final Session session) {
		log.info("getTransactions intent={}, sessionId={}", intent.toString(), session.getSessionId());
		        
		Map<String, Slot> slots = intent.getSlots();
        Slot requestDateSlot = slots.get(REQUESTDATE_SLOT);
        String requestDate = "";
        if ((requestDateSlot != null) && (requestDateSlot.getValue() != null))  {
        	requestDate = requestDateSlot.getValue();
        }
        
        String speechText = "";
		
		String output = getRestResponse(urlTransactions);
		
		double transactionAmount;
		JSONObject transactionObject;
		String transactionCounterPartyName, transactionUsage, transactionBookingDate;

		JSONArray transactions = new JSONArray(output);
		int count = 0;
		int i = 0;
		
		while ((i < transactions.length()) && (i < 3)) {
			transactionObject = transactions.getJSONObject(i);
		 	i++;
			
 		    transactionAmount = transactionObject.getDouble("amount");
 		    transactionCounterPartyName = transactionObject.getString("counterPartyName");
		 	transactionUsage = transactionObject.getString("usage");
		 	transactionBookingDate = transactionObject.getString("bookingDate");
		 	
		 	if ((requestDate.equals("")) || (requestDate.equals(transactionBookingDate))) {
			 	count++; 
				if (transactionAmount > 0) {
					speechText = speechText + "Du hast einen Zahlungseingang von ";
				} else {
					speechText = speechText + "Du hast einen Zahlungsausgang von ";
				}
				speechText = speechText + transactionCounterPartyName + " \u00FCber " + Math.abs(transactionAmount) + " Euro vom " + transactionBookingDate + ". Der Betreff ist " + transactionUsage + ". ";
			}
		}
		
		openIntent = null;
		
		if (count == 0) {
			speechText = "Ich habe für Dich keine Buchungen vorliegen.";
		}		
		String repromptText = "Wie kann ich Dir helfen?";

        return getSpeechletResponse(speechText, repromptText, true);
    }
	
	//
	
	private SpeechletResponse getFigoBalance(final Intent intent, final Session session) {
		log.info("getFigoBalance intent={}, sessionId={}", intent.toString(), session.getSessionId());
		
    	String output = "";
        
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpGet getRequest = new HttpGet(urlCashAccountsFigo);
			getRequest.addHeader("accept", "application/json");
			getRequest.addHeader("authorization", "Bearer ASHWLIkouP2O6_bgA2wWReRhletgWKHYjLqDaqb0LFfamim9RjexTo22ujRIP_cjLiRiSyQXyt2kM1eXU2XLFZQ0Hro15HikJQT_eNeT_9XQ");
			
			HttpResponse response = httpClient.execute(getRequest);
			
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
			output = br.readLine();
			
			httpClient.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONObject figoAccountObject = new JSONObject(output);
		JSONObject figoBalanceObject = figoAccountObject.getJSONObject("balance");
		Double figoBalance = figoBalanceObject.getDouble("balance");
		
		String speechText = "Dein Figo Kontostand betr\u00E4gt " + figoBalance + " Euro.";
		String repromptText = "Wie kann ich Dir helfen?";
		
		openIntent = null;

        return getSpeechletResponse(speechText, repromptText, true);
    }
	
	
	//
	
	private SpeechletResponse getHbciBalance(final Intent intent, final Session session) {
		log.info("getHbciBalance intent={}, sessionId={}", intent.toString(), session.getSessionId());
		
    	String output = "";
    	String speechText = "";
    	
		Map<String, Slot> slots = intent.getSlots();
        Slot requestcustomerId = slots.get(REQUESTCUSTOMERID_SLOT);
        
        if ((requestcustomerId != null) && (requestcustomerId.getValue() != null))  {
        	String requestConfirmationCode = requestcustomerId.getValue();
        	
        	requestConfirmationCode = requestConfirmationCode.substring(0, 1).toUpperCase()
	        		+ requestConfirmationCode.substring(1, requestConfirmationCode.length()).toLowerCase();
        
			try {
				CloseableHttpClient httpClient = HttpClients.createDefault();
				HttpGet getRequest = new HttpGet(urlCashAccountsHbci  + "?customerId=" + requestConfirmationCode);
				getRequest.addHeader("accept", "application/json");
				
				HttpResponse response = httpClient.execute(getRequest);
				
				if (response.getStatusLine().getStatusCode() != 200) {
					throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
				}
	
				BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
				output = br.readLine();
				
				httpClient.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			speechText = "Der Kontostand von " + requestConfirmationCode + " betr\u00E4gt " + output + " Euro.";			
        } else {
        	speechText = "Ich habe Dich leider nicht richtig verstanden. Bitte wiederhole Deine Anfrage.";
        }
        
        String repromptText = "Wie kann ich Dir helfen?";
		openIntent = null;

        return getSpeechletResponse(speechText, repromptText, true);
    }
		
	//
	//
	//

    private SpeechletResponse getSpeechletResponse(String speechText, String repromptText, boolean isAskResponse) {
        SimpleCard card = new SimpleCard();
        card.setTitle("Deine Deutsche Bank!");
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
    
    private String getRestResponse(String url) {
    	
    	String output = "";
    
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpGet getRequest = new HttpGet(url);
			getRequest.addHeader("accept", "application/json");
			getRequest.addHeader("authorization", "Bearer " + accessToken);
			
			HttpResponse response = httpClient.execute(getRequest);
			log.info("Response from Url={} Response={}", url, response.getStatusLine().getStatusCode());
			
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
			output = br.readLine();
			
			httpClient.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return output;
    }
    
}