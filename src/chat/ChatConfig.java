package chat;
 
public class ChatConfig {
    // Adresse du broker – à modifier selon votre réseau
    public static final String BROKER_URL = "tcp://IP_SERVEUR:61616";
 
    // Nom du Topic pour le chat public
    public static final String CHAT_TOPIC = "CHAT_PUBLIC";
 
    // Préfixe de Queue pour les messages privés
    // Format : PRIVATE_<destinataire>
    public static final String PRIVATE_QUEUE_PREFIX = "PRIVATE_";
}
