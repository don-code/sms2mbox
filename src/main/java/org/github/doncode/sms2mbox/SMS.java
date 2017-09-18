package org.github.doncode.sms2mbox;

import java.util.Date;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

/**
 * Represents an SMS message.
 */
public class SMS {
	private Address address;
	private AddressType addressType;
	private String body;
	private Date date;
	private String subject;
	private static Address myAddress = null;

	public SMS(String address, int addressType, String body, long date, String subject) throws AddressException {
		this.address = new InternetAddress(address);
		this.addressType = AddressType.fromOrdinal(addressType);
		this.body = body;
		this.date = new Date(date);
		this.subject = subject;

		if(myAddress == null) {
			myAddress = new InternetAddress("me");
		}
	}
	
	public AddressType getAddressType() {
		return addressType;
	}
	
	public Message toMessage(Session s) throws MessagingException {
		Message m = new MimeMessage(s);

		switch(addressType) {
		case INBOUND:
			m.setFrom(address);
			m.setRecipient(RecipientType.TO, myAddress);
			break;
		case OUTBOUND:
			m.setFrom(myAddress);
			m.setRecipient(RecipientType.TO, address);
			break;
		}

		m.setContent(body, "text/plain");
		m.setSentDate(date);
		m.setSubject(subject);

		return m;
	}
}
