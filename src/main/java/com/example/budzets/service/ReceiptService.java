package com.example.budzets.service;

import com.example.budzets.repository.ProductRepository;
import com.example.budzets.repository.ReceiptRepository;
import com.example.budzets.model.*;
import com.example.budzets.util.PDFParserUtil;
import com.example.budzets.util.ImageParserUtil;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReceiptService {
    private final PDFParserUtil pdfParser;
    private final ImageParserUtil imageParser;
    private final ProductRepository productRepository;
    private final ReceiptRepository receiptRepository;

    public ReceiptService(PDFParserUtil pdfParser,
                          ImageParserUtil imageParser,
                          ProductRepository productRepository,
                          ReceiptRepository receiptRepository) {
        this.pdfParser = pdfParser;
        this.imageParser = imageParser;
        this.productRepository = productRepository;
        this.receiptRepository = receiptRepository;
    }

    public ReceiptEntity handle(File file) throws IOException, TesseractException {
        String fileName = file.getName().toLowerCase();
        Receipt parsed;

        if (fileName.endsWith(".pdf")) {
            parsed = pdfParser.parseReceipt(file);
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png")) {
            parsed = imageParser.parseImage(file);
        } else {
            throw new IllegalArgumentException("Nepieļauts faila formāts: " + fileName);
        }

        return saveParsedReceipt(parsed);
    }

    private ReceiptEntity saveParsedReceipt(Receipt parsed) {
        // ✅ Pārbaude uz dublikātu
        Optional<ReceiptEntity> existing = receiptRepository.findByReceiptNumber(parsed.getReceiptNumber());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Čeks ar numuru " + parsed.getReceiptNumber() + " jau eksistē!");
        }

        ReceiptEntity receiptEntity = new ReceiptEntity();
        receiptEntity.setReceiptNumber(parsed.getReceiptNumber());
        receiptEntity.setDate(parsed.getDate());
        receiptEntity.setTotal(round(parsed.getTotal()));

        List<CheckProductEntity> checkProducts = new ArrayList<>();

        for (Product p : parsed.getProducts()) {
            ProductEntity productEntity = productRepository
                    .findByNameAndUnitPrice(p.getName(), p.getUnitPrice())
                    .orElseGet(() -> {
                        ProductEntity newProduct = new ProductEntity();
                        newProduct.setName(p.getName());
                        newProduct.setUnitPrice(round(p.getUnitPrice()));
                        return productRepository.save(newProduct);
                    });

            CheckProductEntity checkProduct = new CheckProductEntity();
            checkProduct.setProduct(productEntity);
            checkProduct.setQuantity(roundQuantity(p.getQuantity()));
            checkProduct.setTotalPrice(round(p.getTotalPrice()));
            checkProduct.setDiscountAmount(p.getDiscountAmount());
            checkProduct.setReceipt(receiptEntity);

            checkProducts.add(checkProduct);
        }

        receiptEntity.setProducts(checkProducts);
        return receiptRepository.save(receiptEntity);
    }

    public ReceiptEntity handleManual(Receipt manual) {
        ReceiptEntity receiptEntity = new ReceiptEntity();
        receiptEntity.setReceiptNumber(manual.getReceiptNumber());
        receiptEntity.setDate(manual.getDate());
        receiptEntity.setTotal(manual.getTotal());

        List<CheckProductEntity> checkProducts = new ArrayList<>();

        for (Product p : manual.getProducts()) {
            ProductEntity productEntity;

            if (p.getId() != null) {
                productEntity = productRepository.findById(p.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Produkta ID nav atrasts DB: " + p.getId()));
            } else {
                productEntity = new ProductEntity();
                productEntity.setName(p.getName());
                productEntity.setUnitPrice(p.getUnitPrice());
                productEntity = productRepository.save(productEntity);
            }

            CheckProductEntity checkProduct = new CheckProductEntity();
            checkProduct.setProduct(productEntity);
            checkProduct.setQuantity(p.getQuantity());
            checkProduct.setTotalPrice(p.getTotalPrice());
            checkProduct.setDiscountAmount(p.getDiscountAmount());
            checkProduct.setReceipt(receiptEntity);

            checkProducts.add(checkProduct);
        }

        receiptEntity.setProducts(checkProducts);
        return receiptRepository.save(receiptEntity);
    }

    public List<ReceiptEntity> getAllReceipts() {
        return receiptRepository.findAll();
    }

    public Double getTotalSpent() {
        return receiptRepository.findTotalSpent();
    }

    public List<ReceiptEntity> getByDateRange(LocalDateTime start, LocalDateTime end) {
        return receiptRepository.findByDateBetween(start, end, Sort.by(Sort.Direction.DESC, "date"));
    }

    public Double getTotalSpentBetween(LocalDateTime start, LocalDateTime end) {
        return Optional.ofNullable(receiptRepository.findTotalSpentBetween(start, end)).orElse(0.0);
    }

    private double round(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private double roundQuantity(double value) {
        return BigDecimal.valueOf(value).setScale(3, RoundingMode.HALF_UP).doubleValue();
    }
}
