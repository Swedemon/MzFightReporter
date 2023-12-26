package org.vmy;

import org.vmy.util.TextAreaOutputStream;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

public class MainFrame {
    public static TextAreaOutputStream consoleStream;
    public static TextAreaOutputStream reportStream;
    public static JLabel statusLabel = new JLabel();
    public static Parameters p = Parameters.getInstance();
    private static final Color BGCOLOR = new Color(0, 0, 0);
    private static final Color FGCOLOR = new Color(255, 255, 230);

    public static void addComponentToPane(Container pane) {
        JTabbedPane tabbedPane = new JTabbedPane();

        //console panel
        JPanel consolePanel = new JPanel();
        consolePanel.setLayout(new BorderLayout());
        JTextArea consoleArea = new JTextArea();
        setColors(consoleArea);
        consoleArea.setFont(new Font("Verdana", Font.PLAIN, 14));
        DefaultCaret caret = (DefaultCaret) consoleArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        consoleArea.setEditable(false);
        JScrollPane consoleScroll = new JScrollPane(consoleArea,
                VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_ALWAYS);
        consolePanel.add(consoleScroll);
        consoleStream = new TextAreaOutputStream(consoleArea, consoleScroll);
        consoleArea.append("Welcome to MzFightReporter v4.0.0");
        consoleArea.append("\r\nHosted at https://github.com/Swedemon/MzFightReporter");
        consoleArea.append("\r\n\r\nClick on the Settings tab to apply changes.\r\n\r\n");

        //report logs panel
        JPanel logPanel = new JPanel();
        logPanel.setLayout(new BorderLayout());
        JTextArea logArea = new JTextArea();
        setColors(logArea);
        logArea.setFont(new Font("Lucida Console", Font.PLAIN, 15));
        logArea.setEditable(false);
        caret = (DefaultCaret) logArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane logScroll = new JScrollPane (logArea,
                VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        logPanel.add(logScroll);
        reportStream = new TextAreaOutputStream(logArea, logScroll);
        logArea.append("Fight Reports will be displayed here.  Ensure the Discord Webhook is applied in the Settings tab.\r\n\r\n");

        //settings checkbox panel
        JPanel settingsGrandParentPanel = new JPanel();
        settingsGrandParentPanel.setLayout(new BoxLayout(settingsGrandParentPanel, BoxLayout.Y_AXIS));
        settingsGrandParentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        settingsGrandParentPanel.add(Box.createHorizontalGlue());
        JPanel settingsParentPanel = new JPanel();
        settingsParentPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JPanel settingsCheckboxPanel = new JPanel();
        settingsCheckboxPanel.setLayout(new BoxLayout(settingsCheckboxPanel, BoxLayout.Y_AXIS));
        buildCheckBox("Show Squad Summary", settingsCheckboxPanel, p.showSquadSummary);
        buildCheckBox("Show Enemy Summary", settingsCheckboxPanel, p.showEnemySummary);
        buildCheckBox("Show Damage", settingsCheckboxPanel, p.showDamage);
        buildCheckBox("Show Spike Damage", settingsCheckboxPanel, p.showSpikeDmg);
        buildCheckBox("Show Cleanses", settingsCheckboxPanel, p.showCleanses);
        buildCheckBox("Show Strips", settingsCheckboxPanel, p.showStrips);
        buildCheckBox("Show Defensive Boons", settingsCheckboxPanel, p.showDefensiveBoons);
        buildCheckBox("Show Heals", settingsCheckboxPanel, p.showHeals);
        buildCheckBox("Show Outgoing CC's", settingsCheckboxPanel, p.showCCs);
        buildCheckBox("Show Enemy Breakdown", settingsCheckboxPanel, p.showEnemyBreakdown);
        buildCheckBox("Show Quick Report", settingsCheckboxPanel, p.showQuickReport);
        buildCheckBox("Show Damage Graph", settingsCheckboxPanel, p.showDamageGraph);
        //settings label panel
        JPanel settingsTextFieldPanel = new JPanel();
        settingsTextFieldPanel.setLayout(new GridLayout(0, 2));
        settingsTextFieldPanel.setPreferredSize(new Dimension(300,200));
        buildTextField(settingsTextFieldPanel, "Discord Webhook", p.discordWebhook, 50, true);
        buildTextField(settingsTextFieldPanel, "Discord Thumbnail", p.discordThumbnail, 12, false);
        buildTextField(settingsTextFieldPanel, "Twitch Channel Name", p.twitchChannelName, 12, false);
        buildTextField(settingsTextFieldPanel, "Twitch Bot Token", p.twitchBotToken, 12, false);
        buildTextField(settingsTextFieldPanel, "Custom Log Folder", p.customLogFolder, 12, false);
        buildTextField(settingsTextFieldPanel, "Max Parse Memory (MB)", String.valueOf(p.maxParseMemory), 5, false);
        buildTextField(settingsTextFieldPanel, "Graph Player Limit", String.valueOf(p.graphPlayerLimit), 2, false);
        settingsParentPanel.add(settingsCheckboxPanel);
        settingsParentPanel.add(Box.createRigidArea(new Dimension(50, 0)));
        settingsParentPanel.add(settingsTextFieldPanel);
        settingsGrandParentPanel.add(settingsParentPanel);
        //settings button panel
        JPanel settingsButtonPane = new JPanel();
        settingsButtonPane.setPreferredSize(new Dimension(400, 45));
        settingsButtonPane.setMinimumSize(new Dimension(400, 45));
        settingsButtonPane.setMaximumSize(new Dimension(400, 45));
        settingsButtonPane.setLayout(new BoxLayout(settingsButtonPane, BoxLayout.LINE_AXIS));
        settingsButtonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        settingsButtonPane.add(Box.createHorizontalGlue());
        settingsButtonPane.add(new Button("Apply"));
        settingsButtonPane.add(Box.createRigidArea(new Dimension(30, 0)));
        settingsButtonPane.add(new Button("Reset"));
        settingsButtonPane.add(Box.createRigidArea(new Dimension(30, 200)));
        settingsButtonPane.add(Box.createHorizontalGlue());
        settingsGrandParentPanel.add(settingsButtonPane);
        JPanel settingsBottonPane = new JPanel();
        settingsBottonPane.setPreferredSize(new Dimension(400, 400));
        settingsBottonPane.setMaximumSize(new Dimension(400, 400));
        settingsBottonPane.setLayout(new BoxLayout(settingsBottonPane, BoxLayout.LINE_AXIS));
        settingsGrandParentPanel.add(settingsBottonPane);

        //about panel
        JPanel aboutPanel = new JPanel();
        aboutPanel.setLayout(new BoxLayout(aboutPanel, BoxLayout.Y_AXIS));
        aboutPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        aboutPanel.add(Box.createHorizontalGlue());
        aboutPanel.add(new JLabel("MzFightReporter v4.0.0"));
        aboutPanel.add(buildURLButton("https://github.com/Swedemon/MzFightReporter", "Website"));
        aboutPanel.add(new JLabel("Author: Swedemon.4670"));

        //add tab panels
        tabbedPane.addTab("Console", consolePanel);
        tabbedPane.addTab("Reports", logPanel);
        tabbedPane.addTab("Settings", settingsGrandParentPanel);
        tabbedPane.addTab("About", aboutPanel);

        pane.add(tabbedPane, BorderLayout.CENTER);
        pane.setBackground(BGCOLOR);
    }

    private static void setColors(JComponent component) {
        component.setBackground(BGCOLOR);
        component.setForeground(FGCOLOR);
    }

    private static void buildCheckBox(String label, JPanel settingsCheckboxPanel, boolean state) {
        Checkbox checkbox = new Checkbox(label);
        checkbox.setState(state);
        addMouseOver(checkbox);
        settingsCheckboxPanel.add(checkbox);
    }

    private static JTextField buildTextField(JPanel panel, String name, String value, int columns, boolean isBold) {
        GridBagConstraints c = new GridBagConstraints();
        JLabel label = new JLabel(name);
        label.setSize(50, 20);
        label.setPreferredSize(new Dimension(50, 20));
        label.setMinimumSize(new Dimension(50, 20));
        label.setMaximumSize(new Dimension(50, 20));
        if (isBold)
            label.setFont(new Font(label.getFont().getFontName(), Font.BOLD, label.getFont().getSize()));
        c.gridx = 100;
        panel.add(label, c);
        JTextField textField = new JTextField(value, columns);
        textField.setSize(70, 20);
        textField.setPreferredSize(new Dimension(70, 20));
        textField.setMinimumSize(new Dimension(70, 20));
        textField.setMaximumSize(new Dimension(70, 20));
        c.gridx = 200;
        addMouseOver(textField);
        panel.add(textField, c);
        textField.setHorizontalAlignment(SwingConstants.LEFT);
        return textField;
    }

    private static void addMouseOver(Component component) {
        component.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                component.setBackground(Color.GREEN);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                component.setBackground(UIManager.getColor("control"));
            }
        });
    }

    private static JLabel buildURLButton(final String url, String label) {
        JLabel webLabel = new JLabel();
        webLabel.setText("<html>" + label + " : <a href=\"\">"+url+"</a></html>");
        webLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        webLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (Exception ignored) {
                }
            }
        });
        return webLabel;
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("MzFightReporter UI v4.0.0");
        frame.setSize(900,700);
        frame.setLocation(200, 100);
        //frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        MainFrame mainFrame = new MainFrame();
        mainFrame.addComponentToPane(frame.getContentPane());

        //Create the status bar panel and shove it down the bottom of the frame
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        frame.add(statusPanel, BorderLayout.SOUTH);
        statusPanel.setPreferredSize(new Dimension(frame.getWidth(), 18));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        statusLabel = new JLabel("Status: Active");
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.add(statusLabel);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void start() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
            SwingUtilities.invokeLater(MainFrame::createAndShowGUI);
            Thread.sleep(1000);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        start();
    }
}
