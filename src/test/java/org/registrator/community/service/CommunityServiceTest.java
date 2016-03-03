package org.registrator.community.service;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.registrator.community.dao.CommunityRepository;
import org.registrator.community.entity.TerritorialCommunity;
import org.registrator.community.service.impl.CommunityServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CommunityServiceTest {
	
	private Logger logger = LoggerFactory.getLogger(CommunityServiceTest.class);
	
	// test data
	String NAME = "TestName";
	TerritorialCommunity territorialCommunity = new TerritorialCommunity(NAME);
	
	//end test data
	
	@InjectMocks
	private CommunityService communityServiceImpl = new CommunityServiceImpl();
	
	@Mock
	private CommunityRepository communityRepository;

	@BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
    }
	
	
	@Test
	public void findAll(){
		
	}
	
	@Test
	public void findByName(){
		
	}
	
	@Test
	public void addCommunity(){
		
	}
	
	@Test
	public void findById(){
		
	}
	
	@Test
	public void deleteCommunity(){
		
	}
	
	@Test
	public void findAllByAsc(){
		
	}

}
