package tn.biramgroup.pointage.services.implement;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

//    @Autowired
//    private SendGrid sendGrid;
//
//    @Value("${sendgrid.from-email}")
//    private String fromEmail;
//
//    public void sendEmail(String to, String subject, String body) throws IOException {
//        Email from = new Email(fromEmail);
//        Email toEmail = new Email(to);
//        Content content = new Content("text/html", body);
//        Mail mail = new Mail(from, subject, toEmail, content);
//
//        Request request = new Request();
//        request.setMethod(Method.POST);
//        request.setEndpoint("mail/send");
//        request.setBody(mail.build());
//        Response response = sendGrid.api(request);
//        if (response.getStatusCode() >= 400) {
//            throw new IOException("SendGrid error: " + response.getBody());
//        }
//    }
}
