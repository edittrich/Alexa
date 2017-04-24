package org.kapott.hbci.passport;

import org.kapott.hbci.manager.HBCIUtils;

public class HBCIPassportCustom extends HBCIPassportPinTan {

	private static final long serialVersionUID = 2L;

	public HBCIPassportCustom(Object init, int dummy) {
		super(init);
	}

	public HBCIPassportCustom(Object initObject) {
		this(initObject, 0);

		this.setBLZ(HBCIUtils.getParam("client.passport.pintan.custom.blz","not defined"));
		this.setHost(HBCIUtils.getParam("client.passport.pintan.custom.host","not defined"));
		this.setPort(new Integer(HBCIUtils.getParam("client.passport.pintan.custom.port","not defined")));
		this.setHBCIVersion(HBCIUtils.getParam("client.passport.pintan.custom.hbciversion","not defined"));
		this.setUserId(HBCIUtils.getParam("client.passport.pintan.custom.userid","not defined"));
		this.setCustomerId(HBCIUtils.getParam("client.passport.pintan.custom.userid","not defined"));
		this.setPIN(HBCIUtils.getParam("client.passport.pintan.custom.pin","not defined"));
		this.setCurrentTANMethod(HBCIUtils.getParam("client.passport.pintan.custom.currenttanmethod","not defined"));

		this.setCountry("DE");
		this.setCheckCert(false);
		this.setFilterType("Base64");
	}

	public void saveChanges() {
		//
	}

} 