package com.example.budzets.controller;

import com.example.budzets.dto.ReceiptDTO;
import com.example.budzets.model.ReceiptEntity;
import com.example.budzets.service.ReceiptService;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/receipts")
@CrossOrigin(origins = "http://localhost:3000")
public class ReceiptController {

    private final ReceiptService receiptService;

    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        try {
            ReceiptEntity saved = receiptService.handleUploadedFile(file);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("⚠️ " + e.getMessage());
        } catch (TesseractException e) {
            return ResponseEntity.badRequest().body("❌ Neizdevās nolasīt attēlu: " + e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("❌ Faila apstrādes kļūda: " + e.getMessage());
        }
    }

    @GetMapping
    public List<ReceiptEntity> getAllReceipts() {
        return receiptService.getAllSortedByDateDesc();
    }

    @GetMapping("/total")
    public Double getTotalSpent() {
        return receiptService.getTotalSpent();
    }

    @GetMapping("/filter")
    public List<ReceiptEntity> filterByDate(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return receiptService.getByDateRangeSorted(start, end);
    }

    @GetMapping("/total/filter")
    public Double totalByDate(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return receiptService.getTotalSpentBetween(start, end);
    }

    @PostMapping("/import-folder")
    public ResponseEntity<String> importReceiptsFromFolder() {
        File folder = new File("scripts/receipts");
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));

        if (files == null || files.length == 0) {
            return ResponseEntity.ok("Nav atrasti nevieni PDF faili.");
        }

        int importedCount = 0;
        for (File file : files) {
            try {
                receiptService.parseAndSaveReceiptFromFile(file);
                importedCount++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ResponseEntity.ok("Importēti " + importedCount + " faili.");
    }

    @PostMapping("/manual")
    public ReceiptEntity createManualReceipt(@RequestBody ReceiptDTO dto) {
        return receiptService.createManualReceiptFromDTO(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReceipt(@PathVariable Long id) {
        return receiptService.deleteReceiptById(id)
                ? ResponseEntity.ok("Čeks veiksmīgi dzēsts.")
                : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReceipt(@PathVariable Long id, @RequestBody ReceiptDTO dto) {
        try {
            ReceiptEntity updated = receiptService.updateReceiptFromDTO(id, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
