# README - Simple SES #

### What is this repository for? ###

A microservice that provides a simple REST API endpoint to send dynamic HTML emails via AWS SES (asynchronously).

### Prerequisites ###

You shall need one of these two:
* An AWS account where you've already gotten approval from AWS to use the SES service OUTSIDE the sandbox
* An AWS account where the SES service is INSIDE the sandbox (default) and the emails you want to send to are verified


### How do I get set up? ###

* Clone the repository to a remote EC2 instance.
* Ensure that you configure the AWS Key ID and Secret for the email flows. This must be configured in '~/.aws/credentials'. Refer: https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-files.html.
* Modify the application.properties file with sender details.
* Modify the default-html-template.html: Change the text 'Team BestOrg' to your own organization's name.
* Run 'sudo ./buildAndInstall.sh &lt;name of deployment profile&gt;. E.g. 'sudo ./buildAndInstall.sh dev'. This builds the project and configures the microservice as a systemd service. Please create proper profiles for production when required.
* Run 'sudo systemctl start msvc-easy-ses' to start the microservice on port 8080.

### How does the API flow work? ###

* The API request body consists of the following details:
    - emailList: Array of email IDs of the recipients
    - subject: Subject of the email
    - mainMessage: Main body of the email (can include HTML formatting)
    - ctaButtonLabel: The label for the Call to Action button in the mail body
    - ctaButtonLink: The link for the Call to Action button
    - footerMessage: Footer text
   
* Once the request is received, the Controller extracts the information from the request body and calls the asynchronous method (sendMailsAsync) in the Service layer

* In the Service layer:
    - The recipient list is broken down into chunks of 50 emails (due to the AWS SES limit per AWS SES API call)
    - The placeholders in the mail HTML body is replaced by the details provided in the API request
    - A call is made to the AWS SES API to send the emails

### Sample cURL ###

curl --location --request POST 'http://HOST:8080/v1/send-mails' \
--header 'Content-Type: application/json' \
--data-raw '{
    "emailList":["EMAIL1", "EMAIL2"],
    "subject":"Test",
    "mainMessage":"Test",
    "ctaButtonLabel":"Test",
    "ctaButtonLink":"https://test.com",
    "footerMessage":"Test"
}'

### Sample Email ###

<img width="579" alt="Screenshot 2021-06-12 at 1 42 04 AM" src="https://user-images.githubusercontent.com/8582657/121743737-6c84cf80-cb1f-11eb-914c-ab79584ab64e.png">

### Who do I talk to? ###

* akashc2310@gmail.com
