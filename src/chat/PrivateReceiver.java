package chat;
 
import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
 
public class PrivateReceiver implements MessageListener {
 
    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            try {
                System.out.println("\n*** " +
                    ((TextMessage) message).getText() + " ***");
                System.out.print("> ");
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
 
    public static void start(String pseudo) throws JMSException {
        ConnectionFactory factory =
            new ActiveMQConnectionFactory(ChatConfig.BROKER_URL);
        Connection connection = factory.createConnection();
        connection.start();
 
        Session session = connection.createSession(
            false, Session.AUTO_ACKNOWLEDGE);
 
        // S'abonner à sa propre queue privée
        String queueName = ChatConfig.PRIVATE_QUEUE_PREFIX + pseudo;
        Destination queue = session.createQueue(queueName);
        MessageConsumer consumer = session.createConsumer(queue);
        consumer.setMessageListener(new PrivateReceiver());
 
        System.out.println("[" + pseudo + "] En écoute sur la queue privée \'"+
            queueName + "\' ...");
    }
}
