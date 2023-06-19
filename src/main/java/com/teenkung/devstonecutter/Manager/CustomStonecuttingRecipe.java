package com.teenkung.devstonecutter.Manager;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomStonecuttingRecipe {

    private final String input;
    private final ArrayList<String> result = new ArrayList<>();
    private final HashMap<String, Integer> resultAmount = new HashMap<>();

    public CustomStonecuttingRecipe(String input) {
        this.input = input;
    }

    public void addResult(String result, int amount) {
        if (!this.result.contains(result)) {
            this.result.add(result);
        }
        resultAmount.put(result, amount);
    }

    public int getResultAmount(String result) {
        return resultAmount.getOrDefault(result, 0);
    }

    @SuppressWarnings("unused")
    public String getInput() { return input; }
    public ArrayList<String> getResult() { return result; }

}
