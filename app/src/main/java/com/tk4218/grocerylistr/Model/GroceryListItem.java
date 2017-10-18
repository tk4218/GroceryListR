package com.tk4218.grocerylistr.Model;

/**
 * Created by Tk4218 on 10/14/2017.
 */

public class GroceryListItem {

    private int mGroceryListItemKey;
    private Ingredient mIngredient;
    private double mIngredientAmount;
    private String mIngredientUnit;
    private boolean mAddedToCart;

    public GroceryListItem(){

    }

    public void setIngredientAmount(double amount){
        mIngredientAmount = amount;
    }

    public double getIngredientAmount(){
        return mIngredientAmount;
    }
    public String getFormattedIngredientAmount(){
        return toFraction(mIngredientAmount, 1000);
    }

    private String toFraction(double d, int factor) {
        StringBuilder sb = new StringBuilder();
        if (d < 0) {
            sb.append('-');
            d = -d;
        }
        long l = (long) d;
        if (l != 0) sb.append(l);
        d -= l;
        double error = Math.abs(d);
        int bestDenominator = 1;
        for(int i=2;i<=factor;i++) {
            double error2 = Math.abs(d - (double) Math.round(d * i) / i);
            if (error2 < error) {
                error = error2;
                bestDenominator = i;
            }
        }
        if (bestDenominator > 1)
            sb.append(' ').append(Math.round(d * bestDenominator)).append('/') .append(bestDenominator);
        return sb.toString();
    }

}
