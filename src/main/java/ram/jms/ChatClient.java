package chat;
 
import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import java.util.Scanner;
 
public class ChatClient {
 
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Entrez votre pseudonyme : ");
        String pseudo = scanner.nextLine().trim();
 
        // Démarrer l'écoute asynchrone (Topic public + Queue privée)
        ChatReceiver.start(pseudo);
        PrivateReceiver.start(pseudo);
 
        // Connexion pour l'envoi
        ConnectionFactory factory =
            new ActiveMQConnectionFactory(ChatConfig.BROKER_URL);
        Connection connection = factory.createConnection();
        connection.start();
        Session session = connection.createSession(
            false, Session.AUTO_ACKNOWLEDGE);
        Destination topic =
            session.createTopic(ChatConfig.CHAT_TOPIC);
        MessageProducer producer = session.createProducer(topic);
 
        System.out.println("\n=== Bienvenue " + pseudo + " ===");
        System.out.println("Commandes disponibles :");
        System.out.println("  /msg <pseudo> <texte>  -> message privé");
        System.out.println("  /quit                  -> quitter");
        System.out.println("  <texte>                -> message public");
        System.out.println("");
 
        String input;
        System.out.print("> ");
        while (!(input = scanner.nextLine()).equalsIgnoreCase("/quit")) {
            if (input.startsWith("/msg ")) {
                // Message privé : /msg <dest> <texte>
                String[] parts = input.substring(5).split(" ", 2);
                if (parts.length < 2) {
                    System.out.println("Usage : /msg <pseudo> <texte>");
                } else {
                    String dest = parts[0];
                    String text = parts[1];
                    // Créer connexion temporaire pour la queue privée
                    Connection c2 = factory.createConnection();
                    c2.start();
                    Session s2 = c2.createSession(
                        false, Session.AUTO_ACKNOWLEDGE);
                    Destination q = s2.createQueue(
                        ChatConfig.PRIVATE_QUEUE_PREFIX + dest);
                    s2.createProducer(q).send(
                            s2.createTextMessage(
                                    "[PRIVÉ de " + pseudo + "] " + text));
                            c2.close();
                            System.out.println("  >> Privé envoyé à " + dest);
                        }
                    } else if (!input.isBlank()) {
                        // Message public sur le Topic
                        String fullMsg = "[" + pseudo + "] " + input;
                        producer.send(session.createTextMessage(fullMsg));
                    }
                    System.out.print("> ");
                }
         
                System.out.println("Au revoir, " + pseudo + " !");
                connection.close();
                scanner.close();
            }
        }
