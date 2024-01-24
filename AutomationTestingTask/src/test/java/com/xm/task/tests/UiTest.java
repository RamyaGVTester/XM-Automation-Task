package com.xm.task.tests;

import java.awt.Toolkit;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class UiTest {

	@Test
	public void automationTaskUi() {

		// Define the screen resolutions
		
		  List<Dimension> resolutions = Arrays.asList( new Dimension((int)
		  Toolkit.getDefaultToolkit().getScreenSize().getWidth(), (int)
		  Toolkit.getDefaultToolkit().getScreenSize().getHeight()), new Dimension(1024,
		  768), new Dimension(800, 600));
		 
		
		// Iterate over the screen resolutions
		for (Dimension resolution : resolutions) {
			// Initialize WebDriver

			ChromeOptions chromeOptions = new ChromeOptions();
			WebDriverManager.chromedriver().setup();
			WebDriver driver = new ChromeDriver(chromeOptions);
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
			JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
			try {

				// Set the browser's screen resolution
				driver.manage().window().setSize(resolution);

				// Navigate to the Home page
				// driver.manage().window().maximize();
				driver.get("https://xm.com");
				driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
				try {

					// Accept all cookies
					driver.findElement(By.xpath("//button[text()='ACCEPT ALL']")).click();
				} catch (Exception e) {
					System.out.println("Cookies acceptance button not found or already accepted.");
				}

				// Verify home page
				String expectedHomePageTitle = "Forex & CFD Trading on Stocks, Indices, Oil, Gold by XM™";
				String actualHomePageTitle = driver.getTitle();
				Assert.assertEquals(actualHomePageTitle, expectedHomePageTitle,"We are not on XM Home Page");
				// Click the "Trading" link
				WebElement menuButton = driver.findElement(By.xpath("//span[text()='Menu']"));
				WebElement tradingMenu = driver.findElement(By.xpath("//a[@href='#tradingMenu']"));
				WebElement stocksMenu = driver.findElement(By.xpath("//span[contains(text(),'Stocks')]"));
				if (menuButton.isDisplayed()) {
					menuButton.click();
					tradingMenu.click();
					jsExecutor.executeScript("arguments[0].scrollIntoView(true);", stocksMenu);
					Assert.assertTrue(stocksMenu.isDisplayed());
					jsExecutor.executeScript("arguments[0].click();", stocksMenu);

				} else {
					driver.findElement(By.xpath("//a[contains(text(),'Trading')]")).click();
					driver.findElement(By.xpath("//a[contains(text(),'Stocks')]")).click();
				}
				// Click on Stocks link
				// Choose the Norway stock filter
				WebElement NorwayButton = driver.findElement(By.xpath("//button[contains(text(), 'Norway')]"));
				jsExecutor.executeScript("arguments[0].scrollIntoView(true);", NorwayButton);
				jsExecutor.executeScript("arguments[0].click();", NorwayButton);
				// Get data from the table for the Orkla ASA (ORK.OL)
				// Find the table element
				WebElement table = driver.findElement(By.xpath("//table[contains(@class,'dataTB')]"));

				// Find the Orkla ASA (ORK.OL) data in the table using pagination
				String orkla = null;
				while (orkla == null) {
					List<WebElement> rows = table.findElements(By.xpath(".//tbody/tr/td"));
					for (int i = 0; i < rows.size(); i++) {
						if (rows.get(i).getText().contains("Orkla ASA (ORK.OL)")) {

							orkla = rows.get(i).getText();
							
							String symbolStockPage;
							String minSpreadStockPage;
							String minMaxTradeSizeStockPage;
							String marginRequirementStockPage;
							String swapLongStockPage;
							String swapShortStockPage;
							String limitStopLevelStockPage;
							
							boolean isCollapsed = false;
							
							if(table.getAttribute("class").contains("collapsed"))
							{
								isCollapsed = true;
								rows.get(i).click();
							}
			            	
							symbolStockPage = rows.get(i)
									.findElement(By.xpath("following-sibling::td[@data-xm-qa-name='symbol']"))
									.getText();
							minSpreadStockPage = rows.get(i)
									.findElement(By.xpath("following-sibling::td[@data-xm-qa-name='minSpread']"))
									.getText();
							minMaxTradeSizeStockPage = rows.get(i)
									.findElement(By.xpath("following-sibling::td[@data-xm-qa-name='minMaxTradeSize']"))
									.getText();
							marginRequirementStockPage = rows.get(i)
									.findElement(
											By.xpath("following-sibling::td[@data-xm-qa-name='marginRequirement']"))
									.getText();
							swapLongStockPage = rows.get(i)
									.findElement(By.xpath("following-sibling::td[@data-xm-qa-name='swapLong']"))
									.getText();

							String styleAttribute = rows.get(i).
									findElement(By.xpath("following-sibling::td[@data-xm-qa-name='swapShort']")).getAttribute("style");
							if (isCollapsed && styleAttribute.contains("display: none;"))
							{
								swapShortStockPage = rows.get(i)
										.findElement(By.xpath("//span[contains(text(), 'Short Swap')]/following-sibling::span[@class='dtr-data']")).getText();								
							}
							else 
							{
								swapShortStockPage = rows.get(i)
										.findElement(By.xpath("following-sibling::td[@data-xm-qa-name='swapShort']")).getText();								
							}
							
							styleAttribute = rows.get(i).
									findElement(By.xpath("following-sibling::td[@data-xm-qa-name='limitStopLevel']")).getAttribute("style");
							if (isCollapsed && styleAttribute.contains("display: none;"))
							{
				            	limitStopLevelStockPage = rows.get(i)
										.findElement(By.xpath("//span[contains(text(), 'Limit')]/following-sibling::span[@class='dtr-data']")).getText();
							}
							else
							{
								limitStopLevelStockPage = rows.get(i)
										.findElement(By.xpath("following-sibling::td[@data-xm-qa-name='limitStopLevel']")).getText();																
							}
							
							if (isCollapsed)
							{
								jsExecutor.executeScript("arguments[0].click();", rows.get(i).findElement(By.xpath(
										"//span[@class='dtr-data']/a[contains(text(),'Read More') and @href='https://www.xm.com/stocks/orkla']")));
							}
							else 
							{
								jsExecutor.executeScript("arguments[0].click();", rows.get(i).findElement(By.xpath(
										"//a[contains(text(),'Read More') and @href='https://www.xm.com/stocks/orkla']")));								
							}

							String symbolOrkla = driver
									.findElement(By.xpath("//td[@data-xm-qa-name='symbols__value']/span")).getText();
							String minSpreadOrkla = driver.findElement(By.xpath(
									"//table[@class='table pull-right']//td[@data-xm-qa-name='spreads_as_low_as__value']/span/strong"))
									.getText();
							String minMaxTradeSizeOrkla = driver
									.findElement(By.xpath("//td[@data-xm-qa-name='min_max_trade_size__value']/span"))
									.getText();
							String marginRequirementOrkla = driver
									.findElement(
											By.xpath("//td[@data-xm-qa-name='margin_requirement__value']/span/strong"))
									.getText();
							String swapLongOrkla = driver
									.findElement(By.xpath(
											"//td[@data-xm-qa-name='swap_value_in_margin_currency_long__value']/span"))
									.getText();
							String swapShortOrkla = driver
									.findElement(By.xpath(
											"//td[@data-xm-qa-name='swap_value_in_margin_currency_short__value']/span"))
									.getText();
							String limitStopLevelOrkla = driver
									.findElement(By.xpath("//td[@data-xm-qa-name='limit_and_stop_levels__title']/span"))
									.getText();
							Assert.assertEquals(symbolStockPage, symbolOrkla, "Symbol in Stocks page does not match with Symbol in Orkla page");
							Assert.assertEquals(minSpreadStockPage, minSpreadOrkla,"Minumum Spread in Stocks page does not match with Minumum Spread in Orkla page");
							Assert.assertEquals(minMaxTradeSizeStockPage, minMaxTradeSizeOrkla,"Min/Max Trade Size in Stocks page does not match with Min/Max Trade Size in Orkla page");
							Assert.assertEquals(marginRequirementStockPage, marginRequirementOrkla,"Margin Requirement in Stocks page does not match with Margin Requirement in Orkla page");
							Assert.assertEquals(swapLongStockPage, swapLongOrkla,"Long swap in Stocks page does not match with Long swap in Orkla page");
							Assert.assertEquals(swapShortStockPage, swapShortOrkla,"Short swap in Stocks page does not match with Short swap in Orkla page");
							Assert.assertEquals(limitStopLevelStockPage, limitStopLevelOrkla,"Limit and Stop levels in Stocks page does not match with Limit and Stop levels in Orkla page");
							break;
						}
					}

					if (orkla == null) {
						WebElement nextPageButton = driver.findElement(
								By.xpath("//a[contains(@class,'paginate_button') and contains(text(), 'Next')]"));

						if (nextPageButton.getAttribute("class").contains("disabled")) {
							System.out.println("Could not find Orkla ASA (ORK.OL) data in the table.");
							break;
						} else {
							jsExecutor.executeScript("arguments[0].scrollIntoView(true);", nextPageButton);
							jsExecutor.executeScript("arguments[0].click();", nextPageButton);
						}
					}
				}
           

			} finally {
				// Close the browser
				driver.quit();
			}
		}
	}
}
