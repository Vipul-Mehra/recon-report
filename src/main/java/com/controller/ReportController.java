package com.controller;

import com.service.ProcessKafkaDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.HashMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ProcessKafkaDataService processKafkaDataService;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Value("${external.api.url}")
    private String externalApiUrl;

    @GetMapping("/compare-external")
    public Mono<ResponseEntity<Map<String, Object>>> compareExternalData(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {

        WebClient webClient = webClientBuilder.build();

        // Define the correct ParameterizedTypeReference
        ParameterizedTypeReference<List<Map<String, Object>>> ptr = new ParameterizedTypeReference<>() {};

        return webClient.get()
                .uri(externalApiUrl + "/api/data")
                .retrieve()
                .bodyToMono(ptr)
                .flatMap(jsonDataList -> {
                    System.out.println("JSON Data Received from Mock API: " + jsonDataList); // Print JSON data
                    Map<String, Object> result = processSimplifiedData(startDate, endDate, jsonDataList);
                    return Mono.just(ResponseEntity.ok(result));
                })
                .onErrorResume(e -> {
                    Map<String, Object> error = Map.of(
                            "error", "Error fetching data from external API: " + e.getMessage()
                    );
                    return Mono.just(ResponseEntity.status(500).body(error));
                });
    }

    @GetMapping("/compare-data")
    public ResponseEntity<Map<String, Object>> compareData(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {

        WebClient webClient = webClientBuilder.build();

        // Fetch JSON data from the mock API
        ParameterizedTypeReference<List<Map<String, Object>>> ptr = new ParameterizedTypeReference<>() {};
        List<Map<String, Object>> jsonDataList = webClient.get()
                .uri(externalApiUrl + "/api/data")
                .retrieve()
                .bodyToMono(ptr)
                .block(); // Block to get the result synchronously

        System.out.println("JSON Data Received from Mock API: " + jsonDataList); // Print JSON data

        // Delegate to the service to process the data
        Map<String, Object> result = processSimplifiedData(startDate, endDate, jsonDataList);

        return ResponseEntity.ok(result);
    }

    private Map<String, Object> processSimplifiedData(String startDate, String endDate, List<Map<String, Object>> jsonDataList) {
        // Call the service to process the data
        Map<String, Object> detailedResult = processKafkaDataService.compareData(startDate, endDate, jsonDataList);

        // Simplify the result
        Map<String, Object> simplifiedResult = new HashMap<>();
        simplifiedResult.put("allIds", detailedResult.getOrDefault("allIds", ""));
        simplifiedResult.put("numberOfIds", detailedResult.getOrDefault("numberOfIds", 0));
        simplifiedResult.put("processedIds", detailedResult.getOrDefault("processedIds", 0));
        simplifiedResult.put("filteredIds", detailedResult.getOrDefault("filteredIds", 0));
        simplifiedResult.put("failedIds", detailedResult.getOrDefault("failedIds", 0));

        return simplifiedResult;
    }
}