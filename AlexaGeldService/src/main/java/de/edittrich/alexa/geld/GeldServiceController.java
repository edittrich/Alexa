package de.edittrich.alexa.geld;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Properties;

import org.kapott.hbci.GV.HBCIJob;
import org.kapott.hbci.GV_Result.GVRSaldoReq;
import org.kapott.hbci.callback.HBCICallbackConsole;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.AbstractHBCIPassport;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.status.HBCIExecStatus;
import org.kapott.hbci.structures.Konto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
public class GeldServiceController {
    private static class customHBCICallbackConsole extends HBCICallbackConsole {
	    public void callback(HBCIPassport passport, int reason, String msg, int dataType, StringBuffer retData) {	        
	    }
	    
        public synchronized void status(HBCIPassport passport, int statusTag, Object[] objs) {
		}
    }

    @RequestMapping("/confirmationCode")
    @ResponseBody
    public ResponseEntity<String> confirmationCode(@RequestParam("customerId") String customerId, @RequestParam("confirmationCode") int confirmationCode) {
    	if (confirmationCode == 1234) {
    		return new ResponseEntity<String>("Authorized " + customerId, HttpStatus.OK);
    	} else {
    	    return new ResponseEntity<String>("Unauthorized " + customerId, HttpStatus.UNAUTHORIZED);
    	}
    }
    
    @RequestMapping("/cashAccountBalance")
    @ResponseBody
    public ResponseEntity<String> cashAccountBalance(@RequestParam("customerId") String customerId) {
        Properties properties = new Properties(); 
        properties.put("client.passport.default","PinTan");
        properties.put("client.passport.PinTan.init","0");
        properties.put("log.loglevel.default",Integer.toString(HBCIUtils.LOG_NONE));
        
        properties.put("client.passport.pintan.custom.blz","1");
        properties.put("client.passport.pintan.custom.host","1");
        properties.put("client.passport.pintan.custom.port","1");
        properties.put("client.passport.pintan.custom.hbciversion","1");
        properties.put("client.passport.pintan.custom.userid","1");
        properties.put("client.passport.pintan.custom.pin","1");
        properties.put("client.passport.pintan.custom.currenttanmethod","1");
        HBCIUtils.init(properties, new customHBCICallbackConsole());
        
        if (customerId.equals("Erik")) {
            HBCIUtils.setParam("client.passport.pintan.custom.blz","87070024");    
	        HBCIUtils.setParam("client.passport.pintan.custom.host","fints.deutsche-bank.de/");
	        HBCIUtils.setParam("client.passport.pintan.custom.port","443");
	        HBCIUtils.setParam("client.passport.pintan.custom.hbciversion","300");
	        HBCIUtils.setParam("client.passport.pintan.custom.userid","x");
	        HBCIUtils.setParam("client.passport.pintan.custom.pin","y");
	        HBCIUtils.setParam("client.passport.pintan.custom.currenttanmethod","900");
        } else if (customerId.equals("Tania")) {
            HBCIUtils.setParam("client.passport.pintan.custom.blz","25010030");    
	        HBCIUtils.setParam("client.passport.pintan.custom.host","hbci.postbank.de/banking/hbci.do");
	        HBCIUtils.setParam("client.passport.pintan.custom.port","443");
	        HBCIUtils.setParam("client.passport.pintan.custom.hbciversion","300");
	        HBCIUtils.setParam("client.passport.pintan.custom.userid","x");
	        HBCIUtils.setParam("client.passport.pintan.custom.pin","y");
	        HBCIUtils.setParam("client.passport.pintan.custom.currenttanmethod","901");	        
        } else {
            HBCIUtils.setParam("client.passport.pintan.custom.blz","1");    
	        HBCIUtils.setParam("client.passport.pintan.custom.host","1");
	        HBCIUtils.setParam("client.passport.pintan.custom.port","1");
	        HBCIUtils.setParam("client.passport.pintan.custom.hbciversion","1");
	        HBCIUtils.setParam("client.passport.pintan.custom.userid","1");
	        HBCIUtils.setParam("client.passport.pintan.custom.pin","1");
	        HBCIUtils.setParam("client.passport.pintan.custom.currenttanmethod","1");
        }

        HBCIPassport hbciPassport = AbstractHBCIPassport.getInstance("Custom"); 
        HBCIHandler hbciHandle = new HBCIHandler(hbciPassport.getHBCIVersion(), hbciPassport); 
        
        try {
        	hbciHandle = new HBCIHandler("300",hbciPassport);
            Konto myCashAccount = hbciPassport.getAccounts()[0];
            HBCIJob hbciJob = hbciHandle.newJob("SaldoReq");
            hbciJob.setParam("my", myCashAccount);
            hbciJob.addToQueue();

            HBCIExecStatus status = hbciHandle.execute();

            GVRSaldoReq result = (GVRSaldoReq) hbciJob.getJobResult();
            if (result.isOK()) {
            	String cashAccountBalance = Double.toString((double) result.getEntries()[0].ready.value.getLongValue() / 100);
            	if (cashAccountBalance.substring(cashAccountBalance.length() - 5, cashAccountBalance.length() - 4).equals("0")) {
            		return new ResponseEntity<String>(cashAccountBalance.substring(cashAccountBalance.length() - 4), HttpStatus.OK);
            	} else {
            		return new ResponseEntity<String>(cashAccountBalance.substring(cashAccountBalance.length() - 5), HttpStatus.OK);
            	}
            } else {
            	return new ResponseEntity<String>("HBCI Error " + status.getErrorString(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
    		e.printStackTrace();
        } finally {
            if (hbciHandle!=null) {
            	hbciHandle.close();
            	
            } else if (hbciPassport!=null) {
            	hbciPassport.close();
            }
        }
        return new ResponseEntity<String>("Service Error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
   
}