package com.example.budzets.util;

import com.example.budzets.model.Product;
import com.example.budzets.model.Receipt;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class ImageParserUtil {

    public Receipt parseImage(File imageFile) throws TesseractException {
        Tesseract tesseract = new Tesseract();
        String tessPath = "tessdata"; // vai pilnais ceļš

        // 👇 Izdrukā pilno ceļu un pārbauda vai lat.traineddata pastāv
        File trainedFile = new File(tessPath + "/lat.traineddata");
        System.out.println("🔍 Meklē failu: " + trainedFile.getAbsolutePath());

        if (!trainedFile.exists()) {
            throw new IllegalStateException("❌ lat.traineddata NAV atrasts: " + trainedFile.getAbsolutePath());
        } else {
            System.out.println("✅ lat.traineddata atrasts!");
        }

        tesseract.setDatapath(tessPath);
        tesseract.setLanguage("lat");

        String text = tesseract.doOCR(imageFile);
        return parseTextToReceipt(text);
    }

    private Receipt parseTextToReceipt(String text) {
        Receipt receipt = new Receipt();
        List<Product> products = new ArrayList<>();
        String[] lines = text.split("\n");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String lastLine = "";

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            System.out.println("OCR rinda: " + line);

            if (line.toLowerCase().contains("čeks")) {
                receipt.setReceiptNumber(line.replaceAll("[^\\d]", ""));
            }

            if (line.matches(".*\\d{4}-\\d{2}-\\d{2}.*\\d{2}:\\d{2}:\\d{2}")) {
                String[] parts = line.split("\\s+");
                receipt.setDate(LocalDateTime.parse(parts[0] + " " + parts[1], formatter));
            }

            // Pielaidīgāks regex produkta rindai
            if (line.matches(".*\\d{1,3},\\d{2}\\s*X\\s*\\d+(,\\d+)?\\s*(gab|kg).*")) {
                String name = lastLine;

                String[] parts = line.split("\\s+");
                try {
                    double unitPrice = Double.parseDouble(parts[0].replace(",", "."));
                    double quantity = Double.parseDouble(parts[2].replace(",", "."));
                    double total = unitPrice * quantity;

                    Product product = Product.builder()
                            .name(name)
                            .unitPrice(unitPrice)
                            .quantity(quantity)
                            .totalPrice(0.00)
                            .discountAmount(null)
                            .build();

                    products.add(product);
                } catch (Exception e) {
                    System.err.println("❌ Neizdevās parsēt produktu no: " + line);
                }
            }

            // Saglabā pēdējo rindu kā iespējamo nosaukumu
            lastLine = line;
        }

        receipt.setProducts(products);
        receipt.setTotal(products.stream().mapToDouble(Product::getTotalPrice).sum());
        return receipt;
    }
}
