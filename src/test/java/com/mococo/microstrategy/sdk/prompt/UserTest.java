package com.mococo.microstrategy.sdk.prompt;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microstrategy.utils.serialization.EnumWebPersistableState;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectSource;
import com.microstrategy.web.objects.WebObjectsFactory;
import com.microstrategy.web.objects.WebSubscriptionContact;
import com.microstrategy.web.objects.admin.users.WebUser;
import com.microstrategy.webapi.EnumDSSXMLObjectTypes;
import com.mococo.microstrategy.sdk.util.MstrUserUtil;
import com.mococo.microstrategy.sdk.util.MstrUtil;
import com.mococo.web.util.CustomProperties;
import com.mococo.web.util.EncryptUtil;

public class UserTest {
	
	private static final Logger logger = LoggerFactory.getLogger(PromptDaoTest.class);
	
	@Test
	public void testUser() throws Exception {
		WebIServerSession session = null; 
		
		try {
		    WebObjectsFactory factory = WebObjectsFactory.getInstance();
		    session = MstrUtil.connectSession(
		    		CustomProperties.getProperty("mstr.server.name")
		    		, CustomProperties.getProperty("mstr.default.project.name")
		    		, CustomProperties.getProperty("mstr.admin.user.id")
		    		, CustomProperties.getProperty("mstr.admin.user.pwd")
		    	);
		    
		    	WebObjectSource source = session.getFactory().getObjectSource();

				// Get the user for whom the address needs to be created
				WebUser user = (WebUser) source.getObject("E96A7BBD11D4BBCE10004694316DE8A4", EnumDSSXMLObjectTypes.DssXmlTypeUser); // Replace the object with user's UID.
		    	
			
				user.populate(); // need to call populate method before manipulating the object.

				/*
				// Get the current user addresses
				WebSubscriptionUserAddresses _addresses = user.getAddresses();

				// Add new address to the addresses list for the required delivery mode
				WebSubscriptionAddress subAddr = _addresses.addNewAddress(EnumDSSXMLSubscriptionDeliveryType.DssXmlDeliveryTypeEmail);

				// Set the Address Name
				subAddr.setName("MyTest");

				// Set the Physical Address
				subAddr.setValue("myaddr@abc.com");

				// Set the device id to generic email
				subAddr.setDevice("1D2E6D168A7711D4BE8100B0D04B6F0B");

				// Save the address
				subAddr.save();
				_addresses.saveAddress(subAddr);

				// Save the user
				source.save(user);
				*/
				
				System.out.println("user : " + user.getContacts().get(0).getName());
				System.out.println("user : " + user.getContacts().get(0).getType());
				System.out.println("user : " + user.getContacts().get(0).getContactType());
				
//				WebSubscriptionsSource subSource =session.getFactory().getSubscriptionsSource();
				WebSubscriptionContact wsc = (WebSubscriptionContact)source.getObject("DEBA686E4BC8019DB2F6DC8E8DAFD8A3", EnumWebPersistableState.BARE_MINIMAL_STATE_INFO);
//				user.getContacts().add(wsc);
//				source.save(user);
				
				System.out.println("user : " + user.getContacts().size());

		    
		} catch (Exception e) {
			logger.error("!!! error", e);
		} finally {
			MstrUtil.closeISession(session);
		}
	}
	
	
	/**
	 * 사용자 생성 테스트
	 * @throws Exception
	 */
	@Test
	public void createUserTest() throws Exception {
		WebIServerSession session = null; 
		
		try {
		    WebObjectsFactory factory = WebObjectsFactory.getInstance();
		    session = MstrUtil.connectSession(
		    		CustomProperties.getProperty("mstr.server.name")
		    		, CustomProperties.getProperty("mstr.default.project.name")
		    		, CustomProperties.getProperty("mstr.admin.user.id")
		    		, EncryptUtil.decrypt(CustomProperties.getProperty("mstr.admin.user.pwd"))
		    	);
		    
		    	WebObjectSource source = session.getFactory().getObjectSource();

		    	MstrUserUtil.createUser(session, "test", "테스트", "1");
		    
		    	logger.debug("끝");
		} catch (Exception e) {
			logger.error("!!! error", e);
		} finally {
			MstrUtil.closeISession(session);
		}
	}
}
