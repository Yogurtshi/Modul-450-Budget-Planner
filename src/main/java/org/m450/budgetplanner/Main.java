package org.m450.budgetplanner;

import org.m450.budgetplanner.ui.ConsoleMenu;

public class Main {
    public static void main(String[] args) {
        ConsoleMenu menu = new ConsoleMenu("budget_data.json");
        menu.run();
    }
}