package example;

import jdk.net.SocketFlow;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;

import javax.jms.*;
import javax.xml.soap.Text;
import java.util.logging.Logger;

public class myListener {

    public static void main(String[] args) throws JMSException, InterruptedException{

        String  actualMessage = "Pune is a great city, full of bright people";
        ConnectionService cs = new ConnectionService();
        MessageServices msgServices = new MessageServices();

        QueueConnectionFactory cf = (QueueConnectionFactory) cs.createQueueConnectionFactory();
        QueueConnection conn = cs.getQueueConnection(cf);
        QueueSession session = cs.getQueueSession(conn);

        msgServices.sendTextMessage(session, actualMessage);
        Thread.sleep(1000);
        System.out.println("\n\n\t");
        conn.start();

        msgServices.readTextMessage(session, "Pune");

        session.close();
        conn.close();

        System.exit(0);
    }

}

class MessageServices{

    public void sendTextMessage( Session session, String actualMessage) throws JMSException{
        MessageProducer msgProducer = session.createProducer(session.createQueue("Pune"));
        Logger.getGlobal().info("MESSAGE : "+actualMessage);
        TextMessage message = session.createTextMessage(actualMessage);
        msgProducer.send(message);
        return;
    }

    public void readTextMessage(Session session, String destination) throws JMSException{
        Queue queue = session.createQueue(destination);
        MessageConsumer msgConsumer = session.createConsumer(queue);
        boolean checkMore = true;

        while(checkMore) {
            TextMessage message = (TextMessage)msgConsumer.receive(1000);
            if(null!= message){
                System.out.println("The consumer side shows : "+message.getText());
            }
            checkMore = false;
        }

    }
}

class ConnectionService{

      public QueueConnectionFactory createQueueConnectionFactory(){
        return (QueueConnectionFactory)new ActiveMQConnectionFactory(
                "tcp://localhost:61616"
        );
    }

    public QueueConnection getQueueConnection(QueueConnectionFactory cf ) throws JMSException {
        return cf.createQueueConnection();
    }


    public QueueSession getQueueSession(QueueConnection conn) throws JMSException{
        return conn.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
    }

}
