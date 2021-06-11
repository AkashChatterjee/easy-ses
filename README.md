# README - Simple SES #

### What is this repository for? ###

A simple controller-service combination that enables a user to quickly start sending dynamic HTML emails via AWS SES.

### How do I get set up? ###

* Clone the repository to a remote EC2 instance.
* Ensure that you configure the AWS Key ID and Secret for the email flows. This must be configured in '~/.aws/credentials'. Refer: https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-files.html.
* Modify the application.properties file with sender details.
* Modify the default-html-template.html: Change the text 'Best Org' to your own organization's name.
* Run 'sudo ./buildAndInstall.sh &lt;name of deployment profile&gt;. E.g. 'sudo ./buildAndInstall.sh dev'. This builds the project and configures the microservice as a systemd service. Please create proper profiles for production when required.
* Run 'sudo systemctl start msvc-easy-ses' to start the microservice on port 8080.

### Sample cURL ###

curl --location --request POST 'http://<HOST>:8080/v1/send-mails' \
--header 'Content-Type: application/json' \
--data-raw '{
    "emailList":["<EMAIL1>", "<EMAIL2>"],
    "subject":"Test",
    "mainMessage":"Test",
    "ctaButtonLabel":"Test",
    "ctaButtonLink":"https://test.com",
    "footerMessage":"Test"
}'

### Sample Email ###

<img width="589" alt="Screenshot 2021-06-12 at 1 34 31 AM" src="https://user-images.githubusercontent.com/8582657/121743078-617d6f80-cb1e-11eb-8906-b0d27ccee7ba.png">

### Who do I talk to? ###

* akashc2310@gmail.com
