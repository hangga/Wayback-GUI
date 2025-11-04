package id.web.hangga;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;

public class WaybackQueryTool extends JFrame {
    private final JTextField domainField;
    private final JTextArea resultArea;
    private final JComboBox<String> modeSelector;
    private final JButton fetchButton;

    public WaybackQueryTool() {
        setTitle("Wayback Query Tool");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Dark theme
        Color bg = new Color(30, 30, 30);
        Color fg = new Color(220, 220, 220);
        getContentPane().setBackground(bg);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(bg);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(bg);

        JLabel label = new JLabel("Domain/URL: ");
        label.setForeground(fg);
        domainField = new JTextField(30);
        domainField.setBackground(new Color(50, 50, 50));
        domainField.setForeground(fg);
        domainField.setCaretColor(fg);
        domainField.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));

        String[] modes = {
            "1. Main domain",
            "2. Wildcard domain",
            "3. Specific path",
            "4. Sensitive file extensions"
        };
        modeSelector = new JComboBox<>(modes);
        modeSelector.setBackground(new Color(50, 50, 50));
        modeSelector.setForeground(fg);

        fetchButton = new JButton("Fetch");
        fetchButton.setBackground(new Color(70, 130, 180));
        fetchButton.setForeground(Color.WHITE);
        fetchButton.setFocusPainted(false);

        topPanel.add(label);
        topPanel.add(domainField);
        topPanel.add(modeSelector);
        topPanel.add(fetchButton);

        resultArea = new JTextArea();
        resultArea.setBackground(new Color(40, 40, 40));
        resultArea.setForeground(fg);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Consolas", Font.PLAIN, 13));

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        add(panel);

        fetchButton.addActionListener(e -> fetchResults());
    }

    private void fetchResults() {
        String input = domainField.getText().trim();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter the domain or URL first!");
            return;
        }

        String url = buildUrl(input, modeSelector.getSelectedIndex());

        // ASCII art (escaped for Java string) + hackery message
        String header = """
              _      __          __            __   _______  ______
             | | /| / /__ ___ __/ /  ___ _____/ /__/ ___/ / / /  _/
             | |/ |/ / _ `/ // / _ \\/ _ `/ __/  '_/ (_ / /_/ // / \s
             |__/|__/\\_,_/\\_, /_.__/\\_,_/\\__/_/\\_\\\\___/\\____/___/ \s
                         /___/                                    \s
            """;

        String hackerMsg = """
            ▸ booting stealth probe...
            ▸ opening clandestine sockets to Wayback nodes...
            ▸ fingerprinting archive shards... stay frosty.
        
            """;

        // print header + hacker message + target url
        resultArea.setText(header + hackerMsg + "Target: " + url + "\n\n");

        // Start streaming thread
        new Thread(() -> {
            try {
                URL apiUrl = new URL(url);
                BufferedReader reader = new BufferedReader(new InputStreamReader(apiUrl.openStream(), StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null) {
                    final String currentLine = line;
                    SwingUtilities.invokeLater(() -> {
                        resultArea.append(currentLine + "\n");
                        resultArea.setCaretPosition(resultArea.getDocument().getLength());
                    });
                }
                reader.close();

                // When finished, re-enable button and show finish dialog & save confirmation
                SwingUtilities.invokeLater(() -> {
                    fetchButton.setEnabled(true);
                    resultArea.append("\n▸ done. stream terminated.\n");
                    // Ask whether user wants to save
                    int choice = JOptionPane.showConfirmDialog(
                        this,
                        "Probe complete. Save output to text file?",
                        "Selesai",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                    );
                    if (choice == JOptionPane.YES_OPTION) {
                        saveOutputToFile();
                    }
                });

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    fetchButton.setEnabled(true);
                    resultArea.append("\n❌ probe failed: " + ex.getMessage() + "\n");
                    JOptionPane.showMessageDialog(this, "Probe failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void saveOutputToFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save output as...");
        chooser.setSelectedFile(new File("wayback_output.txt"));
        int userSelection = chooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = chooser.getSelectedFile();
            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileToSave), StandardCharsets.UTF_8);
                 BufferedWriter bw = new BufferedWriter(writer)) {
                bw.write(resultArea.getText());
                bw.flush();
                JOptionPane.showMessageDialog(this, "Saved to: " + fileToSave.getAbsolutePath(), "Saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(this, "Failed to save file: " + ioe.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String buildUrl(String input, int modeIndex) {
        String encoded = input.replace("https://", "").replace("http://", "");
        return switch (modeIndex) {
            case 0 -> "https://web.archive.org/cdx/search/cdx?url=" + encoded + "/*&collapse=urlkey&output=text&fl=original";
            case 1 -> "https://web.archive.org/cdx/search/cdx?url=*." + encoded + "/*&collapse=urlkey&output=text&fl=original";
            case 2 -> "https://web.archive.org/cdx/search/cdx?url=https://" + encoded + "/*&collapse=urlkey&output=text&fl=original";
            case 3 -> "https://web.archive.org/cdx/search/cdx?url=*." + encoded +
                "/*&collapse=urlkey&output=text&fl=original&filter=original:.*\\.(xls|xml|xlsx|json|pdf|sql|doc|docx|pptx|txt|zip|tar\\.gz|tgz|bak|7z|rar|log|cache|secret|db|backup|yml|gz|git|config|csv|yaml|md|md5|exe|dll|bin|ini|bat|sh|tar|deb|rpm|iso|img|apk|msi|env|dmg|tmp|crt|pem|key|pub|asc)$";
            default -> "";
        };
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatMacDarkLaf.setup();
            new WaybackQueryTool().setVisible(true);
        });
    }
}
