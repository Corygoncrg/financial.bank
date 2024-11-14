package com.example.financial.transactions.Service;

import com.example.financial.transactions.model.LocalDateTimeEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class XmlParserService {

    private final LocalDateTimeEditor localDateTimeEditor;

    @Autowired
    public XmlParserService() {
        this.localDateTimeEditor = new LocalDateTimeEditor("yyyy-MM-dd'T'HH:mm:ss");
    }

    public List<LocalDateTime> extractTransactionDatesFromFile(List<String> fileContent) {
        List<LocalDateTime> transactionDates = new ArrayList<>();
        StringBuilder contentBuilder = new StringBuilder();

        // Combine the lines to form a single XML content string
        for (String line : fileContent) {
            contentBuilder.append(line.trim());
        }

        try {
            // Parse the XML content
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(contentBuilder.toString().getBytes(StandardCharsets.UTF_8)));
            document.getDocumentElement().normalize();

            // Extract elements containing the transaction date (e.g., <transactionDate>)
            NodeList dateNodes = document.getElementsByTagName("data");
            for (int i = 0; i < dateNodes.getLength(); i++) {
                Node node = dateNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String dateText = element.getTextContent();

                    try {
                        // Parse the date string to LocalDateTime
                        localDateTimeEditor.setAsText(dateText);
                        LocalDateTime transactionDate = (LocalDateTime) localDateTimeEditor.getValue();
                        transactionDates.add(transactionDate);
                    } catch (Exception e) {
                        System.err.println("Error parsing date: " + dateText + ". Skipping...");
                        e.printStackTrace(); // Optional: for debugging
                    }
                }
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.err.println("Error parsing XML content. Skipping file...");
            e.printStackTrace(); // Optional: for detailed debugging
        }

        return transactionDates;
    }
}
