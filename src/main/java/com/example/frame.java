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
import java.awt.GridLayout;
import java.awt.event.*;

import javax.swing.*;
import java.util.*;

public class frame extends JFrame implements ActionListener {

    List<String> crypto = getCrypto();
    ImageIcon btc = new ImageIcon(new ImageIcon(getClass().getClassLoader().getResource("img/btc.png")).getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT));
    ImageIcon eth = new ImageIcon(new ImageIcon(getClass().getClassLoader().getResource("img/eth.png")).getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT));
    JFrame win = new JFrame("Crypto prices");
    JPanel[] pn = new JPanel[2];
    JLabel[] icon = new JLabel[10];
    JLabel[] price = new JLabel[10];
    JButton refresh = new JButton("Refresh");

    public frame() {
        /* Set frame */
        win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        win.setPreferredSize(new Dimension(1080, 720));
        win.setVisible(true);
        win.setLayout(new BorderLayout());
        refresh.addActionListener(this);

        /* set Panel */
        pn[0] = new JPanel();
        pn[0].setBackground(Color.DARK_GRAY);
        pn[0].setLayout(new GridLayout(0, 2));

        /* Icon of Cryptocurrency */
        icon[0] = new JLabel();
        icon[0].setIcon(btc);
        icon[1] = new JLabel();
        icon[1].setIcon(eth);

        /* Prices for Cryptocurrecy */
        for (int i = 0; i < 2; i++) {
            price[i] = new JLabel();
            price[i].setForeground(Color.GREEN);
        }

        /* Adding Icon and Price to panel */
        pn[0].add(icon[0]);
        pn[0].add(price[0]);
        pn[0].add(icon[1]);
        pn[0].add(price[1]);
        pn[0].add(refresh);

        /* Adding panel to frame */
        win.add(pn[0]);

        /* Visibility and pack for frame */
        win.setVisible(true);
        win.pack();

        for (int i = 0; i < 2; i++) {
            try {
                price[i].setText(getPrices(crypto.get(i)));
            } catch (Exception e) {
                System.exit(1);
            }
        }
    }

    public String getPrices(String str) throws Exception {
        String url = "https://min-api.cryptocompare.com/data/price?fsym="+str+"&tsyms=USD,EUR,CNY,JPY,GBP";
        URL financeUrl = new URL(url);
        InputStreamReader reader = new InputStreamReader(financeUrl.openStream());
        JsonReader jsonReader = new JsonReader(reader);
        JsonParser parser = new JsonParser();
        JsonObject response = parser.parse(jsonReader).getAsJsonObject();
        return response.get("USD").getAsString();
    }

    public List<String> getCrypto() {
        List<String> list = new ArrayList<String>();
        list.add("BTC");
        list.add("ETH");
        return list;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == refresh) {
            for (int i = 0; i < 2; i++) {
                try {
                    System.out.println(getPrices(crypto.get(i)));
                    price[i].setText(getPrices(crypto.get(i)));
                } catch (Exception a) {
                    System.exit(1);
                }
            }
        }
    }

}
