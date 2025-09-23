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
        consoleArea.append("\r\nSupport Discord invite is available at the github page above.");
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
        JPanel settingsHeaderPanel = new JPanel();
        settingsHeaderPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        settingsHeaderPanel.add(new Label("To save changes click the 'Apply' button."));
        settingsGrandParentPanel.add(settingsHeaderPanel);
        JPanel settingsParentPanel = new JPanel();
        settingsParentPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        //settings label panel
        JPanel settingsTextFieldPanel = new JPanel();
        settingsTextFieldPanel.setLayout(new GridLayout(0, 2));
        settingsTextFieldPanel.setPreferredSize(new Dimension(300,500));
        buildWebhookSelection(settingsTextFieldPanel, p.activeDiscordWebhook);
        buildTextField(settingsTextFieldPanel, "discordWebhook", "Discord Webhook #1", p.discordWebhook, "Discord Webhook #1. Refer to MzFightReporter website for setup instructions.", 50, true);
        buildTextField(settingsTextFieldPanel, "discordWebhookLabel", "Label Discord #1", p.discordWebhookLabel, "Label for Discord Webhook #1", 50, false);
        buildTextField(settingsTextFieldPanel, "discordWebhook2", "Discord Webhook #2", p.discordWebhook2, "Discord Webhook #2. Refer to MzFightReporter website for setup instructions.", 50, false);
        buildTextField(settingsTextFieldPanel, "discordWebhookLabel2", "Label Discord #2", p.discordWebhookLabel2, "Label for Discord Webhook #2", 50, false);
        buildTextField(settingsTextFieldPanel, "discordWebhook3", "Discord Webhook #3", p.discordWebhook3, "Discord Webhook #3. Refer to MzFightReporter website for setup instructions.", 50, false);
        buildTextField(settingsTextFieldPanel, "discordWebhookLabel3", "Label Discord #3", p.discordWebhookLabel3, "Label for Discord Webhook #3", 50, false);
        buildSpacer(settingsTextFieldPanel);
        buildTextField(settingsTextFieldPanel, "minFightDuration", "Min Fight Duration (sec)", String.valueOf(p.minFightDuration), "The minimum fight duration in seconds required to process a report.", 3, false);
        buildTextField(settingsTextFieldPanel, "minFightDowns", "Min Fight Downs", String.valueOf(p.minFightDowns), "The minimum number of squad plus enemy downs to process a report.", 2, false);
        buildTextField(settingsTextFieldPanel, "minFightTotalDmg", "Min Fight Total Dmg", String.valueOf(p.minFightTotalDmg), "The minimum total squad plus enemy damage to process a report.", 8, false);
        buildTextField(settingsTextFieldPanel, "discordThumbnail", "Discord Thumbnail", p.discordThumbnail, "Enter the URL of an image for your Discord content.", 12, false);
        buildTextField(settingsTextFieldPanel, "graphPlayerLimit", "Graph Player Limit", String.valueOf(p.graphPlayerLimit), "Number of players to display in the Discord graph image.", 2, false);
        buildTextField(settingsTextFieldPanel, "maxParseMemory", "Max Parse Memory (MB)", String.valueOf(p.maxParseMemory), "The maximum amount of system memory to allow this application to use.  It will be relinquished after each fight report.", 5, false);
        buildTextField(settingsTextFieldPanel, "maxUploadMegabytes", "Max Upload Size (MB)", String.valueOf(p.maxUploadMegabytes), "The arcdps file size to upload to the Dps.Report website.  Excessively large files will cause a long delay and in the end likely fail.", 2, false);
        buildTextField(settingsTextFieldPanel, "defaultLogFolder", "ArcDps Log Folder #1", p.defaultLogFolder, "The default arcdps log folder.  Customize this path as needed.", 12, false);
        buildTextField(settingsTextFieldPanel, "customLogFolder", "ArcDps Log Folder #2", p.customLogFolder, "The alternate arcdps log folder.  Customize this path as needed.",12, false);
        buildTextField(settingsTextFieldPanel, "twitchBotToken", "Twitch Bot Token", p.twitchBotToken, "Refer to MzFightReporter website for setup instructions.", 12, false);
        buildTextField(settingsTextFieldPanel, "twitchChannelName", "Twitch Channel Name", p.twitchChannelName, "Refer to MzFightReporter website for setup instructions.", 12, false);
        JPanel settingsCheckboxPanel = new JPanel();
        settingsCheckboxPanel.setLayout(new BoxLayout(settingsCheckboxPanel, BoxLayout.Y_AXIS));
        settingsCheckboxPanel.add(new Label("UI Options:"));
        buildCheckBox(settingsCheckboxPanel, "startMinimized", "Start Minimized", "Start this app minimized to the taskbar.", p.startMinimized);
        buildCheckBox(settingsCheckboxPanel, "minimizeToTray", "Minimize to System Tray (requires restart)", "Start this app minimized to the system tray.", p.minimizeToTray);
        buildCheckBox(settingsCheckboxPanel, "closeToTray", "Close to System Tray (requires restart)", "Closing this app minimizes to the system tray.", p.closeToTray);
        settingsCheckboxPanel.add(new Label("Report Options:"));
        buildCheckBox(settingsCheckboxPanel, "enableDiscordBot", "Enable Discord Bot", "Enable the sending of data to the selected Discord webhook(s).", p.enableDiscordBot);
        buildCheckBox(settingsCheckboxPanel, "enableTwitchBot", "Enable Twitch Upload", "Enable the sending of overview to the configured Twitch channel.", p.enableTwitchBot);
        buildCheckBox(settingsCheckboxPanel, "enableDiscordMobileMode", "Enable Compressed Mobile Mode", "Enable sending a compressed text width to the selected Discord webhook(s).  May assist on small form mobile devices.", p.enableDiscordMobileMode);
        buildCheckBox(settingsCheckboxPanel, "enableReportUpload", "Enable Report Upload", "Enable uploading the ArcDps data to the Dps.Report website.  Disabling this will reduce overall processing time.", p.enableReportUpload);
        buildCheckBox(settingsCheckboxPanel, "largeUploadsAfterParse", "Large Uploads Process Later", "Enable processing the Dps.Report upload after the fight report to receive data sooner.  This threshold is 7 megabytes.", p.largeUploadsAfterParse);
        buildCheckBox(settingsCheckboxPanel, "showSquadSummary", "Show Squad Summary", "Display the squad summary in the fight report.", p.showSquadSummary);
        buildCheckBox(settingsCheckboxPanel, "showEnemySummary", "Show Enemy Summary", "Display the enemy summary in the fight report.", p.showEnemySummary);
        buildCheckBox(settingsCheckboxPanel, "showDamage", "Show Damage", "Display the damage and down contribution in the fight report.", p.showDamage);
        buildCheckBox(settingsCheckboxPanel, "showBurstDmg", "Show Burst Damage", "Display the burst damage in the fight report.", p.showBurstDmg);
        buildCheckBox(settingsCheckboxPanel, "showCleanses", "Show Cleanses", "Display the cleanses in the fight report.", p.showCleanses);
        buildCheckBox(settingsCheckboxPanel, "showStrips", "Show Strips", "Display the strips in the fight report.", p.showStrips);
        buildCheckBox(settingsCheckboxPanel, "showDefensiveBoons", "Show Defensive Boons", "Display the defensive boons in the fight report.", p.showDefensiveBoons);
        buildCheckBox(settingsCheckboxPanel, "showOffensiveBoons", "Show Offensive Boons", "Display the offensive boons in the fight report.", p.showOffensiveBoons);
        buildCheckBox(settingsCheckboxPanel, "showHeals", "Show Heals", "Display the healing and barrier in the fight report.", p.showHeals);
        buildCheckBox(settingsCheckboxPanel, "showDefense", "Show Defense", "Display the defense (evades, blocks, invulns) in the fight report.", p.showDefense);
        buildCheckBox(settingsCheckboxPanel, "showDownsKills", "Show Outgoing Downs & Kills", "Display the squad downs and kills in the fight report.", p.showDownsKills);
        buildCheckBox(settingsCheckboxPanel, "showCCs", "Show Outgoing CC's", "Display the outgoing CC's and interrupts in the fight report.", p.showCCs);
        buildCheckBox(settingsCheckboxPanel, "showTopEnemySkills", "Show Top Enemy Skills", "Display the top enemy skills and conditions in the fight report.", p.showTopEnemySkills);
        buildCheckBox(settingsCheckboxPanel, "showEnemyBreakdown", "Show Enemy Breakdown", "Display the enemy breakdown in the fight report.", p.showEnemyBreakdown);
        buildCheckBox(settingsCheckboxPanel, "showQuickReport", "Show Quick Report", "Display the quick report in the fight report.", p.showQuickReport);
        buildCheckBox(settingsCheckboxPanel, "showDamageGraph", "Show Damage Graph", "Enable sending the damage graph to the selected Discord webhook(s).", p.showDamageGraph);
        settingsParentPanel.add(settingsTextFieldPanel);
        settingsParentPanel.add(Box.createRigidArea(new Dimension(50, 0)));
        settingsParentPanel.add(settingsCheckboxPanel);
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
        resetButton.addActionListener(actionEvent -> Parameters.getInstance().resetSettings(settingsMap));
        settingsButtonPane.add(Box.createRigidArea(new Dimension(30, 200)));
        settingsButtonPane.add(Box.createHorizontalGlue());
        settingsGrandParentPanel.add(settingsButtonPane);
        JPanel settingsBottomPane = new JPanel();
        settingsBottomPane.setPreferredSize(new Dimension(320, 280));
        settingsBottomPane.setMaximumSize(new Dimension(320, 280));
        settingsBottomPane.setLayout(new BoxLayout(settingsBottomPane, BoxLayout.LINE_AXIS));
        settingsGrandParentPanel.add(settingsBottomPane);

        //about panel
        JPanel aboutPanel = new JPanel();
        aboutPanel.setLayout(new BoxLayout(aboutPanel, BoxLayout.Y_AXIS));
        aboutPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        aboutPanel.add(Box.createHorizontalGlue());
        aboutPanel.add(new JLabel("MzFightReporter v" + Parameters.appVersion));
        aboutPanel.add(buildURLButton("https://github.com/Swedemon/MzFightReporter", "App Website"));
        aboutPanel.add(new JLabel("Support Discord invite is available on the above App Website."));

        //add tab panels
        tabbedPane.addTab("Console", consolePanel);
        tabbedPane.addTab("Reports", logPanel);
        tabbedPane.addTab("Settings", settingsGrandParentPanel);
        tabbedPane.addTab("About", aboutPanel);

        pane.add(tabbedPane, BorderLayout.CENTER);
        pane.setBackground(BGCOLOR);
    }

    private static void buildSpacer(JPanel settingsTextFieldPanel) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 100;
        settingsTextFieldPanel.add(new JLabel(""), c);
        settingsTextFieldPanel.add(new JLabel(""), c);
    }

    private static void setColors(JComponent component) {
        component.setBackground(BGCOLOR);
        component.setForeground(FGCOLOR);
    }

    private static void buildCheckBox(JPanel jPanel, String property, String label, String tooltip, boolean selected) {
        JCheckBox checkbox = new JCheckBox(label);
        checkbox.setToolTipText(tooltip);
        checkbox.setSelected(selected);
        checkbox.setName(property);
        addMouseOver(checkbox);
        jPanel.add(checkbox);
        settingsMap.put(property, checkbox);
    }

    private static void buildWebhookSelection(JPanel panel, int value) {
        GridBagConstraints c = new GridBagConstraints();
        JLabel jLabel = new JLabel("Active Discord Webhook #");
        jLabel.setToolTipText("Choose the active Discord webhook(s).");
        jLabel.setSize(50, 20);
        jLabel.setPreferredSize(new Dimension(50, 20));
        jLabel.setMinimumSize(new Dimension(50, 20));
        jLabel.setMaximumSize(new Dimension(50, 20));
        jLabel.setFont(new Font(jLabel.getFont().getFontName(), Font.BOLD, jLabel.getFont().getSize()));
        c.gridx = 100;
        panel.add(jLabel, c);
        JComboBox jComboBox = new JComboBox(new String[]{"1", "2", "3", "1 and 2", "1 and 3", "2 and 3", "1, 2 and 3", "None"});
        jComboBox.setSelectedIndex(value-1);
        jComboBox.setToolTipText("Choose the active Discord webhook(s).");
        panel.add(jComboBox);
        settingsMap.put("activeDiscordWebhook", jComboBox);
    }

    private static void buildTextField(JPanel panel, String property, String label, String value, String tooltip, int columns, boolean isBold) {
        GridBagConstraints c = new GridBagConstraints();
        JLabel jLabel = new JLabel(label);
        jLabel.setToolTipText(tooltip);
        jLabel.setSize(50, 20);
        jLabel.setPreferredSize(new Dimension(50, 20));
        jLabel.setMinimumSize(new Dimension(50, 20));
        jLabel.setMaximumSize(new Dimension(50, 20));
        if (isBold)
            jLabel.setFont(new Font(jLabel.getFont().getFontName(), Font.BOLD, jLabel.getFont().getSize()));
        c.gridx = 100;
        panel.add(jLabel, c);
        JTextField textField = new JTextField(value, columns);
        textField.setToolTipText(tooltip);
        textField.setSize(70, 20);
        textField.setPreferredSize(new Dimension(70, 20));
        textField.setMinimumSize(new Dimension(70, 20));
        textField.setMaximumSize(new Dimension(70, 20));
        c.gridx = 200;
        if ((property.equals("twitchBotToken") || property.equals("defaultLogFolder") || property.equals("customLogFolder"))
                && !StringUtils.isEmpty(value))
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
