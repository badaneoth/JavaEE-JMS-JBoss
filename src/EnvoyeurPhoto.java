import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class EnvoyeurPhoto extends JFrame {
	private JLabel jLabelPhoto = new JLabel("photo");
	private JComboBox<String> jComboBoxPhotos;
	private JButton jButtonEnvoyer = new JButton("envoyer");
	private PanelPhoto panelPhoto = new PanelPhoto();

	public EnvoyeurPhoto(){
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());
		JPanel jPanelN =new JPanel();
		File f= new File("photos");
		String[] photos = f.list();
		jComboBoxPhotos =new JComboBox<String>(photos);
		jPanelN.setLayout(new FlowLayout());
		jPanelN.add(jLabelPhoto);
		jPanelN.add(jComboBoxPhotos);
		jPanelN.add(jButtonEnvoyer);
		this.add(jPanelN,BorderLayout.NORTH);
		this.add(panelPhoto,BorderLayout.CENTER);
		this.setBounds(10,10,400,300);
		this.setVisible(true);
		jComboBoxPhotos.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				String photo=(String) jComboBoxPhotos.getSelectedItem();
		File file= new File("photos/"+photo);
		BufferedImage bi;
		try {
			bi = ImageIO.read(file);
			panelPhoto.setBufferedImage(bi);
			panelPhoto.repaint();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
			}
		});
		jButtonEnvoyer.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				try {
				Properties p= new Properties();
				p.put(Context.INITIAL_CONTEXT_FACTORY,"org.jboss.naming.remote.client.InitialContextFactory");
				p.put(Context.PROVIDER_URL,"remote://localhost:4447");
				p.put(Context.SECURITY_PRINCIPAL,"admin");
				p.put(Context.SECURITY_CREDENTIALS,"963984");
					
				Context ctx=new InitialContext(p);
				ConnectionFactory factory=(ConnectionFactory)ctx.lookup("jms/RemoteConnectionFactory");
				Connection conn=factory.createConnection("admin","963984");
				Destination destination= (Destination) ctx.lookup("jms/java:/topic/testTopic");
				Session session=conn.createSession(false,QueueSession.AUTO_ACKNOWLEDGE);
				MessageProducer producer = session.createProducer(destination);
				conn.start();
				
				File file= new File("photos/"+(String) jComboBoxPhotos.getSelectedItem());
				FileInputStream fis=new FileInputStream(f);
				byte[] data = new byte[(int)f.length()];
				fis.read(data);
				StreamMessage message =session.createStreamMessage();
				message.writeString((String)jComboBoxPhotos.getSelectedItem());
				message.writeInt(data.length);
				message.writeBytes(data);
				producer.send(message);
				} catch (NamingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JMSException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
				
				
			}
		});
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new EnvoyeurPhoto();
	}

}
