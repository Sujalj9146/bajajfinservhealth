package com.bajaj.webhooksolver;

import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class WebhookRunner implements CommandLineRunner {

    private static final String GENERATE_URL =
            "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

    private static final String FINAL_QUERY =
            "SELECT e.EMP_ID, e.FIRST_NAME, e.LAST_NAME, d.DEPARTMENT_NAME, " +
            "COUNT(e2.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT " +
            "FROM EMPLOYEE e " +
            "JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
            "LEFT JOIN EMPLOYEE e2 ON e2.DEPARTMENT = e.DEPARTMENT AND e2.DOB > e.DOB " +
            "GROUP BY e.EMP_ID, e.FIRST_NAME, e.LAST_NAME, d.DEPARTMENT_NAME " +
            "ORDER BY e.EMP_ID DESC;";

    @Override
    public void run(String... args) {
        try {
            RestTemplate rt = new RestTemplate();

            Map<String, String> body = new HashMap<>();
            body.put("name", "Sujal Jadhavar");
            body.put("regNo", "ADT23SOCB1167");
            body.put("email", "sujalj9146@gmail.com");

            HttpHeaders h1 = new HttpHeaders();
            h1.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> req1 = new HttpEntity<>(body, h1);

            ResponseEntity<Map> resp = rt.exchange(GENERATE_URL, HttpMethod.POST, req1, Map.class);
            System.out.println("========================================");
            System.out.println("Generate response: " + resp.getBody());

            Map respBody = resp.getBody();
            String webhook = (String) respBody.get("webhook");
            String token = (String) respBody.get("accessToken");

            System.out.println("Webhook URL: " + webhook);
            System.out.println("Token: " + token);
            System.out.println("========================================");

            HttpHeaders h2 = new HttpHeaders();
            h2.setContentType(MediaType.APPLICATION_JSON);
            h2.set("Authorization", token);

            Map<String, String> queryBody = new HashMap<>();
            queryBody.put("finalQuery", FINAL_QUERY);

            HttpEntity<Map<String, String>> req2 = new HttpEntity<>(queryBody, h2);
            ResponseEntity<String> submitResp = rt.exchange(webhook, HttpMethod.POST, req2, String.class);

            System.out.println("Submission status: " + submitResp.getStatusCode());
            System.out.println("Submission body: " + submitResp.getBody());
            System.out.println("========================================");
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}