package com.easyses.email.v1.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.easyses.email.v1.util.Constants;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

@Service
public class SendMailService {

    private static final String CHAR_SET = "UTF-8";
    private final AmazonSimpleEmailService emailService;
    private final String sender;

    @Autowired
    public SendMailService(AmazonSimpleEmailService emailService,
                           @Value("${aws.email.sender}") String sender) {
        this.emailService = emailService;
        this.sender = sender;
    }

    //Asynchronous method to send mails via AWS SES in batches of
    @Async("asyncExecutor")
    public void sendMailsAsync(List<String> emailList,
                               String subject,
                               String mainMessage,
                               String ctaLink,
                               String ctaButtonLabel,
                               String footerMessage) {

        Properties props = new Properties();

        props.put(Constants.PROP_MAIN_MESSAGE, mainMessage);
        props.put(Constants.PROP_CTA_LINK, ctaLink);
        props.put(Constants.PROP_CTA_BUTTON_LABEL, ctaButtonLabel);
        props.put(Constants.PROP_FOOTER_MESSAGE, footerMessage);

        String htmlFinal = populateMailPlaceholders(props);

        //Break up the email list into groups of 50 as that's the AWS SES limit for a single API call
        List<List<String>> partitionedLists = ListUtils.partition(emailList, 50);

        for (List<String> emailListPartitioned : partitionedLists) {
            if (Objects.nonNull(htmlFinal)) {
                try {
                    // The time for request/response round trip to aws in milliseconds
                    int requestTimeout = 3000;
                    SendEmailRequest request = new SendEmailRequest()
                            .withDestination(
                                    new Destination().withBccAddresses(emailListPartitioned))
                            .withMessage(new Message()
                                    .withBody(new Body()
                                            .withHtml(new Content()
                                                    .withCharset(CHAR_SET).withData(htmlFinal)))
                                    .withSubject(new Content()
                                            .withCharset(CHAR_SET).withData(subject)))
                            .withSource(sender).withSdkRequestTimeout(requestTimeout);
                    SendEmailResult sendEmailResult = emailService.sendEmail(request);
                    System.out.println(sendEmailResult.getSdkResponseMetadata().toString());
                } catch (RuntimeException e) {
                    System.err.println("Error: " + e.getMessage());
                }
            } else {
                System.err.println("Error: The html received from the populateMailPlaceholders method was null");
            }
        }
    }

    //Helper method to populate email details
    private String populateMailPlaceholders(Properties props) {
        String emailTemplateFilename = "default-mail-template.html";
        String fileName = "classpath:" + Constants.HTML_TEMPLATES_FOLDER + emailTemplateFilename;

        try {
            Resource emailTemplate = new ClassPathResource(fileName);
            if (emailTemplate.exists()) {
                InputStream inputStream = emailTemplate.getInputStream();
                byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);
                String htmlContent = new String(bdata);

                String htmlFinal = htmlContent
                        .replace(Constants.PLACEHOLDER_MAIN_MESSAGE, props.getProperty(Constants.PROP_MAIN_MESSAGE))
                        .replace(Constants.PLACEHOLDER_CTA_LINK, props.getProperty(Constants.PROP_CTA_LINK))
                        .replace(Constants.PLACEHOLDER_CTA_BUTTON_LABEL, props.getProperty(Constants.PROP_CTA_BUTTON_LABEL))
                        .replace(Constants.PLACEHOLDER_FOOTER_MESSAGE, props.getProperty(Constants.PROP_FOOTER_MESSAGE));

                return htmlFinal;
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }
}