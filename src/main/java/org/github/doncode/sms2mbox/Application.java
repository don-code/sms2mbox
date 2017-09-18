package org.github.doncode.sms2mbox;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

public class Application {
	// for JavaMail
	private static Session session;
	private static Store store;
	private static Folder inbox;
	private static Folder sent;
	
	// for SQLite
	private static Connection connection;
	
	public static void main(String[] args) throws Exception {
		if(args.length != 3) {
			printUsage();
			System.exit(1);
		}
		
		initDB(args[0]);
		initMailbox(args[1], args[2]);
		
		ResultSet rs = getAllSMS();
		while(rs.next()) {
			SMS sms = new SMS(
				rs.getString("address"),
				rs.getInt("type"),
				rs.getString("body"),
				rs.getLong("date"),
				rs.getString("subject")
			);
			
			switch(sms.getAddressType()) {
			case INBOUND:
				inbox.appendMessages(new Message[] {sms.toMessage(session)});
				break;
			case OUTBOUND:
				sent.appendMessages(new Message[] {sms.toMessage(session)});
				break;
			}
		}
		
		closeMailbox();
		closeDB();
	}
	
	private static ResultSet getAllSMS() throws SQLException {
		// id=1 == received, id=2 == sent. others exist, but their meaning is yet unknown
		PreparedStatement s = connection.prepareStatement("select address, date, subject, body, type from sms where type in (1,2);");
		return s.executeQuery();
	}
	
	private static void closeDB() throws SQLException {
		connection.close();
	}
	
	private static void closeMailbox() throws MessagingException {
		store.close();
	}
	
	private static void initDB(String dbPath) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath.replace("\\", "\\\\"));
	}
	
	private static void initMailbox(String inboxPath, String sentPath) throws MessagingException {
		session = Session.getDefaultInstance(new Properties());
		store = session.getStore("mbox");
		store.connect();
		inbox = store.getFolder(inboxPath);
		sent = store.getFolder(sentPath);
	}
	
	private static void printUsage() {
		System.out.println("sms2mbox - translates Android SMS databases to Unix mbox format");
		System.out.println("USAGE: sms2mbox <sqlite DB> <inbox mbox file> <sent mbox file>");
	}
}
