package org.registrator.community.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.registrator.community.config.LoggingConfig;
import org.registrator.community.config.root.SpringRootConfig;
import org.registrator.community.config.root.TestingConfiguration;
import org.registrator.community.entity.User;
import org.registrator.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("testing")
@ContextConfiguration(classes={TestingConfiguration.class,LoggingConfig.class,SpringRootConfig.class})
public class UserServiceTest {
	
	@Autowired
	UserService userService;
	
	@Test
    public void testFindUserByLoginService() {
		User user=userService.getUserByLogin("user");
		Assert.assertEquals("Compare user.mail in service", "ivan@gmail.com",user.getEmail());
        assertTrue("True",true);
    }
}
