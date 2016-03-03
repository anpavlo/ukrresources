package org.registrator.community.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.registrator.community.config.root.SpringRootConfig;
import org.registrator.community.config.root.TestingConfiguration;
import org.registrator.community.entity.DiscreteParameter;
import org.registrator.community.entity.TerritorialCommunity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.junit.Assert;


@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("testing")
@ContextConfiguration(classes={TestingConfiguration.class,SpringRootConfig.class})
public class Sample {
	
	 @Autowired
	 DiscreteParameterService discreteParameterService;
	 @Autowired
	 CommunityService communityService;
	
	@Test
	public void test1(){
		//DiscreteParameter discreteParameter = discreteParameterService.findById(2);
		TerritorialCommunity newTerritorialCommunity = new TerritorialCommunity("TEST");
		communityService.addCommunity(newTerritorialCommunity);
		TerritorialCommunity territorialCommunity = communityService.findById(1);
		//Long expectedId=(long) 1;
		//Long id = (long) territorialCommunity.getTerritorialCommunityId();
		Assert.assertEquals(new Integer(1), territorialCommunity.getTerritorialCommunityId());
		//Assert.assertEquals(expectedId, id);
		//Assert.assertEquals(DiscreteParameter.class, discreteParameter.getClass());
	}
}


