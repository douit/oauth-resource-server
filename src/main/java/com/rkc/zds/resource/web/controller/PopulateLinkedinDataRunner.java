package com.rkc.zds.resource.web.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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

public class PopulateLinkedinDataRunner implements Runnable {
	
    private static final Logger logger = LoggerFactory.getLogger(PopulateLinkedinDataRunner.class);

	// @Autowired
	ContactService contactService;

	// @Autowired
	PcmEMailService emailService;

	// @Autowired
	PhoneService phoneService;

	// @Autowired
	AddressService addressService;

	// @Autowired
	WebsiteService websiteService;

	public PopulateLinkedinDataRunner(ContactService contactService,

			PcmEMailService emailService,

			PhoneService phoneService,

			AddressService addressService,

			WebsiteService websiteService) {
		super();
		this.contactService = contactService;

		this.emailService = emailService;

		this.phoneService = phoneService;

		this.addressService = addressService;

		this.websiteService = websiteService;
	}

	@Override
	public void run() {
		int rowCount = 0;
		// try (CSVReader reader = new CSVReader(new
		// FileReader("/_/data/linkedin/LinkedInComplete.csv"))) {
		// File inFile = null;
		// String filePath = "/_/data/linkedin/LinkedInComplete.csv";
		// inFile = new File(filePath);

		long lastKnownPosition = 0;

		try {
			// RandomAccessFile readWriteFileAccess = new RandomAccessFile(inFile, "rw");
			// readWriteFileAccess.seek(lastKnownPosition);

//			BufferedReader reader = 
//					  new BufferedReader(new FileReader("src/main/resources/input.txt")), 16384);	long initialFileLength = inFile.length();

			BufferedReader reader = new BufferedReader(new FileReader("/home/rcampion/_/data/linkedin/LinkedInComplete.csv"), 65536);

			String lineIn = null;

			String[] lineInArray;

			String id = "";
			String fullName = "";
			String email = "";
			String phoneNumber = "";
			String linkedIn = "";
			String title = "";
			String company = "";
			String companyPhone = "";
			String website1 = "";
			String website2 = "";
			String facebook = "";
			String twitter = "";
			String website3 = "";
			String country = "";
			String city = "";
			boolean inBounds;
			int index = 0;
			
			String test = "";
			boolean isAscii = false;

			// skip over first line
			// lineInArray = reader.readNext();
			// readWriteFileAccess.seek(lastKnownPosition);
			// lineIn = readWriteFileAccess.readLine();
			lineIn = reader.readLine();

			// ignore previous pass
			/*
			 * while ((lineIn = reader.readLine()) != null) { lineInArray =
			 * lineIn.split(","); if (0 < lineInArray.length) id = lineInArray[0]; if (1 <
			 * lineInArray.length) fullName = lineInArray[1];
			 * 
			 * if (!fullName.equalsIgnoreCase("") && !fullName.equalsIgnoreCase("?") &&
			 * !fullName.equalsIgnoreCase("null")) { // getting firstName and lastName
			 * String[] nameParts = fullName.split(" ");
			 * 
			 * String firstName = nameParts[0];
			 * 
			 * String lastName = "";
			 * 
			 * for (int i = 1; i < nameParts.length; i++) {
			 * 
			 * lastName += nameParts[i]; if (i < nameParts.length - 1) { lastName += " "; }
			 * 
			 * } System.out.println(id + ", " + firstName + ", " + lastName); }
			 * 
			 * if (id.contains("103459")) break; }
			 */
			String originalStr = "";
			while ((lineIn = reader.readLine()) != null) {

				// while ((lineInArray = reader.readNext()) != null) {
				// while ((lineIn = readWriteFileAccess.readLine()) != null) {

				lineInArray = lineIn.split(",");

				// inBounds = (index >= 0) && (index < lineInArray.length);

				if (0 < lineInArray.length) {
					id = lineInArray[0];
					originalStr = id;
					if (originalStr.startsWith("\"")) {
						originalStr = originalStr.substring(1, originalStr.length());
					}
					if (originalStr.endsWith("\"")) {
						originalStr = originalStr.substring(0, originalStr.length() - 1);
					}
					if (id.contains("null"))
						id = "";
					else
						id = originalStr;
				}

				if (1 < lineInArray.length) {
					fullName = lineInArray[1];
					originalStr = fullName;
					if (originalStr.startsWith("\"")) {
						originalStr = originalStr.substring(1, originalStr.length());
					}
					if (originalStr.endsWith("\"")) {
						originalStr = originalStr.substring(0, originalStr.length() - 1);
					}
					if (fullName.contains("null"))
						fullName = "";
					else
						fullName = originalStr;
				}

				if (2 < lineInArray.length) {
					email = lineInArray[2];
					originalStr = email;
					if (originalStr.startsWith("\"")) {
						originalStr = originalStr.substring(1, originalStr.length());
					}
					if (originalStr.endsWith("\"")) {
						originalStr = originalStr.substring(0, originalStr.length() - 1);
					}
					if (email.contains("null"))
						email = "";
					else
						email = originalStr;
				}

				if (3 < lineInArray.length) {
					phoneNumber = lineInArray[3];
					originalStr = phoneNumber;
					if (originalStr.startsWith("\"")) {
						originalStr = originalStr.substring(1, originalStr.length());
					}
					if (originalStr.endsWith("\"")) {
						originalStr = originalStr.substring(0, originalStr.length() - 1);
					}
					if (phoneNumber.contains("null"))
						phoneNumber = "";
					else
						phoneNumber = originalStr;
				}

				if (4 < lineInArray.length) {
					linkedIn = lineInArray[4];
					originalStr = linkedIn;
					if (originalStr.startsWith("\"")) {
						originalStr = originalStr.substring(1, originalStr.length());
					}
					if (originalStr.endsWith("\"")) {
						originalStr = originalStr.substring(0, originalStr.length() - 1);
					}
					if (linkedIn.contains("null"))
						linkedIn = "";
					else
						linkedIn = originalStr;
				}

				if (5 < lineInArray.length) {
					title = lineInArray[5];
					originalStr = title;
					if (originalStr.startsWith("\"")) {
						originalStr = originalStr.substring(1, originalStr.length());
					}
					if (originalStr.endsWith("\"")) {
						originalStr = originalStr.substring(0, originalStr.length() - 1);
					}
					if (title.contains("null"))
						title = "";
					else
						title = originalStr;
				}

				if (6 < lineInArray.length) {
					company = lineInArray[6];
					originalStr = company;
					if (originalStr.startsWith("\"")) {
						originalStr = originalStr.substring(1, originalStr.length());
					}
					if (originalStr.endsWith("\"")) {
						originalStr = originalStr.substring(0, originalStr.length() - 1);
					}
					if (company.contains("null"))
						company = "";
					else
						company = originalStr;
				}

				if (7 < lineInArray.length) {
					companyPhone = lineInArray[7];
					originalStr = companyPhone;
					if (originalStr.startsWith("\"")) {
						originalStr = originalStr.substring(1, originalStr.length());
					}
					if (originalStr.endsWith("\"")) {
						originalStr = originalStr.substring(0, originalStr.length() - 1);
					}
					if (companyPhone.contains("null"))
						companyPhone = "";
					else
						companyPhone = originalStr;
				}

				if (8 < lineInArray.length) {
					website1 = lineInArray[8];
					originalStr = website1;
					if (originalStr.startsWith("\"")) {
						originalStr = originalStr.substring(1, originalStr.length());
					}
					if (originalStr.endsWith("\"")) {
						originalStr = originalStr.substring(0, originalStr.length() - 1);
					}
					if (website1.contains("null"))
						website1 = "";
					else
						website1 = originalStr;
				}

				if (9 < lineInArray.length) {
					website2 = lineInArray[9];
					originalStr = website2;
					if (originalStr.startsWith("\"")) {
						originalStr = originalStr.substring(1, originalStr.length());
					}
					if (originalStr.endsWith("\"")) {
						originalStr = originalStr.substring(0, originalStr.length() - 1);
					}
					if (website2.contains("null"))
						website2 = "";
					else
						website2 = originalStr;
				}

				if (10 < lineInArray.length) {
					facebook = lineInArray[10];
					originalStr = facebook;
					if (originalStr.startsWith("\"")) {
						originalStr = originalStr.substring(1, originalStr.length());
					}
					if (originalStr.endsWith("\"")) {
						originalStr = originalStr.substring(0, originalStr.length() - 1);
					}
					if (facebook.contains("null"))
						facebook = "";
					else
						facebook = originalStr;
				}

				if (11 < lineInArray.length) {
					twitter = lineInArray[11];
					originalStr = twitter;
					if (originalStr.startsWith("\"")) {
						originalStr = originalStr.substring(1, originalStr.length());
					}
					if (originalStr.endsWith("\"")) {
						originalStr = originalStr.substring(0, originalStr.length() - 1);
					}
					if (twitter.contains("null"))
						twitter = "";
					else
						twitter = originalStr;
				}

				if (12 < lineInArray.length) {
					website3 = lineInArray[12];
					originalStr = website3;
					if (originalStr.startsWith("\"")) {
						originalStr = originalStr.substring(1, originalStr.length());
					}
					if (originalStr.endsWith("\"")) {
						originalStr = originalStr.substring(0, originalStr.length() - 1);
					}
					if (website3.contains("null"))
						website3 = "";
					else
						website3 = originalStr;
				}

				if (13 < lineInArray.length) {
					country = lineInArray[13];
					originalStr = country;
					if (originalStr.startsWith("\"")) {
						originalStr = originalStr.substring(1, originalStr.length());
					}
					if (originalStr.endsWith("\"")) {
						originalStr = originalStr.substring(0, originalStr.length() - 1);
					}
					if (country.contains("null"))
						country = "";
					else
						country = originalStr;
				}

				if (14 < lineInArray.length) {
					city = lineInArray[14];
					originalStr = city;
					if (originalStr.startsWith("\"")) {
						originalStr = originalStr.substring(1, originalStr.length());
					}
					if (originalStr.endsWith("\"")) {
						originalStr = originalStr.substring(0, originalStr.length() - 1);
					}
					if (city.contains("null"))
						city = "";
					else
						city = originalStr;
				}

				logger.info(id + "," + fullName + "," + email + "," + phoneNumber + "," + linkedIn + "," + title
						+ "," + company + "," + companyPhone + "," + website1 + "," + website2 + "," + facebook + ","
						+ twitter + "," + website3 + "," + country + "," + city);

				if (!fullName.isEmpty() && !fullName.equalsIgnoreCase("?")
						&& !fullName.contains("null")) {
					
					// getting firstName and lastName
					String[] nameParts = fullName.split(" ");
					
					
					String firstName = "";
					if (0 < nameParts.length) {
						firstName = nameParts[0];
						originalStr = firstName;
						if (originalStr.startsWith("\"")) {
							originalStr = originalStr.substring(1, originalStr.length());
						}
						if (originalStr.endsWith("\"")) {
							originalStr = originalStr.substring(0, originalStr.length() - 1);
						}
						firstName = originalStr;
					}

					String lastName = "";

					for (int i = 1; i < nameParts.length; i++) {

						lastName += nameParts[i];
						if (i < nameParts.length - 1) {
							lastName += " ";
						}

					}

					originalStr = fullName;
					if (originalStr.startsWith("\"")) {
						originalStr = originalStr.substring(1, originalStr.length());
					}
					if (originalStr.endsWith("\"")) {
						originalStr = originalStr.substring(0, originalStr.length() - 1);
					}
					fullName = originalStr;
					
					originalStr = lastName;
					if (originalStr.startsWith("\"")) {
						originalStr = originalStr.substring(1, originalStr.length());
					}
					if (originalStr.endsWith("\"")) {
						originalStr = originalStr.substring(0, originalStr.length() - 1);
					}
					lastName = originalStr;
					
					test = firstName + ", " + lastName;

					isAscii = CharMatcher.ascii().matchesAllOf(test);
					
					if(isAscii) {
					// System.out.println("firstname:" + firstName + " lastName:" + lastName);
					System.out.println(id + ", " + firstName + ", " + lastName);

					ContactEntity contactDTO = new ContactEntity();
					contactDTO.setCompany(company);
					contactDTO.setEnabled(1);
					
					contactDTO.setFacebook(facebook);
					contactDTO.setFirstName(firstName);
					contactDTO.setFullName(fullName);
					// contactDTO.setId(null)
					contactDTO.setImageURL("https://www.zdslogic.com/download/user.png");
					contactDTO.setLastName(lastName);
					contactDTO.setLinkedin(linkedIn);
					contactDTO.setNotes("");
					contactDTO.setPresenceImageUrl("");
					contactDTO.setSkype("");
					contactDTO.setTitle(title);
					contactDTO.setTwitter(twitter);
					contactDTO.setUserId(0);
					
					Timestamp stamp = new Timestamp(new Date().getTime());

					// SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy MM dd HH.mm.ss");
					// SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
					// This is the format Angular needs
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
					String dateString = dateFormat.format(stamp);

					contactDTO.setCreatedAt(dateString);
					contactDTO.setUpdatedAt(dateString);

					ContactEntity contact = contactService.saveContact(contactDTO);

					// address
					AddressEntity addressDTO = new AddressEntity();
					if (!country.isEmpty()) {
						addressDTO.setContactId(contact.getId());
						addressDTO.setAddressCountry(country);
					}
					if (!city.isEmpty())	{
						addressDTO.setContactId(contact.getId());
						addressDTO.setAddressCity(city);
					}
					if(addressDTO.getContactId()!=0){						
						addressDTO.setAddressKind(0);
						addressService.saveAddress(addressDTO);
					}

					// email					
					if (!email.isEmpty()) {
						EMailEntity emailDTO = new EMailEntity();
						emailDTO.setContactId(contact.getId());
						emailDTO.setEmail(email);
						emailDTO.setEmailKind(0);

						emailService.saveEMail(emailDTO);
					}

					// phone numbers
					if (!phoneNumber.isEmpty()) {
						PhoneEntity phoneDTO = new PhoneEntity();

						phoneDTO.setContactId(contact.getId());
						phoneDTO.setPhone(phoneNumber);
						phoneDTO.setPhoneKind(0);

						phoneService.savePhone(phoneDTO);
					}

					if (!companyPhone.isEmpty()) {
						PhoneEntity phoneDTO = new PhoneEntity();

						phoneDTO.setContactId(contact.getId());
						phoneDTO.setPhone(companyPhone);
						phoneDTO.setPhoneKind(1);

						phoneService.savePhone(phoneDTO);
					}

					// websites
					if (!website1.isEmpty()) {
						WebsiteEntity websiteDTO = new WebsiteEntity();

						websiteDTO.setContactId(contact.getId());
						websiteDTO.setWebsite(website1);
						websiteDTO.setWebsiteKind(0);

						websiteService.saveWebsite(websiteDTO);
					}

					if (!website2.isEmpty()) {
						WebsiteEntity websiteDTO = new WebsiteEntity();

						websiteDTO.setContactId(contact.getId());
						if (!website2.contains("http")) {
							website2 = "http://" + website2;
						}
						websiteDTO.setWebsite(website2);
						websiteDTO.setWebsiteKind(0);

						websiteService.saveWebsite(websiteDTO);
					}

					// websites
					if (!website3.isEmpty()) {
						WebsiteEntity websiteDTO = new WebsiteEntity();

						websiteDTO.setContactId(contact.getId());
						websiteDTO.setWebsite(website3);
						websiteDTO.setWebsiteKind(0);

						websiteService.saveWebsite(websiteDTO);
					}

					rowCount++;
					}
				}
			}
//	    } catch (IndexOutOfBoundsException e) {
//	        // Output expected IndexOutOfBoundsExceptions.
//			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logger.info("rowCount:" + rowCount);

	}

}
