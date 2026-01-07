package com.myproject.view;

import com.myproject.controller.MangaController;
import com.myproject.model.ChapterInfo;
import com.myproject.model.MangaInfo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class DetailViewUI extends JPanel {

    private final MainView mainView;
    private final MangaController controller;

    private JLabel titleLabel;
    private JLabel metaLabel;

    private DefaultListModel<String> chapterModel;
    private JList<String> chapterList;

    private MangaInfo currentMangaInfo;

    public DetailViewUI(MainView mainView, MangaController controller) {
        this.mainView = mainView;
        this.controller = controller;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(26, 26, 26));
        setBorder(new EmptyBorder(16, 18, 18, 18));

        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setBackground(new Color(26, 26, 26));
        header.setBorder(new EmptyBorder(0, 0, 12, 0));

        JButton back = new JButton("←");
        back.setFocusPainted(false);
        back.setBackground(new Color(50, 50, 50));
        back.setForeground(Color.WHITE);
        back.setFont(new Font("Segoe UI", Font.BOLD, 16));
        back.setPreferredSize(new Dimension(40, 36));
        back.setBorder(BorderFactory.createEmptyBorder());
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        back.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { back.setBackground(new Color(70, 70, 70)); }
            public void mouseExited(java.awt.event.MouseEvent evt) { back.setBackground(new Color(50, 50, 50)); }
        });
        back.addActionListener(e -> mainView.navigateTo("HOME"));

        titleLabel = new JLabel("Loading...");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));

        header.add(back, BorderLayout.WEST);
        header.add(titleLabel, BorderLayout.CENTER);

        return header;
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setBackground(new Color(26, 26, 26));

        metaLabel = new JLabel();
        metaLabel.setForeground(new Color(180, 180, 180));
        metaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        content.add(metaLabel, BorderLayout.NORTH);
        content.add(createChapterPanel(), BorderLayout.CENTER);

        return content;
    }

    private JScrollPane createChapterPanel() {
        chapterModel = new DefaultListModel<>();
        chapterList = new JList<>(chapterModel);

        chapterList.setBackground(new Color(34, 34, 34));
        chapterList.setForeground(Color.WHITE);
        chapterList.setSelectionBackground(new Color(55, 55, 55));
        chapterList.setFixedCellHeight(38);
        chapterList.setBorder(new EmptyBorder(6, 10, 6, 10));

        chapterList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            if (currentMangaInfo == null) return;
            int idx = chapterList.getSelectedIndex();
            if (idx < 0) return;
            ChapterInfo ch = currentMangaInfo.getChapters().get(idx);
            chapterList.clearSelection();
            mainView.showReader(ch.getId());
        });

        JScrollPane scroll = new JScrollPane(chapterList);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(26, 26, 26));
        scroll.getVerticalScrollBar().setUnitIncrement(14);

        return scroll;
    }

    public void loadManga(String mangaId) {
        titleLabel.setText("Loading...");
        metaLabel.setText("");
        chapterModel.clear();
        currentMangaInfo = null;

        new SwingWorker<MangaInfo, Void>() {
            protected MangaInfo doInBackground() { return controller.getMangaInfo(mangaId); }
            protected void done() {
                try { currentMangaInfo = get(); updateUI(currentMangaInfo); } catch (Exception ignored) {}
            }
        }.execute();
    }

    private void updateUI(MangaInfo info) {
        titleLabel.setText(info.getTitle());

        String author = info.getAuthors() != null ? String.join(", ", info.getAuthors()) : "-";
        String status = info.getStatus() != null ? info.getStatus() : "-";
        String genres = info.getGenres() != null ? String.join(", ", info.getGenres()) : "-";

        metaLabel.setText("<html><body style='width:520px'>Author: " + author +
                " &nbsp;•&nbsp; Status: " + status +
                " &nbsp;•&nbsp; Genres: " + genres +
                "</body></html>");

        List<ChapterInfo> chapters = info.getChapters();
        if (chapters != null) {
            for (int i = chapters.size() - 1; i >= 0; i--) {
                chapterModel.addElement("Chapter " + (i + 1));
            }
        }
    }
}
