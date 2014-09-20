package com.nhefner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Account {

	private final int number;

	private int balance;
	
	public Account(int number, int openingBalance) {
		this.number = number;
		this.balance = openingBalance;
	}

	public void withdraw(int amount) throws Exception {

		if (amount > balance) {
			throw new Exception();
		}

		balance -= amount;
	}

	public void deposit(int amount) {

		balance += amount;
	}

	public int getNumber() {
		return number;
	}

	public int getBalance() {
		return balance;
	}
}

