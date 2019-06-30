package com.tk4218.grocerylistr.model;

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
        mUnitOrder.put("milliliter", 0);
        mUnitOrder.put("ml.", 1);
        mUnitOrder.put("ml", 2);
        mUnitOrder.put("milliliters", 3);

        mUnitOrder.put("t.", 4);
        mUnitOrder.put("t", 5);
        mUnitOrder.put("tsp.", 6);
        mUnitOrder.put("tsp", 7);

        mUnitOrder.put("tb.", 8);
        mUnitOrder.put("tb", 9);
        mUnitOrder.put("tbsp.", 10);
        mUnitOrder.put("tbsp", 11);

        mUnitOrder.put("fl. oz.", 12);
        mUnitOrder.put("fl oz.", 13);
        mUnitOrder.put("fluid ounces", 14);
        mUnitOrder.put("fluid oz.", 15);
        mUnitOrder.put("fluid oz", 16);

        mUnitOrder.put("cup", 17);
        mUnitOrder.put("c", 18);
        mUnitOrder.put("c.", 19);
        mUnitOrder.put("cups", 20);

        mUnitOrder.put("pint", 21);
        mUnitOrder.put("pt.", 22);
        mUnitOrder.put("pt", 23);
        mUnitOrder.put("pints", 24);

        mUnitOrder.put("quart", 25);
        mUnitOrder.put("qt.", 26);
        mUnitOrder.put("qt", 27);
        mUnitOrder.put("quarts", 28);

        mUnitOrder.put("liter", 29);
        mUnitOrder.put("l.", 30);
        mUnitOrder.put("l", 31);
        mUnitOrder.put("liters", 32);

        mUnitOrder.put("gallon", 33);
        mUnitOrder.put("gal.", 34);
        mUnitOrder.put("gal", 35);
        mUnitOrder.put("gallons", 36);


        mUnitOrder.put("gram", 37);
        mUnitOrder.put("g.", 38);
        mUnitOrder.put("g", 39);
        mUnitOrder.put("grams", 40);

        mUnitOrder.put("ounce", 41);
        mUnitOrder.put("oz.", 42);
        mUnitOrder.put("oz", 43);
        mUnitOrder.put("ounces", 44);

        mUnitOrder.put("pound", 45);
        mUnitOrder.put("lb.", 46);
        mUnitOrder.put("lb", 47);
        mUnitOrder.put("lbs.",48);
        mUnitOrder.put("lbs",49);
        mUnitOrder.put("pounds", 50);
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
        if(!mUnitOrder.containsKey(mMeasurementUnit.toLowerCase())){
            if(!mMeasurementUnit.equalsIgnoreCase(unit)){
                return false;
            } else {
                mMeasurementAmount = mMeasurementAmount.add(new BigDecimal(amount));
                return true;
            }
        } else{
            //check for incompatible units
            if(!((mUnitOrder.get(mMeasurementUnit.toLowerCase()) < 37 && mUnitOrder.get(unit.toLowerCase()) < 37) ||
                    (mUnitOrder.get(mMeasurementUnit.toLowerCase()) >= 37 && mUnitOrder.get(unit.toLowerCase()) >= 37))){
                return false;
            }
        }

        if(mUnitOrder.get(unit.toLowerCase()) > mUnitOrder.get(mMeasurementUnit.toLowerCase())){
            mMeasurementAmount = convertAmount(mMeasurementUnit, unit, mMeasurementAmount);
            mMeasurementUnit = unit;
        } else if(mUnitOrder.get(unit.toLowerCase()) < mUnitOrder.get(mMeasurementUnit.toLowerCase())){
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
        switch (unit.toLowerCase()){
            case "milliliters":
            case "ml.":
            case "ml":
            case "milliliter":
                return MILLILITER;
            case "tsp":
            case "t":
            case "t.":
            case "tsp.":
                return TEASPOON;
            case "tb":
            case "tb.":
            case "tbsp.":
            case "tbsp":
                return TABLESPOON;
            case "fluid oz":
            case "fl oz.":
            case "fl.oz":
            case "fluid oz.":
            case "fluid ounces":
                return  FLUID_OUNCE;
            case "cups":
            case "cup":
            case "c":
            case "c.":
                return CUP;
            case "pints":
            case "pint":
            case "pt":
            case "pt.":
                return PINT;
            case "quarts":
            case "quart":
            case "qt":
            case "qt.":
                return QUART;
            case "liters":
            case "l":
            case "l.":
            case "liter":
                return LITER;
            case "gallons":
            case "gallon":
            case "gal":
            case "gal.":
                return GALLON;
            case "grams":
            case "gram":
            case "g":
            case "g.":
                return GRAM;
            case "oz":
            case "oz.":
            case "ounce":
            case "ounces":
                return OUNCE;
            case "lbs":
            case "lbs.":
            case "lb":
            case "lb.":
            case "pound":
            case "pounds":
                return POUND;
        }
        return new BigDecimal(1);
    }
}
