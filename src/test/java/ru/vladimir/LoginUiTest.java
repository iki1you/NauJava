package ru.vladimir;

import java.time.Duration;
import java.util.UUID;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import ru.vladimir.dto.UserRequest;
import ru.vladimir.model.User;
import ru.vladimir.repository.UserRepository;
import ru.vladimir.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LoginUiTest {
    @LocalServerPort
    private int port;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private WebDriver driver;
    private User user;

    @BeforeEach
    void setUp() {
        user = userService.addUser(new UserRequest(
                "ui-" + UUID.randomUUID(), "Test", "User", "test-password"));

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        driver = new ChromeDriver(options);
    }

    @AfterEach
    void tearDown() {
        try {
            if (driver != null) {
                driver.quit();
            }
        } finally {
            if (user != null) {
                userRepository.deleteById(user.getId());
            }
        }
    }

    @Test
    void loginAndLogout() {
        String baseUrl = "http://localhost:" + port;
        driver.get(baseUrl + "/login");

        driver.findElement(By.name("username")).sendKeys(user.getLogin());
        driver.findElement(By.name("password")).sendKeys("test-password");
        driver.findElement(By.cssSelector("input[type='submit']")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlToBe(baseUrl + "/ui/projects"));

        assertEquals("Проекты", driver.findElement(By.tagName("h1")).getText());

        driver.findElement(By.cssSelector("form[action='/logout'] button")).click();
        wait.until(ExpectedConditions.urlToBe(baseUrl + "/login?logout"));

        assertEquals("Вход в систему", driver.findElement(By.tagName("h1")).getText());

        driver.get(baseUrl + "/ui/projects");
        wait.until(ExpectedConditions.urlToBe(baseUrl + "/login"));
    }
}
