package ChatApplication;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Server implements ActionListener {

    // ── Colour palette ──────────────────────────────────────────────
    static final Color BG_DARK       = new Color(18, 18, 28);
    static final Color BG_PANEL      = new Color(26, 26, 40);
    static final Color HEADER_COLOR  = new Color(30, 30, 48);
    static final Color BUBBLE_OUT    = new Color(99, 102, 241);   // indigo
    static final Color BUBBLE_IN     = new Color(38, 38, 58);
    static final Color INPUT_BG      = new Color(38, 38, 58);
    static final Color SEND_BTN      = new Color(99, 102, 241);
    static final Color TEXT_PRIMARY  = new Color(240, 240, 255);
    static final Color TEXT_MUTED    = new Color(140, 140, 170);
    static final Color ONLINE_DOT    = new Color(72, 199, 142);

    JTextField text;
    JPanel     chatArea;
    JScrollPane scrollPane;
    static Box      vertical = Box.createVerticalBox();
    static JFrame   f        = new JFrame();
    static DataOutputStream dout = null;

    Server() {
        f.setUndecorated(true);
        f.setSize(460, 720);
        f.setLocationRelativeTo(null);
        f.setLocation(180, 60);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLayout(new BorderLayout());
        f.getContentPane().setBackground(BG_DARK);
        f.setShape(new RoundRectangle2D.Double(0, 0, 460, 720, 20, 20));

        f.add(buildHeader("Ahnaf", "Server"), BorderLayout.NORTH);
        f.add(buildChatPanel(),               BorderLayout.CENTER);
        f.add(buildInputBar(),                BorderLayout.SOUTH);

        f.setVisible(true);
    }

    // ── Header ───────────────────────────────────────────────────────
    private JPanel buildHeader(String name, String role) {
        JPanel header = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(HEADER_COLOR);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(460, 72));
        header.setBorder(new EmptyBorder(10, 16, 10, 16));

        // Avatar
        JLabel avatar = new JLabel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BUBBLE_OUT);
                g2.fillOval(0, 0, 46, 46);
                g2.setColor(TEXT_PRIMARY);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                FontMetrics fm = g2.getFontMetrics();
                String initials = name.substring(0, 1).toUpperCase();
                g2.drawString(initials,
                        (46 - fm.stringWidth(initials)) / 2,
                        (46 - fm.getHeight()) / 2 + fm.getAscent());
                // Online dot
                g2.setColor(ONLINE_DOT);
                g2.fillOval(32, 32, 12, 12);
                g2.setColor(HEADER_COLOR);
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(32, 32, 12, 12);
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(46, 46));

        // Name + status
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        info.setBorder(new EmptyBorder(0, 12, 0, 0));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        nameLabel.setForeground(TEXT_PRIMARY);

        JLabel statusLabel = new JLabel("● Online");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(ONLINE_DOT);

        info.add(nameLabel);
        info.add(statusLabel);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        left.add(avatar);
        left.add(info);

        // Close button
        JLabel close = new JLabel("✕");
        close.setFont(new Font("Segoe UI", Font.BOLD, 14));
        close.setForeground(TEXT_MUTED);
        close.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        close.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { System.exit(0); }
            public void mouseEntered(MouseEvent e) { close.setForeground(Color.RED); }
            public void mouseExited(MouseEvent e)  { close.setForeground(TEXT_MUTED); }
        });

        // Drag to move window
        final Point[] dragStart = {null};
        header.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { dragStart[0] = e.getPoint(); }
        });
        header.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point loc = f.getLocation();
                f.setLocation(loc.x + e.getX() - dragStart[0].x,
                              loc.y + e.getY() - dragStart[0].y);
            }
        });

        header.add(left,  BorderLayout.WEST);
        header.add(close, BorderLayout.EAST);
        return header;
    }

    // ── Chat scroll area ─────────────────────────────────────────────
    private JScrollPane buildChatPanel() {
        chatArea = new JPanel();
        chatArea.setLayout(new BoxLayout(chatArea, BoxLayout.Y_AXIS));
        chatArea.setBackground(BG_DARK);
        chatArea.setBorder(new EmptyBorder(12, 8, 12, 8));
        chatArea.add(vertical);

        scrollPane = new JScrollPane(chatArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setBackground(BG_DARK);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setBackground(BG_DARK);
        // Hide scrollbar UI
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(4, 0));
        return scrollPane;
    }

    // ── Input bar ────────────────────────────────────────────────────
    private JPanel buildInputBar() {
        JPanel bar = new JPanel(new BorderLayout(10, 0));
        bar.setBackground(BG_PANEL);
        bar.setBorder(new EmptyBorder(12, 14, 14, 14));

        text = new JTextField() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(INPUT_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        text.setOpaque(false);
        text.setBackground(new Color(0, 0, 0, 0));
        text.setForeground(TEXT_PRIMARY);
        text.setCaretColor(TEXT_PRIMARY);
        text.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        text.setBorder(new EmptyBorder(10, 16, 10, 16));
        text.putClientProperty("JTextField.placeholderText", "Type a message…");
        // Enter key sends
        text.addActionListener(this);

        JButton send = new JButton("Send") {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? BUBBLE_OUT.darker() : SEND_BTN);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
        };
        send.setOpaque(false);
        send.setContentAreaFilled(false);
        send.setBorderPainted(false);
        send.setFocusPainted(false);
        send.setPreferredSize(new Dimension(90, 44));
        send.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        send.addActionListener(this);

        bar.add(text, BorderLayout.CENTER);
        bar.add(send, BorderLayout.EAST);
        return bar;
    }

    // ── Send action ──────────────────────────────────────────────────
    public void actionPerformed(ActionEvent ae) {
        try {
            String out = text.getText().trim();
            if (out.isEmpty()) return;

            addBubble(out, true);
            text.setText("");

            if (dout != null) dout.writeUTF(out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Bubble builder ───────────────────────────────────────────────
    public static JPanel makeBubble(String msg, boolean outgoing) {
        Color bubbleColor = outgoing ? BUBBLE_OUT : BUBBLE_IN;
        Color textColor   = TEXT_PRIMARY;

        JPanel bubble = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bubbleColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
            }
        };
        bubble.setOpaque(false);
        bubble.setLayout(new BoxLayout(bubble, BoxLayout.Y_AXIS));
        bubble.setBorder(new EmptyBorder(10, 14, 6, 14));

        JLabel msgLabel = new JLabel("<html><p style='width:180px;'>" + msg + "</p></html>");
        msgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        msgLabel.setForeground(textColor);

        Calendar cal = Calendar.getInstance();
        JLabel timeLabel = new JLabel(new SimpleDateFormat("HH:mm").format(cal.getTime()));
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        timeLabel.setForeground(new Color(200, 200, 220, 160));
        timeLabel.setAlignmentX(outgoing ? Component.RIGHT_ALIGNMENT : Component.LEFT_ALIGNMENT);

        bubble.add(msgLabel);
        bubble.add(Box.createVerticalStrut(2));
        bubble.add(timeLabel);
        return bubble;
    }

    static void addBubble(String msg, boolean outgoing) {
        JPanel bubble = makeBubble(msg, outgoing);
        JPanel row = new JPanel(new FlowLayout(outgoing ? FlowLayout.RIGHT : FlowLayout.LEFT, 8, 2));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(440, Integer.MAX_VALUE));
        row.add(bubble);

        vertical.add(row);
        vertical.add(Box.createVerticalStrut(4));
        f.revalidate();
        f.repaint();

        // Auto-scroll to bottom
        SwingUtilities.invokeLater(() -> {
            JScrollBar sb = null;
            for (Component c : f.getContentPane().getComponents()) {
                if (c instanceof JScrollPane) {
                    sb = ((JScrollPane) c).getVerticalScrollBar();
                }
            }
            // scroll via the static reference
        });
    }

    // ── Socket server (background thread) ───────────────────────────
    public static void startServer() {
        new Thread(() -> {
            try (ServerSocket skt = new ServerSocket(6001)) {
                System.out.println("Server listening on port 6001…");
                Socket s = skt.accept();
                System.out.println("Client connected!");
                DataInputStream  din  = new DataInputStream(s.getInputStream());
                dout = new DataOutputStream(s.getOutputStream());

                while (true) {
                    String msg = din.readUTF();
                    SwingUtilities.invokeLater(() -> addBubble(msg, false));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Server::new);
        startServer();
    }
}
