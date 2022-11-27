package com.github.rahulpat.antifraudsystem.entities;

import javax.persistence.*;
import java.util.Arrays;

@Entity
@Table
public class StolenCreditCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;
    @Column
    private String number;

    public StolenCreditCard() {};

    public StolenCreditCard(long id, String number) {
        this.id = id;
        this.number = number;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    // Luhn's Algorithm - used to validate if Credit Card number is valid
    public static boolean isCardNumberValid(String cardNumber) {

        int[] cardIntArray=new int[cardNumber.length()];

        for(int i = 0 ; i < cardNumber.length() ; i++) {
            char c= cardNumber.charAt(i);
            cardIntArray[i]=  Integer.parseInt(""+c);
        }

        for(int i = cardIntArray.length - 2; i >=0 ; i= i - 2) {
            int num = cardIntArray[i];
            num = num * 2;
            if(num > 9) {
                num = num % 10 + num / 10;
            }

            cardIntArray[i] = num;
        }

        int sum = Arrays.stream(cardIntArray).sum();

        return sum % 10 == 0;
    }
}
