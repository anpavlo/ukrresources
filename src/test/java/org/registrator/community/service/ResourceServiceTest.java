package org.registrator.community.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.registrator.community.config.LoggingConfig;
import org.registrator.community.config.root.SpringRootConfig;
import org.registrator.community.config.root.TestingConfiguration;
import org.registrator.community.dto.ResourceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.junit.Assert;

import javax.transaction.Transactional;

/**
 * Unit test for Resource service methods
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("testing")
@Transactional
@ContextConfiguration(classes={TestingConfiguration.class,LoggingConfig.class,SpringRootConfig.class})
public class ResourceServiceTest {

    @Autowired
    ResourceService resourceService;
    
    @Test
    public void findResourcebyIdentifier() {
        
        ResourceDTO resourceDTO = resourceService.findByIdentifier("111111");
        Assert.assertEquals("111111",resourceDTO.getIdentifier());
        Assert.assertTrue(true);
    }

}