package com.StanfordTimeCard.StanfordTimeCard;


import java.awt.EventQueue;
/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				new TimeCardSU();
			}
        	
        });
    }
}
