package org.vmy;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.vmy.util.TextAreaOutputStream;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static java.awt.Frame.*;
import static javax.swing.ScrollPaneConstants.*;

public class MainFrame {
    public static TextAreaOutputStream consoleStream;
    public static TextAreaOutputStream reportStream;
    public static JLabel statusLabel = new JLabel();
    public static Parameters p = Parameters.getInstance();
    public static HashMap<String, Component> settingsMap = new LinkedHashMap<>();
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
        consoleArea.append("Welcome to MzFightReporter v" + Parameters.appVersion);
        consoleArea.append("\r\nHosted at https://github.com/Swedemon/MzFightReporter");
        consoleArea.append("\r\nSupport Discord at https://discord.gg/5JfZ3qpW3Q");
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
                VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_ALWAYS);
        logPanel.add(logScroll);
        reportStream = new TextAreaOutputStream(logArea, logScroll);
        logArea.append("New Fight Reports will be displayed below.\r\n\r\n");

        //settings checkbox panel
        JPanel settingsGrandParentPanel = new JPanel();
        settingsGrandParentPanel.setLayout(new BoxLayout(settingsGrandParentPanel, BoxLayout.Y_AXIS));
        settingsGrandParentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        settingsGrandParentPanel.add(Box.createHorizontalGlue());
        JPanel settingsParentPanel = new JPanel();
        settingsParentPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JPanel settingsCheckboxPanel = new JPanel();
        settingsCheckboxPanel.setLayout(new BoxLayout(settingsCheckboxPanel, BoxLayout.Y_AXIS));
        buildCheckBox(settingsCheckboxPanel, "enableDiscordBot", "Enable Discord Bot", p.enableDiscordBot);
        buildCheckBox(settingsCheckboxPanel, "enableTwitchBot", "Enable Twitch Upload", p.enableTwitchBot);
        buildCheckBox(settingsCheckboxPanel, "enableDiscordMobileMode", "Enable Compressed Mobile Mode", p.enableDiscordMobileMode);
        buildCheckBox(settingsCheckboxPanel, "enableReportUpload", "Enable Report Upload", p.enableReportUpload);
        buildCheckBox(settingsCheckboxPanel, "largeUploadsAfterParse", "Large Uploads Process Later", p.largeUploadsAfterParse);
        buildCheckBox(settingsCheckboxPanel, "showSquadSummary", "Show Squad Summary", p.showSquadSummary);
        buildCheckBox(settingsCheckboxPanel, "showEnemySummary", "Show Enemy Summary", p.showEnemySummary);
        buildCheckBox(settingsCheckboxPanel, "showDamage", "Show Damage", p.showDamage);
        buildCheckBox(settingsCheckboxPanel, "showSpikeDmg", "Show Spike Damage", p.showSpikeDmg);
        buildCheckBox(settingsCheckboxPanel, "showCleanses", "Show Cleanses", p.showCleanses);
        buildCheckBox(settingsCheckboxPanel, "showStrips", "Show Strips", p.showStrips);
        buildCheckBox(settingsCheckboxPanel, "showDefensiveBoons", "Show Defensive Boons", p.showDefensiveBoons);
        buildCheckBox(settingsCheckboxPanel, "showOffensiveBoons", "Show Offensive Boons", p.showOffensiveBoons);
        buildCheckBox(settingsCheckboxPanel, "showHeals", "Show Heals", p.showHeals);
        buildCheckBox(settingsCheckboxPanel, "showDownsKills", "Show Outgoing Downs & Kills", p.showDownsKills);
        buildCheckBox(settingsCheckboxPanel, "showCCs", "Show Outgoing CC's", p.showCCs);
        buildCheckBox(settingsCheckboxPanel, "showEnemyBreakdown", "Show Enemy Breakdown", p.showEnemyBreakdown);
        buildCheckBox(settingsCheckboxPanel, "showQuickReport", "Show Quick Report", p.showQuickReport);
        buildCheckBox(settingsCheckboxPanel, "showDamageGraph", "Show Damage Graph", p.showDamageGraph);
        buildCheckBox(settingsCheckboxPanel, "startMinimized", "Start Minimized", p.startMinimized);
        buildCheckBox(settingsCheckboxPanel, "minimizeToTray", "Minimize to System Tray (requires restart)", p.minimizeToTray);
        buildCheckBox(settingsCheckboxPanel, "closeToTray", "Close to System Tray (requires restart)", p.closeToTray);
        //settings label panel
        JPanel settingsTextFieldPanel = new JPanel();
        settingsTextFieldPanel.setLayout(new GridLayout(0, 2));
        settingsTextFieldPanel.setPreferredSize(new Dimension(300,200));
        //buildTextField(settingsTextFieldPanel, "discordThumbnail", "Discord Thumbnail", p.discordThumbnail, 12, false);
        buildTextField(settingsTextFieldPanel, "discordWebhook", "Discord Webhook", p.discordWebhook, 50, true);
        buildTextField(settingsTextFieldPanel, "graphPlayerLimit", "Graph Player Limit", String.valueOf(p.graphPlayerLimit), 2, false);
        buildTextField(settingsTextFieldPanel, "maxParseMemory", "Max Parse Memory (MB)", String.valueOf(p.maxParseMemory), 5, false);
        buildTextField(settingsTextFieldPanel, "maxUploadMegabytes", "Max Upload Size (MB)", String.valueOf(p.maxUploadMegabytes), 2, false);
        buildTextField(settingsTextFieldPanel, "customLogFolder", "Custom Log Folder", p.customLogFolder, 12, false);
        buildTextField(settingsTextFieldPanel, "twitchBotToken", "Twitch Bot Token", p.twitchBotToken, 12, false);
        buildTextField(settingsTextFieldPanel, "twitchChannelName", "Twitch Channel Name", p.twitchChannelName, 12, false);
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
        Button saveButton = new Button("Apply");
        settingsButtonPane.add(saveButton);
        saveButton.addActionListener(actionEvent -> {
            String errors = Parameters.getInstance().validateSettings(settingsMap);
            if (errors.length() == 0) {
                Parameters.getInstance().saveSettings(settingsMap);
                JOptionPane.showMessageDialog(null, "Settings updated successfully." + errors, "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Errors detected:\n\n" + errors, "Error Saving", JOptionPane.ERROR_MESSAGE);
            }
        });
        settingsButtonPane.add(Box.createRigidArea(new Dimension(30, 0)));
        Button resetButton = new Button("Reset");
        settingsButtonPane.add(resetButton);
        resetButton.addActionListener(actionEvent -> {
            Parameters.getInstance().resetSettings(settingsMap);
        });
        settingsButtonPane.add(Box.createRigidArea(new Dimension(30, 200)));
        settingsButtonPane.add(Box.createHorizontalGlue());
        settingsGrandParentPanel.add(settingsButtonPane);
        JPanel settingsBottonPane = new JPanel();
        settingsBottonPane.setPreferredSize(new Dimension(320, 400));
        settingsBottonPane.setMaximumSize(new Dimension(320, 400));
        settingsBottonPane.setLayout(new BoxLayout(settingsBottonPane, BoxLayout.LINE_AXIS));
        settingsGrandParentPanel.add(settingsBottonPane);

        //about panel
        JPanel aboutPanel = new JPanel();
        aboutPanel.setLayout(new BoxLayout(aboutPanel, BoxLayout.Y_AXIS));
        aboutPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        aboutPanel.add(Box.createHorizontalGlue());
        aboutPanel.add(new JLabel("MzFightReporter v" + Parameters.appVersion));
        aboutPanel.add(buildURLButton("https://github.com/Swedemon/MzFightReporter", "App Website"));
        aboutPanel.add(buildURLButton("https://discord.gg/5JfZ3qpW3Q", "Support Discord"));

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

    private static void buildCheckBox(JPanel jPanel, String property, String label, boolean selected) {
        JCheckBox checkbox = new JCheckBox(label);
        checkbox.setToolTipText(property);
        checkbox.setSelected(selected);
        checkbox.setName(property);
        addMouseOver(checkbox);
        jPanel.add(checkbox);
        settingsMap.put(property, checkbox);
    }

    private static void buildTextField(JPanel panel, String property, String label, String value, int columns, boolean isBold) {
        GridBagConstraints c = new GridBagConstraints();
        JLabel jLabel = new JLabel(label);
        jLabel.setToolTipText(property);
        jLabel.setSize(50, 20);
        jLabel.setPreferredSize(new Dimension(50, 20));
        jLabel.setMinimumSize(new Dimension(50, 20));
        jLabel.setMaximumSize(new Dimension(50, 20));
        if (isBold)
            jLabel.setFont(new Font(jLabel.getFont().getFontName(), Font.BOLD, jLabel.getFont().getSize()));
        c.gridx = 100;
        panel.add(jLabel, c);
        JTextField textField = new JTextField(value, columns);
        textField.setToolTipText(property);
        textField.setSize(70, 20);
        textField.setPreferredSize(new Dimension(70, 20));
        textField.setMinimumSize(new Dimension(70, 20));
        textField.setMaximumSize(new Dimension(70, 20));
        c.gridx = 200;
        if (property.equals("twitchBotToken") && !StringUtils.isEmpty(value))
            textField.setBackground(Color.black);
        addMouseOver(textField);
        panel.add(textField, c);
        textField.setHorizontalAlignment(SwingConstants.LEFT);
        textField.setScrollOffset(0);
        textField.setCaretPosition(0);
        settingsMap.put(property, textField);
    }

    private static void addMouseOver(Component component) {
        component.addMouseListener(new java.awt.event.MouseAdapter() {
            private final Color origBgColor = component.getBackground();
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                component.setBackground(Color.GREEN);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                component.setBackground(origBgColor);
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
                } catch (Exception ignored) {}
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
        JFrame frame = new JFrame("MzFightReporter v" + Parameters.appVersion);
        frame.setSize(1120, 700);
        frame.setLocation(200, 50);
        //frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage("mztray.png"));

        if (Parameters.getInstance().minimizeToTray || Parameters.getInstance().closeToTray) {
            TrayIcon trayIcon;
            if (SystemTray.isSupported()) {
                // create a popup menu
                PopupMenu popup = new PopupMenu();
                // get the SystemTray instance
                SystemTray tray = SystemTray.getSystemTray();
                // load an image
                Image image = Toolkit.getDefaultToolkit().getImage("mztray.png");
                // construct a TrayIcon
                trayIcon = new TrayIcon(image, "MzFightReporter v" + Parameters.appVersion, popup);
                // create menu item for the default action
                MenuItem viewItem = new MenuItem("Show Interface");
                ActionListener viewListener = event -> {
                    frame.setVisible(true);
                    frame.setExtendedState(JFrame.NORMAL);
                };
                viewItem.addActionListener(viewListener);
                popup.add(viewItem);
                // create menu item for the default action
                MenuItem exitItem = new MenuItem("Exit Program");
                ActionListener exitListener = event -> {
                    tray.remove(trayIcon);
                    System.exit(0);
                };
                exitItem.addActionListener(exitListener);
                popup.add(exitItem);
                // set the TrayIcon properties
                trayIcon.addActionListener(viewListener);

                if (Parameters.getInstance().closeToTray) {
                    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                    frame.addWindowListener(new WindowAdapter() {
                        public void windowClosing(WindowEvent e) {
                            try {
                                frame.setState(Frame.ICONIFIED);
                                tray.add(trayIcon);
                                frame.setVisible(false);
                            } catch (AWTException ex) {
                                System.out.println("Unable to add to system tray...");
                            }
                        }
                    });
                }

                frame.addWindowStateListener(e -> {
                    if (e.getNewState() == ICONIFIED) {
                        try {
                            tray.add(trayIcon);
                            frame.setVisible(false);
                        } catch (AWTException ex) {
                            System.out.println("Unable to add to system tray...");
                        }
                    }
                    if (Parameters.getInstance().minimizeToTray && e.getNewState() == 7) {
                        try {
                            tray.add(trayIcon);
                            frame.setVisible(false);
                        } catch (AWTException ex) {
                            System.out.println("Unable to add to system tray...");
                        }
                    }
                    if (e.getNewState() == MAXIMIZED_BOTH) {
                        tray.remove(trayIcon);
                        frame.setVisible(true);
                    }
                    if (e.getNewState() == NORMAL) {
                        tray.remove(trayIcon);
                        frame.setVisible(true);
                    }
                });
            }
        }

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

        if (Parameters.getInstance().startMinimized) {
            frame.setState(Frame.ICONIFIED);
        }
    }

    private static final String base64Image = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABtElEQVQ4y6XRv2sTYRzH8XfOp03uYhMNSayorQotDWgNogaHGgeHLnZx0H/ASUToP+Hg7uIfUTp2CoiWokVJEYQmhgx6NhG9JrnzmvvxPA6lqUcyNZ/xeb68+P6IlZe3VxgjAlgfFwBgppBicXYS/yCg8naf+Bmde3eSxBRsfuxQupXmlHZYa5kO73fcKNAXgtUXs+D6fK12KT65yOrDFPvNLp8bHk8fZ6nUJI/KU6y9rg8A7QiwzD4+oOmCuwtx7hcNAHqWh1KCjbU9krk4AF8a7mAEbXiqGKWlLFenB81h1v/wpuJw/YKA0Gf3mz8asG0fKeFGOYfT7kfYsxmDTErD/e3RCtRowG271CyJhmLjkxMB5m+n0WPws9kjCBgNoCQfqg5+74CaGUa+bl5LArC7YyNHnfEo77a6lBICW/5vaywuJEAptqpupH5oiY3NFs9e/oi8TWbjXJ7SCP2QjqHz6nl+GDh3xSCT08kmwZewVDhsOX/JoFhIMSGAUPJg5Ty/TO/4ZuXlbQWQziWYyQuadZueB3Nzp0lMAAr2rIDpzPG0re8u7U4YBU4aAfwdBxg7/wDO053gp55eKwAAAABJRU5ErkJggg==";
    private static final byte[] iconBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);

    public static void start() {
        try {
            if (!new File("mztray.png").exists()) {
                try {
                    FileUtils.writeByteArrayToFile(new File("mztray.png"), iconBytes);
                } catch (Exception ignored) {}
            }
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
