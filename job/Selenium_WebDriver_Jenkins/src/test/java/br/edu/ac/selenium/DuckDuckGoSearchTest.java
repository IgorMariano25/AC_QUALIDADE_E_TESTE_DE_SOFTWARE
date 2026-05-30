package br.edu.ac.selenium;

import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Teste de aceitação simples usando Selenium WebDriver para validar
 * uma busca no DuckDuckGo. O Selenium Manager (Selenium 4.6+) baixa
 * automaticamente o chromedriver compatível com o Chrome instalado.
 */
class DuckDuckGoSearchTest {

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        // Necessário ao rodar dentro do contêiner do Jenkins
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("Deve abrir o DuckDuckGo e exibir resultados para 'Selenium WebDriver'")
    void deveBuscarNoDuckDuckGo() {
        driver.get("https://duckduckgo.com/");

        WebElement searchBox = driver.findElement(By.name("q"));
        searchBox.sendKeys("Selenium WebDriver");
        searchBox.submit();

        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(d -> d.getTitle() != null
                        && d.getTitle().toLowerCase().contains("selenium"));

        String title = driver.getTitle();
        Assertions.assertNotNull(title, "Título da página não deveria ser nulo");
        Assertions.assertTrue(title.toLowerCase().contains("selenium"),
                "O título deveria conter 'selenium', mas era: " + title);
    }
}
