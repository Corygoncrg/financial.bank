package com.example.financial.transactions.Service;

import com.example.financial.transactions.model.LocalDateTimeEditor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class XmlParserServiceTest {


    @InjectMocks
    XmlParserService xmlParserService;


    @Test
    @DisplayName("Test transaction dates are extracted correctly")
    void testExtractTransactionDatesFromFile() {
        // Arrange
        List<String> xmlLines = List.of(
                "<transacoes>",
                "<transacao>",
                "<origem>",
                "<banco>BANCO DO BRASIL</banco>",
                "<agencia>0001</agencia>",
                "<conta>00001-1</conta>",
                "</origem>",
                "<destino>",
                "<banco>BANCO DO BRASIL</banco>",
                "<agencia>0001</agencia>",
                "<conta>00001-2</conta>",
                "</destino>",
                "<valor>100.00</valor>",
                "<data>2022-01-02T07:30:00</data>",
                "</transacao>",
                "</transacoes>"
        );
        LocalDateTime expectedDate = LocalDateTime.of(2022, 1, 2, 7, 30);

        // Act
        List<LocalDateTime> result = xmlParserService.extractTransactionDatesFromFile(xmlLines);

        // Assert
        assertEquals(1, result.size());
        assertEquals(expectedDate, result.get(0));
    }

    @Test
    @DisplayName("Test empty file content returns empty date list")
    void testEmptyFileContent() {
        // Arrange
        List<String> emptyXmlLines = List.of();

        // Act
        List<LocalDateTime> result = xmlParserService.extractTransactionDatesFromFile(emptyXmlLines);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Test invalid date format is skipped")
    void testInvalidDateFormat() {
        // Arrange
        List<String> xmlLines = List.of(
                "<transacoes>",
                "<transacao>",
                "<origem>",
                    "<banco>BANCO DO BRASIL</banco>",
                    "<agencia>0001</agencia>",
                    "<conta>00001-1</conta>",
                "</origem>",
                "<destino>",
                    "<banco>BANCO DO BRASIL</banco>",
                    "<agencia>0001</agencia>",
                    "<conta>00001-2</conta>",
                "</destino>",
                "<valor>100.00</valor>",
                "<data>invalid-date</data>",
                "</transacao>",
                "</transacoes>"
        );

        // Act
        List<LocalDateTime> result = xmlParserService.extractTransactionDatesFromFile(xmlLines);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Test multiple dates are extracted correctly")
    void testMultipleDates() {
        // Arrange
        List<String> xmlLines = List.of(
                "<transacoes>",
                "<transacao>",
                "<origem>",
                "<banco>BANCO DO BRASIL</banco>",
                "<agencia>0001</agencia>",
                "<conta>00001-1</conta>",
                "</origem>",
                "<destino>",
                "<banco>BANCO DO BRASIL</banco>",
                "<agencia>0001</agencia>",
                "<conta>00001-2</conta>",
                "</destino>",
                "<valor>100.00</valor>",
                "<data>2022-01-02T07:30:00</data>",
                "</transacao>",
                "<transacao>",
                "<origem>",
                "<banco>BANCO DO BRASIL</banco>",
                "<agencia>0001</agencia>",
                "<conta>00001-1</conta>",
                "</origem>",
                "<destino>",
                "<banco>BANCO DO BRASIL</banco>",
                "<agencia>0001</agencia>",
                "<conta>00001-2</conta>",
                "</destino>",
                "<valor>100.00</valor>",
                "<data>2022-01-03T08:45:00</data>",
                "</transacao>",
                "</transacoes>"
        );
        LocalDateTime expectedDate1 = LocalDateTime.of(2022, 1, 2, 7, 30);
        LocalDateTime expectedDate2 = LocalDateTime.of(2022, 1, 3, 8, 45);

        // Act
        List<LocalDateTime> result = xmlParserService.extractTransactionDatesFromFile(xmlLines);

        // Assert
        assertEquals(2, result.size());
        assertEquals(expectedDate1, result.get(0));
        assertEquals(expectedDate2, result.get(1));
    }

    @Test
    @DisplayName("Test invalid XML structure is handled gracefully")
    void testInvalidXmlStructure() {
        // Arrange
        List<String> invalidXmlLines = List.of(
                "<transacoes>",
                "<transacao>",
                "<origem>",
                "<banco>BANCO DO BRASIL</banco>",
                "<agencia>0001</agencia>",
                "<conta>00001-1</conta>",
                "</origem>",
                "<destino>",
                "<banco>BANCO DO BRASIL</banco>",
                "<agencia>0001</agencia>",
                "<conta>00001-2</conta>",
                "</destino>",
                "<valor>100.00</valor>",
                "</transacao>"
                // Missing closing tags
        );

        // Act
        List<LocalDateTime> result = xmlParserService.extractTransactionDatesFromFile(invalidXmlLines);

        // Assert
        assertTrue(result.isEmpty());
    }
}