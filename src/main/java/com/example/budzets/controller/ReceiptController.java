package com.example.budzets.controller;

import com.example.budzets.dto.CheckProductDTO;
import com.example.budzets.dto.ReceiptDTO;
import com.example.budzets.model.CheckProductEntity;
import com.example.budzets.model.ProductEntity;
import com.example.budzets.model.Receipt;
import com.example.budzets.model.ReceiptEntity;
import com.example.budzets.repository.CategoryRepository;
import com.example.budzets.repository.ProductRepository;
import com.example.budzets.repository.ReceiptRepository;
import com.example.budzets.service.ReceiptService;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/receipts")
@CrossOrigin(origins = "http://localhost:3000")
public class ReceiptController {

    private final ReceiptService receiptService;
    private final ProductRepository productRepository;
    private final ReceiptRepository receiptRepository;

    public ReceiptController(ReceiptService receiptService, ProductRepository productRepository, ReceiptRepository receiptRepository) {
        this.receiptService = receiptService;
        this.productRepository = productRepository;
        this.receiptRepository = receiptRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            File tempFile = File.createTempFile("receipt-", extension);

            file.transferTo(tempFile);

            ReceiptEntity saved = receiptService.handle(tempFile);
            return ResponseEntity.ok(saved);

        } catch (IllegalArgumentException e) {
            // ✅ Ja čeks jau eksistē — neatļaujam dublikātu
            return ResponseEntity.status(HttpStatus.CONFLICT).body("⚠️ " + e.getMessage());

        } catch (TesseractException e) {
            return ResponseEntity.badRequest().body("❌ Neizdevās nolasīt attēlu: " + e.getMessage());

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("❌ Faila apstrādes kļūda: " + e.getMessage());
        }
    }

    @GetMapping
    public List<ReceiptEntity> getAllReceipts() {
        return receiptRepository.findAll(Sort.by(Sort.Direction.DESC, "date"));
    }

    @GetMapping("/total")
    public Double getTotalSpent() {
        return receiptService.getTotalSpent();
    }

    @GetMapping("/filter")
    public List<ReceiptEntity> filterByDate(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return receiptRepository.findByDateBetween(start, end, Sort.by(Sort.Direction.DESC, "date"));
    }

    @GetMapping("/total/filter")
    public Double totalByDate(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return receiptService.getTotalSpentBetween(start, end);
    }

    @PostMapping("/import-folder")
    public ResponseEntity<String> importReceiptsFromFolder() {
        File folder = new File("scripts/receipts"); // ceļš relatīvs pret projekta root
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));

        if (files == null || files.length == 0) {
            return ResponseEntity.ok("Nav atrasti nevieni PDF faili.");
        }

        int importedCount = 0;
        for (File file : files) {
            try {
                receiptService.handle(file);
                importedCount++;
            } catch (Exception e) {
                System.err.println("❌ Kļūda apstrādājot failu: " + file.getName());
                e.printStackTrace();
            }
        }

        return ResponseEntity.ok("Importēti " + importedCount + " faili.");
    }

    @PostMapping("/manual")
    public ReceiptEntity createManualReceipt(@RequestBody ReceiptDTO dto) {
        ReceiptEntity receipt = new ReceiptEntity();
        receipt.setReceiptNumber(dto.getReceiptNumber());
        receipt.setDate(dto.getDate());
        receipt.setTotal(dto.getTotal());

        List<CheckProductEntity> checkProducts = new ArrayList<>();
        for (CheckProductDTO cp : dto.getProducts()) {
            ProductEntity product;

            if (cp.getProductId() != null) {
                product = productRepository.findById(cp.getProductId())
                        .orElseThrow(() -> new RuntimeException("Produkts nav atrasts"));
            } else {
                product = new ProductEntity();
                product.setName(cp.getName());
                product.setUnitPrice(cp.getUnitPrice());
                product = productRepository.save(product);
            }

            CheckProductEntity checkProduct = new CheckProductEntity();
            checkProduct.setProduct(product);
            checkProduct.setQuantity(cp.getQuantity());
            checkProduct.setTotalPrice(cp.getTotalPrice());
            checkProduct.setReceipt(receipt);
            checkProducts.add(checkProduct);
        }

        receipt.setProducts(checkProducts);
        return receiptRepository.save(receipt);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReceipt(@PathVariable Long id) {
        if (!receiptRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        receiptRepository.deleteById(id);
        return ResponseEntity.ok("Čeks veiksmīgi dzēsts.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReceipt(@PathVariable Long id, @RequestBody ReceiptDTO dto) {
        return receiptRepository.findById(id).map(existing -> {
            existing.setReceiptNumber(dto.getReceiptNumber());
            existing.setDate(dto.getDate());
            existing.setTotal(dto.getTotal());

            // Izdzēš vecos produktus
            existing.getProducts().clear();

            List<CheckProductEntity> newProducts = new ArrayList<>();
            for (CheckProductDTO cp : dto.getProducts()) {
                ProductEntity product;

                if (cp.getProductId() != null) {
                    product = productRepository.findById(cp.getProductId())
                            .orElseThrow(() -> new RuntimeException("Produkts nav atrasts"));
                } else {
                    product = new ProductEntity();
                    product.setName(cp.getName());
                    product.setUnitPrice(cp.getUnitPrice());
                    product = productRepository.save(product);
                }

                CheckProductEntity checkProduct = new CheckProductEntity();
                checkProduct.setProduct(product);
                checkProduct.setQuantity(cp.getQuantity());
                checkProduct.setTotalPrice(cp.getTotalPrice());
                checkProduct.setReceipt(existing);
                newProducts.add(checkProduct);
            }

            existing.setProducts(newProducts);
            return ResponseEntity.ok(receiptRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }
}
