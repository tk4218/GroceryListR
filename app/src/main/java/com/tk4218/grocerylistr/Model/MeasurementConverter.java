package com.tk4218.grocerylistr.Model;

import java.math.BigDecimal;
import java.util.HashMap;

class MeasurementConverter {
    private BigDecimal mMeasurementAmount;
    private String mMeasurementUnit;
    private HashMap<String, Integer> mUnitOrder;

    private static BigDecimal MILLILITER = new BigDecimal(236.588);
    private static BigDecimal TEASPOON = new BigDecimal(48.0);
    private static BigDecimal TABLESPOON = new BigDecimal(16.0);
    private static BigDecimal FLUID_OUNCE = new BigDecimal(8.0);
    private static BigDecimal CUP = new BigDecimal(1.0);
    private static BigDecimal PINT = new BigDecimal(0.5);
    private static BigDecimal QUART = new BigDecimal(0.25);
    private static BigDecimal LITER = new BigDecimal(0.236588);
    private static BigDecimal GALLON = new BigDecimal(0.0625);

    private static BigDecimal GRAM = new BigDecimal(28.3495);
    private static BigDecimal OUNCE = new BigDecimal(1.0);
    private static BigDecimal POUND = new BigDecimal(0.0625);

    MeasurementConverter(){
        mUnitOrder = new HashMap<>();
        mUnitOrder.put("milliliters", 0);
        mUnitOrder.put("tsp", 1);
        mUnitOrder.put("Tbsp",2);
        mUnitOrder.put("fluid oz", 3);
        mUnitOrder.put("cups", 4);
        mUnitOrder.put("pints", 5);
        mUnitOrder.put("quarts", 6);
        mUnitOrder.put("liters", 7);
        mUnitOrder.put("gallons", 8);

        mUnitOrder.put("grams", 9);
        mUnitOrder.put("oz", 10);
        mUnitOrder.put("lbs",11);
    }

    String getMeasurementUnit(){
        return mMeasurementUnit;
    }
    void setMesurementUnit(String unit){
        mMeasurementUnit = unit;
    }

    double getMeasurementAmount(){
        return mMeasurementAmount.doubleValue();
    }
    void setmMeasurementAmount(double amount){
        mMeasurementAmount = new BigDecimal(amount);
    }

    public boolean add(double amount, String unit){
        if(mMeasurementUnit.equals("")) {
            mMeasurementUnit = unit;
        }

        //Check if amounts can be added together.
        //If units are not convertable, only add if adding the two of the same units.
        //Only add convertable units if they are compatible. Ex. tsp to tbsp, NOT tsp to lbs.
        if(!mUnitOrder.containsKey(mMeasurementUnit)){
            if(!mMeasurementUnit.equals(unit)){
                return false;
            } else {
                mMeasurementAmount = mMeasurementAmount.add(new BigDecimal(amount));
                return true;
            }
        } else{
            //check for incompatible units
            if(!((mUnitOrder.get(mMeasurementUnit) < 9 && mUnitOrder.get(unit) < 9) ||
                    (mUnitOrder.get(mMeasurementUnit) >= 9 && mUnitOrder.get(unit) >= 9))){
                return false;
            }
        }

        if(mUnitOrder.get(unit) > mUnitOrder.get(mMeasurementUnit)){
            mMeasurementAmount = convertAmount(mMeasurementUnit, unit, mMeasurementAmount);
            mMeasurementUnit = unit;
        } else if(mUnitOrder.get(unit) < mUnitOrder.get(mMeasurementUnit)){
            amount = convertAmount(unit, mMeasurementUnit, new BigDecimal(amount)).doubleValue();
        }
        mMeasurementAmount = mMeasurementAmount.add(new BigDecimal(amount));
        return true;
    }

    private BigDecimal convertAmount(String unitFrom, String unitTo, BigDecimal amount){
        BigDecimal convertFactorFrom = convertFactor(unitFrom);
        BigDecimal convertFactorTo = convertFactor(unitTo);
        BigDecimal convertCups = amount.divide(convertFactorFrom, 4, BigDecimal.ROUND_HALF_UP);
        return convertCups.multiply(convertFactorTo);
    }

    private BigDecimal convertFactor(String unit){
        switch (unit){
            case "milliliters":
                return MILLILITER;
            case "tsp":
                return TEASPOON;
            case "Tbsp":
                return TABLESPOON;
            case "fluid oz":
                return  FLUID_OUNCE;
            case "cups":
                return CUP;
            case "pints":
                return PINT;
            case "quarts":
                return QUART;
            case "liters":
                return LITER;
            case "gallons":
                return GALLON;
            case "grams":
                return GRAM;
            case "oz":
                return OUNCE;
            case "lbs":
                return POUND;
        }
        return new BigDecimal(1);
    }
}
