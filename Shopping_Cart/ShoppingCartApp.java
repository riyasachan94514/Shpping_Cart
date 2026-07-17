import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

class Product {
    int id; String name; String cat; double price;
    Product(int id, String name, String cat, double price){ this.id=id; this.name=name; this.cat=cat; this.price=price; }
    public String toString(){ return "["+cat+"] "+id+" - "+name+" (₹"+price+")"; }
}
class CartItem {
    Product p; int q;
    CartItem(Product p,int q){ this.p=p; this.q=q; }
    double total(){ return p.price*q; }
}

class BlueButton extends JButton {
    private final Color base, hover, text;
    public BlueButton(String textLabel, Color base, Color hover, Color text){
        super(textLabel);
        this.base = base; this.hover = hover; this.text = text;
        setOpaque(false); setForeground(text); setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR)); setBorder(new EmptyBorder(8,16,8,16));
        setContentAreaFilled(false); setBackground(base);
        addMouseListener(new MouseAdapter(){ public void mouseEntered(MouseEvent e){ setBackground(hover); repaint(); } public void mouseExited(MouseEvent e){ setBackground(base); repaint(); }});
    }
    @Override protected void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground()); g2.fillRoundRect(0,0,getWidth(),getHeight(),14,14);
        super.paintComponent(g2); g2.dispose();
    }
    @Override public void updateUI(){ super.updateUI(); setContentAreaFilled(false); }
    @Override public boolean isOpaque(){ return false; }
}

class GlassPanel extends JPanel {
    private Color base; private int arc=20;
    public GlassPanel(Color base,int arc){ this.base=base; this.arc=arc; setOpaque(false); }
    @Override protected void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w=getWidth(), h=getHeight();
        if(w>0 && h>0){
            g2.setColor(new Color(0,0,0,36));
            g2.fillRoundRect(6,6,Math.max(0,w-12),Math.max(0,h-12),arc,arc);
            Color c1 = new Color(base.getRed(), base.getGreen(), base.getBlue(), Math.min(140, base.getAlpha()));
            GradientPaint gp = new GradientPaint(0,0,c1,0,h,new Color(255,255,255,30));
            g2.setPaint(gp);
            g2.fillRoundRect(0,0,Math.max(0,w-12),Math.max(0,h-12),arc,arc);
            g2.setColor(new Color(255,255,255,110));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0,0,Math.max(0,w-13),Math.max(0,h-13),arc,arc);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}


class Splash extends JWindow {
    private float opacityValue = 0f;
    private javax.swing.Timer fadeInTimer, fadeOutTimer;

    public Splash(Runnable onFinish) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(0, 0, screen.width, screen.height);
        setAlwaysOnTop(true);
        setOpacity(0f);

        // Define your custom background color
        Color bgColor = new Color(6, 20, 40);
        Color lighterBg = new Color(16, 40, 80);

        JPanel bg = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, bgColor, 0, getHeight(), lighterBg));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };

        bg.setLayout(null);

        int cx = screen.width / 2;
        int cy = screen.height / 2;

        ImageIcon icon = null;
        try {
            icon = new ImageIcon("cart.png");
        } catch (Exception ignored) {
        }

        JLabel iconLabel = new JLabel();
        if (icon != null && icon.getIconWidth() > 0)
            iconLabel.setIcon(new ImageIcon(icon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH)));
        else {
            iconLabel.setText("🛒");
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 120));
            iconLabel.setForeground(Color.WHITE);
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }
        iconLabel.setBounds(cx - 90, cy - 200, 180, 180);

        JLabel title = new JLabel("E-Commerce Shopping Cart", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 36));
        title.setBounds(cx - 360, cy + 10, 720, 48);

        JProgressBar bar = new JProgressBar();
        bar.setBounds(cx - 220, cy + 90, 440, 20);
        bar.setBackground(new Color(40, 40, 50));
        bar.setForeground(new Color(70, 150, 255));

        JLabel loadingTxt = new JLabel("Loading...", SwingConstants.CENTER);
        loadingTxt.setForeground(Color.WHITE);
        loadingTxt.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        loadingTxt.setBounds(cx - 100, cy + 120, 200, 26);

        bg.add(iconLabel);
        bg.add(title);
        bg.add(bar);
        bg.add(loadingTxt);

        setContentPane(bg);

        fadeInTimer = new javax.swing.Timer(20, e -> {
            opacityValue += 0.04f;
            if (opacityValue >= 1f) {
                opacityValue = 1f;
                fadeInTimer.stop();
            }
            setOpacity(Math.max(0f, Math.min(1f, opacityValue)));
        });

        fadeOutTimer = new javax.swing.Timer(20, e -> {
            opacityValue -= 0.04f;
            if (opacityValue <= 0f) {
                opacityValue = 0f;
                fadeOutTimer.stop();
                dispose();
                onFinish.run();
            }
            setOpacity(Math.max(0f, opacityValue));
        });

        fadeInTimer.start();

        new javax.swing.Timer(30, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int v = bar.getValue();
                if (v < 100) bar.setValue(v + 1);
                else {
                    ((javax.swing.Timer) e.getSource()).stop();
                    new javax.swing.Timer(350, ev -> fadeOutTimer.start()).start();
                }
            }
        }).start();

        bg.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (fadeInTimer.isRunning()) fadeInTimer.stop();
                fadeOutTimer.start();
            }
        });

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (fadeInTimer.isRunning()) fadeInTimer.stop();
                fadeOutTimer.start();
            }
        });
    }
}


public class ShoppingCartApp extends JFrame {
    // Models
    DefaultListModel<Product> electronics = new DefaultListModel<>();
    DefaultListModel<Product> clothing    = new DefaultListModel<>();
    DefaultListModel<Product> groceries   = new DefaultListModel<>();
    DefaultListModel<Product> books       = new DefaultListModel<>();
    DefaultListModel<Product> accessories = new DefaultListModel<>();
    java.util.List<CartItem> cart = new ArrayList<>();

    JTable table = new JTable(new DefaultTableModel(new Object[]{"ID","Name","Qty","Price","Line"},0){
        public boolean isCellEditable(int r,int c){return false;}
    });
    JLabel subLbl = new JLabel("Subtotal: ₹0.00"), disLbl = new JLabel("Discount: -₹0.00"), totLbl = new JLabel("Total: ₹0.00");

    DefaultListModel<String> couponNames = new DefaultListModel<>();
    Map<String,Integer> couponAmount = new LinkedHashMap<>();
    String appliedCoupon = "";
    int appliedAmount = 0;

    DecimalFormat df = new DecimalFormat("#,##0.00");

    // theme
    final Color APP_BG = new Color(10,25,40);
     final Color HEADER_BG = new Color(10,25,140);
    final Color PANEL_BG = new Color(15,35,60);
    final Color LIST_BG = new Color(20,45,70);
    final Color LIST_BORDER = new Color(0,0,0,120);
    final Color TEXT_COLOR = new Color(230,240,255);
    final Color PRIMARY_BLUE = new Color(59,130,246);
    final Color SELECTION_BG = new Color(50,110,255);
    final Color HOVER_BLUE = new Color(8,70,205,160);
    final Border ROUNDED_CARD = new LineBorder(new Color(0,0,0,60),1,true);
    final Border SOFT_SHADOW = new MatteBorder(4,4,6,4,new Color(0,0,0,20));

    // users & persistence
    Map<String,String> users = new LinkedHashMap<>();
    File usersFile = new File("users.dat");
    String currentUser = null;

    // UI
    private JPanel cards;
    private static final String CARD_LOGIN = "LOGIN", CARD_REGISTER = "REGISTER", CARD_MAIN = "MAIN";
    private JLayeredPane layered;
    private JPanel overlayPanel;
    private GlassPanel overlayContent;

    public ShoppingCartApp(){
        super("E-Commerce Shopping Cart");
        loadUsers();
        if(!users.containsKey("Dhruv")) users.put("Dhruv","java");

        // window
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(false);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(true);

        seedProducts(); seedCoupons();
        setGlobalFont(new Font("Segoe UI", Font.PLAIN,13));

        layered = new JLayeredPane(); layered.setLayout(null);
        getContentPane().add(layered, BorderLayout.CENTER);
        getContentPane().setBackground(APP_BG);

        cards = new JPanel(new CardLayout()); cards.setBackground(APP_BG);
        cards.add(buildLoginPage(), CARD_LOGIN);
        cards.add(buildRegisterPage(), CARD_REGISTER);
        cards.add(buildMainPage(), CARD_MAIN);
        layered.add(cards, JLayeredPane.DEFAULT_LAYER);

        overlayPanel = new JPanel(null); overlayPanel.setOpaque(false); overlayPanel.setVisible(false);
        layered.add(overlayPanel, JLayeredPane.PALETTE_LAYER);
        overlayContent = new GlassPanel(new Color(18,30,50,220), 18); overlayContent.setLayout(new GridBagLayout()); overlayContent.setVisible(false);
        overlayPanel.add(overlayContent);

        addComponentListener(new ComponentAdapter(){ @Override public void componentResized(ComponentEvent e){
            Dimension d = getContentPane().getSize(); cards.setBounds(0,0,d.width,d.height); overlayPanel.setBounds(0,0,d.width,d.height);
            if(overlayContent.isVisible()){ Dimension c = overlayContent.getPreferredSize(); overlayContent.setBounds((d.width-c.width)/2,(d.height-c.height)/2,c.width,c.height); }
        }@Override public void componentShown(ComponentEvent e){ Dimension d = getContentPane().getSize(); cards.setBounds(0,0,d.width,d.height); overlayPanel.setBounds(0,0,d.width,d.height); }});

        // Attempt auto-login using last_user.dat
        String last = loadLastUser();
        if(last != null && users.containsKey(last)){
            currentUser = last;
            loadCartForUser(currentUser);
            switchTo(CARD_MAIN);
            refresh();
        } else {
            switchTo(CARD_LOGIN);
        }

        // save on close
        addWindowListener(new WindowAdapter(){
            @Override public void windowClosing(WindowEvent e){
                saveUsers();
                if(currentUser != null) saveCartForUser(currentUser);
                saveLastUser(currentUser);
            }
        });

        pack();
    }

    private void switchTo(String key){ CardLayout cl=(CardLayout)cards.getLayout(); cl.show(cards,key); }

    // ---------------- login page ----------------
    private JPanel buildLoginPage(){
        JPanel page = new JPanel(new BorderLayout()); page.setBackground(APP_BG);
        JPanel header = new JPanel(new BorderLayout()); header.setBackground(HEADER_BG); header.setPreferredSize(new Dimension(0,80));
        JLabel title = new JLabel("E-Commerce Shopping Cart", SwingConstants.CENTER); title.setForeground(TEXT_COLOR); title.setFont(new Font("Segoe UI",Font.BOLD,28)); header.add(title, BorderLayout.CENTER);
        page.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout()); center.setOpaque(false);
        GlassPanel glass = new GlassPanel(new Color(255,255,255,18),20); glass.setLayout(new GridBagLayout()); glass.setPreferredSize(new Dimension(560,380));
        GridBagConstraints ig=new GridBagConstraints(); ig.insets=new Insets(10,12,8,12); ig.gridx=0; ig.gridy=0; ig.gridwidth=2; ig.anchor=GridBagConstraints.CENTER;
        JLabel formTitle=new JLabel("Login"); formTitle.setFont(new Font("Segoe UI",Font.BOLD,22)); formTitle.setForeground(TEXT_COLOR); glass.add(formTitle,ig);

        ig.gridy++; ig.gridwidth=1; ig.anchor=GridBagConstraints.WEST;
        JLabel uL=new JLabel("Username:"); uL.setForeground(TEXT_COLOR); JTextField userField=new JTextField(18); glass.add(uL,ig); ig.gridx=1; glass.add(userField,ig);

        ig.gridy++; ig.gridx=0; JLabel pL=new JLabel("Password:"); pL.setForeground(TEXT_COLOR); JPasswordField passField=new JPasswordField(18); glass.add(pL,ig); ig.gridx=1; glass.add(passField,ig);

        ig.gridy++; ig.gridx=0; ig.gridwidth=2; ig.anchor=GridBagConstraints.CENTER;
        JPanel btnRow=new JPanel(new FlowLayout(FlowLayout.CENTER,12,0)); btnRow.setOpaque(false);
        BlueButton loginBtn=new BlueButton("Login", PRIMARY_BLUE, HOVER_BLUE, Color.WHITE);
        BlueButton regBtn=new BlueButton("Register", new Color(255,255,255,40), new Color(255,255,255,60), Color.WHITE);
        btnRow.add(loginBtn); btnRow.add(regBtn); glass.add(btnRow,ig);

        ig.gridy++; JLabel tip=new JLabel("Tip: Register for New User"); tip.setForeground(new Color(255,255,255,200)); glass.add(tip,ig);
        center.add(glass); page.add(center, BorderLayout.CENTER);

        loginBtn.addActionListener(e -> {
            String u = userField.getText().trim(); String p = new String(passField.getPassword());
            if(u.isEmpty() || p.isEmpty()){ JOptionPane.showMessageDialog(this,"Enter both username and password.","Missing",JOptionPane.WARNING_MESSAGE); return; }
            String stored = users.get(u);
            if(stored != null && stored.equals(p)){
                currentUser = u;
                saveLastUser(u);
                loadCartForUser(u);
                switchTo(CARD_MAIN);
                refresh();
            } else JOptionPane.showMessageDialog(this,"Invalid credentials.","Login Failed",JOptionPane.ERROR_MESSAGE);
        });
        regBtn.addActionListener(e -> switchTo(CARD_REGISTER));
        return page;
    }

    // ---------------- register page ----------------
    private JPanel buildRegisterPage(){
        JPanel page=new JPanel(new BorderLayout()); page.setBackground(APP_BG);
        JPanel header=new JPanel(new BorderLayout()); header.setBackground(HEADER_BG); header.setPreferredSize(new Dimension(0,80));
        JLabel title=new JLabel("Create your account",SwingConstants.CENTER); title.setForeground(TEXT_COLOR); title.setFont(new Font("Segoe UI",Font.BOLD,26)); header.add(title,BorderLayout.CENTER); page.add(header,BorderLayout.NORTH);

        JPanel center=new JPanel(new GridBagLayout()); center.setOpaque(false);
        GlassPanel glass=new GlassPanel(new Color(255,255,255,18),20); glass.setPreferredSize(new Dimension(560,420)); glass.setLayout(new GridBagLayout());
        GridBagConstraints ig=new GridBagConstraints(); ig.insets=new Insets(10,12,8,12); ig.gridx=0; ig.gridy=0; ig.gridwidth=2; ig.anchor=GridBagConstraints.CENTER;
        JLabel formTitle=new JLabel("Register"); formTitle.setFont(new Font("Segoe UI",Font.BOLD,20)); formTitle.setForeground(TEXT_COLOR); glass.add(formTitle,ig);

        ig.gridy++; ig.gridwidth=1; ig.anchor=GridBagConstraints.WEST;
        JLabel nuL=new JLabel("Username:"); nuL.setForeground(TEXT_COLOR); JTextField nuField=new JTextField(18); glass.add(nuL,ig); ig.gridx=1; glass.add(nuField,ig);

        ig.gridy++; ig.gridx=0; JLabel pwL=new JLabel("Password:"); pwL.setForeground(TEXT_COLOR); JPasswordField pwField=new JPasswordField(18); glass.add(pwL,ig); ig.gridx=1; glass.add(pwField,ig);

        ig.gridy++; ig.gridx=0; JLabel cpwL=new JLabel("Confirm:"); cpwL.setForeground(TEXT_COLOR); JPasswordField cpwField=new JPasswordField(18); glass.add(cpwL,ig); ig.gridx=1; glass.add(cpwField,ig);

        ig.gridy++; ig.gridx=0; ig.gridwidth=2; JLabel note=new JLabel("Password must be at least 4 characters"); note.setForeground(new Color(255,255,255,200)); glass.add(note,ig);
        ig.gridy++; ig.gridwidth=2; ig.anchor=GridBagConstraints.CENTER;
        JPanel btnRow=new JPanel(new FlowLayout(FlowLayout.CENTER,12,0)); btnRow.setOpaque(false);
        BlueButton regBtn=new BlueButton("Register", PRIMARY_BLUE, HOVER_BLUE, Color.WHITE);
        BlueButton backBtn=new BlueButton("Back to Login", new Color(255,255,255,40), new Color(255,255,255,60), Color.WHITE);
        btnRow.add(regBtn); btnRow.add(backBtn); glass.add(btnRow,ig);

        center.add(glass); page.add(center,BorderLayout.CENTER);

        regBtn.addActionListener(e -> {
            String un = nuField.getText().trim(); String pw = new String(pwField.getPassword()); String cpw = new String(cpwField.getPassword());
            if(un.isEmpty() || pw.isEmpty()){ JOptionPane.showMessageDialog(this,"Username and password cannot be empty.","Missing",JOptionPane.WARNING_MESSAGE); return; }
            if(pw.length()<4){ JOptionPane.showMessageDialog(this,"Password must be at least 4 characters.","Weak Password",JOptionPane.WARNING_MESSAGE); return; }
            if(!pw.equals(cpw)){ JOptionPane.showMessageDialog(this,"Passwords do not match.","Mismatch",JOptionPane.WARNING_MESSAGE); return; }
            if(users.containsKey(un)){ JOptionPane.showMessageDialog(this,"Username already exists.","Duplicate",JOptionPane.WARNING_MESSAGE); return; }
            users.put(un,pw); saveUsers(); JOptionPane.showMessageDialog(this,"Registration successful! You can login now.","Registered",JOptionPane.INFORMATION_MESSAGE);
            switchTo(CARD_LOGIN);
        });
        backBtn.addActionListener(e -> switchTo(CARD_LOGIN));
        return page;
    }

    // ---------------- main page ----------------
    private JPanel buildMainPage(){
        JPanel root=new JPanel(new BorderLayout()); root.setBackground(APP_BG);
        JPanel header=new JPanel(new BorderLayout()); header.setOpaque(true); header.setBackground(HEADER_BG); header.setPreferredSize(new Dimension(0,72));
        header.add(new JLabel("  "), BorderLayout.WEST);
        JLabel title=new JLabel("E-Commerce Shopping Cart", SwingConstants.CENTER); title.setFont(new Font("Segoe UI",Font.BOLD,20)); title.setForeground(TEXT_COLOR); title.setBorder(new EmptyBorder(10,14,10,14)); header.add(title,BorderLayout.CENTER);
        JButton logout=new JButton("Logout"); logout.addActionListener(e -> { saveCartOnLogout(); currentUser=null; switchTo(CARD_LOGIN); }); logout.setForeground(SELECTION_BG); logout.setFocusPainted(false);
        JPanel rightWrap=new JPanel(new FlowLayout(FlowLayout.RIGHT)); rightWrap.setOpaque(false); rightWrap.add(logout); header.add(rightWrap, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        JTabbedPane tabs=new JTabbedPane(); tabs.setForeground(SELECTION_BG);tabs.setFont(tabs.getFont().deriveFont(Font.PLAIN,13f));
        tabs.addTab("Electronics", buildCategoryPanel(electronics,"Electronics"));
        tabs.addTab("Clothing", buildCategoryPanel(clothing,"Clothing"));
        tabs.addTab("Groceries", buildCategoryPanel(groceries,"Groceries"));
        tabs.addTab("Books", buildCategoryPanel(books,"Books"));
        tabs.addTab("Accessories", buildCategoryPanel(accessories,"Accessories"));
        tabs.addTab("Coupons", buildCouponsPanel());
        tabs.setBackground(APP_BG); tabs.setBorder(new EmptyBorder(6,6,6,6));
        root.add(tabs, BorderLayout.WEST);

        root.add(buildCartPanel(), BorderLayout.CENTER);
        root.add(buildTotalsPanel(), BorderLayout.SOUTH);
        return root;
    }

    JPanel buildCategoryPanel(DefaultListModel<Product> model, String titleText){
        JPanel card=new JPanel(new BorderLayout(8,8)); card.setBackground(PANEL_BG); card.setBorder(new CompoundBorder(SOFT_SHADOW, ROUNDED_CARD));
        JLabel head=new JLabel("  "+titleText); head.setOpaque(true); head.setBackground(new Color(20,28,40)); head.setForeground(TEXT_COLOR); head.setFont(head.getFont().deriveFont(Font.BOLD,14f));
        head.setBorder(new CompoundBorder(new MatteBorder(0,0,1,0,new Color(0,0,0,80)), new EmptyBorder(8,10,8,10)));
        DefaultListModel<String> display=new DefaultListModel<>(); for(int i=0;i<model.size();i++) display.addElement(model.get(i).toString());
        JList<String> list=new JList<>(display); list.setBackground(LIST_BG); list.setForeground(TEXT_COLOR); list.setSelectionBackground(SELECTION_BG); list.setSelectionForeground(Color.WHITE); list.setBorder(new LineBorder(LIST_BORDER,1,true)); list.setFont(list.getFont().deriveFont(13f)); list.setFixedCellHeight(26);
        JPopupMenu popup=new JPopupMenu(); JMenuItem buyNow=new JMenuItem("Buy Now (Open details)"); buyNow.addActionListener(e -> { int idx=list.getSelectedIndex(); if(idx<0){ msg("Select a product"); return; } showBuyNowOverlay(model.get(idx)); });
        JMenuItem addToCart=new JMenuItem("Add to Cart"); addToCart.addActionListener(e -> { int idx=list.getSelectedIndex(); if(idx<0){ msg("Select a product"); return; } addToCart(model.get(idx),1); refresh(); });
        popup.add(buyNow); popup.add(addToCart);
        list.addMouseListener(new MouseAdapter(){ public void mousePressed(MouseEvent e){ if(e.isPopupTrigger()) return; } public void mouseReleased(MouseEvent e){ if(e.isPopupTrigger()) popup.show(list,e.getX(),e.getY()); } public void mouseClicked(MouseEvent e){ if(e.getButton()==MouseEvent.BUTTON1 && e.getClickCount()==2){ int idx=list.getSelectedIndex(); if(idx>=0) showBuyNowOverlay(model.get(idx)); } } });
        JScrollPane sp=new JScrollPane(list); sp.getViewport().setBackground(LIST_BG); sp.setBorder(new LineBorder(new Color(0,0,0,60),1,true));
        JSpinner qtySpin=new JSpinner(new SpinnerNumberModel(1,1,50,1));
        JPanel south=new JPanel(new FlowLayout(FlowLayout.LEFT)); south.setOpaque(false);
        JLabel qtyLbl=new JLabel("Qty:"); qtyLbl.setForeground(TEXT_COLOR); qtyLbl.setFont(qtyLbl.getFont().deriveFont(Font.BOLD));
        BlueButton addBtn=new BlueButton("Add to Cart", PRIMARY_BLUE, HOVER_BLUE, Color.WHITE);
        addBtn.addActionListener(e -> { int idx=list.getSelectedIndex(); if(idx<0){ msg("Select a product"); return; } int q=(Integer)qtySpin.getValue(); addToCart(model.get(idx),q); refresh(); });
        south.add(qtyLbl); south.add(qtySpin); south.add(addBtn);
        card.add(head,BorderLayout.NORTH); card.add(sp,BorderLayout.CENTER); card.add(south,BorderLayout.SOUTH); card.setPreferredSize(new Dimension(430,0));
        return card;
    }

    private void showBuyNowOverlay(Product p){
        overlayPanel.setVisible(true); overlayContent.removeAll(); overlayContent.setPreferredSize(new Dimension(520,360));
        GridBagConstraints ig=new GridBagConstraints(); ig.insets=new Insets(10,10,10,10); ig.gridx=0; ig.gridy=0; ig.gridwidth=2; ig.anchor=GridBagConstraints.CENTER;
        JLabel title=new JLabel("Buy Now"); title.setFont(new Font("Segoe UI",Font.BOLD,20)); title.setForeground(TEXT_COLOR); overlayContent.add(title,ig);
        ig.gridy++; ig.gridwidth=1; ig.anchor=GridBagConstraints.WEST;
        JLabel nameL=new JLabel("Product:"); nameL.setForeground(TEXT_COLOR); JLabel nameV=new JLabel(p.name); nameV.setForeground(TEXT_COLOR); overlayContent.add(nameL,ig); ig.gridx=1; overlayContent.add(nameV,ig);
        ig.gridy++; ig.gridx=0; JLabel priceL=new JLabel("Price:"); priceL.setForeground(TEXT_COLOR); JLabel priceV=new JLabel("₹"+df.format(p.price)); priceV.setForeground(TEXT_COLOR); overlayContent.add(priceL,ig); ig.gridx=1; overlayContent.add(priceV,ig);
        ig.gridy++; ig.gridx=0; JLabel qtyL=new JLabel("Quantity:"); qtyL.setForeground(TEXT_COLOR); JSpinner qtySpin=new JSpinner(new SpinnerNumberModel(1,1,50,1)); overlayContent.add(qtyL,ig); ig.gridx=1; overlayContent.add(qtySpin,ig);
        ig.gridy++; ig.gridx=0; JLabel totalL=new JLabel("Total:"); totalL.setForeground(TEXT_COLOR); JLabel totalV=new JLabel("₹"+df.format(p.price)); totalV.setForeground(TEXT_COLOR); overlayContent.add(totalL,ig); ig.gridx=1; overlayContent.add(totalV,ig);
        qtySpin.addChangeListener(ev -> totalV.setText("₹"+df.format(p.price * (Integer)qtySpin.getValue())));
        ig.gridy++; ig.gridx=0; ig.gridwidth=2; ig.anchor=GridBagConstraints.CENTER; JPanel btnRow=new JPanel(new FlowLayout(FlowLayout.CENTER,12,0)); btnRow.setOpaque(false);
        BlueButton buyBtn=new BlueButton("Confirm Purchase", PRIMARY_BLUE, HOVER_BLUE, Color.WHITE);
        BlueButton cancelBtn=new BlueButton("Cancel", new Color(255,255,255,40), new Color(255,255,255,60), Color.WHITE);
        btnRow.add(buyBtn); btnRow.add(cancelBtn); overlayContent.add(btnRow,ig);
        buyBtn.addActionListener(ev -> { int q=(Integer)qtySpin.getValue(); long ts=System.currentTimeMillis(); String file="instant_receipt_"+ts+".txt"; try(PrintWriter pw=new PrintWriter(new FileWriter(file))){ pw.println("=== Instant Purchase Receipt "+ts+" ==="); pw.println("Item: "+p.name); pw.println("Quantity: "+q); pw.println("Price per item: ₹"+df.format(p.price)); pw.println("Total: ₹"+df.format(p.price*q)); pw.println("\n--- No coupons applied ---"); pw.println("\nThank you for your purchase!"); }catch(Exception ex){ msg("Failed to create receipt"); } msg("Purchase complete!\nSaved: "+file); hideOverlay(); });
        cancelBtn.addActionListener(ev -> hideOverlay());
        showOverlayCentered();
    }

    private void showOverlayCentered(){ overlayContent.setVisible(true); overlayPanel.setVisible(true); Dimension d=getContentPane().getSize(); Dimension pref=overlayContent.getPreferredSize(); overlayContent.setBounds((d.width-pref.width)/2,(d.height-pref.height)/2,pref.width,pref.height); overlayPanel.revalidate(); overlayPanel.repaint(); }
    private void hideOverlay(){ overlayContent.setVisible(false); overlayPanel.setVisible(false); overlayContent.removeAll(); }

    // ---------------- Checkout: open external window using CheckoutSummaryPage ----------------
    private void openCheckoutPage() {
        if (cart.isEmpty()) {
            msg("Cart is empty");
            return;
        }

        double sub = 0;
        for (CartItem ci : cart) sub += ci.total();

        int discount = (appliedAmount > sub) ? (int) sub : appliedAmount;
        double total = sub - discount;

        final double fSub = sub;
        final int fDiscount = discount;
        final double fTotal = total;
        final String fAppliedCoupon = appliedCoupon;

        // Create the dialog with parent "this" and a Consumer to receive address
      CheckoutSummaryPage win = new CheckoutSummaryPage(
        this,                       // Frame owner
        new ArrayList<>(cart),
        fSub,
        fDiscount,
        fTotal,
        fAppliedCoupon,

        // onConfirmAddress
        (String addressText) -> {
            saveReceiptForCheckout(fSub, fDiscount, fTotal, fAppliedCoupon, addressText);
            saveHistoryEntry(currentUser, new ArrayList<>(cart), fSub, fDiscount, fTotal, fAppliedCoupon, addressText);

            cart.clear();
            appliedCoupon = "";
            appliedAmount = 0;
            refresh();

            msg("Order confirmed and Checked Out.\nAddress: " + addressText);
        },

        // onCancel (Runnable)
        () -> { /* nothing */ },

        // Colors (NOT an array – your constructor expects 5 separate Color parameters)
        APP_BG,
        PANEL_BG,
        TEXT_COLOR,
        PRIMARY_BLUE,
        HOVER_BLUE
);

        win.setVisible(true);
    }

    // ---------------- coupons ----------------
    JPanel buildCouponsPanel(){
        JPanel wrapper=new JPanel(new BorderLayout(8,8)); wrapper.setBackground(PANEL_BG); wrapper.setBorder(new CompoundBorder(SOFT_SHADOW, ROUNDED_CARD));
        JLabel head=new JLabel("  Available Coupons"); head.setOpaque(true); head.setBackground(new Color(20,28,40)); head.setForeground(TEXT_COLOR); head.setFont(head.getFont().deriveFont(Font.BOLD,14f));
        head.setBorder(new CompoundBorder(new MatteBorder(0,0,1,0,new Color(0,0,0,80)), new EmptyBorder(8,10,8,10)));
        JList<String> list = new JList<>(couponNames); list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); list.setBackground(LIST_BG); list.setForeground(TEXT_COLOR); list.setSelectionBackground(SELECTION_BG); list.setSelectionForeground(Color.WHITE); list.setBorder(new LineBorder(LIST_BORDER,1,true));
        JScrollPane sp=new JScrollPane(list); sp.getViewport().setBackground(LIST_BG); sp.setBorder(new LineBorder(new Color(0,0,0,60),1,true));
        BlueButton applyBtn=new BlueButton("Apply Selected", PRIMARY_BLUE, HOVER_BLUE, Color.WHITE);
        BlueButton clearBtn=new BlueButton("Clear Coupon", PRIMARY_BLUE, HOVER_BLUE, Color.WHITE);
        applyBtn.addActionListener(e->{ String name=list.getSelectedValue(); if(name==null){ msg("Select a coupon"); return; } appliedCoupon=name; appliedAmount=couponAmount.getOrDefault(name,0); msg("Applied: "+appliedCoupon+" (₹"+appliedAmount+" off)"); refreshTotals(); });
        clearBtn.addActionListener(e->{ appliedCoupon=""; appliedAmount=0; refreshTotals(); });
        JTextArea info=new JTextArea("Tip: Only one flat-amount coupon can be applied at a time."); info.setEditable(false); info.setLineWrap(true); info.setWrapStyleWord(true); info.setBackground(PANEL_BG); info.setForeground(TEXT_COLOR); info.setBorder(new EmptyBorder(6,6,6,6));
        JPanel left=new JPanel(new BorderLayout(6,6)); left.setOpaque(false); left.add(head,BorderLayout.NORTH); left.add(sp,BorderLayout.CENTER);
        JPanel btns=new JPanel(new FlowLayout(FlowLayout.LEFT)); btns.setOpaque(false); btns.add(applyBtn); btns.add(clearBtn); left.add(btns,BorderLayout.SOUTH);
        JPanel right=new JPanel(new BorderLayout()); right.setOpaque(false); right.add(new JScrollPane(info),BorderLayout.CENTER);
        JSplitPane split=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,left,right); split.setResizeWeight(0.6); split.setBorder(null);
        wrapper.add(split, BorderLayout.CENTER); return wrapper;
    }

    // ---------------- cart panel ----------------
    JPanel buildCartPanel(){
        JPanel card=new JPanel(new BorderLayout(8,8)); card.setBackground(PANEL_BG); card.setBorder(new CompoundBorder(SOFT_SHADOW, ROUNDED_CARD));
        table.setBackground(new Color(18,28,40)); table.setForeground(TEXT_COLOR); table.setSelectionBackground(new Color(35,55,90)); table.setSelectionForeground(Color.WHITE); table.setRowHeight(26);
        JScrollPane sp=new JScrollPane(table); sp.getViewport().setBackground(new Color(18,28,40)); sp.setBorder(new LineBorder(new Color(0,0,0,60),1,true));
        JPanel controls=new JPanel(new FlowLayout(FlowLayout.LEFT)); controls.setOpaque(false);
        BlueButton removeBtn=new BlueButton("Remove item", PRIMARY_BLUE, HOVER_BLUE, Color.WHITE); BlueButton clearBtn=new BlueButton("Clear Cart", PRIMARY_BLUE, HOVER_BLUE, Color.WHITE); BlueButton checkout=new BlueButton("Checkout", PRIMARY_BLUE, HOVER_BLUE, Color.WHITE);
        removeBtn.addActionListener(e->{ int row=table.getSelectedRow(); if(row<0){ msg("Select cart row"); return; } int pid=Integer.parseInt(table.getValueAt(row,0).toString()); cart.removeIf(ci->ci.p.id==pid); refresh(); });
        clearBtn.addActionListener(e->{ cart.clear(); refresh(); });
        checkout.addActionListener(e-> openCheckoutPage());
        controls.add(removeBtn); controls.add(clearBtn); controls.add(new JLabel(" | ")); controls.add(checkout);
        card.add(sp, BorderLayout.CENTER); card.add(controls, BorderLayout.SOUTH); return card;
    }

    JPanel buildTotalsPanel(){
        // We'll make left area for View Past Purchases button and right area for the totals labels
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(new Color(6,18,36));
        container.setBorder(new EmptyBorder(8,10,8,10));

        // left: view history button
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.setOpaque(false);
        BlueButton viewHistoryBtn = new BlueButton("View Past Purchases", new Color(255,255,255,30), new Color(255,255,255,50), Color.WHITE);
        viewHistoryBtn.addActionListener(e -> {
            if (currentUser == null) { msg("No user logged in."); return; }
            HistoryWindow hw = new HistoryWindow(this, currentUser);
            hw.setVisible(true);
        });
        left.add(viewHistoryBtn);

        // center/right: totals labels
        JPanel right = new JPanel(new GridLayout(1,3,6,6));
        right.setOpaque(false);
        subLbl.setForeground(TEXT_COLOR); disLbl.setForeground(TEXT_COLOR); totLbl.setForeground(TEXT_COLOR);
        subLbl.setFont(subLbl.getFont().deriveFont(Font.BOLD)); disLbl.setFont(disLbl.getFont().deriveFont(Font.BOLD)); totLbl.setFont(totLbl.getFont().deriveFont(Font.BOLD));
        right.add(subLbl); right.add(disLbl); right.add(totLbl);

        container.add(left, BorderLayout.WEST);
        container.add(right, BorderLayout.EAST);
        return container;
    }

    // ---------------- data seeding ----------------
    void seedProducts(){ seedCategory("Electronics",40,1001,899,49999,electronics); seedCategory("Clothing",22,2001,299,2199,clothing); seedCategory("Groceries",24,3001,49,599,groceries); seedCategory("Books",16,4001,199,1499,books); seedCategory("Accessories",20,5001,149,2999,accessories); }
    void seedCategory(String cat,int count,int startId,int minPrice,int maxPrice, DefaultListModel<Product> model){
        Random rnd=new Random(42+startId); for(int i=0;i<count;i++){ int id=startId+i; int price=minPrice + rnd.nextInt(Math.max(1, maxPrice-minPrice+1)); model.addElement(new Product(id, cat+" Item "+(i+1), cat, price)); }
    }
    void seedCoupons(){ addCoupon("WELCOME50",50); addCoupon("SAVE100",100); addCoupon("FEST200",200); addCoupon("NEW300",300); addCoupon("BIG500",500); }
    void addCoupon(String name,int amount){ couponNames.addElement(name); couponAmount.put(name, amount); }

    void addToCart(Product p,int q){ for(CartItem ci: cart) if(ci.p.id==p.id){ ci.q = Math.max(1, ci.q+q); return; } cart.add(new CartItem(p,q)); }

    void refresh(){ DefaultTableModel m=(DefaultTableModel)table.getModel(); m.setRowCount(0); for(CartItem ci: cart) m.addRow(new Object[]{ci.p.id, ci.p.name, ci.q, "₹"+df.format(ci.p.price), "₹"+df.format(ci.total())}); refreshTotals(); }
    void refreshTotals(){ double sub=0; for(CartItem ci: cart) sub+=ci.total(); int discount = (appliedAmount > sub) ? (int)sub : appliedAmount; double total = sub - discount; subLbl.setText("Subtotal: ₹"+df.format(sub)); disLbl.setText("Discount: -₹"+df.format(discount) + (appliedCoupon.isEmpty()?"":" ("+appliedCoupon+")")); totLbl.setText("Total: ₹"+df.format(total)); }
    void msg(String s){ JOptionPane.showMessageDialog(this,s); }

    // ---------------- persistence: users & carts ----------------
    void loadUsers(){
        users.clear(); users.put("Dhruv","java");
        try(BufferedReader br=new BufferedReader(new FileReader(usersFile))){ String line; while((line=br.readLine())!=null){ line=line.trim(); if(line.isEmpty()||line.startsWith("#")) continue; String[] parts=line.split(":",2); if(parts.length==2) users.put(parts[0], parts[1]); } }catch(Exception ignore){}
    }
    void saveUsers(){ try(PrintWriter pw=new PrintWriter(new FileWriter(usersFile))){ pw.println("# username:password"); for(Map.Entry<String,String> e: users.entrySet()) pw.println(e.getKey()+":"+e.getValue()); }catch(Exception ignore){} }

    void saveLastUser(String user){ try(PrintWriter pw=new PrintWriter(new FileWriter("last_user.dat"))){ if(user!=null) pw.println(user); }catch(Exception ignored){} }
    String loadLastUser(){ try(BufferedReader br=new BufferedReader(new FileReader("last_user.dat"))){ return br.readLine(); }catch(Exception e){ return null; } }

    void saveCartForUser(String user){
        if(user==null) return;
        File f = new File("cart_"+user+".dat");
        try(PrintWriter pw=new PrintWriter(new FileWriter(f))){
            for(CartItem ci: cart) pw.println(ci.p.id + "," + ci.q);
        }catch(Exception ignored){}
    }
    void loadCartForUser(String user){
        cart.clear();
        if(user==null) return;
        File f = new File("cart_"+user+".dat");
        if(!f.exists()) return;
        try(BufferedReader br=new BufferedReader(new FileReader(f))){
            String line;
            while((line=br.readLine())!=null){
                String[] parts=line.split(",");
                if(parts.length<2) continue;
                int pid = Integer.parseInt(parts[0]); int qty = Integer.parseInt(parts[1]);
                Product found = findProductById(pid);
                if(found!=null) cart.add(new CartItem(found, qty));
            }
        }catch(Exception ignored){}
        refresh();
    }
    Product findProductById(int id){
        DefaultListModel<Product>[] models = new DefaultListModel[]{ electronics, clothing, groceries, books, accessories };
        for(DefaultListModel<Product> m: models) for(int i=0;i<m.size();i++) if(m.get(i).id==id) return m.get(i);
        return null;
    }
    void saveCartOnLogout(){ if(currentUser!=null) saveCartForUser(currentUser); }

    // ---------------- RECEIPT + HISTORY ----------------
    // Save receipt including address
    void saveReceiptForCheckout(double sub, int discount, double total, String coupon, String address){
        long ts=System.currentTimeMillis(); String file="receipt_"+ts+".txt";
        try(PrintWriter pw=new PrintWriter(new FileWriter(file))){
            pw.println("=== Receipt "+ts+" ===");
            pw.println("");
            for(CartItem ci: cart) pw.println(ci.p.name+" x"+ci.q+" = ₹"+df.format(ci.total()));
            pw.println("-----------------");
            pw.println("Subtotal: ₹"+df.format(sub));
            pw.println("Discount: -₹"+df.format(discount) + (coupon==null?"":" ("+coupon+")"));
            pw.println("Total: ₹"+df.format(total));
            pw.println("");
            pw.println("Shipping Address:");
            pw.println(address);
            pw.println("");
            pw.println("Thank you for your order!");
        }catch(Exception ignored){}
    }

    // Append a history entry for the user in a simple parseable format
    void saveHistoryEntry(String user, java.util.List<CartItem> items,double sub, int discount, double total,String coupon, String address){
        if(user==null) return;
        File f = new File("history_" + user + ".dat");
        try(PrintWriter pw = new PrintWriter(new FileWriter(f, true))){
            pw.println("TIMESTAMP:" + System.currentTimeMillis());
            // items as one-line semicolon separated
            StringBuilder sb = new StringBuilder();
            for(CartItem ci: items){
                if(sb.length()>0) sb.append(" ; ");
                sb.append(ci.p.name).append(" x").append(ci.q).append(" = ₹").append(df.format(ci.total()));
            }
            pw.println("ITEMS:" + sb.toString());
            pw.println("SUBTOTAL:" + df.format(sub));
            pw.println("DISCOUNT:" + df.format(discount));
            pw.println("TOTAL:" + df.format(total));
            pw.println("ADDRESS:" + address.replace("\n"," / "));
            pw.println("--------");
        }catch(Exception ex){ ex.printStackTrace(); }
    }

    // helper to read history raw entries (used by HistoryWindow if needed)
   java.util.List<String> loadHistoryEntriesForUser(String user){
    java.util.List<String> entries = new java.util.ArrayList<>();
        if(user==null) return entries;
        File f = new File("history_" + user + ".dat");
        if(!f.exists()) return entries;
        try(BufferedReader br = new BufferedReader(new FileReader(f))){
            StringBuilder entry = new StringBuilder();
            String line;
            while((line = br.readLine()) != null){
                if(line.equals("--------")){
                    entries.add(entry.toString());
                    entry.setLength(0);
                } else {
                    entry.append(line).append("\n");
                }
            }
            if(entry.length()>0) entries.add(entry.toString());
        } catch(Exception ex){ ex.printStackTrace(); }
        return entries;
    }

    void clearHistoryForUser(String user){
        if(user==null) return;
        File f = new File("history_" + user + ".dat");
        if(f.exists()) f.delete();
    }

    // helper quick checkout (legacy)
    void checkout(){
        if(cart.isEmpty()){ msg("Cart is empty"); return; }
        long ts=System.currentTimeMillis(); String file="receipt_"+ts+".txt";
        try(PrintWriter pw=new PrintWriter(new FileWriter(file))){
            pw.println("=== Receipt "+ts+" ===");
            for(CartItem ci: cart) pw.println(ci.p.name+" x"+ci.q+" = ₹"+df.format(ci.total()));
            double sub=0; for(CartItem ci: cart) sub+=ci.total();
            int discount = (appliedAmount > sub) ? (int)sub : appliedAmount;
            double total = sub - discount;
            pw.println("-----------------");
            pw.println("Subtotal: ₹"+df.format(sub));
            pw.println("Discount: -₹"+df.format(discount) + (appliedCoupon.isEmpty()?"":" ("+appliedCoupon+")"));
            pw.println("Total: ₹"+df.format(total));
            pw.println("Thanks!");
        }catch(Exception ex){ msg("Failed to write receipt"); return; }
        cart.clear(); appliedCoupon=""; appliedAmount=0; refresh();
        msg("Checked out. Saved "+file);
    }

    private void setGlobalFont(Font f){
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while(keys.hasMoreElements()){
            Object key = keys.nextElement();
            Object val = UIManager.get(key);
            if(val instanceof Font) UIManager.put(key, f);
        }
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            ShoppingCartApp app = new ShoppingCartApp();
            Splash splash = new Splash(() -> {
                app.setVisible(true);
            });
            splash.setVisible(true);
            splash.requestFocusInWindow();
        });
    }
}
