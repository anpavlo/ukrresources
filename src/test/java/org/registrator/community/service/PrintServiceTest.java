/*package org.registrator.community.service;

import java.io.File;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.RunWith;
import org.registrator.community.config.root.TestingConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestingConfiguration.class)

@WebAppConfiguration
public class PrintServiceTest {

	private static Logger LOG=Logger.getLogger(PrintServiceTest.class);
	
	@Autowired
	PrintService printService;

	
	@Rule
	public TestWatcher testWatcher = new TestWatcher() {

		protected void succeeded(org.junit.runner.Description description) {
			LOG.info(description.getMethodName());
			System.out.println("<<SUCCESS>> - " + description.getMethodName());

		};

		protected void failed(Throwable e, org.junit.runner.Description description) {

			LOG.error(description.getMethodName());
			System.out.println("<<FAILED>> - " + description.getMethodName());
		};

	};
	
	
	@Test
	public void testOne() {
		printService.printProcuration(1);
		File file=new File("D:\\resourcePDF\file1.pdf");
		if(!file.exists()){
			Assert.fail("file is not created");
		}
		String [] name=file.getName().split(".");
		if(!name[name.length-1].equals("pdf")){
			Assert.fail("created file is not pdf format");
		}
		
			
	}

	@Test
	public void testTwo() {
		printService.printExtract(1);
		File file=new File("D:\\resourcePDF\file1.pdf");
		if(!file.exists()){
			Assert.fail("file is not created");
		}
		String [] name=file.getName().split(".");
		if(!name[name.length-1].equals("pdf")){
			Assert.fail("created file is not pdf format");
		}
	}

	@Test
	public void testThree() {
		printService.printProcurationOnSubmitInfo(1);
		File file=new File("D:\\resourcePDF\file1.pdf");
		if(!file.exists()){
			Assert.fail("file is not created");
		}
		String [] name=file.getName().split(".");
		if(!name[name.length-1].equals("pdf")){
			Assert.fail("created file is not pdf format");
		}
	}

}
*/

