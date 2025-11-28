package mx.edu.utez.proyectorecetario.util;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import java.util.Properties;
import java.util.Random;

public class Email {
    private final String emailFrom = "recetarionamdams@gmail.com";
    private final String passwordFrom = "irtzhpqxjysmrldn";
    private String emailTo;
    private String subject;
    private String content;

    private Properties mProperties;
    private Session mSession;
    private MimeMessage mCorreo;

    // Constructor
    public Email(String emailTo, String nombreUsuario) {
        this.emailTo = emailTo;
        this.subject = "Mensaje de Confirmación.";
        this.content = generarMensajeConfirmacion(generarPin(), nombreUsuario);

        mProperties = new Properties();
        configurarPropiedades();
        crearCorreo();
    }

    private void configurarPropiedades() {
        mProperties.put("mail.smtp.host", "smtp.gmail.com");
        mProperties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        mProperties.setProperty("mail.smtp.starttls.enable", "true");
        mProperties.setProperty("mail.smtp.port", "587");
        mProperties.setProperty("mail.smtp.user", emailFrom);
        mProperties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
        mProperties.setProperty("mail.smtp.auth", "true");

        mSession = Session.getInstance(mProperties);
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
            Transport transport = mSession.getTransport("smtp");
            transport.connect(emailFrom, passwordFrom);
            transport.sendMessage(mCorreo, mCorreo.getAllRecipients());
            transport.close();
            System.out.println("Correo enviado correctamente.");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public static String generarPin() {
        return String.valueOf(1000 + new Random().nextInt(9000));
    }

    public static String generarMensajeConfirmacion(String pin, String nombreUsuario) {
        return "Estimado/a " + nombreUsuario + ",\n\n" +
                "Se ha generado un código de confirmación para su cuenta:\n\n" +
                "Código de confirmación: " + pin + "\n\n" +
                "Por favor, introduzca este código para completar la verificación de su cuenta.\n\n" +
                "Si no solicitó este código, ignore este mensaje.\n\n" +
                "Atentamente,\n" +
                "El equipo de soporte de Ñam Dams";
    }

    public static void main(String[] args) {
        String usuarioActual = "Damian";
        String emailDestino = "hdamian955@gmail.com";

        Email email = new Email(emailDestino, usuarioActual);
        email.sendEmail();
    }
}
