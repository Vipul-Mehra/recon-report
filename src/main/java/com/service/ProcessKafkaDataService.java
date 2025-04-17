package com.service;

import com.repository.EleoxOxNomTradeExtractRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ProcessKafkaDataService {
 private static final Logger log = LoggerFactory.getLogger(ProcessKafkaDataService.class);

 @Autowired
 private EleoxOxNomTradeExtractRepository eleoxOxNomTradeExtractRepository;

 public Map<String, Map<String, Object>> compareData(String startDateStr, String endDateStr, List<Map<String, Object>> jsonDataList) {
  log.info("Comparing JSON Data for date range: {} - {}", startDateStr, endDateStr);
  log.info("Received JSON Data for comparison: {}", jsonDataList);

  // Parse input date strings into LocalDate objects
  LocalDate startDate = LocalDate.parse(startDateStr, DateTimeFormatter.ISO_DATE);
  LocalDate endDate = LocalDate.parse(endDateStr, DateTimeFormatter.ISO_DATE);

  // Fetch ETRM deal IDs from the database for the given date range
  List<Long> dbEtrmDealIds = eleoxOxNomTradeExtractRepository.findAllDealIds(startDate, endDate);
  Set<Long> dbEtrmDealIdSet = new HashSet<>(dbEtrmDealIds);
  log.info("Fetched ETRM deal IDs from DB for range {} - {}: {}", startDate, endDate, dbEtrmDealIds);

  // Map to store results grouped by processType
  Map<String, Map<String, Object>> resultByProcessType = new HashMap<>();

  // Process JSON data if it is not null
  if (jsonDataList != null) {
   // Create a Set of endurIds from JSON data that fall within the date range
   Set<Long> jsonEndurIdsInRange = new HashSet<>();
   for (Map<String, Object> jsonRecord : jsonDataList) {
    try {
     String createDateStr = (String) jsonRecord.get("createDate");
     Long endurId = Long.parseLong(String.valueOf(jsonRecord.get("endurId")));

     // Parse createDate into a LocalDate object
     LocalDate createDate = LocalDate.parse(createDateStr.substring(0, 10), DateTimeFormatter.ISO_DATE);

     // Check if the createDate falls within the specified range
     if (!createDate.isBefore(startDate) && !createDate.isAfter(endDate)) {
      jsonEndurIdsInRange.add(endurId);
     }
    } catch (Exception e) {
     log.warn("Error processing JSON record: {}", jsonRecord, e);
    }
   }

   // Initialize lists for processed, filtered, failed, and not matched IDs
   List<Long> allMatchedIds = new ArrayList<>();
   List<Long> processedIds = new ArrayList<>();
   List<Long> filteredIds = new ArrayList<>();
   List<Long> failedIds = new ArrayList<>();
   List<Long> notMatchedIds = new ArrayList<>();

   // Iterate through database IDs and categorize them based on JSON data
   for (Long dbId : dbEtrmDealIdSet) {
    if (jsonEndurIdsInRange.contains(dbId)) {
     // ID exists in JSON data within the date range
     allMatchedIds.add(dbId);

     // Find the corresponding JSON record
     for (Map<String, Object> jsonRecord : jsonDataList) {
      try {
       Long endurId = Long.parseLong(String.valueOf(jsonRecord.get("endurId")));
       String outcomeDescription = ((String) jsonRecord.get("outcomeDescription")).toLowerCase();

       if (endurId.equals(dbId)) {
        switch (outcomeDescription) {
         case "processed":
          processedIds.add(dbId);
          break;
         case "failed":
          failedIds.add(dbId);
          break;
         case "filtered":
          filteredIds.add(dbId);
          break;
        }
        break; // Exit the loop once the ID is processed
       }
      } catch (Exception e) {
       log.warn("Error processing JSON record: {}", jsonRecord, e);
      }
     }
    } else {
     // ID does not exist in JSON data within the date range
     notMatchedIds.add(dbId);
    }
   }

   // Populate the result map for the current processType
   Map<String, Object> processTypeResult = new HashMap<>();
   processTypeResult.put("allIds", allMatchedIds);
   processTypeResult.put("numberOfIds", allMatchedIds.size());
   processTypeResult.put("processedIds", processedIds);
   processTypeResult.put("numberOfProcessed", processedIds.size());
   processTypeResult.put("filteredIds", filteredIds);
   processTypeResult.put("numberOfFiltered", filteredIds.size());
   processTypeResult.put("failedIds", failedIds);
   processTypeResult.put("numberOfFailed", failedIds.size());
   processTypeResult.put("notMatchedIds", notMatchedIds);
   processTypeResult.put("numberOfNotMatched", notMatchedIds.size());

   // Add the result to the main map under the processType key
   resultByProcessType.put("ETRMTradeAlert", processTypeResult);
  }

  // Log the comparison result in a readable JSON format
  log.info("Comparison Result for range {} - {}:\n{}", startDate, endDate, formatAsJson(resultByProcessType));
  return resultByProcessType;
 }

 /**
  * Formats a map as a pretty-printed JSON string.
  */
 private String formatAsJson(Map<String, Map<String, Object>> resultMap) {
  StringBuilder jsonBuilder = new StringBuilder("{\n");
  for (Map.Entry<String, Map<String, Object>> entry : resultMap.entrySet()) {
   String processType = entry.getKey();
   Map<String, Object> processTypeResult = entry.getValue();

   jsonBuilder.append("    \"").append(processType).append("\": {\n");
   jsonBuilder.append("        \"allIds\": ").append(processTypeResult.get("allIds")).append(",\n");
   jsonBuilder.append("        \"numberOfIds\": ").append(processTypeResult.get("numberOfIds")).append(",\n");
   jsonBuilder.append("        \"processed\": ").append(processTypeResult.get("numberOfProcessed")).append(", [").append(processTypeResult.get("processedIds")).append("],\n");
   jsonBuilder.append("        \"filtered\": ").append(processTypeResult.get("numberOfFiltered")).append(", [").append(processTypeResult.get("filteredIds")).append("],\n");
   jsonBuilder.append("        \"failed\": ").append(processTypeResult.get("numberOfFailed")).append(", [").append(processTypeResult.get("failedIds")).append("],\n");
   jsonBuilder.append("        \"notMatched\": ").append(processTypeResult.get("numberOfNotMatched")).append(", [").append(processTypeResult.get("notMatchedIds")).append("]\n");
   jsonBuilder.append("    },\n");
  }

  // Remove the trailing comma and close the JSON object
  if (jsonBuilder.length() > 2) {
   jsonBuilder.setLength(jsonBuilder.length() - 2); // Remove ",\n"
  }
  jsonBuilder.append("\n}");
  return jsonBuilder.toString();
 }
}