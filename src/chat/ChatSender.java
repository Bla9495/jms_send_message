package chat;
 
import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import java.util.Scanner;
 
public class ChatSender {
 
    public static void main(String[] args) throws JMSException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Entrez votre pseudonyme : ");
        String pseudo = scanner.nextLine().trim();
 
        // Connexion au broker
        ConnectionFactory factory =
            new ActiveMQConnectionFactory(ChatConfig.BROKER_URL);
        Connection connection = factory.createConnection();
        connection.start();
 
        // Création d'une session non-transactionnelle
        Session session = connection.createSession(
            false, Session.AUTO_ACKNOWLEDGE);
 
        // Création du Topic
        Destination topic = session.createTopic(ChatConfig.CHAT_TOPIC);
        MessageProducer producer = session.createProducer(topic);
 
        System.out.println("[" + pseudo + "] Connecté. Tapez vos messages :");
        System.out.println("(tapez 'exit' pour quitter)");
 
        String input;
        while (!(input = scanner.nextLine()).equalsIgnoreCase("exit")) {
            // Encapsuler le pseudo dans le message
            String fullMessage = "[" + pseudo + "] " + input;
            TextMessage message = session.createTextMessage(fullMessage);
            producer.send(message);
            System.out.println("  >> Message envoyé");
        }
 
        System.out.println("Déconnexion...");
        connection.close();
        scanner.close();
    }
}
