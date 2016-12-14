import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ReceveurJMS extends JFrame{
	private PanelPhoto jPanelPhoto = new PanelPhoto();

	public ReceveurJMS(){
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());
		this.add(jPanelPhoto,BorderLayout.CENTER);
		this.setBounds(10,10,400,400);
		this.setVisible(true);
		
		try {
			Properties p= new Properties();
			p.put(Context.INITIAL_CONTEXT_FACTORY,"org.jboss.naming.remote.client.InitialContextFactory");
			p.put(Context.PROVIDER_URL,"remote://localhost:4447");
			p.put(Context.SECURITY_PRINCIPAL,"admin");
			p.put(Context.SECURITY_CREDENTIALS,"963984");
				
			Context ctx=new InitialContext(p);
			ConnectionFactory factory=(ConnectionFactory)ctx.lookup("jms/RemoteConnectionFactory");
			Connection conn=factory.createConnection("admin","963984");
			Destination destination= (Destination) ctx.lookup("jms/topic/testTopic2");
			Session session=conn.createSession(false,QueueSession.AUTO_ACKNOWLEDGE);
			//MessageProducer producer = session.createProducer(destination);
			MessageConsumer consumer = session.createConsumer(destination);
			consumer.setMessageListener(new MessageListener()
			
			{
				
				@Override
				public void onMessage(Message message) {
					// TODO Auto-generated method stub
					try{
						StreamMessage m= (StreamMessage) message;
						String nomPhoto =m.readString();
						int length=m.readInt();
						byte[] data = new byte[length];
						m.readBytes(data);
						ByteArrayInputStream bais=new ByteArrayInputStream(data);
						BufferedImage bi=ImageIO.read(bais);
						jPanelPhoto.setBufferedImage(bi);
						jPanelPhoto.repaint();
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					
					
				}
			});
		conn.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		public static void main(String[] args){
		new ReceveurJMS();
	}
	
}
