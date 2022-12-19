package com.example;

import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.net.URL;
import java.io.InputStreamReader;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
import java.awt.Cursor;

import javax.swing.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import java.io.*;

import java.net.*;

public class frame extends JFrame implements ActionListener, ChangeListener, FocusListener, MouseListener {

    int index = 0;
    List<String> cryptop = getCryptoforp();
    List<String> crypto = getCryto();
    List<Double> next = getNext();
    List<ImageIcon> Marketimg = getMarketImg();
    List<ImageIcon> Tradingimg = getTradingImg();
    JFrame win = new JFrame("Crypto prices");
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
    JScrollPane scrollPane = new JScrollPane(rootPane);
    JPanel[] pnTrading = new JPanel[4];
    JLabel error = new JLabel("Erreur de connexion...");
    double balance = readBalance();
    String[] options = crypto.toArray(new String[0]);
    JComboBox<String> symbol = new JComboBox<String>(options);
    JLabel coin = new JLabel();
    JSlider leverage = new JSlider(1, 50, 1);
    JLabel valueLeverage = new JLabel("x1");
    JTextField amount = new JTextField();
    JButton Longit = new JButton("Long");
    JButton Shortit = new JButton("Short");
    JLabel lbalance = new JLabel("Balance : " + balance + " $");
    List<JComponent> components = getComp();
    JLabel pnl = new JLabel("PNL");
    ImageIcon cnc = new ImageIcon(new ImageIcon(getClass().getClassLoader().getResource("img/cancel.png")).getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH));
    Color gay = new Color(60, 63, 65);
    Color greenp = new Color(144, 238, 144);
    Color redp = new Color(255, 127, 127);
    List<List<JLabel>> order = new ArrayList<List<JLabel>>();
    int gridy = 0;

    public frame() {

        setMenu();
        setFrame();
        setMarketFrame();
        setTradingFrame();

        /* Visibility and pack for frame */
        win.pack();
        win.setVisible(true);

        for (int i = 0; i < crypto.size(); i++)
            coins[i].setText(next.get(i).toString() + " $");

        coin.setIcon(Tradingimg.get(symbol.getSelectedIndex()));
        coin.setText(coins[symbol.getSelectedIndex()].getText());
        readOrder();

        for (;;) {
            getMarket();
        }
    }

    public List<String> getCryptoforp() {
        List<String> list = new ArrayList<String>();
        list.add("bitcoin");
        list.add("ethereum");
        list.add("binance-coin");
        list.add("solana");
        list.add("fantom");
        list.add("WLKN");
        return list;
    }

    public List<String> getCryto() {
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
        for (int i = 0; i < cryptop.size(); i++) {
            try {
                list.add(Double.parseDouble(getPrices(cryptop.get(i))));
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return list;
    }

    public List<ImageIcon> getMarketImg() {
        List<ImageIcon> list = new ArrayList<ImageIcon>();
        for (int i = 0; i< crypto.size(); i++)
            list.add(new ImageIcon(new ImageIcon(getClass().getClassLoader().getResource("img/"+crypto.get(i)+".png")).getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH)));
        return list;
    }

    public List<ImageIcon> getTradingImg() {
        List<ImageIcon> list = new ArrayList<ImageIcon>();
        for (int i = 0; i < crypto.size(); i++)
            list.add(new ImageIcon(new ImageIcon(getClass().getClassLoader().getResource("img/"+crypto.get(i)+".png")).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
        return list;
    }

    public String getPrices(String str) throws Exception {
        URL financeUrl = new URL("https://api.coincap.io/v2/assets/"+str+"?api_key=185cee67-ae9a-46bd-bd1e-0375946363e9");
        if (str.equals("WLKN"))
            financeUrl = new URL("https://min-api.cryptocompare.com/data/price?fsym="+str+"&tsyms=USD&api_key=ef6c349317448a25c217cdab8e57e2c94bb8e053f17c51ca600efcf2ae862a1b");
        InputStreamReader reader = new InputStreamReader(financeUrl.openStream());
        JsonReader jsonReader = new JsonReader(reader);
        JsonParser parser = new JsonParser();
        if (str.equals("WLKN"))
            return parser.parse(jsonReader).getAsJsonObject().get("USD").getAsString();
        JsonElement rootElement = parser.parse(jsonReader);
        JsonObject rootObject = rootElement.getAsJsonObject();
        JsonObject data = rootObject.get("data").getAsJsonObject();
        double tmp = data.get("priceUsd").getAsDouble();
        if (tmp < 10)
            return (new BigDecimal(tmp).setScale(4, RoundingMode.HALF_UP)).toPlainString();
        return (new BigDecimal(tmp).setScale(2, RoundingMode.HALF_UP)).toPlainString();
    }

    public void getMarket() {
        for (;;) {
            for (int i = 0; i < crypto.size(); i++) {
                try {
                    next.set(i, Double.parseDouble(getPrices(cryptop.get(i))));
                    if (error.isVisible()) {
                        error.setVisible(false);
                        Bmarket.setVisible(true);
                        Btrading.setVisible(true);
                    }
                    error.setVisible(false);
                    if (next.get(i) < Double.parseDouble(coins[i].getText().replaceAll("[^0-9.]", ""))) {
                        coins[i].setForeground(Color.RED);
                        if (symbol.getSelectedItem().equals(crypto.get(i)))
                            coin.setForeground(redp);
                    }
                    else if (next.get(i) > Double.parseDouble(coins[i].getText().replaceAll("[^0-9.]", ""))) {
                        coins[i].setForeground(Color.GREEN);
                        if (symbol.getSelectedItem().equals(crypto.get(i)))
                            coin.setForeground(greenp);
                    }
                    coins[i].setText(next.get(i).toString() + " $");
                    coin.setText(coins[symbol.getSelectedIndex()].getText());
                    getOrder();
                    if (i == crypto.size() - 1)
                        Thread.sleep(5000);
                } catch (Exception a) {
                    error.setVisible(true);
                    Bmarket.setVisible(false);
                    Btrading.setVisible(false);
                    System.out.println("1 = " + a);
                }
            }
        }
    }

    public void getOrder() {
        double res = 0;
        int index = 0;
        for (int i = 0; i < order.size(); i++) {
            for (int j = 0; j < crypto.size(); j++) {
                if (order.get(i).get(0).getText().equals(symbol.getItemAt(j)))
                    index = j;
            }
            order.get(i).get(9).setText(coins[index].getText());
            order.get(i).get(9).setForeground((coins[index].getForeground() == Color.red) ? redp : greenp);
            double amount = Double.parseDouble(order.get(i).get(2).getText().replace("$", ""));
            int lev = Integer.parseInt(order.get(i).get(4).getText().replaceAll("x", ""));
            double price = Double.parseDouble(order.get(i).get(7).getText().replace("$", ""));
            double market = Double.parseDouble(order.get(i).get(9).getText().replace("$", ""));
            double prev = Double.parseDouble(order.get(i).get(11).getText().replaceAll("[+$]", ""));
            if (order.get(i).get(5).getText().equals("Long"))
                res = (market - price) * ((amount/price) * lev) * 1;
            else
                res = (price - market) * ((amount/price) * lev) * 1;
            res = Math.round(res*100);
            if (res > 0)
               order.get(i).get(11).setText("+" + Double.toString(res/100) + " $");
            else
                order.get(i).get(11).setText(Double.toString(res/100) + " $");
            if (prev < res)
                order.get(i).get(11).setForeground(greenp);
            else
                order.get(i).get(11).setForeground(redp);
        }
    }

    public void displayOrder(List<JLabel> list) {
        GridBagConstraints cstr = new GridBagConstraints();
        cstr.insets = new Insets(0, 0, 15, 5);
        for (int i = 0; i < list.size() - 1; i++) {
            cstr.gridx = i;
            cstr.gridy = gridy;
            if (i == list.size() - 2)
                cstr.insets = new Insets(0, 20, 15, 5);
            pnTrading[1].add(list.get(i), cstr);
        }
        gridy++;
    }

    public void reDisplay(int gridy, int rm, int read) {
        pnTrading[1].removeAll();
        pnTrading[1].setVisible(false);
        GridBagConstraints cstr = new GridBagConstraints();
        for (int j = 0; j < order.size(); j++) {
            for (int i = 0; i < order.get(j).size() - 1; i++) {
                cstr.insets = new Insets(0, 0, 15, 5);
                cstr.gridx = i;
                cstr.gridy = gridy;
                if (i == order.get(j).size() - 2) {
                    cstr.insets = new Insets(0, 20, 15, 5);
                }
                pnTrading[1].add(order.get(j).get(i), cstr);
            }
            gridy++;
        }
        if (read == 0)
            for (int i = rm; i < order.size(); i++) {
                order.get(i).get(order.get(i).size()-1).setText(Integer.toString((Integer.parseInt(order.get(i).get(order.get(i).size()-1).getText())-1)));
        }
        pnTrading[1].setVisible(true);
    }

    public double readBalance() {
        try {
            File file = new File("src/main/java/data/balance.txt");
            Scanner scan = new Scanner(file);
            balance = Double.parseDouble(scan.nextLine());
            scan.close();
            return balance;
        } catch (Exception e) {
            System.out.println("File" + e);
        }
        return 0;
    }

    public void writeBalance() {
        try (FileWriter fw = new FileWriter("src/main/java/data/balance.txt")) {
            fw.write(String.valueOf(balance));
          } catch (IOException e) {
            e.printStackTrace();
          }
    }

    public void readOrder() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/java/data/order.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                List<JLabel> list = new ArrayList<JLabel>();
                String[] tmp = line.split("/");
                JLabel[] third = new JLabel[3];
                JLabel[] info = new JLabel[5];
                info[0] = new JLabel(tmp[1]);
                info[1] = new JLabel(tmp[3]);
                info[2] = new JLabel(tmp[6]);
                info[3] = new JLabel(tmp[8]);
                info[4] = new JLabel(tmp[9]);
                for (int i = 0; i < info.length; i++)
                    info[i].setForeground(gay);
                third[0] = new JLabel(tmp[0]);
                third[1] = new JLabel(tmp[2]);
                third[2] = new JLabel(tmp[4]);
                for (int i = 0; i < third.length; i++) {
                    third[i].setForeground(Color.WHITE);
                    list.add(third[i]);
                    if (i != third.length - 1)
                        list.add(info[i]);
                }
                JLabel sl = new JLabel(tmp[5]);
                if (tmp[5].equals("Long")) 
                    sl.setForeground(greenp);
                else
                    sl.setForeground(redp);
                list.add(sl);
                list.add(info[2]);
                JLabel price = new JLabel(tmp[7]);
                price.setForeground(Color.WHITE);
                list.add(price);
                list.add(info[3]);
                JLabel market = new JLabel(tmp[9]);
                market.setForeground(greenp);
                list.add(market);
                list.add(info[4]);
                JLabel pnl = new JLabel(tmp[11]);
                pnl.setForeground(greenp);
                list.add(pnl);
                JLabel cancel = new JLabel(cnc);
                list.add(cancel);
                final JLabel indexof = new JLabel(Integer.toString(index));
                list.add(indexof);
                cancel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        setBalance(Integer.parseInt(indexof.getText()));
                        order.remove(Integer.parseInt(indexof.getText()));
                        reDisplay(0, Integer.parseInt(indexof.getText()), 0);
                        writeBalance();
                        writeOrder();
                        index--;
                    }
                });
                index++;
                cancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                for (int i = 0; i < list.size(); i++)
                    list.get(i).setFont(new Font("Arial", Font.BOLD, 14));;
                displayOrder(list);
                order.add(list);   
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeOrder() {
        try (BufferedWriter fw = new BufferedWriter(new FileWriter("src/main/java/data/order.txt", false))) {
            for (int i = 0; i < order.size(); i++) {
                for (int j = 0; j < order.get(i).size(); j++) {
                    fw.write(order.get(i).get(j).getText() + "/");
                }
                fw.newLine();
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
    }

    public void setBalance(int i) {
        double pl =  Double.parseDouble(order.get(i).get(order.get(i).size() - 3).getText().replaceAll("[$+]", ""));
        double amount = Double.parseDouble(order.get(i).get(2).getText().replace("$", ""));
        double res = Math.round((balance += (amount + pl))*100);
        lbalance.setText("Balance : " + res/100 + " $");
    }

    public void setOrder(String str) {
        List<JLabel> list = new ArrayList<JLabel>();
        JLabel[] third = new JLabel[3];
        JLabel[] info = new JLabel[5];
        info[0] = new JLabel("| Amount :");
        info[1] = new JLabel("| Leverage :");
        info[2] = new JLabel("| Position :");
        info[3] = new JLabel("| Market :");
        info[4] = new JLabel("| P&L :");
        for (int i = 0; i < info.length; i++)
            info[i].setForeground(gay);
        third[0] = new JLabel(symbol.getSelectedItem().toString());
        third[1] = new JLabel(amount.getText() + " $");
        third[2] = new JLabel(valueLeverage.getText());
        for (int i = 0; i < third.length; i++) {
            third[i].setForeground(Color.WHITE);
            list.add(third[i]);
            if (i != third.length - 1)
                list.add(info[i]);
        }
        JLabel sl = new JLabel(str);
        if (str.equals("Long")) 
            sl.setForeground(greenp);
        else
            sl.setForeground(redp);
        list.add(sl);
        list.add(info[2]);
        JLabel price = new JLabel(coin.getText());
        price.setForeground(Color.WHITE);
        list.add(price);
        list.add(info[3]);
        JLabel market = new JLabel(coin.getText());
        market.setForeground(greenp);
        list.add(market);
        list.add(info[4]);
        JLabel pnl = new JLabel("0.0");
        pnl.setForeground(greenp);
        list.add(pnl);
        JLabel cancel = new JLabel(cnc);
        list.add(cancel);
        final JLabel indexof = new JLabel(Integer.toString(index));
        list.add(indexof);
        cancel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setBalance(Integer.parseInt(indexof.getText()));
                order.remove(Integer.parseInt(indexof.getText()));
                reDisplay(0, Integer.parseInt(indexof.getText()), 0);
                writeBalance();
                writeOrder();
                index--;
            }
          });
        index++;
        cancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        for (int i = 0; i < list.size(); i++)
            list.get(i).setFont(new Font("Arial", Font.BOLD, 14));;
        displayOrder(list);
        order.add(list);
    }

    public List<JComponent> getComp() {
        List<JComponent> list = new ArrayList<JComponent>();
        list.add(symbol);
        list.add(coin);
        list.add(leverage);
        list.add(valueLeverage);
        list.add(amount);
        list.add(Longit);
        list.add(Shortit);
        list.add(lbalance);
        return list;
    }

    public void setMenu() {
        Menu.setLayout(new FlowLayout());
        Menu.setBackground(gay);
        Bmarket.setBackground(Color.WHITE);
        Btrading.setBackground(Color.WHITE);
        Bmarket.setForeground(Color.DARK_GRAY);
        Btrading.setForeground(Color.DARK_GRAY);
        Bmarket.setFocusPainted(false);
        Btrading.setFocusPainted(false);
        error.setFont(new Font("Arial", Font.BOLD, 14));
        error.setForeground(redp);
        error.setVisible(false);
        Menu.add(Bmarket);
        Menu.add(Btrading);
        Menu.add(error);
        Bmarket.addActionListener(this);
        Btrading.addActionListener(this);
        error.setFont(new Font("Arial", Font.BOLD, 14));

    }

    public void setFrame() {
        win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        win.setPreferredSize(new Dimension(1080, 720));
        win.setVisible(true);
        win.setLayout(new BorderLayout());
        win.setResizable(false);
        win.setBackground(Color.BLACK);
        win.add(Menu, BorderLayout.NORTH);
    }

    public void setMarketFrame() {
        /* set Panels for MarketFrame */
        MarketFrame.setLayout(new GridBagLayout());
        MarketFrame.setBackground(Color.BLACK);
        
            /* Icon and Prices of Cryptocurrency */
        for (int i = 0; i < crypto.size(); i++) {
            coins[i] = new JLabel();
            coins[i].setIcon(Marketimg.get(i));
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

    public void setTradingFrame() {
        /* set Panels for TradingFrame */
        TradingFrame.setLayout(new BorderLayout());
        TradingFrame.setBackground(Color.BLACK);
        TradingFrame.setVisible(false);
        pnTrading[0] = new JPanel();
        pnTrading[0].setBackground(Color.BLACK);
        pnTrading[0].setLayout(new BorderLayout());
        Border pn0border = BorderFactory.createMatteBorder(0, 0, 0, 3, Color.LIGHT_GRAY);
        pnTrading[0].setBorder(pn0border);

            /* Order panel */
        pnTrading[1] = new JPanel();
        pnTrading[1].setBackground(Color.BLACK);
        pnTrading[1].setLayout(new GridBagLayout());
        Border border = BorderFactory.createLineBorder(Color.BLACK, 5);
        GridBagConstraints c = new GridBagConstraints();
        JScrollPane scrollPane = new JScrollPane(pnTrading[1]);
        scrollPane.setBackground(Color.BLACK);
        scrollPane.setBorder(border);
        pnTrading[0].add(scrollPane, BorderLayout.CENTER);

            /* Resume panel */
        pnTrading[2] = new JPanel();
        pnTrading[2].setBackground(Color.BLACK);
        Border pn2border = BorderFactory.createMatteBorder(3, 0, 0, 0, Color.LIGHT_GRAY);
        pnTrading[2].setBorder(pn2border);
        pnl.setFont(new Font("Arial", Font.PLAIN, 20));
        pnl.setForeground(Color.WHITE);
        pnTrading[2].add(pnl);
        pnTrading[0].add(pnTrading[2], BorderLayout.SOUTH);

            /* Add pn[1&2] to TradingFrame */
        TradingFrame.add(pnTrading[0], BorderLayout.CENTER);

            /* placeOrder panel */
        pnTrading[3] = new JPanel(new GridBagLayout());
        pnTrading[3].setBackground(Color.BLACK);
        //symbol
        symbol.setForeground(Color.BLACK);
        symbol.setBackground(Color.WHITE);
        symbol.setFocusable(false);
        //coin
        coin.setIconTextGap(10);
        coin.setFont(new Font("Arial", Font.BOLD, 15));
        coin.setForeground(greenp);
        coin.setIcon(Tradingimg.get(symbol.getSelectedIndex()));
        coin.setText(coins[symbol.getSelectedIndex()].getText());
        //leverage
        leverage.setForeground(gay);
        leverage.setBackground(Color.WHITE);
        //valueLeverage
        valueLeverage.setForeground(Color.WHITE);
        //amount
        amount.setText("Amount in $");
        amount.setForeground(Color.BLACK);
        amount.setBackground(Color.WHITE);
        amount.setFont(new Font("Arial", Font.PLAIN, 14));
        amount.setMinimumSize(new Dimension(amount.getPreferredSize()));
        amount.setHorizontalAlignment(JTextField.CENTER);
        //long
        Longit.setForeground(Color.BLACK);
        Longit.setBackground(new Color(144, 238, 144));
        //short
        Shortit.setForeground(Color.BLACK);
        Shortit.setBackground(new Color(255, 127, 127));
        //balance
        lbalance.setFont(new Font("Arial", Font.BOLD, 16));
        lbalance.setForeground(Color.WHITE);

        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 2;
        c.weighty = 2;

        c.fill = GridBagConstraints.VERTICAL;
        for (int i = 0; i < components.size(); i++) {
            c.insets = new Insets(25, 20, 25, 20);
            if (components.get(i) == leverage)
                c.insets = new Insets(25, 20, 0, 20);
            if (components.get(i) == valueLeverage)
                c.insets = new Insets(0, 20, 25, 20);
            c.gridy = i;
            pnTrading[3].add(components.get(i), c);
        }

        TradingFrame.add(pnTrading[3], BorderLayout.EAST);

        symbol.addActionListener(this);
        leverage.addChangeListener(this);
        amount.addFocusListener(this);
        Longit.addActionListener(this);
        Shortit.addActionListener(this);
    }

    public void cancelOrder() {
        System.out.println(order);
        //order.remove(ABORT)
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == Bmarket) {
            if (!MarketFrame.isVisible()) {
                win.remove(TradingFrame);
                TradingFrame.setVisible(false);
                win.add(MarketFrame, BorderLayout.CENTER);
                MarketFrame.repaint();
                MarketFrame.setVisible(true);
            }
        }
        if (e.getSource() == Btrading) {
            if (!TradingFrame.isVisible()) {
                win.remove(MarketFrame);
                MarketFrame.setVisible(false);
                win.add(TradingFrame, BorderLayout.CENTER);
                TradingFrame.repaint();
                TradingFrame.setVisible(true);
            }
        }
        if (e.getSource() == symbol) {
            coin.setIcon(Tradingimg.get(symbol.getSelectedIndex()));
            coin.setText(coins[symbol.getSelectedIndex()].getText());
        }
        if (e.getSource() == Longit) {
            if (Double.parseDouble(amount.getText()) > balance)
                amount.setBackground(redp);
            else {
                amount.setBackground(Color.WHITE);
                balance = Math.round((balance - Double.parseDouble(amount.getText()))*100);
                balance = balance/100;
                setOrder("Long");
                writeOrder();
                lbalance.setText("Balance : " + balance + " $");
            }
        }
        if (e.getSource() == Shortit) {
            if (Double.parseDouble(amount.getText()) > balance)
                amount.setBackground(redp);
            else {
                amount.setBackground(Color.WHITE);
                balance = Math.round((balance - Double.parseDouble(amount.getText()))*100);
                balance = balance/100;
                setOrder("Short");
                writeOrder();
                lbalance.setText("Balance : " + balance + " $");
            }
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        valueLeverage.setText("x" + source.getValue());
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (amount.getText().equals("Amount in $"))
            amount.setText(null);
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (amount.getText().isEmpty() || Double.parseDouble(amount.getText()) < balance) {
            amount.setBackground(Color.WHITE);
            if (amount.getText().isEmpty())
                amount.setText("Amount in $");
        }
    }

    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}