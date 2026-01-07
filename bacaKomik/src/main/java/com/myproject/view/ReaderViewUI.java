package com.myproject.view;

import com.myproject.controller.MangaController;
import com.myproject.model.ChapterPage;
import com.myproject.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ReaderViewUI extends JPanel {

    private final MangaController controller;
    private final MainView mainView;

    private JLabel imageLabel;
    private JLabel pageInfo;

    private JButton backBtn, prevBtn, nextBtn, zoomInBtn, zoomOutBtn, goPageBtn;
    private JToggleButton autoPlayToggle;
    private JComboBox<String> intervalBox;
    private JTextField goPageField;

    private Timer autoPlayTimer;

    private List<ChapterPage> pages;
    private int index = 0;
    private boolean hoverImage = false;
    private double zoomFactor = 1.0;

    private boolean isLoading = false;
    private boolean hasError = false;

    public ReaderViewUI(MangaController controller, MainView mainView) {
        this.controller = controller;
        this.mainView = mainView;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        backBtn = iconBtn("←");
        prevBtn = iconBtn("<");
        nextBtn = iconBtn(">");
        zoomInBtn = iconBtn("+");
        zoomOutBtn = iconBtn("-");

        goPageField = new JTextField();
        goPageField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        goPageField.setBackground(new Color(36, 36, 36));
        goPageField.setForeground(Color.WHITE);
        goPageField.setCaretColor(Color.WHITE);
        goPageField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
        goPageField.setAlignmentX(Component.LEFT_ALIGNMENT);

        goPageBtn = wideBtn("Go");

        backBtn.addActionListener(e -> mainView.navigateTo("DETAIL"));
        prevBtn.addActionListener(e -> prev());
        nextBtn.addActionListener(e -> next());
        zoomInBtn.addActionListener(e -> { zoomFactor *= 1.2; showPage(); });
        zoomOutBtn.addActionListener(e -> { zoomFactor /= 1.2; showPage(); });
        goPageBtn.addActionListener(e -> goToPage());

        autoPlayToggle = new JToggleButton("Auto Play");
        autoPlayToggle.setFocusPainted(false);
        autoPlayToggle.setForeground(Color.WHITE);
        autoPlayToggle.setBackground(new Color(36, 36, 36));
        autoPlayToggle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        autoPlayToggle.setAlignmentX(Component.LEFT_ALIGNMENT);
        autoPlayToggle.addActionListener(e -> toggleAutoPlay());

        intervalBox = new JComboBox<>(new String[]{"5 sec", "10 sec", "15 sec", "30 sec", "1 min"});
        intervalBox.setBackground(new Color(36, 36, 36));
        intervalBox.setForeground(Color.WHITE);
        intervalBox.setPreferredSize(new Dimension(140, 28));
        intervalBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        intervalBox.setFocusable(false);
        intervalBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        intervalBox.addActionListener(e -> restartAutoPlay());

        pageInfo = new JLabel("", SwingConstants.CENTER);
        pageInfo.setForeground(Color.LIGHT_GRAY);
        pageInfo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBackground(new Color(30, 30, 30));
        left.setPreferredSize(new Dimension(160, 0));
        left.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));

        left.add(backBtn);
        left.add(Box.createVerticalStrut(8));
        left.add(prevBtn);
        left.add(Box.createVerticalStrut(8));
        left.add(nextBtn);
        left.add(Box.createVerticalStrut(8));
        left.add(zoomInBtn);
        left.add(Box.createVerticalStrut(4));
        left.add(zoomOutBtn);
        left.add(Box.createVerticalStrut(8));
        left.add(goPageField);
        left.add(Box.createVerticalStrut(4));
        left.add(goPageBtn);
        left.add(Box.createVerticalStrut(12));
        left.add(autoPlayToggle);
        left.add(Box.createVerticalStrut(6));
        left.add(intervalBox);
        left.add(Box.createVerticalStrut(12));
        left.add(pageInfo);

        imageLabel = new JLabel("", SwingConstants.CENTER);
        imageLabel.setOpaque(true);
        imageLabel.setBackground(Color.BLACK);

        imageLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { next(); }
            public void mouseEntered(MouseEvent e) { hoverImage = true; }
            public void mouseExited(MouseEvent e) { hoverImage = false; }
        });

        JScrollPane scroll = new JScrollPane(imageLabel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.BLACK);
        scroll.getVerticalScrollBar().setUnitIncrement(18);

        add(left, BorderLayout.WEST);
        add(scroll, BorderLayout.CENTER);

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) next();
                if (e.getKeyCode() == KeyEvent.VK_LEFT) prev();
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) mainView.navigateTo("DETAIL");
            }
        });
    }

    private JButton iconBtn(String symbol) {
        JButton b = new JButton(symbol);
        b.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b.setFocusPainted(false);
        b.setBackground(new Color(50, 50, 50));
        b.setForeground(Color.WHITE);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        return b;
    }

    private JButton wideBtn(String t) {
        JButton b = new JButton(t);
        b.setFocusPainted(false);
        b.setBackground(new Color(50, 50, 50));
        b.setForeground(Color.WHITE);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        return b;
    }

    public void openChapter(String chapterId) {
        pages = null;
        index = 0;
        zoomFactor = 1.0;
        imageLabel.setIcon(null);
        imageLabel.setText("Loading...");
        imageLabel.setForeground(Color.WHITE);
        pageInfo.setText("Loading...");
        stopAutoPlay();
        autoPlayToggle.setSelected(false);
        isLoading = true;
        hasError = false;

        requestFocusInWindow();

        new SwingWorker<List<ChapterPage>, Void>() {
            protected List<ChapterPage> doInBackground() { return controller.readChapter(chapterId); }
            protected void done() {
                try {
                    pages = get();
                    index = 0;
                    showPage();
                } catch (Exception e) {
                    pages = null;
                    imageLabel.setText("❌ Failed to load chapter");
                    imageLabel.setForeground(Color.RED);
                }
            }
        }.execute();
    }

    private void showPage() {
        if (pages == null || pages.isEmpty()) return;

        pageInfo.setText((index + 1) + " / " + pages.size());
        prevBtn.setEnabled(index > 0);
        nextBtn.setEnabled(index < pages.size() - 1);

        isLoading = true;
        hasError = false;
        imageLabel.setText("Loading...");
        imageLabel.setIcon(null);
        imageLabel.setForeground(Color.WHITE);

        new SwingWorker<ImageIcon, Void>() {
            protected ImageIcon doInBackground() {
                try {
                    byte[] img = controller.getImage(pages.get(index).getImg());
                    Image raw = Utils.bytesToImage(img);
                    if (raw == null) throw new Exception("Image data null");

                    int maxH = getHeight() - 40;
                    int maxW = getWidth() - 160;

                    Image scaled = Utils.scaleFit(raw, maxW, maxH);
                    int w = (int) (scaled.getWidth(null) * zoomFactor);
                    int h = (int) (scaled.getHeight(null) * zoomFactor);
                    Image zoomed = scaled.getScaledInstance(w, h, Image.SCALE_SMOOTH);
                    return new ImageIcon(zoomed);
                } catch (Exception e) {
                    hasError = true;
                    return null;
                }
            }

            protected void done() {
                isLoading = false;
                try {
                    ImageIcon icon = get();
                    if (hasError || icon == null) {
                        imageLabel.setIcon(null);
                        imageLabel.setText("❌ Failed to load image");
                        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        imageLabel.setForeground(Color.RED);
                    } else {
                        imageLabel.setText(null);
                        imageLabel.setIcon(icon);
                    }
                } catch (Exception e) {
                    imageLabel.setIcon(null);
                    imageLabel.setText("❌ Failed to load image");
                    imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    imageLabel.setForeground(Color.RED);
                }
            }
        }.execute();
    }

    private void next() { if (!isLoading && pages != null && index < pages.size() - 1) { index++; showPage(); } }
    private void prev() { if (!isLoading && pages != null && index > 0) { index--; showPage(); } }
    private void goToPage() {
        if (isLoading || pages == null) return;
        try {
            int pageNum = Integer.parseInt(goPageField.getText().trim());
            if (pageNum >= 1 && pageNum <= pages.size()) { index = pageNum - 1; showPage(); }
        } catch (NumberFormatException ignored) {}
    }

    private void toggleAutoPlay() { if (autoPlayToggle.isSelected()) startAutoPlay(); else stopAutoPlay(); }
    private void startAutoPlay() { stopAutoPlay(); autoPlayTimer = new Timer(getIntervalMs(), e -> { if (!hoverImage) next(); }); autoPlayTimer.start(); }
    private void stopAutoPlay() { if (autoPlayTimer != null) { autoPlayTimer.stop(); autoPlayTimer = null; } }
    private void restartAutoPlay() { if (autoPlayToggle.isSelected()) startAutoPlay(); }
    private int getIntervalMs() { return switch (intervalBox.getSelectedIndex()) { case 0 -> 5000; case 1 -> 10000; case 2 -> 15000; case 3 -> 30000; default -> 60000; }; }
}
