package com.example.budzets.service;

import com.example.budzets.model.*;
import com.example.budzets.repository.ProductRepository;
import com.example.budzets.repository.ReceiptRepository;
import com.example.budzets.util.ImageParserUtil;
import com.example.budzets.util.PDFParserUtil;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ReceiptService {

    private final PDFParserUtil pdfParser;
    private final ImageParserUtil imageParser;
    private final ProductRepository productRepository;
    private final ReceiptRepository receiptRepository;

    public ReceiptService(PDFParserUtil pdfParser, ImageParserUtil imageParser,
                          ProductRepository productRepository, ReceiptRepository receiptRepository) {
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
        // Dublikātu pārbaude
        Optional<ReceiptEntity> existing = receiptRepository.findByReceiptNumber(parsed.getReceiptNumber());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Čeks ar numuru " + parsed.getReceiptNumber() + " jau eksistē!");
        }

        ReceiptEntity receiptEntity = new ReceiptEntity();
        receiptEntity.setReceiptNumber(parsed.getReceiptNumber());
        receiptEntity.setDate(parsed.getDate());
        receiptEntity.setTotal(round(parsed.getTotal()));

        List<CheckProductEntity> checkProducts = new ArrayList<>();
        for (Product product : parsed.getProducts()) {
            checkProducts.add(buildCheckProduct(product, receiptEntity));
        }

        receiptEntity.setProducts(checkProducts);
        return receiptRepository.save(receiptEntity);
    }

    public ReceiptEntity handleManual(Receipt manual) {
        ReceiptEntity receiptEntity = new ReceiptEntity();
        receiptEntity.setReceiptNumber(manual.getReceiptNumber());
        receiptEntity.setDate(manual.getDate());
        receiptEntity.setTotal(round(manual.getTotal()));

        List<CheckProductEntity> checkProducts = new ArrayList<>();
        for (Product product : manual.getProducts()) {
            checkProducts.add(buildCheckProduct(product, receiptEntity));
        }

        receiptEntity.setProducts(checkProducts);
        return receiptRepository.save(receiptEntity);
    }

    private CheckProductEntity buildCheckProduct(Product product, ReceiptEntity receiptEntity) {
            ProductEntity productEntity;

        if (product.getId() != null) {
            productEntity = productRepository.findById(product.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Produkta ID nav atrasts DB: " + product.getId()));
            } else {
            productEntity = productRepository
                    .findByNameAndUnitPrice(product.getName(), round(product.getUnitPrice()))
                    .orElseGet(() -> {
                        ProductEntity newProduct = new ProductEntity();
                        newProduct.setName(product.getName());
                        newProduct.setUnitPrice(round(product.getUnitPrice()));
                        return productRepository.save(newProduct);
                    });
            }

            CheckProductEntity checkProduct = new CheckProductEntity();
            checkProduct.setProduct(productEntity);
        checkProduct.setQuantity(roundQuantity(product.getQuantity()));
        checkProduct.setTotalPrice(round(product.getTotalPrice()));
        checkProduct.setDiscountAmount(product.getDiscountAmount());
            checkProduct.setReceipt(receiptEntity);

        return checkProduct;
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
