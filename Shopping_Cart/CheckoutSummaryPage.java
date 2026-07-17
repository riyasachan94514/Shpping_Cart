// CheckoutSummaryPage.java
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class CheckoutSummaryPage extends JDialog {

    public CheckoutSummaryPage(
            Frame owner,               // IMPORTANT: real parent → no flickering
            List<CartItem> cart,
            double subtotal,
            int discount,
            double total,
            String appliedCoupon,
            Consumer<String> onConfirmAddress,  // <-- receives the shipping address
            Runnable onCancel,
            Color BG,
            Color PANEL,
            Color TEXT,
            Color PRIMARY_BLUE,
            Color HOVER_BLUE
    ){
        super(owner, "Order Summary", true);   // modal with owner
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(10,10));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // ---- HEADER ----
        JLabel head = new JLabel("Order Summary", SwingConstants.CENTER);
        head.setForeground(TEXT);
        head.setFont(new Font("Segoe UI", Font.BOLD, 22));
        head.setBorder(new EmptyBorder(10,0,10,0));
        add(head, BorderLayout.NORTH);

        // ---- MAIN PANEL ----
        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(PANEL);
        main.setBorder(new EmptyBorder(15,15,15,15));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8,8,8,8);
        gc.gridx = 0;
        gc.gridy = 0;
        gc.anchor = GridBagConstraints.WEST;

        // ---- ITEMS LABEL ----
        JLabel itemsL = new JLabel("Items:");
        itemsL.setForeground(TEXT);
        itemsL.setFont(itemsL.getFont().deriveFont(Font.BOLD, 15f));
        main.add(itemsL, gc);

        // ---- ITEMS LIST ----
        gc.gridy++;
        DefaultListModel<String> model = new DefaultListModel<>();
        for (CartItem c : cart)
            model.addElement(c.p.name + " x" + c.q + "   = ₹" + String.format("%,.2f", c.total()));

        JList<String> list = new JList<>(model);
        list.setBackground(new Color(22,32,48));
        list.setForeground(TEXT);
        list.setBorder(new LineBorder(TEXT,1,true));

        JScrollPane sp = new JScrollPane(list);
        sp.setPreferredSize(new Dimension(600, 220));
        main.add(sp, gc);

        // ---- SUBTOTAL ----
        gc.gridy++;
        JLabel subL = new JLabel("Subtotal:  ₹" + String.format("%,.2f", subtotal));
        subL.setForeground(TEXT);
        main.add(subL, gc);

        // ---- DISCOUNT ----
        gc.gridy++;
        JLabel disL = new JLabel("Discount:  -₹" + discount +
                (appliedCoupon == null || appliedCoupon.isEmpty() ? "" : " ("+appliedCoupon+")"));
        disL.setForeground(TEXT);
        main.add(disL, gc);

        // ---- TOTAL ----
        gc.gridy++;
        JLabel totL = new JLabel("Total:  ₹" + String.format("%,.2f", total));
        totL.setForeground(TEXT);
        totL.setFont(totL.getFont().deriveFont(Font.BOLD));
        main.add(totL, gc);

        // ---- ADDRESS LABEL ----
        gc.gridy++;
        JLabel addressL = new JLabel("Shipping Address:");
        addressL.setForeground(TEXT);
        addressL.setFont(addressL.getFont().deriveFont(Font.BOLD));
        main.add(addressL, gc);

        // ---- ADDRESS BOX ----
        gc.gridy++;
        JTextArea addressBox = new JTextArea(6, 50);
        addressBox.setLineWrap(true);
        addressBox.setWrapStyleWord(true);
        addressBox.setBackground(new Color(22,32,48));
        addressBox.setForeground(TEXT);

        JScrollPane asp = new JScrollPane(addressBox);
        asp.setPreferredSize(new Dimension(600, 130));
        main.add(asp, gc);

        // ---- BUTTONS ----
        gc.gridy++;
        gc.anchor = GridBagConstraints.CENTER;

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btns.setOpaque(false);

        JButton confirm = new JButton("Buy Now");
        confirm.setBackground(PRIMARY_BLUE);
        confirm.setForeground(Color.WHITE);
        confirm.setFocusPainted(false);

        JButton cancel = new JButton("Cancel");
        cancel.setBackground(new Color(120,120,120));
        cancel.setForeground(Color.WHITE);
        cancel.setFocusPainted(false);

        btns.add(confirm);
        btns.add(cancel);

        main.add(btns, gc);

        // ---- BUTTON ACTIONS ----
        confirm.addActionListener(e -> {
            String addr = addressBox.getText().trim();
            if (addr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter shipping address.");
                return;
            }
            // pass address back to caller
            onConfirmAddress.accept(addr);
            dispose();
        });

        cancel.addActionListener(e -> {
            onCancel.run();
            dispose();
        });

        add(main, BorderLayout.CENTER);

        // ---- FINAL WINDOW SETTINGS ----
        pack();                        // auto size correctly
        setResizable(false);           // no distortion
        setLocationRelativeTo(owner);  // center relative to main window
    }
}
