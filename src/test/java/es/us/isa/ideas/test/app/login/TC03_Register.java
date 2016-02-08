package es.us.isa.ideas.test.app.login;

import es.us.isa.ideas.test.utils.ExpectedActions;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import es.us.isa.ideas.test.utils.IdeasStudioActions;
import es.us.isa.ideas.test.utils.TestCase;
import org.openqa.selenium.By;
import static org.junit.Assert.assertTrue;

/**
 * Applied Software Engineering Research Group (ISA Group) University of
 * Sevilla, Spain
 *
 * @author Felipe Vieira da Cunha Serafim <fvieiradacunha@us.es>
 * @version 1.0
 */
// Connect to database using a test data.xml
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {
//    "classpath:**/data.xml"
//})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TC03_Register extends es.us.isa.ideas.test.utils.TestCase {

//    @Autowired
//    ResearcherRepository researcherRepository;
    private static final Logger LOG = Logger.getLogger(TC03_Register.class
            .getName());
    private static boolean testResult = true;
    private static String name = "";
    private static String email = "";
    private static String phone = "";
    private static String address = "";
    private static String password = "";

    @BeforeClass
    public static void setUp() throws InterruptedException {
        LOG.log(Level.INFO, "Init TC03_Register...");
        logout();
    }

    @AfterClass
    public static void tearDown() throws InterruptedException {
        LOG.log(Level.INFO, "TC03_Register finished");
        logout();
    }

    @After
    public void tearDownTest() {
        LOG.log(Level.INFO, "testResult value: {0}", testResult);
    }

    // Database will must be prepared by 
//    @Test
//    public void step01_prepareDatabase() {
//
//        // Check if user already exists
//        String email = getAutotesterEmail();
//        Researcher researcher = researcherRepository.findByEmail(email);
//        if (researcher != null) {
//            // Remove researcher
//            int researcherId = researcher.getId();
//            researcherRepository.delete(researcherId);
//        }
//
//    }
    
    @Test
    public void step02_goHomePage() {
        testResult = IdeasStudioActions.goHomePage();
        assertTrue(testResult);
    }

    @Test
    public void step03_loadAutoTesterProperties() {

        name = getAutotesterName();
        email = getAutotesterEmail();
        phone = getAutotesterPhone();
        address = getAutotesterAddress();

        testResult = validatePropertyValues(name, email, phone, address);
        assertTrue(testResult);

    }

    @Test
    public void step04_register() {

        Boolean register = IdeasStudioActions.registerUser(name, email, phone, address);
        waitForVisibleSelector("#statusPanel");
        String statusPanelText = getWebDriver().findElement(By.cssSelector("#statusPanel")).getText();

        testResult = register && !statusPanelText.contains("The email address you entered is already in use");
        assertTrue(testResult);

    }

    @Test
    public void step05_loginToGmail() {

        getWebDriver().get("https://www.gmail.com");

        waitForVisibleSelector("#Email");

        ExpectedActions action = getExpectedActions();
        action.sendKeys(By.cssSelector("#Email"), getAutotesterEmail());
        action.click(By.cssSelector("#next"));
        action.sendKeys(By.cssSelector("#Passwd"), getAutotesterEmailPassword());
        action.click(By.cssSelector("#signIn"));

        testResult = getCurrentUrl().contains("mail.google.com/mail/u");
        assertTrue(testResult);

    }

    @Test
    public void step06_validateRegisterByEmail() throws InterruptedException {

        String selectorConfirmationEmail = "#\\3a 2 > div > div > div.UI tbody tr:first-child td:nth-child(4)";
        waitForVisibleSelector(selectorConfirmationEmail);
        getExpectedActions().click(By.cssSelector(selectorConfirmationEmail)); // opening

        waitForVisibleSelector(".ads");
        String urlConfirmation = (String) getJs()
                .executeScript(
                        "return document.getElementsByClassName('ads')[0].textContent.match(/http.+code=[a-zA-Z0-9\\-]+/i)[0]");

        Thread.sleep(1000);
        deleteCurrentGmailEmail();

        Thread.sleep(2000);
        getWebDriver().get(urlConfirmation);

        String selectorModalTitle = "#message > div > div > div.modal-header > h4";
        waitForVisibleSelector(selectorModalTitle);
        String modalTitle = TestCase.getWebDriver()
                .findElement(By.cssSelector(selectorModalTitle)).getText();

        testResult = "Account validated successfully".equals(modalTitle);
        assertTrue(testResult);

    }

    @Test
    public void step07_copyUserPassword() throws InterruptedException {

        getWebDriver().get("https://www.gmail.com");

        String selectorEmail = "#\\3a 2 > div > div > div.UI tbody tr:first-child td:nth-child(4)";
        waitForVisibleSelector(selectorEmail);
        getExpectedActions().click(By.cssSelector(selectorEmail));

        LOG.info("Opening \'Welcome to IDEAS\' email");

        waitForVisibleSelector(".gs");
        String scriptCopyPass = "var str=document.getElementsByClassName('gs')[0].textContent;"
                + "return str.match(/([0-9a-zA-Z]+-)+([0-9a-zA-Z]+)/i)[0];";
        String pass = (String) getJs().executeScript(scriptCopyPass);

        LOG.log(Level.INFO, "Copying user password ''{0}''", pass);

        testResult = !pass.equals("");
        assertTrue(testResult);

    }

    @Test
    public void step08_loginWithCopiedPassword() throws InterruptedException {

        Thread.sleep(2000);

        waitForVisibleSelector(".gs");
        String scriptCopyPass = "var str=document.getElementsByClassName('gs')[0].textContent;"
                + "return str.match(/([0-9a-zA-Z]+-)+([0-9a-zA-Z]+)/i)[0];";
        password = (String) getJs().executeScript(scriptCopyPass);

        setAutotesterPassword(password);

        Thread.sleep(1000);
        deleteCurrentGmailEmail();

        Thread.sleep(2000);

        testResult = loginWithParams(getAutotesterUser(), password);
        assertTrue(testResult);

    }
}
