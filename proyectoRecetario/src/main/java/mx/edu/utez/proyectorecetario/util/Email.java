package mx.edu.utez.proyectorecetario.util;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class Email {

    private final String emailFrom = "recetarionamdams@gmail.com";
    private final String passwordFrom = "irtzhpqxjysmrldn"; // contraseña de aplicación

    private String emailTo;
    private String subject;
    private String content;

    private Properties mProperties;
    private Session mSession;
    private MimeMessage mCorreo;

    public Email(String emailTo, String nombreUsuario, String pin) {
        this.emailTo = emailTo;
        this.subject = "Mensaje de Confirmación";
        this.content = generarMensajeConfirmacion(pin, nombreUsuario);

        mProperties = new Properties();
        configurarPropiedades();
        crearCorreo();
    }

    private void configurarPropiedades() {
        mProperties.put("mail.smtp.host", "smtp.gmail.com");
        mProperties.put("mail.smtp.port", "587");
        mProperties.put("mail.smtp.auth", "true");
        mProperties.put("mail.smtp.starttls.enable", "true");
        mProperties.put("mail.smtp.ssl.protocols", "TLSv1.2");

        mSession = Session.getInstance(mProperties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailFrom, passwordFrom);
            }
        });
    }

    private void crearCorreo() {
        try {
            mCorreo = new MimeMessage(mSession);
            mCorreo.setFrom(new InternetAddress(emailFrom));
            mCorreo.setRecipient(Message.RecipientType.TO, new InternetAddress(emailTo));
            mCorreo.setSubject(subject);
            mCorreo.setText(content, "UTF-8", "html");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendEmail() {
        try {
            Transport.send(mCorreo);
            System.out.println("Correo enviado correctamente.");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public static String generarMensajeConfirmacion(String pin, String nombreUsuario) {
        return "Estimado/a " + nombreUsuario + ",<br><br>" +
                "Se ha generado un código de confirmación para su cuenta:<br><br>" +
                "<b>Código de confirmación: " + pin + "</b><br><br>" +
                "Por favor, introduzca este código para completar la verificación de su cuenta.<br><br>" +
                "Si no solicitó este código, ignore este mensaje.<br><br>" +
                "Atentamente,<br>" +
                "El equipo de soporte de Ñam Dams";
    }
}
