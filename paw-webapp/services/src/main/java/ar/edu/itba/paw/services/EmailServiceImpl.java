package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.models.Enterprise;
import ar.edu.itba.paw.models.JobOffer;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private SpringTemplateEngine templateEngine;
    @Autowired
    private MessageSource messageSource;
    private static final int MULTIPART_MODE = MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED;
    private static final String ENCODING = StandardCharsets.UTF_8.name();
    private final String baseUrl = "http://pawserver.it.itba.edu.ar/paw-2022b-4/";
    private static final String REGISTER_SUCCESS_TEMPLATE = "registerSuccess.html";
    private static final String CONTACT_TEMPLATE = "contactEmail.html";
    private static final String ANSWER_TEMPLATE = "answerEmail.html";
    private static final String CLOSE = "close";
    private static final String CANCEL = "cancel";
    private static final String ACCEPT = "acceptMsg";

    @Async
    @Override
    public void sendEmail(String to, String subject, String template, Map<String, Object> variables) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, MULTIPART_MODE, ENCODING);

            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setFrom("noreply.cloned.in@gmail.com");
            mimeMessageHelper.setSubject(subject);
            variables.put("baseUrl", baseUrl);
            mimeMessageHelper.setText(getHtmlBody(template, variables), true);

            mailSender.send(mimeMessage);
        } catch(MessagingException messagingException) {
        }
    }

    private String getHtmlBody(String template, Map<String, Object> variables) {
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(variables);
        return templateEngine.process(template, thymeleafContext);
    }

    @Async
    @Override
    public void sendContactEmail(User user, Enterprise enterprise, JobOffer jobOffer, String message, Locale locale) {
        final Map<String, Object> mailMap = new HashMap<>();
        mailMap.put("username", user.getName());
        mailMap.put("profileUrl", baseUrl + "notificationsUser/" + user.getId());
        mailMap.put("jobDesc", jobOffer.getDescription());
        mailMap.put("jobPos", jobOffer.getPosition());
        mailMap.put("salary", jobOffer.getSalary() != null? "$" + String.valueOf(jobOffer.getSalary()) :
                messageSource.getMessage("contactMail.noSalaryMsg", null, locale));
        mailMap.put("modality", jobOffer.getModality());
        mailMap.put("enterpriseName", enterprise.getName());
        mailMap.put("enterpriseEmail", enterprise.getEmail());
        mailMap.put("message", message);
        mailMap.put("congratulationsMsg", messageSource.getMessage("contactMail.congrats", null, locale));
        mailMap.put("enterpriseMsg", messageSource.getMessage("contactMail.enterprise", null, locale));
        mailMap.put("positionMsg", messageSource.getMessage("contactMail.position", null, locale));
        mailMap.put("descriptionMsg", messageSource.getMessage("contactMail.description", null, locale));
        mailMap.put("salaryMsg", messageSource.getMessage("contactMail.salary", null, locale));
        mailMap.put("modalityMsg", messageSource.getMessage("contactMail.modality", null, locale));
        mailMap.put("additionalCommentsMsg", messageSource.getMessage("contactMail.additionalComments", null, locale));
        mailMap.put("buttonMsg", messageSource.getMessage("contactMail.button", null, locale));
        String subject = messageSource.getMessage("contactMail.subject", null, locale) + enterprise.getName();
        sendEmail(user.getEmail(), subject, CONTACT_TEMPLATE, mailMap);
    }

    @Async
    @Override
    public void sendRegisterUserConfirmationEmail(User user, Locale locale) {
        sendRegisterConfirmationEmail(user.getEmail(), user.getName(), locale, "profileUser/" + user.getId());
    }

    @Async
    @Override
    public void sendRegisterEnterpriseConfirmationEmail(String email, String enterpriseName, Locale locale) {
        sendRegisterConfirmationEmail(email, enterpriseName, locale, "");
    }

    @Async
    void sendRegisterConfirmationEmail(String email, String username, Locale locale, String callToActionMsg) {
        final Map<String, Object> mailMap = new HashMap<>();
        mailMap.put("username", username);
        mailMap.put("welcomeMsg", messageSource.getMessage("registerMail.welcomeMsg", null, locale));
        mailMap.put("bodyMsg", messageSource.getMessage("registerMail.bodyMsg", null, locale));
        mailMap.put("buttonMsg", messageSource.getMessage("registerMail.button", null, locale));
        mailMap.put("callToActionUrl", baseUrl + callToActionMsg);
        String subject = messageSource.getMessage("registerMail.subject", null, locale);
        sendEmail(email, subject, REGISTER_SUCCESS_TEMPLATE, mailMap);
    }

    @Async
    @Override
    public void sendReplyJobOfferEmail(Enterprise enterprise, String username, String email, String jobOfferPosition, String answerMsg, Locale locale) {
        final Map<String, Object> mailMap = new HashMap<>();

        mailMap.put("username", username);
        mailMap.put("answerMsg", messageSource.getMessage(answerMsg, null, locale));
        mailMap.put("contactMsg", answerMsg.compareTo(ACCEPT)==0?
                messageSource.getMessage("contactMsg", null, locale) + email :
                messageSource.getMessage("nonContactMsg", null, locale));
        mailMap.put("jobOffer", jobOfferPosition);
        mailMap.put("contactsUrl", baseUrl + "/contactsEnterprise/" + enterprise.getId());
        mailMap.put("buttonMsg", messageSource.getMessage("answerMail.button", null, locale));

        String subject = messageSource.getMessage("answerMail.subject", null, locale);

        sendEmail(enterprise.getEmail(), subject, ANSWER_TEMPLATE, mailMap);
    }

    @Async
    @Override
    public void sendCloseJobOfferEmail(User user, String enterpriseName, String jobOfferPosition, Locale locale) {
        sendFinishJobOfferCycleEmail(user, enterpriseName, jobOfferPosition, CLOSE, locale);
    }

    @Async
    @Override
    public void sendCancelJobOfferEmail(User user, String enterpriseName, String jobOfferPosition, Locale locale) {
        sendFinishJobOfferCycleEmail(user, enterpriseName, jobOfferPosition, CANCEL, locale);
    }

    @Async
    void sendFinishJobOfferCycleEmail(User user, String enterpriseName, String jobOfferPosition, String action, Locale locale) {
        final Map<String, Object> mailMap = new HashMap<>();

        mailMap.put("username", enterpriseName);
        mailMap.put("answerMsg", messageSource.getMessage(action + "Msg", null, locale));
        mailMap.put("jobOffer", jobOfferPosition);
        mailMap.put("contactsUrl", baseUrl + "/notificationsUser/" + user.getId());
        mailMap.put("buttonMsg", messageSource.getMessage("closeMail.button", null, locale));

        String subject = messageSource.getMessage(action + "Mail.subject", null, locale);

        sendEmail(user.getEmail(), subject, ANSWER_TEMPLATE, mailMap);
    }

}
