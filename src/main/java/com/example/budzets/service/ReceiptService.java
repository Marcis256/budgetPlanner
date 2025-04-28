package com.example.budzets.service;

import com.example.budzets.dto.CheckProductDTO;
import com.example.budzets.dto.ReceiptDTO;
import com.example.budzets.model.*;
import com.example.budzets.repository.ReceiptRepository;
import com.example.budzets.parser.ImageParserUtil;
import com.example.budzets.parser.PDFParserUtil;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static com.example.budzets.util.RoundUtil.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ReceiptService {

    private final PDFParserUtil pdfParser;
    private final ImageParserUtil imageParser;
    private final ReceiptRepository receiptRepository;
    private final ProductService productService;

    public ReceiptService(PDFParserUtil pdfParser, ImageParserUtil imageParser, ReceiptRepository receiptRepository, ProductService productService) {
        this.pdfParser = pdfParser;
        this.imageParser = imageParser;
        this.receiptRepository = receiptRepository;
        this.productService = productService;
    }

    public ReceiptEntity parseAndSaveReceiptFromFile(File file) throws IOException, TesseractException {
        Receipt parsed;

        if (file.getName().toLowerCase().endsWith(".pdf")) {
            parsed = pdfParser.parseReceipt(file);
        } else if (file.getName().matches("(?i).*\\.(jpg|jpeg|png)$")) {
            parsed = imageParser.parseImage(file);
        } else {
            throw new IllegalArgumentException("Nepieļauts faila formāts: " + file.getName());
        }

        return saveParsedReceipt(parsed);
    }

    public Double getTotalSpent() {
        return receiptRepository.findTotalSpent();
    }

    public List<ReceiptEntity> getByDateRangeSorted(LocalDateTime start, LocalDateTime end) {
        return receiptRepository.findByDateBetween(start, end, Sort.by(Sort.Direction.DESC, "date"));
    }

    public Double getTotalSpentBetween(LocalDateTime start, LocalDateTime end) {
        return Optional.ofNullable(receiptRepository.findTotalSpentBetween(start, end)).orElse(0.0);
    }

    public ReceiptEntity createManualReceiptFromDTO(ReceiptDTO dto) {
        ReceiptEntity receipt = buildReceipt(dto.getReceiptNumber(), dto.getDate(), dto.getTotal(), dto.getProducts(), true);
        return receiptRepository.save(receipt);
    }

    public ReceiptEntity updateReceiptFromDTO(Long id, ReceiptDTO dto) {
        ReceiptEntity existing = receiptRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Čeks nav atrasts: ID = " + id));

        existing.setReceiptNumber(dto.getReceiptNumber());
        existing.setDate(dto.getDate());
        existing.setTotal(round(dto.getTotal()));

        List<CheckProductEntity> updatedProducts = dto.getProducts().stream().map(cp -> {
            ProductEntity product = cp.getProductId() != null
                    ? productService.findByIdOrThrow(cp.getProductId())
                    : productService.createNewProduct(cp.getName(), cp.getUnitPrice());

            return CheckProductEntity.builder()
                    .product(product)
                    .quantity(cp.getQuantity())
                    .totalPrice(cp.getTotalPrice())
                    .discountAmount(cp.getDiscountAmount())
                    .receipt(existing)
                    .build();
        }).toList();

        existing.getProducts().clear();
        existing.setProducts(updatedProducts);

        return receiptRepository.save(existing);
    }

    public boolean deleteReceiptById(Long id) {
        if (!receiptRepository.existsById(id)) {
            return false;
        }
        receiptRepository.deleteById(id);
        return true;
    }

    public ReceiptEntity handleUploadedFile(MultipartFile multipartFile) throws IOException, TesseractException {
        File tempFile = File.createTempFile("receipt-", getFileExtension(multipartFile.getOriginalFilename()));
        multipartFile.transferTo(tempFile);
        return parseAndSaveReceiptFromFile(tempFile);
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }

    public List<ReceiptEntity> getAllSortedByDateDesc() {
        return receiptRepository.findAll(Sort.by(Sort.Direction.DESC, "date"));
    }

    public List<CheckProductEntity> getCheckProductsWithCategoryByDateRange(LocalDateTime start, LocalDateTime end) {
        return getByDateRangeSorted(start, end).stream()
                    .flatMap(receipt -> receipt.getProducts().stream())
                    .filter(cp -> cp.getProduct().getCategory() != null)
                    .toList();
    }





    private ReceiptEntity createReceiptEntity(Receipt parsed) {
        return buildReceipt(parsed.getReceiptNumber(), parsed.getDate(), parsed.getTotal(), parsed.getProducts(), false);
    }

    private ReceiptEntity buildReceipt(String receiptNumber, LocalDateTime date, double total, List<? extends Object> rawProducts, boolean isDto) {
        ReceiptEntity receipt = new ReceiptEntity();
        receipt.setReceiptNumber(receiptNumber);
        receipt.setDate(date);
        receipt.setTotal(round(total));

        List<CheckProductEntity> checkProducts;

        if (isDto) {
            checkProducts = ((List<CheckProductDTO>) rawProducts).stream().map(cp -> {
                ProductEntity product = (cp.getProductId() != null)
                        ? productService.findByIdOrThrow(cp.getProductId())
                        : productService.createNewProduct(cp.getName(), cp.getUnitPrice());

                return CheckProductEntity.builder()
                        .product(product)
                        .quantity(cp.getQuantity())
                        .totalPrice(cp.getTotalPrice())
                        .discountAmount(cp.getDiscountAmount())
                        .receipt(receipt)
                        .build();
            }).toList();
        } else {
            checkProducts = ((List<Product>) rawProducts).stream().map(p -> {
                return buildCheckProduct(p, receipt);
            }).toList();
        }

        receipt.setProducts(checkProducts);
        return receipt;
    }

    private ReceiptEntity saveParsedReceipt(Receipt parsed) {
        if (receiptRepository.findByReceiptNumber(parsed.getReceiptNumber()).isPresent()) {
            throw new IllegalArgumentException("Čeks ar numuru " + parsed.getReceiptNumber() + " jau eksistē!");
        }
        return receiptRepository.save(createReceiptEntity(parsed));
    }

    private CheckProductEntity buildCheckProduct(Product product, ReceiptEntity receiptEntity) {
        ProductEntity productEntity = (product.getId() != null)
                ? productService.findByIdOrThrow(product.getId())
                : productService.findOrCreateProductByNameAndPrice(product.getName(), product.getUnitPrice());

        return CheckProductEntity.builder()
                .product(productEntity)
                .quantity(roundQuantity(product.getQuantity()))
                .totalPrice(round(product.getTotalPrice()))
                .discountAmount(product.getDiscountAmount())
                .receipt(receiptEntity)
                .build();
    }
}
