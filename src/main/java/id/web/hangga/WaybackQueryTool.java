package id.web.hangga;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;

public class WaybackQueryTool extends JFrame {
    private final JTextField domainField;
    private final JTextArea resultArea;
    private final JComboBox<String> modeSelector;
    private final JButton fetchButton;
    private final JProgressBar progressBar;
    private final JLabel statusLabel;
    private boolean isFetching = false;

    public WaybackQueryTool() {
        setTitle("Wayback GUI");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Dark theme
        Color bg = new Color(30, 30, 30);
        Color fg = new Color(220, 220, 220);
        getContentPane().setBackground(bg);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(bg);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(bg);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel label = new JLabel("Domain/URL:");
        label.setForeground(fg);
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        topPanel.add(label, gbc);

        domainField = new JTextField(30);
        domainField.setBackground(new Color(50, 50, 50));
        domainField.setForeground(fg);
        domainField.setCaretColor(fg);
        domainField.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1;
        topPanel.add(domainField, gbc);

        String[] modes = {
            "1. Main domain",
            "2. Wildcard domain",
            "3. Specific path",
            "4. Sensitive file extensions"
//            ,
//            "5. With date range (2000-2010)",
//            "6. With date range (2011-2020)",
//            "7. With date range (2021-2024)"
        };
        modeSelector = new JComboBox<>(modes);
        modeSelector.setBackground(new Color(50, 50, 50));
        modeSelector.setForeground(fg);
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0;
        topPanel.add(modeSelector, gbc);

        fetchButton = new JButton("Start Probe");
        fetchButton.setBackground(new Color(70, 130, 180));
        fetchButton.setForeground(Color.WHITE);
        fetchButton.setFocusPainted(false);
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0;
        topPanel.add(fetchButton, gbc);

        // Status Panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(bg);

        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        progressBar.setIndeterminate(true);
        progressBar.setForeground(new Color(70, 130, 180));
        progressBar.setBackground(new Color(50, 50, 50));

        statusLabel = new JLabel("Ready");
        statusLabel.setForeground(new Color(150, 150, 150));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        statusPanel.add(progressBar, BorderLayout.CENTER);
        statusPanel.add(statusLabel, BorderLayout.SOUTH);

        // Result Area
        resultArea = new JTextArea();
        resultArea.setBackground(new Color(40, 40, 40));
        resultArea.setForeground(fg);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        resultArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        scrollPane.getVerticalScrollBar().setBackground(new Color(50, 50, 50));

        // Assembly
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(statusPanel, BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);
        add(panel);

        fetchButton.addActionListener(e -> toggleFetch());

        // Enter key support
        domainField.addActionListener(e -> toggleFetch());
    }

    private void toggleFetch() {
        if (isFetching) {
            stopFetch();
        } else {
            startFetch();
        }
    }

    private void startFetch() {
        String input = domainField.getText().trim();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter the domain or URL first!");
            return;
        }

        isFetching = true;
        fetchButton.setText("Stop Probe");
        fetchButton.setBackground(new Color(178, 34, 34));
        progressBar.setVisible(true);
        statusLabel.setText("Initializing stealth probe...");

        new Thread(this::fetchResults).start();
    }

    private void stopFetch() {
        isFetching = false;
        fetchButton.setText("Start Probe");
        fetchButton.setBackground(new Color(70, 130, 180));
        progressBar.setVisible(false);
        statusLabel.setText("Probe stopped by user");
        resultArea.append("\n▸ Probe terminated by user\n");
    }

    private String getRandomUserAgent() {
        String[] userAgents = {
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.1 Safari/605.1.15",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/119.0",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 17_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.1 Mobile/15E148 Safari/604.1",
            "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)",
            "Mozilla/5.0 (compatible; Bingbot/2.0; +http://www.bing.com/bingbot.htm)",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.111 Safari/537.36",
            "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/119.0",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/603.3.8 (KHTML, like Gecko) Version/10.1.2 Safari/603.3.8"
        };
        return userAgents[new Random().nextInt(userAgents.length)];
    }

    private InputStream getDecompressedStream(HttpURLConnection conn) throws IOException {
        String contentEncoding = conn.getContentEncoding();
        InputStream inputStream = conn.getInputStream();

        if (contentEncoding != null) {
            if (contentEncoding.equalsIgnoreCase("gzip")) {
                return new GZIPInputStream(inputStream);
            } else if (contentEncoding.equalsIgnoreCase("deflate")) {
                return new InflaterInputStream(inputStream, new Inflater(true));
            }
        }
        return inputStream;
    }

    private void fetchResults() {
        String input = domainField.getText().trim();
        int modeIndex = modeSelector.getSelectedIndex();
        String url = buildUrl(input, modeIndex);

        // Display header and info
        SwingUtilities.invokeLater(() -> {
            resultArea.setText(buildHeader());
            resultArea.append("▸ Target: " + input + "\n");
            resultArea.append("▸ Mode: " + modeSelector.getSelectedItem() + "\n");
            resultArea.append("▸ User-Agent: " + getRandomUserAgent().substring(0, 30) + "...\n");
            resultArea.append("▸ Start Time: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n");
            resultArea.append("▸ Stealth Mode: ACTIVE\n\n");
            resultArea.append("Initializing connection to Wayback Machine...\n\n");
        });

        try {
            // Initial random delay (2-8 seconds)
            int initialDelay = 2000 + new Random().nextInt(6000);
            SwingUtilities.invokeLater(() ->
                statusLabel.setText("Random delay: " + (initialDelay/1000) + "s before start"));
            Thread.sleep(initialDelay);

            for (int retryCount = 0; retryCount <= 3 && isFetching; retryCount++) {
                if (retryCount > 0) {
                    int backoffDelay = (int) (Math.pow(2, retryCount) * 1000) + new Random().nextInt(3000);
                    int finalRetryCount = retryCount;
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Retry " + finalRetryCount + " - Backoff delay: " + (backoffDelay/1000) + "s");
                        resultArea.append("▸ Retry attempt " + finalRetryCount + " after " + (backoffDelay/1000) + "s delay\n");
                    });
                    Thread.sleep(backoffDelay);
                }

                try {
                    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                    conn.setRequestProperty("User-Agent", getRandomUserAgent());
                    conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                    conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                    conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
                    conn.setRequestProperty("Connection", "keep-alive");
                    conn.setRequestProperty("DNT", "1");
                    conn.setConnectTimeout(45000);
                    conn.setReadTimeout(45000);

                    // Add random referer sometimes
                    if (new Random().nextBoolean()) {
                        conn.setRequestProperty("Referer", "https://web.archive.org/");
                    }

                    SwingUtilities.invokeLater(() ->
                        statusLabel.setText("Connecting to Wayback Machine..."));

                    int responseCode = conn.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        SwingUtilities.invokeLater(() ->
                            statusLabel.setText("Receiving and decompressing data..."));

                        // Use decompression method
                        InputStream decompressedStream = getDecompressedStream(conn);
                        BufferedReader reader = new BufferedReader(
                            new InputStreamReader(decompressedStream, StandardCharsets.UTF_8));

                        String line;
                        int lineCount = 0;
                        int totalLines = 0;

                        while ((line = reader.readLine()) != null && isFetching) {
                            final String currentLine = line;
                            SwingUtilities.invokeLater(() -> {
                                resultArea.append(currentLine + "\n");
                                // Auto-scroll
                                resultArea.setCaretPosition(resultArea.getDocument().getLength());
                            });

                            lineCount++;
                            totalLines++;

                            // Add small delay every 20-30 lines
                            if (lineCount >= 20 + new Random().nextInt(10)) {
                                int microDelay = 100 + new Random().nextInt(400);
                                Thread.sleep(microDelay);
                                lineCount = 0;

                                // Update status occasionally
                                if (totalLines % 100 == 0) {
                                    int finalTotalLines = totalLines;
                                    SwingUtilities.invokeLater(() ->
                                        statusLabel.setText("Received " + finalTotalLines + " lines..."));
                                }
                            }
                        }
                        reader.close();
                        decompressedStream.close();
                        conn.disconnect();

                        // Success - break retry loop
                        int finalTotalLines1 = totalLines;
                        SwingUtilities.invokeLater(() -> handleSuccess(finalTotalLines1));
                        return;

                    } else if (responseCode == 429) { // Too Many Requests
                        SwingUtilities.invokeLater(() -> {
                            resultArea.append("\n⚠️ Rate limit detected (429 Too Many Requests)\n");
                            resultArea.append("▸ Implementing countermeasures...\n");
                        });
                        // Continue to retry logic

                    } else {
                        throw new IOException("HTTP error: " + responseCode + " - " + conn.getResponseMessage());
                    }

                    conn.disconnect();

                } catch (SocketTimeoutException e) {
                    SwingUtilities.invokeLater(() ->
                        resultArea.append("⏱️ Timeout occurred, will retry...\n"));
                    // Continue to retry logic

                } catch (ConnectException e) {
                    SwingUtilities.invokeLater(() ->
                        resultArea.append("🔌 Connection failed, will retry...\n"));
                    // Continue to retry logic
                } catch (IOException e) {
                    // Handle decompression errors specifically
                    if (e.getMessage() != null && e.getMessage().contains("Not in GZIP format")) {
                        SwingUtilities.invokeLater(() ->
                            resultArea.append("🔧 Data format issue, retrying with different approach...\n"));
                    } else {
                        throw e;
                    }
                }
            }

            // If we get here, all retries failed
            if (isFetching) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("All retry attempts failed");
                    resultArea.append("\n❌ All retry attempts exhausted. Probe failed.\n");
                    resetUI();
                });
            }

        } catch (Exception ex) {
            if (isFetching) {
                SwingUtilities.invokeLater(() -> handleError(ex));
            }
        }
    }

    private void handleSuccess(int totalLines) {
        statusLabel.setText("Probe completed successfully");
        resultArea.append("\n✅ Probe completed at " +
            new SimpleDateFormat("HH:mm:ss").format(new Date()));
        resultArea.append("\n▸ Total results: " + totalLines + " lines");
        resultArea.append("\n▸ Stealth mode: MISSION ACCOMPLISHED\n");

        resetUI();

        // Ask about saving
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Probe completed successfully!\nFound " + totalLines + " results.\n\nSave output to file?",
            "Mission Accomplished",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );
        if (choice == JOptionPane.YES_OPTION) {
            saveOutputToFile();
        }
    }

    private void handleError(Exception ex) {
        statusLabel.setText("Probe failed");
        resultArea.append("\n❌ Probe failed: " + ex.getMessage() + "\n");
        resultArea.append("▸ Time: " + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "\n");

        resetUI();

        JOptionPane.showMessageDialog(this,
            "Probe failed:\n" + ex.getMessage() +
                "\n\nTry using a different mode or check your connection.",
            "Probe Failure",
            JOptionPane.ERROR_MESSAGE);
    }

    private void resetUI() {
        isFetching = false;
        SwingUtilities.invokeLater(() -> {
            fetchButton.setText("Start Probe");
            fetchButton.setBackground(new Color(70, 130, 180));
            progressBar.setVisible(false);
        });
    }

    private void saveOutputToFile() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save probe results...");
        chooser.setSelectedFile(new File(domainField.getText().replace(".","-") + timestamp + ".txt"));
//        chooser.setSelectedFile(new File("wayback_probe_" + timestamp + ".txt"));

        int userSelection = chooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = chooser.getSelectedFile();
            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileToSave), StandardCharsets.UTF_8);
                 BufferedWriter bw = new BufferedWriter(writer)) {
                bw.write(resultArea.getText().replace(buildHeader(),""));
                bw.flush();
                JOptionPane.showMessageDialog(this,
                    "Results saved to:\n" + fileToSave.getAbsolutePath(),
                    "Data Secured",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(this,
                    "Failed to save file: " + ioe.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String buildUrl(String input, int modeIndex) {
        String encoded = input.replace("https://", "").replace("http://", "");

        return switch (modeIndex) {
            case 0 -> "https://web.archive.org/cdx/search/cdx?url=" + encoded + "/*&collapse=urlkey&output=text&fl=original&filter=statuscode:200";
            case 1 -> "https://web.archive.org/cdx/search/cdx?url=*." + encoded + "/*&collapse=urlkey&output=text&fl=original&filter=statuscode:200";
            case 2 -> "https://web.archive.org/cdx/search/cdx?url=https://" + encoded + "/*&collapse=urlkey&output=text&fl=original&filter=statuscode:200";
            case 3 -> "https://web.archive.org/cdx/search/cdx?url=*." + encoded +
                "/*&collapse=urlkey&output=text&fl=original&filter=original:.*\\.(xls|xml|xlsx|json|pdf|sql|doc|docx|pptx|txt|zip|tar\\.gz|tgz|bak|7z|rar|log|cache|secret|db|backup|yml|gz|git|config|csv|yaml|md|md5|exe|dll|bin|ini|bat|sh|tar|deb|rpm|iso|img|apk|msi|env|dmg|tmp|crt|pem|key|pub|asc)$&filter=statuscode:200";
            case 4 -> "https://web.archive.org/cdx/search/cdx?url=" + encoded + "/*&collapse=urlkey&output=text&fl=original&from=2000&to=2010&filter=statuscode:200";
            case 5 -> "https://web.archive.org/cdx/search/cdx?url=" + encoded + "/*&collapse=urlkey&output=text&fl=original&from=2011&to=2020&filter=statuscode:200";
            case 6 -> "https://web.archive.org/cdx/search/cdx?url=" + encoded + "/*&collapse=urlkey&output=text&fl=original&from=2021&to=2024&filter=statuscode:200";
            default -> "https://web.archive.org/cdx/search/cdx?url=" + encoded + "/*&collapse=urlkey&output=text&fl=original&filter=statuscode:200";
        };
    }

    private String buildHeader() {
        return """
              _      __          __            __   _______  ______
             | | /| / /__ ___ __/ /  ___ _____/ /__/ ___/ / / /  _/
             | |/ |/ / _ `/ // / _ \\/ _ `/ __/  '_/ (_ / /_/ // / \s
             |__/|__/\\_,_/\\_, /_.__/\\_,_/\\__/_/\\_\\\\___/\\____/___/ \s
                         /___/                                    \s
                         
            ▸ STEALTH MODE ACTIVATED
            ▸ Anti-rate-limit protocols: ENABLED
            ▸ User-Agent rotation: ACTIVE
            ▸ Randomized delays: OPERATIONAL
            ▸ Retry mechanisms: ARMED
            ▸ GZIP decompression: ACTIVE
            
            """;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(new FlatMacDarkLaf());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new WaybackQueryTool().setVisible(true);
        });
    }
}