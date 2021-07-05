package Main.UI;

import Main.Algorithm.CuttingPlan;
import Main.UI.Panels.InputPanel;
import Main.UI.Panels.OutputPanel;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final InputPanel inputPanel;
    private OutputPanel outputPanel;
    private final Dimension frameSize = new Dimension(750, 500);

    public MainFrame() {
        setTitle("Cutting Plan V2.0 Made by Mingeon Sung");
        setPreferredSize(frameSize);

        inputPanel = new InputPanel(this);

        add(inputPanel);
        setContentPane(inputPanel);


        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(this.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void changeToInputPanel() {
        remove(outputPanel);
        setContentPane(inputPanel);
        getContentPane().revalidate();
        getContentPane().repaint();
        pack();
    }

    public void changeToOutputPanel(CuttingPlan[] cuttingPlans) {
        outputPanel = new OutputPanel(this, cuttingPlans);
        add(outputPanel);
        setContentPane(outputPanel);
        getContentPane().revalidate();
        getContentPane().repaint();
        pack();
    }

}
