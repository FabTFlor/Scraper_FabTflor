import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.io.FileOutputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class testjava {

    @Test
    void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");

        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/src/test/resources/chromedriver.exe");
        System.setProperty("webdriver.chrome.logfile", System.getProperty("user.dir") + "/src/test/resources/chromedriver.log");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            driver.get("https://salcobrand.cl/t/medicamentos");

            List<Medication> medications = new ArrayList<>();

            for (int page = 1; page <= 32; page++) {

                try {
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".pagination-sm .active a")));
                    WebElement currentPageElement = driver.findElement(By.cssSelector(".pagination-sm .active a"));
                    String currentPage = currentPageElement.getText();
                    System.out.println("Página actual: " + currentPage);

                    // Scroll hasta el fondo de la página
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    js.executeScript("window.scrollBy(0, 5000);");
                    Thread.sleep(5000);

                    js.executeScript("window.scrollBy(0, 2000);");
                    Thread.sleep(2000);

                    List<WebElement> productList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".inner-product-box")));
                    System.out.println("boxes: " + productList.size());

                    for (WebElement product : productList) {
                        String name = null;
                        String activeIngredient = null;
                        String precioFarmacia = null;

                        try {
                            name = product.findElement(By.cssSelector(".product-name.truncate")).getText();
                        } catch (Exception e) {
                            System.out.println("Nombre no encontrado");
                        }

                        try {
                            activeIngredient = product.findElement(By.cssSelector(".product-info.truncate")).getText();
                        } catch (Exception e) {
                            System.out.println("Compuesto activo no encontrado");
                        }

                        try {
                            precioFarmacia = product.findElement(By.cssSelector(".price.selling")).getText();
                        } catch (Exception e) {
                            System.out.println("Precio farmacia no encontrado");
                        }

                        System.out.println("Nombre: " + name);
                        System.out.println("Compuesto Activo: " + activeIngredient);
                        System.out.println("Precio Farmacia: " + precioFarmacia);
                        System.out.println("--------------------------------------------------------");

                        medications.add(new Medication(name, activeIngredient, precioFarmacia));
                    }

                    // Esperar a que el botón de siguiente página sea clickable y luego hacer clic
                    if (page < 32) {
                        WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[text()='»']")));
                        nextButton.click();
                        Thread.sleep(2000);
                        js.executeScript("window.scrollBy(0, -7000);");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
            exportToExcel(medications);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }

    public static void exportToExcel(List<Medication> medications) {
        String[] columns = {"Nombre", "Compuesto Activo", "Precio Farmacia"};

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Medicamentos");

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (Medication medication : medications) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(medication.getName());
            row.createCell(1).setCellValue(medication.getActiveIngredient());
            row.createCell(2).setCellValue(medication.getPrecioFarmacia());
        }

        try (FileOutputStream fileOut = new FileOutputStream(System.getProperty("user.dir") + "/output/medications.xlsx")) {
            workbook.write(fileOut);
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class Medication {
        private String name;
        private String activeIngredient;
        private String precioFarmacia;

        public Medication(String name, String activeIngredient, String precioFarmacia) {
            this.name = name;
            this.activeIngredient = activeIngredient;
            this.precioFarmacia = precioFarmacia;
        }

        public String getName() {
            return name;
        }

        public String getActiveIngredient() {
            return activeIngredient;
        }

        public String getPrecioFarmacia() {
            return precioFarmacia;
        }
    }
}
