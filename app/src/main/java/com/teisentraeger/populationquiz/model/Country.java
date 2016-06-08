package com.teisentraeger.populationquiz.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * POJO representing a Country
 * Created by teisentraeger on 5/28/2016.
 */
public class Country implements Parcelable {

    public String name;
    public String capital;
    public String region;
    public Long population;
    public Double area;

    public static final String MAPPING_NAME = "name";
    public static final String MAPPING_CAPITAL = "capital";
    public static final String MAPPING_REGION = "region";
    public static final String MAPPING_POPULATION = "population";
    public static final String MAPPING_AREA = "area";

    private DecimalFormat df  = new DecimalFormat("#");;

    public Country() {
        df.setRoundingMode(RoundingMode.CEILING);
    }

    public Country(String name, String capital, String region, Long population, Double area) {
        df.setRoundingMode(RoundingMode.CEILING);
        this.name = name;
        this.capital = capital;
        this.region = region;
        this.population = population;
        this.area = area;
    }

    public String getFilename() {
        return name.replaceAll("\\s+","_").toLowerCase();
    }

    /**
     * Returns Population density in pop per sqm
     * @return
     */
    public Double getDensity() {
        if(this.population == null || this.population == 0 || this.area == null || this.area == 0) {
            return 0.0;
        }
        return this.population / this.area;
    }

    public String getDensityRounded() {
        return df.format(getDensity());
    }

    public boolean anyValueIsNull () {
        return (name==null || capital==null || region==null || population==null || area==null);
    }

    @Override
    public String toString() {
        return "Country{" +
                "name='" + name + '\'' +
                ", capital='" + capital + '\'' +
                ", region='" + region + '\'' +
                ", population=" + population +
                ", area=" + area +
                ", density=" + getDensityRounded() +
                ", filename=" + getFilename() +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString (capital);
        dest.writeString (region);
        dest.writeLong(population);
        dest.writeDouble(area);
    }

    /*** Here how do I populate my List of Products ***/
    private Country(Parcel in) {
        name = in.readString();
        capital = in.readString();
        region = in.readString();
        population = in.readLong();
        area = in.readDouble();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Country createFromParcel(Parcel in) {
            return new Country(in);
        }

        public Country[] newArray(int size) {
            return new Country[size];
        }
    };
}
