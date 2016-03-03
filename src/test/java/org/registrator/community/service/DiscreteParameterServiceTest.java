package org.registrator.community.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.registrator.community.dao.DiscreteParameterRepository;
import org.registrator.community.entity.DiscreteParameter;
import org.registrator.community.entity.ResourceType;
import org.registrator.community.service.impl.DiscreteParameterServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DiscreteParameterServiceTest {
	
	
	private Logger logger = LoggerFactory.getLogger(DiscreteParameterServiceTest.class);
	
	
	//test  data
	private final Integer ID = 1;
	private final String TYPENAME = "TestTypeName";
	private final String DESCRIPTION = "TEST_Description";
	private final String UNITNAME = "TEST_UnitName";
	private ResourceType resourceType = new ResourceType(TYPENAME);
	private final DiscreteParameter discreteParameter = new DiscreteParameter(resourceType, DESCRIPTION, UNITNAME);
	private List<DiscreteParameter>  listDiscreteParameter = Arrays.asList(discreteParameter);
	//end test data
	
	@InjectMocks
	private  DiscreteParameterService discreteParameterService = new DiscreteParameterServiceImpl();
	
	@Mock
	private  DiscreteParameterRepository  discreteParameterRepository;
	
	@BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
    }
	
	@Test
	public void findById(){
		logger.info("start: DiscreteParameterServiceTest.findById ");
		Mockito.when(discreteParameterRepository.findByDiscreteParameterId(ID)).thenReturn(discreteParameter);
		DiscreteParameter discreteParameter = discreteParameterService.findById(ID);
		Assert.assertEquals(discreteParameter.getResourceType(), resourceType );
		Assert.assertEquals(discreteParameter.getDescription(),DESCRIPTION);
		Assert.assertEquals(discreteParameter.getUnitName(), UNITNAME);
		logger.info("end: DiscreteParameterServiceTest.findById ");
	}
	@Test
	public void findAllByResourceType(){
		logger.info("start: DiscreteParameterServiceTest.findAllByResourceType ");
		Mockito.when(discreteParameterRepository.findAllByResourceType(resourceType)).thenReturn(listDiscreteParameter);
		List<DiscreteParameter> expectedList = discreteParameterService.findAllByResourceType(resourceType);
		Assert.assertTrue(expectedList.size()>0);
		Assert.assertTrue(expectedList.get(0).equals(discreteParameter));
		logger.info("end: DiscreteParameterServiceTest.findAllByResourceType ");
	}

}
