package chat;
 
import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
 
public class ChatReceiver implements MessageListener {
 
    private String pseudo;
 
    public ChatReceiver(String pseudo) {
        this.pseudo = pseudo;
    }
 
    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            try {
                String text = ((TextMessage) message).getText();
                // Ne pas afficher ses propres messages
                if (!text.startsWith("[" + pseudo + "]")) {
                    System.out.println("\n" + text);
                    System.out.print("> "); // ré-afficher le prompt
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
 
    public static void start(String pseudo) throws JMSException {
        ConnectionFactory factory =
            new ActiveMQConnectionFactory(ChatConfig.BROKER_URL);
        Connection connection = factory.createConnection();
        // Identifiant unique pour l'abonnement durable (optionnel)
        connection.setClientID(pseudo + "_subscriber");
        connection.start();
 
        Session session = connection.createSession(
            false, Session.AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic(ChatConfig.CHAT_TOPIC);
 
        // Abonnement au Topic
        MessageConsumer consumer = session.createConsumer(topic);
        consumer.setMessageListener(new ChatReceiver(pseudo));
 
        System.out.println("[" + pseudo + "] En écoute sur le Topic \'"
                + ChatConfig.CHAT_TOPIC + "\' ...");
        }
    }
