import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class YandexMarketTest {

    private Properties properties = new Properties();

    private WebDriver driver;

    private List<String> displayedCategoriesLinks = new ArrayList<>();


    @BeforeClass
    public void testSetup() throws IOException {
        properties.load(ClassLoader.getSystemResourceAsStream("conf.properties"));
        if (properties.getProperty("browser").equals("chrome")) {
            driver = new ChromeDriver();
        } else if (properties.getProperty("browser").equals("firefox")) {
            driver = new FirefoxDriver();
        }
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().window().maximize();
    }

    @Test(description = "Открываем страницу и преходим на логин страницу", priority = 1)
    public void openLoginPage() {
        driver.get(properties.getProperty("yandex.market.page"));
        YandexMarketHomePage homePage = new YandexMarketHomePage(driver);
        homePage.clickLoginButton();
        switchToTheRightHandle();
        assertEquals(driver.getTitle(), "Авторизация");
    }

    @Test(description = "Авторизуемся", priority = 2)
    public void signIn() {
        YandexLoginPage loginPage = new YandexLoginPage(driver);
        assertTrue(loginPage.isInitialized());
        loginPage.enterLogin(properties.getProperty("login"));
        loginPage.clickSignInButton();
        loginPage.enterPassword(properties.getProperty("password"));
        loginPage.clickSignInButton();
        switchToTheRightHandle();
        YandexMarketHomeAuthorizedPage authorizedHomePage = new YandexMarketHomeAuthorizedPage(driver);
        assertTrue(authorizedHomePage.isAuthorized());
    }


    @Test(description = "Получаем спсиок видимых категории", priority = 3)
    public void getAllDisplayedCategories() {
        switchToTheRightHandle();
        driver.get(properties.getProperty("yandex.market.page"));
        YandexMarketHomeAuthorizedPage authorizedHomePage = new YandexMarketHomeAuthorizedPage(driver);
        displayedCategoriesLinks = authorizedHomePage.getDisplayedCategoryLinks();
        assertTrue(displayedCategoriesLinks.size() > 0);
    }

    @Test(description = "Открываем страницу случайной категории", priority = 4)
    public void openCategoryPage() {
        int randomInt = new Random().nextInt(displayedCategoriesLinks.size() - 1);
        String randomCategoryLink = displayedCategoriesLinks.get(randomInt);
        driver.get(randomCategoryLink);
        assertEquals(driver.getCurrentUrl(), randomCategoryLink);
    }

    @Test(description = "Открываем главную страницу, получаем список всех категории и сохраняем его в СSV файле.", priority = 5)
    public void getAllCategories() throws IOException {
        switchToTheRightHandle();
        YandexMarketHomeAuthorizedPage authorizedHomePage = new YandexMarketHomeAuthorizedPage(driver);
        List<String> allCategoriesLinks = authorizedHomePage.getAllCategoriesLinks();
        writeCategoriesToCSV(allCategoriesLinks);
        assertTrue(allCategoriesLinks.size() > 0);
    }

    @Test(description = "Сравниваем категории из CSV файла с категориями полученными в getAllDisplayedCategories", priority = 6)
    public void compareCategories() throws IOException {
        List<String> categories = readCategoriesFromCSV();
        assertTrue(categories.containsAll(displayedCategoriesLinks));
    }

    private void writeCategoriesToCSV(List<String> allCategoriesLinks) throws IOException {
        FileWriter csvWriter = new FileWriter("categories.csv");
        for (String catLink : allCategoriesLinks) {
            csvWriter.append(catLink);
            csvWriter.append(",");
        }
        csvWriter.flush();
        csvWriter.close();
    }

    private List<String> readCategoriesFromCSV() throws IOException {
        List<String> categories = new ArrayList<>();
        BufferedReader csvReader = new BufferedReader(new FileReader("categories.csv"));
        String row;
        while ((row = csvReader.readLine()) != null) {
            String[] data = row.split(",");
            categories.addAll(Arrays.asList(data));
        }
        csvReader.close();
        return categories;
    }


    private void switchToTheRightHandle() {
        List<String> tabHandles = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(tabHandles.get(tabHandles.size() - 1));
    }


    @AfterClass
    public void afterClass() {
        driver.quit();
    }

}
