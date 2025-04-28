package com.example.budzets.parser;

import com.example.budzets.model.Product;
import com.example.budzets.model.Receipt;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class PDFParserUtil {

    public Receipt parseReceipt(File pdfFile) throws IOException {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            return parseTextToReceipt(text);
        }
    }

    private Receipt parseTextToReceipt(String text) {
        Receipt receipt = new Receipt();
        List<Product> products = new ArrayList<>();

        String[] lines = text.split("\n");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();

            if (line.startsWith("Čeks")) {
                receipt.setReceiptNumber(line.replace("Čeks", "").trim());
            }

            if (line.matches(".*\\d{2}\\.\\d{2}\\.\\d{4}.*\\d{2}:\\d{2}:\\d{2}")) {
                String[] parts = line.split("\\s+");
                String date = parts[parts.length - 2];
                String time = parts[parts.length - 1];
                receipt.setDate(LocalDateTime.parse(date + " " + time, formatter));
            }

        // Atpazīt produktu rindu ar gabaliem vai kilogramiem, atļaujot arī decimāldaļas daudzumā
            // Produkta rinda: 1,29 X 1 gab. vai 0,89 X 0,658 kg
            if (line.matches(".*\\d+,\\d{2}\\s+X\\s+\\d+(,\\d+)?\\s*(gab|kg).*")) {
                String name = "";
                if (i - 1 >= 0 && !lines[i - 1].trim().toLowerCase().startsWith("čeks")) {
                    name = lines[i - 1].trim();
                }

                // Ja nosaukums varētu būt sadalīts divās rindās — pārbauda iepriekšējo rindu
                if (i - 2 >= 0 && !lines[i - 2].trim().toLowerCase().startsWith("čeks")) {
                    String prevLine = lines[i - 2].trim();

                    // Ja tajā nav cenas (nebeidzas ar cipariem + A/N/B), tad pievieno pie nosaukuma
                    if (!prevLine.matches(".*\\d+,\\d{2}\\s+[ANB]?$")) {
                        name = prevLine + " " + name;
                    }
                }

                String[] parts = line.trim().split("\\s+");
                double unitPrice = Double.parseDouble(parts[0].replace(",", "."));
                double quantity = Double.parseDouble(parts[2].replace(",", "."));
                double totalPrice = unitPrice * quantity;
                Double discountAmount = null;

                // Pārbaude uz atlaidi nākamajās 1–2 rindās
                for (int j = 1; j <= 2 && (i + j) < lines.length; j++) {
                    String nextLine = lines[i + j].trim().toLowerCase();

                    if (nextLine.contains("cena ar atlaidi")) {
                        // Izvelk "cena ar atlaidi" vērtību
                        java.util.regex.Matcher matcher = java.util.regex.Pattern
                                .compile("cena ar atlaidi.*?(\\d+,\\d{2})")
                                .matcher(nextLine);
                        if (matcher.find()) {
                            double discountedTotal = Double.parseDouble(matcher.group(1).replace(",", "."));
                            totalPrice = discountedTotal;

                            // ✅ Mēģinām izvilkt reālo atlaidi, kas parādās kā -0,11 A
                            java.util.regex.Matcher discountMatcher = java.util.regex.Pattern
                                    .compile("(-\\d+,\\d{2})\\s*A")
                                    .matcher(nextLine);
                            if (discountMatcher.find()) {
                                discountAmount = Double.parseDouble(discountMatcher.group(1).replace(",", ".").replace("-", ""));
                            } else {
                                // Ja nav atlaides summas, tad aprēķinām
                                discountAmount = (unitPrice * quantity) - discountedTotal;
                                discountAmount = Math.round(discountAmount * 100.0) / 100.0;
                            }
                        }
                        break;
                    }
                }

                Product product = Product.builder()
                        .name(name)
                        .unitPrice(unitPrice)
                        .quantity(quantity)
                        .totalPrice(totalPrice)
                        .discountAmount(discountAmount)
                        .build();

                products.add(product);
            }

        }

        receipt.setProducts(products);
        double total = products.stream().mapToDouble(Product::getTotalPrice).sum();
        receipt.setTotal(total);

        return receipt;
    }
}