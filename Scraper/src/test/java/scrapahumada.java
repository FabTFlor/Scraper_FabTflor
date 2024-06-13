
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
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class scrapahumada {

    @Test
    void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");

        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/src/test/resources/chromedriver.exe");
        System.setProperty("webdriver.chrome.logfile", System.getProperty("user.dir") + "/src/test/resources/chromedriver.log");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));


        try {
            driver.get("https://www.farmaciasahumada.cl/medicamentos");
            Thread.sleep(6000);


            for (int i = 0 ; i < 15 ; i++) {

                Thread.sleep(750);
                WebElement npButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Más Resultados')]")));
                npButton.click();

            }

            List<Medication> medications = new ArrayList<>();



                    List<WebElement> productList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".tile-body")));
                    System.out.println("boxes: " + productList.size());

                    for (WebElement product : productList) {
                        String name = null;
                        String lab = null;
                        String precioFarmacia = null;

                        try {
                            name = product.findElement(By.cssSelector("a.link")).getText();
                        } catch (Exception e) {
                            System.out.println("Nombre no encontrado");
                        }

                        try {
                            lab = product.findElement(By.cssSelector("span.link")).getText();
                        } catch (Exception e) {
                            System.out.println("Compuesto activo no encontrado");
                        }

                        try {
                            precioFarmacia = product.findElement(By.cssSelector("span.value.d-flex.align-items-center")).getText();
                        } catch (Exception e) {
                            System.out.println("Precio farmacia no encontrado");
                        }

                        System.out.println("Nombre: " + name);
                        System.out.println("Laboratorio: " + lab);
                        System.out.println("Precio Farmacia: " + precioFarmacia);
                        System.out.println("--------------------------------------------------------");

                        medications.add(new Medication(name, lab, precioFarmacia));
                    }




            // Exportar la lista de medicamentos a un archivo Excel
            exportToExcel(medications);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }

    public void exportToExcel(List<Medication> medications) {
        String[] columns = {"Nombre", "Laboratorio", "Precio Farmacia"};

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

        try (FileOutputStream fileOut = new FileOutputStream(System.getProperty("user.dir") + "/output/medicationsahumada.xlsx")) {
            workbook.write(fileOut);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class Medication {
        private String name;
        private String lab;
        private String precioFarmacia;

        public Medication(String name, String activeIngredient, String precioFarmacia) {
            this.name = name;
            this.lab = activeIngredient;
            this.precioFarmacia = precioFarmacia;
        }

        public String getName() {
            return name;
        }

        public String getActiveIngredient() {
            return lab;
        }

        public String getPrecioFarmacia() {
            return precioFarmacia;
        }
    }
}
