package com.myproject.view;

import com.myproject.controller.MangaController;
import com.myproject.model.MangaSummary;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HomeViewUI extends JPanel {

    private final MainView mainView;
    private final MangaController controller;

    private JPanel listPanel;
    private JTextField searchField;
    private JComboBox<String> genreBox;
    private JButton loadMoreBtn;
    private JLabel loadingLabel;
    private JLabel emptyLabel;

    private int page = 1;
    private boolean loading = false;

    private final Map<String, String> genreMap = new LinkedHashMap<>();

    public HomeViewUI(MainView mainView, MangaController controller) {
        this.mainView = mainView;
        this.controller = controller;

        initGenre();
        initUI();
        fetch(true);
    }

    private void initGenre() {
        genreMap.put("All", "all");
        genreMap.put("Action", "action");
        genreMap.put("Adventure", "adventure");
        genreMap.put("Comedy", "comedy");
        genreMap.put("Drama", "drama");
        genreMap.put("Fantasy", "fantasy");
        genreMap.put("Romance", "romance");
        genreMap.put("Slice of Life", "slice-of-life");
        genreMap.put("School", "school");
        genreMap.put("Supernatural", "supernatural");
        genreMap.put("Mystery", "mystery");
        genreMap.put("Horror", "horror");
        genreMap.put("Psychological", "psychological");
        genreMap.put("Sci-Fi", "sci-fi");
        genreMap.put("Sports", "sports");
        genreMap.put("Martial Arts", "martial-arts");
        genreMap.put("Mecha", "mecha");
        genreMap.put("Historical", "historical");
        genreMap.put("Isekai", "isekai");
        genreMap.put("Seinen", "seinen");
        genreMap.put("Shounen", "shounen");
        genreMap.put("Shoujo", "shoujo");
        genreMap.put("Josei", "josei");
        genreMap.put("Ecchi", "ecchi");
        genreMap.put("Harem", "harem");
        genreMap.put("Music", "music");
        genreMap.put("Tragedy", "tragedy");
        genreMap.put("Vampire", "vampire");
        genreMap.put("Magic", "magic");
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(24, 24, 24));
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setBackground(new Color(24, 24, 24));
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JLabel title = new JLabel("Manga Santuy");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        controls.setBackground(new Color(24, 24, 24));

        searchField = input(160);
        genreBox = combo(genreMap.keySet().toArray(new String[0]));

        JButton searchBtn = button("Go");
        JButton clearBtn = button("Clear");

        searchBtn.addActionListener(e -> fetch(true));
        clearBtn.addActionListener(e -> clearFilter());

        controls.add(searchField);
        controls.add(genreBox);
        controls.add(searchBtn);
        controls.add(clearBtn);

        header.add(title, BorderLayout.WEST);
        header.add(controls, BorderLayout.EAST);
        return header;
    }

    private JScrollPane createContent() {
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(new Color(24, 24, 24));

        loadingLabel = new JLabel("Loading manga...");
        loadingLabel.setForeground(Color.GRAY);
        loadingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadingLabel.setVisible(false);

        emptyLabel = new JLabel("😢 Yah, komik kosong");
        emptyLabel.setForeground(Color.GRAY);
        emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyLabel.setVisible(false);

        listPanel.add(loadingLabel);
        listPanel.add(Box.createVerticalStrut(12));
        listPanel.add(emptyLabel);

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(24, 24, 24));
        scroll.getVerticalScrollBar().setUnitIncrement(14);

        return scroll;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel();
        footer.setBackground(new Color(24, 24, 24));
        footer.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        loadMoreBtn = button("Load more");
        loadMoreBtn.addActionListener(e -> {
            if (!loading) {
                page++;
                fetch(false);
            }
        });

        footer.add(loadMoreBtn);
        return footer;
    }

    private void fetch(boolean reset) {
        if (loading) return;
        loading = true;

        if (reset) {
            page = 1;
            listPanel.removeAll();
            listPanel.add(loadingLabel);
            listPanel.add(Box.createVerticalStrut(12));
            listPanel.add(emptyLabel);
        }

        loadingLabel.setVisible(true);
        emptyLabel.setVisible(false);
        loadMoreBtn.setEnabled(false);

        new SwingWorker<List<MangaSummary>, Void>() {
            protected List<MangaSummary> doInBackground() {
                String q = searchField.getText().trim();
                String genre = genreMap.get(genreBox.getSelectedItem());

                if (!q.isEmpty())
                    return controller.search(q, page).getResults();

                if (!"all".equals(genre))
                    return controller.getMangaByGenre(genre, page).getResults();

                return controller.getLatestManga(page).getResults();
            }

            protected void done() {
                try {
                    List<MangaSummary> data = get();

                    if (data.isEmpty() && page == 1) {
                        emptyLabel.setVisible(true);
                    } else {
                        for (MangaSummary m : data) {
                            listPanel.add(item(m));
                            listPanel.add(Box.createVerticalStrut(8));
                        }
                    }

                    loadMoreBtn.setVisible(!data.isEmpty());
                } catch (Exception ignored) {}

                loadingLabel.setVisible(false);
                loading = false;
                loadMoreBtn.setEnabled(true);
                revalidate();
                repaint();
            }
        }.execute();
    }

    private void clearFilter() {
        searchField.setText("");
        genreBox.setSelectedIndex(0);
        fetch(true);
    }

    private JPanel item(MangaSummary manga) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(34, 34, 34));
        p.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel t = new JLabel(manga.getTitle());
        t.setForeground(Color.WHITE);

        p.add(t, BorderLayout.CENTER);

        p.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                mainView.showDetail(manga.getId());
            }
            public void mouseEntered(java.awt.event.MouseEvent e) {
                p.setBackground(new Color(42, 42, 42));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                p.setBackground(new Color(34, 34, 34));
            }
        });

        return p;
    }

    private JTextField input(int w) {
        JTextField f = new JTextField();
        f.setPreferredSize(new Dimension(w, 30));
        f.setBackground(new Color(36, 36, 36));
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return f;
    }

    private JComboBox<String> combo(String[] items) {
        JComboBox<String> c = new JComboBox<>(items);
        c.setBackground(new Color(36, 36, 36));
        c.setForeground(Color.WHITE);
        c.setPreferredSize(new Dimension(140, 30));
        return c;
    }

    private JButton button(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(50, 50, 50));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        return b;
    }
}
