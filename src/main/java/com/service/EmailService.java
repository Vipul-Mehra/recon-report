package com.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;
import java.util.Set;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    public void sendEmailWithReport(String startDate, String endDate, Set<String> recipients,  Map<String,Object> data) {
        // Fetch report JSON

        System.out.println("Fetched report data: " + data);



        // build Thymeleaf context
        Context ctx = new Context();
        ctx.setVariable("startDate", startDate);
        ctx.setVariable("endDate",   endDate);
        ctx.setVariable("totalIds",  data.get("numberOfIds"));
        ctx.setVariable("processed", data.get("numberOfProcessed"));
        ctx.setVariable("filtered",  data.get("numberOfFiltered"));
        ctx.setVariable("failed",    data.get("numberOfFailed"));

        // render HTML
        String html = templateEngine.process("emailTemplate", ctx);

        // send email
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            helper.setTo(recipients.toArray(new String[0]));
            helper.setSubject("Eleox Status Report");
            helper.setText(html, true);
            mailSender.send(msg);
        } catch (MessagingException e) {
            throw new RuntimeException("Email send failed", e);
        }
    }
}