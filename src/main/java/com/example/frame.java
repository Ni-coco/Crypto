package com.example;

import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.JsonObject;
import java.io.InputStreamReader;
import java.net.URL;

import java.awt.Image;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.*;

import javax.swing.*;
import java.util.*;

public class frame extends JFrame implements ActionListener {

    List<String> crypto = getCrypto();
    List<Double> next = getNext();
    List<ImageIcon> img = getImg();
    JFrame win = new JFrame("Crypto prices"); //frame
    /* Related to Menu */
    JPanel Menu = new JPanel();
    JButton Bmarket = new JButton("Market");
    JButton Btrading = new JButton("Trading");
    /* Related to MarketFrame */
    JPanel MarketFrame = new JPanel();
    JPanel pnPrice = new JPanel();
    JLabel[] coins = new JLabel[6];
    /* Related to TradingFrame */
    JPanel TradingFrame = new JPanel();
    JPanel[] pnTrading = new JPanel[2];


    public frame() {

        setMenu();
        setFrame();
        //setMarketFrame();

        /* set Panels for TradingFrame */
        TradingFrame.setLayout(new BorderLayout());
        TradingFrame.setBackground(Color.BLACK);
        pnTrading[0] = new JPanel();
        pnTrading[0].setBackground(Color.MAGENTA);
        TradingFrame.add(pnTrading[0], BorderLayout.CENTER);
        TradingFrame.setVisible(false);

        setMarketFrame();

        /* Visibility and pack for frame */
        win.pack();
        win.setVisible(true);

        for (int i = 0; i < crypto.size(); i++)
            coins[i].setText(next.get(i).toString() + " $");

        for (;;) {
            if (MarketFrame.isVisible()) {
                getMarket();
            }
        }
    }

    public List<String> getCrypto() {
        List<String> list = new ArrayList<String>();
        list.add("BTC");
        list.add("ETH");
        list.add("BNB");
        list.add("SOL");
        list.add("FTM");
        list.add("WLKN");
        return list;
    }
    

    public List<Double> getNext() {
        List<Double> list = new ArrayList<Double>();
        for (int i = 0; i < crypto.size(); i++) {
            try {
                list.add(Double.parseDouble(getPrices(crypto.get(i))));
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return list;
    }

    public List<ImageIcon> getImg() {
        List<ImageIcon> list = new ArrayList<ImageIcon>();
        for (int i = 0; i< crypto.size(); i++)
            list.add(new ImageIcon(new ImageIcon(getClass().getClassLoader().getResource("img/"+crypto.get(i)+".png")).getImage().getScaledInstance(70, 70, Image.SCALE_DEFAULT)));
        return list;
    }

    public String getPrices(String str) throws Exception {
        URL financeUrl = new URL("https://min-api.cryptocompare.com/data/price?fsym="+str+"&tsyms=USD&api_key=ef6c349317448a25c217cdab8e57e2c94bb8e053f17c51ca600efcf2ae862a1b");
        InputStreamReader reader = new InputStreamReader(financeUrl.openStream());
        JsonReader jsonReader = new JsonReader(reader);
        JsonParser parser = new JsonParser();
        JsonObject response = parser.parse(jsonReader).getAsJsonObject();
        return response.get("USD").getAsString();
    }

    public void getMarket() {
        for (;;) {
            if (!MarketFrame.isVisible())
                break;
            for (int i = 0; i < crypto.size(); i++) {
                try {
                    next.set(i, Double.parseDouble(getPrices(crypto.get(i))));
                    String tmp = coins[i].getText();
                    if (next.get(i) < Double.parseDouble(tmp.replaceAll("[^0-9.]", "")))
                        coins[i].setForeground(Color.RED);
                    else
                        coins[i].setForeground(Color.GREEN);
                    coins[i].setText(next.get(i).toString() + " $");
                    if (i == crypto.size() - 1)
                        Thread.sleep(10000);
                } catch (Exception a) {
                    System.out.println("1 = " + a);
                }
            }
        }
    }

    public void setMenu() {
        Menu.setLayout(new FlowLayout());
        Menu.setBackground(Color.WHITE);
        Menu.add(Bmarket);
        Menu.add(Btrading);
        Bmarket.addActionListener(this);
        Btrading.addActionListener(this);
    }

    public void setFrame() {
        win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        win.setPreferredSize(new Dimension(1080, 720));
        win.setVisible(true);
        win.setLayout(new BorderLayout());
        win.add(Menu, BorderLayout.NORTH);
    }

    public void setMarketFrame() {
        /* set Panels for MarketFrame */
        MarketFrame.setLayout(new GridBagLayout());
        MarketFrame.setBackground(Color.BLACK);
        
            /* Icon and Prices of Cryptocurrency */
        for (int i = 0; i < crypto.size(); i++) {
            coins[i] = new JLabel();
            coins[i].setIcon(img.get(i));
            coins[i].setForeground(Color.GREEN);
            coins[i].setIconTextGap(20);
            coins[i].setFont(new Font("Arial", Font.BOLD, 20));
        }
        
            /* Adding Components to Panels */
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(50, 50, 100, 50);
        for (int i = 0; i < crypto.size(); i++) {
            c.gridx = i % 3;
            c.gridy = i / 3;
            MarketFrame.add(coins[i], c);
        }
        
            /* Adding panel to frame */
        win.add(MarketFrame, BorderLayout.CENTER);
        MarketFrame.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == Bmarket) {
            if (!MarketFrame.isVisible()) {
                win.remove(TradingFrame);
                TradingFrame.setVisible(false);
                win.add(MarketFrame, BorderLayout.CENTER);
                MarketFrame.repaint();
                MarketFrame.setVisible(true);
                System.out.println("Market");
            }
        }
        if (e.getSource() == Btrading) {
            if (!TradingFrame.isVisible()) {
                win.remove(MarketFrame);
                MarketFrame.setVisible(false);
                win.add(TradingFrame, BorderLayout.CENTER);
                TradingFrame.repaint();
                TradingFrame.setVisible(true);
                System.out.println("Trading");
            }
        }
    }
}
