package data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import ui.utils.Utils;

public class Emailer {

    private static final String DESTINATION_EMAIL_ADDRESS = "kjlubick@ncsu.edu";

    private String emailUsername;

    private String emailPassword;

    private boolean emailInitialized = false;

    private Session session;

    private static Emailer emailer = new Emailer();

    private Emailer() {
        try (Scanner scanner = new Scanner(Utils.getEmailFile(),
                StandardCharsets.UTF_8.name())) {
            emailUsername = scanner.nextLine();
            emailPassword = scanner.nextLine();
            emailInitialized = true;
        } catch (Exception e) {
            System.err.println("Could not set up email, check the credentials");
            emailUsername = "notInitialized";
            emailPassword = "notInitialized";
            e.printStackTrace();
        }
        setUpSession();
    }

    private void setUpSession()
    {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        this.session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailUsername, emailPassword);
            }
        });
    }

    @SuppressWarnings("unused")
    private void sendSampleEmail()
    {
        if (!emailInitialized) {
            System.err.println("Could not send email because auth wasn't initialized");
            return;
        }
        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailUsername));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(DESTINATION_EMAIL_ADDRESS));
            message.setSubject("Java Test email 2");
            message.setText("Dear Kevin,\n\n Please check if you got this!");

            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void sendRecords(File zipFile) throws IOException {
        if (!emailInitialized) {
            System.err.println("Could not send email because auth wasn't initialized");
            return;
        }
        try {

            Message message = new MimeMessage(session);
            message.setSentDate(new Date());
            message.setFrom(new InternetAddress(emailUsername));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(DESTINATION_EMAIL_ADDRESS));
            message.setSubject("Reco Study Results " + Utils.getUserName() + " " + Utils.getParticipantID()+" "+Utils.didUserConsent());

            MimeBodyPart text = new MimeBodyPart();
            text.setText("Find the results attached");

            MimeBodyPart zipFilePart = new MimeBodyPart();
            zipFilePart.attachFile(zipFile);

            Multipart mp = new MimeMultipart();
            mp.addBodyPart(text);
            mp.addBodyPart(zipFilePart);
            
            message.setContent(mp);
            
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public static void emailRecords() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File zipFile = zipRecords();
                    emailer.sendRecords(zipFile);
                    System.out.println("Email sent");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });      
        
        thread.start();

        try {
            Thread.sleep(1000);//wait for one seconds to give the email a head start in sending.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }     
    }

    // http://www.avajava.com/tutorials/lessons/how-do-i-zip-a-directory-and-all-its-contents.html
    private static File zipRecords() throws IOException {
        File directoryToZip = new File(Utils.getUserFolder());
        String userName = Utils.getUserName();
        if (userName == null || userName.isEmpty()) {
            userName = "BLANK";
        }
        File output = new File(userName+".zip");
        if (!output.createNewFile()) {
            System.err.println("Could not create file " + output.getAbsolutePath()+" maybe it already exists???");
        } else {
            System.out.println("Zipping to " + output.getAbsolutePath());
        }
        List<File> fileList = new ArrayList<File>();
        getAllFiles(directoryToZip, fileList);
        writeZipFile(directoryToZip, fileList, output);

        return output;
    }

    public static void getAllFiles(File dir, List<File> fileList) {
        File[] files = dir.listFiles();
        
        if (files == null) {
            return;
        }
        
        for (File file : files) {
            fileList.add(file);
            if (file.isDirectory()) {
                getAllFiles(file, fileList);
            }
        }
    }

    public static void writeZipFile(File directoryToZip, List<File> fileList, File output) {

        try (FileOutputStream fos = new FileOutputStream(output);
                ZipOutputStream zos = new ZipOutputStream(fos);) {
            for (File file : fileList) {
                if (!file.isDirectory()) { // we only zip files, not directories
                    addToZip(directoryToZip, file, zos);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addToZip(File directoryToZip, File file, ZipOutputStream zos) throws FileNotFoundException,
            IOException {

        try (FileInputStream fis = new FileInputStream(file);)
        {

            // we want the zipEntry's path to be a relative path that is relative
            // to the directory being zipped, so chop off the rest of the path
            String zipFilePath = file.getCanonicalPath().substring(directoryToZip.getCanonicalPath().length() + 1,
                    file.getCanonicalPath().length());
            ZipEntry zipEntry = new ZipEntry(zipFilePath);
            zos.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zos.write(bytes, 0, length);
            }

            zos.closeEntry();
        }
    }

}
