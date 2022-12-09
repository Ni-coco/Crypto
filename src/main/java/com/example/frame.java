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
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.*;

import javax.swing.*;
import java.util.*;

public class frame extends JFrame implements ActionListener {

    List<String> crypto = getCrypto();
    List<Double> next = getNext();
    List<ImageIcon> img = getImg();
    JFrame win = new JFrame("Crypto prices");
    JPanel[] pn = new JPanel[4];
    JLabel[] icon = new JLabel[6];
    JLabel[] price = new JLabel[6];
    JButton refresh = new JButton("Refresh");

    public frame() {

        /* Set frame */
        win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        win.setPreferredSize(new Dimension(1080, 720));
        win.setVisible(true);
        win.setLayout(new BorderLayout());
        refresh.addActionListener(this);

        /* set Panels */
        for (int i = 0; i < pn.length; i++)
            pn[i] = new JPanel();
        pn[0].setLayout(new FlowLayout());
        pn[0].setBackground(Color.WHITE);
        pn[1].setLayout(new BorderLayout());
        pn[2].setBackground(Color.WHITE);
        pn[2].setLayout(new GridLayout(0, 1, 20, 35));
        pn[3].setBackground(Color.BLACK);
        pn[3].setLayout(new GridBagLayout());


        /* Icon and Prices of Cryptocurrency */
        for (int i = 0; i < crypto.size(); i++) {
            icon[i] = new JLabel();
            icon[i].setIcon(img.get(i));
            price[i] = new JLabel();
            price[i].setForeground(Color.GREEN);
        }

        /* Adding Icon and Price to panel */
        pn[0].add(refresh);
        GridBagConstraints c = new GridBagConstraints();
        for (int i = 0; i < crypto.size(); i++) {
            pn[2].add(icon[i]);
            c.gridy = i;
            pn[3].add(price[i], c);
        }

        /* Adding panel to frame */
        win.add(pn[0], BorderLayout.NORTH);
        win.add(pn[1], BorderLayout.CENTER);
        pn[1].add(pn[2], BorderLayout.WEST);
        pn[1].add(pn[3], BorderLayout.CENTER);

        /* Visibility and pack for frame */
        win.pack();
        win.setVisible(true);

        for (int i = 0; i < crypto.size(); i++)
            price[i].setText(next.get(i).toString());

        /*for (;;) {
            for (int i = 0; i < crypto.size(); i++) {
                try {
                    next.set(i, Double.parseDouble(getPrices(crypto.get(i))));
                    if (next.get(i) < Double.parseDouble(price[i].getText()))
                        price[i].setForeground(Color.RED);
                    else
                        price[i].setForeground(Color.GREEN);
                    price[i].setText(next.get(i).toString());
                    if (i == crypto.size() - 1)
                        Thread.sleep(10000);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }*/
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

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == refresh) {
            for (int i = 0; i < crypto.size(); i++) {
                try {
                    next.set(i, Double.parseDouble(getPrices(crypto.get(i))));
                    if (next.get(i) < Double.parseDouble(price[i].getText()))
                        price[i].setForeground(Color.RED);
                    else
                        price[i].setForeground(Color.GREEN);
                    price[i].setText(next.get(i).toString());
                } catch (Exception a) {
                    System.out.println(a);
                }
            }
        }
    }

}
