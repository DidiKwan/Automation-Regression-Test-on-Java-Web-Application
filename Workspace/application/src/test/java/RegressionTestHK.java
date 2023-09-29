import java.util.regex.Pattern;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import java.io.*;

import static org.testng.Assert.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class HealthCheckHK {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();
  private String password = new String();
  private Dimension dimension = new Dimension(1024, 768);
  private String TextOnPage = new String();
  private static WebDriverWait wait;
  private String URL;
  private String Filename;
  private String Username;
  private String Email;
  private String DownloadFolder;

  @Parameters({ "User-Name", "ChromeDriver", "Email", "Download-Folder" })
  @BeforeClass(alwaysRun = true)
  public void setUp(String UN, String CD, String EM, String DF) throws Exception {
    System.setProperty("webdriver.chrome.driver", CD);
    driver = new ChromeDriver();
    baseUrl = "https://application.company.com/";
    wait = new WebDriverWait(driver, 30);
    Username = UN;
    Email = EM;
    DownloadFolder = DF;
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  @Test(priority = 1)
  public void LoginTest() throws Exception {
    driver.get(baseUrl);
    driver.manage().window().setSize(dimension);
    driver
        .findElement(
            By.xpath("//table[@id='Main']/tbody/tr[3]/td/table/tbody/tr[2]/td[4]/table/tbody/tr[2]/td[2]/a/span"))
        .click();
    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//td[2]/input")));
    URL = driver.getCurrentUrl();
    assertTrue(URL.contains(
        "https://application.company.com/portalWeb/com/asis/application/useradmin/portlet/CustLogin/toLoginPage.do?COMPANY_CD=8000"),
        "Hong Kong Login Page Fail");
    driver.findElement(By.id("iUsername")).sendKeys("username");
    driver.findElement(By.id("iPassword")).sendKeys("password");
    driver.findElement(By.id("iCaptchaAnswer")).click();
    driver.findElement(By.id("iCaptchaAnswer")).clear();
    wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Admin")));
    URL = driver.getCurrentUrl();
    assertTrue(URL.contains("https://application.company.com/portalWeb/appmanager/portal/index"), "Login Fail");
  }

  @Test(priority = 2)
  public void CreateUser() throws Exception {
    driver.findElement(By.linkText("Admin")).click();
    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='secRow']/div/a[1]/ul/li")));
    URL = driver.getCurrentUrl();
    assertTrue(
        URL.contains("https://application.company.com/portalWeb/appmanager/portal/index?_nfpb=true&_pageLabel=page_admin"),
        "Admin Page Fail");

    driver.findElement(By.xpath("//*[@id='secRow']/div/a[1]/ul/li")).click();
    wait.until(
        ExpectedConditions.elementToBeClickable(By.name("portlet_inst_CountryUserListactionOverride:startCreateUser")));
    URL = driver.getCurrentUrl();
    assertTrue(
        URL.contains(
            "https://application.company.com/portalWeb/appmanager/portal/index?_nfpb=true&_pageLabel=page_userList2"),
        "User List Page Fail");

    driver.findElement(By.name("portlet_inst_CountryUserListactionOverride:startCreateUser")).click();
    wait.until(
        ExpectedConditions.elementToBeClickable(By.name("portlet_inst_CountryUserListactionOverride:createUser")));
    assertTrue(isElementPresent(By.name("portlet_inst_CountryUserListactionOverride:createUser")),
        "Create User Page Fail");

    driver.findElement(By.id("portlet_inst_CountryUserList{actionForm.userName}")).sendKeys(Username);
    driver.findElement(By.id("portlet_inst_CountryUserList{actionForm.firstName}")).sendKeys("Test");
    driver.findElement(By.id("portlet_inst_CountryUserList{actionForm.lastName}")).sendKeys("ing");
    driver.findElement(By.id("portlet_inst_CountryUserList{actionForm.businessPhone}")).sendKeys("12345678");
    driver.findElement(By.id("portlet_inst_CountryUserList{actionForm.email}"))
        .sendKeys(Email);
    driver.findElement(By.id("portlet_inst_CountryUserListwlw-checkbox_key:{pageFlow.listRoles[0].isChecked}")).click();
    driver.findElement(By.id("portlet_inst_CountryUserListwlw-checkbox_key:{pageFlow.listRoles[1].isChecked}")).click();
    driver.findElement(By.id("portlet_inst_CountryUserListwlw-checkbox_key:{pageFlow.listRoles[2].isChecked}")).click();
    driver.findElement(By.id("portlet_inst_CountryUserListwlw-checkbox_key:{pageFlow.listRoles[4].isChecked}")).click();
    driver.findElement(By.id("portlet_inst_CountryUserListwlw-checkbox_key:{pageFlow.listRoles[5].isChecked}")).click();
    driver.findElement(By.name("portlet_inst_CountryUserListactionOverride:createUser")).click();

    wait.until(ExpectedConditions.numberOfWindowsToBe(2));
    String mainWindowHandle = driver.getWindowHandle();
    Set<String> allWindowHandles = driver.getWindowHandles();
    Iterator<String> iterator = allWindowHandles.iterator();
    while (iterator.hasNext()) {
      String ChildWindow = iterator.next();
      if (!mainWindowHandle.equalsIgnoreCase(ChildWindow)) {
        driver.switchTo().window(ChildWindow);
        password = driver.findElement(By.xpath("//tr[2]/td/table/tbody/tr/td/p[2]")).getText();
        password = password.split(" ")[7];
        driver.findElement(By.linkText("here")).click();
        driver.findElement(By.xpath("//input[@value='Close']")).click();
      }
    }
    driver.switchTo().window(mainWindowHandle);
    wait.until(ExpectedConditions.elementToBeClickable(By.id("portlet_inst_CountryUserList_pw_reset_button")));
    assertTrue(isElementPresent(By.id("portlet_inst_CountryUserList_pw_reset_button")), "Create User Fail");
  }

  @Test(priority = 3)
  public void RetrievePassword() throws Exception {
    driver.findElement(By.xpath("//img[@title='Logout']")).click();
    wait.until(ExpectedConditions.elementToBeClickable(
        By.xpath("//table[@id='Main']/tbody/tr[3]/td/table/tbody/tr[2]/td[4]/table/tbody/tr[2]/td[2]/a/span")));
    URL = driver.getCurrentUrl();
    assertTrue(URL.contains(
        "https://application.company.com/portalWeb/com/asis/application/useradmin/portlet/CustLogin/CustLoginController.jpf"),
        "Logout Fail");

    driver
        .findElement(
            By.xpath("//table[@id='Main']/tbody/tr[3]/td/table/tbody/tr[2]/td[4]/table/tbody/tr[2]/td[2]/a/span"))
        .click();
    wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Forgot password?")));

    driver.findElement(By.linkText("Forgot password?")).click();
    wait.until(ExpectedConditions.elementToBeClickable(By.name("actionOverride:emailPwdToUser")));
    URL = driver.getCurrentUrl();
    assertTrue(URL.contains(
        "https://application.company.com/portalWeb/com/asis/application/useradmin/portlet/forgotpwd/begin.do?companyCd=8000"),
        "Retrieve Password Page Fail");

    driver.findElement(By.id("{actionForm.userName}")).sendKeys(Username);
    driver.findElement(By.id("{actionForm.email}")).sendKeys(Email);
    driver.findElement(By.id("{actionForm.captchaAnswer}")).click();
    driver.findElement(By.id("{actionForm.captchaAnswer}")).clear();
    wait.until(
        ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id='reminderForm']/table/tbody/tr[3]/td/font")));
    TextOnPage = driver.findElement(By.xpath("//*[@id='reminderForm']/table/tbody/tr[3]/td/font")).getText();
    assertTrue(TextOnPage.contains("New password has sent to"), "Retrieve Password Fail");
  }

  @Test(priority = 4)
  public void LoginTestNewUser() throws Exception {
    driver
        .findElement(
            By.xpath("//table[@id='Main']/tbody/tr[3]/td/table/tbody/tr[2]/td[4]/table/tbody/tr[2]/td[2]/a/span"))
        .click();
    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//td[2]/input")));
    driver.findElement(By.id("iUsername")).sendKeys(Username);
    driver.findElement(By.id("iPassword")).sendKeys(password);
    driver.findElement(By.id("iCaptchaAnswer")).click();
    driver.findElement(By.id("iCaptchaAnswer")).clear();
    wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Admin")));
    URL = driver.getCurrentUrl();
    assertTrue(URL.contains("https://application.company.com/portalWeb/appmanager/portal/index"), "Login Fail");
  }

  @Test(priority = 5)
  public void Menu() throws Exception {
    SoftAssert Assert = new SoftAssert();
    driver.findElement(By.xpath("//*[@id='navigation']/li[1]/a/img")).click();
    wait.until(ExpectedConditions.elementToBeClickable(By.linkText("My Orders")));
    URL = driver.getCurrentUrl();
    Assert.assertTrue(
        URL.contains("https://application.company.com/portalWeb/appmanager/portal/index?_nfpb=true&_pageLabel=page_home"),
        "Homg Page Fail");

    driver.findElement(By.linkText("My Orders")).click();
    wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Trade Meeting Admin")));
    URL = driver.getCurrentUrl();
    Assert.assertTrue(
        URL.contains(
            "https://application.company.com/portalWeb/appmanager/portal/index?_nfpb=true&_pageLabel=page_myOrders"),
        "My Orders Page Fail");

    driver.findElement(By.linkText("Trade Meeting Admin")).click();
    wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Stock Order")));
    URL = driver.getCurrentUrl();
    Assert.assertTrue(
        URL.contains(
            "https://application.company.com/portalWeb/appmanager/portal/index?_nfpb=true&_pageLabel=page_tradeMtgList"),
        "Trade Meeting Admin Page Fail");

    driver.findElement(By.linkText("Stock Order")).click();
    wait.until(ExpectedConditions.elementToBeClickable(By.linkText("E-catalogue")));
    URL = driver.getCurrentUrl();
    Assert.assertTrue(
        URL.contains(
            "https://application.company.com/portalWeb/appmanager/portal/index?_nfpb=true&_pageLabel=book_stkOrder"),
        "Stock Order Page Fail");

    driver.findElement(By.linkText("E-catalogue")).click();
    wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Orders & Services")));
    URL = driver.getCurrentUrl();
    Assert.assertTrue(
        URL.contains("https://application.company.com/portalWeb/appmanager/portal/index?_nfpb=true&_pageLabel=book_eCat"),
        "E-catalogue Page Fail");

    driver.findElement(By.linkText("Orders & Services")).click();
    wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Admin")));
    URL = driver.getCurrentUrl();
    Assert.assertTrue(URL.contains(
        "https://application.company.com/portalWeb/appmanager/portal/index?_nfpb=true&_pageLabel=page_orders_and_services"),
        "Orders & Services Page Fail");

    driver.findElement(By.linkText("Admin")).click();
    wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Trade Meeting Admin")));
    URL = driver.getCurrentUrl();
    Assert.assertTrue(
        URL.contains("https://application.company.com/portalWeb/appmanager/portal/index?_nfpb=true&_pageLabel=page_admin"),
        "Admin Page Fail");

    Assert.assertAll();
  }

  @Parameters({ "Switch", "Trade-Meeting" })
  @Test(priority = 6)
  public void TradeMeetingMenu(@Optional("Switch") String SW, String TM) throws Exception {
    SoftAssert Assert = new SoftAssert();
    driver.findElement(By.linkText("Trade Meeting Admin")).click();
    wait.until(ExpectedConditions.elementToBeClickable(By.linkText("My Orders")));

    if (SW.equals("FW23")) {
      driver.findElement(By.id("switchIcon23FW")).click();
    }
    if (SW.equals("SS23")) {
      driver.findElement(By.id("switchIcon23SS")).click();
    }
    if (SW.equals("FW22")) {
      driver.findElement(By.id("switchIcon22FW")).click();
    }
    if (SW.equals("SS22")) {
      driver.findElement(By.id("switchIcon22SS")).click();
    }
    if (SW.equals("FW21")) {
      driver.findElement(By.id("switchIcon21FW")).click();
    }
    if (SW.equals("SS21")) {
      driver.findElement(By.id("switchIcon21SS")).click();
    }

    driver.findElement(By.xpath("//*[text()[contains(.,'" + TM + "')]]")).click();
    wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Details")));
    Assert.assertTrue(isElementPresent(By.xpath("//*[@id='portlet_inst_preOrderAdm_curForm']/table[3]")),
        "TM Name Fail");

    driver.findElement(By.linkText("Details")).click();
    wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Articles")));
    Assert.assertTrue(isElementPresent(By.xpath("//*[@id='portlet_inst_preOrderAdm_curForm']/table[3]")),
        "Details Page Fail");

    driver.findElement(By.linkText("Articles")).click();
    wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Customers")));
    Assert.assertTrue(isElementPresent(By.xpath("//*[@id='portlet_inst_preOrderAdm_saveArticleForm']")),
        "Articles Page Fail");

    driver.findElement(By.linkText("Customers")).click();
    wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Assortment Plan")));
    Assert.assertTrue(isElementPresent(By.xpath("//*[@id='portlet_inst_preOrderAdm_saveCustRangeForm']")),
        "Customers Page Fail");

    driver.findElement(By.linkText("Printable Catalogue")).click();
    wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Assortment Plan")));
    Assert.assertTrue(isElementPresent(By.xpath("//*[@id='portlet_inst_preOrderAdmstepForm']")),
        "Printable Catalogue Page Fail");

    driver.findElement(By.linkText("Assortment Plan")).click();
    wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Status")));
    Assert.assertTrue(isElementPresent(By.xpath("//*[@id='portlet_inst_preOrderAdm_uploadForm']")),
        "Assortment Plan Page Fail");

    driver.findElement(By.linkText("Status")).click();
    wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Orders")));
    Assert.assertTrue(isElementPresent(By.xpath("//*[@id='portlet_inst_preOrderAdm_curForm']/table[4]")),
        "Status Page Fail");

    driver.findElement(By.linkText("Orders")).click();
    wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Reports")));
    Assert.assertTrue(isElementPresent(By.xpath("//*[@id='portlet_inst_preOrderAdmpreOrderForm']")),
        "Orders Page Fail");

    driver.findElement(By.linkText("Reports")).click();
    wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Articles")));
    Assert.assertTrue(isElementPresent(By.xpath(
        "/html/body/center/div/div/table[2]/tbody/tr/td/div[1]/table[1]/tbody/tr/td/div/div/div/table/tbody/tr/td[3]/table[4]")),
        "Reports Page Fail");

    Assert.assertAll();
  }

  @Test(priority = 7)
  public void DownloadReports() throws Exception {
    driver.findElement(By.linkText("Articles")).click();
    wait.until(ExpectedConditions.elementToBeClickable(
        By.name("portlet_inst_preOrderAdmactionOverride:exportTmArticleSrdSizeWhenTmIsOpenForNewOrderForm")));
    driver
        .findElement(
            By.name("portlet_inst_preOrderAdmactionOverride:exportTmArticleSrdSizeWhenTmIsOpenForNewOrderForm"))
        .click();
    wait.until(ExpectedConditions.numberOfWindowsToBe(2));
    String mainWindowHandle = driver.getWindowHandle();
    Set<String> allWindowHandles = driver.getWindowHandles();
    Iterator<String> iterator = allWindowHandles.iterator();
    while (iterator.hasNext()) {
      String ChildWindow = iterator.next();
      if (!mainWindowHandle.equalsIgnoreCase(ChildWindow)) {
        driver.switchTo().window(ChildWindow);
        Filename = driver.findElement(By.xpath("//div[3]/div[2]")).getText();
        driver.findElement(By.linkText(Filename)).click();
        boolean check = new File(DownloadFolder, Filename).exists();
        while (!check) {
          check = new File(DownloadFolder, Filename).exists();
        }
        driver.close();
      }
    }
    driver.switchTo().window(mainWindowHandle);

    driver.findElement(By.linkText("Reports")).click();
    wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Reports")));
    driver.findElement(By.linkText("Order Consolidation Report (New SRD)")).click();

    driver.findElement(By.linkText("Order Consolidation Report")).click();
    wait.until(ExpectedConditions.numberOfWindowsToBe(2));
    mainWindowHandle = driver.getWindowHandle();
    allWindowHandles = driver.getWindowHandles();
    iterator = allWindowHandles.iterator();
    while (iterator.hasNext()) {
      String ChildWindow = iterator.next();
      if (!mainWindowHandle.equalsIgnoreCase(ChildWindow)) {
        driver.switchTo().window(ChildWindow);
        Filename = driver.findElement(By.xpath("//a[contains(text(),'OrderConsolidationReport')]")).getText();
        driver.findElement(By.linkText(Filename)).click();
        boolean check = new File(DownloadFolder, Filename).exists();
        while (!check) {
          check = new File(DownloadFolder, Filename).exists();
        }
        driver.close();
      }
    }
    driver.switchTo().window(mainWindowHandle);
  }

  @Test(priority = 8)
  public void SalesOrder() throws Exception {
    driver.findElement(By.linkText("Orders & Services")).click();
    wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Admin")));
    driver.findElement(By.xpath("//div[3]/a/ul/li")).click();
    wait.until(ExpectedConditions.elementToBeClickable(By.id("portlet_inst_OrderactionOverride:searchOrder")));
    assertTrue(isElementPresent(By.id("portlet_inst_OrderactionOverride:searchOrder")), "Sales Order Page Fail");

    driver.findElement(By.id("portlet_inst_OrderactionOverride:searchOrder")).click();
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='portlet_inst_OrderorderListForm']")));
    assertTrue(isElementPresent(By.xpath("//*[@id='portlet_inst_OrderorderListForm']")), "Search Sales Order Fail");
    Filename = driver
        .findElement(By.xpath("//*[@id='portlet_inst_OrderorderListForm']/div/div/table/tbody/tr[2]/td[5]/a/span"))
        .getText();
    driver.findElement(By.xpath("//*[@id='portlet_inst_OrderorderListForm']/div/div/table/tbody/tr[2]/td[5]/a/span"))
        .click();
    wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Printed Version")));
    assertEquals(
        driver.findElement(By.xpath("//form[@id='portlet_inst_OrdersearchOrderForm']/div/div/span")).getText(),
        Filename);
    driver.findElement(By.linkText("Printed Version")).click();

    wait.until(ExpectedConditions.numberOfWindowsToBe(2));
    String mainWindowHandle = driver.getWindowHandle();
    Set<String> allWindowHandles = driver.getWindowHandles();
    Iterator<String> iterator = allWindowHandles.iterator();
    while (iterator.hasNext()) {
      String ChildWindow = iterator.next();
      if (!mainWindowHandle.equalsIgnoreCase(ChildWindow)) {
        driver.switchTo().window(ChildWindow);
        Filename = driver.findElement(By.xpath("//a[contains(text(),'SO_8600')]")).getText();
        driver.findElement(By.linkText(Filename)).click();
        boolean check = new File(DownloadFolder, Filename).exists();
        while (!check) {
          check = new File(DownloadFolder, Filename).exists();
        }
        driver.close();
      }
    }
    driver.switchTo().window(mainWindowHandle);
  }

  @Test(priority = 9)
  public void FinanceDocument() throws Exception {
    driver.findElement(By.linkText("Orders & Services")).click();
    wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Admin")));
    driver.findElement(By.xpath("//div[2]/div[2]/a/ul/li")).click();
    wait.until(ExpectedConditions.elementToBeClickable(By.id("portlet_inst_PdfDownloadactionOverride:SearchPdf")));
    assertTrue(isElementPresent(By.id("portlet_inst_PdfDownloadactionOverride:SearchPdf")),
        "Finance Document Page Fail");
    driver.findElement(By.id("portlet_inst_PdfDownload_printPdf")).click();

    wait.until(ExpectedConditions.numberOfWindowsToBe(2));
    String mainWindowHandle = driver.getWindowHandle();
    Set<String> allWindowHandles = driver.getWindowHandles();
    Iterator<String> iterator = allWindowHandles.iterator();
    while (iterator.hasNext()) {
      String ChildWindow = iterator.next();
      if (!mainWindowHandle.equalsIgnoreCase(ChildWindow)) {
        driver.switchTo().window(ChildWindow);
        Filename = driver.findElement(By.xpath("//a[contains(text(),'CS_8000')]")).getText();
        driver.findElement(By.linkText(Filename)).click();
        boolean check = new File(DownloadFolder, Filename).exists();
        while (!check) {
          check = new File(DownloadFolder, Filename).exists();
        }
        driver.close();
      }
    }
    driver.switchTo().window(mainWindowHandle);
  }

  @Parameters({ "Customer-No" })
  @Test(priority = 10)
  public void ChangeRole(String CS) throws Exception {
    driver.findElement(By.xpath("//ul[@id='top_usermenu']/li[5]/a/div")).click();
    wait.until(ExpectedConditions.elementToBeClickable(By.id("portlet_inst_CountryChangeRole_createButton")));
    URL = driver.getCurrentUrl();
    assertTrue(URL.contains(
        "https://application.company.com/portalWeb/appmanager/portal/index?_nfpb=true&_pageLabel=label_page_CountryChangeRole"),
        "Change Role Page Fail");
    driver.findElement(By.id("portlet_inst_CountryChangeRole{actionForm.customerId}")).sendKeys(CS);
    driver.findElement(By.id("portlet_inst_CountryChangeRole_createButton")).click();
    wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Trade Meeting")));

    Filename = driver.findElement(By.xpath("//ul[@id='top_usermenu']/li[5]/a/div")).getText();
    assertEquals(Filename, "Exit Role");
  }

  @Test(priority = 11)
  public void CustomerMenu() throws Exception {
    SoftAssert Assert = new SoftAssert();
    driver.findElement(By.xpath("//ul[@id='navigation']/li/a/img")).click();
    wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Trade Meeting")));
    URL = driver.getCurrentUrl();
    Assert.assertTrue(
        URL.contains("https://application.company.com/portalWeb/appmanager/portal/index?_nfpb=true&_pageLabel=page_home"),
        "Homg Page Fail");

    driver.findElement(By.linkText("Trade Meeting")).click();
    wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Stock Order")));
    URL = driver.getCurrentUrl();
    Assert.assertTrue(URL.contains(
        "https://application.company.com/portalWeb/appmanager/portal/index?_nfpb=true&_pageLabel=page_custTradeMtgList"),
        "Trade Meeting Page Fail");

    driver.findElement(By.linkText("Stock Order")).click();
    wait.until(ExpectedConditions.elementToBeClickable(By.linkText("E-catalogue")));
    URL = driver.getCurrentUrl();
    Assert.assertTrue(
        URL.contains(
            "https://application.company.com/portalWeb/appmanager/portal/index?_nfpb=true&_pageLabel=book_stkOrder"),
        "Stock Order Page Fail");

    driver.findElement(By.linkText("E-catalogue")).click();
    wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Orders & Services")));
    URL = driver.getCurrentUrl();
    Assert.assertTrue(
        URL.contains("https://application.company.com/portalWeb/appmanager/portal/index?_nfpb=true&_pageLabel=book_eCat"),
        "E-catalogue Page Fail");

    driver.findElement(By.linkText("Orders & Services")).click();
    wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Trade Meeting")));
    URL = driver.getCurrentUrl();
    Assert.assertTrue(URL.contains(
        "https://application.company.com/portalWeb/appmanager/portal/index?_nfpb=true&_pageLabel=page_orders_and_services"),
        "Orders & Services Page Fail");

    Assert.assertAll();
  }

  @Test(priority = 12)
  public void DownloadOrderForm() throws Exception {
    driver.findElement(By.xpath("//ul[@id='navigation']/li/a/img")).click();
    wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Trade Meeting")));
    assertTrue(isElementPresent(By.cssSelector("img[title=\"Order Forms\"]")),
        "No Order Form can be downloaded at the moment");
    driver.findElement(By.cssSelector("img[title=\"Order Forms\"]")).click();
    driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
    if (isElementPresent(By.linkText("Download"))) {
      driver.findElement(By.linkText("Download")).click();
    } else if (isElementPresent(By.linkText("Generate"))) {
      driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
      driver.findElement(By.linkText("Generate")).click();
      try {
        Thread.sleep(15000);
      } catch (InterruptedException ie) {
      }
      driver.findElement(By.linkText("Upload Files")).click();
      wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Order Forms")));
      driver.findElement(By.linkText("Order Forms")).click();
      wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Download")));
      driver.findElement(By.linkText("Download")).click();
    }
    driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
    if (isElementPresent(By.linkText("Cancel"))) {
      driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
      driver.findElement(By.cssSelector("input.general_button.general_btn")).click();
    }
    wait.until(ExpectedConditions.numberOfWindowsToBe(2));
    String mainWindowHandle = driver.getWindowHandle();
    Set<String> allWindowHandles = driver.getWindowHandles();
    Iterator<String> iterator = allWindowHandles.iterator();
    while (iterator.hasNext()) {
      String ChildWindow = iterator.next();
      if (!mainWindowHandle.equalsIgnoreCase(ChildWindow)) {
        driver.switchTo().window(ChildWindow);
        Filename = driver.findElement(By.xpath("//a[contains(text(),'OrderExportFile_')]")).getText();
        driver.findElement(By.linkText(Filename)).click();
        boolean check = new File(DownloadFolder, Filename).exists();
        while (!check) {
          check = new File(DownloadFolder, Filename).exists();
        }
        driver.close();
      }
    }
    driver.switchTo().window(mainWindowHandle);
  }

  @Test(priority = 13)
  public void Ecatalogue() throws Exception {
    driver.findElement(By.linkText("E-catalogue")).click();
    wait.until(ExpectedConditions.elementToBeClickable(By.id("portlet_inst_ArticleBrowsesearchButton")));
    driver.findElement(By.xpath("//div[@id='expand_form']/div[2]/div/a/span")).click();
    driver.findElement(By.xpath("//div[@id='expand_form']/div/div[6]/div/p/input")).sendKeys("1");
    driver.findElement(By.id("portlet_inst_ArticleBrowsesearchButton")).click();
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='company']")));
    assertEquals(driver.findElement(By.xpath("//div[@id='company']/span")).getText(), "No record found");
  }

  @Test(priority = 14)
  public void Logout() throws Exception {
    driver.findElement(By.cssSelector("img[title=\"Logout\"]")).click();
    driver
        .findElement(
            By.xpath("//table[@id='Main']/tbody/tr[3]/td/table/tbody/tr[2]/td[4]/table/tbody/tr[2]/td[2]/a/span"))
        .click();
    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//td[2]/input")));
    driver.findElement(By.id("iUsername")).sendKeys("username");
    driver.findElement(By.id("iPassword")).sendKeys("password");
    driver.findElement(By.id("iCaptchaAnswer")).click();
    driver.findElement(By.id("iCaptchaAnswer")).clear();
    wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Admin")));
  }

  @Test(priority = 15)
  public void Delete() throws Exception {
    driver.findElement(By.linkText("Admin")).click();
    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='secRow']/div/a[1]/ul/li")));
    driver.findElement(By.xpath("//*[@id='secRow']/div/a[1]/ul/li")).click();
    driver.findElement(By.id("portlet_inst_CountryUserList{actionForm.userName}")).sendKeys(Username);
    driver.findElement(By.id("portlet_inst_CountryUserListactionOverride:searchUser")).click();
    wait.until(ExpectedConditions.elementToBeClickable(By.linkText(Username)));
    assertTrue(isElementPresent(By.linkText(Username)), "Search User Fail");
    driver.findElement(By.id("portlet_inst_CountryUserListwlw-checkbox_key:{pageFlow.curUserList[0].isChecked}"))
        .click();
    driver.findElement(By.name("portlet_inst_CountryUserListactionOverride:deleteUserList")).click();
    CloseAlertAndGetItsText();
    driver.findElement(By.cssSelector("img[title=\"Logout\"]")).click();
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("span.dHeader")));
  }

  @AfterClass(alwaysRun = true)
  public void tearDown() throws Exception {
    driver.quit();
    String verificationErrorString = verificationErrors.toString();
    if (!"".equals(verificationErrorString)) {
      fail(verificationErrorString);
    }
  }

  private boolean isElementPresent(By by) {
    driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
    try {
      driver.findElement(by);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    } finally {
      driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }
  }

  private void CloseAlertAndGetItsText() {
    try {
      Alert alert = driver.switchTo().alert();
      // String alertText = alert.getText();
      if (acceptNextAlert) {
        alert.accept();
      } else {
        alert.dismiss();
      }
      // return alertText;
    } finally {
      acceptNextAlert = true;
    }
  }
}