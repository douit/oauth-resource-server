package com.rkc.zds.resource.web.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.apache.derby.jdbc.EmbeddedDriver;

import com.google.common.base.CharMatcher;

import com.rkc.zds.resource.entity.AddressEntity;
import com.rkc.zds.resource.entity.ContactEntity;
import com.rkc.zds.resource.entity.EMailEntity;
import com.rkc.zds.resource.entity.PhoneEntity;
import com.rkc.zds.resource.entity.WebsiteEntity;
import com.rkc.zds.resource.service.AddressService;
import com.rkc.zds.resource.service.ContactService;
import com.rkc.zds.resource.service.PcmEMailService;
import com.rkc.zds.resource.service.PhoneService;
import com.rkc.zds.resource.service.WebsiteService;

public class PopulateFullNameRunner implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(PopulateFullNameRunner.class);

	private Connection connect = null;
	private Statement statement = null;
	private ResultSet resultSet = null;

	// @Autowired
	ContactService contactService;

	public PopulateFullNameRunner(ContactService contactService) {
		super();
		this.contactService = contactService;
	}

	@Override
	public void run() {

		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			connect = DriverManager.getConnection("jdbc:derby:/_/data/pcm/derbyDB", "PCM", "PCM");

			statement = connect.createStatement();

			String sql = "SELECT CONTACT_ID, FIRSTNAME, LASTNAME FROM PCM_CONTACTS";

			resultSet = statement.executeQuery(sql);

			long rowCount = 0;
			int contactId = 0;
			String firstName = "";
			String lastName = "";
			String fullName = "";
			String test = "";
			boolean isAscii = false;

			while (resultSet.next()) {
				contactId = resultSet.getInt("CONTACT_ID");
				firstName = resultSet.getString("FIRSTNAME");
				lastName = resultSet.getString("LASTNAME");

				fullName = firstName + " " + lastName;

				System.out.println(fullName);

				ContactEntity entity = contactService.getContact(contactId);

				entity.setFullName(firstName + " " + lastName);

				contactService.saveContact(entity);

				test = entity.getId() + "," + firstName + ", " + lastName;

				isAscii = CharMatcher.ascii().matchesAllOf(test);

				if (!isAscii) {
					contactService.deleteContact(contactId);
				}

				rowCount++;
				
				break;
			}

			System.out.println("Done. RowCount:" + rowCount);

		} catch (Exception e) { // TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close();
		}

	}

	private void close() {
		try {
			if (resultSet != null) {
				resultSet.close();
			}
			if (statement != null) {
				statement.close();
			}
			if (connect != null) {
				connect = DriverManager.getConnection("jdbc:derby:/_/data/pcm/derbyDB", "PCM", "PCM;shutdown=true");
				connect.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void runOutOfMempry() {
		long rowCount = 0;

		List<ContactEntity> list = contactService.findAll();
		// ("/home/rcampion/_/data/linkedin/LinkedInComplete.csv"), 65536);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter("/home/rcampion/_/data/linkedin/LinkedInCompleteJunk.csv"),
					65536);
			String str;

			String regexValidCharacters = "[A-Za-z0-9 ]*";

			boolean isAscii = false;

			for (ContactEntity entity : list) {

				String firstName = entity.getFirstName();

				String lastName = entity.getLastName();

				entity.setFullName(firstName + " " + lastName);

				contactService.saveContact(entity);

				str = entity.getId() + "," + firstName + ", " + lastName;

				isAscii = CharMatcher.ascii().matchesAllOf(str);

				if (!isAscii) {
					writer.write(str + "\n");
				}

				// System.out.println(entity.getId() + "," + firstName + " " + lastName);

				rowCount++;

			}

			writer.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logger.warn("rowCount:" + rowCount);

	}

}
