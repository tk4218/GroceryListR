package com.tk4218.grocerylistr;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.tk4218.grocerylistr.Database.JSONResult;
import com.tk4218.grocerylistr.Database.QueryBuilder;

public class Ingredient extends BaseObservable{
    private QueryBuilder mQb = new QueryBuilder();

    private String mIngredientKey;
    private String mIngredientName;
    private String mIngredientType;
    private static int mShelfLife;
    private int mShelfLifeInterval;
    private double mIngredientAmount;
    private static String mIngredientUnit;
    private String mPreparation1;
    private String mPreparation2;

    private static int mIngredientUnitPosition;
    private static int mShelfLifeIntervalPosition;
    private static int mIngredientTypePosition;
    private static String[] mIngredientUnits;
    private static String[] mIngredientTypes;

    public Ingredient(){

    }

    public Ingredient(Context context){
        mIngredientUnits = context.getResources().getStringArray(R.array.measurements);
        mIngredientTypes = context.getResources().getStringArray(R.array.ingredient_type);
    }

    public Ingredient(int ingredientKey){
        JSONResult ingredient = mQb.getIngredient(0);
        if(ingredient.getCount() > 0){
            setIngredientKey("");
            setIngredientName(ingredient.getString("IngredientName"));
            setIngredientType(ingredient.getString("IngredientType"));
            setShelfLife(ingredient.getInt("ShelfLife"));
        }
    }

    public Ingredient(String ingredientName){
        JSONResult ingredient = mQb.getIngredientByName(ingredientName);
        if(ingredient.getCount() > 0){
            setIngredientKey(ingredient.getString("IngredientKey"));
            setIngredientName(ingredient.getString("IngredientName"));
            setIngredientType(ingredient.getString("IngredientType"));
            setShelfLife(ingredient.getInt("ShelfLife"));
        }
    }

    public Ingredient(int recipeKey, String ingredientName){
        JSONResult ingredient = mQb.getRecipeIngredient(recipeKey, ingredientName);
        if(ingredient.getCount() > 0){
            setIngredientKey(ingredient.getString("IngredientKey"));
            setIngredientName(ingredient.getString("IngredientName"));
            setIngredientType(ingredient.getString("IngredientType"));
            setShelfLife(ingredient.getInt("ShelfLife"));
            setIngredientAmount(ingredient.getDouble("IngredientAmount"));
            setIngredientUnit(ingredient.getString("IngredientUnit"));
        }
    }

    public Ingredient(String ingredientKey, String ingredientName, String ingredientType, int shelfLife, double ingredientAmount, String ingredientUnit, String preparation1, String preparation2) {
        setIngredientKey(ingredientKey);
        setIngredientName(ingredientName);
        setIngredientType(ingredientType);
        setShelfLife(shelfLife);
        setIngredientAmount(ingredientAmount);
        setIngredientUnit(ingredientUnit);
        setPreparation1(preparation1);
        setPreparation2(preparation2);
    }


    public void setIngredientKey(String ingredientKey){
        mIngredientKey = ingredientKey;
    }
    public String getIngredientKey(){
        return mIngredientKey;
    }

    @Bindable
    public String getIngredientName(){
        return mIngredientName;
    }
    public void setIngredientName(String ingredientName){
        if(!ingredientName.equals(mIngredientName)){
            mIngredientName = ingredientName;
            notifyPropertyChanged(BR.ingredientName);
        }
    }

    @Bindable
    public String getIngredientType(){
        return mIngredientType;
    }
    public void setIngredientType(String ingredientType){
        if(!ingredientType.equals(mIngredientType)) {
            mIngredientType = ingredientType;
            notifyPropertyChanged(BR.ingredientType);
        }
    }

    @Bindable
    public int getIngredientTypePosition() {
        return mIngredientTypePosition;
    }
    public void setIngredientTypePosition(int position) {
        mIngredientTypePosition = position;
        //setIngredientType(mIngredientTypes[position]);
        notifyPropertyChanged(BR.ingredientTypePosition);
    }

    @Bindable
    public int getShelfLife(){
        return mShelfLife;
    }
    public void setShelfLife(int shelfLife){
        if(mShelfLife != shelfLife) {
            mShelfLife = shelfLife;
            notifyPropertyChanged(BR.shelfLife);
        }
    }

    @Bindable
    public void setShelfLifeText(String shelfLifeText){
        if (shelfLifeText != null && !shelfLifeText.isEmpty() && Integer.parseInt(shelfLifeText) != mShelfLife) {
            mShelfLife = Integer.parseInt(shelfLifeText);
            notifyPropertyChanged(BR.shelfLifeText);
        }
    }
    public String getShelfLifeText() {
        return Integer.toString(mShelfLife);
    }

    public int getShelfLifeInterval(){
        return mShelfLifeInterval;
    }
    public void setShelfLifeInterval(int interval){
        mShelfLifeInterval = interval;
    }

    @Exclude
    @Bindable
    public int getShelfLifeIntervalPosition(){
        return mShelfLifeIntervalPosition;
    }
    public void setShelfLifeIntervalPosition(int position){
        mShelfLifeIntervalPosition = position;
        switch(position){
            case 0:
                mShelfLifeInterval = 1;
                break;
            case 1:
                mShelfLifeInterval = 7;
                break;
            case 2:
                mShelfLifeInterval = 30;
                break;
        }
        notifyPropertyChanged(BR.shelfLifeIntervalPosition);
    }

    @Bindable
    public double getIngredientAmount(){
        return  mIngredientAmount;
    }
    public void setIngredientAmount(double ingredientAmount){
        if(mIngredientAmount != ingredientAmount) {
            mIngredientAmount = ingredientAmount;
            notifyPropertyChanged(BR.ingredientAmount);
        }
    }
    public String getFormattedIngredientAmount() {
        return toFraction(mIngredientAmount, 10);
    }

    @Bindable
    public String getIngredientUnit(){
        return mIngredientUnit;
    }
    public void setIngredientUnit(String ingredientUnit){
        if(!ingredientUnit.equals(mIngredientUnit)) {
            mIngredientUnit = ingredientUnit;
        }
    }

    @Exclude
    @Bindable
    public int getIngredientUnitPosition(){
        return mIngredientUnitPosition;
    }
    public void setIngredientUnitPosition(int ingredientUnitPosition){
        if(mIngredientUnitPosition != ingredientUnitPosition) {
            mIngredientUnitPosition = ingredientUnitPosition;
            //mIngredientUnit = mIngredientUnits[ingredientUnitPosition];
            notifyPropertyChanged(BR.ingredientUnitPosition);
        }
    }

    public String getPreparation1(){
        return mPreparation1;
    }
    public void setPreparation1(String preparation1){
        if(!mPreparation1.equals(preparation1)) {
            mPreparation1 = preparation1;
        }
    }

    public String getPreparation2(){
        return mPreparation2;
    }
    public void setPreparation2(String preparation2){
        mPreparation2 = preparation2;
    }

    public boolean save() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ingredientRef = database.getReference("ingredient");

        if(mIngredientKey != null){

        } else{
            DatabaseReference newIngredientRef = ingredientRef.push();
            mIngredientKey = newIngredientRef.getKey();
            newIngredientRef.setValue(this);
        }

        return true;
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
        if (bestDenominator > 1) {
            if (l != 0) sb.append(' ');
            sb.append(Math.round(d * bestDenominator)).append('/').append(bestDenominator);
        }
        return sb.toString();
    }
}
