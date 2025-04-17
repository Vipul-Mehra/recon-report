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
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
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
        Map<String, Map<String, Object>> resultByProcessType = processKafkaDataService.compareData(startDate, endDate, jsonDataList);

        // Simplify and format the result
        Map<String, Object> formattedResult = new HashMap<>();
        for (Map.Entry<String, Map<String, Object>> entry : resultByProcessType.entrySet()) {
            String processType = entry.getKey();
            Map<String, Object> processTypeResult = entry.getValue();

            Map<String, Object> simplifiedResult = new HashMap<>();
            simplifiedResult.put("allIds", processTypeResult.get("allIds"));
            simplifiedResult.put("numberOfIds", processTypeResult.get("numberOfIds"));
            simplifiedResult.put("processed", processTypeResult.get("numberOfProcessed"));
            simplifiedResult.put("filtered", processTypeResult.get("numberOfFiltered"));
            simplifiedResult.put("failed", processTypeResult.get("numberOfFailed"));

            formattedResult.put(processType, simplifiedResult);
        }

        return ResponseEntity.ok(formattedResult);
    }
}