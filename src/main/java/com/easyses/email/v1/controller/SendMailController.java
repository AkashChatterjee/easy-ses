package com.easyses.email.v1.controller;

import com.easyses.email.v1.request.SendMailRQ;
import com.easyses.email.v1.service.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/send-mails")
public class SendMailController {

    @Autowired
    private SendMailService sendMailService;

    @PostMapping
    public ResponseEntity<String> sendMail(@RequestBody SendMailRQ sendMailRQ) {

        sendMailService.sendMailsAsync(sendMailRQ.getEmailList(),
                sendMailRQ.getSubject(),
                sendMailRQ.getMainMessage(),
                sendMailRQ.getCtaButtonLink(),
                sendMailRQ.getCtaButtonLabel(),
                sendMailRQ.getFooterMessage());

        return ResponseEntity.ok("Emails shall be sent soon!");
    }

}
