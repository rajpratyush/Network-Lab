import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;

public class SingleChat extends javax.swing.JFrame {

    ServerSocket ss;
    Socket s;
    JTextField text=new JTextField();
    JButton send=new JButton();
    JTextArea messages=new JTextArea();
    PrintWriter pout;
    BufferedReader br;
    ActionListener al;
    String ipstring;
    boolean ready2send=false;
    SingleChat pt;
    boolean HorC;
    String cliOrServ;

    public SingleChat(boolean hostOrConnect,String ip)
    {

        ipstring=ip;
        setLayout(null);
        setSize(500,500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        add(text);
        text.setLocation(5,5);
        text.setSize(getWidth()-40-60,30);
        text.setEnabled(false);
        add(send);
        send.setLocation(10+text.getWidth(),text.getY());
        send.setText("Send");
        send.setSize(60,30);
        add(messages);
        messages.setEditable(false);
        messages.setBorder(new EtchedBorder());
        messages.setLocation(5,text.getHeight()+text.getY()+5);
        messages.setSize(getWidth()-30,getHeight()-text.getY()-text.getHeight()-50);

 al=new ActionListener()
 {
 public void actionPerformed(ActionEvent e)
  {
  ready2send=true;
  }
 };

 send.addActionListener(al);
        pt=this;
        HorC=hostOrConnect;
        if(HorC)
            cliOrServ="\nServer: ";
        else
            cliOrServ="\nClient: ";
        Messenger.start();

    }


    public static void main(String args[]) {


        int inp=JOptionPane.showConfirmDialog(null,"Do you want to host the chat?\nYes - Act as server\nNo - Act as client","Want to host a chat?",JOptionPane.YES_NO_OPTION);
        if(inp==0)
            {
            new SingleChat(true,null).setVisible(true);
            }
        else
            {
            String ipstring=JOptionPane.showInputDialog("Please enter the ip address");
            try{
                InetAddress.getByName(ipstring);
                new SingleChat(false,ipstring).setVisible(true);
            }catch(Exception e){JOptionPane.showMessageDialog(null,"Invalid or Unreachable IP");}
            }

    }


Thread Messenger=new Thread()
{
 public void run()
 {
  try{
  if(HorC)
        {
        messages.setText("Waiting for an incoming connection.\nEnter my ip at client side.\nMy ip: "+InetAddress.getLocalHost().getHostAddress());
        ss=new ServerSocket(4444);
        s= ss.accept();
        s.setKeepAlive(true);
        }
  else
        {
        messages.setText("Connecting to:"+ipstring+":9999");
        s=new Socket(InetAddress.getByName(ipstring),4444);
        }
  text.setEnabled(true);
  pout = new PrintWriter(s.getOutputStream(),true);
  br=new BufferedReader(new InputStreamReader(s.getInputStream()));
  messages.setText(messages.getText()+"\nConnected to:"+s.getInetAddress().getHostAddress()+":"+s.getPort());
  while(true)
  {
   if(ready2send==true)
        {
        pout.println(text.getText());
        messages.setText(messages.getText()+"\nMe: "+text.getText());
        text.setText("");
        ready2send=false;
        }
    if(br.ready())
        {
        messages.setText(messages.getText()+cliOrServ+br.readLine());
        }
    Thread.sleep(80);
  }
 }catch(Exception ex)
    {
    JOptionPane.showMessageDialog(pt,ex.getMessage());
    messages.setText("Cannot connect!");
      try {
          wait(3000);
      } catch (InterruptedException ex1) {}
    System.exit(0);
    }
 }
};
}
