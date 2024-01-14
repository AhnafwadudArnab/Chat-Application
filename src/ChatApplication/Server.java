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
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Server implements ActionListener {
    JTextField text;
    JPanel a1;
    static Box vertical = Box.createVerticalBox();
    static JFrame f = new JFrame();

    static DataOutputStream dout=null;

    Server() {
        f.setLayout(null);

        JPanel jPanel = new JPanel();
        jPanel.setBackground(new Color(7, 94, 84));
        jPanel.setBounds(0, 0, 450, 70);
        f.add(jPanel);
        jPanel.setLayout(null);

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("Icons/3.png"));
        Image i2 = i1.getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel back = new JLabel(i3);
        back.setBounds(5, 20, 25, 25);
        jPanel.add(back);
        back.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent ae) {
                System.exit(0);
            }
        });
        ImageIcon i4 = new ImageIcon(ClassLoader.getSystemResource("Icons/man.png"));
        Image i5 = i4.getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT);
        ImageIcon i6 = new ImageIcon(i5);
        JLabel profile = new JLabel(i6);
        profile.setBounds(40, 10, 50, 50);
        jPanel.add(profile);

        ImageIcon i10 = new ImageIcon(ClassLoader.getSystemResource("Icons/phone.png"));
        Image i11 = i10.getImage().getScaledInstance(35, 30, Image.SCALE_DEFAULT);
        ImageIcon i12 = new ImageIcon(i11);
        JLabel phone = new JLabel(i12);
        phone.setBounds(360, 20, 35, 30);
        jPanel.add(phone);

        ImageIcon i7 = new ImageIcon(ClassLoader.getSystemResource("Icons/video.png"));
        Image i8 = i7.getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT);
        ImageIcon i9 = new ImageIcon(i8);
        JLabel video = new JLabel(i9);
        video.setBounds(300, 20, 25, 25);
        jPanel.add(video);

        ImageIcon i13 = new ImageIcon(ClassLoader.getSystemResource("Icons/3icon.png"));
        Image i14 = i13.getImage().getScaledInstance(10, 25, Image.SCALE_DEFAULT);
        ImageIcon i15 = new ImageIcon(i14);
        JLabel dots = new JLabel(i15);
        dots.setBounds(420, 20, 10, 25);
        jPanel.add(dots);

        JLabel name = new JLabel("Ahnaf");
        name.setBounds(110, 15, 100, 19);
        name.setForeground(Color.WHITE);
        name.setFont(new Font("SAN_SERIF", Font.BOLD, 16));
        jPanel.add(name);

        JLabel status = new JLabel("Active now");
        status.setBounds(110, 40, 100, 10);
        status.setForeground(Color.WHITE);
        status.setFont(new Font("SAN_SERIF", Font.BOLD, 11));
        jPanel.add(status);

        a1 = new JPanel();
        a1.setBounds(5, 75, 439, 528);
        f.add(a1);

        text = new JTextField();
        text.setBounds(5, 610, 310, 40);
        text.setFont(new Font("SAN_SERIF", Font.PLAIN, 12));
        f.add(text);

        JButton send = new JButton("Send");
        send.setBounds(310, 610, 135, 40);
        send.setBackground(new Color(7, 94, 84));
        send.addActionListener(this);
        send.setFont(new Font("SAN_SERIF", Font.PLAIN, 12));
        send.setForeground(Color.WHITE);
        f.add(send);

        f.setSize(450, 690);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close the application when the window is closed
        f.setLocation(200, 50);
        f.getContentPane().setBackground(Color.WHITE);
        f.setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        try {
            String out = text.getText();

            JPanel p2 = formatLabel(out);

            a1.setLayout(new BorderLayout());

            JPanel right = new JPanel(new BorderLayout());
            right.add(p2, BorderLayout.LINE_END);
            vertical.add(right);
            vertical.add(Box.createVerticalStrut(15));

            a1.add(vertical, BorderLayout.PAGE_START);

            text.setText("");
            dout.writeUTF(out);

            f.repaint();
            f.revalidate(); // Use revalidate instead of invalidate and validate
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JPanel formatLabel(String out) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel output = new JLabel("<html><p style=\"width:150px\">" + out + "</p></html>");

        output.setFont(new Font("Tahoma", Font.PLAIN, 14));
        output.setBackground(new Color(37, 211, 102));
        panel.add(output);
        output.setOpaque(true);
        output.setBorder(new EmptyBorder(15, 15, 15, 50));

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        JLabel time = new JLabel();
        time.setText(sdf.format(cal.getTime()));
        panel.add(time);
        return panel;
    }

    public static void main(String[] args) {
        // new Server();

        try {
            try (ServerSocket skt = new ServerSocket(6001)) {
                while (true) {
                    Socket s = skt.accept();
                    DataInputStream din = new DataInputStream(s.getInputStream());

                    DataOutputStream dout = new DataOutputStream(s.getOutputStream());

                    while (true) {
                        String msg = din.readUTF();
                        JPanel panel = formatLabel(msg);

                        JPanel left = new JPanel(new BorderLayout());
                        left.add(panel, BorderLayout.LINE_START);
                        vertical.add(left);
                        f.revalidate();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
