package chat;
 
import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import java.util.Scanner;
 
public class PrivateSender {
 
    public static void send(String fromPseudo) throws JMSException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Destinataire : ");
        String dest = scanner.nextLine().trim();
 
        System.out.print("Message privé : ");
        String text = scanner.nextLine().trim();
 
        ConnectionFactory factory =
            new ActiveMQConnectionFactory(ChatConfig.BROKER_URL);
        Connection connection = factory.createConnection();
        connection.start();
 
        Session session = connection.createSession(
            false, Session.AUTO_ACKNOWLEDGE);
 
        // La queue privée du destinataire
        String queueName = ChatConfig.PRIVATE_QUEUE_PREFIX + dest;
        Destination queue = session.createQueue(queueName);
        MessageProducer producer = session.createProducer(queue);
 
        String fullMsg = "[PRIVÉ de " + fromPseudo + "] " + text;
        producer.send(session.createTextMessage(fullMsg));
 
        System.out.println("Message privé envoyé à " + dest);
        connection.close();
    }
}
