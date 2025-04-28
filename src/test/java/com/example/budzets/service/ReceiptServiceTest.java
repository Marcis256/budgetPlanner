package com.example.budzets.service;

import com.example.budzets.dto.CheckProductDTO;
import com.example.budzets.dto.ReceiptDTO;
import com.example.budzets.model.*;
import com.example.budzets.parser.ImageParserUtil;
import com.example.budzets.parser.PDFParserUtil;
import com.example.budzets.repository.ReceiptRepository;
import net.sourceforge.tess4j.TesseractException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ReceiptServiceTest {

    private PDFParserUtil pdfParser;
    private ImageParserUtil imageParser;
    private ReceiptRepository receiptRepository;
    private ProductService productService;
    private ReceiptService receiptService;

    @BeforeEach
    void setUp() {
        pdfParser = mock(PDFParserUtil.class);
        imageParser = mock(ImageParserUtil.class);
        receiptRepository = mock(ReceiptRepository.class);
        productService = mock(ProductService.class);

        receiptService = new ReceiptService(pdfParser, imageParser, receiptRepository, productService);
    }

    @Test
    void testGetTotalSpentBetweenReturnsValue() {
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();
        when(receiptRepository.findTotalSpentBetween(start, end)).thenReturn(123.45);

        double result = receiptService.getTotalSpentBetween(start, end);
        assertEquals(123.45, result);
    }

    @Test
    void testDeleteReceiptByIdReturnsTrue() {
        when(receiptRepository.existsById(1L)).thenReturn(true);
        boolean result = receiptService.deleteReceiptById(1L);
        assertTrue(result);
        verify(receiptRepository).deleteById(1L);
    }

    @Test
    void testCreateManualReceiptFromDTO() {
        CheckProductDTO productDTO = new CheckProductDTO();
        productDTO.setName("Milk");
        productDTO.setUnitPrice(1.20);
        productDTO.setQuantity(2);
        productDTO.setTotalPrice(2.40);

        ReceiptDTO dto = new ReceiptDTO();
        dto.setReceiptNumber("123");
        dto.setDate(LocalDateTime.now());
        dto.setTotal(2.40);
        dto.setProducts(List.of(productDTO));

        when(productService.createNewProduct(anyString(), anyDouble())).thenReturn(new ProductEntity());
        when(receiptRepository.save(any(ReceiptEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ReceiptEntity result = receiptService.createManualReceiptFromDTO(dto);

        assertNotNull(result);
        assertEquals("123", result.getReceiptNumber());
    }
    @Test
    void testUpdateReceiptFromDTO() {
        Long receiptId = 1L;
        ReceiptEntity existing = new ReceiptEntity();
        existing.setProducts(new ArrayList<>());

        CheckProductDTO productDTO = new CheckProductDTO();
        productDTO.setName("Bread");
        productDTO.setUnitPrice(0.90);
        productDTO.setQuantity(1);
        productDTO.setTotalPrice(0.90);

        ReceiptDTO dto = new ReceiptDTO();
        dto.setReceiptNumber("999");
        dto.setDate(LocalDateTime.now());
        dto.setTotal(0.90);
        dto.setProducts(List.of(productDTO));

        when(receiptRepository.findById(receiptId)).thenReturn(Optional.of(existing));
        when(productService.createNewProduct(anyString(), anyDouble())).thenReturn(new ProductEntity());
        when(receiptRepository.save(any(ReceiptEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ReceiptEntity updated = receiptService.updateReceiptFromDTO(receiptId, dto);

        assertEquals("999", updated.getReceiptNumber());
        assertEquals(1, updated.getProducts().size());
    }

    @Test
    void testParseAndSaveReceiptFromPDF() throws Exception {
        File file = new File("sample.pdf");
        Receipt parsedReceipt = new Receipt();
        parsedReceipt.setReceiptNumber("XYZ123");
        parsedReceipt.setDate(LocalDateTime.now());
        parsedReceipt.setTotal(10.0);
        parsedReceipt.setProducts(List.of());

        when(pdfParser.parseReceipt(file)).thenReturn(parsedReceipt);
        when(receiptRepository.findByReceiptNumber("XYZ123")).thenReturn(Optional.empty());
        when(receiptRepository.save(any(ReceiptEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        ReceiptEntity result = receiptService.parseAndSaveReceiptFromFile(file);

        assertEquals("XYZ123", result.getReceiptNumber());
    }
}
