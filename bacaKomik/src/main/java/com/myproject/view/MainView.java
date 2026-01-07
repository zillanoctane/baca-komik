package com.myproject.view;

import com.myproject.controller.MangaController;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame {

    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    private final MangaController controller;

    private final HomeViewUI homeView;
    private final DetailViewUI detailView;
    private final ReaderViewUI readerView;

    public MainView() {
        controller = new MangaController();

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        homeView = new HomeViewUI(this, controller);
        detailView = new DetailViewUI(this, controller);
        readerView = new ReaderViewUI(controller, this);

        mainPanel.add(homeView, "HOME");
        mainPanel.add(detailView, "DETAIL");
        mainPanel.add(readerView, "READER");

        setTitle("Baca Komik");
        setSize(720, 540); // ukuran default (HOME & DETAIL)
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(mainPanel);

        navigateTo("HOME");
        setVisible(true);
    }

    // ===================== NAVIGASI ===================== //
    public void navigateTo(String route) {
        cardLayout.show(mainPanel, route);

        // Atur ukuran window sesuai halaman
        switch (route) {
            case "READER" -> setSize(900, 720); // lebih besar untuk reader
            default -> setSize(720, 540);       // HOME & DETAIL
        }

        // Tetap di tengah layar
        setLocationRelativeTo(null);
    }

    public void showDetail(String mangaId) {
        detailView.loadManga(mangaId);
        navigateTo("DETAIL");
    }

    public void showReader(String chapterId) {
        readerView.openChapter(chapterId);
        navigateTo("READER");
    }
}
