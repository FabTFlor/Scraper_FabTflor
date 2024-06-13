
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.openqa.selenium.By;
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

public class scrapsimi {

    @Test
    void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");

        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/src/test/resources/chromedriver.exe");
        System.setProperty("webdriver.chrome.logfile", System.getProperty("user.dir") + "/src/test/resources/chromedriver.log");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));


        try {
            driver.get("https://www.drsimi.cl/medicamento");
            Thread.sleep(6000);


            for (int i = 0 ; i < 3 ; i++) {

                Thread.sleep(2000);
                WebElement npButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(text(),'Mostrar más')]")));
                npButton.click();

            }

            List<Medication> medications = new ArrayList<>();



                    List<WebElement> productList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".vtex-search-result-3-x-galleryItem.vtex-search-result-3-x-galleryItem--normal.vtex-search-result-3-x-galleryItem--grid.pa4")));
                    System.out.println("boxes: " + productList.size());

                    for (WebElement product : productList) {
                        String name = null;
                        String detail = null;
                        String precioFarmacia = null;

                        try {
                            name = product.findElement(By.cssSelector("span.vtex-product-summary-2-x-productBrand.vtex-product-summary-2-x-brandName.t-body")).getText();
                        } catch (Exception e) {
                            System.out.println("Nombre no encontrado");
                        }

                        try {
                            detail = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("span.vtex-product-summary-2-x-description"))).getText();
                        } catch (Exception e) {
                            System.out.println("Compuesto activo no encontrado");
                        }

                        try {
                            precioFarmacia = product.findElement(By.cssSelector("flex.mt5.mb4.pt0.pb0.justify-start.vtex-flex-layout-0-x-flexRowContent.vtex-flex-layout-0-x-flexRowContent--pricesQty.vtex-flex-layout-0-x-flexRowContent--pricesSummary.items-stretch.w-100")).getText();
                        } catch (Exception e) {
                            System.out.println("Precio farmacia no encontrado");
                        }

                        System.out.println("Nombre: " + name);
                        System.out.println("Detalle: " + detail);
                        System.out.println("Precio Farmacia: " + precioFarmacia);
                        System.out.println("--------------------------------------------------------");

                        medications.add(new Medication(name, detail, precioFarmacia));
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
        String[] columns = {"Nombre", "Detalle", "Precio Farmacia"};

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

        try (FileOutputStream fileOut = new FileOutputStream(System.getProperty("user.dir") + "/output/medicationSimi.xlsx")) {
            workbook.write(fileOut);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class Medication {
        private String name;
        private String detail;
        private String precioFarmacia;

        public Medication(String name, String activeIngredient, String precioFarmacia) {
            this.name = name;
            this.detail = activeIngredient;
            this.precioFarmacia = precioFarmacia;
        }

        public String getName() {
            return name;
        }

        public String getActiveIngredient() {
            return detail;
        }

        public String getPrecioFarmacia() {
            return precioFarmacia;
        }
    }
}
