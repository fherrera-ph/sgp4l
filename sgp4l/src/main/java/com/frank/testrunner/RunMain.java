package com.frank.testrunner;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import io.netty.util.internal.StringUtil;

public class RunMain {

	
	private static DecimalFormat df2 = new DecimalFormat("#.00");
	
	public static void main(String[] args) {

		String CHROME_DIR = "/Users/frankjas/eclipse-workspace/sgpickz4less/git/sgp4l/sgp4l/src/main/resources/driver/chromedriver";

		File chromeDir = new File(CHROME_DIR);

		System.out.println(chromeDir.exists());
		System.out.println(chromeDir.canExecute());

		chromeDir.setExecutable(true);
		System.out.println(chromeDir.canExecute());

		System.setProperty("webdriver.chrome.driver", CHROME_DIR);
		
		
		List<Product> productList = new ArrayList<>();
		
		
		
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("disable-popup-blocking", true);
		 
		ChromeOptions options = new ChromeOptions();
		options.addArguments("incognito");
		options.addArguments("--disable-popup-blocking");
		options.addArguments("--disable-notifications");
		options.addArguments("test-type");
		options.setCapability("enable-download-notification", false);
		
		capabilities.setCapability(ChromeOptions.CAPABILITY, options);

	    
		WebDriver sgDriver = new ChromeDriver(capabilities);

		String sgUrl = "https://www.nike.com/sg/w/mens-shoes-nik1zy7ok";

		sgDriver.get(sgUrl);
		browseWebsite(sgDriver, productList, true, 36.01);
		sgDriver.quit();
		
		WebDriver phDriver = new ChromeDriver(capabilities);
		
		String phUrl = "https://www.nike.com/ph/w/mens-shoes-nik1zy7ok";
		phDriver.get(phUrl);
		browseWebsite(phDriver, productList, false, 1);
		phDriver.quit();

		String header = String.join("\t|", 
				StringUtils.rightPad("Shoe Name", 50, " "), 
				"SG Price", 
				"SGD to PHP", 
				"PH Price", 
				"Cheaper Country", 
				"Difference");
		System.out.println(header);
		
		
		Collections.sort(productList, new Comparator<Product>() {
		      @Override
		      public int compare(final Product object1, final Product object2) {
		          return object1.getName().compareTo(object2.getName());
		      }
		  });
		
		
		System.out.println("***************");
		productList.forEach( product-> {
//
			
			String padded = StringUtils.rightPad(product.getName(), 50, " ");
			
			String result = String.join("\t|", 
					padded, 
					String.valueOf(df2.format(product.getSgd())) + "\t", 
					String.valueOf(df2.format(product.getSgdToPhp())), 
					String.valueOf(df2.format(product.getPhp())), 
					StringUtils.isBlank(product.getRecommended())? "SG" : product.getRecommended(), 
					String.valueOf(df2.format(product.getEarning())));
			System.out.println(result);
			
			
		});

	}
	
	
	protected static void browseWebsite(WebDriver webDriver, List<Product> productList, boolean isSG, double conversion) {
		try {
		    long lastHeight = (Long) ((JavascriptExecutor) webDriver).executeScript("return document.body.scrollHeight");

		    while (true) {
		        ((JavascriptExecutor) webDriver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
		        Thread.sleep(2000);

		        
		        
		        WebElement popup = webDriver.findElement(By.xpath("//*[@id=\"gen-nav-commerce-header-v2\"]/div[1]/div/button"));
		        
		        if (popup.isDisplayed()) { 
		        	popup.click();
		        }
		        long newHeight = (Long) ((JavascriptExecutor) webDriver).executeScript("return document.body.scrollHeight");
		        if (newHeight == lastHeight) {
		            break;
		        }
		        lastHeight = newHeight;
		    }
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
		
		List<WebElement> products = webDriver.findElements(By.className("product-card__info"));
		
		
		
		products.forEach( webElement -> {
			
			String shoeName = webElement.findElement(By.className("product-card__title")).getText();
			String shoePrice = webElement.findElement(By.className("product-price")).getText().split(" ")[0];
			shoePrice = shoePrice.replace("S$", "").replace("₱", "").replace(",", "");
			
			
//			System.out.println(shoeName + " = " + shoePrice);
			
			
			if (!StringUtil.isNullOrEmpty(shoePrice) && Double.valueOf(shoePrice) > 0) {
				if(isSG) {
					
					Product product = new Product();
					product.setName(shoeName);
					product.setSgd(Double.valueOf(shoePrice));
					product.setSgdToPhp(Double.valueOf(shoePrice) * conversion);
					
					productList.add(product);
					
				} else {
			
					Iterator<Product> iterator = productList.iterator();
					
					while (iterator.hasNext()) {
						Product sgdProduct = iterator.next();
						
						if(sgdProduct.getName().equals(shoeName)) {
									
							double php = Double.valueOf(shoePrice);
							sgdProduct.setPhp(php);
							
							sgdProduct.setRecommended(php > sgdProduct.getSgdToPhp() ? "SG": "PH");
							
							sgdProduct.setEarning(Math.abs(php - sgdProduct.getSgdToPhp()));
								 
							break;
						}
						
					}
				}	
			}
			

			
		});
	}

}
