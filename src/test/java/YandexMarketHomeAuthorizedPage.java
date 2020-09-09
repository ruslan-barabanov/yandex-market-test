import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class YandexMarketHomeAuthorizedPage extends PageObject {

    public YandexMarketHomeAuthorizedPage(WebDriver driver) {
        super(driver);
    }

    public boolean isAuthorized() {
        try {
            driver.findElement(By.linkText("Войти"));
        } catch (NoSuchElementException e) {
            return true;
        }
        return false;
    }

    public List<String> getDisplayedCategoryLinks() {
        List<String> categoriesLinks = new ArrayList<>();
        List<WebElement> categoriesList = driver.findElements(By.cssSelector("div[data-zone-name=\"category-link\"]"));
        List<WebElement> displayedCategories = categoriesList.stream()
                .filter(e -> e.getText() != null && !e.getText().trim().isEmpty()
                        && !e.getText().contains("Скидки") && !e.getText().contains("Журнал"))
                .collect(Collectors.toList());
        displayedCategories.forEach(e -> categoriesLinks.add(e.findElement(By.cssSelector("a")).getAttribute("href")));
        return categoriesLinks;
    }

    public List<String> getAllCategoriesLinks() {
        List<String> categoriesLinks = new ArrayList<>();
        WebElement allCatButton = driver.findElement(By.cssSelector("div[data-zone-name=\"all-categories\"]"));
        allCatButton.click();
        WebElement catTabList = driver.findElement(By.cssSelector("div[role=\"tablist\"]"));
        List<WebElement> allCategoriesList = catTabList.findElements(By.cssSelector("div[data-zone-name=\"category-link\"]"));
        allCategoriesList.forEach(e -> categoriesLinks.add(e.findElement(By.cssSelector("a")).getAttribute("href")));
        return categoriesLinks;
    }

}
