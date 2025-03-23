package com.fujitsu.fujitsu_food_delivery.scheduler;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fujitsu.fujitsu_food_delivery.entities.WeatherData;
import com.fujitsu.fujitsu_food_delivery.repositories.WeatherDataRepository;


/**
 * Component responsible for importing weather data periodically from the Estonian Environment Agency.
 * This scheduler uses a RestTemplate to fetch XML data from the weather portal, parses it, and
 * stores the resulting WeatherData objects in the database via WeatherDataRepository.
 * The scheduled task is configured to run every 30 seconds (for testing) and can be adjusted as needed.
 */
@Component
public class WeatherDataScheduler {

    private final WeatherDataRepository weatherDataRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Constructs a new WeatherDataScheduler with the specified WeatherDataRepository.
     *
     * @param weatherDataRepository the repository used to save parsed weather data
     */
    public WeatherDataScheduler(WeatherDataRepository weatherDataRepository) {
        this.weatherDataRepository = weatherDataRepository;
    }

    /**
     * Scheduled task that imports weather data by fetching XML data from the specified URL,
     * parsing it, and saving records for the specified weather stations.
     * For testing purposes, this method is currently scheduled to run every 30 seconds.
     * In production, update the cron expression to "0 15 * * * *" to run at 15 minutes past each hour.
     */
    @Scheduled(cron = "*/30 * * * * *") // only for testing
    //@Scheduled(cron = "0 15 * * * *")
    public void importWeatherData() {
        String url = "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php";
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String xmlData = response.getBody();

            parseAndSaveWeatherData(xmlData);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * Parses the provided XML data and saves the weather data for selected stations in the database.
     * This method reads the XML's root element to retrieve a timestamp (as a UNIX time integer)
     * and iterates over each "station" element. Only stations with names "Tallinn-Harku", "Tartu-Tõravere",
     * or "Pärnu" are processed.
     *
     * @param xmlData the XML data as a String
     * @throws Exception if any error occurs during parsing or data processing
     */
    private void parseAndSaveWeatherData(String xmlData) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new ByteArrayInputStream(xmlData.getBytes(StandardCharsets.UTF_8)));

        document.getDocumentElement().normalize();
        String timestamp = document.getDocumentElement().getAttribute("timestamp");
        int timestampInteger = Integer.parseInt(timestamp);    
        NodeList stationNodes = document.getElementsByTagName("station");

        for (int i = 0; i < stationNodes.getLength(); i++) {
            Node node = stationNodes.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element station = (Element) node;

                String stationName = station.getElementsByTagName("name").item(0).getTextContent();

                // only save data for specified stations
                if (stationName.equals("Tallinn-Harku") ||
                    stationName.equals("Tartu-Tõravere") ||
                    stationName.equals("Pärnu")) {

                    WeatherData weatherData = new WeatherData();
                    weatherData.setStationName(stationName);
                    weatherData.setWmoCode(station.getElementsByTagName("wmocode").item(0).getTextContent());
                    weatherData.setAirTemperature(parseDouble(station, "airtemperature"));
                    weatherData.setWindSpeed(parseDouble(station, "windspeed"));
                    weatherData.setWeatherPhenomenon(station.getElementsByTagName("phenomenon").item(0).getTextContent());
                    weatherData.setTimestamp(timestampInteger);

                    weatherDataRepository.save(weatherData);
                }
            }
        }
    }

    /**
     * Helper method that parses a double value from the text content of a specified XML element tag.
     * If the text content is empty, this method returns null.
     *
     * @param element the XML element that contains the desired tag
     * @param tag     the name of the tag whose text content should be parsed as a Double
     * @return the parsed Double value, or null if the tag is empty
     */
    private Double parseDouble(Element element, String tag) {
        String value = element.getElementsByTagName(tag).item(0).getTextContent();
        return value.isEmpty() ? null : Double.parseDouble(value);
    }
}