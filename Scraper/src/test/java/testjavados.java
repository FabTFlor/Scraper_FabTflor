import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.time.Duration;

public class testjavados {

    @Test
    void setup() {
        // Configura las opciones del navegador
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");

        // Especifica el path correcto del ChromeDriver
        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/src/test/resources/chromedriver.exe");
        System.setProperty("webdriver.chrome.logfile", System.getProperty("user.dir") + "/src/test/resources/chromedriver.log");

        // Inicializa el WebDriver
        WebDriver driver = new ChromeDriver(options);

        // Espera hasta que la página esté cargada
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));




        driver.get("https://salcobrand.cl/t/medicamentos");

        // Iterar a través de las páginas
        for (int page = 1; page <= 32; page++) {

            try {
                // Esperar a que los medicamentos se carguen
                Thread.sleep(5000);
                WebElement currentPageElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".pagination-sm .active a")));
                String currentPage = currentPageElement.getText();
                System.out.println("Página actual: " + currentPage);

                // Realiza las operaciones necesarias en la página actual aquí

                // Scroll hasta el fondo de la página
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("window.scrollBy(0, 5000);");
                Thread.sleep(5000);

                js.executeScript("window.scrollBy(0, 2000);");
                Thread.sleep(5000);






                // Esperar a que el botón de siguiente página sea clickable y luego hacer clic
                if (page < 32) {
                    WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[text()='»']")));
                    nextButton.click();
                    Thread.sleep(2000);
                    js.executeScript("window.scrollBy(0, 7000);");

                }
            } catch (Exception e) {
                break;
            }


        }

        // Cerrar el navegador
        driver.quit();
    }
}
