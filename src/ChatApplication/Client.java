package ChatApplication;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static ChatApplication.Server.f;

public class Client implements ActionListener {
    JTextField text;
    static JPanel a1;
    static Box verticle  = Box.createVerticalBox();
    static JFrame ff = new JFrame();
    static DataOutputStream dout ;
    Client(){
        ff.setLayout(null);

        JPanel jPanel = new JPanel();
        jPanel.setBackground(new Color(7,94,84));
        jPanel.setBounds(0,0,450,70);
        ff.add(jPanel);
        jPanel.setLayout(null);


        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("Icons/3.png"));
        Image i2 = i1.getImage().getScaledInstance(25,25,Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel back = new JLabel(i3);
        back.setBounds(5,20,25,25);
        jPanel.add(back);
        back.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent ae){
                System.exit(0);
            }
        });

        ImageIcon i4 = new ImageIcon(ClassLoader.getSystemResource("Icons/2.png"));
        Image i5 = i4.getImage().getScaledInstance(50,50,Image.SCALE_DEFAULT);
        ImageIcon i6 = new ImageIcon(i5);
        JLabel profile = new JLabel(i6);
        profile.setBounds(40,10,50,50);
        jPanel.add(profile);

        ImageIcon i10 = new ImageIcon(ClassLoader.getSystemResource("Icons/phone.png"));
        Image i11 = i10.getImage().getScaledInstance(35,30,Image.SCALE_DEFAULT);
        ImageIcon i12 = new ImageIcon(i11);
        JLabel phone = new JLabel(i12);
        phone.setBounds(300,20,25,30);
        jPanel.add(phone);

        ImageIcon i7 = new ImageIcon(ClassLoader.getSystemResource("Icons/video.png"));
        Image i8 = i7.getImage().getScaledInstance(25,25,Image.SCALE_DEFAULT);
        ImageIcon i9 = new ImageIcon(i8);
        JLabel video = new JLabel(i9);
        video.setBounds(360,20,35,30);
        jPanel.add(video);

        ImageIcon i13 = new ImageIcon(ClassLoader.getSystemResource("Icons/3icon.png"));
        Image i14 = i13.getImage().getScaledInstance(10,25,Image.SCALE_DEFAULT);
        ImageIcon i15 = new ImageIcon(i14);
        JLabel dots = new JLabel(i15);
        dots.setBounds(420,20,10,25);
        jPanel.add(dots);

        JLabel name =new JLabel("Client");
        name.setBounds(110,15,100,19);
        name.setForeground(Color.WHITE);
        name.setFont(new Font("SAN_SERIF", Font.BOLD,16));
        jPanel.add(name);

        JLabel status =new JLabel("Active now");
        status.setBounds(110,40,100,10);
        status.setForeground(Color.WHITE);
        status.setFont(new Font("SAN_SERIF", Font.BOLD,11));
        jPanel.add(status);

        a1 = new JPanel();
        a1.setBounds(5,75,439,528);
        ff.add(a1);

        text =new JTextField();
        text.setBounds(5,610,310,40);
        text.setFont(new Font("SAN_SERIF", Font.PLAIN,12));
        ff.add(text);

        JButton send = new JButton("Send");
        send.setBounds(310,610,135,40);
        send.setBackground(new Color(7,94, 81));
        send.addActionListener(this);
        send.setFont(new Font("SAN_SERIF", Font.PLAIN,12));
        send.setForeground(Color.WHITE);
        ff.add(send);

        ff.setSize(450,690);
        ff.setUndecorated(false);
        ff.setLocation(400,50);
        ff.getContentPane().setBackground(Color.WHITE);

        ff.setVisible(true);
    }
    public void actionPerformed (ActionEvent ae) {
        try {  String out = text.getText();
            JPanel p2 = formatLabel(out);
            a1.setLayout(new BorderLayout());
            JPanel right = new JPanel(new BorderLayout());
            right.add(p2, BorderLayout.LINE_END);
            verticle.add(right);
            verticle.add(Box.createVerticalStrut(15));

            a1.add(verticle, BorderLayout.PAGE_START);

            dout.writeUTF(out);
            text.setText("");

            ff.repaint();
            ff.invalidate();
            ff.validate();}catch(Exception e){
            e.printStackTrace();
        }
    }public static JPanel formatLabel(String out){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));

        JLabel output = new JLabel("<html><p style=\"width:150px\">" + out + "</p></html>");

        output.setFont(new Font("Tahoma",Font.PLAIN,14));
        output.setBackground(new Color(37,211,102));
        panel.add(output);
        output.setOpaque(true);
        output.setBorder(new EmptyBorder(15,15,15,50));

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        JLabel time = new JLabel() ;
        time.setText(sdf.format(cal.getTime()));
        panel.add(time);
        return panel;
    }


    public static void main(String[] args) {
        new Client();
        try {
            Socket s = new Socket("127.0.0.1",6001);
            DataInputStream din = new DataInputStream(s.getInputStream());
            dout = new DataOutputStream(s.getOutputStream());

            while (true){
                a1.setLayout(new BorderLayout());

                String msg;
                msg = din.readUTF();
                JPanel panel = formatLabel(msg);

                JPanel left = new JPanel(new BorderLayout());
                left.add(panel,BorderLayout.LINE_START);
                verticle.add(left);
                verticle.add(Box.createVerticalStrut(15));
                a1.add(verticle,BorderLayout.PAGE_START);
                f.validate();
            }


        }catch (Exception e){
            e.printStackTrace();
        }





    }
}
