package com.easyses.email.v1.request;

import lombok.Data;

import java.util.List;

@Data
public class SendMailRQ {

    //Recipients - Shall be sent in BCC
    private List<String> emailList;

    //Email subject
    private String subject;

    //Main body of email - can include HTML for formatting
    private String mainMessage;

    //Call to Action button label
    private String ctaButtonLabel;

    //Call to Action link
    private String ctaButtonLink;

    //Footer message
    private String footerMessage;
}
