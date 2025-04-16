package com.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.repository.EleoxOxNomTradeExtractRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ProcessKafkaDataService {

 private static final Logger log = LoggerFactory.getLogger(ProcessKafkaDataService.class);

 @Autowired
 private EleoxOxNomTradeExtractRepository eleoxOxNomTradeExtractRepository;

 public Map<String, Object> compareData(String startDateStr, String endDateStr, List<Map<String, Object>> jsonDataList) {
  log.info("Comparing JSON Data for date range: {} - {}", startDateStr, endDateStr);
  log.info("Received JSON Data for comparison: {}", jsonDataList);

  // Parse input date strings into LocalDate objects
  LocalDate startDate = LocalDate.parse(startDateStr, DateTimeFormatter.ISO_DATE);
  LocalDate endDate = LocalDate.parse(endDateStr, DateTimeFormatter.ISO_DATE);

  // Fetch ETRM deal IDs from the database for the given date range
  List<Long> dbEtrmDealIds = eleoxOxNomTradeExtractRepository.findAllDealIds(startDate, endDate);
  Set<Long> dbEtrmDealIdSet = new HashSet<>(dbEtrmDealIds);
  log.info("Fetched ETRM deal IDs from DB for range {} - {}: {}", startDate, endDate, dbEtrmDealIds);

  // Initialize lists to store matched IDs based on outcome
  List<Long> allMatchedIds = new ArrayList<>();
  List<Long> processedIds = new ArrayList<>();
  List<Long> failedIds = new ArrayList<>();
  List<Long> filteredIds = new ArrayList<>();

  // Process JSON data if it is not null
  if (jsonDataList != null) {
   for (Map<String, Object> jsonRecord : jsonDataList) {
    try {
     // Extract relevant fields from the JSON record
     String createDateStr = (String) jsonRecord.get("createDate");
     Long endurId = Long.parseLong(String.valueOf(jsonRecord.get("endurId")));
     String outcomeDescription = ((String) jsonRecord.get("outcomeDescription")).toLowerCase();

     // Parse createDate into a LocalDate object
     LocalDate createDate = LocalDate.parse(createDateStr.substring(0, 10), DateTimeFormatter.ISO_DATE);

     // Check if the ID exists in the database and falls within the date range
     if (dbEtrmDealIdSet.contains(endurId) && !createDate.isBefore(startDate) && !createDate.isAfter(endDate)) {
      allMatchedIds.add(endurId);

      // Categorize the ID based on its outcome description
      switch (outcomeDescription) {
       case "processed":
        processedIds.add(endurId);
        break;
       case "failed":
        failedIds.add(endurId);
        break;
       case "filtered":
        filteredIds.add(endurId);
        break;
      }
     }
    } catch (Exception e) {
     log.warn("Error processing JSON record: {}", jsonRecord, e);
    }
   }
  }

  // Remove IDs from filtered if they are also in processed
  filteredIds.removeIf(processedIds::contains);

  // Prepare the result map
  Map<String, Object> result = new HashMap<>();
  result.put("allIds", allMatchedIds.stream().map(String::valueOf).reduce((a, b) -> a + "," + b).orElse(""));
  result.put("numberOfIds", allMatchedIds.size());
  result.put("processedIds", processedIds.size());
  result.put("filteredIds", filteredIds.size());
  result.put("failedIds", failedIds.size());

  // Log the comparison result
  log.info("Comparison Result for range {} - {}: {}", startDate, endDate, result);

  return result;
 }
}